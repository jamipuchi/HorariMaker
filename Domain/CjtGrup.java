package Domain;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.HashMap;

public class CjtGrup {
    // static variable single_instance of type Singleton

    // variable of type String
    @Expose
    private int id;
    @Expose
    private int numSuperGroups;
    @Expose
    private int lastSuperGroup;
//    private HashMap<Integer, ArrayList<Grup>> grups;
    @Expose
    private ArrayList<Grup> allGroups;
    @Expose
    private int hores;
    @Expose
    private int numGrups;

    // private constructor restricted to this class itself
    public CjtGrup() {
        id = 0;
        numSuperGroups = 0;
        lastSuperGroup = 0;
        allGroups = new ArrayList<>();
//        grups = new HashMap<>();
    }

    public void addGroups(int nSG, int numSubGroups, int hours, Assignatura a) {
        numSuperGroups += nSG;
        hores = hours/2;
        numGrups = nSG*numSubGroups;

        //Assignem id automaticament als grups de la assignatura en funció dels últims grups creats
        try {
//            a.setCjtGrups(this);

            int groupNum = lastSuperGroup;
            ArrayList<Integer> grupsAssig = new ArrayList<>();

            while (nSG != 0) {
                ArrayList<Grup> subGroups = new ArrayList<>();
                for (int i = 0; i < numSubGroups; ++i) {
                    Grup g;
                    if (numSubGroups == 1) {
                         g = new Grup(Integer.toString(groupNum), hours, a.getName());
                    }
                    else {
                         g = new Grup(Integer.toString(groupNum), hours / 2, a.getName());
                    }
                    subGroups.add(g);
                    ++groupNum;
                    allGroups.add(g);
                    g.setIdGrup(id);
                    id++;
                }

                //Afegeix el arraylist de subgrups que pertanyen a la assignatura
                for (int i = 0; i < subGroups.size(); ++i) {
                    grupsAssig.add(subGroups.get(i).getIdGrup());
                }

//                grups.put(groupNum, subGroups); //afegim al mapa de grups els subgrups del superGrup (groupNum)
                --nSG;

                while (groupNum % 10 != 0) ++groupNum;
                if (groupNum % 10 == 0) ++groupNum;
            }

            lastSuperGroup = groupNum;
        } catch (Exception e) {
            System.out.println("Error de addGroups: " + e);
        }
    }

    // Retornem els subGrups amb id = id
    public Grup getGrup(int id){
        if (id > allGroups.size() || id < 0) return null;
        return allGroups.get(id);
    }

    public Grup getGrupByNum(String num) {
        for (Grup g : allGroups) {
            if (g.getNum().equals(num)) return g;
        }
        return null;
    }

    public ArrayList<Grup> getGrups() {
        ArrayList<Grup> grups = new ArrayList<>();
        try {
            for (int i = 0; i < allGroups.size(); ++i) {
                grups.add(allGroups.get(i));
            }
        } catch (Exception e) {
            System.out.println("Error iterant sobre els grups de l'assignatura: " + e.getMessage());
        }
        return grups;
    }

    public HashMap<Integer, String> getIdGrups() {
        HashMap<Integer, String> grups = new HashMap<Integer, String>();
        try {
            for (Grup g : allGroups) {
                grups.put(g.getIdGrup(), g.getNum());
            }
        } catch (Exception e) {
            System.out.println("Error iterant sobre els grups de l'assignatura: " + e.getMessage());
        }
        return grups;
    }

    public int getTotalHores() {
        return numGrups*hores;
    }

    public boolean hasGrup(Grup selectedG) {
        return allGroups.size() > selectedG.getIdGrup();
    }

    public void removeGrup(Grup selectedG) {
        allGroups.remove(selectedG.getIdGrup()); // el borrem de la llista, el qual provoca un shift dels índexs de la resta
        for (int i = 0; i < allGroups.size(); ++i) { // canviem l'id dels grups que han canviat d'índex
            if (allGroups.get(i).getIdGrup() != i) allGroups.get(i).setIdGrup(i);
        }
    }
}
