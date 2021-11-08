import org.junit.Test;
import java.io.File;
import static org.junit.Assert.assertFalse;

public class PlayerTests extends PebbleGame {
    
    @Test
    public void testHasWon(){
        playerCount = 1;
        bags[0] = new Bag(new int [] {1,2,3,4,5}, 0);
        bags[3] = new Bag(3);
        bags[1] = new Bag(new int [] {1,2,3,4,5}, 0);
        bags[4] = new Bag(3);
        bags[2] = new Bag(new int [] {1,2,3,4,5}, 0);
        bags[5] = new Bag(3);

        Player p = null;
        File outfile = new File("Coursework_player_test_output.txt");
        try{
            outfile.createNewFile();
            p = new Player(outfile, 0);
        }catch (Exception e){
            System.out.println(e.getMessage());
            assert false;
        }

        Bag bag = new Bag (new int [] {9}, 0);

        p.drawHand(bag);

        assertFalse(p.hasWon());

        bag = new Bag (new int [] {11}, 0);
        p.drawHand(bag);

        assertFalse(p.hasWon());

        bag = new Bag (new int[] {10}, 0);
        p.drawHand(bag);

        assert p.hasWon();
    }
}
