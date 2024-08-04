import java.net.Socket;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.util.Random;
import java.util.ArrayList;
import java.util.Scanner;

public class Hangman 
{
	// Variables
	private Socket socket;
	private String username;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	
	private Boolean host; // Game host 
	
	private static volatile Boolean gameOn = false; 
	private static final Object lock = new Object();	
	private Boolean killer; // Player chooses word
	private static String word; // Word that needs to be guessed 
	private Boolean inWaitingRoom; 

	// private String command;

	private static ArrayList<Hangman> players = new ArrayList<>(); 


	// Hangman()
	public Hangman(String username, Socket socket)
	{
		try
		{
			this.username = username;
			this.socket = socket;
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

		}catch(IOException e)
		{
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}

	// Listener -- Listen for commands	
	public void listenForCommands(String command)
	{  
		synchronized (lock)
		{ 						 
			command = command.trim(); 
			switch(command) 
			{
				// String command; 
				// try
				// { 

						// command = bufferedReader.readLine();
				case "/Create game":
					if(Hangman.gameOn != true  )
					{ 
						// Call Create() game 
						gameOn = true;
						System.out.println("------- Create() called --------- ");
						Hangman.gameOn = true;
						Create();
					} else 
					{
						System.out.println("Game is already on");
					}

				case "/Join game":
					Join();
					// boolean isOn = false;
					// for(Hangman player : players)
					// {
					// 	System.out.println(player.username + " " + Hangman.gameOn);
					// 	if(Hangman.gameOn == true)
					// 	{
					// 		isOn = true;
					// 	}
					// }
					// if(isOn == true)
					// {
					// 	// Call Join() game
					// 	System.out.println("Added: " + username);
					// 	Join();
					// }else
					// {
					// 	System.out.println("No game to join " + gameOn);
					// }

							// else if(gameOn != false && command.equals("/Leave game"))
							// {
							// 	// Call Leave() game
							// 	Leave();
							// } 

							// else if(gameOn != false && host != false && command.equals("/End gane"))
							// {
							// 	End();
							// }  
				// }catch(IOException e)
				// {
				// 	closeEverything(socket, bufferedReader, bufferedWriter);
				// } 
			}
		}
	}

	public static void setGameOn(boolean x)
	{
		synchronized (lock)
		{
			if(!gameOn)
			{
				Hangman.gameOn = true; 
			}else
			{
				System.out.println("Game is already on.");
			}
		}
	} 

	public static boolean getGameOn()
	{
		synchronized (lock)
		{
			return Hangman.gameOn;
		}
	}

	// Create() -- Game
	public void Create()
	{
		players.add(this);
		System.out.println("Setting on"); 
		setGameOn(true);
		System.out.println(gameOn);
		synchronized (lock)
		{
			setGameOn(true);
			System.out.println("Game created by: " + username); // Debug log
		}

		try
		{ 	
			this.host = true;
			
			bufferedWriter.write("Server: " + username + " has created a Hangman match game!");
			bufferedWriter.newLine();
			bufferedWriter.flush();

			bufferedWriter.write("Server: Type /Join game to join " + username + " match! ");
			bufferedWriter.newLine();
			bufferedWriter.flush();

			 
			while(socket.isConnected() && Hangman.gameOn != false)
			{  
				if(players.size() > 1)
				{
					setGameOn(true);
					// newRound();
				}

			} 
		}catch(IOException e)
		{
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}



	public void newRound()
	{
		this.inWaitingRoom = false;

		Random random = new Random();
		Scanner input = new Scanner(System.in);
	
		//Choose Random Player as killer
		int randNum = random.nextInt(players.size());
		Hangman killerPlayer = players.get(randNum);		

		if(killerPlayer.username.equals(username))
		{
			System.out.println("What will your guessing word be: ");
			word = input.nextLine();
		}
		else if(players.contains(this))
		{
			int strike = 0; 
			new Thread(new Runnable(){
				public void run()
				{ 
					String guess;
					char[] guessCharacters;
					String[] wordCharacters = word.split(""); 

					for(String c : wordCharacters){c = "_";}

					String reveal = String.join(" ", wordCharacters);

					String currentGuessed = reveal;

					System.out.println("What will your letter guess be: ");	

					// while(socket.isConnected() && players.contains(this))
					// {
					// 	String = bufferedReader.readLine();
					// 	guessCharacters = guess.toCharArray();

					// 	if(!(guessCharacters.length > 1))
					// 	{
					// 		if(word.contains(guess))
					// 		{
					// 			Reveal(currentGuessed, guess);
					// 			Update(reveal);
					// 		}
					// 	} 
					// }
				}
			}).start();
		}

	}

	// public String updateCurrentGuessed(String currentGuessed, char guess)
	// {
	// 	char[] wordArray = word.toCharArray(); 
	// 	String[] cgArray = currentGuessed.split("");


	// 	for(int i = 0; i < wordArray.length; i++)
	// 	{
	// 		if(wordArray[i] = guess)
	// 		{
	// 			cgArray[i] = guess;
	// 		}
	// 	}

	// 	return String.join(" ", cgArray);
	// }

	//Reveal() -- Game
	// public String Reveal(char guess)
	// { 
	// 	char[] wordInChars = word.toCharArray();
	// 	ArrayList<String> tempWord = new ArrayList<>();

	// 	for(char c : wordInChars)
	// 	{
	// 		if(c != guess)
	// 		{
	// 			tempWord.add("_");
	// 		}else
	// 		{
	// 			tempWord.add(Character.toString(c));
	// 		}
	// 	}
	// 	return String.join(" ", tempWord);
	// }

	// public String Update(String currentReveal)
	// {
	// 	char[] wordArray = word.toCharArray();
	// 	ArrayList<String> currentWordArray = new ArrayList<>();
	// 	String currentWord = String.join("", currentWordArray);

	// 	List[] split = currentReveal(" ");
	// 	ArrayList<String> currentRevealArray = new ArrayList<>(Arrays.asList(split)); 
	// 	for(char c : wordArray)
	// 	{
	// 		currentWordArray.add(Character.toString(c));
	// 	}

	// 	int counter = 0;
	// 	for(String w : currentWordArray)
	// 	{
	// 		for(String r : currentRevealArray)
	// 		{
	// 			if(!(w.equals(r)))
	// 			{
	// 				currentWordArray[counter] = "_"
	// 			}
	// 			counter++;
	// 		}
	// 	} 

	// 	return String.join("",currentWordArray);

	// }

	// Join() -- Game
	public void Join()
	{ 
		
		players.add(this);
		host = false;
		boolean isOn = false;
		
		for(int i = 0; i < players.size(); i++)
		{ 
			if(players.get(i).host == true)
			{ 
				isOn = players.get(i).host;
			}
		}
		
		synchronized(lock)
		{
			if(Hangman.getGameOn())
			{	
				System.out.println("Getting " + getGameOn());
				broadcastMessage("Server: " + username + " has join the match!");

			} else {
                System.out.println("Game is not started yet. " + isOn); // Debug log
            }
		} 
	}
	// Leave()

	public void broadcastMessage(String message)
    { 
		for(Hangman player : players)
        {
            try
            {
                if(!player.getUsername().equals(username))
                {
                    player.bufferedWriter.write(message); 
                    player.bufferedWriter.newLine();
                    player.bufferedWriter.flush();
                }
            }catch(IOException e)
            {
                closeEverything(socket, bufferedReader, bufferedWriter);
            } 
		}
    }

	public String getUsername()
	{
		return this.username;
	}

	public void Leave()
	{
		players.remove(this);
	}

	// public void setCommand(String command){this.command = command;}

	public void End()
	{
		try{
			if(host != false){Hangman.gameOn = false;} 
			this.host = false; 
			bufferedWriter.write("Server: " + username + " has ended the match.");
			bufferedWriter.newLine();
			bufferedWriter.flush();
		}catch(IOException e)
		{
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}

	public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter)
	{
		Leave();
		End();
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
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}