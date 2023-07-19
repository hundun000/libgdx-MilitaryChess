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
import hundun.militarychess.logic.LogicContext.ChessShowMode;
import hundun.militarychess.logic.LogicContext.ChessState;
import hundun.militarychess.logic.LogicContext.CrossScreenDataPackage;
import hundun.militarychess.logic.LogicContext.PlayerMode;
import hundun.militarychess.logic.data.ArmyRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData.ChessSide;
import hundun.militarychess.ui.MilitaryChessGame;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
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

        Map<CheckBox, PlayerMode> playerModeCheckBoxMap = new LinkedHashMap<>();
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

        Map<CheckBox, ChessShowMode> chessShowModeCheckBoxMap = new LinkedHashMap<>();
        chessShowModeCheckBoxMap.put(new CheckBox(ChessShowMode.MING_QI.getChinese(), game.getMainSkin()), ChessShowMode.MING_QI);
        chessShowModeCheckBoxMap.put(new CheckBox(ChessShowMode.AN_QI.getChinese(), game.getMainSkin()), ChessShowMode.AN_QI);
        ButtonGroup<CheckBox> chessShowModeButtonGroup = new ButtonGroup<>();
        chessShowModeButtonGroup.setMaxCheckCount(1);
        chessShowModeButtonGroup.setUncheckLast(true);
        chessShowModeCheckBoxMap.keySet().forEach(it -> {
            chessShowModeButtonGroup.add(it);
            uiRootTable.add(it);
        });
        uiRootTable.row();

        TextButton buttonNewGame = new TextButton("开始", game.getMainSkin());
        buttonNewGame.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                var playerMode = playerModeCheckBoxMap.get(playerModeButtonGroup.getChecked());
                var chessShowMode = chessShowModeCheckBoxMap.get(chessShowModeButtonGroup.getChecked());
                var crossScreenDataPackage = CrossScreenDataPackage.builder()
                    .game(game)
                    .playerMode(playerMode)
                    .chessShowMode(chessShowMode)
                    .currentChessShowSides(new HashSet<>())
                    .pvcPlayerSide(playerMode == PlayerMode.PVC ? ChessSide.FIRST_SIDE : null)
                    .currentSide(ChessSide.FIRST_SIDE)
                    .currentState(ChessState.WAIT_SELECT_FROM)
                    .armyMap(Map.of(
                        ChessSide.FIRST_SIDE,
                        ArmyRuntimeData.builder()
                            .chessRuntimeDataList(ChessRuntimeData.fromCodes(
                                "abccddeeffggghhhiiijjkklj",
                                game.getScreenContext().getLayoutConst(),
                                ChessSide.FIRST_SIDE))
                            .build(),
                        ChessSide.SECOND_SIDE,
                        ArmyRuntimeData.builder()
                            .chessRuntimeDataList(ChessRuntimeData.fromCodes(
                                "jlkkijiiihhhgggffeeddccba",
                                game.getScreenContext().getLayoutConst(),
                                ChessSide.SECOND_SIDE))
                            .build()
                    ))
                    .build();
                crossScreenDataPackage.update();
                game.getLogicContext().setCrossScreenDataPackage(crossScreenDataPackage);
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
