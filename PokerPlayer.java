package Bot.Bot;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.entities.impl.UserImpl;

public class PokerPlayer
{
	private User user;
	private int balance;
	private Cards cards[];
	private boolean status;
	private int numOfCards;

	public void setNumOfCards(int numOfCards){ this.numOfCards = numOfCards; }
	public int getNumOfCards(){return numOfCards;}
	public Cards[] getCardsList(){return cards;}

	public PokerPlayer(User u)
	{
		user = u;
		balance = 0;
		cards = new Cards[2];
		numOfCards = 2;
		status = true;
	}

	public String getCards() {
		String str = "";
		for (int i = 0; i < 2; i++)
		{
			str = str + cards[i].getNumber() + cards[i].getSymbol() + " \n";
		}
		return str;
	}

	public void setCards(Cards c, int n) {
		cards[n] = c;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public int getBalance()
	{
		return balance;
	}

	public int subBalance(int b)
	{
		return balance -= b;
	}

	public boolean canBuyIn()
	{
		return balance - 1 > 0;
	}

	public String getName()
	{
		return user.getName()+user.getDiscriminator();
	}

	public String getID()
	{
		return Long.toString(user.getIdLong());
	}

	public void setStatus(boolean s)
	{
		status = s;
	}

	public boolean getStatus()
	{
		return status;
	}
}
