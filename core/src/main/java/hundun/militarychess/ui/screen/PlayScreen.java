package hundun.militarychess.ui.screen;

import java.util.ArrayList;
import java.util.function.Function;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import hundun.gdxgame.corelib.base.util.DrawableFactory;
import hundun.militarychess.logic.LogicContext.CrossScreenDataPackage;
import hundun.militarychess.logic.data.RoomRuntimeData;
import hundun.militarychess.ui.MilitaryChessGame;
import hundun.militarychess.ui.other.CameraDataPackage;
import hundun.militarychess.ui.screen.builder.BuilderMainBoardVM;
import hundun.militarychess.ui.screen.shared.ChessVM;
import hundun.militarychess.ui.screen.shared.DeskAreaVM;

/**
 * @author hundun
 * Created on 2023/05/09
 */
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
        // for newest DeskDatas
        RoomRuntimeData currentRoomData = crossScreenDataPackage.getCurrentRoomData();
        if (currentRoomData != null) {
            deskAreaVM.updateDeskDatas(
                    currentRoomData.getChessRuntimeDataList()
            );
        } else {

        }

        mainBoardVM.updateForShow();
    }

    @Override
    protected void logicOnDraw() {

    }

    @Override
    public void onDeskClicked(ChessVM vm) {

    }
}
