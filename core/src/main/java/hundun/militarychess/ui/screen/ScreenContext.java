package hundun.militarychess.ui.screen;

import com.sun.tools.javac.Main;
import de.eskalon.commons.screen.transition.impl.BlendingTransition;
import hundun.militarychess.ui.MilitaryChessGame;
import lombok.Getter;


@Getter
public class ScreenContext {
    PlayScreen playScreen;
    BattleScreen battleScreen;
    JunqiPrepareScreen junqiPrepareScreen;
    MainPrepareScreen mainPrepareScreen;
    MyMenuScreen menuScreen;

    LayoutConst layoutConst;

    public ScreenContext(MilitaryChessGame game) {

    }

    public void lazyInit(MilitaryChessGame game) {
        this.layoutConst = new LayoutConst();
        this.menuScreen = new MyMenuScreen(game);
        this.playScreen = new PlayScreen(game);
        this.battleScreen = new BattleScreen(game);
        this.junqiPrepareScreen = new JunqiPrepareScreen(game);
        this.mainPrepareScreen = new MainPrepareScreen(game);

        game.getScreenManager().addScreen(menuScreen.getClass().getSimpleName(), menuScreen);
        game.getScreenManager().addScreen(playScreen.getClass().getSimpleName(), playScreen);
        game.getScreenManager().addScreen(battleScreen.getClass().getSimpleName(), battleScreen);
        game.getScreenManager().addScreen(junqiPrepareScreen.getClass().getSimpleName(), junqiPrepareScreen);
        game.getScreenManager().addScreen(mainPrepareScreen.getClass().getSimpleName(), mainPrepareScreen);

        BlendingTransition blendingTransition = new BlendingTransition(game.getBatch(), 1F);
        game.getScreenManager().addScreenTransition(BlendingTransition.class.getSimpleName(), blendingTransition);


    }
}
