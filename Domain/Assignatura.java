package Domain;
import com.google.gson.annotations.Expose;

import java.lang.String;
import java.lang.reflect.Array;
import java.util.*;

public class Assignatura {

    @Expose
    private String name; // id
    @Expose
    private int level;
    @Expose
    private int capacity;
    @Expose
    private int hours;
    @Expose
    private ArrayList<Restriccio> restriccions;
    @Expose
    private Set<String> corequisits; // assignatures prerequisides

    @Expose
    private CjtGrup cjtGrups;
    @Expose(serialize = false, deserialize = false)
    private PlaEstudis pla = null;
    @Expose
    private int num;
    @Expose
    private int num2;

    public Assignatura(String name) {
        this.name = name;
        restriccions = new ArrayList<>();
        corequisits = new TreeSet<>();
    }

    public Assignatura(String name, int hours, String pla, String inst) {
        this(name);
        this.hours = hours;
        this.pla = CjtInstitucio.getInstance().getInst(inst).getStudyPlan(pla);
        this.cjtGrups= new CjtGrup();
    }

    public String getName() {
        return name;
    }

    public int getLevel(){
        return level;
    }

    public int getCapacity(){
        return capacity;
    }

    public int getHours() {
        return hours;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Restriccio> getRestriccions() { return restriccions; }

    public void addRestriccio(Restriccio r){ this.restriccions.add(r); }

    public Set<String> getCoRequisits() { return corequisits; }

    public void addCoRequisit(String r) {
        corequisits.add(r);
    }

    public void deleteCoRequisit(String name) {
        corequisits.remove(name);
    }

    boolean checkRestriccions(DiaSetmana dia, int hora, Horari h) {
        if (restriccions.size() == 0) return true;
        for (Restriccio r : restriccions){
            if (!r.check(dia, hora, h)) return false;
        }
        return true;
    }

    public ArrayList<Grup> getGrups() {
        return cjtGrups.getGrups();
    }

    public void setCjtGrups(CjtGrup cjtGrups){
        this.cjtGrups = cjtGrups;
    }

    public int getNumAules() {
        return pla.getNumAules();
    }

    public void addGroups(int num, int num2) {
        this.num = num;
        this.num2 = num2;
        cjtGrups = new CjtGrup();
        cjtGrups.addGroups(num, num2, this.hours, this);
    }

    public int getNum() {
        return num;
    }

    public int getNum2() {
        return num2;
    }

    public int getNumHoresGrups() {
        return cjtGrups.getTotalHores();
    }

    public HashMap<Integer, String> getIdGrups() {
        return cjtGrups.getIdGrups();
    }

    public Grup getGrup(int idGrup) {
        return cjtGrups.getGrup(idGrup);
    }

    public Grup getGrupByNum(String num) {
        return cjtGrups.getGrupByNum(num);
    }

    public void removeGroups() {
        cjtGrups = null;
    }

    public boolean removeGrup(Grup selectedG) {
        if (cjtGrups == null) return false;
        else {
            if (cjtGrups.hasGrup(selectedG)) {
                cjtGrups.removeGrup(selectedG);
                return true;
            } else return false;
        }
    }

    public boolean deleteRestriccio(int i) {
        if (restriccions.size() > i) {
            restriccions.remove(i);
            return true;
        } else return false;
    }
}