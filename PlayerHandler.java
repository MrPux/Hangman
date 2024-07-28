import java.net.Socket;

import java.io.IOException;
import java.io.BufferedReader; 
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;

import java.util.ArrayList;

public class PlayerHandler implements Runnable 
{
    public Socket socket;
    public String username;
    public BufferedReader bufferedReader;
    public BufferedWriter bufferedWriter;

    private static ArrayList<PlayerHandler> playerHandlers = new ArrayList<>();

    public PlayerHandler(Socket socket)
    {
        try
        {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = bufferedReader.readLine();

            broadcastMessage("Server: " + username + " has entered the chat!");

        }catch(IOException e)
        {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void run()
    {
     String message;
     while(socket.isConnected())
     {
        try
        {
            message = bufferedReader.readLine();
            broadcastMessage(message);
        }catch(IOException e)
        {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
     } 
    }
    
    public void broadcastMessage(String message)
    {
        for(PlayerHandler player : playerHandlers)
        {
            try
            {
                if(!player.username.equals(username))
                {
                    bufferedWriter.write(message); 
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
            }catch(IOException e)
            {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }
 
    public void removeHandler()
    {
        playerHandlers.remove(this);
        broadcastMessage("Server: " + username + " has left the chat!");
    }
    
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter)
    {
        removeHandler();
        try
        {
            if(socket != null)
            {
                socket.close();
            }
            if(bufferedWriter != null) 
            {
                bufferedWriter.close();
            } 
            if(bufferedReader != null)
            {
                bufferedReader.close();
            }
        }catch(IOException e )
        {
            e.printStackTrace();
        }
    } 
}