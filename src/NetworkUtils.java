import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class NetworkUtils {

    final String SERVER_ADRESSE;
    final int PORT;

    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    private String EnemyName;   // Name of the opposing player

    // variables to accessed by the Ui
    private boolean turn;
    private int hit;
    private String winner;
    private char[] board;
    private int[] shoot;

    NetworkUtils(String serverAdresse, int port) throws IOException {
        this.SERVER_ADRESSE = serverAdresse;
        this.PORT = port;
        this.winner = null;

        socket = new Socket(SERVER_ADRESSE, PORT);
        in  = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }
    public void connectToServer(String code, String name, char[] board) throws IOException {
        out.writeUTF(code);                 // send handshake
        out.writeUTF(name);                 // send Username
        out.writeUTF(new String(board));    // send the board to the Server
        out.flush();

        EnemyName = in.readUTF();           // get enemyName
        turn = in.readBoolean();            // and who starts
    }

    public void sendShoot(int x, int y) throws IOException {
        out.writeUTF("SHOOT");   // pro-word
        out.writeInt(x);            // first coordinate
        out.writeInt(y);            // second coordinate
        out.flush();
    }

    public void receiveResult() throws IOException {
        // 1) framing tag
        String tag = in.readUTF();
        if (!"RESULT".equals(tag)) {
            throw new IOException("Protocol error: expected RESULT but got \"" + tag + "\"");
        }

        // 2) who shoots next
        String playerName = in.readUTF();
        turn = !playerName.equals(getEnemyName()); // set turn

        // 3) State of board that just got shoot at
        board = in.readUTF().toCharArray();

        // 4) The last shoot
        String move = in.readUTF();
        shoot = parseCoords(move); // converted to In[] -> {x, y}

        // 5) hit count numbers according to the BGE
        hit = in.readInt();

        // 6) optional winner
        if (in.readBoolean()) {     // Boolean indicates if there is a winner
            winner = in.readUTF();  // Read name of winner
        } else
            winner = null;
    }

    /**
     * Helper function for receiving the result
     * @param s String input from the Server
     * @return The int[] with {x, y}
     */
    public static int[] parseCoords(String s) {
        // e.g. s = "[3, 5]"
        String[] p = s.replaceAll("\\[|\\]|\\s", "").split(",");
        return new int[]{ Integer.parseInt(p[0]), Integer.parseInt(p[1]) };
    }
    // gracefully end the connection to the server
    public void endConnection() {
        // Close output stream
        try {
            out.close();
        } catch (IOException ignored) {}

        // Close input stream
        try {
            in.close();
        } catch (IOException ignored) {}

        // Close socket
        try {
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ignored) {}
    }
    // expose the data from the server to the UI
    public String getEnemyName() {
        return this.EnemyName;
    }
    public boolean getTurn() {
        return this.turn;
    }
    public String getWinner() {
        return this.winner;
    }
    public char[] getBoard() {
        return this.board;
    }
    public int getHit() {
        return this.hit;
    }
    public int[] getShoot() {
        return this.shoot;
    }
}