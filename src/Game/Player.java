/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

/**
 *
 * @author Diogo
 */
public class Player {
    String name;
    String username;
    String password;
    int PlayerPort;
    
    //empty contructor to create player offline
    public Player(){
        name="offline";
        username="offline";
    }
    //new player
    public Player(String name1,String password1){
        name=name1;
        password=password1;
    }
    
    //overload para criar Jogador adeversário na classe Game
    public Player(String username1){
        name="****";
        username=username1;
    }
    
    public void setUser(String name1){
        username=name1;
    }
    
    public String getUsername (){
        return username;
    }
    
    public String getPass (){
        return password;
    }
}
