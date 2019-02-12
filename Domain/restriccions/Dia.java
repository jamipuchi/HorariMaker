package Domain.restriccions;

import Domain.DiaSetmana;
import Domain.Horari;
import Domain.Restriccio;
import com.google.gson.annotations.Expose;

public class Dia extends Restriccio {

    @Expose
    private DiaSetmana diaProhibit;

    public Dia(DiaSetmana diaProhibit){
        super("Dia");
        this.diaProhibit=diaProhibit;
    }

    @Override
    public boolean check(DiaSetmana dia, int horaClasse, Horari h){
//        System.out.println("Hola!! Jo prohibeixo els " + diaProhibit + "S");
        if (dia == diaProhibit) return false;
        else return true;
    }

    @Override
    public String toString() {
        return "Dia " + diaProhibit.toString();
    }
}
