package Bot.Bot;

import javax.swing.*;
import javax.swing.Timer;

import net.dv8tion.jda.client.entities.UserSettings;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

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

public class Poker
{
    private JDA jda;
    
    private List saveFile;
    
    DM dm = new DM();
    private PokerPlayer player[];
    private User user[];
    private Cards cards[];
    private MessageChannel channel;
    private Stack<Integer> deck = new Stack<Integer>();
    
    Random rng;
    private House house;
    private Pot pot;
    private Timer timer = new Timer(1000, new TimerListener());
    private int counter = 0;
    private int playerCounter = 0;
    private boolean wasRaised = false;
    private int currentRaise = 0;
    private boolean gameFinished;
    
    public Poker(JDA j)
    {
    	house = new House();
    	pot = new Pot();
        rng = new Random();
        cards = new Cards[52];
        saveFile = new List();
        jda = j;
        player = new PokerPlayer[6];
        user = new User[6];
        gameFinished = false;
    }
    
    public void init()
    {  	
        loadPlayers();
        loadCards();
        shuffleDeck();
        play();      
    }
    
    private void play()
    {
        house.add(deal());
        house.add(deal());
        house.add(deal());
        
        channel.sendMessage(house.printCards()).queue();
        
        for (int i = 0; i < counter; i++)
        {
        	if (player[i].canBuyIn())
        	{
                player[i].setCards(deal(), 0);
                player[i].setCards(deal(), 1);
                pot.add(player[i].subBalance(1));
                dm.sendDm(player[i].getCards(), i);	
        	}
        	else
        	{
        		player[i].setStatus(false);
        	}
        }
    	channel.sendMessage(player[playerCounter].getName()+"'s turn!").queue();
    	channel.sendMessage("Current Pot: "+pot.getBalance()).queue();
    }
    
    public void turn(User u, String str)
    {
    	turn(u, str, 0);
    }
    public void turn(User u, String str, int num) 
    {	
    	if (player[playerCounter].getID().equals(u.getId()))
    	{
    		if (str.equalsIgnoreCase("!poker raise "+num))
    		{
    			if (player[playerCounter].getBalance() > 0)
    			{
        			pot.add(player[playerCounter].subBalance(num));
        			currentRaise = num;
        			wasRaised = true;	
    			}
    			else
    			{
    				channel.sendMessage("You have insufficient funds to raise!").queue();
    				return;
    			}
    		}
    		if (str.equalsIgnoreCase("!poker check") && !wasRaised)
    		{
    			
    		}
    		if (str.equalsIgnoreCase("!poker call") && wasRaised)
    		{
    			if (player[playerCounter].getBalance() >= currentRaise)
    			{
    				pot.add(player[playerCounter].subBalance(currentRaise));
    			}
    			else
    			{
    				channel.sendMessage("You have insufficient funds to call! All in!").queue();
    				allIn();
    				return;
    			}
    		}
    		if (str.equalsIgnoreCase("!poker fold"))
    		{
    			player[playerCounter].setStatus(false);
    		}
    		playerCounter++;
    		
    		if (player[playerCounter] == null || !player[playerCounter].getStatus())
    			playerCounter++;
    		
    		if (player[playerCounter] == null)
    		{
    			playerCounter = 0;
    			if (house.isFull() && !str.equalsIgnoreCase("!poker fold"))
    			{
    				showCards();
    				return;
    			}  			
    			else if (str.equalsIgnoreCase("!poker fold"))
    			{			
    				boolean flag = false;
    				for (int i = 0; i < playerCounter; i++)
    				{
    					if(player[i].getStatus())
    						flag = true; 
    				}
    				if (!flag)
    				{
    					gameFinished = true;
    					channel.sendMessage("Everyone folded!").queue();
    					return;
    				}	
    			}
    			else
    			{			
        			house.add(deal());
        			channel.sendMessage(house.printCards()).queue();	
    			}
    		}
    		if (player[playerCounter].getStatus())
    		{
            	channel.sendMessage(player[playerCounter].getName()+"'s turn!").queue();
            	channel.sendMessage("Current Pot: "+pot.getBalance()).queue();  			
    		}
    	}
    }
    
    public boolean gameDone()
    {
    	return gameFinished;
    }
    
    private void allIn()
    {
    	pot.add(player[playerCounter].subBalance(player[playerCounter].getBalance()));
    }
    private void showCards()
    {
    	gameFinished = true;
    	channel.sendMessage(jda.getUserById(player[0].getID()).getName() + jda.getUserById(player[0].getID()).getDiscriminator()+" won "+pot.getBalance()).queue();
    }
    private void shuffleDeck()
    {
        for(int i = 0; i < 52; i++)
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
                color = "Red";
                counter++;
            }   
            else if (i % 4 == 1) {
                symbol = "club";
                color = "black";
            }
            else if (i % 4 == 2) {
                symbol = "heart";
                color = "Red";
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
            player[i].setBalance(saveFile.getBalance(player[i].getID()));
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
    public void setChannel(MessageChannel ch){channel = ch;}
    public MessageChannel getChannel(int n){return channel;}
    public void setPlayer(User u, int n){user[n] = u;}
    public User getPlayer(int n){return user[n];}
    
}

