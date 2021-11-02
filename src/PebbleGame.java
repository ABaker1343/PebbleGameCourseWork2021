public class PebbleGame {

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
        @Override
        public void run(){
            //TODO if player hand is equal to 100 intterupt all other player threads
            
            //TODO otherwise discard one random pebble and draw from a black bag
        }
    }

    class Bag {
        public int drawStone(){
            //TODO if bag has stones remaining draw one and return it

            //TODO otherwise draw all stones from corresponding white bag
            int stone = 0;
            return stone;
        }
    }
}