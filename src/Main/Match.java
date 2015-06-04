package Main;

public class Match {
    private final String name, ip_address;
    private final int port_number;

    public Match(String name, String ip_address, int port_number) {
        this.name = name;
        this.ip_address = ip_address;
        this.port_number = port_number;
    }

    public String getName() {
        return name;
    }

    public String getIPAddress() {
        return ip_address;
    }

    public int getPortNumber() {
        return port_number;
    }
    
    public void print() {
        System.out.println("[Match] Name: " + name + " IP: " + ip_address + " Port: " + port_number);
    }
}
