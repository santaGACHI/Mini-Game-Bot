package Bot.Bot;

public class Pot 
{
	private int balance;
	
	public Pot() {
		balance = 0;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}
	
	public void add(int balance) {
		this.balance += balance;
	}
}
