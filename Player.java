import java.net.Socket;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.util.Scanner;

public class Player
{
	private Socket socket;
	private String username;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter; 
	private Hangman game;

	public static void main(String[] args) throws IOException
	{
		Scanner input = new Scanner(System.in);
		System.out.println("What is your name: ");
		String username = input.nextLine();
		Socket socket = new Socket("localhost", 1818);
		Hangman game = new Hangman(username, socket);
 
		Player player = new Player(username, socket, game); 


		player.listenForMessage();
		player.sendMessage(); 
		// new Thread(new Runnable()
		// {
		// 	@Override 
		// 	public void run()
		// 	{
		// 	}
		// }).start();
		input.close();

	}


	public Player(String username, Socket socket, Hangman game)
	{
		try
		{
			this.username = username;
			this.socket = socket;
			this.game = game;
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); 
			 
			// game.listenForCommands();
		}catch(IOException e)
		{
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}

	
	public void setHangman(Hangman game){this.game = game;}
	public Hangman getHangman(){return this.game;} 

	public void sendMessage()
	{
		try
		{

			bufferedWriter.write(username);
			bufferedWriter.newLine();
			bufferedWriter.flush();

			Scanner input = new Scanner(System.in);	
			while(socket.isConnected())
			{
				String message = input.nextLine();
				bufferedWriter.write(username + ": " + message);
				bufferedWriter.newLine();
				bufferedWriter.flush(); 

				// game.setCommand(message);
				game.listenForCommands(message);
			}
			input.close();
		}catch(IOException e)
		{ 
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}

	public void listenForMessage()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				String message; 
				try
				{
					while(socket.isConnected())
					{
						message = bufferedReader.readLine();
						if(message == null) break;
						System.out.println(message);
						bufferedWriter.flush();
				 	}

				}catch(IOException e)
				{
					closeEverything(socket, bufferedReader, bufferedWriter);
				}
			}
		}).start();
	}

	public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter)
	{
		try
		{
			if(socket != null)
			{
				socket.close();
			}
			if(bufferedReader != null)
			{
				bufferedReader.close();
			}
			if(bufferedWriter != null)
			{
				bufferedWriter.close();
			}
			game.closeEverything(socket, bufferedReader, bufferedWriter);

		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
