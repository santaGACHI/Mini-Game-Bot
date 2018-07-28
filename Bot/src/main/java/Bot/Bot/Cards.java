package Bot.Bot;

public class Cards 
{
	private int number;
	private String symbol;
	private String color;
	
	public Cards(int number, String symbol, String color) {
		this.number = number;
		this.symbol = symbol;
		this.color = color;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	

	
}
