package hundun.militarychess.ui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import de.eskalon.commons.screen.transition.impl.BlendingTransition;
import hundun.gdxgame.gamelib.base.util.JavaFeatureForGwt;
import hundun.militarychess.logic.LogicContext.ChessState;
import hundun.militarychess.logic.manager.CrossScreenDataManager;
import hundun.militarychess.logic.chess.ChessRule.BattleResultType;
import hundun.militarychess.logic.data.ChessRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData.ChessSide;
import hundun.militarychess.ui.MilitaryChessGame;
import hundun.militarychess.ui.other.CameraDataPackage;
import hundun.militarychess.ui.screen.board.MainBoardVM;
import hundun.militarychess.ui.screen.shared.ChessVM;
import hundun.militarychess.ui.screen.shared.DeskAreaVM;

import java.util.ArrayList;
import java.util.List;


public class PlayScreen extends AbstractMilitaryChessScreen {




    // ------ UI layer ------
    private MainBoardVM mainBoardVM;
    // ------ desk layer ------
    protected OrthographicCamera deskCamera;
    protected Stage deskStage;
    protected DeskAreaVM deskAreaVM;
    // ------ image previewer layer ------


    // ------ popup layer ------


    public PlayScreen(MilitaryChessGame game) {
        super(game, game.getSharedViewport());

        this.deskCamera = new OrthographicCamera();
        this.deskStage = new Stage(new ScreenViewport(deskCamera), game.getBatch());
    }







    @Override
    protected void create() {


        // ------ desk layer ------
        deskAreaVM = new DeskAreaVM(this);
        deskStage.addActor(deskAreaVM);
        deskStage.setScrollFocus(deskAreaVM);

        // ------ UI layer ------
        mainBoardVM = new MainBoardVM(this);
        uiRootTable.add(mainBoardVM)
            .expandX()
            .growY()
            .right()
        ;

        // ------ image previewer layer ------


        // ------ popup layer ------

    }

    @Override
    public void dispose() {


    }

    @Override
    public void show() {
        super.show();

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(popupUiStage);
        multiplexer.addProcessor(uiStage);
        multiplexer.addProcessor(deskStage);
        Gdx.input.setInputProcessor(multiplexer);

        //Gdx.input.setInputProcessor(uiStage);
        //game.getBatch().setProjectionMatrix(uiStage.getViewport().getCamera().combined);
        int roomWidth = this.getGame().getScreenContext().getLayoutConst().PLAY_WIDTH;
        int roomHeight = this.getGame().getScreenContext().getLayoutConst().PLAY_HEIGHT;

        deskAreaVM.getCameraDataPackage().forceSet(
            roomWidth * 0.9f,
            roomHeight * 0.5f,
            CameraDataPackage.DEFAULT_CAMERA_ZOOM_WEIGHT
        );

        updateUIAfterRoomChanged();


        Gdx.app.log(this.getClass().getSimpleName(), "show done");
    }

    @Override
    public void updateUIAfterRoomChanged() {
        CrossScreenDataManager crossScreenDataManager = game.getLogicContext().getCrossScreenDataManager();

        if (game.getLogicContext().getAfterBattleManager().getBattleResult() != null) {
            afterFight(game.getLogicContext().getAfterBattleManager().getBattleResult().getBattleResultType());
        } else {
            // 构造棋子
            List<ChessRuntimeData> allChessRuntimeDataList = new ArrayList<>();
            game.getLogicContext().getChessTileManager().getArmyMap().values().forEach(it -> allChessRuntimeDataList.addAll(it.getChessRuntimeDataList()));
            allChessRuntimeDataList.addAll(game.getLogicContext().getChessTileManager().getMoreChessList());
            deskAreaVM.updateDeskDatas(allChessRuntimeDataList);

            mainBoardVM.getAllButtonPageVM().updateForNewSide();
            mainBoardVM.updateForShow();
        }
    }


    private ChessVM findVM(ChessRuntimeData chessRuntimeData) {
        return deskAreaVM.getNodes().get(chessRuntimeData.getId());
    }


    /**
     * 每秒被调用一次
     */
    @Override
    public void onLogicFrame() {
        CrossScreenDataManager crossScreenDataManager = game.getLogicContext().getCrossScreenDataManager();
        // 当前执棋方统计耗时
        crossScreenDataManager.currentSideAddTime(1);
        mainBoardVM.getAllButtonPageVM().updateTime(game.getLogicContext());
        // 若有AiAction，则执行它
        if (crossScreenDataManager.getAiAction() == null) {
            return;
        }
        switch (crossScreenDataManager.getCurrentState()) {
            case WAIT_SELECT_FROM:
                game.getFrontend().log(this.getClass().getSimpleName(),
                    "AiAction score = {0}, from = {1}, to = {2}",
                    crossScreenDataManager.getAiAction().getScore(),
                    crossScreenDataManager.getAiAction().getFrom().toText(),
                    crossScreenDataManager.getAiAction().getTo().toText()
                );
                if (crossScreenDataManager.getAiAction().isCapitulated()) {
                    // 模拟Ai点击认输
                    onCapitulated();
                } else {
                    // 模拟Ai点击棋子
                    onDeskClicked(findVM(crossScreenDataManager.getAiAction().getFrom()));
                }

                break;
            case WAIT_SELECT_TO:
                // 模拟Ai点击棋子
                onDeskClicked(findVM(crossScreenDataManager.getAiAction().getTo()));
                break;
            case WAIT_COMMIT:
                // 模拟Ai点击确认
                onBattleStartButtonClicked();
                break;
            default:
        }
    }
    /**
     * 当认输被点击
     */
    public void onCapitulated() {
        CrossScreenDataManager crossScreenDataManager = game.getLogicContext().getCrossScreenDataManager();

        String message = JavaFeatureForGwt.stringFormat(
            "%s已认输。\n%s胜利。",
            crossScreenDataManager.getCurrentSide().getChinese(),
            ChessSide.getOpposite(crossScreenDataManager.getCurrentSide()).getChinese()
        );
        startDialog(message,"对局结束", () -> {
            // 回到菜单页
            game.getScreenManager().pushScreen(MyMenuScreen.class.getSimpleName(), BlendingTransition.class.getSimpleName());
        });
    }

    /**
     * 当棋子被点击
     */
    public void onDeskClicked(ChessVM chessVM) {
        CrossScreenDataManager crossScreenDataManager = game.getLogicContext().getCrossScreenDataManager();
        switch (crossScreenDataManager.getCurrentState()) {
            case WAIT_SELECT_FROM:
                if (chessVM.getDeskData().getChessSide() == crossScreenDataManager.getCurrentSide()) {
                    mainBoardVM.getAllButtonPageVM().setFrom(chessVM);
                    deskAreaVM.updateMask(chessVM);
                    game.getLogicContext().getAfterBattleManager().setBattleFromChess(chessVM.getDeskData());
                    crossScreenDataManager.setCurrentState(ChessState.WAIT_SELECT_TO);
                }
                break;
            case WAIT_SELECT_TO:
                if (chessVM.getDeskData().getChessSide() != crossScreenDataManager.getCurrentSide()) {
                    game.getLogicContext().getAfterBattleManager().setBattleToChess(chessVM.getDeskData());
                    var battleResult = game.getLogicContext().getChessRule().getFightV2Result(
                        game.getLogicContext().getAfterBattleManager().getBattleFromChess(),
                        game.getLogicContext().getAfterBattleManager().getBattleToChess()
                    );
                    game.getLogicContext().getAfterBattleManager().setBattleResult(battleResult);
                    mainBoardVM.getAllButtonPageVM().setTo(chessVM, battleResult.getBattleResultType());
                    crossScreenDataManager.setCurrentState(ChessState.WAIT_COMMIT);
                }
                break;
            default:
        }
    }
    /**
     * 当确认被点击
     */
    public void onBattleStartButtonClicked() {
        CrossScreenDataManager crossScreenDataManager = game.getLogicContext().getCrossScreenDataManager();
        if (game.getLogicContext().getAfterBattleManager().getBattleResult().getBattleResultType() == BattleResultType.JUST_MOVE) {
            game.getLogicContext().commitFightResult();
            afterFight(game.getLogicContext().getAfterBattleManager().getBattleResult().getBattleResultType());
        } else {
            game.getScreenManager().pushScreen(BattleScreen.class.getSimpleName(), BlendingTransition.class.getSimpleName());
        }
    }

    /**
     * 构造消息弹窗
     */
    private void startDialog(String message, String title, Runnable callback) {
        final Dialog dialog = new Dialog(title, game.getMainSkin(), "dialog") {
            public void result(Object obj) {
                boolean action = (boolean) obj;
                if (action) {
                    callback.run();
                }
            }
        };
        dialog.text(message);
        dialog.button("OK", true);
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                dialog.show(popupUiStage);
            }
        });
    }

    /**
     * 战斗后的处理
     */
    private void afterFight(BattleResultType battleResultType) {
        game.getFrontend().log(this.getClass().getSimpleName(),
            "afterFight, from = {0}, to = {1}",
            mainBoardVM.getAllButtonPageVM().getFromChessVM().getDeskData().toText(),
            mainBoardVM.getAllButtonPageVM().getToChessVM().getDeskData().toText()
            );
        CrossScreenDataManager crossScreenDataManager = game.getLogicContext().getCrossScreenDataManager();
        // 若干UI重置
        mainBoardVM.getAllButtonPageVM().getFromChessVM().updateUIForChessChanged();
        mainBoardVM.getAllButtonPageVM().getToChessVM().updateUIForChessChanged();
        mainBoardVM.getAllButtonPageVM().updateForNewSide();
        deskAreaVM.afterFightOrClear();
        // 若已对局结束，则展示
        if (game.getLogicContext().getAfterBattleManager().getLoseSide() != null) {
            String message = JavaFeatureForGwt.stringFormat(
                "%s失败，原因：%s。\n%s胜利。",
                game.getLogicContext().getAfterBattleManager().getLoseSide().getChinese(),
                game.getLogicContext().getAfterBattleManager().getLoseReason(),
                ChessSide.getOpposite(game.getLogicContext().getAfterBattleManager().getLoseSide()).getChinese()
            );
            startDialog(message,"对局结束", () -> {
                // 回到菜单页
                game.getScreenManager().pushScreen(MyMenuScreen.class.getSimpleName(), BlendingTransition.class.getSimpleName());
            });
        }
    }

    /**
     * 当清空被点击
     */
    public void onClearButtonClicked() {
        CrossScreenDataManager crossScreenDataManager = game.getLogicContext().getCrossScreenDataManager();
        mainBoardVM.getAllButtonPageVM().setFrom(null);
        mainBoardVM.getAllButtonPageVM().setTo(null, null);
        deskAreaVM.afterFightOrClear();
        game.getLogicContext().getAfterBattleManager().setBattleFromChess(null);
        game.getLogicContext().getAfterBattleManager().setBattleToChess(null);
        crossScreenDataManager.setCurrentState(ChessState.WAIT_SELECT_FROM);
    }

    @Override
    protected void belowUiStageDraw(float delta) {

        deskStage.act();
        deskStage.getViewport().getCamera().position.set(
            deskAreaVM.getCameraDataPackage().getCurrentCameraX(),
            deskAreaVM.getCameraDataPackage().getCurrentCameraY(),
            0);
        if (deskAreaVM.getCameraDataPackage().getAndClearCameraZoomDirty()) {
            float weight = deskAreaVM.getCameraDataPackage().getCurrentCameraZoomWeight();
            deskCamera.zoom = CameraDataPackage.cameraZoomWeightToZoomValue(weight);
            game.getFrontend().log(this.getClass().getSimpleName(), "deskCamera.zoom = %s", deskCamera.zoom);
        }
        deskStage.getViewport().apply();
        deskStage.draw();
    }
}
