package Drivers;
import Domain.*;
import Domain.restriccions.Dia;

import java.util.*;

public class HorariDriver {

    static Scanner sc = new Scanner(System.in);

    static private void start(){

        String s = "----------------------------------\n"
                 + "------TIMETABLE DRIVER GUIDE------\n"
                 + "----------------------------------\n";

        System.out.println(s);
    }

    static private int menu(Scanner sc) {

        String s = "------CHOOSE YOUR OPTION------\n"
                 + "~~~~~~~   0 -> EXIT                 ~~~~~~~\n"
                 + "~~~~~~~   1 -> CREATE TIMETABLE     ~~~~~~~\n"
                 + "~~~~~~~   2 -> PRINT TIMETABLE      ~~~~~~~\n" // a timetable must be chosen
                 ;

        System.out.println(s);
        return sc.nextInt();
    }

    static private void wip() {

        String s = "~~~~~~~   WORK IN PROGRESS    ~~~~~~~~\n"
                 + "~~~~~~~   PLEASE STAND BY!    ~~~~~~~~";

        System.out.println(s);
    }

    static class SortByDia implements Comparator<DiaSetmana> {
        public int compare(DiaSetmana a, DiaSetmana b){
            if (a.num() < b.num()) return -1;
            else if (a.num() == b.num()) return 0;
            else return 0;
        }
    }

    static private ArrayList<DiaSetmana> getDies(Scanner sc){
        System.out.print("Introdueix els numeros del/s dies lectius, tot seguits, en qualsevol ordre:\n");
        ArrayList<DiaSetmana> dies = new ArrayList<>(EnumSet.allOf(DiaSetmana.class));
        int i = 0;
        for (DiaSetmana dia : dies){
            System.out.print(Integer.toString(i) + ": " + dia.toString());
            i++;
            if (i < dies.size()){
                System.out.print(" | ");
            } else {
                System.out.print("\n");
            }
        }
        String in = sc.next();
        ArrayList<DiaSetmana> diesDef = new ArrayList<>();
        for (i = 0; i < in.length(); i++){
            int x = Character.getNumericValue(in.charAt(i));
            diesDef.add(dies.get(x));
        }
        diesDef.sort(new SortByDia());
        return diesDef;
    }

    static void main(PlaEstudis sp, Scanner sc) {
        try {
            HorariDriver obj = new HorariDriver();
            obj.run(sp, sc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void run(PlaEstudis sp, Scanner sc){

        start();
        int choose_option = -1;

        while (choose_option != 0){

            int in2, in3;

            try {

                choose_option = menu(sc);

                switch(choose_option) {

                    case 0:

                        System.out.println("Bye Bye!");
                        break;

                    case 1: // 1 -> CREATE TIMETABLE
                        System.out.println("Enter the times of start and end");
                        in2 = sc.nextInt();
                        in3 = sc.nextInt();
                        ArrayList<DiaSetmana> dies = getDies(sc);

                        System.out.println("Perfect, now we'll create the timetable!");
                        sp.newHorari(in2, in3, dies);
                        break;

                    case 2: // 2 -> PRINT TIMETABLE

                        sp.printHorari();
                        break;


                    default:

                        System.out.println("Invalid option");
                        break;
                }

                System.out.printf("\n");

            } catch (Exception e){
                System.out.println("Horari Driver Error");
                break;
            }
        }
    }
}
