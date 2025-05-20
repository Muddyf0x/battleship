import java.io.File;
import java.lang.annotation.Target;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class IO {
    private static final Scanner scanner = new Scanner(System.in);
    private static final int BOARD_SIZE = BGE.getBoardSize();

    // Converts inputs like "B7", "7B", "b7", etc. to int[2] = {x, y}
    public static int[] parseCoordinate(String input) throws IllegalArgumentException {
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

        if (x < 0 || x >= BOARD_SIZE || y < 0 || y >= BOARD_SIZE) {
            throw new IllegalArgumentException("Coordinates out of board range.");
        }

        return new int[]{x, y};
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
        System.out.print("""
    ================== PLAYER SETUP ==================

    Please enter your player name. This will be used
    to identify you during the game.

    ================================================
    Enter your name: """);
        String name;

        while (true) {
            name = scanner.nextLine().trim();
            if (!name.isEmpty()) return name;
            System.out.print("Name cannot be empty. Enter your name: ");
        }
    }
    public static int showGameModeSelection() {
        System.out.print("""
    ================== GAME MODE SELECTION ==================

    1) Singleplayer         - You vs The AI
    2) Local Multiplayer    - Play against an other Player on this PC
    3) Online Multiplayer   - Play against an other Player over the Network
    4) Quit                 - Exit the game

    =========================================================
    Please enter a number [1-4] to select a mode: """);

        while (true) {
            String input = scanner.nextLine().trim().toLowerCase();
            switch (input) {
                case "1", "2", "3" -> {
                    int selected = Integer.parseInt(input);
                    System.out.println("You selected: " + getModeName(selected));
                    return selected;
                }
                case "4", "quit" -> {
                    System.out.println("Thanks for playing! Goodbye 👋");
                    System.exit(0);
                }
                case "23071912" -> {
                    System.out.println("How'd we get here?");
                    return Integer.parseInt(input);
                }
                default -> System.out.print("Invalid input. Please enter a number between 1 and 4: ");
            }
        }
    }
    public static int promptDifficulty() {

        System.out.println("================== DIFFICULTY SELECTION ==================\n");
        System.out.println("    1) Easy    - Simple random AI");
        System.out.println("    2) Medium  - Parity + Target AI");
        System.out.println("=========================================================\n");
        System.out.print("Please enter a number [1-2] to select difficulty: ");

        int choice;
        while (true) {
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                if (choice >= 1 && choice <= 2) {
                    break;
                }
            } else {
                scanner.next(); // consume invalid token
            }
            System.out.print("    Invalid input. Please enter [1-2]: ");
        }

        return choice;
    }
    public static boolean askRemoteConnection() {
        System.out.println("""
    ================== CONNECTION SETUP ==================

    Do you want to start a game server on this maschine?

    1) Yes
    2) No  -  join a remote game

    ======================================================
    Please enter 1 or 2:
    """);
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
    public static void printBoard(char[] board) {
        // Print top letters
        System.out.print("   ");
        for (int x = 0; x < BOARD_SIZE; x++) {
            System.out.print((char) ('A' + x) + " ");
        }
        System.out.println();

        for (int y = 0; y < BOARD_SIZE; y++) {
            // Print left-side number (row index)
            System.out.printf("%2d ", y + 1);
            for (int x = 0; x < BOARD_SIZE; x++) {
                System.out.print(board[x + y * BOARD_SIZE] + " ");
            }
            System.out.println();
        }
    }


    public static String askForTargetCoordinate(String name) {
        System.out.println("\n================== " + name.toUpperCase() + "'S TURN ==================\n");
        System.out.println("""
    Enter the coordinate to fire at.
    Format: LetterNumber (e.g., A1, C5)
    Type "quit" to end the game.
    """);

        // Build valid letter range based on board size
        char maxLetter = (char) ('A' + BOARD_SIZE - 1);
        String regex = "^[A-" + maxLetter + "]([1-9]|" + (BOARD_SIZE >= 10 ? "10" : "") + ")$";

        while (true) {
            System.out.print("Target: ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equalsIgnoreCase("quit")) {
                return "QUIT";
            }

            if (input.matches(regex)) {
                return input;
            } else {
                System.out.println("Invalid input. Please enter a valid coordinate (A1–" + maxLetter + BOARD_SIZE + ") or type 'quit'.");
            }
        }
    }

    public static File askForCustomBoardFile() {
        System.out.println("""
    ============ SHIP PLACEMENT ============

    Do you want to use a custom ship layout?
    Enter the full path to the layout file.
    Type "no" to place ships randomly.

    ========================================
    """);

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
    public static void printEnemyBanner(String name) {
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
        System.out.println("             *** " + name.toUpperCase() + " WINS! ***");
        System.out.println("""
      ---------------------------------------------
    """);
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

    public static void printShipSunkBanner() {
        System.out.println("""
       ---------------------------------------------------
                  YOU SUNK AN ENEMY SHIP!
       ---------------------------------------------------
    """);
    }
    public static void printTargetLocation(int[] target) {
        char xChar = (char) ('A' + target[0]);
        int y = target[1];
        System.out.println("Target: " + xChar + y);
    }
    public static void printTargetLocation(int x, int y) {
        char xChar = (char) ('A' + x);
        System.out.println("Target: " + xChar + y);
    }
}