package Server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;

class HTTPSHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange he) throws IOException {
        // Handles the different requests based on path
        String path = he.getRequestURI().getPath();
        
        System.out.print("> Remote: " + he.getRemoteAddress().getAddress().getHostAddress());
        System.out.println(" Port: " + he.getRemoteAddress().getPort());
        System.out.print("> Local: " + he.getLocalAddress().getAddress().getHostAddress());
        System.out.println(" Port: " + he.getLocalAddress().getPort());
        
        if (null != path) switch (path) {
            case "/hangman/register":
                Register.handle(he);
                break;
            case "/hangman/login":
                Login.handle(he);
                break;
            case "/hangman/logout":
                Logout.handle(he);
                break;
            case "/hangman/matchmaking/host":
                MatchmakingHost.handle(he);
                break;
            case "/hangman/matchmaking/add":
                MatchmakingAdd.handle(he);
                break;
            case "/hangman/matchmaking/remove":
                MatchmakingRemove.handle(he);
                break;
            case "/hangman/matchmaking/view":
                MatchmakingView.handle(he);
                break;
            case "/hangman/matchmaking/propose":
                MatchmakingPropose.handle(he);
                break;
            case "/hangman/matchmaking/unpropose":
                MatchmakingUnpropose.handle(he);
                break;
            case "/hangman/matchmaking/proposals":
                MatchmakingProposals.handle(he);
                break;
            case "/hangman/matchmaking/accept":
                MatchmakingAccept.handle(he);
                break;
        }
            
        Server.printAccountList();
        Server.printLoginList();
    }
}