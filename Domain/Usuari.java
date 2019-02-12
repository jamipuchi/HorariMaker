package Domain;
import com.google.gson.annotations.Expose;

import java.lang.String;
import java.util.HashSet;
import java.util.Set;

public class Usuari {

	@Expose
	private Set<String> inst;

    public Usuari(String name, String password) {
        this.name = name;
        this.password = password;
        inst = new HashSet<>();
    }

	@Expose
	private String name;
	@Expose
	private String password;
	@Expose
	private boolean admin;

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getPassword(){
		return password;
	}	

	public void setPassword(String password){
		this.password = password;
	}

	public boolean getPerms() { return this.admin; }

	public boolean isAdmin(){
		return this.admin;
	}

	public void makeAdmin(){
		if (! admin) admin = true;
		else System.out.println("User is already an admin");
	}

    public void addInst(String name) {
		inst.add(name);
    }

	public Set<String> getInst() {
		return inst;
	}

	public void removeInstitucio(String name) {
		inst.remove(name);
	}
}
