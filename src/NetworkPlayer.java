import java.io.*;
import java.net.Socket;


public class NetworkPlayer implements PlayerController {
    private final BufferedReader in;
    private final PrintWriter out;
    private static String playerName;

    public NetworkPlayer(Socket socket) throws IOException {
        this.in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void setupBoard(char[] board, int boardSize) {
        // no-op: board exchange already happened in UI
    }


    private void sendBoardLines(char[] board, int boardSize) {
        for (int y = 0; y < boardSize; y++) {
            StringBuilder sb = new StringBuilder(boardSize);
            for (int x = 0; x < boardSize; x++) {
                sb.append(board[y * boardSize + x]);
            }
            out.println(sb);
        }
    }

    @Override
    public int[] getNextMove(char[] visibleBoard, int boardSize) {
        try {
            out.println("YOUR_TURN");
            String input = in.readLine();
            if (input == null || input.equalsIgnoreCase("QUIT")) return null;
            return IO.parseCoordinate(input, boardSize);
        } catch (IOException e) {
            throw new RuntimeException("Lost connection to remote player");
        }
    }

    @Override
    public void notifyShotResult(int x, int y, boolean hit) {
        out.println("RESULT " + x + " " + y + " " + (hit ? "HIT" : "MISS"));
    }
    @Override
    public String getPlayerName() {
        return playerName;
    }
}