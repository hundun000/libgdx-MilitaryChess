package hundun.militarychess.ui.screen;

import de.eskalon.commons.screen.transition.impl.BlendingTransition;
import hundun.militarychess.ui.MilitaryChessGame;
import lombok.Getter;

/**
 * @author hundun
 * Created on 2021/11/02
 */
@Getter
public class ScreenContext {
    PlayScreen playScreen;
    MyMenuScreen menuScreen;

    LayoutConst layoutConst;

    public ScreenContext(MilitaryChessGame game) {

    }

    public void lazyInit(MilitaryChessGame game) {
        this.layoutConst = new LayoutConst();
        this.menuScreen = new MyMenuScreen(game);
        this.playScreen = new PlayScreen(game);

        game.getScreenManager().addScreen(menuScreen.getClass().getSimpleName(), menuScreen);
        game.getScreenManager().addScreen(playScreen.getClass().getSimpleName(), playScreen);

        BlendingTransition blendingTransition = new BlendingTransition(game.getBatch(), 1F);
        game.getScreenManager().addScreenTransition(BlendingTransition.class.getSimpleName(), blendingTransition);


    }
}
