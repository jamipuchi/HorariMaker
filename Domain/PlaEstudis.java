package Domain;
import com.google.gson.annotations.Expose;

import java.lang.String;
import java.util.*;

public class PlaEstudis {
    @Expose
    private String name;
    @Expose
    private int curs;
    @Expose
    private Horari h;

    @Expose
    private String institucio;
    @Expose
    private HashMap<String, Assignatura> idAssig;
//    private ArrayList<String> idAssig;

    public PlaEstudis(String name, String institucio){
        this.name = name;
        this.institucio = institucio;
        idAssig = new HashMap<String, Assignatura>();
        this.name = name;
    }

    public int getCurs() {
        return curs;
    }

    public String getNomTitulacio() {
        return name;
    }

    public void setCurs(int curs) {
        this.curs = curs;
    }

    public void setNom(String name) {
        this.name = name;
    }

    public void addAssig(Assignatura a){
        idAssig.put(a.getName(), a);
    }

    public ArrayList<String> getAssigs(){
        return new ArrayList<>(idAssig.keySet());
    }

    public boolean checkRestriccionsAula(DiaSetmana dia, int hora, Horari h, int idAula) {
        try {
            return CjtInstitucio.getInstance().getInst(institucio).checkRestriccionsAula(dia, hora, h, idAula);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getInstitucio() {
        return institucio;
    }

    public void setInstitucio(String institucio) {
        this.institucio = institucio;
    }

    public void addCoRequisit(String assig, String name) {
        idAssig.get(assig).addCoRequisit(name);
        idAssig.get(name).addCoRequisit(assig);
    }

    public void deleteCorequisit(String assig, String name) {
        idAssig.get(assig).deleteCoRequisit(name);
        idAssig.get(name).deleteCoRequisit(assig);
    }

    public void removeAssig(String assig) {
        idAssig.remove(assig);
    }

    public boolean hasHorari() {
        return h != null;
    }

    public Horari getHorari() {
        return h;
    }

    class SortByRestriccions implements Comparator<String> {
        public int compare(String a, String b){ return  idAssig.get(a).getRestriccions().size() - idAssig.get(b).getRestriccions().size(); }
    }

    public Horari newHorari(int horaIni, int horaFi, ArrayList<DiaSetmana> diesLectius){
        ArrayList<String> assigs = new ArrayList<>(idAssig.keySet());
        Collections.sort(assigs, new SortByRestriccions());
        CjtInstitucio cjt = CjtInstitucio.getInstance();
        Institucio i = cjt.getInst(institucio);
        int numAules = i.getNumAules();
        try {
            this.h = new Horari(horaIni, horaFi, diesLectius, numAules, this.name, this.institucio);
            System.out.println("Created timetable");
            return h.genera(assigs, this);
        } catch (Exception e) {
            System.out.println("Error creant l'horari: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public Assignatura getAssig(String name){
        return idAssig.getOrDefault(name, null);
    }

    public void printHorari() {
        h.print();
    }

    public int getNumAules() {
        return CjtInstitucio.getInstance().getInst(institucio).getNumAules();
    }
}
