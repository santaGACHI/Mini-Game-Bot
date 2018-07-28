package Bot.Bot;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.entities.impl.UserImpl;

public class PokerPlayer
{
	User user;
	private int balance;

	public PokerPlayer(User u) 
	{
		user = u;
		balance = 0;
	}
	
	public int getBalance()
	{
		return balance;
	}
	
	public String getName()
	{
		return user.getName()+user.getDiscriminator();
	}
	
	public String getID()
	{
		return Long.toString(user.getIdLong());
	}
	
}
