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
                    try {
                        int port       = IO.promptPort();
                        host   = IO.promptHostOrJoin().equals("HOST");
                        Socket socket;
                        if (host) {
                            Server ns = new Server(port);
                            System.out.println("Waiting for opponent…");
                            socket = ns.waitForClient();
                        } else {
                            String ip = IO.promptServerIP();
                            socket = new Socket(ip, port);
                        }
                        players[0] = new LocalHumanPlayer();
                        players[1] = new NetworkPlayer(socket);
                        /*
                        // 1) Prompt *this* player once for their board:
                        LocalHumanPlayer local = new LocalHumanPlayer();
                        local.setupBoard(BGE.boards[0], boardSize);

                        // 2) Exchange so each side ends up with the other board:
                        NetworkUtils.exchangeBoards(socket,
                                BGE.boards[0],  // my board
                                BGE.boards[1],  // their board
                                boardSize,
                                host);
                        // 3) Set controllers for the game loop:
                        players[0] = local;                       // local moves
                        players[1] = new NetworkPlayer(socket);   // remote moves

                         */
                    } catch (IOException e) {

                    }
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

            // Game loop
            while (true) {
                int current = BGE.currentPlayer;

                PlayerController currentPlayer = players[current];
                char[] visibleEnemyBoard = BGE.getBoard();

                int[] move = currentPlayer.getNextMove(visibleEnemyBoard, boardSize);
                if (move == null) {
                    System.out.println("Player " + current + " quit.");
                    break;
                }

                boolean hit = BGE.shoot(move[0], move[1]);
                currentPlayer.notifyShotResult(move[0], move[1], hit);

                IO.printBoard(BGE.getBoard(), boardSize);

                int result = BGE.isWon();
                if (result == 1) {
                    winner = 1;
                    break;
                } else if (result == 2) {
                    winner = 2;
                    break;
                }

                if (!hit) {
                    BGE.nextPlayer();
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
