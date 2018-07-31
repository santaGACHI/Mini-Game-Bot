package Bot.Bot;

public class House
{
	private Cards cards[];
	private int lastIndex;
	public int getLastIndex() { return lastIndex; }
	public Cards[] getCards() { return cards;};
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

	public String printCards()
	{
		String str = "";
		for (int i = 0; i < lastIndex; i++)
		{
			str = str + cards[i].getNumber() + " " + cards[i].getSymbol() + " \n";
		}
		return str;
	}
}

