import java.util.ArrayList;

public class PebbleGame {

    Bag [] bags;
    Player [] players;

    public void init(){
        //TODO get amount of players as input from the player

        //TODO get the location of the weights for the bags

        //TODO initialise the bags (both white and black)

        //TODO assign the bags their respective weights

        //TODO initialise the players (as threads)

        //TODO set the players starting hands from the bags
    }

    public void run(){
        //TODO run player threads and start the game
    }

    class Player implements Runnable {
        Stone [] hand;

        public Player(){

        }

        public void drawHand(Bag bag){
            //TODO implement a method to draw a full hand from a bag
            //bag here is passed as a pointer
        }

        public void drawStone(){
            //TODO implement drawing one stone
        }

        public void discardStone(){
            //TODO implement discarding a stone from the players hand
            //this should be doable with pointers and the remove() function in ArrayLists
            //if not can be done using indexs in the players hand
        }

        private boolean hasWon(){
            //TODO check if the hand is at the 100 total value to win
            //otherwise continue
            return false;
        }

        @Override
        public void run(){
            //TODO if player hand is equal to 100 intterupt all other player threads
            
            //TODO otherwise discard one random pebble and draw from a black bag
        }
    }

    class Bag {
        
        //in the bag class we will have to make sure that only one player can
        //access the bag at any given time, this is to ensure that players are not
        //operating on stale data

        ArrayList<Stone> stones;

        public Bag(){
            //initialize a new arraylist of stones
            stones = new ArrayList<Stone>();
        }

        public void takeStone(){
            //TODO if bag has stones remaining draw one and return it

            //TODO otherwise draw all stones from corresponding white bag
        }

        public void addStone(Stone stone){
            //TODO add the stone that is passed into the method to the list of stones
            //be careful here because the stone is passed in via pointer so make
        }

        public void transferStones(Bag bag){
            //TODO move all the stones in this bag to a bag passed in through the arguments
        }
    }

    class Stone {
        final int value;

        public Stone(int value){
            this.value = value;
        }

        public int getValue(){
            return value;
        }

    }
}