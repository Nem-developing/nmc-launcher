package fr.nemixcraft.launcher;

import static fr.theshark34.swinger.Swinger.getResource;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.JauntException;
import com.jaunt.UserAgent;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import fr.theshark34.openauth.AuthenticationException;
import fr.theshark34.openlauncherlib.launcher.util.UsernameSaver;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.colored.SColoredBar;
import fr.theshark34.swinger.event.SwingerEvent;
import fr.theshark34.swinger.event.SwingerEventListener;
import fr.theshark34.swinger.textured.STexturedButton;


@SuppressWarnings("serial")
public class LauncherPanel extends JPanel implements SwingerEventListener{
	
	private Image background = getResource("background.png"); 
	
	private UsernameSaver saver = new UsernameSaver(Launcher.SC_INFOS); 
	
	private JTextField usernameField = new JTextField(saver.getUsername("")); 
	private JTextField passwordField = new JPasswordField(); 
	
	private STexturedButton playButton = new STexturedButton(getResource("play.png"));
	private STexturedButton exitButton = new STexturedButton(getResource("exit.png"));
	private STexturedButton hideButton = new STexturedButton(getResource("hide.png"));
	private STexturedButton dicordlogo = new STexturedButton(Swinger.getResource("discord-logo.png"));
	private STexturedButton nmclogo = new STexturedButton(Swinger.getResource("icon-tiny.png"));
	private STexturedButton servstate = new STexturedButton(Swinger.getResource("servstate.png"));
	private STexturedButton playapp = new STexturedButton(Swinger.getResource("google-app.png"));

	
	private SColoredBar progressBar	= new SColoredBar(new Color(255,255,255, 100), new Color(255,255,255, 255));
	private JLabel infoLabel = new JLabel("   Nemixcraft te souhaite la bienvenue !", SwingConstants.CENTER);
	private JLabel vertionlabel = new JLabel("Launcher version 1.0.5");
	
	
	
	public LauncherPanel(){
		this.setLayout(null);
		
		usernameField.setForeground(Color.WHITE);
		usernameField.setFont(usernameField.getFont().deriveFont(20F));
		usernameField.setCaretColor(Color.WHITE);
		usernameField.setOpaque(false);
		usernameField.setBorder(null);
		usernameField.setBounds(35, 364, 220, 42);
		this.add(usernameField);
		
		
		passwordField.setForeground(Color.WHITE);
		passwordField.setFont(passwordField.getFont().deriveFont(20F));
		passwordField.setCaretColor(Color.WHITE);
		passwordField.setOpaque(false);
		passwordField.setBorder(null);
		passwordField.setBounds(35, 496, 220, 42);
		this.add(passwordField);
		
		playButton.setBounds(639, 417);
		playButton.addEventListener(this);
		this.add(playButton);
		
		
		exitButton.setBounds(820, 20);
		exitButton.addEventListener(this);
		this.add(exitButton);
		
		
		hideButton.setBounds(775, 20);
		hideButton.addEventListener(this);
		this.add(hideButton);
		
		progressBar.setBounds(7, 545, 858, 5);
		this.add(progressBar);
		
		infoLabel.setForeground(Color.WHITE);
		infoLabel.setFont(infoLabel.getFont().deriveFont(20F));
		infoLabel.setBounds(7, 500, 858, 30);
		this.add(infoLabel);
		
		vertionlabel.setForeground(Color.WHITE);
		vertionlabel.setFont(infoLabel.getFont().deriveFont(15F));
		vertionlabel.setBounds(10, 6, 300, 30);
		this.add(vertionlabel);
		
		dicordlogo.setBounds(800, 120);
		dicordlogo.addEventListener(this);
		this.add(dicordlogo);
		
		servstate.setBounds(800, 180);
		servstate.addEventListener(this);
		this.add(servstate);
		
		nmclogo.setBounds(800, 250);
		nmclogo.addEventListener(this);
		this.add(nmclogo);
		
		playapp.setBounds(700, 330);
		playapp.addEventListener(this);
		this.add(playapp);
		
		
		
		
		
		
		 
		
		
		
		startRPC();
		System.out.println("Lancement du RPC de Discord");
		
		

	}
	
	@Override
	public void onEvent(SwingerEvent e){
		if(e.getSource() == playButton) {
			setFieldsEnabled(false);
			
			if(usernameField.getText().replace(" ", "").length() == 0 || passwordField.getText().length() == 0) {
				JOptionPane.showMessageDialog(this, "Erreur, veuillez entrer un pseudo et un mot de passe valides", "Erreur", JOptionPane.ERROR_MESSAGE);
				setFieldsEnabled(true);
				return;
			}
			

		
		
		Thread t =  new Thread() {
			@Override 
			public void run() {
				try {
					Launcher.auth(usernameField.getText(), passwordField.getText());
				} catch (AuthenticationException e) {
					JOptionPane.showMessageDialog(LauncherPanel.this, "Erreur, Impossible de se connecter : " + e.getErrorModel().getErrorMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
					setFieldsEnabled(true);
					return;
				}
				
				
				
				saver.setUsername(usernameField.getText());
				
				
				
				try	{
					Launcher.update();
				} catch (Exception e) {
					Launcher.interruptTread();
					Launcher.getErrorUtil().catchError(e, "Impossible de mettre a jour NemixCraft !");
					setFieldsEnabled(true);
					return;
				}
				
				try	{
					Launcher.launch();
				} catch (IOException e) 
				{
					Launcher.getErrorUtil().catchError(e, "Impossible de lancer NemixCraft !");
					setFieldsEnabled(true);
				}
				
				
				
				System.out.println("connection réussie");
				
			}
		};
		
		
		t.start();
		}
		 else if (e.getSource() == exitButton)  
			System.exit(0);
		 else if (e.getSource() == hideButton) 
			LauncherFrame.getInstance().setState(JFrame.ICONIFIED);
		
		if(e.getSource() == dicordlogo)
            try {
                Desktop.getDesktop().browse(new URI("https://discord.gg/Mm5jShV"));
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (URISyntaxException e1) {
                e1.printStackTrace();
            }
		if(e.getSource() == nmclogo)
            try {
                Desktop.getDesktop().browse(new URI("https://nemixcraft.com"));
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (URISyntaxException e1) {
                e1.printStackTrace();
            }
		if(e.getSource() == servstate)
            try {
                Desktop.getDesktop().browse(new URI("https://www.nemixcraft.com/etats-des-serveurs"));
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (URISyntaxException e1) {
                e1.printStackTrace();
            }
		if(e.getSource() == playapp)
            try {
                Desktop.getDesktop().browse(new URI("https://play.google.com/store/apps/details?id=com.nemixcraft.nemixcraft"));
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (URISyntaxException e1) {
                e1.printStackTrace();
            }
		
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g); 
		
		g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), this);
	}
	
	
	private void setFieldsEnabled (boolean enabled) {
		usernameField.setEnabled(enabled);
		passwordField.setEnabled(enabled);
		playButton.setEnabled(enabled);
	}
	
	public SColoredBar progressBar() {
		return progressBar;
	}
	
	public void setInfoText(String text) {
		infoLabel.setText(text);
		
	}
	
	public void startRPC(){
		
		
		
		 DiscordRPC lib = DiscordRPC.INSTANCE;
	        String applicationId = "";
	        String steamId = "";
	        DiscordEventHandlers handlers = new DiscordEventHandlers();
	        handlers.ready = (user) -> System.out.println("Ready!");
	        lib.Discord_Initialize(applicationId, handlers, true, steamId);
	        DiscordRichPresence presence = new DiscordRichPresence();
	        presence.startTimestamp = System.currentTimeMillis() / 1000; // epoch second
	        presence.state = "Skyblock - Survie - Créatif";
	        presence.details = "PvP Faction - OP Prison";
	        presence.largeImageKey = "bannier";
	        presence.largeImageText = "Toi aussi rejoins l'aventure Nemixcraft ! Exclusivement sur nemixcraft.com.";
	        presence.smallImageKey = "logo";
	        presence.smallImageText = "Nemixcraft saura satisfaire toutes tes envies grâce à ses nombreux modes de jeu tel que le prison ou le faction !";
	        presence.partyId = "";
	        presence.partySize = 0;
	        presence.partyMax = 100;
	        presence.spectateSecret = "";
	        presence.joinSecret = "";
	        lib.Discord_UpdatePresence(presence);
	        
	        new Thread(() -> {
	            while (!Thread.currentThread().isInterrupted()) {
	            	
	            	
	            	String total = (String) webparse();
	    	        int nombreplayeursenligne = Integer.parseInt(total);
	            	presence.partySize = nombreplayeursenligne;
	    	        
	    	        
	            	lib.Discord_UpdatePresence(presence);
	            	lib.Discord_RunCallbacks();
					System.out.println("--> Présence Discord actualisée");

	            	try {
	                    Thread.sleep(120000);
	        	        
	                } catch (InterruptedException ignored) {}
	            }
	        }, "RPC-Callback-Handler").start();
    }
	
	
	  public static Object webparse(){
		  String totalplayer = "";
		  
	    try{
	      UserAgent userAgent = new UserAgent();                       
	      userAgent.visit("https://www.nemixcraft.com/etats-des-serveurs");      
	      Element total = userAgent.doc.findFirst("<span id=\"totalplayer\">"); 
	      totalplayer = total.innerHTML();
	    }
	    catch(JauntException e){         
	      System.err.println(e);
	    }
		return totalplayer;
	  }
}
