package Bot.Bot;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.impl.UserImpl;

public class DM 
{
	User user[];
	
	public DM() {user = new User[6];}
	
	public void setUser(User u, int n)
	{
		user[n] = u;
	}
	
	public void sendDm(String str, int n)
	{

		try 
		{
			user[n].openPrivateChannel().complete();
	    	((UserImpl)user[n]).getPrivateChannel().sendMessage(str).queue();			
		}
	    catch (Exception e)
		{
	    	System.out.println();
		}
	}
}
