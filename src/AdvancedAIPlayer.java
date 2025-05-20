import java.util.*;

public class AdvancedAIPlayer implements PlayerController {
    private final Random rand = new Random();
    private final Set<Integer> tried = new HashSet<>();
    private final Deque<int[]> targets = new ArrayDeque<>();
    private final String playerName;
    private final int boardSize = BGE.getBoardSize();

    public AdvancedAIPlayer() {
        String[] robotNames = {
                "Valkyrie", "Sentinel", "NeuroCore", "Ares", "Sigma-9", "HunterBot", "NX-88", "Spectre", "Cerberus", "DroneX"
        };
        this.playerName = robotNames[rand.nextInt(robotNames.length)];
    }

    @Override
    public void setupBoard(char[] board) {
        BGE.placeShipRandom(board);
    }

    @Override
    public int[] getNextMove(char[] visibleBoard) {
        IO.printEnemyBanner(this.getPlayerName());

        // --- Advanced Logic Begins Here ---
        while (!targets.isEmpty()) {
            int[] next = targets.poll();
            int x = next[0];
            int y = next[1];
            int idx = y * boardSize + x;
            if (!tried.contains(idx) && isInBounds(x, y)) {
                tried.add(idx);
                return new int[]{x, y};
            }
        }

        // Hunt mode (fallback to random)
        while (true) {
            int x = rand.nextInt(boardSize);
            int y = rand.nextInt(boardSize);
            int idx = y * boardSize + x;
            if (!tried.contains(idx)) {
                tried.add(idx);
                return new int[]{x, y};
            }
        }
    }

    @Override
    public void notifyShotResult(int x, int y, boolean hit) {
        IO.printTargetLocation(x, y);
        System.out.println(hit ? "Hit!" : "Miss!");

        if (hit) {
            // Add neighbors to target list
            targets.add(new int[]{x + 1, y});
            targets.add(new int[]{x - 1, y});
            targets.add(new int[]{x, y + 1});
            targets.add(new int[]{x, y - 1});
        }
    }

    @Override
    public String getPlayerName() {
        return playerName;
    }

    private boolean isInBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < boardSize && y < boardSize;
    }
}