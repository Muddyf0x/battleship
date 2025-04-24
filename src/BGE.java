import java.util.*;

/*
TODO - Variable size boards
TODO - Add Server for Multiplayer
TODO - Add custom placement for ships, via file/ui
TODO - Add user ability to show stats
TODO - Add different computer enemies
TODO - Automatically mark fields around sunk ships as empty = O
 */

public class BGE {
    final int DEFAULT_BOARD_SIZE = 10;

    static final int BATTLESHIP = 5;
    static final int CRUISER = 4;
    static final int DESTROYER = 3;
    static final int SUBMARINE = 2;
    // uncomment for production
/*
    final static int[] FLEET = {
            BATTLESHIP, CRUISER, CRUISER, DESTROYER, DESTROYER,
            DESTROYER, SUBMARINE, SUBMARINE, SUBMARINE, SUBMARINE
    };
*/
    final static int[] FLEET = { BATTLESHIP };

    int[] board1, board2, emptyBoard;
    List<Ship> ships1 = new ArrayList<>();
    List<Ship> ships2 = new ArrayList<>();
    int numOfPlayers;

    int rounds;
    int hitsPlayer1, hitsPlayer2;
    int missesPlayer1, missesPlayer2;


    public static BGE createGame() {
        BGE game = new BGE();
        return game;
    }

    public void startGame() {
        Random rand = new Random();
        // build board for player1
        placeShipsRandom(ships1, rand);
        applyShipsToBoard(board1, ships1);
        rounds = 0;
    }
    public void startGame(int gameMode) {
        Random rand = new Random();

        switch (gameMode) {
            case 1 -> {singlePlayer(rand);}
            case 2 -> {pvpve(rand);}
            case 3 -> {pve(rand);}
            case 4 -> {onlineMultiPlayer(rand);}
        }
    }

    public boolean shoot(int x, int y) {
        int idx = x + y * DEFAULT_BOARD_SIZE;
        List<Ship> ships;
        int[] board;

        if (numOfPlayers == 1 || rounds % numOfPlayers == 0) {
            // Player 1's turn → shoot at board2
            ships = ships2;
            board = board2;
        } else {
            // Player 2's turn → shoot at board1
            ships = ships1;
            board = board1;
        }

        for (Ship ship : ships) {
            if (ship.isHit(x, y)) {
                board[idx] = 4; // hit
                if (ship.isSunk()) {
                    System.out.println("Ship ID " + ship.id + " has been sunk!");
                } else {
                    System.out.println("Hit on ship " + ship.id + "!");
                }
                return true;
            }
        }

        if (board[idx] == 0) {
            board[idx] = 3; // miss
            System.out.println("Miss.");
        }

        return false;
    }

    public void printFull(int boardNum) {
        switch (boardNum) {
            case 1 -> printBoardFull(this.board1);
            case 2 -> printBoardFull(this.board2);
        }
    }
    public char[] getBoard(int boardNum) {
        switch (boardNum) {
            case 1 -> {
                return printBoard(this.board1);
            }
            case 2 -> {
                return printBoard(this.board2);
            }
        }
        return new char[0];
    }
    public int getDEFAULT_BOARD_SIZE() {
        return this.DEFAULT_BOARD_SIZE;
    }
    public int[] gameStats(int player) {
        int[] stats = new int[1];
        return stats;
    }
    public int isGameWon() {
        if (areAllShipsSunk(ships2)) {
            cleanBoard();
            return 1;
        } else if (areAllShipsSunk(ships1) && numOfPlayers != 1) {
            cleanBoard();
            return 2;
        } else {
            return 0;
        }
    }
    private boolean areAllShipsSunk(List<Ship> ships) {
        for (Ship ship : ships) {
            if (!ship.isSunk()) return false;
        }
        return true;
    }

    private void printBoardFull(int[] board) {
        for (int y = 0; y < DEFAULT_BOARD_SIZE; y++) {
            for (int x = 0; x < DEFAULT_BOARD_SIZE; x++) {
                System.out.print(board[x + y * DEFAULT_BOARD_SIZE] + " ");
            }
            System.out.println();
        }
    }
    private char[] printBoard(int[] board) {
        char[] charBoard = new char[DEFAULT_BOARD_SIZE * DEFAULT_BOARD_SIZE];

        for (int y = 0; y < DEFAULT_BOARD_SIZE; y++) {
            for (int x = 0; x < DEFAULT_BOARD_SIZE; x++) {
                switch (board[x + y * DEFAULT_BOARD_SIZE]) {
                    case 0, 1, 2 -> charBoard[x + y * DEFAULT_BOARD_SIZE] = '.';
                    case 3 -> charBoard[x + y * DEFAULT_BOARD_SIZE] = 'O';
                    case 4 -> charBoard[x + y * DEFAULT_BOARD_SIZE] = 'X';
                }
            }
        }
        return charBoard;
    }

    private BGE() {
        board1 = new int[DEFAULT_BOARD_SIZE * DEFAULT_BOARD_SIZE];
        board2 = new int[DEFAULT_BOARD_SIZE * DEFAULT_BOARD_SIZE];
        emptyBoard = new int[DEFAULT_BOARD_SIZE * DEFAULT_BOARD_SIZE];
    }

    private void placeShipsRandom(List<Ship> ships, Random rand) {
        for (int i = 0; i < FLEET.length; i++) {
            int len = FLEET[i];
            boolean placed = false;

            while (!placed) {
                int x = rand.nextInt(DEFAULT_BOARD_SIZE);
                int y = rand.nextInt(DEFAULT_BOARD_SIZE);
                boolean horizontal = rand.nextBoolean();
                Ship candidate = new Ship(i, x, y, len, horizontal);

                if (canPlaceShip(ships, candidate)) {
                    ships.add(candidate);
                    placed = true;
                }
            }
        }
    }

    private boolean canPlaceShip(List<Ship> ships, Ship newShip) {
        // Check if the ship fits on the board
        if (newShip.horizontal) {
            if (newShip.startX + newShip.length > DEFAULT_BOARD_SIZE) return false;
        } else {
            if (newShip.startY + newShip.length > DEFAULT_BOARD_SIZE) return false;
        }

        // Check if any buffer zone cell is already occupied
        for (int[] coord : newShip.getBufferZone(DEFAULT_BOARD_SIZE)) {
            int x = coord[0], y = coord[1];

            for (Ship other : ships) {
                for (int[] occ : other.getCoordinates()) {
                    if (occ[0] == x && occ[1] == y) return false;
                }
            }
        }

        return true;
    }

    private void applyShipsToBoard(int[] board, List<Ship> ships) {
        for (Ship ship : ships) {
            for (int[] coord : ship.getCoordinates()) {
                int x = coord[0], y = coord[1];
                board[x + y * DEFAULT_BOARD_SIZE] = 2; // 2 = ship
            }
        }
    }
    private void singlePlayer(Random rand) {
        this.numOfPlayers = 1;
        this.rounds = 0;
        placeShipsRandom(ships2, rand);
        applyShipsToBoard(board2, ships2);
        rounds++;
    }
    private void pvpve(Random rand) {
        this.numOfPlayers = 2;
        this.rounds = 0;

        // Create shared enemy board (board2)
        placeShipsRandom(ships2, rand);
        applyShipsToBoard(board2, ships2);

        System.out.println("PvPvE mode started!");

        int hitsPlayer1 = 0;
        int hitsPlayer2 = 0;

        // Game loop continues until all ships on board2 are sunk
        while (!areAllShipsSunk(ships2)) {
            int currentPlayer = (rounds % 2) + 1;
            System.out.println("\nPlayer " + currentPlayer + "'s turn:");
            IO.printBoard(getBoard(2), DEFAULT_BOARD_SIZE); // Always show board2

            int[] coords = IO.readCoordinate(DEFAULT_BOARD_SIZE);
            boolean hit = shootPvPvE(coords[0], coords[1]); // custom shoot version for PvPvE

            if (hit) {
                if (currentPlayer == 1) hitsPlayer1++;
                else hitsPlayer2++;
            }

            rounds++;
        }

        System.out.println("\nGame Over! All ships have been sunk.");
        System.out.println("Player 1 hits: " + hitsPlayer1);
        System.out.println("Player 2 hits: " + hitsPlayer2);

        if (hitsPlayer1 > hitsPlayer2) {
            System.out.println("🏆 Player 1 wins!");
        } else if (hitsPlayer2 > hitsPlayer1) {
            System.out.println("🏆 Player 2 wins!");
        } else {
            System.out.println("🤝 It's a draw!");
        }
    }
    private boolean shootPvPvE(int x, int y) {
        int idx = x + y * DEFAULT_BOARD_SIZE;

        for (Ship ship : ships2) {
            if (ship.isHit(x, y)) {
                board2[idx] = 4; // hit
                System.out.println("Hit!");
                return true;
            }
        }

        if (board2[idx] == 0) {
            board2[idx] = 3; // miss
            System.out.println("Miss.");
        }

        return false;
    }
    public void pve(Random rand) {
        this.numOfPlayers = 2;
        this.rounds = 0;

        // Setup player and AI boards
        placeShipsRandom(ships1, rand); // Player's fleet
        applyShipsToBoard(board1, ships1);

        placeShipsRandom(ships2, rand); // AI's fleet
        applyShipsToBoard(board2, ships2);

        System.out.println("PvE mode started!");
        System.out.println("You are Player 1. Try to sink the AI's fleet!");

        Set<Integer> aiShots = new HashSet<>();

        while (true) {
            boolean hit;

            if (rounds % 2 == 0) {
                // Player's turn
                System.out.println("\nYour turn:");
                IO.printBoard(getBoard(2), DEFAULT_BOARD_SIZE);

                int[] coords = IO.readCoordinate(DEFAULT_BOARD_SIZE);
                hit = shoot(coords[0], coords[1]);
            } else {
                // AI's turn
                System.out.println("\nAI's turn:");
                int x, y, idx;

                do {
                    x = rand.nextInt(DEFAULT_BOARD_SIZE);
                    y = rand.nextInt(DEFAULT_BOARD_SIZE);
                    idx = x + y * DEFAULT_BOARD_SIZE;
                } while (aiShots.contains(idx));

                aiShots.add(idx);
                System.out.println("AI shoots at: " + (char) ('A' + x) + (y + 1));
                hit = shoot(x, y);
            }

            // Check for win condition
            if (areAllShipsSunk(ships1)) {
                System.out.println("💀 The AI has sunk your fleet. You lose.");
                break;
            } else if (areAllShipsSunk(ships2)) {
                System.out.println("🎉 You sunk all the AI's ships! You win!");
                break;
            }

            // Only switch turns if it was a miss
            if (!hit) {
                rounds++;
            } else {
                System.out.println("🔥 Bonus shot! You hit a ship.");
            }
        }
    }
    public void onlineMultiPlayer(Random rand) {

    }
    private void cleanBoard() {
        this.board1 = emptyBoard.clone();
        this.board2 = emptyBoard.clone();
        this.hitsPlayer1 = 0;
        this.hitsPlayer2 = 0;
        this.missesPlayer1 = 0;
        this.missesPlayer2 = 0;
        this.rounds = 0;
    }
}