public interface PlayerController {
    int[] getNextMove(char[] visibleBoard);
    void notifyShotResult(int x, int y, boolean hit);
    void setupBoard(char[] board);
    String getPlayerName();
}