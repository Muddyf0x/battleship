import java.io.File;

public class LocalHumanPlayer implements PlayerController {
     private static String playerName;

    LocalHumanPlayer() {
        playerName = IO.promptPlayerName();
    }

    @Override
    public void setupBoard(char[] board) {
        File shipFile = IO.askForCustomBoardFile();
        // if no file is provided a random layout is chosen
        if (shipFile != null) {
            BGE.placeShipFromFile(shipFile, board);
        } else {
            BGE.placeShipRandom(board);
        }
    }

    @Override
    public int[] getNextMove(char[] visibleBoard) {
        String input = IO.askForTargetCoordinate(playerName);
        if ("QUIT".equalsIgnoreCase(input)) return null;
        return IO.parseCoordinate(input);
    }

    @Override
    public void notifyShotResult(int x, int y, boolean hit) {
        System.out.println(hit ? "Hit!" : "Miss!");
    }
    @Override
    public String getPlayerName() {
        return playerName;
    }
}
