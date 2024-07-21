package hundun.militarychess.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import hundun.militarychess.ui.MilitaryChessGame;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    static int viewportWidth = 1080;
    static int viewportHeight = 720;
    public static void main(String[] args) {
        MilitaryChessGame game = new MilitaryChessGame(viewportWidth, viewportHeight, new PreferencesSaveTool("militarychess-desktop-save.xml"));
        new Lwjgl3Application(game, getDefaultConfiguration(game));
    }


    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration(MilitaryChessGame game) {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("MilitaryChess");
        configuration.useVsync(true);
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate);
        configuration.setWindowedMode(viewportWidth, viewportHeight);
        return configuration;
    }
}
