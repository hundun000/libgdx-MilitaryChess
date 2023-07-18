package hundun.militarychess.ui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import de.eskalon.commons.screen.transition.impl.BlendingTransition;
import hundun.gdxgame.corelib.base.BaseHundunScreen;
import hundun.gdxgame.corelib.base.util.TextureFactory;
import hundun.gdxgame.gamelib.base.util.JavaFeatureForGwt;
import hundun.militarychess.logic.LogicContext.CrossScreenDataPackage;
import hundun.militarychess.logic.LogicContext.PlayerMode;
import hundun.militarychess.ui.MilitaryChessGame;

import java.util.HashMap;
import java.util.Map;


public class PrepareScreen extends BaseHundunScreen<MilitaryChessGame, Void> {

    Image backImage;

    public PrepareScreen(MilitaryChessGame game) {
        super(game, game.getSharedViewport());

        Label titleLabel = new Label(
            JavaFeatureForGwt.stringFormat("     %s     ", "军旗游戏"),
            game.getMainSkin());
        titleLabel.setFontScale(1.5f);

        this.backImage = new Image(new TextureRegionDrawable(new TextureRegion(TextureFactory.getSimpleBoardBackground())));



    }

    @Override
    protected void create() {
        backImage.setFillParent(true);
        backUiStage.addActor(backImage);

        Map<CheckBox, PlayerMode> playerModeCheckBoxMap = new HashMap<>();
        playerModeCheckBoxMap.put(new CheckBox(PlayerMode.PVP.getChinese(), game.getMainSkin()), PlayerMode.PVP);
        playerModeCheckBoxMap.put(new CheckBox(PlayerMode.PVC.getChinese(), game.getMainSkin()), PlayerMode.PVC);
        ButtonGroup<CheckBox> playerModeButtonGroup = new ButtonGroup<>();
        playerModeButtonGroup.setMaxCheckCount(1);
        playerModeButtonGroup.setUncheckLast(true);
        playerModeCheckBoxMap.keySet().forEach(it -> {
            playerModeButtonGroup.add(it);
            uiRootTable.add(it);
        });
        uiRootTable.row();

        TextButton buttonNewGame = new TextButton("开始", game.getMainSkin());
        buttonNewGame.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                CrossScreenDataPackage crossScreenDataPackage = game.getLogicContext().getCrossScreenDataPackage();
                crossScreenDataPackage.setPlayerMode(playerModeCheckBoxMap.get(playerModeButtonGroup.getChecked()));
                game.getScreenManager().pushScreen(PlayScreen.class.getSimpleName(), BlendingTransition.class.getSimpleName());
            }
        });
        uiRootTable.add(buttonNewGame);
    }

    @Override
    public void show() {
        super.show();
        //addInputProcessor(uiStage);
        Gdx.input.setInputProcessor(uiStage);
        game.getBatch().setProjectionMatrix(uiStage.getViewport().getCamera().combined);


    }

    @Override
    public void dispose() {

    }



}
