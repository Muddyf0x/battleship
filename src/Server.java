import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public class Server {
    private final ServerSocket serverSocket;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }
    public Socket waitForClient() throws IOException {
        return serverSocket.accept();
    }
    public void close() throws IOException {
        serverSocket.close();
    }
    // not really the right class but the best match without creating a class just for this function
    public static void exchangeBoards(Socket socket,
                                      char[] myBoard,
                                      char[] theirBoard,
                                      int boardSize,
                                      boolean iAmHost) throws IOException
    {
        BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // Serialize a board into lines of text
        Consumer<char[]> send = board -> {
            for (int y = 0; y < boardSize; y++) {
                StringBuilder sb = new StringBuilder(boardSize);
                for (int x = 0; x < boardSize; x++) {
                    sb.append(board[y * boardSize + x]);
                }
                out.println(sb.toString());
            }
        };

        // Read exactly boardSize lines into board
        Consumer<char[]> recv = board -> {
            try {
                for (int y = 0; y < boardSize; y++) {
                    String row = in.readLine();
                    for (int x = 0; x < boardSize; x++) {
                        board[y * boardSize + x] = row.charAt(x);
                    }
                }
                if (!BGE.isValidBoard(board)) {
                    throw new RuntimeException("Invalid board received over network");
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };

        if (iAmHost) {
            // Host sends first, then receives
            send.accept(myBoard);
            recv.accept(theirBoard);
        } else {
            // Client receives first, then sends
            recv.accept(theirBoard);
            send.accept(myBoard);
        }
    }
}