import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Arrays;
import java.util.function.Consumer;

public class BagTests extends PebbleGame {
    /**
     * test class for the bag class in PebbleGame
     */

    @Test
    public void testGetChar() {
        char [] expectedChars = new char[] {'X', 'Y', 'Z', 'A', 'B', 'C'};
        int [] indexs = new int [] {0,1,2,3,4,5};
        
        for (int i =0; i < indexs.length; i++){
            Bag bag = new Bag(new int [] {1,2,3,4,5}, i);
            assertEquals(expectedChars[i], bag.getChar());
        }
    }

    @Test
    public void testTakePebble(){
        playerCount = 1;
        bags[0] = new Bag(new int [] {1,2,3,4,5}, 0);
        bags[3] = new Bag(3);
        bags[1] = new Bag(new int [] {1,2,3,4,5}, 0);
        bags[4] = new Bag(3);
        bags[2] = new Bag(new int [] {1,2,3,4,5}, 0);
        bags[5] = new Bag(3);
        
        Pebble [] possiblePebbles = new Pebble[] {
            new Pebble(1),
            new Pebble(2),
            new Pebble(3),
            new Pebble(4),
            new Pebble(5)
        };

        Pebble p;

        for (int i = 0; i < 11; i ++){
            p = bags[0].takePebble();
            assert Arrays.stream(possiblePebbles).anyMatch(p::equals);
        }

        assertEquals(null, bags[0].takePebble());

    }
}
