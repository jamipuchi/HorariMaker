package Drivers;
import Domain.*;
import Persistance.ctrlPersistance;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class InstDriver {

    static private ctrlPersistance CP = ctrlPersistance.getInstance();

    static Scanner sc = new Scanner(System.in);

    static private void start(Scanner sc){

        String s = "---------------------------------------\n"
                +  "----------INSTITUTION DRIVER GUIDE-----\n"
                +  "---------------------------------------\n\n\n";

        System.out.println(s);
    }

    static private int menu(Scanner sc) {

        String s = "----------CHOOSE YOUR OPTION----------\n"
                 + "~~~~~~~   0  -> EXIT                       ~~~~~~~\n"
                 + "~~~~~~~   1  -> CREATE AN INSTITUTION      ~~~~~~~\n" // done
                 + "~~~~~~~   2  -> CHOOSE AN INSTITUTION      ~~~~~~~\n" // done
                 + "~~~~~~~   3  -> CREATE A STUDY PLAN        ~~~~~~~\n" // done: an institution has to be chosen
                 + "~~~~~~~   4  -> CHOOSE STUDY PLAN          ~~~~~~~\n" // done: an institution has to be chosen
                 + "~~~~~~~   5  -> CREATE SUBJECT             ~~~~~~~\n" // a study plan must be chosen
                 + "~~~~~~~   6  -> CHOOSE SUBJECT             ~~~~~~~\n" // a study plan must be chosen
                 + "~~~~~~~   7  -> ADD RESTRICTION TO SUBJECT ~~~~~~~\n" // a subject must ne chosen
                 + "~~~~~~~   8  -> CREATE CLASSROOM           ~~~~~~~\n" // done: an institution has to be chosen
                 + "~~~~~~~   9  -> CHOOSE CLASSROOM           ~~~~~~~\n" // done: an institution has to be chosen
                 + "~~~~~~~   10 -> ADD RESTRICTION TO CLASSR..~~~~~~~\n" // a subject must be selected and a group created
                 + "~~~~~~~   11 -> CREATE GROUPS              ~~~~~~~\n" // a subject and a study plan must be selected
                 + "~~~~~~~   12 -> CHOOSE GROUP               ~~~~~~~\n" // a subject must be selected and a group created
                 + "~~~~~~~   13 -> ADD RESTRICTION TO GROUP   ~~~~~~~\n" // a subject must be selected and a group created
                 + "~~~~~~~   14 -> CREATE TIMETABLE           ~~~~~~~\n" // a classroom and a group must be chosen
                 ;

        System.out.println(s);
        return sc.nextInt();
    }

    static private void wip() {

        String s = "~~~~~~~   WORK IN PROGRESS    ~~~~~~~~\n"
                 + "~~~~~~~   PLEASE STAND BY!    ~~~~~~~~";

        System.out.println(s);
    }

    static private void printStringSet(Set<String> set){
        for (String item : set){
            System.out.println(item);
        }
    }

    static private String stringChooser(Set<String> set, Scanner sc){
        if (set.size() > 0) {
            printStringSet(set);
            System.out.println("Type the exact name of your choice:");
            String in = sc.next();
            if (set.contains(in)) {
                System.out.println(in + " chosen!");
                return in;
            } else {
                System.out.println("The specified name doesn't exist");
                return null;
            }
        } else {
            System.out.println("There are no items");
        }
        return null;
    }

    static void main(Scanner sc){

        start(sc);
        int choose_option = -1;
        Institucio i = null;
        PlaEstudis sp = null;
        Assignatura s = null;
        Aula a = null;
        Grup g = null;
        Set<String> keys;

        while (choose_option != 0){

            String in;

            try {

                choose_option = menu(sc);

                switch(choose_option) {

                    case 0:

                        System.out.println("Bye Bye!");
                        break;

                    case 1: // 1 -> CREATE AN INSTITUTION

                        System.out.println("Set a NAME please:");
                        i = new Institucio(sc.next());
                        CjtInstitucio.getInstance().addObject(i);
                        System.out.println("¡¡¡Institution created!!!");
                        break;

                    case 2: // 2 -> CHOOSE AN INSTITUTION

                        CjtInstitucio cjti = CjtInstitucio.getInstance();

                        keys = cjti.getKeyList();

                        in = stringChooser(keys, sc);
                        if (in != null) {
                            i = cjti.getInst(in);
                        }

                        break;

                    case 3: // 3 -> CREATE A STUDY PLAN (an institution has to be chosen)

                        if (i != null) {
                            System.out.println("Please introduce the name of the Study Plan:");
                            in = sc.next();
                            sp = i.addStudyPlan(in);
                            System.out.println("Study plan created correctly!!");
                        } else {
                            System.out.println("You must choose an institution first before creating a study plan");
                        }

                        break;

                    case 4: // 4 -> CHOOSE STUDY PLAN (an institution has to be chosen)

                        if (i != null) {

                            keys = i.getStudyPlans();

                            in = stringChooser(keys, sc);
                            if (in != null) {
                                sp = i.getStudyPlan(in);
                            }
                        } else {

                            System.out.println("You must choose an institution first before choosing a study plan");
                        }

                        break;

                    case 5: // 5 -> CREATE SUBJECT (a study plan must be chosen)

                        if (sp != null) {
                            System.out.println("Please introduce the name and hours of the Subject:");
                            in = sc.next();
                            int h = sc.nextInt();

                            s = new Assignatura(in, h, sp.getNomTitulacio(), sp.getInstitucio());
                            sp.addAssig(s);
                            System.out.println("Subject created correctly!!");

                        } else {
                            System.out.println("You must choose a study plan first before creating a subject");
                        }

                        break;

                    case 6: // 6 -> CHOOSE SUBJECT (a study plan must be chosen)

                        if (sp != null) {

                            keys = new HashSet<>(sp.getAssigs());
                            in = stringChooser(keys, sc);

                            if (in != null){
                                s = sp.getAssig(in);
                            }
                        } else {
                            System.out.println("You must choose a study plan first before choosing a subject");
                        }

                        break;

                    case 7: // 7 -> ADD RESTRICTION TO SUBJECT
                        AssigDriver.main(s, sc, sp);
                        break;

                    case 8: // 8 -> CREATE CLASSROOM (an institution has to be chosen)
                        if (i != null){
                            System.out.println("Please introduce the name and type of the Classroom:");
                            in = sc.next();
                            String in2 = sc.next();

                            a = new Aula(in, in2, i.getName());
                        } else {
                            System.out.println("You must choose an institution first before creating a study plan");
                        }
                        break;
                    case 9: // 9 -> CHOOSE CLASSROOM

                        ArrayList<Aula> aules = i.getAules();
                        for (int j = 0; j < aules.size(); j++) {
                            System.out.println("Aula id: "+j+" Aula name: "+aules.get(j).getNom());
                        }
                        System.out.println("Please introduce the id of a classroom to select it:");
                        int nAula=sc.nextInt();
                        if (nAula>=aules.size()) System.out.println("Wrong, select an id that exists");
                        else{
                            a=aules.get(nAula);
                            System.out.println("Classroom  "+a.getNom()+" selected");
                        }
                        break;
                    case 10: // 10 -> ADD RESTRICTION TO CLASSROOM
                        AulaDriver.main(a,sc, sp);
                        break;

                    case 11: // 11 -> CREATE GROUPS (a subject must be selected)
                        if (s != null){
                            System.out.println("Please introduce the number of supergroups:");
                            int num = sc.nextInt();

                            System.out.println("Please introduce the number of subgroups for each group:");
                            int num2 = sc.nextInt();

                            s.addGroups(num, num2);
                        } else {
                            System.out.println("You must choose a subject first before adding groups");
                        }
                        break;

                    case 12: // 12 -> CHOOSE GROUP
                        if (s != null){
                            System.out.println("Groups de: " + s.getName());
                            ArrayList<Grup> groups = s.getGrups();
                            if (groups.size() != 0){
                                for (Grup G : groups){
                                    System.out.println("Group Num: " + G.getNum() + " Group ID: " + G.getIdGrup());
                                }
                                System.out.println("Please introduce the id of a group to select it:");
                                int nGrup = sc.nextInt();
                                if (nGrup>=groups.size()) System.out.println("Please select an id that exists");
                                else{
                                    g=groups.get(nGrup);
                                    System.out.println("Group "+g.getNum()+" selected");
                                }
                            } else{
                                System.out.println("There are no groups created for this subject");
                            }

                        }
                        else System.out.println("You must choose a subject first");
                        break;
                    case 13: // 13 -> ADD RESTRICTIONS TO GROUP
                        GrupDriver.main(g,sc,sp);
                        break;
                    case 14: // 14 -> CREATE TIMETABLE (an institution and a study plan must be chosen)
                        if (sp != null){
                            HorariDriver.main(sp, sc);
                        } else {
                            System.out.println("You must choose an institution and a study plan before creating a timetable");
                        }
                        break;

                    default:
                        System.out.println("Invalid option");
                        break;
                }

                System.out.printf("\n");

            } catch (Exception e){
                System.out.println("Institution Driver Error: " + e.getMessage());
                break;
            }
        }
    }
}
