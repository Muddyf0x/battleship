import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class SimpleAIPlayer implements PlayerController {
    private final Random rand = new Random();
    private final Set<Integer> tried = new HashSet<>();
    private final String playerName;

    public SimpleAIPlayer() {
        String[] robotNames = {
                "AX-13", "Unit-7", "IronCore", "T-900", "Cobalt", "RX-22", "Echo", "Synthron", "MechaZeta", "BetaPrime"
        };
        this.playerName = robotNames[rand.nextInt(robotNames.length)];
    }


    @Override
    public void setupBoard(char[] board, int boardSize) {
        BGE.placeShipRandom(board);
    }

    @Override
    public int[] getNextMove(char[] visibleBoard, int boardSize) {
        IO.printEnemyBanner(this.getPLAYER_NAME());
        while (true) {
            int x = rand.nextInt(boardSize);
            int y = rand.nextInt(boardSize);
            int idx = y * boardSize + x;
            if (!tried.contains(idx)) {
                tried.add(idx);
                return new int[] {x, y};
            }
        }
    }

    @Override
    public void notifyShotResult(int x, int y, boolean hit) {
        System.out.println(hit ? "Hit!" : "Miss!");
        // Could enhance logic
    }
    @Override
    public String getPLAYER_NAME() {
        return playerName;
    }
}
