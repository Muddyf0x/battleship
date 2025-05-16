import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
class NetworkUtils {
    public static void exchangeBoards(Socket socket, char[] myBoard, char[] theirBoard, int boardSize, boolean isHost) throws IOException {
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        DataInputStream in = new DataInputStream(socket.getInputStream());
        if(isHost) {
            // Host sends its board first
            out.writeInt(boardSize);
            for(int i = 0; i < boardSize * boardSize; i++) {
                out.writeChar(myBoard[i]);
            }
            out.flush();
            // Then host receives the client's board
            int oppSize = in.readInt();
            int cells = oppSize * oppSize;
            for(int i = 0; i < cells && i < theirBoard.length; i++) {
                theirBoard[i] = in.readChar();
            }
        } else {
            // Client receives host's board first
            int remoteSize = in.readInt();
            int cells = remoteSize * remoteSize;
            for(int i = 0; i < cells && i < theirBoard.length; i++) {
                theirBoard[i] = in.readChar();
            }
            // Then client sends its board
            out.writeInt(boardSize);
            for(int i = 0; i < boardSize * boardSize; i++) {
                out.writeChar(myBoard[i]);
            }
            out.flush();
        }
    }
}