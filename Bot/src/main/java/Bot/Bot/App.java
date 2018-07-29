package Bot.Bot;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.audio.AudioSendHandler;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.entities.impl.UserImpl;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.GenericPrivateMessageEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.AudioManager;

import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.awt.*;

public class App extends ListenerAdapter
{
	static JDA jda = null;
	
	Random rng = new Random();
	RPS rps = new RPS();
	Poker poker = new Poker(jda);
	DM dm = new DM();
	boolean pokerStarted = false;
	
	private AudioPlayerManager playerManager;
	
    public static void main( String[] args ) throws Exception
    {
        jda = new JDABuilder(AccountType.BOT).setToken(Ref.token).buildBlocking();
        jda.addEventListener(new App());
        
    }
    
    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) 
    {
    	if (event.getAuthor().isBot())
    		return;
    	
    	String word = event.getMessage().getContentRaw();
    	
    	if (word.equalsIgnoreCase("Rock") || word.equalsIgnoreCase("Paper") || word.equalsIgnoreCase("Scissor"))
    	{
    		if (rps.getCounter() == 0)
    		{
    			rps = new RPS();
    			rps.setFirstWord(word);
    			rps.setPlayer(event.getAuthor(), 0);
    			rps.setChannel(event.getChannel(), 0);
    			rps.addCounter();
    			event.getAuthor().getName();
    		}
    		else
    		{
    			rps.resetCounter();
    			rps.setPlayer(event.getAuthor(), 1);
    			rps.setChannel(event.getChannel(), 1);
    			rps.setSecondWord(word);
    			int winner = rps.start();
    			rps.getChannel(0).sendMessage(rps.getPlayer(0).getName()+" rolled "+rps.getFirstWord()).queue();
    			rps.getChannel(0).sendMessage(rps.getPlayer(1).getName()+" rolled "+rps.getSecondWord()).queue();
    			rps.getChannel(1).sendMessage(rps.getPlayer(0).getName()+" rolled "+rps.getFirstWord()).queue();
    			rps.getChannel(1).sendMessage(rps.getPlayer(1).getName()+" rolled "+rps.getSecondWord()).queue();
    			if (winner == 1)
    			{
    				rps.getChannel(0).sendMessage(rps.getPlayer(0).getName()+" wins!").queue();
    				rps.getChannel(1).sendMessage(rps.getPlayer(0).getName()+" wins!").queue();
    			}				
    			else if (winner == 2)
    			{
    				rps.getChannel(0).sendMessage(rps.getPlayer(1).getName()+" wins!").queue();
    				rps.getChannel(1).sendMessage(rps.getPlayer(1).getName()+" wins!").queue();
    			}
    			else
    			{
    				rps.getChannel(0).sendMessage("Tie!").queue();
    				rps.getChannel(1).sendMessage("Tie!").queue();
    			}
    		}  
    	}
        return;
    }
    @Override
    public void onMessageReceived(MessageReceivedEvent evt)
    {
    	//Objects
    	User objUser = evt.getAuthor();
    	MessageChannel objMsgCh= evt.getChannel();
    	Message objMsg = evt.getMessage();
    	
    	//Commands
    	if (objMsg.getContentRaw().equalsIgnoreCase(Ref.prefix+"hi"))
    	{
    		objMsgCh.sendMessage(objUser.getAsMention() + " You're gay!").queue();   		
    	}
    }
    
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {	
 	
    	if (event.getMessage().getContentRaw().startsWith("!poker"))
    	{
    		if (!pokerStarted && poker.getCounter() < 6 && event.getMessage().getContentRaw().equalsIgnoreCase("!poker join"))
    		{
    			if (!poker.isInRoom(event.getAuthor()))
    			{
        			poker.setPlayer(event.getAuthor(), poker.getCounter());
        			event.getChannel().sendMessage(poker.getPlayer(poker.getCounter()).getAsMention()+" has joined the game!").queue();
        			poker.addCounter();
    			}
    			else
    			{
    				event.getChannel().sendMessage(event.getAuthor().getAsMention()+", you are already in the room!").queue();
    			}
    		}	
    		else if (!pokerStarted && poker.getCounter() >= 6 && event.getMessage().getContentRaw().equalsIgnoreCase("!poker join"))
    		{
    			event.getChannel().sendMessage("There is no more room!").queue();
    		}   		
    		else if (poker.getCounter() > 6 || event.getMessage().getContentRaw().equalsIgnoreCase("!poker start"))
    		{
    			if (!pokerStarted)
    			{
        			poker.setChannel(event.getChannel());
        			event.getChannel().sendMessage("Poker game is starting!").queue();
        			pokerStarted = true;
    				poker.init();
    				poker.turn();
    			}
    			else
    			{
    				event.getChannel().sendMessage("Invalid Poker Command!").queue();
    			}
    		}
    		else if (pokerStarted && event.getMessage().getContentRaw().equalsIgnoreCase("!poker raise"))
    		{
    			poker.turn(event.getAuthor(), event.getMessage().getContentRaw());
    		}
    		else if (pokerStarted && event.getMessage().getContentRaw().equalsIgnoreCase("!poker check"))
    		{
    			poker.turn(event.getAuthor(), event.getMessage().getContentRaw());
    		}
    		else if (pokerStarted && event.getMessage().getContentRaw().equalsIgnoreCase("!poker fold"))
    		{
    			poker.turn(event.getAuthor(), event.getMessage().getContentRaw());
    		}
    		
    		else if (!pokerStarted && event.getMessage().getContentRaw().equalsIgnoreCase("!poker leave"))
    		{
    			if (!poker.isInRoom(event.getAuthor()))
    				event.getChannel().sendMessage(event.getAuthor().getAsMention()+"You are not in the room!").queue();
    			else
    			{
    				poker.subCounter();
    				event.getChannel().sendMessage(poker.getPlayer(poker.getCounter()).getAsMention()+". You have left the room!").queue();
        			poker.setPlayer(null, poker.getCounter());
    			}
    			return;
    		}
    		else if (event.getMessage().getContentRaw().equalsIgnoreCase("!poker collect"))
    		{
    			
    		}
    		else
    		{
    			event.getChannel().sendMessage("Invalid Poker Command!").queue();
    		}  		  		
    	}
    	else
    		return;
    	
    }



    	
    
    /*@Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
    	if (!event.getMessage().getContentRaw().startsWith("!play")) return;
    	
        Guild guild = event.getGuild();      
        VoiceChannel myChannel = event.getMember().getVoiceState().getChannel();      
    	AudioManager audioManager = guild.getAudioManager();
   
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioPlayer player = playerManager.createPlayer();
    	
        AudioSendHandler h = new AudioPlayerSendHandler(player);
        audioManager.setSendingHandler(h);
        audioManager.openAudioConnection(myChannel);
       

        final TrackScheduler trackScheduler = new TrackScheduler();
        player.addListener(trackScheduler);
 
        playerManager.loadItem("https://www.youtube.com/watch?v=U9BwWKXjVaI", new AudioLoadResultHandler() {
        	  public void trackLoaded1(AudioTrack track) {
        	    trackScheduler.queue(track);
        	  }

        	  public void playlistLoaded1(AudioPlaylist playlist) {
        	    for (AudioTrack track : playlist.getTracks()) {
        	      trackScheduler.queue(track);
        	    }
        	  }

        	  public void noMatches() {
        	    // Notify the user that we've got nothing
        	  }

        	  public void loadFailed(FriendlyException throwable) {
        	    // Notify the user that everything exploded
        	  }

			public void playlistLoaded(AudioPlaylist arg0) {
				// TODO Auto-generated method stub
				
			}

			public void trackLoaded(AudioTrack arg0) {
				// TODO Auto-generated method stub
				
			}
        	});
      
        	player.playTrack(player.getPlayingTrack());
    }*/
}
