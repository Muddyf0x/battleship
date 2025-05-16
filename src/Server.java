import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
class Server {
    private ServerSocket serverSocket;
    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }
    public Socket waitForClient() throws IOException {
        // Wait for an incoming client connection
        Socket clientSocket = serverSocket.accept();
        return clientSocket;
    }
    public void close() {
        try {
            if(serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch(IOException e) {
            // Ignore errors on close
        }
    }
}
