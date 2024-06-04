package hundun.militarychess.ui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import de.eskalon.commons.screen.transition.impl.BlendingTransition;
import hundun.gdxgame.corelib.base.BaseHundunScreen;
import hundun.gdxgame.corelib.base.util.TextureFactory;
import hundun.militarychess.logic.manager.CrossScreenDataManager;
import hundun.militarychess.logic.LogicContext.ChessShowMode;
import hundun.militarychess.logic.LogicContext.ChessState;
import hundun.militarychess.logic.LogicContext.PlayerMode;
import hundun.militarychess.logic.chess.ChessType;
import hundun.militarychess.logic.chess.GridPosition;
import hundun.militarychess.logic.chess.LogicFlag;
import hundun.militarychess.logic.chess.lose.ZhanQiLoseChecker;
import hundun.militarychess.logic.data.ArmyRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData.ChessBattleStatus;
import hundun.militarychess.logic.data.ChessRuntimeData.ChessSide;
import hundun.militarychess.logic.StageConfig;
import hundun.militarychess.logic.map.tile.TileBuilder;
import hundun.militarychess.ui.MilitaryChessGame;
import org.apache.commons.math3.util.Pair;

import java.util.List;
import java.util.*;


public class MainPrepareScreen extends BaseHundunScreen<MilitaryChessGame, Void> {

    Image backImage;

    public MainPrepareScreen(MilitaryChessGame game) {
        super(game, game.getSharedViewport());

        this.backImage = new Image(new TextureRegionDrawable(new TextureRegion(TextureFactory.getSimpleBoardBackground())));



    }

    @Override
    protected void create() {
        backImage.setFillParent(true);
        backUiStage.addActor(backImage);



        TextButton buttonZhanqi = new TextButton("战棋模式", game.getMainSkin());
        buttonZhanqi.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                var playerMode = PlayerMode.PVC;
                var chessShowMode = ChessShowMode.MING_QI;
                List<Pair<GridPosition, GridPosition>> extraAddLogicNeighborPair = List.of(
                );
                List<Pair<GridPosition, GridPosition>> extraRemoveLogicNeighborPair = List.of(
                );


                List<TileBuilder> tileBuilders =  JunqiPrepareScreen.junqiTileBuilders();
                tileBuilders.removeIf(it -> {
                    int posX = it.getPosition().getX();
                    int posY = it.getPosition().getY();
                    if (posX > 3) {
                        return true;
                    }
                    if (posY < 4 || posY > 6) {
                        return true;
                    }
                    return false;
                });

                Map<ChessSide, ArmyRuntimeData> armyMap = Map.of(
                    ChessSide.RED_SIDE,
                    ArmyRuntimeData.builder()
                        .chessRuntimeDataList(List.of(
                            fromCode(
                                0,
                                5,
                                "b",
                                game.getScreenContext().getLayoutConst(),
                                ChessSide.RED_SIDE
                            )
                        ))
                        .build(),
                    ChessSide.BLUE_SIDE,
                    ArmyRuntimeData.builder()
                        .chessRuntimeDataList(List.of(
                            fromCode(
                                1,
                                5,
                                "a",
                                game.getScreenContext().getLayoutConst(),
                                ChessSide.BLUE_SIDE
                            )
                        ))
                        .build()
                );

                StageConfig stageConfig = StageConfig.builder()
                    .playerMode(playerMode)
                    .chessShowMode(chessShowMode)
                    .pvcPlayerSide(ChessSide.RED_SIDE)
                    .currentSide(ChessSide.RED_SIDE)
                    .loseChecker(new ZhanQiLoseChecker())
                    .armyMap(armyMap)
                    .tileBuilders(tileBuilders)
                    .extraAddLogicNeighborPair(extraAddLogicNeighborPair)
                    .extraRemoveLogicNeighborPair(extraRemoveLogicNeighborPair)
                    .build();
                game.getLogicContext().prepareDone(stageConfig);
                game.getScreenManager().pushScreen(PlayScreen.class.getSimpleName(), BlendingTransition.class.getSimpleName());
            }
        });
        uiRootTable.add(buttonZhanqi);


        TextButton buttonJunqi = new TextButton("军棋模式", game.getMainSkin());
        buttonJunqi.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.getScreenManager().pushScreen(JunqiPrepareScreen.class.getSimpleName(), BlendingTransition.class.getSimpleName());
            }
        });
        uiRootTable.add(buttonJunqi);

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


    @Override
    public void onLogicFrame() {

    }

    public static ChessRuntimeData fromCode(
        int col,
        int row,
        String code,
        LayoutConst layoutConst,
        ChessSide chessSide
    ) {


        ChessRuntimeData chessRuntimeData;
        ChessType chessType;
        final String id = UUID.randomUUID().toString();

        // 放置棋子
        chessType = ChessType.fromCode(code);
        chessRuntimeData = ChessRuntimeData.builder()
            .id(id)
            .pos(new GridPosition(col, row))
            .chessType(chessType)
            .chessSide(chessSide)
            .build();
        chessRuntimeData.updateUiPos(layoutConst);
        chessRuntimeData.setChessBattleStatus(ChessBattleStatus.createStatus(chessRuntimeData.getChessType()));


        return chessRuntimeData;
    }
}
