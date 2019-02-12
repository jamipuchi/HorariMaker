package Domain.restriccions;

import Domain.DiaSetmana;
import Domain.Horari;
import Domain.Restriccio;

public class Matins extends Restriccio {
    public Matins() {
        super("Matins");
    }

    @Override
    public boolean check(DiaSetmana dia, int hora, Horari h) {
        return hora <= 14;
    }

    @Override
    public String toString() {
        return "Matins";
    }
}
