package battle;
import java.util.*;
public class CharacterContainer {
    ArrayList<Character> characterList = new ArrayList<>();

    public CharacterContainer(){
        characterList.add(new Officer());
        characterList.add(new DebtCollector());
    }
    public Character getCharacter(int index){
        try{
            return characterList.get(index);
        }catch(ArrayIndexOutOfBoundsException e){
            System.out.println("Invalid Character!");
            return null;
        }
    }
}
