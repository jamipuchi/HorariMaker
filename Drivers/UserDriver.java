package Drivers;
import Domain.CjtUsuari;
import Domain.Usuari;
import Persistance.ctrlPersistance;

import java.util.Scanner;

public class UserDriver {

    static private ctrlPersistance CP = ctrlPersistance.getInstance();

    static Scanner sc = new Scanner(System.in);

    static private void start(){

        String s = "---------------------------------------\n"
                 + "----------USER DRIVER GUIDE------------\n"
                 + "---------------------------------------\n\n";

        System.out.println(s);
    }

    static private int menu() {

        String s = "----------CHOOSE YOUR OPTION----------\n"
                 + "~~~~~~~   0 -> EXIT            ~~~~~~~\n"
                 + "~~~~~~~   1 -> CREATE A USER   ~~~~~~~\n"
                 + "~~~~~~~   2 -> CHOOSE A USER   ~~~~~~~\n"
                 + "~~~~~~~   3 -> CHANGE PASSWORD ~~~~~~~\n"
                 + "~~~~~~~   4 -> ASK PERMISSION  ~~~~~~~\n"
                 + "~~~~~~~   5 -> SET ADMIN       ~~~~~~~\n"
                 + "~~~~~~~   6 -> CHANGE NAME     ~~~~~~~\n"
                 + "~~~~~~~   7 -> EXEC INSTDRIVER ~~~~~~~\n";

        System.out.println(s);
        return sc.nextInt();
    }

    static private void wip() {

        String s = "~~~~~~~   WORK IN PROGRESS    ~~~~~~~~\n"
                 + "~~~~~~~   PLEASE STAND BY!    ~~~~~~~~";

        System.out.println(s);
    }

    public static void main(String[] args){

        start();
        int choose_option = -1;
        Usuari user = null;
        CjtUsuari cjtUser = CjtUsuari.getInstance();

        while (choose_option != 0){

            try {

                choose_option = menu();

                String pass;
                String name;

                switch(choose_option) {

                    case 0:

                        System.out.println("Bye Bye!");
                        break;

                    case 1:

                        System.out.println("Set a NAME and a PASSWORD please:");
                        name = sc.next();
                        pass = sc.next();
                        user = new Usuari(name,pass);
                        cjtUser.add(user);
                        System.out.println("¡¡¡User created!!!");
                        break;

                    case 2:

                        if (cjtUser.isEmpty()) System.out.println("There's no User selected");

                        else {
                            cjtUser.list();
                            System.out.println("Type the name of your choice:");
                            name = sc.next();
                            user = cjtUser.select(name);
                        }

                        break;

                    case 3:

                        if (user != null) {

                            System.out.println("Please introduce your new password:");
                            pass = sc.next();
                            user.setPassword(pass);
                            System.out.println("Password set correctly!!");
                        }

                        else {
                            System.out.println("You must create a user first before setting up a password");
                        }

                        break;

                    case 4:

                        if (user != null) {

                            if (user.getPerms()){
                                System.out.println("You are an Administrator ¡¡Hurrey!!");
                            }

                            else {
                                System.out.println("You don't have specials permits ;(((");
                            }
                        }  else {
                            System.out.println("There's no user selected or created");
                        }

                        break;

                    case 5:

                        if (user != null) {

                            if (user.getPerms()){

                                System.out.println("This user is already an admin");
                            }

                            else {

                                user.makeAdmin();
                                System.out.println("This user is now an Administrator");
                            }
                        }

                        else {
                            System.out.println("There's no user selected or created");
                        }

                        break;

                    case 6:

                        if (user != null) {

                            System.out.println("Please type your new name: ");
                            name = sc.next();
                            user.setName(name);
                            System.out.println("Name changed correctly!!!");
                        }

                        else {

                            System.out.println("There's no user selected or created");
                        }

                        break;

                    case 7: // a user must be chosen and be admin

                        if (user != null && user.isAdmin()){
                            InstDriver.main(sc);
                        } else {
                            System.out.println("There's no admin user selected or created");
                        }
                        break;

                    default:

                        System.out.println("Invalid option");
                        break;
                }

                System.out.printf("\n");

            } catch (Exception e){
                System.out.println("Error de Driver de Usuario D;");
                break;
            }
        }
    }
}
