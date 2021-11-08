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


        for (int i = 0; i< 3; i++){

            boolean validInput = false;
            File weightFile = null;

            do{
                System.out.println(
                    String.format("Please enter location of bag number %d to load", i+1) 
                );

                try{
                    String input = reader.readLine();

                    if (input.equals("e") || input.equals("E")) return;

                    weightFile = new File(input);

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

            try {
                bags[i] = new Bag(weightFile, i);
            } catch (Exception e){
                System.out.println(e.getMessage());
                return;
            }
            bags[i+3] = new Bag(i + 3);

        }

        Random rand = new Random();

        File outputDir = new File("/out");
        if (!outputDir.exists()){
            if (!outputDir.mkdir()){
                System.out.println("could not make directory!");
                throw new Exception("could not create dir");
            }
        }

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

    public void run(){
        for (Player p : players){
            p.start();
        }

        boolean threadsAreAlive = true;

        while (threadsAreAlive){
            threadsAreAlive = false;
            for (Player p: players){
                if (p.isAlive()){
                    threadsAreAlive = true;
                }
            }
        }

        System.out.println("congratulations player " + winner.getIndex() + " you won! :)");

        try{
            wait(5000);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    class Player extends Thread {
        Pebble [] hand = new Pebble[10];
        Random rand;
        File outputFile;
        BufferedWriter writer;
        FileWriter fwriter;
        int index;
        Bag previousDrawBag = null;

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

        public void drawHand(Bag bag){
            //a method to draw a full hand from a bag
            for (int i = 0 ; i < 10 ; i++){
                hand[i] = bag.takePebble();
            }
            previousDrawBag = bag;
            //bag here is passed as a pointer
        }

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

        public int getIndex(){
            return index;
        }

        @Override
        public void run(){
            while(winner == null){
                System.out.println(index);
                if(hasWon()) {
                    if (winner == null) { winner = this; }
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

        synchronized private void discardAndDrawPebbles(){
            int freeSpace = rand.nextInt(10);
            discardToBag(freeSpace);
            drawFromBag(freeSpace);
        }

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

    public class Bag {
        
        //in the bag class we will have to make sure that only one player can
        //access the bag at any given time, this is to ensure that players are not
        //operating on stale data

        ArrayList<Pebble> pebbles;
        int [] weights;
        int index;

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

        public Bag(File weightFile, int index) throws Exception{
            //initialize a new arraylist of stones

            this.index = index;

            FileReader fileReader= new FileReader(weightFile);
            BufferedReader reader = new BufferedReader(fileReader);
            
            try{
                String weightString = reader.readLine();

                String [] weightStringArr = weightString.split(",");

                weights = new int[weightStringArr.length];

                for (int i =0; i < weights.length; i++){
                    weights[i] = Integer.parseInt(weightStringArr[i]);
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

            for (int i = 0; i < playerCount * 11; i ++){
                Pebble newPebble = new Pebble(weights[rand.nextInt(weights.length)]);
                pebbles.add(newPebble);
            }


        }

        public Bag(int index){
            pebbles = new ArrayList<Pebble>();
            this.index = index;
        }

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

        public synchronized void addPebble(Pebble pebble){
            pebbles.add(pebble);
        }

        public synchronized void transferPebbles(Bag bag){
            bag.fill(pebbles);
            pebbles = new ArrayList<Pebble>();
        }

        private synchronized void fill(ArrayList<Pebble> pebbles){
            this.pebbles = pebbles;
        }
    }

    class Pebble {
        final int value;

        public Pebble(int value){
            this.value = value;
        }

        public int getValue(){
            return value;
        }

        public boolean equals(Pebble p) {
            if (this.value == p.getValue()) {return true;}
            return false;
        }

    }
}