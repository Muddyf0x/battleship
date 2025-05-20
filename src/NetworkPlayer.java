public class NetworkPlayer implements PlayerController {
    final String PLAYER_NAME;

    NetworkPlayer() {
        this.PLAYER_NAME = "player";
    }

    @Override
    public void setupBoard(char[] board, int boardSize) {
        // no-op: board exchange already happened in UI
    }


    private void sendBoardLines(char[] board, int boardSize) {

    }

    @Override
    public int[] getNextMove(char[] visibleBoard, int boardSize) {
        return new int[]{1, 2};
    }

    @Override
    public void notifyShotResult(int x, int y, boolean hit) {
    }
    @Override
    public String getPLAYER_NAME() {
        return PLAYER_NAME;
    }
}