package Domain;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class CjtUsuari {

    @Expose
    private ArrayList<Usuari> cjtUsers = new ArrayList<>();

    private CjtUsuari(){}

    private static class InstanceHolder {
        @Expose(serialize = false, deserialize = false)
        private static volatile CjtUsuari instance = new CjtUsuari();
        public static void setInstance(CjtUsuari i) {
            instance = i;
        }
    }

    public static synchronized CjtUsuari getInstance() {
        if (InstanceHolder.instance == null) {
            synchronized (CjtInstitucio.class) {
                if (InstanceHolder.instance == null) InstanceHolder.instance = new CjtUsuari();
            }
        }
        return InstanceHolder.instance;
    }

    protected CjtUsuari readResolve() {
        return getInstance();
    }

    public void setInstance(CjtUsuari i) {
        InstanceHolder.setInstance(i);
    }

    public void add(Usuari user){
        cjtUsers.add(user);
    }

    public Usuari select(String name) {

        Usuari user  = null;
        for (Usuari iter : cjtUsers){
            if (iter.getName().equals(name)) {
                user = iter;
            }
        }
        return user;
    }

    public boolean exists(String name) {
        try {
            for (Usuari u : cjtUsers) {
                if (u == null) System.out.println("l'usuari és null");
                if (u.getName().equals(name)) return true;
            }
            return false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean isEmpty(){
        return cjtUsers.isEmpty();
    }

    public void list(){
        int i = 0;
        for (Usuari user : cjtUsers) {
            if (user != null) {
                System.out.print("User " + i + " --- ");
                System.out.println(user.getName());
                ++i;
            }
        }
    }
}
