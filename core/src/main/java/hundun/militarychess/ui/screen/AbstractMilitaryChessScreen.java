package hundun.militarychess.ui.screen;

import com.badlogic.gdx.utils.viewport.Viewport;

import hundun.gdxgame.corelib.base.BaseHundunScreen;
import hundun.militarychess.ui.MilitaryChessGame;

public abstract class AbstractMilitaryChessScreen extends BaseHundunScreen<MilitaryChessGame, Void> {



    public AbstractMilitaryChessScreen(MilitaryChessGame game, Viewport sharedViewport) {
        super(game, sharedViewport);
    }
    protected abstract void updateUIAfterRoomChanged();





}
