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
        int y = Integer.parseInt(numberPart);

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
    public static int showGameModeSelection() {
        System.out.println("""
    ================== GAME MODE SELECTION ==================

    1) Singleplayer    - You vs 1 enemy board
    2) PvPvE           - Two players cooperate vs 1 board
    3) PvE             - You vs AI
    ( 4) Online        - Play against another player online) !TBA! - Try the other mods
    5) Quit            - Exit the game

    =========================================================
    Please enter a number [1-5] to select a mode:
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
                case "4" -> System.out.print("Multiplayer isn't supported yet please come back later");
                default -> System.out.print("Invalid input. Please enter a number between 1 and 5: ");
            }
        }
    }

    private static String getModeName(int mode) {
        return switch (mode) {
            case 1 -> "Singleplayer";
            case 2 -> "PvPvE";
            case 3 -> "PvE";
            case 4 -> "Online Multiplayer";
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
            System.out.printf("%2d ", y);
            for (int x = 0; x < boardSize; x++) {
                System.out.print(board[x + y * boardSize] + " ");
            }
            System.out.println();
        }
    }
}