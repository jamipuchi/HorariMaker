package Domain;
import com.google.gson.annotations.Expose;

import java.lang.String;
import java.util.ArrayList;

public class Aula {
    @Expose
    private String nom;
    @Expose
    private int capacity;
    @Expose
    private String tipus; // Can be lab, theory, ...
    @Expose
    private ArrayList<Restriccio> restriccions;

    @Expose
    private int idAula;
    @Expose(serialize = false, deserialize = false)
    private transient Institucio institucio = null;
    @Expose
    private String nomInst;

    public Aula(String ID, String tipus, String nomInst){
        this.nom = ID;
        this.tipus = tipus;
        this.institucio = CjtInstitucio.getInstance().getInst(nomInst);
        this.nomInst = nomInst;
        restriccions = new ArrayList<>();
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getNom() {
        return nom;
    }

    public String getTipus() {
        return tipus;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setTipus(String tipus) {
        this.tipus = tipus;
    }

    public Institucio getInstitucio() {
        return institucio;
    }

    public int getIdAula() {
        return idAula;
    }

    public void setIdAula(int idAula) {
        this.idAula = idAula;
    }

    public void addRestriccio(Restriccio r){
            this.restriccions.add(r);
    }

    public ArrayList<Restriccio> getRestriccions() {
        return restriccions;
    }

    public boolean checkRestriccions(DiaSetmana dia, int hora, Horari h) {
        for (Restriccio r : restriccions){
            if (!r.check(dia, hora, h)) return false;
        }
        return true;
    }

    public boolean deleteRestriccio(int i) {
        if (restriccions.size() > i) {
            restriccions.remove(i);
            return true;
        } else return false;
    }

}
