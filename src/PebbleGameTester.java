import org.junit.Test;

import static org.junit.Assert.*;

class PebbleGameTester {

    //class for testing to keep test code away from rest of the code
    //keeps it more consise

    //we dont want to explicitly test the private methods as they are only used during the running 
    //of public methods and therefore if they are not working as expected then the public methods
    //will not either.
    //this allows us to use a test class that does not extend the class its testing

    PebbleGameTester() {

    }

    //bag tests
    @Test
    public void testBags(){
        /*PebbleGame game = new PebbleGame();
        PebbleGame.Bag bag = game.new Bag(new int [] {1,2,3}, 0);
        assertEquals('X', bag.getChar());
        bag = game.new Bag(new int[] {1,2,3} , 1);
        assertEquals('Y', bag.getChar());
        bag = game.new Bag(new int[] {1,2,3} , 2);
        assertEquals('Z', bag.getChar());*/
    }


}