import java.io.File;

public class LocalHumanPlayer implements PlayerController {
     private static String playerName;

    LocalHumanPlayer() {
        playerName = IO.promptPlayerName();
    }

    @Override
    public void setupBoard(char[] board, int boardSize) {
        File shipFile = IO.askForCustomBoardFile();
        if (shipFile != null) {
            BGE.placeShipFromFile(shipFile, board);
        } else {
            BGE.placeShipRandom(board);
        }
    }

    @Override
    public int[] getNextMove(char[] visibleBoard, int boardSize) {
        String input = IO.askForTargetCoordinate(boardSize, playerName);
        if ("QUIT".equalsIgnoreCase(input)) return null;
        return IO.parseCoordinate(input, boardSize);
    }

    @Override
    public void notifyShotResult(int x, int y, boolean hit) {
        System.out.println(hit ? "Hit!" : "Miss!");
    }
    @Override
    public String getPLAYER_NAME() {
        return playerName;
    }
}
