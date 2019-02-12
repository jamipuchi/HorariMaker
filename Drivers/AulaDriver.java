package Drivers;

import Domain.*;
import Domain.restriccions.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Scanner;
import java.util.Set;

public class AulaDriver
{
    static Scanner sc = new Scanner(System.in);

    static private void start(){

        String s = "---------------------------------------\n"
                 + "--------CLASSROOM DRIVER GUIDE---------\n"
                 + "---------------------------------------\n\n";

        System.out.println(s);
    }

    static private int menu(Scanner sc) {

        String s = "----------CHOOSE A RESTRICTION TO ADD----------\n"
                + "~~~~~~~   0 -> EXIT                     ~~~~~~~\n"
                + "~~~~~~~   1 -> DAY RESTRICTION          ~~~~~~~\n"
                + "~~~~~~~   2 -> HOUR RESTRICTION         ~~~~~~~\n"
                + "~~~~~~~   3 -> DAY && HOUR RESTRICTION  ~~~~~~~\n"
                + "~~~~~~~   4 -> MATINS                   ~~~~~~~\n"
                + "~~~~~~~   5 -> TARDES                   ~~~~~~~\n";

        System.out.println(s);
        return sc.nextInt();
    }

    static private void wip() {

        String s = "~~~~~~~   WORK IN PROGRESS    ~~~~~~~~\n"
                + "~~~~~~~   PLEASE STAND BY!    ~~~~~~~~";

        System.out.println(s);
    }

    static private DiaSetmana getDia(Scanner sc){
        ArrayList<DiaSetmana> dies = new ArrayList<>(EnumSet.allOf(DiaSetmana.class));
        Integer i = 0;
        Integer in = sc.nextInt();
        DiaSetmana diaDef = dies.get(in);

        return diaDef;
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

    protected static void main(Aula a, Scanner sc, PlaEstudis pla) {

        start();
        int choose = -1;
        while (choose != 0) {
            try {
            choose = menu(sc);
            switch (choose){

                case 0:
                    System.out.println("BYE BYE");
                    break;

                case 1: // RESTRICC
                    System.out.println("Please type the day (0,1,2,3,4,5,6) in which there won't be classes in this classroom:");
                    DiaSetmana dS = getDia(sc);
                    Restriccio h = new Dia(dS);
                    a.addRestriccio(h);
                    System.out.println("Restriction added! Classroom "+ a.getNom()+" won't have any classes on "+ dS);
                    break;

                case 2:
                    System.out.println("Please type the starting and ending hours (example: 12 16) in which there won't be classes in this classroom:");
                    int hI = sc.nextInt();
                    int hF = sc.nextInt();
                    Restriccio d = new Hores(hI, hF);
                    a.addRestriccio(d);
                    System.out.println("Restriction added! "+ a.getNom()+" won't have any classes from "+hI+" to "+hF);
                    break;

                case 3:
                    System.out.println("Please type the the hours and then the day (example: 12 16 1) in which there won't be classes in this classroom :");
                    int hI2 = sc.nextInt();
                    int hF2 = sc.nextInt();
                    DiaSetmana dS2 = getDia(sc);
                    Restriccio dh = new DiaIHora(dS2, hI2, hF2);
                    a.addRestriccio(dh);
                    System.out.println("Restriction added! Classroom "+ a.getNom()+" won't have any classes from "+hI2+" to "+hF2+" on "+dS2);
                    break;

                case 4:

                    System.out.println("Restriction added! "+a.getNom()+" will only have classes on the morning");
                    Restriccio matins = new Matins();
                    a.addRestriccio(matins);
                    break;

                case 5:
                    System.out.println("Restriction added! "+a.getNom()+" will only have classes on the evening");
                    Restriccio tardes = new Tardes();
                    a.addRestriccio(tardes);
                    break;

                default:
                    break;
            }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }
}
