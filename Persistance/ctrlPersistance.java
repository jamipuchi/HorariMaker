package Persistance;

import Domain.ctrlDomain;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

public class ctrlPersistance {

    private ctrlDomain CD;
    private File BBDD = new File("./BBDD");
    private File usersFile = new File(BBDD,"Users.json");
    private File institutionsFile = new File(BBDD,"Institutions.json");
    private File restrictFile = new File(BBDD, "Restrictions.json");


    private static class InstanceHolder {
        private static final ctrlPersistance instance = new ctrlPersistance();
    }

    public static ctrlPersistance getInstance() {
        return InstanceHolder.instance;
    }

    private ctrlPersistance(){
        CD = ctrlDomain.getInstance();
    }

    public void saveUsers(String usersJson){
        if (!usersFile.exists()) {
            try {
                usersFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try{
            PrintWriter pw = new PrintWriter(usersFile);
            pw.print(usersJson);
            pw.close();
        }catch (FileNotFoundException fnfe){
            System.out.println(fnfe);
        }
    }

    public String getUsers() throws IOException {
        String usersJson = "";
        if (usersFile.exists()){
            try{
                List<String> lines = Files.readAllLines(usersFile.toPath());
                for (String line : lines){
                    usersJson += line;
                }
            }catch (FileNotFoundException fnfe){
                System.out.println(fnfe);
            }
        }
        return usersJson;
    }

    public void saveInstitutions(String institutionsJson){
        if (!institutionsFile.exists()) {
            try {
                institutionsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try{
            PrintWriter pw = new PrintWriter(institutionsFile);
            pw.print(institutionsJson);
            pw.close();
        }catch (FileNotFoundException fnfe){
            System.out.println(fnfe);
        }
    }

    public String getInstitutions() throws IOException {
        String institutionsJson = "";
        if (institutionsFile.exists()){
            try {
                List<String> lines = Files.readAllLines(institutionsFile.toPath());
                for (String line : lines){
                    institutionsJson += line;
                }
            }catch (FileNotFoundException fnfe){
                System.out.println(fnfe);
            }
        }
        return institutionsJson;
    }

    public void saveRestrictions(String restrictJson) throws IOException {
        if (!restrictFile.exists()){
            try{
                restrictFile.createNewFile();
            } catch (FileNotFoundException fnfe){
                System.out.println(fnfe);
            }
        }
        try {
            PrintWriter pw = new PrintWriter(restrictFile);
            pw.print(restrictJson);
            pw.close();
        } catch (FileNotFoundException fnfe){
            System.out.println(fnfe);
        }
    }

    public String getRestrictions() throws IOException {
        String restrictJson = "";
        if (restrictFile.exists()){
            try{
                List<String> lines = Files.readAllLines(restrictFile.toPath());
                for (String line : lines){
                    restrictJson += line;
                }
            }catch (FileNotFoundException fnfe){
                System.out.println(fnfe);
            }
        }
        return restrictJson;
    }

    public void setBBDD(String BBDD) {
        this.BBDD = new File(BBDD);
        if (!this.BBDD.exists()) this.BBDD.mkdir();
    }
}
