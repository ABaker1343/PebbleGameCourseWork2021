import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Random;

public class PebbleGame {

    volatile Bag [] bags = new Bag[6];
    Player [] players;
    int playerCount;
    volatile Player winner;

    /**
     * method to initialise the Game objects and set up Bags and players
     * @throws Exception
     */
    public void init() throws Exception{
        //get amount of players as input from the player
        System.out.println("Welcome to the pebble game!");
        System.out.println("Please enter the number of players:");
        InputStreamReader inputreader = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(inputreader);

        try{
            playerCount = Integer.parseInt(reader.readLine());
            if ( playerCount < 1) {
                throw new Exception(
                "illeagal number of players: player number must be positive"
                );
            }

            players = new Player[playerCount];
        }
        catch (Exception e){
            return;
        }

        //get as input the file locations of the weights to use for each bag
        //all files used during development are in the res/ folder
        for (int i = 0; i< 3; i++){

            boolean validInput = false;
            File weightFile = null;

            //input bag location
            do{
                System.out.println(
                    String.format("Please enter location of bag number %d to load", i+1) 
                );

                try{
                    String input = reader.readLine();

                    //if e or E then quit
                    if (input.equals("e") || input.equals("E")) return;

                    weightFile = new File(input);

                    //make sure the file exists
                    if (weightFile.getAbsoluteFile().exists()){
                        validInput = true;
                    }
                    else{
                        System.out.println("file does not exist, please check the formatting");
                    }

                } catch (Exception e){
                    System.out.println("invalid file");
                }

            } while (validInput == false);

            //instansiate a bag with the file given by the user
            try {
                bags[i] = new Bag(weightFile, i);
            } catch (Exception e){
                System.out.println(e.getMessage());
                return;
            }

            //create the corresponding empty white bag
            bags[i+3] = new Bag(i + 3);

        }

        Random rand = new Random();


        //create the out directory if it doesnt exist
        File outputDir = new File("/out");
        if (!outputDir.exists()){
            if (!outputDir.mkdir()){
                System.out.println("could not make directory!");
                throw new Exception("could not create dir");
            }
        }

        //for each player in the game create their output file and then construct the player

        for (int i = 0; i < playerCount; i++){
            String dir = System.getProperty("user.dir");
            dir += String.format("\\out\\player%d_output.txt", i);
            File outputFile = new File(dir);
            try{
                outputFile.createNewFile();
                players[i] = new Player(outputFile, i);
                players[i].drawHand(bags[rand.nextInt(3)]);
            } catch (Exception e){
                System.out.println(dir);
                System.out.println(e.getMessage());
                return;
            }
        }

    }


    /**
     * this method will make the game run until a winner is declared
     */
    public void run(){
        for (Player p : players){
            p.start();
        }

        //make sure that all threads are dead before you move on
        //if all threads are dead then all players are finished
        //and a winnder is delcared
        boolean threadsAreAlive = true;

        while (threadsAreAlive){
            threadsAreAlive = false;
            for (Player p: players){
                if (p.isAlive()){
                    threadsAreAlive = true;
                }
            }
        }

        //print the winner and their hand
        System.out.println("congratulations player " + winner.getIndex() + " you won! :)");
        System.out.println(winner.handToString(winner.hand));

    }


    /**
     * class for player extends Thread
     */
    class Player extends Thread {
        Pebble [] hand = new Pebble[10];
        Random rand;
        File outputFile;
        BufferedWriter writer;
        FileWriter fwriter;
        int index;
        Bag previousDrawBag = null;

        /**
         * constructor for player
         * 
         * takes a file to output and an index to tell the player what number they are
         * @param outputFile
         * @param index
         * @throws Exception
         */
        public Player(File outputFile, int index) throws Exception{
            rand = new Random();
            this.outputFile = outputFile;
            try{
                fwriter = new FileWriter(this.outputFile);
                writer = new BufferedWriter(fwriter);
            } catch (Exception e){
                throw e;
            }
            this.index = index;
        }

        /**
         * a method to draw an entire hand from a given bag
         * @param bag
         */
        public void drawHand(Bag bag){
            //a method to draw a full hand from a bag
            for (int i = 0 ; i < 10 ; i++){
                hand[i] = bag.takePebble();
            }
            previousDrawBag = bag;
            //bag here is passed as a pointer
        }

        /**
         * method to check if the player has won
         * 
         * return true if player hand is equal to 100
         * @return
         */
        public boolean hasWon(){
            int total = 0;
            for (Pebble p : hand){
                if (p == null){
                    return false;
                }
                total += p.getValue();
            }
            if (total == 100) { return true; }
            return false;
        }

        /**
         * getter for index
         * @return
         */
        public int getIndex(){
            return index;
        }

        /**
         * run function, this function will be run on the player thread and contains
         * the player logic
         * its an override for the run function in the Thread class
         */
        @Override
        public void run(){
            while(winner == null){
                System.out.println(index);
                if(hasWon()) {
                    synchronized(this) { if (winner == null) { winner = this; } }
                    break;
                }

                discardAndDrawPebbles();
            }

            try{
                writer.close();
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        }

        /**
         * method to wrap discarding and drawing pebbles into one synchronized action
         */
        synchronized private void discardAndDrawPebbles(){
            int freeSpace = rand.nextInt(10);
            discardToBag(freeSpace);
            drawFromBag(freeSpace);
        }

        /**
         * method to draw from a random bag and place the pebble
         * drawn into a given slot in the hand array
         * @param slot
         */
        private void drawFromBag(int slot){
            boolean drawn = false;
            Bag drawnFrom;
            Pebble newPebble;
            do{
                drawnFrom = bags[rand.nextInt(3)];
                newPebble = drawnFrom.takePebble();
                if (newPebble != null){
                    hand[slot] = newPebble;
                    drawn = true;
                }
            } while (drawn == false);

            previousDrawBag = drawnFrom;

            try{
                writer.append(
                    "Player has drawn a " + newPebble.getValue() + " from bag " + drawnFrom.getChar() + "\n" +
                    "Player hand is " + handToString(hand) + "\n"
                );
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        }

        /**
         * method to discard a pebble in a given slot in the hand
         * into the corresponding white bag for the black bag the player drew from
         * @param slot
         */
        private void discardToBag(int slot){
            Pebble pToDiscard = hand[slot];
            Bag discardTo = null;
            //Bag discardTo = bags[rand.nextInt(3) + 3];
            for (int i = 0; i < 3; i++){
                if (bags[i] == previousDrawBag){
                    discardTo = bags[i+3];
                    break;
                }
            }
            
            discardTo.addPebble(pToDiscard);

            hand[slot] = null;

            try{
                writer.append(
                    "Player has discarded a " + pToDiscard.getValue() + " to bag " + discardTo.getChar() + "\n" +
                    "Player hand is " + handToString(hand) + "\n"
                );
            } catch (Exception e){
                System.out.println(e.getMessage());
            }

        }


        /**
         * method that returns a string to represent the hand for a player
         * 
         * take a hand and outputs a string excluding any null values
         * @param hand
         * @return
         */
        private String handToString(Pebble [] hand){
            String toAppend = "[";
            for(Pebble i : hand){
                if (i != null){
                    toAppend += i.getValue() + ",";
                }
            } 
            return toAppend + "]";
        }
    }


    /**
     * class for bag objects
     */
    public class Bag {
        
        //in the bag class we will have to make sure that only one player can
        //access the bag at any given time, this is to ensure that players are not
        //operating on stale data

        ArrayList<Pebble> pebbles;
        int [] weights;
        int index;

        /**
         * constructor for bag
         * @param weights - used to construct the pebble objects
         * @param index - used so that the bag can find its corresponding white bag
         */
        public Bag(int [] weights, int index){
            this.weights = weights;
            this.index = index;

            pebbles = new ArrayList<Pebble>();

            Random rand = new Random();

            for (int i = 0; i < playerCount * 11; i ++){
                Pebble newPebble = new Pebble(this.weights[rand.nextInt(this.weights.length)]);
                pebbles.add(newPebble);
            }

        }

        /**
         * constructor for bag
         * @param weightFile - a file containing weights that will be converted into an array to construct
         *                     pebbles with
         * @param index - used to find the corresponding white bag
         * @throws Exception
         */
        public Bag(File weightFile, int index) throws Exception{
            //initialize a new arraylist of stones

            this.index = index;

            FileReader fileReader= new FileReader(weightFile);
            BufferedReader reader = new BufferedReader(fileReader);
            
            //read the file and get the weights

            try{
                String weightString = reader.readLine();

                String [] weightStringArr = weightString.split(",");

                weights = new int[weightStringArr.length];

                for (int i =0; i < weights.length; i++){
                    weights[i] = Integer.parseInt(weightStringArr[i]); //have to parse because they are strings
                    if (weights[i] < 1){
                        throw new Exception("weights must be strictly positive integers");
                    }
                }
            }
            catch (Exception e){

            }
            finally{
                reader.close();
            }

            pebbles = new ArrayList<Pebble>();

            Random rand = new Random();

            //fill the bag with pebbles

            for (int i = 0; i < playerCount * 11; i ++){
                Pebble newPebble = new Pebble(weights[rand.nextInt(weights.length)]);
                pebbles.add(newPebble);
            }


        }

        /**
         * constructor for an empty bag
         * used to make white bags
         * @param index
         */
        public Bag(int index){
            pebbles = new ArrayList<Pebble>();
            this.index = index;
        }

        /**
         * returns the character representation for a bag
         * @return
         */
        public char getChar(){
            char returnChar;
            switch(index){
                case 0:
                    returnChar = 'X';
                    break;
                case 1:
                    returnChar = 'Y';
                    break;
                case 2:
                    returnChar = 'Z';
                    break;
                case 3:
                    returnChar = 'A';
                    break;
                case 4:
                    returnChar = 'B';
                    break;
                case 5:
                    returnChar = 'C';
                    break;
                default:
                    returnChar = '!';
                    break;
            }
            return returnChar;
        }

        /**
         * method used to take a random pebble from the bag
         * 
         * methods returns the pebble taken and then removes it from the list of pebbles
         * @return
         */
        public synchronized Pebble takePebble(){
            if (pebbles.isEmpty()){
                bags[index + 3].transferPebbles(this);
                return null;
            }
            Random rand = new Random();
            Pebble p = pebbles.get(rand.nextInt(pebbles.size()));
            pebbles.remove(p);
            return p;

        }

        /**
         * method to add a single pebble to the bag
         * @param pebble
         */
        public synchronized void addPebble(Pebble pebble){
            pebbles.add(pebble);
        }

        /**
         * method to transfer contents of a bag to a given bag
         * 
         * used to move all pebbles from a white bag to a black bag
         * @param bag
         */
        public synchronized void transferPebbles(Bag bag){
            bag.fill(pebbles);
            pebbles = new ArrayList<Pebble>();
        }

        /**
         * method to fill a bag with all the pebbles given in an arrayList
         * @param pebbles
         */
        private synchronized void fill(ArrayList<Pebble> pebbles){
            this.pebbles.addAll(pebbles);
        }
    }


    /**
     * class for pebble
     */
    class Pebble {
        final int value;

        /**
         * constructor sets the weight of a pebble
         * @param value
         */
        public Pebble(int value){
            this.value = value;
        }

        public int getValue(){
            return value;
        }

        /**
         * evaluates that the pebbles are of equal weight
         * @param p
         * @return
         */
        public boolean equals(Pebble p) {
            if (this.value == p.getValue()) {return true;}
            return false;
        }

    }
}