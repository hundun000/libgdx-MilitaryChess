package hundun.militarychess.ui.screen.shared;

import java.util.*;
import java.util.stream.Collectors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import hundun.gdxgame.corelib.base.util.DrawableFactory;
import hundun.militarychess.logic.manager.CrossScreenDataManager;
import hundun.militarychess.logic.chess.GridPosition;
import hundun.militarychess.logic.data.ChessRuntimeData;
import hundun.militarychess.ui.other.CameraDataPackage;
import hundun.militarychess.ui.other.CameraGestureListener;
import hundun.militarychess.ui.other.CameraMouseListener;
import hundun.militarychess.ui.screen.PlayScreen;
import hundun.militarychess.ui.screen.shared.GridVM.MaskType;
import lombok.Getter;


public class DeskAreaVM extends Table {
    @Getter
    PlayScreen screen;
    @Getter
    Map<String, GridVM> postToGridMap = new LinkedHashMap<>();
    @Getter
    CameraDataPackage cameraDataPackage;

    public DeskAreaVM(PlayScreen screen) {
        this.screen = screen;
        this.cameraDataPackage = new CameraDataPackage();

        if (screen.getGame().debugMode) {
            this.debugAll();
        }
    }

    public void updateDeskDatas(
            List<ChessRuntimeData> chessRuntimeDataList) {
        String logMsg = chessRuntimeDataList.stream().map(it -> it.toText()).collect(Collectors.joining(", "));
        screen.getGame().getFrontend().log(this.getClass().getSimpleName(), "updateDeskDatas by: " + logMsg);

        this.clear();
        postToGridMap.clear();

        Image background = new Image();

        background.setDrawable(DrawableFactory.createAlphaBoard(1, 1, Color.GRAY, 1f));

        int roomWidth = screen.getGame().getScreenContext().getLayoutConst().PLAY_WIDTH;
        int roomHeight = screen.getGame().getScreenContext().getLayoutConst().PLAY_HEIGHT;

        background.setBounds(0, 0, roomWidth, roomHeight);

        this.addActor(background);
        this.addListener(new CameraGestureListener(cameraDataPackage));
        this.addListener(new CameraMouseListener(cameraDataPackage));
/*        this.getCameraDataPackage().forceSet(
            roomWidth / 2.0f + 800,
            roomHeight/ 2.0f,
            null);*/

        chessRuntimeDataList.forEach(deskData -> {
            GridVM gridVM = new GridVM(this, deskData.getPos());
            postToGridMap.put(gridVM.getPosition().toId(), gridVM);
            this.addActor(gridVM);
            this.addActor(gridVM.getHitBox());
        });
    }

    public void updateShowSideMask(GridVM from) {
        CrossScreenDataManager crossScreenDataManager = screen.getGame().getLogicContext().getCrossScreenDataManager();
        if (crossScreenDataManager.getCurrentChessShowSides().contains(from.getDeskData().getChessSide())) {
            postToGridMap.values().forEach(it -> {
                Set<GridPosition> all = screen.getGame().getLogicContext().getChessTileManager().finaAllMoveCandidates(from.getDeskData(), crossScreenDataManager);
                it.updateMask(all.contains(it.getDeskData().getPos()) ? MaskType.MOVE_CANDIDATE : MaskType.EMPTY);
            });
        }
        from.updateMask(MaskType.FROM);
    }

    public void afterFightOrClear() {
        postToGridMap.values().forEach(it -> {
            it.updateUIForChessChanged();
            it.updateMask(MaskType.EMPTY);
        });
    }
}
