package Main;

import java.util.Objects;

public class Account {
    private final String username, name, password;
    
    public Account(String username, String name, String password) {
        this.username = username;
        this.name = name;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
    
    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }
    
    public boolean checkUsername(String username) {
        return this.username.equals(username);
    }
    
    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }
    
    @Override
    public boolean equals(Object ac){
        if(ac == null) return false;
        if (!(ac instanceof Account)) return false;
        return this.username.equals(((Account) ac).username);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.username);
        return hash;
    }

    public void print() {
        System.out.print("Username:" + username + " Name:" + name + " Password:" + password);
    }

    public void println() {
        System.out.println("Username:" + username + " Name:" + name + " Password:" + password);
    }
}
