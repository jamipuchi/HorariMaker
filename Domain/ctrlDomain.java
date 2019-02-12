package Domain;
import Domain.gson.typeadapters.RuntimeTypeAdapterFactory;
import Domain.restriccions.*;
import com.google.gson.*;
import Persistance.ctrlPersistance;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class ctrlDomain {

    //funcions que necessito:
    //Retornar dia setmana
    //Triar assig // grup // Aula i que es guardi
    //Poder fer de tot a aixo (canviar nom, ficar restriccions, treure nom, eliminar...)
    //private ctrlData CD;
    private ctrlPersistance CP;
    private CjtUsuari cjtU;
    private CjtInstitucio cjtI;

    private Usuari selectedU = null;
    private Institucio selectedI = null;
    private Aula selectedA = null;
    private PlaEstudis selectedP = null;
    private Assignatura selectedS = null;
    private Grup selectedG = null;
    private Horari selectedH = null;

    private ArrayList<Restriccio> restriccions;

    private ctrlDomain() {
        //CD = new ctrlData();
        CP = ctrlPersistance.getInstance();
        cjtU = CjtUsuari.getInstance();
        cjtI = CjtInstitucio.getInstance();
        restriccions = new ArrayList<>();
    }

    private static class InstanceHolder {
        private static final ctrlDomain instance = new ctrlDomain();
    }

    public static ctrlDomain getInstance() {
        return InstanceHolder.instance;
    }

    ///////////////////
    // BBDD managing //
    ///////////////////

    public void saveBBDD() throws IOException {
        saveUsersBBDD();
        saveInstitutionsBBDD();
    }

    public void getBBDD(String address){
        CP.setBBDD(address);
        getUsersBBDD();
        getInstitutionsBBDD();
    }

    private void saveUsersBBDD(){
        Gson gson = new Gson();
        String usersJson = gson.toJson(cjtU);
        CP.saveUsers(usersJson);
    }

    private void saveInstitutionsBBDD(){
        RuntimeTypeAdapterFactory<Restriccio> adapter =
                RuntimeTypeAdapterFactory.of(Restriccio.class)
                .registerSubtype(Dia.class, "Dia")
                .registerSubtype(DiaIHora.class, "DiaIHora")
                .registerSubtype(Hores.class, "Hores")
                .registerSubtype(Matins.class, "Matins")
                .registerSubtype(Tardes.class, "Tardes");

        GsonBuilder gsonB = new GsonBuilder().registerTypeAdapterFactory(adapter);
        gsonB.setPrettyPrinting();

        gsonB.excludeFieldsWithoutExposeAnnotation();
        Gson gson = gsonB.create();
        String institutionJson = gson.toJson(cjtI);
        CP.saveInstitutions(institutionJson);
    }

    private void saveRestrictionsBBDD() throws IOException {
        Gson gson = new Gson();
        String restrictJson = gson.toJson(restriccions);
        CP.saveRestrictions(restrictJson);
    }

    private void getUsersBBDD() {
        String usersJson = "";
        try {
            usersJson = CP.getUsers();
        } catch (IOException e) {
            System.out.println("Error loading users from BBDD");
            e.printStackTrace();
        }
        Gson gson = new Gson();
        cjtU = gson.fromJson(usersJson, CjtUsuari.class);
        if (cjtU == null) cjtU = CjtUsuari.getInstance();
        else cjtU.setInstance(cjtU);
    }

    private void getInstitutionsBBDD(){
        String institutionsJson = "";
        try {
            institutionsJson = CP.getInstitutions();
        } catch (IOException e) {
            System.out.println("Error loading institutions from BBDD");
            e.printStackTrace();
        }

        RuntimeTypeAdapterFactory<Restriccio> adapter =
                RuntimeTypeAdapterFactory.of(Restriccio.class)
                        .registerSubtype(Dia.class, "Dia")
                        .registerSubtype(DiaIHora.class, "DiaIHora")
                        .registerSubtype(Hores.class, "Hores")
                        .registerSubtype(Matins.class, "Matins")
                        .registerSubtype(Tardes.class, "Tardes");

        GsonBuilder gsonB = new GsonBuilder().registerTypeAdapterFactory(adapter);
        gsonB.setPrettyPrinting();

        gsonB.excludeFieldsWithoutExposeAnnotation();
        Gson gson = gsonB.create();

        cjtI = gson.fromJson(institutionsJson, CjtInstitucio.class);
        if (cjtI == null) cjtI = CjtInstitucio.getInstance();
        else cjtI.setInstance(cjtI);
    }

    private void getRestrictionsBBDD(){
        String restrictionsJson = "";
        try{
            restrictionsJson = CP.getRestrictions();
        } catch (IOException e){
            System.out.println("Error loading restrictions from BBDD");
            e.printStackTrace();
        }
        Gson gson = new Gson();
        restriccions = gson.fromJson(restrictionsJson,restriccions.getClass());
    }

    ///////////////////
    // User managing //
    ///////////////////

    public boolean registerUser(String name, String pass) {
        if (cjtU.exists(name)) return false;
        selectedU = new Usuari(name, pass);
        cjtU.add(selectedU);
        return true;
    }

    public boolean logIn(String name, String pass) {
        boolean ret = cjtU.exists(name) && cjtU.select(name).getPassword().equals(pass);
        if (ret) {
            selectedU = cjtU.select(name);
            return true;
        } else return false;
    }

    public boolean logOut() {
        if (selectedU == null) return false;
        else {
            selectedU = null;
            return true;
        }
    }


    /////////////////////////
    // Institució managing //
    /////////////////////////

    public boolean createInstitucio(String name) {
        if (cjtI.getInst(name) != null || selectedU == null) return false;
        else {
            selectedI = new Institucio(name);
            cjtI.addObject(selectedI);
            selectedU.addInst(name);
            return true;
        }
    }

    public Set<String> getInstitucions() {
        if (selectedU == null) return new HashSet<>();
        else {
            return selectedU.getInst();
        }
    }

    public boolean chooseInstitucio(String name) {
        Institucio temp = cjtI.getInst(name);
        if (temp == null) return false;
        else {
            selectedI = temp;
            return true;
        }
    }

    public boolean renameInstitucio(String name) {
        if (selectedI == null) return false;
        else {
            selectedI.setName(name);
            return true;
        }
    }

    public boolean deleteInstitucio() {
        if (selectedU == null) return false;
        else {
            selectedU.removeInstitucio(selectedI.getName());
            cjtI.removeInstitucio(selectedI.getName());
            selectedI = null;
            return true;
        }
    }

    ///////////////////
    // Aula managing //
    ///////////////////

    public boolean createAula(String name, String type) {
        if (selectedI != null) {
            selectedA = new Aula(name, type, selectedI.getName());
            selectedI.addAula(selectedA);
            return true;
        } else return false;

    }

    public ArrayList<String> getAules() {
        if (selectedI != null) {
            ArrayList<Aula> aules =  selectedI.getAules();
            ArrayList<String> ret = new ArrayList<>();
            for (Aula a : aules) ret.add(a.getNom());
            return ret;
        } else return new ArrayList<>();
    }

    public boolean chooseAula(int idAula) {
        if (selectedI == null) return false;
        else {
            ArrayList<Aula> aules = selectedI.getAules();
            if (idAula >= aules.size() || idAula < 0) return false;
            else {
                selectedA = aules.get(idAula);
                return true;
            }
        }
    }

    public boolean removeAula() {
        if (selectedA == null || selectedI == null) return false;
        else {
            selectedI.removeAula(selectedA.getIdAula());
            selectedA = null;
            return true;
        }
    }

    public String getTipus(){
        if (selectedA == null) return "";
        else return selectedA.getTipus();
    }

    public boolean addRestriccioAulaDia(int dia) {
        if (selectedA == null) return false;
        else {
            Restriccio rest = new Dia(DiaSetmana.nom(dia));
            selectedA.addRestriccio(rest);
            return true;
        }
    }

    public boolean addRestriccioAulaHora(int horaIni, int horaFi) {
        if (selectedA == null) return false;
        else {
            Restriccio rest = new Hores(horaIni, horaFi);
            selectedA.addRestriccio(rest);
            return true;
        }
    }

    public boolean addRestriccioAulaDiaHora(int dia, int horaIni, int horaFi) {
        if (selectedA == null) return false;
        else {
            Restriccio rest = new DiaIHora(DiaSetmana.nom(dia), horaIni, horaFi);
            selectedA.addRestriccio(rest);
            return true;
        }
    }

    public boolean addRestriccioAulaMatins() {
        if (selectedA == null) return false;
        else {
            Restriccio rest = new Matins();
            selectedA.addRestriccio(rest);
            return true;
        }
    }

    public boolean addRestriccioAulaTardes() {
        if (selectedA == null) return false;
        else {
            Restriccio rest = new Tardes();
            selectedA.addRestriccio(rest);
            return true;
        }
    }

    public ArrayList<String> getRestriccionsAula() { // retorna una llista de strings representatives de cada restricció
        if (selectedA == null) return new ArrayList<>(); // mirar cada restricció per veure quina mena de format té l'string
        else {
            ArrayList<String> rest = new ArrayList<>();
            for (Restriccio r : selectedA.getRestriccions()) {
                rest.add(r.toString());
            }
            return rest;
        }
    }

    public boolean deleteRestriccioAula(int i) {
        if (selectedA == null) return false;
        else {
            return selectedA.deleteRestriccio(i);
        }
    }

    /////////////////////////
    // Study plan managing //
    /////////////////////////

    public boolean createPla(String name) {
        if (selectedI != null) {
            selectedI.addStudyPlan(name);
            selectedP = selectedI.getStudyPlan(name);
            return true;
        }
        else return false;
    }

    public ArrayList<String> getPlans() {
        if (selectedI != null){
            return new ArrayList(selectedI.getStudyPlans());
        }
        else return new ArrayList<>();
    }

    public boolean choosePla(String name) {
        if (selectedI == null) return false;
        else {
            PlaEstudis temp = selectedI.getStudyPlan(name);
            if (temp == null) return false;
            else {
                selectedP = temp;
                return true;
            }
        }
    }

    public boolean renamePla(String name) {
        if (selectedP == null) return false;
        else {
            selectedP.setNom(name);
            return true;
        }
    }

    public boolean removePla() {
        if (selectedP == null) return false;
        else {
            selectedP = null;
            return true;
        }
    }

    //////////////////////
    // Subject managing //
    //////////////////////

    public boolean createAssig(String name, int hours) {
        if (selectedP != null) {
            selectedS = new Assignatura(name, hours, selectedP.getNomTitulacio(), selectedP.getInstitucio());
            selectedP.addAssig(selectedS);
            return true;
        } else return false;
    }

    public int getHoresAssig(){
        return selectedS.getHours();
    }

    public ArrayList<String> getAssigs() {
        if (selectedP == null) return new ArrayList<>();
        else {
            return selectedP.getAssigs();
        }
    }

    public Assignatura getAssignatura(String name){
        if (selectedP == null) return null;
        else return selectedP.getAssig(name);
    }

    public boolean chooseAssig(String name) {
        if (selectedP == null) return false;
        else {
            Assignatura temp = selectedP.getAssig(name);
            if (temp == null) return false;
            else {
                selectedS = temp;
                return true;
            }
        }
    }

    public boolean editAssig(String name, int hours) {
        if (selectedS == null) return false;
        else {
            selectedS.setName(name);
            if (hours != selectedS.getHours()) {
                selectedS.setHours(hours);
                selectedS.removeGroups();
                selectedS.addGroups(selectedS.getNum(), selectedS.getNum2()); // regenerem els grups
            }
            return true;
        }
    }

    public boolean removeAssig() {
        if (selectedS == null || selectedP == null) return false;
        else {
            selectedP.removeAssig(selectedS.getName());
            selectedS = null;
            return true;
        }
    }

    public boolean addRestriccioAssigDia(int dia) {
        if (selectedS == null) return false;
        else {
            Restriccio rest = new Dia(DiaSetmana.nom(dia));
            selectedS.addRestriccio(rest);
            return true;
        }
    }

    public boolean addRestriccioAssigHora(int horaIni, int horaFi) {
        if (selectedS == null) return false;
        else {
            Restriccio rest = new Hores(horaIni, horaFi);
            selectedS.addRestriccio(rest);
            return true;
        }
    }

    public boolean addRestriccioAssigDiaHora(int dia, int horaIni, int horaFi) {
        if (selectedS == null) return false;
        else {
            Restriccio rest = new DiaIHora(DiaSetmana.nom(dia), horaIni, horaFi);
            selectedS.addRestriccio(rest);
            return true;
        }
    }

    public boolean addRestriccioAssigMatins() {
        if (selectedS == null) return false;
        else {
            Restriccio rest = new Matins();
            selectedS.addRestriccio(rest);
            return true;
        }
    }

    public boolean addRestriccioAssigTardes() {
        if (selectedS == null) return false;
        else {
            Restriccio rest = new Tardes();
            selectedS.addRestriccio(rest);
            return true;
        }
    }

    public boolean addCorequisit(String assig) {
        if (selectedS == null || selectedP == null) return false;
        else {
            selectedP.addCoRequisit(assig, selectedS.getName());
            return true;
        }
    }

    public ArrayList<String> getCorequisits() {
        if (selectedS == null) return new ArrayList<>();
        else {
            return new ArrayList<>(selectedS.getCoRequisits());
        }
    }

    public boolean removeCorequisit (String assig) {
        if (selectedS == null || selectedP == null) return false;
        else {
            selectedP.deleteCorequisit(assig, selectedS.getName());
            return true;
        }
    }

    public ArrayList<String> getRestriccionsAssig() { // retorna una llista de strings representatives de cada restricció
        if (selectedS == null) return new ArrayList<>(); // mirar cada restricció per veure quina mena de format té l'string
        else {
            ArrayList<String> rest = new ArrayList<>();
            for (Restriccio r : selectedS.getRestriccions()) {
                rest.add(r.toString());
            }
            return rest;
        }
    }

    public boolean deleteRestriccioAssig(int i) {
        if (selectedS == null) return false;
        else {
            return selectedS.deleteRestriccio(i);
        }
    }

    public int getSuperGrups() {
        if (selectedS == null) return 0;
        else {
            return selectedS.getNum();
        }
    }

    public int getSubGrups() {
        if (selectedS == null) return 0;
        else {
            return selectedS.getNum2();
        }
    }

    ////////////////////
    // Grups managing //
    ////////////////////

    public boolean addGroups(int num, int num2) {
        if (selectedS == null) return false;
        else {
            selectedS.addGroups(num, num2);
            return true;
        }
    }

    public ArrayList<String> getGroups() {
        ArrayList<String> ret = new ArrayList<>();
        if (selectedS == null) return ret;
        else {
            for (Map.Entry<Integer, String> grup : selectedS.getIdGrups().entrySet()) {
                ret.add(grup.getValue());
            }
            return ret;
        }
    }

    public boolean chooseGrup(int idGrup) {
        if (selectedS == null) return false;
        else {
            selectedG = selectedS.getGrup(idGrup);
            if (selectedG == null) return false;
            else return true;
        }
    }

    public boolean removeGrup() {
        if (selectedS == null || selectedG == null) return false;
        else {
            return selectedS.removeGrup(selectedG);

        }
    }

    public boolean addRestriccioGrupDia(int dia) {
        if (selectedG == null) return false;
        else {
            Restriccio rest = new Dia(DiaSetmana.nom(dia));
            selectedG.addRestriccio(rest);
            return true;
        }
    }

    public boolean addRestriccioGrupHora(int horaIni, int horaFi) {
        if (selectedG == null) return false;
        else {
            Restriccio rest = new Hores(horaIni, horaFi);
            selectedG.addRestriccio(rest);
            return true;
        }
    }

    public boolean addRestriccioGrupDiaHora(int dia, int horaIni, int horaFi) {
        if (selectedG == null) return false;
        else {
            Restriccio rest = new DiaIHora(DiaSetmana.nom(dia), horaIni, horaFi);
            selectedG.addRestriccio(rest);
            return true;
        }
    }

    public boolean addRestriccioGrupMatins() {
        if (selectedG == null) return false;
        else {
            Restriccio rest = new Matins();
            selectedG.addRestriccio(rest);
            return true;
        }
    }

    public boolean addRestriccioGrupTardes() {
        if (selectedG == null) return false;
        else {
            Restriccio rest = new Tardes();
            selectedG.addRestriccio(rest);
            return true;
        }
    }

    public ArrayList<String> getRestriccionsGrup() { // retorna una llista de strings representatives de cada restricció
        if (selectedG == null) return new ArrayList<>(); // mirar cada restricció per veure quina mena de format té l'string
        else {
            ArrayList<String> rest = new ArrayList<>();
            for (Restriccio r : selectedG.getRestriccions()) {
                rest.add(r.toString());
            }
            return rest;
        }
    }

    public boolean deleteRestriccioGrup(int i) {
        if (selectedG == null) return false;
        else {
            return selectedG.deleteRestriccio(i);
        }
    }

    /////////////////////
    // Horari managing //
    /////////////////////

    static class SortByDia implements Comparator<DiaSetmana> {
        public int compare(DiaSetmana a, DiaSetmana b){
            if (a.num() < b.num()) return -1;
            else if (a.num() == b.num()) return 0;
            else return 0;
        }
    }

    public boolean makeHorari(int horaIni, int horaFi, String sDies) {
        if (selectedP == null) return false;
        else {
            ArrayList<DiaSetmana> dies = new ArrayList<>(EnumSet.allOf(DiaSetmana.class));
            ArrayList<DiaSetmana> diesDef = new ArrayList<>();
            for (int i = 0; i < sDies.length(); i++){
                int x = Character.getNumericValue(sDies.charAt(i));
                diesDef.add(dies.get(x));
            }
            diesDef.sort(new SortByDia());
            selectedH = selectedP.newHorari(horaIni, horaFi, diesDef);
            return selectedH != null;
        }
    }

    public boolean hasHorari() {
        if (selectedP == null) return false;
        else {
            if (selectedP.hasHorari()) {
                selectedH = selectedP.getHorari();
                selectedH.refresh();
                return true;
            } else return false;
        }
    }

    public String getDiesHorari() {
        if (selectedH == null) return "";
        else {
            return selectedH.getDies();
        }
    }

    public int getHoraIni() {
        if (selectedH == null) return 0;
        else {
            return selectedH.getHoraIni();
        }
    }

    public int getHoraFi() {
        if (selectedH == null) return 0;
        else {
            return selectedH.getHoraFi();
        }
    }

    public String getSpotAssig(int dia, int hora, int aula) {
        if (selectedH == null) return null;
        else {
            pos p = new pos(dia, hora, aula);
            Grup g = selectedH.getSpot(p);
            if (g == null) return null;
            else return g.getIdAssignatura();
        }
    }

    public String getSpotNumGrup(int dia, int hora, int aula) {
        if (selectedH == null) return null;
        else {
            pos p = new pos(dia, hora, aula);
            Grup g = selectedH.getSpot(p);
            if (g == null) return null;
            else return g.getNum();
        }
    }

    public boolean moveGrup(int diaIni, int diaFi, int horaIni, int horaFi, int aulaIni, int aulaFi, String nomGrup) {
        if (selectedH == null || selectedP == null) return false;
        else {
            if (nomGrup != null && !nomGrup.isEmpty()) {
                pos ini = new pos(diaIni, horaIni, aulaIni);
                pos fi = new pos(diaFi, horaFi, aulaFi);
                String nomAssig = nomGrup.split(System.getProperty("line.separator"), 2)[0];
                String numGrup = nomGrup.split(System.getProperty("line.separator"), 2)[1];
                Grup grup = selectedP.getAssig(nomAssig).getGrupByNum(numGrup);
                if (grup != null) return selectedH.movePos(ini, fi, grup);
                else return false;
            } else return false;
        }
    }

    public void putSpot(int dia, int hora, int aula) {

    }

    ///////////////////////////
    // Restriccions managing //
    ///////////////////////////

    public void clearRestriccions() {
        if (restriccions == null) restriccions = new ArrayList<>();
        restriccions.clear();
    }

    public ArrayList<String> getLlistaRestriccions(){
        if (restriccions.size()==0) return new ArrayList<>(); // mirar cada restricció per veure quina mena de format té l'string
        else {
            ArrayList<String> rest = new ArrayList<>();
            for (Restriccio r : restriccions) {
                rest.add(r.toString());
            }
            return rest;
        }
    }

    public boolean addRestriccioDia(int dia) {
        Restriccio rest = new Dia(DiaSetmana.nom(dia));
        restriccions.add(rest);
        return true;
    }

    public boolean addRestriccioHora(int horaIni, int horaFi) {
        Restriccio rest = new Hores(horaIni, horaFi);
        restriccions.add(rest);
        return true;
    }

    public boolean addRestriccioDiaHora(int dia, int horaIni, int horaFi) {
        Restriccio rest = new DiaIHora(DiaSetmana.nom(dia), horaIni, horaFi);
        restriccions.add(rest);
        return true;
    }

    public boolean addRestriccioMatins() {
        Restriccio rest = new Matins();
        restriccions.add(rest);
        return true;
    }

    public boolean addRestriccioTardes() {
        Restriccio rest = new Tardes();
        restriccions.add(rest);
        return true;
    }

    public boolean addRestriccionsAula() {
        if (selectedA == null) return false;
        else {
            for (Restriccio r : restriccions) {
                selectedA.addRestriccio(r);
            }
            clearRestriccions();
            return true;
        }
    }

    public boolean addRestriccionsGrup() {
        if (selectedG == null) return false;
        else {
            for (Restriccio r : restriccions) {
                selectedG.addRestriccio(r);
            }
            clearRestriccions();
            return true;
        }
    }

    public boolean addRestriccionsAssig() {
        if (selectedS == null) return false;
        else {
            for (Restriccio r : restriccions) {
                selectedS.addRestriccio(r);
            }
            clearRestriccions();
            return true;
        }
    }

    public boolean addRestriccionsAulaToTemp() {
        if (selectedA == null) return false;
        else {
            restriccions.addAll(selectedA.getRestriccions());
            return true;
        }
    }

    public boolean addRestriccionsAssigToTemp() {
        if (selectedS == null) return false;
        else {
            restriccions.addAll(selectedS.getRestriccions());
            return true;
        }
    }

    public boolean addRestriccionsGrupToTemp() {
        if (selectedG == null) return false;
        else {
            restriccions.addAll(selectedG.getRestriccions());
            return true;
        }
    }

    public boolean deleteRestriccio(int i) {
        if (i > restriccions.size()) return false;
        else {
            restriccions.remove(i);
            return true;
        }
    }

    ////////////
    // Altres //
    ////////////

    public ArrayList<String> getDies() {
        ArrayList<String> ret = new ArrayList<String>();
        for (DiaSetmana d : EnumSet.allOf(DiaSetmana.class)) {
            ret.add(d.toString());
        }
        return ret;
    }
}
