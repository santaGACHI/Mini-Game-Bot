package Bot.Bot;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class RPS 
{
	private User player[] = new User[2];
	private MessageChannel channel[] = new MessageChannel[2];
	private String firstWord;
	private String secondWord;
	private int counter = 0;
	
	public RPS() {}
	
	public int getCounter()
	{
		return counter;
	}
	
	public void addCounter()
	{
		counter++;
	}
	
	public void resetCounter()
	{
		counter = 0;
	}
	
	public void setChannel(MessageChannel ch, int n)
	{
		channel[n] = ch;
	}
	
	public MessageChannel getChannel(int n)
	{
		return channel[n];
	}
	
	public void setPlayer(User u, int n)
	{
		player[n] = u;
	}
	
	public User getPlayer(int n)
	{
		return player[n];
	}
	
	public void setFirstWord(String f)
	{
		firstWord = f;
	}
	
	public void setSecondWord(String f)
	{
		secondWord = f;
	}
	
	public String getFirstWord()
	{
		return firstWord;
	}
	
	public String getSecondWord()
	{
		return secondWord;
	}
	
	private boolean canStart()
	{
		return (firstWord != null && secondWord != null);			
	}
	
	public int start()
	{
		if (firstWord.equalsIgnoreCase("Scissor") && secondWord.equalsIgnoreCase("Paper"))
			return 1;
		else if (firstWord.equalsIgnoreCase("Paper") && secondWord.equalsIgnoreCase("Rock"))
			return 1;
		else if (firstWord.equalsIgnoreCase("Rock") && secondWord.equalsIgnoreCase("Scissor"))
			return 1;
		else if (firstWord.equalsIgnoreCase("Rock") && secondWord.equalsIgnoreCase("Paper"))
			return 2;
		else if (firstWord.equalsIgnoreCase("Scissor") && secondWord.equalsIgnoreCase("Rock"))
			return 2;
		else if (firstWord.equalsIgnoreCase("Paper") && secondWord.equalsIgnoreCase("Scissor"))
			return 2;
		return 0;
	}


	
}
