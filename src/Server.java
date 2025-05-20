import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class Server {
    private static final int PORT = 12345;
    private static final String EXPECTED_CODE = "letmein"; // code word to make sure only correct connections can be made
    static final int DEFAULT_BOARD_SIZE = 10;

    private static ServerSocket serverSocket;
    private static volatile boolean running = true;


    public static void main(String[] args) throws IOException, InterruptedException {
       startServer(System.out);
    }

    public static void startServer(PrintStream printout) throws IOException, InterruptedException {
        serverSocket = new ServerSocket(PORT);
        printout.println("Server listening on port " + PORT);
        // We'll hold exactly two handlers:
        while (running) {
            List<ClientHandler> players = Collections.synchronizedList(new ArrayList<>());
            CountDownLatch readyLatch = new CountDownLatch(2);

            // Accept two connections
            for (int i = 0; i < 2; i++) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket, players, readyLatch, printout);
                players.add(handler);
                new Thread(handler, "PlayerHandler-" + i).start();
            }

            // Wait until both have sent code, name & board
            readyLatch.await();

            printout.println("Both players connected. Starting game setup.");

            // Randomly pick who starts
            int starterIdx = new Random().nextInt(2);

            // Notify each player: the other name & whether they start
            for (int i = 0; i < 2; i++) {
                ClientHandler me = players.get(i);
                ClientHandler other = players.get(1 - i);
                boolean iStart = (i == starterIdx);
                me.sendSetup(other.name, iStart);
            }
            // Start game engine
            BGE.startGame(DEFAULT_BOARD_SIZE);
            // set up boards
            for (int i = 0; i < 2; i++) {
                BGE.setBoard(players.get(i).getBoard(), i);
            }
            BGE.setCurrentPlayer(starterIdx);

            // Enter turn loop
            while (running) {
                ClientHandler active = players.get(BGE.getCurrentPlayer());

                int[] move = active.readMove();// blocking
                int hit;

                hit = BGE.shoot(move[0], move[1]);
                String winner = null;
                if (BGE.isWon() == 1) {
                    winner = active.name;
                }

                // broadcast to both
                String nextPlayer = players.get(BGE.getCurrentPlayer()).getName();
                for (ClientHandler p : players) {
                    p.sendMove(nextPlayer, BGE.getBoard(hit != 0), Arrays.toString(move), hit, winner);
                }
            }
        }
    }
    public void stopServer() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static int getDefaultPort() {
        return PORT;
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private final List<ClientHandler> players;
        private final CountDownLatch readyLatch;
        private DataInputStream in;
        private DataOutputStream out;
        private PrintStream printout;

        String name;
        char[] board;

        ClientHandler(Socket socket, List<ClientHandler> players, CountDownLatch readyLatch, PrintStream printout) {
            this.socket = socket;
            this.players = players;
            this.readyLatch = readyLatch;
            this.printout = printout;
        }

        @Override
        public void run() {
            try {
                in  = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());

                // 1) Handshake: code word
                String code = in.readUTF();
                if (!EXPECTED_CODE.equals(code)) {
                    out.writeUTF("ERROR: invalid code");
                    socket.close();
                    return;
                }

                // 2) Read name
                name = in.readUTF();

                // 3) Read board as a serialized String
                String boardString = in.readUTF();
                this.board = boardString.toCharArray();

                printout.println("Player connected: " + name + "  board=" + boardString);
                // signal ready
                readyLatch.countDown();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /** Send the other player's name and whether this client starts. */
        public void sendSetup(String otherName, boolean youGoFirst) throws IOException {
            out.writeUTF(otherName);
            out.writeBoolean(youGoFirst);
            out.flush();
        }

        /** Blocking read of the next move from this client. */
        public int[] readMove() throws IOException {
            String tag = in.readUTF();
            if (!"SHOOT".equals(tag)) {
                throw new IOException(
                        "Protocol error: expected ‘SHOOT’ but got “" + tag + "”"
                );
            }
            int x = in.readInt();    // must match the order you wrote
            int y = in.readInt();
            return new int[]{ x, y };
        }

        /** Broadcast a move: who moved, and the move string. */
        public void sendMove(String playerName, char[] board, String move, int hit, String winner) throws IOException {
            out.writeUTF("RESULT");           // 1) tag
            out.writeUTF(playerName);             // 2) who shoots next
            out.writeUTF(new String(board));      // 3) board state of board that got shoot at
            out.writeUTF(move);                   // 4) the location of the shoot
            out.writeInt(hit);                    // 5) show hit/miss/sunk/already shoot at

            // 6) optional winner
            out.writeBoolean(winner != null);
            if (winner != null) {
                out.writeUTF(winner);
            }
            out.flush();
        }
        public char[] getBoard() {
            return this.board;
        }
        public String getName() {
            return this.name;
        }
    }
}