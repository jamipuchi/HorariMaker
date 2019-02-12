package Domain;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Institucio {
    @Expose
    private String name; //id
    @Expose
    private int id; // per ids aules

    @Expose
    private HashMap<String, PlaEstudis> plans;
    @Expose
    private ArrayList<Aula> aules; // cada institució té un

    public Institucio(String name){
        plans = new HashMap<>();
        aules = new ArrayList<>();
        id = 0;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PlaEstudis addStudyPlan(String nameP) {
        PlaEstudis pla = new PlaEstudis(nameP, this.name);
        plans.put(nameP, pla);
        return pla;
    }

    public Set<String> getStudyPlans(){
        return plans.keySet();
    }

    public PlaEstudis getStudyPlan(String name){
        return plans.getOrDefault(name, null);
    }

    public ArrayList<Aula> getAules(){
        return aules;
    }

    public void addAula(Aula o){
        aules.add(o);
        o.setIdAula(id);
        ++id;
    }

    public int getNumAules(){
        return aules.size();
    }

    public boolean checkRestriccionsAula(DiaSetmana dia, int hora, Horari h, int idAula) {
        return aules.get(idAula).checkRestriccions(dia, hora, h);
    }

    public boolean removeAula(int idAula) {
        boolean deleted = false;
        for (int i = 0; i < aules.size() && !deleted; ++i) {
            if (aules.get(i).getIdAula() == idAula) {
                aules.remove(i);
                deleted = true;
            }
        }
        return deleted;
    }
}
