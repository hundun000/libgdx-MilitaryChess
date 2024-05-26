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
import hundun.militarychess.logic.CrossScreenDataPackage;
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
import hundun.militarychess.logic.map.StageConfig;
import hundun.militarychess.logic.map.tile.TileBuilder;
import hundun.militarychess.ui.MilitaryChessGame;

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
                var crossScreenDataPackage = CrossScreenDataPackage.builder()
                    .game(game)
                    .playerMode(playerMode)
                    .chessShowMode(chessShowMode)
                    .currentChessShowSides(new HashSet<>())
                    .pvcPlayerSide(ChessSide.RED_SIDE)
                    .currentSide(ChessSide.RED_SIDE)
                    .currentState(ChessState.WAIT_SELECT_FROM)
                    .build();



                List<GridPosition> xingyingPositions = List.of(
                    new GridPosition(1, 8),
                    new GridPosition(3, 8),
                    new GridPosition(2, 9),
                    new GridPosition(1, 10),
                    new GridPosition(3, 10),
                    new GridPosition(1, 2),
                    new GridPosition(3, 2),
                    new GridPosition(2, 3),
                    new GridPosition(1, 4),
                    new GridPosition(3, 4)
                );
                List<GridPosition> noDiagonalNeighborPositions = List.of(
                    new GridPosition(0, 8),
                    new GridPosition(2, 8),
                    new GridPosition(4, 8),

                    new GridPosition(1, 9),
                    new GridPosition(3, 9),

                    new GridPosition(0, 10),
                    new GridPosition(2, 10),
                    new GridPosition(4, 10),

                    new GridPosition(0, 2),
                    new GridPosition(2, 2),
                    new GridPosition(4, 2),

                    new GridPosition(1, 3),
                    new GridPosition(3, 3),

                    new GridPosition(0, 4),
                    new GridPosition(2, 4),
                    new GridPosition(4, 4)
                );
                List<TileBuilder> tileBuilders = new ArrayList<>();
                for (int j = 0; j < 13; j++) {
                    if (j == 6) {
                        tileBuilders.add(TileBuilder.create(0, j, false, LogicFlag.NO_STOP));
                        tileBuilders.add(TileBuilder.create(1, j, false, LogicFlag.NO_STOP, LogicFlag.NO_PASS));
                        tileBuilders.add(TileBuilder.create(2, j, false, LogicFlag.NO_STOP));
                        tileBuilders.add(TileBuilder.create(3, j, false, LogicFlag.NO_STOP, LogicFlag.NO_PASS));
                        tileBuilders.add(TileBuilder.create(4, j, false, LogicFlag.NO_STOP));
                    } else {

                        for (int i = 0; i < 5; i++) {
                            GridPosition pos = GridPosition.builder()
                                .x(i)
                                .y(j)
                                .build();
                            if (xingyingPositions.contains(pos)) {
                                tileBuilders.add(TileBuilder.create(i, j, true, LogicFlag.XING_YING));
                            } else {
                                boolean hasDiagonalNeighbor = j > 0 && j < 12;
                                if (noDiagonalNeighborPositions.contains(pos)) {
                                    hasDiagonalNeighbor = false;
                                }
                                tileBuilders.add(TileBuilder.create(i, j, hasDiagonalNeighbor));
                            }
                        }
                    }
                }



                Map<ChessSide, ArmyRuntimeData> armyMap = Map.of(
                    ChessSide.RED_SIDE,
                    ArmyRuntimeData.builder()
                        .chessRuntimeDataList(List.of(
                            fromCode(
                                0,
                                5,
                                "a",
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
                                "b",
                                game.getScreenContext().getLayoutConst(),
                                ChessSide.BLUE_SIDE
                            )
                        ))
                        .build()
                );

                StageConfig stageConfig = StageConfig.builder()
                    .loseChecker(new ZhanQiLoseChecker())
                    .armyMap(armyMap)
                    .tileBuilders(tileBuilders)
                    .build();
                game.getLogicContext().prepareDone(crossScreenDataPackage, stageConfig);
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
