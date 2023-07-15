package hundun.militarychess.logic;

import hundun.militarychess.logic.data.RoomRuntimeData;
import hundun.militarychess.ui.MilitaryChessGame;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

public class LogicContext {

    MilitaryChessGame game;
    @Getter
    CrossScreenDataPackage crossScreenDataPackage;

    public LogicContext(MilitaryChessGame game) {
        this.game = game;
    }

    public void lazyInitOnCreateStage1() {
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class CrossScreenDataPackage {
        MilitaryChessGame game;

        RoomRuntimeData currentRoomData;

    }

    public void loadEmpty() {
    }

    public void updateCrossScreenDataPackage() {
    }
}
