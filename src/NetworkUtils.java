import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.Socket;

class NetworkUtils {
    final String SERVER_ADRESSE;
    final int PORT;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private String EnemyName;

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
        // Todo - send data to server

        // send handshake
        out.writeUTF(code);
        out.writeUTF(name);
        out.writeUTF(new String(board));
        out.flush();

        EnemyName = in.readUTF();
        turn = in.readBoolean();

    }

    public void sendShoot(int y, int x) throws IOException {
        out.writeUTF("SHOOT");   // pro-word
        out.writeInt(x);         // first coordinate
        out.writeInt(y);         // second coordinate
        out.flush();
    }
    public void reciveResult() throws IOException {
        // Todo - fix this
        // 1) framing tag
        String tag = in.readUTF();
        if (!"RESULT".equals(tag)) {
            throw new IOException("Protocol error: expected RESULT but got \"" + tag + "\"");
        }

        // 2) who shoots next
        String playerName = in.readUTF();
        turn = !playerName.equals(getEnemyName()); // set turn

        // 3) board state
        board = in.readUTF().toCharArray();

        // 4) the move (always present)
        String move = in.readUTF();
        shoot = parseCoords(move);

        // 5) hit count
        hit = in.readInt();

        // 6) optional winner
        String winner = null;
        if (in.readBoolean()) {
            winner = in.readUTF();
        }
    }
    public static int[] parseCoords(String s) {
        // e.g. s = "[3, 5]"
        String[] p = s.replaceAll("\\[|\\]|\\s", "").split(",");
        return new int[]{ Integer.parseInt(p[0]), Integer.parseInt(p[1]) };
    }
    public void endConnection() {

    }


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