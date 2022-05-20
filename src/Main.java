/**
 * The main driver of the program. This file will create the game, create the two agents,
 * and create the window for the game. After that, Connect4Frame runs everything.
 */

public class Main {
    public static void main(String[] args) {
        Connect4Game game = new Connect4Game(7, 6); // create the game; these sizes can be altered for larger or smaller games
        Agent redPlayer = new MiniMaxAgent(game, true, 7); // create the red player, any subclass of Agent
//      Agent yellowPlayer = new MiniMaxAgent(game, false, 3);
        Agent yellowPlayer = new MiniAiyazMaxAgent(game, false, 5);// create the yellow player, any subclass of Agent

        Connect4Frame mainframe = new Connect4Frame(game, redPlayer, yellowPlayer); // create the game window

        int me = 0, them = 0, draw = 0;
        int times = 25;

        for (int i = 0; i < times; i++) {
            mainframe.newGameButtonPressed();
            mainframe.playToEndButtonPressed();
            System.out.println((i/(double)times*100+"%"));
            if (game.gameWon() == 'R') {
                me++;
            } else if (game.gameWon() == 'Y') {
                them++;
                System.out.println("LOST");
            } else {
                draw++;
                System.out.println("DRAW");
            }
        }
        System.out.println("Wins: " + me + "\nLosses: " + them + "\nDraws: " + draw
                + "\nOutcome: " + me / (double) times * 100 + "%");


    }
}
