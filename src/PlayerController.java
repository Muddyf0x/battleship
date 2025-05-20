public interface PlayerController {
    int[] getNextMove(char[] visibleBoard, int boardSize);
    void notifyShotResult(int x, int y, boolean hit);
    void setupBoard(char[] board, int boardSize);
    String getPLAYER_NAME();
}