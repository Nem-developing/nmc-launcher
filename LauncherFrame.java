package fr.nemixcraft.launcher;


import javax.swing.JFrame;

import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.util.WindowMover;

@SuppressWarnings("serial")
public class LauncherFrame extends JFrame{

	private static LauncherFrame instance;
	private LauncherPanel launcherPanel;
	
	public LauncherFrame() {
		this.setTitle("Nemixcraft Launcher");
		this.setSize(874, 561);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setUndecorated(true);
		this.setIconImage(Swinger.getResource("icon.png"));
		this.setContentPane(launcherPanel = new LauncherPanel ());
		
		WindowMover mover = new WindowMover(this); 
		this.addMouseListener(mover);
		this.addMouseMotionListener(mover);
		
		
		this.setVisible(true);
	}
	
	
	public static void main(String[] args) {
		Swinger.setSystemLookNFeel();
		Swinger.setResourcePath("/fr/nemixcraft/launcher/resources/");
		Launcher.SC_CRASH_DIR.mkdirs();
		instance = new LauncherFrame();
	}
	
	public static LauncherFrame getInstance() {
		return instance;
	}

	public LauncherPanel getLauncherPanel() {
		return this.launcherPanel;
	}
}
 
