package fr.nemixcraft.launcher;

import static fr.theshark34.swinger.Swinger.getResource;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import fr.theshark34.openauth.AuthenticationException;
import fr.theshark34.openlauncherlib.launcher.util.UsernameSaver;
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
	
	private SColoredBar progressBar	= new SColoredBar(new Color(255,255,255, 100), new Color(255,255,255, 255));
	private JLabel infoLabel = new JLabel("Clique sur jouer !", SwingConstants.CENTER);
	private JLabel vertionlabel = new JLabel("Launcher version 1.0.2");
	
	
	
	public LauncherPanel() {
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
		
		
		startRPC();
		
		System.out.println("Lancement du RPC de Discord");
	}
	
	@Override
	public void onEvent(SwingerEvent e) {
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
	
	public void startRPC() {
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
	        presence.partySize = 1;
	        presence.partyMax = 100;
	        presence.spectateSecret = "";
	        presence.joinSecret = "";
	        lib.Discord_UpdatePresence(presence);
	        // in a worker thread
	        new Thread(() -> {
	            while (!Thread.currentThread().isInterrupted()) {
	                lib.Discord_RunCallbacks();
	                try {
	                    Thread.sleep(2000);
	                } catch (InterruptedException ignored) {}
	            }
	        }, "RPC-Callback-Handler").start();
    }
	
	
	
	public static class Main {
	    
	}
}
