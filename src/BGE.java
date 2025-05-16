import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BGE {
    private static final int[] SHIP_SIZE = {5, 4, 3, 3, 2};
    static final char WATER = '~';
    static final char SHIP = 'S';
    static final char HIT = 'X';
    static final char MISS = 'O';


    static int boardSize = 10;
    static char[][] boards = new char[2][];
    static int currentPlayer;
    static boolean boardSet;
    static boolean gameReady;

    public static void startGame() {
        startGame(10);
    }

    public static void startGame(int size) {
        boardSet = false;
        gameReady = false;
        boardSize = size;
        boards[0] = new char[boardSize * boardSize];
        boards[1] = new char[boardSize * boardSize];

        // Initialize both boards to water
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < boardSize * boardSize; j++) {
                boards[i][j] = WATER;
            }
        }
        currentPlayer = 0;
    }
    public static boolean placeShip(File file) {
        if (boardSet)
            gameReady = true;
        if (gameReady)
            return true;
        if (file == null)
            placeShipRandom(boards[currentPlayer]);
        else
            placeShipFromFile(file, boards[currentPlayer]);
        boardSet = true;

        nextPlayer();
        return false;
    }

    public static void placeShipFromFile(File file, char[] board) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            int index = 0;
            String line;

            // Fill board with input from file
            while ((line = reader.readLine()) != null && index < board.length) {
                line = line.trim();
                for (int i = 0; i < Math.min(line.length(), boardSize); i++) {
                    char c = line.charAt(i);
                    board[index++] = (c == SHIP) ? SHIP : WATER;
                }
                // Fill remaining cells in line with water if line too short
                while (index % boardSize != 0 && index < board.length) {
                    board[index++] = WATER;
                }
            }

            // Fill remaining cells in board with water if file too short
            while (index < board.length) {
                board[index++] = WATER;
            }

            // Validate ship layout after parsing
            if (!isValidBoard(board)) {
                throw new IllegalArgumentException("Invalid ship layout in file: violates placement rules or incorrect ship count.");
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to read ship placement file: " + e.getMessage());
        }
    }
    // Verify Board - Start
    public static boolean isValidBoard(char[] board) {
        // Tracks visited ship cells
        boolean[][] visited = new boolean[boardSize][boardSize];
        List<Integer> shipLengths = new ArrayList<>();

        // Scan entire board
        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                if (board[y * boardSize + x] == SHIP && !visited[y][x]) {
                    // Found unvisited ship part, trace its full length
                    int length = detectShip(board, visited, x, y);
                    if (length == -1) return false; // Invalid ship or touching error
                    shipLengths.add(length);
                }
            }
        }

        // Count ships by size
        int[] foundCounts = new int[6]; // max ship size = 5
        for (int len : shipLengths) {
            if (len < 1 || len > 5) return false;
            foundCounts[len]++;
        }

        // Expected ship size distribution
        int[] expected = new int[6];
        for (int s : SHIP_SIZE) expected[s]++;

        // Must match exactly
        return Arrays.equals(foundCounts, expected);
    }

    private static int detectShip(char[] board, boolean[][] visited, int x, int y) {
        int dx = 0, dy = 0;

        // Determine orientation: horizontal or vertical
        if (x + 1 < boardSize && board[y * boardSize + (x + 1)] == SHIP) dx = 1;
        else if (y + 1 < boardSize && board[(y + 1) * boardSize + x] == SHIP) dy = 1;

        int length = 0;
        int cx = x, cy = y;

        // Traverse the ship
        while (cx < boardSize && cy < boardSize && board[cy * boardSize + cx] == SHIP) {
            if (visited[cy][cx]) return -1; // already visited → overlapping or loop
            visited[cy][cx] = true;

            // Check for diagonal or adjacent touching ships
            for (int ny = -1; ny <= 1; ny++) {
                for (int nx = -1; nx <= 1; nx++) {
                    int tx = cx + nx, ty = cy + ny;
                    if (tx >= 0 && ty >= 0 && tx < boardSize && ty < boardSize && !visited[ty][tx]) {
                        // Allow only forward/backward direction, forbid diagonal or side contact
                        if ((nx != 0 || ny != 0) && board[ty * boardSize + tx] == SHIP &&
                                (nx != dx || ny != dy)) {
                            return -1; // touching or malformed ship
                        }
                    }
                }
            }

            cx += dx;
            cy += dy;
            length++;
        }

        return length;
    }
    // Verify Board end

    // Place Ships Randomly - Start
    public static void placeShipRandom(char[] board) {
        Random rand = new Random();

        // Fill board with water to reset
        Arrays.fill(board, WATER);

        // Place each ship from the SHIP_SIZE array
        for (int shipSize : SHIP_SIZE) {
            boolean placed = false;

            // Try random positions until valid placement is found
            while (!placed) {
                int x = rand.nextInt(boardSize);
                int y = rand.nextInt(boardSize);
                boolean horizontal = rand.nextBoolean();

                if (canPlaceShip(board, x, y, shipSize, horizontal)) {
                    placeShip(board, x, y, shipSize, horizontal);
                    placed = true;
                }
            }
        }
    }

    /**
     * Checks whether a ship of given length can be placed at (x, y)
     * in the given direction, without overlap or touching other ships.
     */
    private static boolean canPlaceShip(char[] board, int x, int y, int length, boolean horizontal) {
        int dx = horizontal ? 1 : 0;
        int dy = horizontal ? 0 : 1;

        // Ensure ship fits within board bounds
        int endX = x + dx * (length - 1);
        int endY = y + dy * (length - 1);
        if (endX >= boardSize || endY >= boardSize) return false;

        // Check all ship cells and surrounding cells for collisions
        for (int i = 0; i < length; i++) {
            int cx = x + dx * i;
            int cy = y + dy * i;

            for (int ny = -1; ny <= 1; ny++) {
                for (int nx = -1; nx <= 1; nx++) {
                    int tx = cx + nx;
                    int ty = cy + ny;
                    if (tx >= 0 && ty >= 0 && tx < boardSize && ty < boardSize) {
                        if (board[ty * boardSize + tx] == SHIP) return false; // Touching another ship
                    }
                }
            }
        }

        return true;
    }

    /**
     * Places a ship on the board by setting SHIP characters at calculated positions.
     */
    private static void placeShip(char[] board, int x, int y, int length, boolean horizontal) {
        int dx = horizontal ? 1 : 0;
        int dy = horizontal ? 0 : 1;

        for (int i = 0; i < length; i++) {
            int cx = x + dx * i;
            int cy = y + dy * i;
            board[cy * boardSize + cx] = SHIP;
        }
    }
    // Place ships Random - End

    public static boolean shoot(int x, int y) {
        // Determine opponent board
        int opponentId = (currentPlayer + 1) % 2;
        char[] targetBoard = boards[opponentId];

        int index = y * boardSize + x;

        // Out-of-bounds check (optional, assumes UI ensures valid input)
        if (x < 0 || x >= boardSize || y < 0 || y >= boardSize) {
            throw new IllegalArgumentException("Shot out of bounds at (" + x + "," + y + ")");
        }

        // Evaluate tile and apply change
        if (targetBoard[index] == SHIP) {
            targetBoard[index] = HIT;
            return true;
        } else if (targetBoard[index] == WATER) {
            targetBoard[index] = MISS;
            return false;
        }

        // Already shot here (HIT or MISS) – no change
        return false;
    }
    public static char[] getBoard() {
        // Get opponent's board (the one the current player is shooting at)
        int opponentId = (currentPlayer + 1) % 2;
        char[] board = boards[opponentId];

        // Return a masked copy where unhit ships are hidden
        char[] visible = new char[board.length];
        for (int i = 0; i < board.length; i++) {
            char c = board[i];
            if (c == SHIP) {
                visible[i] = WATER; // hide unhit ship
            } else {
                visible[i] = c; // show HIT, MISS, WATER
            }
        }
        return visible;
    }

    public static int isWon() {
        int opponentId = (currentPlayer + 1) % 2;

        // If opponent has no SHIP tiles left, current player wins
        if (!containsShip(boards[opponentId])) return 1;

        // If current player has no SHIP tiles left, they lost
        if (!containsShip(boards[currentPlayer])) return 2;

        // Game still in progress
        return 0;
    }

    /**
     * Returns true if the given board still contains any unhit ship tiles.
     */
    private static boolean containsShip(char[] board) {
        for (char c : board) {
            if (c == SHIP) return true;
        }
        return false;
    }
    public static void nextPlayer() {
        currentPlayer = (currentPlayer + 1) % 2;
    }

    // Tests
    private static boolean testFileToBoard() {
        char[] testBoard = new char[boardSize * boardSize];
        placeShipFromFile(new File("/home/muddy/IdeaProjects/battleship/src/testfile"), testBoard);
        char[] referenceBoard = {
                '~', '~', '~', '~', '~', 'S', '~', '~', '~', '~',
                '~', '~', '~', 'S', '~', 'S', '~', '~', '~', '~',
                '~', '~', '~', 'S', '~', 'S', '~', '~', '~', '~',
                '~', '~', '~', 'S', '~', '~', '~', '~', '~', '~',
                '~', '~', '~', '~', '~', '~', '~', '~', '~', '~',
                '~', '~', 'S', '~', '~', '~', 'S', 'S', '~', '~',
                '~', '~', 'S', '~', '~', '~', '~', '~', '~', '~',
                '~', '~', 'S', '~', '~', '~', '~', '~', '~', '~',
                '~', '~', 'S', '~', '~', 'S', 'S', 'S', 'S', 'S',
                '~', '~', '~', '~', '~', '~', '~', '~', '~', '~'
        };
        if (Arrays.equals(testBoard, referenceBoard)) {
            System.out.println("Test passed - testFileToBoard");
            return true;
        } else
            return false;
    }
    private static boolean testRandomToBoard() {
        char[] testBoard = new char[boardSize * boardSize];
        placeShipRandom(testBoard);
        if (isValidBoard(testBoard)) {
            System.out.println("Test passed - testRandomToBoard");
            return true;
        } else
            return false;
    }
    private static boolean testShoot() {
        boolean test = true;

        currentPlayer = 1;

        char[] referenceBoard = {
                '~', '~', '~', '~', '~', 'S', '~', '~', '~', '~',
                '~', '~', '~', 'S', '~', 'S', '~', '~', '~', '~',
                '~', '~', '~', 'S', '~', 'S', '~', '~', '~', '~',
                '~', '~', '~', 'S', '~', '~', '~', '~', '~', '~',
                '~', '~', '~', '~', '~', '~', '~', '~', '~', '~',
                '~', '~', 'S', '~', '~', '~', 'S', 'S', '~', '~',
                '~', '~', 'S', '~', '~', '~', '~', '~', '~', '~',
                '~', '~', 'S', '~', '~', '~', '~', '~', '~', '~',
                '~', '~', 'S', '~', '~', 'S', 'S', 'S', 'S', 'S',
                '~', '~', '~', '~', '~', '~', '~', '~', '~', '~'
        };
        boards[0] = referenceBoard;
        if (shoot(0, 0))
            test = false;
        if (!shoot(5, 0))
            test = false;
        if (test) {
            System.out.println("Test passed - testShoot");
            return true;
        } else
            return false;
    }
    private static boolean testIsWon() {
        boolean test = true;
        char[] referenceBoard0 = {
                '~', '~', '~', '~', '~', 'S', '~', '~', '~', '~',
                '~', '~', '~', 'S', '~', 'S', '~', '~', '~', '~',
                '~', '~', '~', 'S', '~', 'S', '~', '~', '~', '~',
                '~', '~', '~', 'S', '~', '~', '~', '~', '~', '~',
                '~', '~', '~', '~', '~', '~', '~', '~', '~', '~',
                '~', '~', 'S', '~', '~', '~', 'S', 'S', '~', '~',
                '~', '~', 'S', '~', '~', '~', '~', '~', '~', '~',
                '~', '~', 'S', '~', '~', '~', '~', '~', '~', '~',
                '~', '~', 'S', '~', '~', 'S', 'S', 'S', 'S', 'S',
                '~', '~', '~', '~', '~', '~', '~', '~', '~', '~'
        };
        boards[0] = referenceBoard0;
        char[] referenceBoard1 = {
                '~', '~', '~', '~', '~', 'X', '~', '~', '~', '~',
                '~', '~', '~', 'X', '~', 'X', '~', '~', '~', '~',
                '~', '~', '~', 'X', '~', 'X', '~', '~', '~', '~',
                '~', '~', '~', 'X', '~', '~', '~', '~', '~', '~',
                '~', '~', '~', '~', '~', '~', '~', '~', '~', '~',
                '~', '~', 'X', '~', '~', '~', 'X', 'X', '~', '~',
                '~', '~', 'X', '~', '~', '~', '~', '~', '~', '~',
                '~', '~', 'X', '~', '~', '~', '~', '~', '~', '~',
                '~', '~', 'X', '~', '~', 'X', 'X', 'X', 'X', 'X',
                '~', '~', '~', '~', '~', '~', '~', '~', '~', '~'
        };
        boards[1] = referenceBoard1;
        currentPlayer = 0;
        if (isWon() != 1)
            test = false;
        currentPlayer = 1;
        if (isWon() != 2)
            test = false;

        if (test) {
            System.out.println("Test passed - testIsWon");
            return true;
        } else
            return false;
    }
    private static boolean testGetBoard() {
        char[] referenceBoard = {
                'O', '~', '~', '~', '~', '~', '~', '~', '~', '~',
                '~', '~', '~', '~', '~', '~', '~', '~', '~', '~',
                '~', '~', '~', 'X', '~', '~', '~', '~', '~', '~',
                '~', '~', '~', 'X', '~', '~', '~', '~', '~', '~',
                '~', '~', '~', '~', '~', '~', '~', '~', '~', '~',
                '~', '~', '~', '~', '~', '~', '~', '~', '~', '~',
                '~', '~', '~', '~', '~', '~', '~', '~', '~', '~',
                '~', '~', '~', '~', '~', '~', '~', '~', '~', '~',
                '~', '~', '~', '~', '~', '~', 'X', '~', 'X', '~',
                '~', '~', '~', '~', '~', '~', '~', '~', '~', 'O'
        };
        char[] referenceBoard0 = {
                'O', '~', '~', '~', '~', 'S', '~', '~', '~', '~',
                '~', '~', '~', 'S', '~', 'S', '~', '~', '~', '~',
                '~', '~', '~', 'X', '~', 'S', '~', '~', '~', '~',
                '~', '~', '~', 'X', '~', '~', '~', '~', '~', '~',
                '~', '~', '~', '~', '~', '~', '~', '~', '~', '~',
                '~', '~', 'S', '~', '~', '~', 'S', 'S', '~', '~',
                '~', '~', 'S', '~', '~', '~', '~', '~', '~', '~',
                '~', '~', 'S', '~', '~', '~', '~', '~', '~', '~',
                '~', '~', 'S', '~', '~', 'S', 'X', 'S', 'X', 'S',
                '~', '~', '~', '~', '~', '~', '~', '~', '~', 'O'
        };
        boards[0] = referenceBoard0;
        currentPlayer = 1;

        if (Arrays.equals(getBoard(), referenceBoard)) {
            System.out.println("Test passed - testGetBoard");
            return true;
        } else
            return false;

    }
    // Run Tests
    public static void main(String[] args) {
        int failedTests = 0;
        if (!testFileToBoard())
            failedTests++;
        if (!testRandomToBoard())
            failedTests++;
        if (!testShoot())
            failedTests++;
        if (!testIsWon())
            failedTests++;
        if (!testGetBoard())
            failedTests++;

        if (failedTests == 0)
            System.out.println("All Tests passed successfully");
        else
            System.out.println(failedTests + " Test failed");
    }
}