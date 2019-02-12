package Domain.restriccions;

import Domain.DiaSetmana;
import Domain.Horari;
import Domain.Restriccio;
import com.google.gson.annotations.Expose;

public class DiaIHora extends Restriccio {

    @Expose
    Restriccio dia;
    @Expose
    Restriccio hora;

    public DiaIHora(DiaSetmana dia, int horaInici, int horaFi){
        super("DiaIHora");
        this.dia = new Dia(dia);
        this.hora = new Hores(horaInici, horaFi);
    }

    @Override
    public boolean check(DiaSetmana dia, int hora, Horari h) {
        if (!this.dia.check(dia, hora, h) && !this.hora.check(dia,hora, h)) return false;
        else return true;
    }

    @Override
    public String toString() {
        return dia.toString() + " " + hora.toString();
    }
}
