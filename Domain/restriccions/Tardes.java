package Domain.restriccions;

import Domain.DiaSetmana;
import Domain.Horari;
import Domain.Restriccio;

public class Tardes extends Restriccio {
    public Tardes() {
        super("Tardes");
    }

    @Override
    public boolean check(DiaSetmana dia, int hora, Horari h) {
        return hora > 14;
    }

    @Override
    public String toString() {
        return "Tardes";
    }
}
