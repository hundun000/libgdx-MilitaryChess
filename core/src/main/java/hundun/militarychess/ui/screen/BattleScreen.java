package hundun.militarychess.ui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.eskalon.commons.screen.transition.impl.BlendingTransition;
import hundun.gdxgame.corelib.base.util.TextureFactory;
import hundun.gdxgame.gamelib.base.util.JavaFeatureForGwt;
import hundun.militarychess.logic.LogicContext.CrossScreenDataPackage;
import hundun.militarychess.logic.chess.ChessRule;
import hundun.militarychess.ui.MilitaryChessGame;
import hundun.militarychess.ui.other.CameraDataPackage;
import hundun.militarychess.ui.screen.shared.ChessVM;

public class BattleScreen extends AbstractMilitaryChessScreen {
    Image backImage;
    Label titleLabel;

    TextButton commitButton;

    public BattleScreen(MilitaryChessGame game) {
        super(game, game.getSharedViewport());


    }

    @Override
    public void onDeskClicked(ChessVM vm) {

    }

    @Override
    protected void updateUIAfterRoomChanged() {
        CrossScreenDataPackage crossScreenDataPackage = game.getLogicContext().getCrossScreenDataPackage();


    }

    @Override
    protected void create() {
        this.backImage = new Image(new TextureRegionDrawable(new TextureRegion(TextureFactory.getSimpleBoardBackground())));
        backImage.setFillParent(true);
        backUiStage.addActor(backImage);

        titleLabel = new Label(
            "",
            game.getMainSkin());
        titleLabel.setFontScale(1.5f);
        uiRootTable.add(titleLabel)
            .row();


        this.commitButton = new TextButton("确认", this.getGame().getMainSkin());
        this.commitButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CrossScreenDataPackage crossScreenDataPackage = game.getLogicContext().getCrossScreenDataPackage();
                ChessRule.fight(
                    crossScreenDataPackage.getBattleFromChess(),
                    crossScreenDataPackage.getBattleToChess()
                );
                crossScreenDataPackage.afterFight();
                game.getScreenManager().pushScreen(PlayScreen.class.getSimpleName(), BlendingTransition.class.getSimpleName());
            }
        });
        uiRootTable.add(commitButton).row();
    }

    @Override
    public void dispose() {

    }

    @Override
    public void onLogicFrame() {

    }

    @Override
    public void show() {
        super.show();

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(popupUiStage);
        multiplexer.addProcessor(uiStage);
        Gdx.input.setInputProcessor(multiplexer);

        //Gdx.input.setInputProcessor(uiStage);
        //game.getBatch().setProjectionMatrix(uiStage.getViewport().getCamera().combined);

        updateUIAfterRoomChanged();


        Gdx.app.log(this.getClass().getSimpleName(), "show done");
    }
}
