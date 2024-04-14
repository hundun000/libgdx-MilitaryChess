package hundun.militarychess.ui.screen.shared;

import java.util.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import hundun.militarychess.logic.LogicContext.CrossScreenDataPackage;
import hundun.militarychess.logic.chess.GameboardPosRule;
import hundun.militarychess.logic.chess.GameboardPosRule.SimplePos;
import hundun.militarychess.logic.data.ChessRuntimeData;
import hundun.militarychess.ui.other.CameraDataPackage;
import hundun.militarychess.ui.screen.AbstractMilitaryChessScreen;
import hundun.militarychess.ui.screen.PlayScreen;
import hundun.militarychess.ui.screen.shared.ChessVM.MaskType;
import lombok.Getter;


public class DeskAreaVM extends Table {
    public PlayScreen screen;
    @Getter
    Map<String, ChessVM> nodes = new LinkedHashMap<>();
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
            nodes.put(deskData.getId(), actor);
            this.addActor(actor);

        });

        nodes.values().forEach(hexCellVM -> {
            Actor hitBox = new Image();
            hitBox.setBounds(
                hexCellVM.getX() + screen.getGame().getScreenContext().getLayoutConst().HIT_BOX_X,
                hexCellVM.getY() + screen.getGame().getScreenContext().getLayoutConst().HIT_BOX_Y,
                screen.getGame().getScreenContext().getLayoutConst().HIT_BOX_WIDTH,
                screen.getGame().getScreenContext().getLayoutConst().HIT_BOX_HEIGHT
            );
            if (screen.getGame().debugMode) {
                hitBox.debug();
            }
            this.addActor(hitBox);
            hitBox.addListener(new DeskClickListener(screen, hexCellVM));
        });
    }

    public void updateMask(ChessVM from) {
        CrossScreenDataPackage crossScreenDataPackage = screen.getGame().getLogicContext().getCrossScreenDataPackage();
        if (crossScreenDataPackage.getCurrentChessShowSides().contains(from.getDeskData().getChessSide())) {
            nodes.values().forEach(it -> {
                Set<SimplePos> all = GameboardPosRule.finaAllMoveCandidates(from.getDeskData(), crossScreenDataPackage);
                it.updateMask(all.contains(it.getDeskData().getPos()) ? MaskType.MOVE_CANDIDATE : MaskType.EMPTY);
            });
        }
        from.updateMask(MaskType.FROM);
    }

    public void afterFightOrClear() {
        nodes.values().forEach(it -> {
            it.updateUI();
            it.updateMask(MaskType.EMPTY);
        });
    }
}
