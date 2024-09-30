import java.util.*;
import java.io.*;

public class BlackJackSimulator {
    static StringTokenizer st;
    static BufferedReader br;
    static PrintWriter out;

    static String next() throws IOException {
        while (st == null || !st.hasMoreTokens())
            st = new StringTokenizer(br.readLine().trim());
        return st.nextToken();
    }
    static int nextInt() throws IOException {
        return Integer.parseInt(next());
    }
    static class Card{
        int s;
        int v;
        public Card(int su, int va){
            s=su;
            v=va;
        }
        int getSuit(){
            return s;
        }
        int getValue(){
            return v;
        }
    }
    static ArrayList<Card> deck = new ArrayList<>();
    static ArrayList<Card> discardPile;
    static String[] stringValue = {"ace", "two","three","four","five","six","seven","eight","nine","ten","jack","queen","king"};
    static String[] stringSuits = {"diamonds", "hearts", "clubs", "spades"};

    static String[] intValue = {"A","2","3","4","5","6","7","8","9","10","J","Q","K"};
    static String[] intSuits = {"♦", "♥", "♣", "♠"};

    static boolean playing = false;
    static ArrayList<Card> dealerCards = new ArrayList<>();
    static ArrayList<Card> yourCards = new ArrayList<>();
    static int yourSum = 0;
    static int dealerSum = 0;
    static int yourAces = 0;

    public static void main(String[] args) throws IOException {
        br = new BufferedReader(new InputStreamReader(System.in));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
        System.out.println("Welcome to blackjack :).");
        System.out.println("Draw a card: \"draw\"");
        System.out.println("Reset a deck: \"reset\"");
        System.out.println("Print discard pile: \"print\"");
        System.out.println("Play: \"play\"");
        System.out.println("Quit: \"/quit\"");

        resetDeck();

        String input = br.readLine();
        while(!input.equals("/quit")) {
            if(playing){
                if(input.equals("hit")){
                    if (deck.size() <= 0) {
                        System.out.println("there are no more cards in the deck.");
                    }
                    else {
                        Card currCard = draw();
                        yourSum += getWorth(currCard);
                        yourCards.add(currCard);
                        printHands(0);

                        if(yourSum>21){
                            while(yourSum>21){
                                yourAces--;
                                yourSum-=10;
                            }
                            if(yourAces<0){
                                System.out.println("You lose... :(");
                                resetGame();
                            }
                        }
                        if(yourSum==21){
                            input="stand";
                        }
                    }
                }
                if(input.equals("stand")){
                    while(dealerSum<17){
                        Card currCard= draw();
                        dealerCards.add(currCard);
                        dealerSum+=getWorth(currCard);
                    }
                    printHands(1);

                    if(dealerSum>21||yourSum>dealerSum){
                        System.out.println("You Win! GG");
                    }
                    else if(yourSum==dealerSum){
                        System.out.println("Push (tie)... :|");
                    }
                    else{
                        System.out.println("You Lose... :(");
                    }
                    resetGame();
                }
                if(!input.equals("stand")&&!input.equals("hit")){
                    System.out.println("That command doesn't exist");
                }
            }
            else {
                if(input.equals("play")){

                    playing=true;
                    dealerCards.add(draw());
                    dealerCards.add(draw());
                    yourCards.add(draw());
                    yourCards.add(draw());
                    dealerSum=getWorth(dealerCards.get(0))+getWorth(dealerCards.get(1));
                    yourSum= getWorth(yourCards.get(0))+getWorth(yourCards.get(1));

                    if(yourCards.get(0).getValue()==0){
                        yourAces++;
                        yourSum+=10;
                    }
                    if(yourCards.get(1).getValue()==0){
                        yourAces++;
                        yourSum+=10;
                    }
                    if(dealerCards.get(0).getValue()==0){ //if first card for dealer is ace, it is 11. Otherwise, it is 1
                        dealerSum+=10;
                    }
                    if(yourSum>21){
                        if(yourAces>0){
                            yourAces--;
                            yourSum-=10;
                        }
                        else{
                            System.out.println("You lose... :(");
                            resetGame();
                        }
                    }

                    System.out.println("Good Luck!.");
                    printHands(0);

                    System.out.println("Hit: \"hit\"");
                    System.out.println("Stand: \"stand\"");
                    System.out.println("Quit: \"/quit\"");
                }
                else if (input.equals("reset")) {
                    resetDeck();
                    System.out.println("Deck reset.");
                } else if (input.equals("draw")) {
                    if (deck.size() <= 0) {
                        System.out.println("there are no more cards in the deck: ");
                    } else {
                        Card curr = draw();
                        int value = curr.getValue();
                        int suit = curr.getSuit();
                        System.out.println("Your Card was the " + stringValue[value] + " of " + stringSuits[suit] + " " + intValue[value] + intSuits[suit]);
                    }
                } else if (input.equals("print")) {
                    for (Card a : discardPile) {
                        System.out.print(intValue[a.getValue()] + intSuits[a.getSuit()] + " ");
                    }
                    System.out.println();
                }
                else {
                    System.out.println("That command doesn't exist");
                }
            }
            input = br.readLine();
        }
        out.close();
    }
    static void resetGame(){
        playing = false;
        dealerCards = new ArrayList<>();
        yourCards = new ArrayList<>();
        yourSum = 0;
        dealerSum = 0;
        yourAces=0;
        System.out.println();
        System.out.println("Draw a card: \"draw\"");
        System.out.println("Reset a deck: \"reset\"");
        System.out.println("Print discard pile: \"print\"");
        System.out.println("Play: \"play\"");
        System.out.println("Quit: \"/quit\"");
    }
    static void printHands(int a){ //a=0 --> hide dealer 1st card, a=1 --> show all
        System.out.print("Dealer's cards: ");
        printCard(dealerCards.get(0));
        if(a==1){
            printCard(dealerCards.get(1));
        }
        for(int i=2; i<dealerCards.size(); i++){
            printCard(dealerCards.get(i));
        }
        System.out.println();
        System.out.print("Your cards: ");
        for(Card c : yourCards){
            printCard(c);
        }
        System.out.println();
    }
    static void printCard(Card a){
        System.out.print(intValue[a.getValue()] + intSuits[a.getSuit()] + " ");
    }
    static Card draw(){
        int rand = (int)(Math.random()*deck.size());
        Card victimCard = deck.remove(rand);
        discardPile.add(victimCard);
        return victimCard;
    }
    static void resetDeck(){
        discardPile=new ArrayList<>();
        for(int i=0; i<4; i++){
            for(int j=0; j<13; j++){
                deck.add(new Card(i,j));
            }
        }
    }
    static int getWorth(Card c){
        return Math.min(10,c.getValue()+1);
    }
}
