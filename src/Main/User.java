package Main;

import java.util.ArrayList;

public class User {
    private final String name, username, ip_address;
    private final boolean proposedField;
    private final boolean proposed;

    public User(String name, String username, String ip_address) {
        this.name = name;
        this.username = username;
        this.ip_address = ip_address;
        proposedField = false;
        this.proposed = false;
    }

    public User(String name, String username, String ip_address, String proposed) {
        this.name = name;
        this.username = username;
        this.ip_address = ip_address;
        proposedField = true;
        this.proposed = proposed.equals("true");
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getIpAddress() {
        return ip_address;
    }

    public void print() {
        System.out.print("[User] Name:" + name + " Username:" + username + " IP Address:" + ip_address);
        if (proposedField) {
            if (proposed) {
                System.out.println(" [Proposed]");
            } else {
                System.out.println(" [Not proposed]");
            }
        } else {
            System.out.println("");
        }
    }
    
    public static void print(ArrayList<User> users) {
        int size = users.size();
        for (int i=0; i<size; i++) {
            users.get(i).print();
        }
    }
    
    public static void printIndexed(ArrayList<User> users) {
        int size = users.size();
        for (int i=0; i<size; i++) {
            System.out.print((i+1) + ". ");
            users.get(i).print();
        }
    }
}
