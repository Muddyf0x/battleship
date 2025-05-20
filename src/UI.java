import java.io.File;
import java.io.IOException;

public class UI {
    // Todo - remove unused code
    static final int DEFAULT_BOARD_SIZE = 10;
    static final String CODE_WORD = "letmein";
    static final int DEFAULT_SERVER_PORT = 12345;
    static boolean host;
    public static void main(String[] args) {
        IO.printWelcomeScreen();

        while (true) {
            int gameMode = IO.showGameModeSelection(); // 1 = PvE, 2 = PvP, 3 = Network
            int winner = 0;
            // Start Game engine
            BGE.startGame(DEFAULT_BOARD_SIZE);
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
                    // Todo - add option to start Server
                    host = IO.promptHostOrJoin();
                    if (host) {
                        hostGame();
                    } else
                        playOnlineGame(0, null);
                    continue;
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
                players[i].setupBoard(BGE.boards[i], DEFAULT_BOARD_SIZE);
            }
            boolean hit = true;
            // Game loop
            while (true) {

                int current = BGE.getCurrentPlayer();

                PlayerController currentPlayer = players[current];
                char[] visibleEnemyBoard = BGE.getBoard(hit);

                int[] move = currentPlayer.getNextMove(visibleEnemyBoard, DEFAULT_BOARD_SIZE);
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

                IO.printBoard(BGE.getBoard(hit), DEFAULT_BOARD_SIZE);

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
                IO.printVictoryScreen(players[0].getPLAYER_NAME());
            else
                IO.printDefeatScreen(players[0].getPLAYER_NAME());
        }
    }
    // Todo - match features with single player
    public static void playOnlineGame(int p, String ip) {
        String serverAddress;
        if (ip == null)
            serverAddress = IO.promptServerIP();
        else
            serverAddress = ip;
        int port;
        if (p == 0)
            port = IO.promptPort();
        else
            port = p;

        String name = IO.promptPlayerName();

        try {
            NetworkUtils netUtils = new NetworkUtils(serverAddress, port);
            char[] board = new char[DEFAULT_BOARD_SIZE * DEFAULT_BOARD_SIZE];
            File shipFile = IO.askForCustomBoardFile();
            if (shipFile != null) {
                BGE.placeShipFromFile(shipFile, board);
            } else {
                BGE.placeShipRandom(board);
            }

            netUtils.connectToServer(CODE_WORD, name, board);

            while (netUtils.getWinner() == null) {
                if (netUtils.getTurn()) {
                    String input = IO.askForTargetCoordinate(DEFAULT_BOARD_SIZE, name);
                    if ("QUIT".equalsIgnoreCase(input)) {
                        netUtils.endConnection();
                        return;
                    }
                    int[] target =  IO.parseCoordinate(input, DEFAULT_BOARD_SIZE);
                    netUtils.sendShoot(target[0], target[1]);
                } else {
                    IO.printEnemyBanner(netUtils.getEnemyName());
                }
                netUtils.receiveResult();

                IO.printBoard(netUtils.getBoard(), DEFAULT_BOARD_SIZE);
            }

            if (netUtils.getWinner().equalsIgnoreCase(name))
                IO.printVictoryScreen(name);
            else
                IO.printDefeatScreen(netUtils.getEnemyName());

            netUtils.endConnection(); // Todo - implement end Connection

        } catch (IOException e) {
            System.out.println("Error during connection: " + e);
        }
    }
    public static void hostGame() {
        Server server = new Server();
        Thread serverThread = new Thread(() -> {
            try {
                Server.startServer(System.err);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        serverThread.start();
        System.out.println("Server starting on port: " + Server.getDefaultPort());
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            System.out.println("Oh well, that happend");
        }
        playOnlineGame(Server.getDefaultPort(), "localhost");
        server.stopServer();
    }
}
