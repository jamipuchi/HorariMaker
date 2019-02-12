package Domain.restriccions;

import Domain.DiaSetmana;
import Domain.Horari;
import Domain.Restriccio;
import com.google.gson.annotations.Expose;

public class Hores extends Restriccio {

    @Expose
    int horaInici;
    @Expose
    int horaFi;

    public Hores(int horaInici, int horaFi){
        super("Hores");
        this.horaInici = horaInici;
        this.horaFi = horaFi;
    }
    @Override
    public boolean check(DiaSetmana dia, int hora, Horari h) {
        if (hora >= horaInici && hora < horaFi) return false;
        else return true;
    }

    @Override
    public String toString() {
        return "Hora " + horaInici + " " + horaFi;
    }
}
