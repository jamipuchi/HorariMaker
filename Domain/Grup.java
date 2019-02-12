package Domain;
import com.google.gson.annotations.Expose;

import java.lang.String;
import java.util.ArrayList;

public class Grup {
    @Expose
    private int idGrup; // identificador

    @Expose
    private int idSuperGrup;
//    private Assignatura assignatura;
    @Expose
    private ArrayList<Restriccio> restriccions;

    @Expose
    private String idAssignatura;
    @Expose
    private String num;
    @Expose
    private String tipus;
    @Expose
    private int hours;

    public Grup(String num, int hours, String assignatura){
        this.num = num;
        this.hours = hours;
//        this.assignatura = assignatura;
        this.idAssignatura = assignatura;
        restriccions = new ArrayList<>();
    }

    public String getTipus() {
        return tipus;
    }

    public void setTipus(String tipus) {
        this.tipus = tipus;
    }

    public String getNum(){
        if (Integer.parseInt(num) < 10) return "0" + num;
        return num;
    }

    public void setNum(String num){
        this.num = num;
    }

    public void setIdSuperGrup(int idSuperGrup) {
        this.idSuperGrup = idSuperGrup;
    }

    public String getIdAssignatura() {
        return idAssignatura;
    }

//    public Assignatura getAssignatura() {
//        return ctrlDomain.getInstance().getAssignatura(idAssignatura);
//    }

//    public void setAssignatura(Assignatura assignatura) {
//        this.assignatura = assignatura;
//    }

    public int getIdGrup() {
        return idGrup;
    }

    public void setIdGrup(int idGrup) {
        this.idGrup = idGrup;
    }

    public int getHours() {
        return hours;
    }

    public void addRestriccio(Restriccio r){ this.restriccions.add(r); }

    public boolean checkRestriccions(DiaSetmana dia, int hora, Horari h) {
        if (restriccions.size() == 0) return true;
        for (Restriccio r : restriccions){
            if (!r.check(dia, hora, h)) return false;
        }
        return true;
    }

    public ArrayList<Restriccio> getRestriccions() {
        return restriccions;
    }

    public boolean deleteRestriccio(int i) {
        if (restriccions.size() > i) {
            restriccions.remove(i);
            return true;
        } else return false;
    }

    @Override
    public String toString() {
        if (Integer.parseInt(num) < 10) return idAssignatura + " 0"  + num;
        return idAssignatura + " "  + num;
    }
}
