import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class IO {
    private static final Scanner scanner = new Scanner(System.in);

    // Converts inputs like "B7", "7B", "b7", etc. to int[2] = {x, y}
    public static int[] parseCoordinate(String input, int boardSize) throws IllegalArgumentException {
        input = input.trim().toUpperCase();

        Character letter = null;
        String numberPart = "";

        for (char c : input.toCharArray()) {
            if (Character.isLetter(c)) {
                if (letter == null) {
                    letter = c;
                } else {
                    throw new IllegalArgumentException("Too many letters in input.");
                }
            } else if (Character.isDigit(c)) {
                numberPart += c;
            }
        }

        if (letter == null || numberPart.isEmpty()) {
            throw new IllegalArgumentException("Invalid input format. Use formats like B7 or 7B.");
        }

        int x = letter - 'A';
        int y = Integer.parseInt(numberPart) - 1;

        if (x < 0 || x >= boardSize || y < 0 || y >= boardSize) {
            throw new IllegalArgumentException("Coordinates out of board range.");
        }

        return new int[]{x, y};
    }

    // Optionally, a helper method to read and parse input interactively
    public static int[] readCoordinate(int boardSize) {
        while (true) {
            System.out.print("Enter coordinate (e.g., B7 or 7B): ");
            String input = scanner.nextLine();

            try {
                return parseCoordinate(input, boardSize);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    public static void printWelcomeScreen() {
        System.out.println("""
       ____        _   _   _           _     _
      |  _ \\      | | | | | |         | |   (_)
      | |_) | __ _| |_| |_| | ___  ___| |__  _ _ __
      |  _ < / _` | __| __| |/ _ \\/ __| '_ \\| | '_ \\
      | |_) | (_| | |_| |_| |  __/\\__ \\ | | | | |_) |
      |____/ \\__,_|\\__|\\__|_|\\___||___/_| |_|_| .__/
                                              | |
                                              |_|
      ---------------------------------------------
                 Welcome to BATTLESHIP!
               Sink all enemy ships to win!
      ---------------------------------------------
    """);
    }
    public static String promptPlayerName() {
        System.out.println("""
    ================== PLAYER SETUP ==================

    Please enter your player name. This will be used
    to identify you during the game.

    ================================================ 
    Enter your name:
    """);

        Scanner scanner = new Scanner(System.in);
        String name;

        while (true) {
            name = scanner.nextLine().trim();
            if (!name.isEmpty()) return name;
            System.out.print("Name cannot be empty. Enter your name: ");
        }
    }
    public static int showGameModeSelection() {
        System.out.println("""
    ================== GAME MODE SELECTION ==================

    1) Singleplayer         - You vs The AI
    2) Local Multiplayer    - Play against an other Player on this PC
    3) Online Multiplayer   - Play against an other Player over the Network 
    4) Quit                 - Exit the game

    =========================================================
    Please enter a number [1-4] to select a mode:
    """);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine().trim();
            switch (input) {
                case "1", "2", "3" -> {
                    int selected = Integer.parseInt(input);
                    System.out.println("You selected: " + getModeName(selected));
                    return selected;
                }
                case "5" -> {
                    System.out.println("Thanks for playing! Goodbye 👋");
                    System.exit(0);
                }
                case "23071912" -> {
                    System.out.println("How'd we get here?");
                    return Integer.parseInt(input);
                }
                default -> System.out.print("Invalid input. Please enter a number between 1 and 5: ");
            }
        }
    }
    public static boolean askRemoteConnection() {
        System.out.println("""
    ================== CONNECTION SETUP ==================

    Do you want to connect to a remote game server?

    1) Yes - Enter IP and connect to remote host
    2) No  - Start or join a local game on this machine

    ======================================================
    Please enter 1 or 2:
    """);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.equals("1")) return true;
            if (input.equals("2")) return false;
            System.out.println("Invalid input. Please enter 1 or 2:");
        }
    }

    public static String getValidServerAddressOrQuit() {
        System.out.println("""
    ================== SERVER ADDRESS INPUT ==================

    Enter a valid IP address or hostname to connect to a remote game.
    Type 'q' or 'quit' to return to the previous menu.

    ==========================================================
    """);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Server address: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("q") || input.equalsIgnoreCase("quit")) {
                return null;
            }

            try {
                InetAddress.getByName(input); // Validates IP or hostname
                return input;
            } catch (UnknownHostException e) {
                System.out.println("Invalid address. Please enter a valid IP/hostname or 'quit':");
            }
        }
    }


    private static String getModeName(int mode) {
        return switch (mode) {
            case 1 -> "Singleplayer";
            case 2 -> "Local PVP";
            case 3 -> "Online PVP";
            default -> "Unknown";
        };
    }
    public static void printBoard(char[] board, int boardSize) {
        // Print top letters
        System.out.print("   ");
        for (int x = 0; x < boardSize; x++) {
            System.out.print((char) ('A' + x) + " ");
        }
        System.out.println();

        for (int y = 0; y < boardSize; y++) {
            // Print left-side number (row index)
            System.out.printf("%2d ", y + 1);
            for (int x = 0; x < boardSize; x++) {
                System.out.print(board[x + y * boardSize] + " ");
            }
            System.out.println();
        }
    }


    public static String askForTargetCoordinate(int boardSize, String name) {
        System.out.println("\n================== " + name.toUpperCase() + "'S TURN ==================\n");
        System.out.println("""
    Enter the coordinate to fire at.
    Format: LetterNumber (e.g., A1, C5)
    Type "quit" to end the game.
    """);

        // Build valid letter range based on board size
        char maxLetter = (char) ('A' + boardSize - 1);
        String regex = "^[A-" + maxLetter + "]([1-9]|" + (boardSize >= 10 ? "10" : "") + ")$";

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Target: ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equalsIgnoreCase("quit")) {
                return "QUIT";
            }

            if (input.matches(regex)) {
                return input;
            } else {
                System.out.println("Invalid input. Please enter a valid coordinate (A1–" + maxLetter + boardSize + ") or type 'quit'.");
            }
        }
    }
    public static void displayBoardsWithStats(int[][] yourBoard, int[][] enemyBoard, int yourHits, int enemyHits) {
        System.out.printf("""
                ================== BATTLEFIELD ==================
                
                Your Board (Hits: %d)         Enemy Board (Hits: %d)
                %n""", yourHits, enemyHits);

        // Column headers
        System.out.print("   ");
        for (int i = 1; i <= 10; i++) {
            System.out.printf("%2d ", i);
        }
        System.out.print("       ");
        for (int i = 1; i <= 10; i++) {
            System.out.printf("%2d ", i);
        }
        System.out.println();

        for (int row = 0; row < 10; row++) {
            char rowLabel = (char) ('A' + row);
            System.out.print(" " + rowLabel + " ");
            for (int col = 0; col < 10; col++) {
                System.out.print(symbolForCell(yourBoard[row][col]) + "  ");
            }

            System.out.print("    " + rowLabel + " ");
            for (int col = 0; col < 10; col++) {
                System.out.print(symbolForCell(enemyBoard[row][col]) + "  ");
            }

            System.out.println();
        }

        System.out.println("==================================================\n");
    }

    private static String symbolForCell(int cell) {
        return switch (cell) {
            case 0 -> "~";  // untouched
            case 1 -> "o";  // miss
            case 2 -> "X";  // hit
            default -> "?";
        };
    }
    public static File askForCustomBoardFile() {
        System.out.println("""
    ============ SHIP PLACEMENT ============

    Do you want to use a custom ship layout?
    Enter the full path to the layout file.
    Type "no" to place ships randomly.

    ========================================
    """);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("File path or 'no': ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("no")) {
                return null;
            }

            File file = new File(input);
            if (file.exists() && file.isFile()) {
                return file;
            } else {
                System.out.println("Invalid file path. Please enter a valid file or 'no'.");
            }
        }
    }
    public static void printAIBanner(String name) {
        System.out.println("""
    ============================================
                ENEMY TURN (""" + name.toUpperCase() + ")"
                + """ 
    \n============================================
    """);
    }
    public static void printVictoryScreen(String name) {
        System.out.println("""
      ---------------------------------------------
               You sank all enemy ships!
    """);
        System.out.println("              *** " + name.toUpperCase() + " WINS! ***");
        System.out.println("""
      ---------------------------------------------
    """);
    }

    public static void printDefeatScreen(String name) {
        System.out.println("""
      ---------------------------------------------
            All your ships have been sunk!
    """);
        System.out.println("             *** " + name.toUpperCase() + " LOST! ***");
        System.out.println("""
      ---------------------------------------------
    """);
    }

    public static String promptServerIP() {
        System.out.print("Enter host IP address: ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().trim();
    }

    public static int promptPort() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter port number (1024–65535): ");
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                int port = Integer.parseInt(input);
                if (port >= 1024 && port <= 65535) {
                    return port;
                }
            } catch (NumberFormatException ignored) {}
            System.out.print("Invalid port. Enter a number between 1024 and 65535: ");
        }
    }

    public static String promptHostOrJoin() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Type 'host' to host a game or 'join' to connect: ");
        while (true) {
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("host")) {
                return "HOST";
            }
            if (input.equals("join")) {
                return "JOIN";
            }
            System.out.print("Invalid choice. Type 'host' or 'join': ");
        }
    }
}