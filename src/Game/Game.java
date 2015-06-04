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
public class Game {
    Player local;
    Player partner;
    String sentence;
    String serialSentence;
    String guess;
    Boolean isLocalTurn;
    char[] unusedLetters;
    String usedLetters;
    int lettersToWin;
    char letter;
    
    public Game(Player local1){
        local=local1;
    }
    
    public String getGuess(){
        return guess;
    }
    
    public void cleanUL(){
        char[] clean = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','Y','Z'};
        unusedLetters=clean;
        usedLetters=" Used Letters:";
    }
    
    public Player getPlayer(){
        return local;
    }
    
    public void setPartner(String user){
        Player vs = new Player(user);
        partner=vs;
    }
    
    public void setLocal(String user, String pass){
        Player vs = new Player(user,pass);
        local=vs;
    }
    
    public void startGame(String sentence1, Boolean turn, String user){
        sentence=sentence1.toUpperCase();
        serialSentence=serializeSentence(sentence1);
        isLocalTurn=turn;
        createGuess(sentence1);
        cleanUL();
        setPartner(user);
    }
    
    String serializeSentence(String sentence1){
        //serializar frase para evitar erros de comparação
        sentence1=sentence1.toUpperCase();
        //A
        sentence1=sentence1.replace('À', 'A');
        sentence1=sentence1.replace('Á', 'A');
        sentence1=sentence1.replace('Ã', 'A');
        sentence1=sentence1.replace('Â', 'A');
        //E
        sentence1=sentence1.replace('É', 'E');
        sentence1=sentence1.replace('È', 'E');
        sentence1=sentence1.replace('Ê', 'E');
        //I
        sentence1=sentence1.replace('Í', 'I');
        sentence1=sentence1.replace('Ì', 'I');
        sentence1=sentence1.replace('Î', 'I');
        //O
        sentence1=sentence1.replace('Ó', 'O');
        sentence1=sentence1.replace('Ò', 'O');
        sentence1=sentence1.replace('Õ', 'O');
        sentence1=sentence1.replace('Ô', 'O');
        //U
        sentence1=sentence1.replace('Ú', 'U');
        sentence1=sentence1.replace('Ù', 'U');
        sentence1=sentence1.replace('Û', 'U');
        //C
        sentence1=sentence1.replace('Ç', 'C');
        return sentence1;
    }
        
    public void saveLetter(){
        if(!updateGuess()) 
             changePlayer();  
    }
    
    public void createGuess (String sentence1){
        int count=0;
        String newGuess = "_ ";
        String space;
        for(int i=1;i<sentence1.length();i++){
            space = sentence1.substring(i, i+1);
            if(" ".equals(space)) //checkar se funciona
                newGuess=newGuess.concat("  ");
            else if(",".equals(space)) //checkar se funciona
                newGuess=newGuess.concat(", ");
            else if(".".equals(space)) //checkar se funciona
                newGuess=newGuess.concat(". ");
            else if("-".equals(space)) //checkar se funciona
                newGuess=newGuess.concat("- ");
            else if("!".equals(space)) //checkar se funciona
                newGuess=newGuess.concat("! ");
            else if("?".equals(space)) //checkar se funciona
                newGuess=newGuess.concat("? ");
            else if("'".equals(space)) //checkar se funciona
                newGuess=newGuess.concat("' ");
            else{
                newGuess=newGuess.concat("_ ");
                count++;
            }
            
        }
        lettersToWin=count;
        guess= newGuess;
    }
    
    public Boolean checkInput(String input){
        
        if(input.length()>1)return false;
        else {
            letter= input.toCharArray()[0];
            return checkMove(letter);
        }
    }
    Boolean checkMove(char letter){ //verificar se letra ainda não foi usada em usedLetters
        for(int i=0;i<unusedLetters.length;i++){
            if(unusedLetters[i]==letter){
                unusedLetters[i]=' ';
                usedLetters=usedLetters.concat(" "+letter);
                return true;
            }
        }
        return false ; 
    }
    
    Boolean updateGuess(){
        Boolean check=false;
        int operator;
        String subPrev;
        String subFront;
        for(int i=0;i<sentence.length();i++){
            if(serialSentence.charAt(i)==letter){
                operator=i*2;
                if(operator==0){
                    subPrev=sentence.charAt(i)+" ";
                    subFront=guess.substring(operator+2);
                    guess=subPrev.concat(subFront);
                }
                else{
                    subPrev=guess.substring(0, operator-1);
                    subFront=guess.substring(operator+1);
                    guess=subPrev.concat(" "+sentence.charAt(i));
                    guess=guess.concat(subFront);
                }
                
                lettersToWin--;
                check=true;
            }
        }
        return check;
    }
    
    void changePlayer(){
        if(isLocalTurn) isLocalTurn=false;
        else
            isLocalTurn=true;
    }
    
    public String getUsedLetters(){
        return usedLetters;
    }
    
    public Boolean getTurn(){
        return isLocalTurn;
    }
}
