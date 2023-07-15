package hundun.militarychess.ui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import hundun.militarychess.logic.LogicContext.ChessState;
import hundun.militarychess.logic.LogicContext.CrossScreenDataPackage;
import hundun.militarychess.logic.chess.ChessRule;
import hundun.militarychess.logic.chess.ChessRule.FightResultType;
import hundun.militarychess.logic.data.ChessRuntimeData;
import hundun.militarychess.ui.MilitaryChessGame;
import hundun.militarychess.ui.other.CameraDataPackage;
import hundun.militarychess.ui.screen.builder.BuilderMainBoardVM;
import hundun.militarychess.ui.screen.shared.ChessVM;
import hundun.militarychess.ui.screen.shared.DeskAreaVM;

import java.util.ArrayList;
import java.util.List;


public class PlayScreen extends AbstractComikeScreen {




    // ------ UI layer ------
    private BuilderMainBoardVM mainBoardVM;

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
        mainBoardVM = new BuilderMainBoardVM(this);
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

        updateUIForShow();


        Gdx.app.log(this.getClass().getSimpleName(), "show done");
    }

    private void updateUIForShow() {


        deskAreaVM.getCameraDataPackage().forceSet(null, null, CameraDataPackage.DEFAULT_CAMERA_ZOOM_WEIGHT);

        updateUIAfterRoomChanged();
    }

    @Override
    public void updateUIAfterRoomChanged() {
        CrossScreenDataPackage crossScreenDataPackage = game.getLogicContext().getCrossScreenDataPackage();

        List<ChessRuntimeData> allChessRuntimeDataList = new ArrayList<>();
        crossScreenDataPackage.getArmyMap().values().forEach(it -> allChessRuntimeDataList.addAll(it.getChessRuntimeDataList()));
        deskAreaVM.updateDeskDatas(allChessRuntimeDataList);

        mainBoardVM.getAllButtonPageVM().updateForNewSide(crossScreenDataPackage.getCurrentSide());
        mainBoardVM.updateForShow();
    }

    @Override
    protected void logicOnDraw() {

    }



    @Override
    public void onDeskClicked(ChessVM chessVM) {
        CrossScreenDataPackage crossScreenDataPackage = game.getLogicContext().getCrossScreenDataPackage();
        switch (crossScreenDataPackage.getCurrentState()) {
            case WAIT_SELECT_FROM:
                if (chessVM.getDeskData().getChessSide() == crossScreenDataPackage.getCurrentSide()) {
                    mainBoardVM.getAllButtonPageVM().setFrom(chessVM);
                    crossScreenDataPackage.setCurrentState(ChessState.WAIT_SELECT_TO);
                }
                break;
            case WAIT_SELECT_TO:
                if (chessVM.getDeskData().getChessSide() != crossScreenDataPackage.getCurrentSide()) {
                    FightResultType fightResultPreview = ChessRule.canMove(
                        mainBoardVM.getAllButtonPageVM().getFromChessVM().getDeskData(),
                        chessVM.getDeskData()
                        );

                    mainBoardVM.getAllButtonPageVM().setTo(chessVM, fightResultPreview);
                    crossScreenDataPackage.setCurrentState(ChessState.WAIT_COMMIT);
                }
                break;
            default:
        }
    }

    public void onCommitButtonClicked() {
        CrossScreenDataPackage crossScreenDataPackage = game.getLogicContext().getCrossScreenDataPackage();
        ChessRule.fight(
            mainBoardVM.getAllButtonPageVM().getFromChessVM().getDeskData(),
            mainBoardVM.getAllButtonPageVM().getToChessVM().getDeskData()
        );
        crossScreenDataPackage.afterFight();
        mainBoardVM.getAllButtonPageVM().getFromChessVM().updateUI();
        mainBoardVM.getAllButtonPageVM().getToChessVM().updateUI();
        mainBoardVM.getAllButtonPageVM().updateForNewSide(crossScreenDataPackage.getCurrentSide());
        crossScreenDataPackage.setCurrentState(ChessState.WAIT_SELECT_FROM);
    }

    public void onClearButtonClicked() {
        CrossScreenDataPackage crossScreenDataPackage = game.getLogicContext().getCrossScreenDataPackage();
        mainBoardVM.getAllButtonPageVM().setFrom(null);
        mainBoardVM.getAllButtonPageVM().setTo(null, null);
        crossScreenDataPackage.setCurrentState(ChessState.WAIT_SELECT_FROM);
    }
}
