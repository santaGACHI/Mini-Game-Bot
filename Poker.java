package Bot.Bot;

import javax.swing.*;
import javax.swing.Timer;

import net.dv8tion.jda.client.entities.UserSettings;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.awt.event.*;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.awt.*;
import java.util.Map.Entry;


public class Poker
{
	private JDA jda;

	private List saveFile;

	DM dm = new DM();
	private PokerPlayer player[];
	private User user[];
	private Cards cards[];
	private MessageChannel channel[];
	private Stack<Integer> deck = new Stack<Integer>();

	Random rng;
	private Timer timer = new Timer(1000, new TimerListener());
	private int counter = 0;

	public Poker(JDA j)
	{
		rng = new Random();
		cards = new Cards[52];
		saveFile = new List();
		jda = j;
		player = new PokerPlayer[6];
		user = new User[6];
		channel = new MessageChannel[10];
	}

	public void init()
	{
		loadPlayers();
		loadCards();
		shuffleDeck();
		play();
		savePlayers();
	}

	private void play()
	{
		House house = new House();
		/*
		house.add(deal());
		house.add(deal());
		house.add(deal());
		*/
		Cards temp = new Cards(1, "spade", "red");
		Cards temp2 = new Cards(1, "diamond", "red");
		Cards temp3 = new Cards(1, "spade", "red");
		Cards temp4 = new Cards(10, "heart", "red");
		Cards temp5 = new Cards(10, "spade", "red");
		Cards temp6 = new Cards(10, "spade", "red");
		Cards temp7 = new Cards(13, "spade", "red");

		house.add(temp);
		house.add(temp2);
		house.add(temp4);

		house.add(temp6);
		house.add(temp7);


		channel[0].sendMessage(house.printCards()).queue();
		player[0].setCards(temp3,0);
		player[0].setCards(temp5,1);
		//dm.sendDm(player[0].getCards(), 0);
		dm.sendDm(player[0].getCards(), 2);

		checkTriple(player[0], house);
		/*
		for (int i = 0; i < counter; i++)
		{
			player[i].setCards(deal(), 0);
			player[i].setCards(deal(), 1);
			player[i].setNumOfCards(2);
			dm.sendDm(player[i].getCards(), i);
		}
		*/



	}
	private void shuffleDeck()
	{
		for(int i = 0; i < 52; i ++)
			deck.add(i,i);
		Collections.shuffle(deck);
	}
	private Cards deal()
	{
		int n = deck.pop();
		Cards c = cards[n];
		return c;
	}

	private void loadCards()
	{
		int counter = 1;
		String color = "";
		String symbol = "";

		for (int i = 0; i < 52; i++)
		{
			if (i % 4 == 0) {
				symbol = "diamond";
				color = "red";
				counter++;
			}
			else if (i % 4 == 1) {
				symbol = "club";
				color = "black";
			}
			else if (i % 4 == 2) {
				symbol = "heart";
				color = "red";
			}
			else if (i % 4 == 3) {
				symbol = "spade";
				color = "black";
			}
			cards[i] = new Cards(counter, symbol, color);

		}
	}

	private void savePlayers()
	{
		try {
			FileOutputStream fos = new FileOutputStream(new File("./list.xml"));
			XMLEncoder encoder = new XMLEncoder(fos);
			encoder.writeObject(saveFile);
			encoder.close();
			fos.close();
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}

	private void loadPlayers()
	{

		try {
			File file = new File("./list.xml");
			if (!file.exists())
			{
				FileOutputStream fos = new FileOutputStream(new File("./list.xml"));
				XMLEncoder encoder = new XMLEncoder(fos);
				encoder.writeObject(new List());
				encoder.close();
				fos.close();
				System.out.println("Creating list.xml file");
			}
			FileInputStream fis = new FileInputStream(file);
			XMLDecoder decoder = new XMLDecoder(fis);
			saveFile = (List)decoder.readObject();
			decoder.close();
			fis.close();
		}
		catch (IOException e){
			e.printStackTrace();
		}
		for (int i = 0; i < counter; i++)
		{
			player[i] = new PokerPlayer(user[i]);
			dm.setUser(user[i], i);
			saveFile.add(player[i].getID());
		}
		//System.out.println(jda.getUserById(id).getName() + jda.getUserById(id).getDiscriminator());
	}




	public boolean isInRoom(User u)
	{
		for (int i = 0; i < counter; i++)
		{
			if (u.getIdLong() == user[i].getIdLong())
				return true;
		}
		return false;
	}

	private class TimerListener implements ActionListener
	{
		public TimerListener() {}
		public void actionPerformed(ActionEvent e)
		{
			System.out.println("hi");
			counter++;
			if (counter == 5)
			{
				counter = 0;
				timer.stop();
			}
		}
	}
	public int getCounter(){return counter;}
	public void addCounter(){counter++;}
	public void subCounter(){counter--;}
	public void resetCounter(){counter = 0;}
	public void setChannel(MessageChannel ch, int n){channel[n] = ch;}
	public MessageChannel getChannel(int n){return channel[n];}
	public void setPlayer(User u, int n){user[n] = u;}
	public User getPlayer(int n){return user[n];}


	/*
	Combination checker methods
	 */
	private boolean checkRoyalFlush(PokerPlayer player, House house)
	{
		boolean answer = false;
		Integer count;
		HashMap<String,Integer> freq = new HashMap<String, Integer>();

		ArrayList<Cards> allCards =  new ArrayList<Cards>();
		ArrayList<Integer> allCards2 = new ArrayList<Integer>();
		ArrayList<Integer> checkSuits = new ArrayList<Integer>();

		Cards[] houseCards = house.getCards().clone();
		Cards[] playerCards = player.getCardsList().clone();
		for(int i = 0; i < house.getLastIndex(); i ++)
			allCards.add(houseCards[i]);

		for(int i = 0; i < 2; i ++)
			allCards.add(playerCards[i]);

		/*
		for(int i = 0; i < allCards.size(); i ++)
			System.out.println(allCards.get(i).getNumber() + " suite: " + allCards.get(i).getSymbol());
		*/

		/*
		check for highest suit
		*/
		for(int i = 0; i < allCards.size(); i ++)
		{
			count = freq.get(allCards.get(i).getSymbol());
			if(count == null)
				count = 0;
			freq.put(allCards.get(i).getSymbol(), 1 + count);
		}
		int maxCount = 0;
		String highestSuit = "";
		for(Map.Entry<String,Integer> ent: freq.entrySet())
		{
			if (ent.getValue() > maxCount)
			{
				highestSuit = ent.getKey();
				maxCount = ent.getValue();
			}
		}
		//System.out.println("Highest suit: " + highestSuit + " total number: " + maxCount);

		if(maxCount < 5)
		{
			//System.out.println("No Royal Flush!");
			return false;
		}
		else
		{
			for(int i = 0; i < allCards.size(); i ++)
				if(!allCards.get(i).getSymbol().equals(highestSuit))
					allCards.remove(i);

			/*
			for(int i = 0; i < allCards.size(); i ++)
				System.out.println(allCards.get(i).getNumber() + " suite: " + allCards.get(i).getSymbol());
			*/

			for(int k = 0; k < 4; k ++)
				for(int i = 0; i <allCards.size(); i ++)
				{
					if(allCards.get(i).getNumber() == 10 + k)
						answer = true;
					else
					{
						answer = false;
						break;
					}
				}

		}
		if(answer == true)
			System.out.println("Royal Flush!");
		else
			System.out.println("No Royal Flush!");
		return answer;
	}
	private boolean checkStraightFlush(PokerPlayer player, House house)
	{
		boolean answer = false;
		Integer count = 0;
		HashMap<String,Integer> freq = new HashMap<String, Integer>();

		ArrayList<Cards> allCards =  new ArrayList<Cards>();
		ArrayList<Integer> allCards2 = new ArrayList<Integer>();
		ArrayList<Integer> checkSuits = new ArrayList<Integer>();

		Cards[] houseCards = house.getCards().clone();
		Cards[] playerCards = player.getCardsList().clone();
		for(int i = 0; i < house.getLastIndex(); i ++)
			allCards.add(houseCards[i]);

		for(int i = 0; i < 2; i ++)
			allCards.add(playerCards[i]);

		for(int i = 0; i < allCards.size(); i ++)
			System.out.println(allCards.get(i).getNumber() + " suite: " + allCards.get(i).getSymbol());

		for(int i = 0; i < allCards.size(); i ++)
			allCards2.add(allCards.get(i).getNumber());

		/*
		check for highest suit
		*/
		for(int i = 0; i < allCards.size(); i ++)
		{
			count = freq.get(allCards.get(i).getSymbol());
			if(count == null)
				count = 0;
			freq.put(allCards.get(i).getSymbol(), 1 + count);
		}
		int maxCount = 0;
		String highestSuit = "";
		for(Map.Entry<String,Integer> ent: freq.entrySet())
		{
			if (ent.getValue() > maxCount)
			{
				highestSuit = ent.getKey();
				maxCount = ent.getValue();
			}
		}
		System.out.println("Highest suit: " + highestSuit + " total number: " + maxCount);

		if(maxCount < 5)
		{
			System.out.println("No Straight Flush!");
			return false;
		}
		else
		{
			for(int i = 0; i < allCards.size(); i ++)
				if(!allCards.get(i).getSymbol().equals(highestSuit))
					allCards.remove(i);
			for(int i = 0; i < allCards.size(); i ++)
				allCards2.add(allCards.get(i).getNumber());
			//Collections.sort(allCards2);
			insertionSort(allCards);
			for(int i  = 0; i < allCards.size(); i ++)
			{
				System.out.println(allCards.get(i).getNumber() + " suite: " + allCards.get(i).getSymbol());
				//System.out.println(allCards2.get(i));
			}
			count = 1;
			for(int i = 0 ; i < allCards.size() - 1; i ++)
			{
				int curr = allCards.get(i).getNumber();
				if(curr + 1 == allCards.get(i + 1).getNumber())
					count++;
				else
					count = 1;
				System.out.println("Counter: " + count);
				if(count == 5)
				{
					answer = true;
					break;
				}
			}
			if(answer == true)
				System.out.println("Straight Flush!");
			else
				System.out.println("No Straight Flush!");
		}
		return answer;
	}
	private boolean checkFourOfAKind(PokerPlayer player, House house)
	{
		Integer count = 0;
		boolean answer = false;
		HashMap<Integer,Integer> freq = new HashMap<Integer, Integer>();
		ArrayList<Cards> allCards =  new ArrayList<Cards>();
		Cards[] houseCards = house.getCards().clone();
		Cards[] playerCards = player.getCardsList().clone();

		for(int i = 0; i < house.getLastIndex(); i ++)
			allCards.add(houseCards[i]);

		for(int i = 0; i < 2; i ++)
			allCards.add(playerCards[i]);

		/*
		check for highest suit
		*/
		for(int i = 0; i < allCards.size(); i ++)
		{
			count = freq.get(allCards.get(i).getNumber());
			if(count == null)
				count = 0;
			freq.put(allCards.get(i).getNumber(), 1 + count);
		}
		int maxCount = 0,freqCard = 0;
		for(Map.Entry<Integer,Integer> ent: freq.entrySet())
		{
			if (ent.getValue() > maxCount)
			{
				freqCard = ent.getKey();
				maxCount = ent.getValue();
			}
		}
		System.out.println("Highest freq Card: " + freqCard + " total number: " + maxCount);
		if(maxCount == 4)
		{

			System.out.println("Bomb!");
			return true;
		}
		else
			System.out.println("No Bomb!");

		return answer;
	}
	private boolean checkFullHouse(PokerPlayer player, House house)
	{
		Integer count = 0;
		boolean answer = false;
		HashMap<Integer,Integer> freq = new HashMap<Integer, Integer>();
		HashMap<Integer,Cards> freq2 = new HashMap<Integer,Cards>();
		ArrayList<Cards> allCards =  new ArrayList<Cards>();
		Cards[] houseCards = house.getCards().clone();
		Cards[] playerCards = player.getCardsList().clone();

		for(int i = 0; i < house.getLastIndex(); i ++)
			allCards.add(houseCards[i]);

		for(int i = 0; i < 2; i ++)
			allCards.add(playerCards[i]);

		/*
		check for highest suit
		*/
		for(int i = 0; i < allCards.size(); i ++)
		{
			count = freq.get(allCards.get(i).getNumber());
			if(count == null)
				count = 0;
			freq.put(allCards.get(i).getNumber(),  count + 1);
			freq2.put(count + 1, allCards.get(i));
		}
		if(freq.containsValue(3) && freq.containsValue(2))
		{
			answer = true;
			System.out.println("Full house of : " + freq2.get(3).getNumber() + "  " + freq2.get(2).getNumber());
		}
		if(answer == true)
			System.out.println("Has full house");
		else
			System.out.println("No Has full house");
		/*
		Set<Entry<Integer,Integer>> set = freq.entrySet();
		ArrayList<Entry<Integer,Integer>> list = new ArrayList<Entry<Integer, Integer>>();
		for(Entry<Integer,Integer> entry: set)
		{
			if(entry!= null)
				list.add(entry);
		}
		insertionSort2(list);
		Iterator<Entry<Integer,Integer>> it = list.iterator();
		while(it.hasNext())
		{
			Entry temp = it.next();
			System.out.println(temp.getKey() + "  " + temp.getValue());
		}
		*/
		return answer;
	}
	private boolean checkFlush(PokerPlayer player, House house)
	{
		boolean answer = false;
		Integer count = 0;
		HashMap<String,Integer> freq = new HashMap<String, Integer>();

		ArrayList<Cards> allCards =  new ArrayList<Cards>();
		ArrayList<Integer> allCards2 = new ArrayList<Integer>();
		ArrayList<Integer> checkSuits = new ArrayList<Integer>();

		Cards[] houseCards = house.getCards().clone();
		Cards[] playerCards = player.getCardsList().clone();
		for(int i = 0; i < house.getLastIndex(); i ++)
			allCards.add(houseCards[i]);

		for(int i = 0; i < 2; i ++)
			allCards.add(playerCards[i]);

		for(int i = 0; i < allCards.size(); i ++)
			System.out.println(allCards.get(i).getNumber() + " suite: " + allCards.get(i).getSymbol());

		for(int i = 0; i < allCards.size(); i ++)
			allCards2.add(allCards.get(i).getNumber());

		/*
		check for highest suit
		*/
		for(int i = 0; i < allCards.size(); i ++)
		{
			count = freq.get(allCards.get(i).getSymbol());
			if(count == null)
				count = 0;
			freq.put(allCards.get(i).getSymbol(), 1 + count);
		}
		int maxCount = 0;
		String highestSuit = "";
		for(Map.Entry<String,Integer> ent: freq.entrySet())
		{
			if (ent.getValue() > maxCount)
			{
				highestSuit = ent.getKey();
				maxCount = ent.getValue();
			}
		}
		System.out.println("Highest suit: " + highestSuit + " total number: " + maxCount);

		if(maxCount >= 5)
		{
			System.out.println("Flush!");
			return true;
		}
		return answer;
	}
	private boolean checkStraight(PokerPlayer player, House house)
	{
		boolean answer = false;
		Integer count = 0;

		ArrayList<Cards> allCards =  new ArrayList<Cards>();

		Cards[] houseCards = house.getCards().clone();
		Cards[] playerCards = player.getCardsList().clone();
		for(int i = 0; i < house.getLastIndex(); i ++)
			allCards.add(houseCards[i]);

		for(int i = 0; i < 2; i ++)
			allCards.add(playerCards[i]);

		insertionSort(allCards);
		for(int i = 0; i < allCards.size(); i ++)
			System.out.println(allCards.get(i).getNumber() + " suite: " + allCards.get(i).getSymbol());

		count = 1;
		for(int i = 0 ; i < allCards.size() - 1; i ++)
		{
			int curr = allCards.get(i).getNumber();
			if(curr + 1 == allCards.get(i + 1).getNumber())
				count++;
			else
				count = 1;
			if(count == 5)
			{
				answer = true;
				break;
			}
		}
		if(answer == true)
			System.out.println("Straight ");
		else
			System.out.println("No Straight!");
		return answer;
	}
	private boolean checkTriple(PokerPlayer player, House house)
	{
		Integer count;
		boolean answer = false;
		HashMap<Integer, Integer> freq = new HashMap<Integer, Integer>();
		ArrayList<Cards> allCards = new ArrayList<Cards>();
		ArrayList<Cards> listOfThrees = new ArrayList<Cards>();
		Cards[] houseCards = house.getCards().clone();
		Cards[] playerCards = player.getCardsList().clone();

		for (int i = 0; i < house.getLastIndex(); i++)
			allCards.add(houseCards[i]);

		for (int i = 0; i < 2; i++)
			allCards.add(playerCards[i]);

		for (int i = 0; i < allCards.size(); i++) {
			count = freq.get(allCards.get(i).getNumber());
			if (count == null)
				count = 0;
			freq.put(allCards.get(i).getNumber(), count + 1);
		}
		if(freq.containsValue(3))
		{
			System.out.println("Triple");

			Set<Entry<Integer,Integer>> set = freq.entrySet();
			ArrayList<Entry<Integer,Integer>> list = new ArrayList<Entry<Integer, Integer>>();
			for(Entry<Integer,Integer> entry: set)
			{
				if(entry!= null)
					list.add(entry);
			}
			insertionSort2(list);
			for(int i = 0; i < list.size(); i ++)
			{
				if(list.get(i).getValue() != 3)
					list.remove(i);
			}
			for(int i = 0; i < list.size(); i ++)
			{
				for(int k = 0; k < allCards.size(); k ++)
				{
					if (list.get(i).getKey() == allCards.get(k).getNumber())
					{
						listOfThrees.add(allCards.get(k));
					}
				}
			}

			//for(int i = 0; i < listOfThrees.size(); i ++)
				//System.out.println("All triples: " + listOfThrees.get(i).getNumber());
		}
		return answer;
	}
	private boolean checkDouble(PokerPlayer player, House house)
	{
		Integer count;
		boolean answer = false;
		HashMap<Integer, Integer> freq = new HashMap<Integer, Integer>();
		ArrayList<Cards> allCards = new ArrayList<Cards>();
		ArrayList<Cards> listOfThrees = new ArrayList<Cards>();
		Cards[] houseCards = house.getCards().clone();
		Cards[] playerCards = player.getCardsList().clone();

		for (int i = 0; i < house.getLastIndex(); i++)
			allCards.add(houseCards[i]);

		for (int i = 0; i < 2; i++)
			allCards.add(playerCards[i]);

		for (int i = 0; i < allCards.size(); i++) {
			count = freq.get(allCards.get(i).getNumber());
			if (count == null)
				count = 0;
			freq.put(allCards.get(i).getNumber(), count + 1);
		}
		if(freq.containsValue(2))
		{
			System.out.println("Double");

			Set<Entry<Integer,Integer>> set = freq.entrySet();
			ArrayList<Entry<Integer,Integer>> list = new ArrayList<Entry<Integer, Integer>>();
			for(Entry<Integer,Integer> entry: set)
			{
				if(entry!= null)
					list.add(entry);
			}
			insertionSort2(list);
			for(int i = 0; i < list.size(); i ++)
			{
				if(list.get(i).getValue() != 2)
					list.remove(i);
			}
			for(int i = 0; i < list.size(); i ++)
			{
				for(int k = 0; k < allCards.size(); k ++)
				{
					if (list.get(i).getKey() == allCards.get(k).getNumber())
					{
						listOfThrees.add(allCards.get(k));
					}
				}
			}
			//for(int i = 0; i < listOfThrees.size(); i ++)
			//System.out.println("All triples: " + listOfThrees.get(i).getNumber());
		}
		return answer;
	}
	public void insertionSort(ArrayList<Cards> S)
	{
		Comp comp = new Comp();
		insertionSort(S, comp);
	}
	private void insertionSort(ArrayList<Cards> S, Comparator comp)
	{
		for (int i=1; i < S.size(); i++)
		{
			Cards key = S.get(i);
			int j = i-1;
			while (j>=0 && comp.compare(S.get(j).getNumber(),key.getNumber()) > 0)//S[j].getKey() > key.getKey())
			{
				S.remove(j+1);
				S.add(j+1,S.get(j));
				j = j-1;
			}
			S.remove(j+1);
			S.add(j+1,key);
		}
	}
	public void insertionSort2(ArrayList<Entry<Integer,Integer>> S)
	{
		Comp comp = new Comp();
		insertionSort2(S, comp);
	}
	private void insertionSort2(ArrayList<Entry<Integer,Integer>> S, Comparator comp)
	{
		for (int i=1; i < S.size(); i++)
		{
			Entry key = S.get(i);
			int j = i-1;
			while (j>=0 && comp.compare(S.get(j).getKey(),key.getKey()) > 0)//S[j].getKey() > key.getKey())
			{
				S.remove(j+1);
				S.add(j+1,S.get(j));
				j = j-1;
			}
			S.remove(j+1);
			S.add(j+1,key);
		}
	}
}

