package hundun.militarychess.ui.screen.shared;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import hundun.gdxgame.corelib.base.util.DrawableFactory;
import hundun.militarychess.logic.data.ChessRuntimeData;
import hundun.militarychess.logic.data.generic.GenericPosData;
import hundun.militarychess.ui.other.CameraDataPackage;
import hundun.militarychess.ui.other.CameraGestureListener;
import hundun.militarychess.ui.other.CameraMouseListener;
import hundun.militarychess.ui.screen.AbstractComikeScreen;
import lombok.Getter;

/**
 * @author hundun
 * Created on 2023/05/09
 */
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

        background.setDrawable(DrawableFactory.getSimpleBoardBackground());

        int roomWidth = 5000;
        int roomHeight = 5000;

        background.setBounds(0, 0, roomWidth, roomHeight);

        this.addActor(background);
        this.addListener(new CameraGestureListener(cameraDataPackage));
        this.addListener(new CameraMouseListener(cameraDataPackage));
        this.getCameraDataPackage().forceSet(roomWidth / 2.0f, roomHeight/ 2.0f, null);

        chessRuntimeDataList.forEach(deskData -> {

            ChessVM actor = ChessVM.typeMain(this, deskData, deskData.getMainLocation());
            nodes.put(deskData, actor);

            GenericPosData roomPos = deskData.getMainLocation();
            actor.setBounds(
                    roomPos.getX(),
                    roomPos.getY(),
                    screen.getGame().getScreenContext().getLayoutConst().DESK_WIDTH,
                    screen.getGame().getScreenContext().getLayoutConst().DESK_HEIGHT
                    );
            actor.addListener(new DeskClickListener(screen, actor));
            this.addActor(actor);

        });


    }

    public void updateCartData() {


    }

}
