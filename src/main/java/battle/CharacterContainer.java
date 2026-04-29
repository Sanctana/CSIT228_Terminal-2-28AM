package battle;

import java.util.ArrayList;

public class CharacterContainer {
    ArrayList<Character> characterList = new ArrayList<>();

    public CharacterContainer(main.GamePanel gp) {
        characterList.add(new Officer(gp));
        characterList.add(new DebtCollector(gp));
        characterList.add(new Detective(gp));
        characterList.add(new Intruder(gp));
        characterList.add(new Artist(gp));

    }

    public Character getCharacter(int index) {
        try {
            return characterList.get(index);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Invalid Character!");
            return null;
        }
    }
}
