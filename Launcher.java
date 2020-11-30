package fr.nemixcraft.launcher;

import java.io.File;
import java.io.IOException;

import fr.theshark34.openauth.AuthPoints;
import fr.theshark34.openauth.AuthenticationException;
import fr.theshark34.openauth.Authenticator;
import fr.theshark34.openauth.model.AuthAgent;
import fr.theshark34.openauth.model.response.AuthResponse;
import fr.theshark34.openlauncherlib.launcher.AuthInfos;
import fr.theshark34.openlauncherlib.launcher.GameFolder;
import fr.theshark34.openlauncherlib.launcher.GameInfos;
import fr.theshark34.openlauncherlib.launcher.GameLauncher;
import fr.theshark34.openlauncherlib.launcher.GameTweak;
import fr.theshark34.openlauncherlib.launcher.GameType;
import fr.theshark34.openlauncherlib.launcher.GameVersion;
import fr.theshark34.openlauncherlib.util.ErrorUtil;
import fr.theshark34.supdate.BarAPI;
import fr.theshark34.supdate.SUpdate;
import fr.theshark34.supdate.application.integrated.FileDeleter;
import fr.theshark34.swinger.Swinger;

public class Launcher {
	public static final GameVersion SC_VERSION = new GameVersion("1.7.10", GameType.V1_7_10);
	public static final GameInfos SC_INFOS = new GameInfos("Nemixcraft", SC_VERSION, true, new GameTweak[] {GameTweak.FORGE});
	public static final File SC_DIR = SC_INFOS.getGameDir();
	public static final File SC_CRASH_DIR = new File(SC_DIR, "CRASH");
	
	private static AuthInfos authInfos;
	private static Thread updateThread;
	
	
	private static ErrorUtil errorUtil = new ErrorUtil(SC_CRASH_DIR);
	
	
	public static void auth(String username, String password) throws AuthenticationException {
		Authenticator authenticator = new  Authenticator(Authenticator.MOJANG_AUTH_URL, AuthPoints.NORMAL_AUTH_POINTS);
		AuthResponse responce = authenticator.authenticate(AuthAgent.MINECRAFT, username, password, "");
		authInfos = new AuthInfos(responce.getSelectedProfile().getName(), responce.getAccessToken(), responce.getSelectedProfile().getId());
	}
	
	public static void update() throws Exception {
		SUpdate su = new SUpdate("https://nemixcraft.com/minecraft/launcher/", SC_DIR);
		su.getServerRequester().setRewriteEnabled(true);
		su.addApplication(new FileDeleter());

		
		updateThread= new Thread() {
			private int val;
			private int max;
			
			
			@Override
			public void run() {
				while (!this.isInterrupted()) {
					if(BarAPI.getNumberOfFileToDownload() == 0) {
						LauncherFrame.getInstance().getLauncherPanel().setInfoText("Verification des fichiers");
						continue;
					}
					val = (int) (BarAPI.getNumberOfTotalDownloadedBytes() / 1000);
					max = (int) (BarAPI.getNumberOfTotalBytesToDownload() / 1000);			
					
					LauncherFrame.getInstance().getLauncherPanel().progressBar().setMaximum(max);
					LauncherFrame.getInstance().getLauncherPanel().progressBar().setValue(val);
					
					LauncherFrame.getInstance().getLauncherPanel().setInfoText("Téléchargement " + BarAPI.getNumberOfDownloadedFiles() + "/" + BarAPI.getNumberOfFileToDownload() + " " + Swinger.percentage(val, max) + "%");				
				}
			}
		};
		updateThread.start();
		su.start();
		updateThread.interrupt();
		
	}
	
	public static void launch() throws IOException{
		GameLauncher gameLauncher = new GameLauncher(SC_INFOS, GameFolder.BASIC, authInfos);
		Process p = gameLauncher.launch();
		try {
			Thread.sleep(5000);
		}catch (InterruptedException e) {
		}
		LauncherFrame.getInstance().setVisible(false);
		try {
			p.waitFor();
		}catch (InterruptedException e) {
		}
		System.exit(0);
	}
	
	
	
	public static void interruptTread() {
		updateThread.interrupt();
	}
	
	public static ErrorUtil getErrorUtil() {
		return errorUtil;
	}
	
}
