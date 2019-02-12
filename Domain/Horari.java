package Domain;

import Domain.restriccions.Dia;
import com.google.gson.annotations.Expose;

import java.security.SecureRandom;
import java.util.*;

class pos {
    @Expose
    public int dia;
    @Expose
    int hora;
    @Expose
    int aula;

    pos(int dia, int hora, int aula){
        this.dia = dia;
        this.hora = hora;
        this.aula = aula;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        pos pos = (pos) o;
        return dia == pos.dia && hora == pos.hora && aula == pos.aula;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dia, hora, aula);
    }
}

class assignacio {
    @Expose
    pos p = null;
    @Expose
    Grup g;

    assignacio(Grup g){
        this.g = g;
    }

    assignacio(pos p, Grup g){
        this(g);
        this.p = p;
    }
}

class resRestriccio {
    @Expose
    boolean resultat;
    @Expose
    String tipus;

    resRestriccio(String tipus, boolean resultat){
        this.resultat = resultat;
        this.tipus = tipus;
    }
}

public class Horari {

    @Expose
    private int horaIni;
    @Expose
    private int horaFi;
    @Expose
    private ArrayList<DiaSetmana> diesLectius;
    @Expose
    private int nClasses;
    @Expose
    private int nHoresDia;
    @Expose
    private int primerDia;
    @Expose
    private int ultimDia;
    @Expose
    private Grup[][][] horari;
    @Expose
    private boolean[][][] ocupats;
    @Expose
    private Map<Integer, Integer> usosAula;
    @Expose
    private int numAules;
    @Expose
    private int iter = 0;
    @Expose
    private int every;
    @Expose
    private ArrayList<Grup> grupsPendents;
    @Expose
    private Set<String> assigPendents;
    @Expose
    private Map<String, Boolean> assigPendentsRest;
    @Expose
    private Map<String, ArrayList<assignacio>> assignacions; // per cada assignatura, en guardem les assignacions grup <->(hora, dia, aula)
    @Expose(serialize = false, deserialize = false)
    private Map<pos, Set<String>> assignacionsHores; // per cada posició dins l'horari, en guardem les assignatures que s'hi fan
    @Expose
    private int maxProfunditat;
    @Expose
    private Map<Integer, Boolean> diesMirats;
    @Expose(serialize = false, deserialize = false)
    private transient PlaEstudis pla = null;
    @Expose
    private int nAssig = 0;
    @Expose
    private String nomPla;
    @Expose
    private String nomInst;
//    private ArrayList<Integer> hores;


    public Horari(int horaIni, int horaFi, ArrayList<DiaSetmana> diesLectius, int numAules, String nomPla, String nomInst){
            this.horaIni = horaIni;
            this.horaFi = horaFi;
            this.diesLectius = diesLectius;
//            Collections.shuffle(diesLectius, new SecureRandom());
            this.nHoresDia = horaFi-horaIni;
            this.nClasses = diesLectius.size()*nHoresDia; // nombre d'hores on es pot fer classe
            this.primerDia = diesLectius.get(0).num();
            this.ultimDia = diesLectius.get(diesLectius.size() - 1).num();
            this.horari = new Grup[diesLectius.size()][nHoresDia][numAules];
            this.ocupats = new boolean[diesLectius.size()][nHoresDia][numAules];
//            this.hores = new ArrayList<>();
            this.numAules = numAules;
            this.usosAula = new TreeMap<>();
            this.grupsPendents= new ArrayList<>();
            this.assigPendents= new HashSet<>();
            this.assigPendentsRest = new HashMap<>();
            this.every = 2; // les restriccions d'assignatura es comproven cada 'every' assignatures iterades
            this.assignacions = new HashMap<>();
            this.assignacionsHores = new HashMap<>();
            this.maxProfunditat = 100;
            this.diesMirats = new HashMap<>();
            this.nomInst = nomInst;
            this.nomPla = nomPla;
            this.pla = CjtInstitucio.getInstance().getInst(nomInst).getStudyPlan(nomPla);
            for (int i = 0; i < numAules; ++i) usosAula.put(i, 0);
    }

    private void resetDiesMirats(){
        try {
            for (DiaSetmana diesLectiu : diesLectius) diesMirats.put(diesLectiu.num(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearOcu() {
        for (boolean i[][]: ocupats) {
            for (boolean j[] : i) {
                for (boolean k : j) k = false;
            }
        }
    }

    private int minAula(){ // obtenim l'aula amb menys usos
        Map.Entry<Integer, Integer> min = null;
        for (Map.Entry<Integer, Integer> entry : usosAula.entrySet()) {
            if (min == null || min.getValue() > entry.getValue()) {
                min = entry;
            }
        }
        if (min != null) return min.getKey();
        else return 0;
    }

    private int nextDia(int dia){
        if (dia == diesLectius.size() - 1) return diesLectius.get(0).num();
        else return dia + 1;
    }

    private int nextAula(int aula){
        if (aula == numAules - 1) return 0;
        else return aula + 1;
    }

    private boolean checkAssigRequisit (Grup g, int dia, int hora){
        Set<String> assigs = pla.getAssig(g.getIdAssignatura()).getCoRequisits();

        for (String assig : assigs) {  // per cada assignatura corequisit
            for (int i = 0; i < numAules; ++i) { // mirem si a alguna aula s'hi fa també classe en aquella hora
                pos pos = new pos(dia, hora, i);
                if (assignacions.containsKey(assig)) {
                    ArrayList<assignacio> as = assignacions.get(assig);
                    for (assignacio a : as) {
                        if (a.p.dia == dia && a.p.hora == hora) return false;
                    }
                }
            }
        }
        return true;
    }

    private resRestriccio av(Grup g, int dia, int hora, int idAula) { // comprova si en cert dia i aula, començant per l'hora, es pot posar el grup
        for (int i = 0; i < g.getHours(); ++i) { // comprovem per cada hora on caldria posar el grup si es pot
            if (hora + i >= (horaFi - horaIni)) return new resRestriccio("hora", false);
            else if (!ocupats[dia][hora + i][idAula]) { // si l'aula no està ocupada en aquell moment comprovem si s'hi compleixen les restriccions
                if (!g.checkRestriccions(DiaSetmana.nom(dia), horaIni + (hora + i), this)) return new resRestriccio("grup", false);
                if (!pla.getAssig(g.getIdAssignatura()).checkRestriccions(diesLectius.get(dia), horaIni + hora + i, this)) return new resRestriccio("assignatura", false);
                if (!checkAssigRequisit(g, dia, horaIni + hora + i)) return new resRestriccio("requisits", false);
                if (!pla.checkRestriccionsAula(DiaSetmana.nom(dia), horaIni + hora + i, this, idAula)) return new resRestriccio("aula", false);

                // if (!restrAssig(idAssig, dia, hora + i)) return new resRestriccio("assignatura", false); // funció que comprova restriccions retardadament
            } else return new resRestriccio("hora", false);
        }
        return new resRestriccio("cap", true);
    }

    private boolean recursiu (Grup g, int dia, int hora, int aula, int iter) {
        diesMirats.put(dia, true);
//        System.out.println("Provem " + g.getIdAssignatura() + g.getNum() + " en un " + DiaSetmana.nom(dia));
        if (iter < nClasses) {
            resRestriccio res = av(g, dia, hora, aula);
            if (res.resultat) {
                String idAssig = g.getIdAssignatura();
                for (int i = 0; i < g.getHours(); ++i) { // assignem el grup a les seves hores
                    pos pos = new pos(dia, hora + i, aula);
                    assignacions.get(idAssig).add(new assignacio(pos, g));
                    if (assignacionsHores.containsKey(pos)) assignacionsHores.get(pos).add(idAssig);
                    else assignacionsHores.put(pos, new HashSet<>(Collections.singletonList(idAssig)));
                    ocupats[dia][hora + i][aula] = true;
                    assigPendentsRest.put(idAssig, true);
                }
                usosAula.put(aula, usosAula.get(aula) + 1);
                return true;
            } else {
                switch (res.tipus) {
                    case "grup":
                    case "assignatura":
                    case "requisits":
                    case "hora":  // si la restricció incomplerta era d'assignatura o grup, canviem hora o dia
                        if (hora + g.getHours() < nHoresDia) return recursiu(g, dia, hora + g.getHours(), aula, iter + 1); // si en el mateix dia encara no hem visitat totes les hores
                        else {
                            if (diesMirats.get(nextDia(dia))) return false;
                            return recursiu(g, nextDia(dia), 0, aula, iter + 1); // si no, mirem al següent dia
                        }
                    case "aula":
                        return recursiu(g, dia, 0, nextAula(aula), iter + 1); // si ja hem vist totes les hore del dia en certa aula, mirem a  l'aula del costat sempre i quant no
                    default:
                        return false;
                }
            }
        }
        return false;
    }

    private boolean recursiuMove(Grup g, int dia, int hora, int aula, int iter) {
        diesMirats.put(dia, true);
//        System.out.println("Provem " + g.getIdAssignatura() + g.getNum() + " en un " + DiaSetmana.nom(dia));
        if (iter < g.getHours()) {
//            if (hora > (horaFi - horaIni)) return false;
            resRestriccio res = av(g, dia, hora, aula);
            // mirem si en aquesta posicio ja hi ha un
            if (res.resultat) {
                String idAssig = g.getIdAssignatura();
                for (int i = 0; i < g.getHours(); ++i) { // assignem el grup a les seves hores
                    pos pos = new pos(dia, hora + i, aula);
                    assignacions.get(idAssig).add(new assignacio(pos, g));
                    if (assignacionsHores.containsKey(pos)) assignacionsHores.get(pos).add(idAssig);
                    else assignacionsHores.put(pos, new HashSet<>(Collections.singletonList(idAssig)));
                    ocupats[dia][hora + i][aula] = true;
                    assigPendentsRest.put(idAssig, true);
                    horari[dia][hora + i][aula] = g;
                }
                usosAula.put(aula, usosAula.get(aula) + 1);
                return true;
            } else {
                return false;
//                switch (res.tipus) {
//                    case "grup":
//                    case "assignatura":
//                    case "requisits":
//                    case "hora":  // si la restricció incomplerta era d'assignatura o grup, canviem hora o dia
//                        if (hora + g.getHours() < nHoresDia) return recursiu(g, dia, hora + g.getHours(), aula, iter + 1); // si en el mateix dia encara no hem visitat totes les hores
//                        else {
//                            if (diesMirats.get(nextDia(dia))) return false;
//                            return recursiu(g, nextDia(dia), 0, aula, iter + 1); // si no, mirem al següent dia
//                        }
//                    case "aula":
//                        return recursiu(g, dia, 0, nextAula(aula), iter + 1); // si ja hem vist totes les hore del dia en certa aula, mirem a  l'aula del costat sempre i quant no
//                    default:
//                        return false;
//                }
            }
        }
        return false;
    }

    private boolean addGroups(ArrayList<Grup> grups){
        if (grups.size() == 0) return false;
        boolean val = true;
        for (int i = 0; i < grups.size() && val; ++i){ //Grup g : a.getGrups()) { // cada grup de l'assignatura s'intenta afegir a l'horari
            int dia = new SecureRandom().nextInt(diesLectius.size());
            resetDiesMirats();

            if (!recursiu(grups.get(i), dia, 0, minAula(), 0)) {
                grupsPendents.add(grups.get(i));
                val = false;
            }
        }
        return val;
    }

    private boolean addAssig(Assignatura a){
//        grupsPendents.clear();
//        assigPendentsRest.put(a.getIdAssignatura(), false); // afegim l'assig com a pendent a comprovar restricions
        try {
            return addGroups(a.getGrups());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    Horari genera(ArrayList<String> idAssig, PlaEstudis pla) {
        if (pla == null) pla = CjtInstitucio.getInstance().getInst(nomInst).getStudyPlan(nomPla);
        nAssig = idAssig.size();
        assigPendentsRest.clear();
        clearOcu();
        iter = 0;
        every = 2;
        for (String id : idAssig) {         // per cada assignatura del pla
            assignacions.put(id, new ArrayList<>());
            ++iter;
            if (addAssig(pla.getAssig(id))) { // cridem a afegir
                System.out.println("\u001B[32m" + id + " fully added to timetable (all its groups could be added)" + "\u001B[0m");
            }
            else {
                System.out.println("\u001B[31m" + id + " not fully added to timetable (some groups may have not been added)" + "\u001B[0m");
//                if (!assigPendents.contains(id)) assigPendents.add(id);
            }
        }

        addGroups(grupsPendents);

        for (Map.Entry<String, ArrayList<assignacio>> entry : assignacions.entrySet()) {
            for (assignacio assig : entry.getValue()) {
                horari[assig.p.dia][assig.p.hora][assig.p.aula] = assig.g;
            }
        }

        return this;
    }

    private static String abbreviateString(String input, int maxLength) {
        if (input.length() <= maxLength)
            return input;
        else
            return input.substring(0, maxLength-2) + "..";
    }

    void print() {
        try {
            int len = 0;
            for (String id : assignacions.keySet()) {
                if (id.length() > len) len = id.length();
            }
            len += 2;
            for (int i = 0; i < numAules; i++) {
                if (usosAula.get(i) > 0){
                    System.out.println("\nAula " + i);
                    for (int j = -1; j < diesLectius.size(); ++j) {
                        if (j < 0) System.out.format("%-6s", "Hora");
                        else System.out.format("%-11s", diesLectius.get(j).name());
                        System.out.print("|");
                    }
                    System.out.print("\n");
                    for (int j = 0; j < horari[0].length; j++) {
                        System.out.print("------");
                        for (int k = 0; k <horari.length; k++) {
                            System.out.print("------------");
                        }
                        System.out.print("\n");
                        if (horaIni + j < 10) {
                            System.out.format("%-2s", "0" + (horaIni + j));
                        } else System.out.format("%-2s", horaIni + j);
                        System.out.print(":00 |");
                        for (int k = 0; k < horari.length; k++) {
                            if (horari[k][j][i] != null) {
                                System.out.format("%-11s", abbreviateString(horari[k][j][i].getIdAssignatura(),8) + horari[k][j][i].getNum());
                                System.out.print("|");
                            } else {
                                System.out.format("%12s", "|");
                            }
                        }
                        System.out.println();
                    }
                } else{
                    System.out.println("Classroom "+ i +" not used");
                }
            }
        } catch (Exception e) {
            System.out.println("Error imprimint: ");
            e.printStackTrace();
        }
    }

    public int getHoraIni() {
        return horaIni;
    }

    Grup getSpot(pos p) {
//        if (pla == null) pla = CjtInstitucio.getInstance().getInst(nomInst).getStudyPlan(nomPla);
        if (p.hora < 0 || p.dia < 0 || p.aula < 0) return null;
        if (p.dia > horari.length || p.hora > horari[p.dia].length || p.aula > horari[p.dia][p.hora].length) return null;
        return horari[p.dia][p.hora][p.aula];
    }

    private void removeGroup(pos p) {
        if (horari != null && horari[p.dia][p.hora][p.aula] != null) {
            int iter = 0;
            boolean removed = false;
            int grup = horari[p.dia][p.hora][p.aula].getIdGrup();
            String assig = horari[p.dia][p.hora][p.aula].getIdAssignatura();
            int hours = horari[p.dia][p.hora][p.aula].getHours();
            while (!removed && p.hora < (horaFi - horaIni) && iter < hours) {
                ocupats[p.dia][p.hora][p.aula] = false;
                ArrayList<assignacio> assigList = assignacions.get(assig);
                for (int i = 0; i < assigList.size(); ++i) {
                    if (assigList.get(i).p == p && assigList.get(i).g.getIdGrup() == grup && assigList.get(i).g.getIdAssignatura().equals(assig)) assignacions.get(assig).remove(i);
                }

//                Set<String> hores = assignacionsHores.get(p);
//                for (String a : hores) {
//                    if (a.equals(assig)) assignacionsHores.get(p).remove(a);
//                }

                horari[p.dia][p.hora][p.aula] = null;

                p = new pos(p.dia, p.hora + 1, p.aula);
                if (p.hora < (horaFi - horaIni)
                        && horari[p.dia][p.hora][p.aula] != null
                        && (horari[p.dia][p.hora][p.aula].getIdGrup() != grup || !horari[p.dia][p.hora][p.aula].getIdAssignatura().equals(assig))) {
                    removed = true;
                }
                iter++;
            }
        }
    }

    boolean movePos(pos ini, pos fi, Grup g) { // s'ha d'actualitzar this.ocupats, this.horari, this.assignacionsHores i this.assignacions
        if (pla == null) pla = CjtInstitucio.getInstance().getInst(nomInst).getStudyPlan(nomPla);
        boolean ended = false;
        pos p = ini;
        p = new pos(p.dia, p.hora - 1, p.aula);
        if (getSpot(p) != null && getSpot(p).getIdAssignatura().equals(g.getIdAssignatura()) && getSpot(p).getIdGrup() == g.getIdGrup()) {
            return false;
        }
        // si l'aula és diferent, cal restar a usosAula
        boolean ret = recursiuMove(g, fi.dia, fi.hora, fi.aula, 0); // recursiu afegeix a assignacionsHores, ocupats, usosAula, assigPendentsRest
        // cridant recursiu abans de retornar ens assegurem que no canviem cap a la mateixa hora
        if (ret) {
            removeGroup(ini);
//            for (Map.Entry<String, ArrayList<assignacio>> entry : assignacions.entrySet()) {
//                for (assignacio assig : entry.getValue()) {
//                    horari[assig.p.dia][assig.p.hora][assig.p.aula] = assig.g;
//                }
//            }
            return true;
        } else return false;
    }

    public int getHoraFi() {
        return horaFi;
    }

    public String getDies() {
        String ret = "";
        for (DiaSetmana d : diesLectius) {
            ret += String.valueOf(d.num());
        }
        return ret;
    }

    public void refresh() {
        if (assignacionsHores == null && horari != null) {
            assignacionsHores = new HashMap<>();
            for (int i = 0; i < horari.length; ++i) {
                for (int j = 0; j < horari[i].length; ++j) {
                    for (int k = 0; k < horari[i][j].length; ++k) {
                        pos p = new pos(i, j, k);
                        if (!assignacionsHores.containsKey(p)) assignacionsHores.put(p, new HashSet<>());

                        if (horari[i][j][k] != null) {
                            assignacionsHores.get(p).add(horari[i][j][k].getIdAssignatura());
                        }
                    }
                }
            }
        }
    }
}
