package hundun.militarychess.ui.screen.shared;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import hundun.gdxgame.corelib.base.util.DrawableFactory;
import hundun.militarychess.logic.data.ChessRuntimeData;
import hundun.militarychess.logic.data.generic.GenericPosData;
import hundun.militarychess.ui.other.CameraDataPackage;
import hundun.militarychess.ui.other.CameraGestureListener;
import hundun.militarychess.ui.other.CameraMouseListener;
import hundun.militarychess.ui.screen.AbstractComikeScreen;
import lombok.Getter;


public class DeskAreaVM extends Table {
    public AbstractComikeScreen screen;
    @Getter
    Map<ChessRuntimeData, ChessVM> nodes = new LinkedHashMap<>();
    @Getter
    CameraDataPackage cameraDataPackage;

    public DeskAreaVM(AbstractComikeScreen screen) {
        this.screen = screen;
        this.cameraDataPackage = new CameraDataPackage();

        if (screen.getGame().debugMode) {
            this.debugAll();
        }
    }

    public void updateDeskDatas(
            List<ChessRuntimeData> chessRuntimeDataList) {
        this.clear();
        nodes.clear();

        Image background = new Image();

        background.setDrawable(new TextureRegionDrawable(new Texture(Gdx.files.internal("棋盘.jpg"))));

        int roomWidth = screen.getGame().getScreenContext().getLayoutConst().PLAY_WIDTH;
        int roomHeight = screen.getGame().getScreenContext().getLayoutConst().PLAY_HEIGHT;

        background.setBounds(0, 0, roomWidth, roomHeight);

        this.addActor(background);
        //this.addListener(new CameraGestureListener(cameraDataPackage));
        //this.addListener(new CameraMouseListener(cameraDataPackage));
        this.getCameraDataPackage().forceSet(
            roomWidth / 2.0f + 800,
            roomHeight/ 2.0f,
            null);

        chessRuntimeDataList.forEach(deskData -> {

            ChessVM actor = new ChessVM(this, deskData);
            nodes.put(deskData, actor);
            actor.addListener(new DeskClickListener(screen, actor));
            this.addActor(actor);

        });


    }

    public void updateCartData() {


    }

}
