import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

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

    @Test
    public void testAddPebble(){
        playerCount = 1;
        bags[0] = new Bag(new int [] {1,2,3,4,5}, 0);
        bags[3] = new Bag(3);
        bags[1] = new Bag(new int [] {1,2,3,4,5}, 0);
        bags[4] = new Bag(3);
        bags[2] = new Bag(new int [] {1,2,3,4,5}, 0);
        bags[5] = new Bag(3);
        
        Pebble p1 = new Pebble(1);

        bags[3].addPebble(p1);

        assertEquals(bags[3].pebbles.get(0), p1);

        assertThrows(IndexOutOfBoundsException.class, () -> bags[3].pebbles.get(1));

        Pebble p2 = new Pebble(2);
        bags[3].addPebble(p2);
        assertEquals(bags[3].pebbles.get(1), p2);
    }

    @Test
    public void testTransferPebbles(){
        playerCount = 1;
        bags[0] = new Bag(new int [] {1,2,3,4,5}, 0);
        bags[3] = new Bag(3);
        bags[1] = new Bag(new int [] {1,2,3,4,5}, 0);
        bags[4] = new Bag(3);
        bags[2] = new Bag(new int [] {1,2,3,4,5}, 0);
        bags[5] = new Bag(3);

        ArrayList<Pebble> bagList = bags[0].pebbles;

        bags[0].transferPebbles(bags[3]);

        assertEquals(bags[3].pebbles, bagList);
        assert bags[0].pebbles.isEmpty();
    }

    @Test
    public void testPebbles(){
        Pebble p1 = new Pebble(1);
        Pebble p2 = new Pebble(1);
        Pebble p3 = new Pebble(2);

        assert p1.equals(p2);
        assertEquals(2, p3.getValue());
    }
}
