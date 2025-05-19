import java.io.IOException;
import java.net.Socket;

public class UI {
    static boolean host;
    public static void main(String[] args) {
        IO.printWelcomeScreen();

        while (true) {
            int gameMode = IO.showGameModeSelection(); // 1 = PvE, 2 = PvP, 3 = Network
            int boardSize = 10;
            int winner = 0;

            BGE.startGame(boardSize);
            PlayerController[] players = new PlayerController[2];

            // Select player types based on gameMode
            switch (gameMode) {
                case 1 -> { // PvE
                    players[0] = new LocalHumanPlayer();
                    players[1] = new SimpleAIPlayer();
                }
                case 2 -> { // Local PvP
                    players[0] = new LocalHumanPlayer();
                    players[1] = new LocalHumanPlayer();
                }
                case 3 -> { // Network

                }
                case 23071912 -> {
                    players[0] = new SimpleAIPlayer();
                    players[1] = new SimpleAIPlayer();
                }
                default -> {
                    System.out.println("Invalid mode. Returning to menu.");
                    continue;
                }
            }

            // Setup boards
            for (int i = 0; i < 2; i++) {
                players[i].setupBoard(BGE.boards[i], boardSize);
            }
            boolean hit = true;
            // Game loop
            while (true) {

                int current = BGE.getCurrentPlayer();

                PlayerController currentPlayer = players[current];
                char[] visibleEnemyBoard = BGE.getBoard(hit);

                int[] move = currentPlayer.getNextMove(visibleEnemyBoard, boardSize);
                if (move == null) {
                    System.out.println("Player " + current + " quit.");
                    break;
                }


                switch (BGE.shoot(move[0], move[1])) {
                    case 0 -> hit = false;  // Missed shoot
                    case 1 -> hit = true;   // Hit
                    case 2 -> { // Sunk enemy Ship
                        IO.printShipSunkBanner();
                        hit = true;
                    }
                    case 3 -> { // Already shoot
                        System.out.println("Already Shoot here");
                        hit = true;
                    }
                }
                currentPlayer.notifyShotResult(move[0], move[1], hit);

                IO.printBoard(BGE.getBoard(hit), boardSize);

                int result = BGE.isWon();
                if (result == 1) {
                    winner = 1;
                    break;
                } else if (result == 2) {
                    winner = 2;
                    break;
                }
            }

            // End screen
            if (winner == 1 && BGE.currentPlayer == 0)
                IO.printVictoryScreen(players[0].getPlayerName());
            else
                IO.printDefeatScreen(players[0].getPlayerName());
        }
    }
}
