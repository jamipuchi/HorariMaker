package Domain;

import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.Set;

public class CjtInstitucio {

    // variable of type String
    @Expose
    private HashMap<String, Institucio> institucions;

    // private constructor restricted to this class itself
    private CjtInstitucio() {
        if (InstanceHolder.instance != null) {
//            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
        institucions = new HashMap<>();
    }

    private static class InstanceHolder {
        @Expose(serialize = false, deserialize = false)
        private static volatile CjtInstitucio instance = new CjtInstitucio();
        public static void setInstance(CjtInstitucio i) {
            instance = i;
        }
    }

    // static method to create instance of Singleton class
    public static synchronized CjtInstitucio getInstance() {
        if (InstanceHolder.instance == null) {
            synchronized (CjtInstitucio.class) {
                if (InstanceHolder.instance == null) InstanceHolder.setInstance(new CjtInstitucio());
            }
        }
        return InstanceHolder.instance;
    }

    protected CjtInstitucio readResolve() {
        return getInstance();
    }

    public void removeInstitucio(String name) {
        institucions.remove(name);
    }

    public void addObject(Institucio o){
        institucions.put(o.getName(), o);
    }

    public Institucio getInst(String name){
        return institucions.getOrDefault(name, null);
    }

    public Set<String> getKeyList() {
        return institucions.keySet();
    }

    public void setInstance(CjtInstitucio i) {
        InstanceHolder.setInstance(i);
    }
}
