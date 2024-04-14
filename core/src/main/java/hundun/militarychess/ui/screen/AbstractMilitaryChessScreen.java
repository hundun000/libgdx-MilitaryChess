package hundun.militarychess.ui.screen;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

import hundun.gdxgame.corelib.base.BaseHundunScreen;
import hundun.militarychess.ui.MilitaryChessGame;
import hundun.militarychess.ui.other.CameraDataPackage;
import hundun.militarychess.ui.screen.shared.ChessVM;
import hundun.militarychess.ui.screen.shared.DeskAreaVM;

public abstract class AbstractMilitaryChessScreen extends BaseHundunScreen<MilitaryChessGame, Void> {



    public AbstractMilitaryChessScreen(MilitaryChessGame game, Viewport sharedViewport) {
        super(game, sharedViewport);
    }
    protected abstract void updateUIAfterRoomChanged();





}
