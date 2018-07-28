package Bot.Bot;

public class House 
{
	private Cards cards[];
	private int lastIndex;
	
	public House() {
		cards = new Cards[6];
		lastIndex = 0;
	}
	
	public void add(Cards cards)
	{
		this.cards[lastIndex] = cards;
		lastIndex++;
	}

	public Cards getCard(int index)
	{
		return cards[index];
	}
	
	public void printCards()
	{
		for (int i = 0; i < lastIndex; i++)
		{
			System.out.println(cards[i].getNumber());
			System.out.println(cards[i].getSymbol());
		}
	}
}
