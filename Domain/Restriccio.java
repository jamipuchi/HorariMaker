package Domain;

import com.google.gson.annotations.Expose;

public abstract class Restriccio {
    @Expose
    private int id;
    protected transient String type;

    public Restriccio(String type) {
        this.type = type;
    }

    public abstract boolean check(DiaSetmana dia, int hora, Horari h);

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toString(){
        return "Restriccio [type=" + type + "]";
    };
}
