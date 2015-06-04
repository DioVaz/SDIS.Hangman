package Main;

import java.util.ArrayList;

public class LoggedIn {
    private final Account account;
    private String ip_address;
    private boolean available; // Available for matchmaking offers
    private ArrayList<LoggedIn> proposals; // Match proposals from other accounts
    private int port_number; // Port to be used by the server to notify the user
    
    public LoggedIn(Account account, String ip_address, int port_number) {
        this.account = account;
        this.ip_address = ip_address;
        this.available = false;
        this.proposals = new ArrayList<LoggedIn>();
        this.port_number = port_number;
    }

    public void acceptedMatch() {
        available = false;
        proposals.clear();
    }

    public boolean isAvailable() {
        return available;
    }
    public ArrayList<LoggedIn> getProposals() {
        return proposals;
    }
    public Account getAccount() {
        return account;
    }
    public String getIPAddress() {
        return ip_address;
    }
    public int getPortNumber() {
        return port_number;
    }
    
    public void setIPAddress(String ip_address) {
        this.ip_address = ip_address;
    }
    public void setPortNumber(int port_number) {
        this.port_number = port_number;
    }
    public void setAvailable(boolean available) {
        this.available = available;
        if (!this.available)
            proposals.clear();
    }
    public int addProposal(LoggedIn login) {
        if (!available)
            return 1;
        int numProposals = proposals.size();
        for (int i=0; i<numProposals; i++) {
            if (proposals.get(i).equals(login))
                return 2;
        }
        proposals.add(login);
        return 3;
    }
    public boolean removeProposal(String username) {
        int numProposals = proposals.size();
        for (int i=0; i<numProposals; i++) {
            if (proposals.get(i).getAccount().checkUsername(username)) {
                proposals.remove(i);
                return true;
            }
        }
        return false;
    }
    
    public void print() {
        System.out.print("[Login] ");
        account.print();
        System.out.println(" IP:" + ip_address + " Port:" + port_number + " Available:" + available);
        int numProposals = proposals.size();
        for (int i=0; i<numProposals; i++) {
            System.out.print("[Proposal " + i + "]");
            proposals.get(i).print();
        }
    }
}
