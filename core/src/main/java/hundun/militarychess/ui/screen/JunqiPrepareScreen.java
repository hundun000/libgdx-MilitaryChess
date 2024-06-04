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
import hundun.militarychess.logic.chess.lose.JunQiLoseChecker;
import hundun.militarychess.logic.data.ArmyRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData.ChessBattleStatus;
import hundun.militarychess.logic.data.ChessRuntimeData.ChessSide;
import hundun.militarychess.logic.StageConfig;
import hundun.militarychess.logic.map.tile.TileBuilder;
import hundun.militarychess.ui.MilitaryChessGame;
import org.apache.commons.math3.util.Pair;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


public class JunqiPrepareScreen extends BaseHundunScreen<MilitaryChessGame, Void> {

    Image backImage;

    public JunqiPrepareScreen(MilitaryChessGame game) {
        super(game, game.getSharedViewport());

        this.backImage = new Image(new TextureRegionDrawable(new TextureRegion(TextureFactory.getSimpleBoardBackground())));



    }

    @Override
    protected void create() {
        backImage.setFillParent(true);
        backUiStage.addActor(backImage);

        HorizontalGroup playerModeHorizontalGroup = new HorizontalGroup();
        Map<CheckBox, PlayerMode> playerModeCheckBoxMap = new LinkedHashMap<>();
        playerModeCheckBoxMap.put(new CheckBox(PlayerMode.PVP.getChinese(), game.getMainSkin()), PlayerMode.PVP);
        playerModeCheckBoxMap.put(new CheckBox(PlayerMode.PVC.getChinese(), game.getMainSkin()), PlayerMode.PVC);
        ButtonGroup<CheckBox> playerModeButtonGroup = new ButtonGroup<>();
        playerModeButtonGroup.setMaxCheckCount(1);
        playerModeButtonGroup.setUncheckLast(true);
        playerModeCheckBoxMap.keySet().forEach(it -> {
            playerModeButtonGroup.add(it);
            playerModeHorizontalGroup.addActor(it);
        });
        uiRootTable.add(playerModeHorizontalGroup);
        uiRootTable.row();

        HorizontalGroup pvcPlayerSideHorizontalGroup = new HorizontalGroup();
        pvcPlayerSideHorizontalGroup.addActor(new Label("(仅人机对战时有效)", game.getMainSkin()));
        Map<CheckBox, Boolean> pvcPlayerSideCheckBoxMap = new LinkedHashMap<>();
        pvcPlayerSideCheckBoxMap.put(new CheckBox("先手", game.getMainSkin()), Boolean.TRUE);
        pvcPlayerSideCheckBoxMap.put(new CheckBox("后手", game.getMainSkin()), Boolean.FALSE);
        ButtonGroup<CheckBox> pvcPlayerSideButtonGroup = new ButtonGroup<>();
        pvcPlayerSideButtonGroup.setMaxCheckCount(1);
        pvcPlayerSideButtonGroup.setUncheckLast(true);
        pvcPlayerSideCheckBoxMap.keySet().forEach(it -> {
            pvcPlayerSideButtonGroup.add(it);
            pvcPlayerSideHorizontalGroup.addActor(it);
        });
        uiRootTable.add(pvcPlayerSideHorizontalGroup);
        uiRootTable.row();

        HorizontalGroup chessShowModeHorizontalGroup = new HorizontalGroup();
        Map<CheckBox, ChessShowMode> chessShowModeCheckBoxMap = new LinkedHashMap<>();
        chessShowModeCheckBoxMap.put(new CheckBox(ChessShowMode.MING_QI.getChinese(), game.getMainSkin()), ChessShowMode.MING_QI);
        chessShowModeCheckBoxMap.put(new CheckBox(ChessShowMode.AN_QI.getChinese(), game.getMainSkin()), ChessShowMode.AN_QI);
        ButtonGroup<CheckBox> chessShowModeButtonGroup = new ButtonGroup<>();
        chessShowModeButtonGroup.setMaxCheckCount(1);
        chessShowModeButtonGroup.setUncheckLast(true);
        chessShowModeCheckBoxMap.keySet().forEach(it -> {
            chessShowModeButtonGroup.add(it);
            chessShowModeHorizontalGroup.addActor(it);
        });
        uiRootTable.add(chessShowModeHorizontalGroup);
        uiRootTable.row();

        Table codesHorizontalGroup;
        codesHorizontalGroup = new Table();
        // jlkkijiiihhhgggffeeddccba
        TextField redSideTextField = new TextField("jlkkijiiihhhgggffeeddccba", game.getMainSkin());
        codesHorizontalGroup.add(new Label("红方：", game.getMainSkin()));
        codesHorizontalGroup.add(redSideTextField).width(400);
        uiRootTable.add(codesHorizontalGroup);
        uiRootTable.row();
        codesHorizontalGroup = new Table();
        // abccddeeffggghhhiiijjkklj
        TextField blueSideTextField = new TextField("abccddeeffggghhhiiijjkklj", game.getMainSkin());
        codesHorizontalGroup.add(new Label("蓝方：", game.getMainSkin()));
        codesHorizontalGroup.add(blueSideTextField).width(400);
        uiRootTable.add(codesHorizontalGroup);
        uiRootTable.row();

        TextButton buttonNewGame = new TextButton("开始", game.getMainSkin());
        buttonNewGame.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                var playerMode = playerModeCheckBoxMap.get(playerModeButtonGroup.getChecked());
                var chessShowMode = chessShowModeCheckBoxMap.get(chessShowModeButtonGroup.getChecked());
                var pvcPlayerAsFirst = pvcPlayerSideCheckBoxMap.get(pvcPlayerSideButtonGroup.getChecked());
                List<Pair<GridPosition, GridPosition>> extraAddLogicNeighborPair = List.of(
                    new Pair<>(new GridPosition(0, 6), new GridPosition(1, 7)),
                    new Pair<>(new GridPosition(2, 6), new GridPosition(1, 7)),
                    new Pair<>(new GridPosition(2, 6), new GridPosition(3, 7)),
                    new Pair<>(new GridPosition(4, 6), new GridPosition(3, 7)),

                    new Pair<>(new GridPosition(0, 5), new GridPosition(1, 4)),
                    new Pair<>(new GridPosition(2, 5), new GridPosition(1, 4)),
                    new Pair<>(new GridPosition(2, 5), new GridPosition(3, 4)),
                    new Pair<>(new GridPosition(4, 5), new GridPosition(3, 4))
                );
                List<Pair<GridPosition, GridPosition>> extraRemoveLogicNeighborPair = List.of(
                    new Pair<>(new GridPosition(1, 6), new GridPosition(1, 5)),
                    new Pair<>(new GridPosition(3, 6), new GridPosition(3, 5))
                );

                // first: left-down-node; second: right-up-node;
                List<TileBuilder> tileBuilders = junqiTileBuilders();
                List<GridPosition> xingyingList = tileBuilders.stream()
                    .filter(it -> it.getLogicFlags().contains(LogicFlag.XING_YING))
                    .map(it -> it.getPosition())
                    .collect(Collectors.toList());
                Map<ChessSide, ArmyRuntimeData> armyMap = Map.of(
                    ChessSide.RED_SIDE,
                    ArmyRuntimeData.builder()
                        .chessRuntimeDataList(fromCodes(
                            xingyingList,
                            redSideTextField.getText(),
                            game.getScreenContext().getLayoutConst(),
                            ChessSide.RED_SIDE,
                            0
                        ))
                        .build(),
                    ChessSide.BLUE_SIDE,
                    ArmyRuntimeData.builder()
                        .chessRuntimeDataList(fromCodes(
                            xingyingList,
                            blueSideTextField.getText(),
                            game.getScreenContext().getLayoutConst(),
                            ChessSide.BLUE_SIDE,
                            6
                        ))
                        .build()
                );
                StageConfig stageConfig = StageConfig.builder()
                    .playerMode(playerMode)
                    .chessShowMode(chessShowMode)
                    .pvcPlayerSide(ChessSide.RED_SIDE)
                    .currentSide(pvcPlayerAsFirst ? ChessSide.RED_SIDE : ChessSide.BLUE_SIDE)
                    .loseChecker(new JunQiLoseChecker())
                    .armyMap(armyMap)
                    .tileBuilders(tileBuilders)
                    .extraAddLogicNeighborPair(extraAddLogicNeighborPair)
                    .extraRemoveLogicNeighborPair(extraRemoveLogicNeighborPair)
                    .build();
                game.getLogicContext().prepareDone(stageConfig);
                game.getScreenManager().pushScreen(PlayScreen.class.getSimpleName(), BlendingTransition.class.getSimpleName());
            }
        });
        uiRootTable.add(buttonNewGame);
    }

    public static List<TileBuilder> junqiTileBuilders() {
        List<Pair<GridPosition, GridPosition>> railPairs = List.of(
            new Pair<>(new GridPosition(0, 5), new GridPosition(4, 5))
        );
        List<GridPosition> xingyingPositions = List.of(
            new GridPosition(1, 7),
            new GridPosition(3, 7),
            new GridPosition(2, 8),
            new GridPosition(1, 9),
            new GridPosition(3, 9),
            new GridPosition(1, 2),
            new GridPosition(3, 2),
            new GridPosition(2, 3),
            new GridPosition(1, 4),
            new GridPosition(3, 4)
        );
        List<GridPosition> noDiagonalNeighborPositions = List.of(
            new GridPosition(0, 6),
            new GridPosition(1, 6),
            new GridPosition(2, 6),
            new GridPosition(3, 6),
            new GridPosition(4, 6),

            new GridPosition(0, 7),
            new GridPosition(2, 7),
            new GridPosition(4, 7),

            new GridPosition(1, 8),
            new GridPosition(3, 8),

            new GridPosition(0, 9),
            new GridPosition(2, 9),
            new GridPosition(4, 9),

            new GridPosition(0, 2),
            new GridPosition(2, 2),
            new GridPosition(4, 2),

            new GridPosition(1, 3),
            new GridPosition(3, 3),

            new GridPosition(0, 4),
            new GridPosition(2, 4),
            new GridPosition(4, 4),

            new GridPosition(0, 5),
            new GridPosition(1, 5),
            new GridPosition(2, 5),
            new GridPosition(3, 5),
            new GridPosition(4, 5)
        );
        List<TileBuilder> tileBuilders = new ArrayList<>();
        for (int j = 0; j < 13; j++) {
            for (int i = 0; i < 5; i++) {
                GridPosition pos = GridPosition.builder()
                    .x(i)
                    .y(j)
                    .build();
                TileBuilder tileBuilder;
                if (xingyingPositions.contains(pos)) {
                    tileBuilder = TileBuilder.create(i, j, true, LogicFlag.XING_YING);
                } else {
                    boolean isRail = railPairs.stream()
                        .anyMatch(railPair -> {
                            boolean xMatch = pos.getX() >= railPair.getFirst().getX() && pos.getX() <= railPair.getSecond().getX();
                            boolean yMatch = pos.getY() >= railPair.getFirst().getY() && pos.getY() <= railPair.getSecond().getY();
                            return xMatch && yMatch;
                        });
                    boolean hasDiagonalNeighbor = j > 0 && j < 12;
                    if (noDiagonalNeighborPositions.contains(pos)) {
                        hasDiagonalNeighbor = false;
                    }
                    if (isRail) {
                        tileBuilder = TileBuilder.create(i, j, hasDiagonalNeighbor, LogicFlag.RAIL);
                    } else {
                        tileBuilder = TileBuilder.create(i, j, hasDiagonalNeighbor);
                    }
                }
                tileBuilders.add(tileBuilder);
            }
        }
        return tileBuilders;
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

    public static List<ChessRuntimeData> fromCodes(
        List<GridPosition> xingyingList,
        String codes,
        LayoutConst layoutConst,
        ChessSide chessSide,
        int startRow
    ) {
        List<ChessRuntimeData> result = new ArrayList<>();
        int row = startRow;
        int col = 0;
        for (int i = 0; i < codes.length(); ) {
            ChessRuntimeData chessRuntimeData;
            ChessType chessType;
            final int tempCol = col;
            final int tempRow = row;
            final String id = UUID.randomUUID().toString();
            boolean isXingying = xingyingList.stream()
                .anyMatch(it -> it.getX() == tempCol
                    && it.getY() == tempRow
                );
            if (!isXingying) {
                // 放置棋子
                String code = String.valueOf(codes.charAt(i));
                chessType = ChessType.fromCode(code);
                chessRuntimeData = ChessRuntimeData.builder()
                    .id(id)
                    .pos(new GridPosition(col, row))
                    .chessType(chessType)
                    .chessSide(chessSide)
                    .build();
                chessRuntimeData.updateUiPos(layoutConst);
                chessRuntimeData.setChessBattleStatus(ChessBattleStatus.createStatus(chessRuntimeData.getChessType()));
                result.add(chessRuntimeData);
                i++;
            }

            col++;
            if (col > 4) {
                col = 0;
                row++;
            }
        }
        return result;
    }
}
