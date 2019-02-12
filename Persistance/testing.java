package Persistance;

import Domain.CjtUsuari;
import Domain.ctrlDomain;

import java.io.IOException;

public class testing {

    public static void main(String args[]) throws IOException {
        ctrlDomain CD = ctrlDomain.getInstance();

        //CD.registerUser("Mike", "123");
        //CD.saveUsers();
        CD.getBBDD("./BBDD");
        CD.logIn("Mike","123");
        //CD.createInstitucio("fib");
        System.out.println(CD.chooseInstitucio("uab"));
        //System.out.println(CD.createPla("compu"));
        System.out.println(CD.getPlans());
        CD.choosePla("si");
        //CD.createAssig("SO", 10);
        //CD.createAula("Vj", "lab");
        //CD.chooseAula(1);
        //System.out.println(CD.removeAula());
        //System.out.println(CD.getAules());
        //CD.chooseAssig("SO2");
        //CD.addCorequisit("SO");
        //CD.saveInstitutionsBBDD();
        System.out.println(CD.getAssigs());
        CD.chooseAssig("SO");
        //CD.addGroups(10,20);
        //CD.saveInstitutionsBBDD();
        System.out.println(CD.getGroups());
        CD.saveBBDD();
    }
}
