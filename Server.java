import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class Server 
{
    ServerSocket serverSocket;

    public static void main(String[] args) throws IOException
    { 
        ServerSocket serverSocket = new ServerSocket(1818);
        Server server = new Server(serverSocket);
        server.Start();
    }

    public Server(ServerSocket serverSocket)
    {
        this.serverSocket = serverSocket;
    }

    public void Start()
    {
        try
        {
            while(!serverSocket.isClosed())
            {
                Socket socket = serverSocket.accept();
                System.out.println("New user connected!");
                
                PlayerHandler playerHandler = new PlayerHandler(socket);
                Thread thread = new Thread(playerHandler);
                thread.start();
            }

        }catch(IOException e)
        {
            closeServer(serverSocket);
        }
    }

    public void closeServer(ServerSocket serverSocket)
    {
        try
        {
            if(serverSocket != null)
            {
                serverSocket.close();
            }
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}