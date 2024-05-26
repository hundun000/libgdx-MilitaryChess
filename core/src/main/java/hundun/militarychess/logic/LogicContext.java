package hundun.militarychess.logic;

import hundun.militarychess.logic.chess.AiLogic;
import hundun.militarychess.logic.chess.ChessRule;
import hundun.militarychess.logic.data.ArmyRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData.ChessSide;
import hundun.militarychess.logic.map.StageConfig;
import hundun.militarychess.ui.MilitaryChessGame;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class LogicContext {

    MilitaryChessGame game;
    @Getter
    CrossScreenDataPackage crossScreenDataPackage;
    @Getter
    ChessRule chessRule;
    @Getter
    MilitaryChessTileMap tileMap;
    @Getter
    AiLogic aiLogic;
    public LogicContext(MilitaryChessGame game) {
        this.game = game;
        this.chessRule = new ChessRule(this);
        this.tileMap = new MilitaryChessTileMap(this);
        this.aiLogic = new AiLogic(this);
    }

    public void lazyInitOnCreateStage1() {
    }


    public void prepareDone(CrossScreenDataPackage crossScreenDataPackage, StageConfig stageConfig) {
        this.crossScreenDataPackage = crossScreenDataPackage;
        crossScreenDataPackage.prepareDone(stageConfig);
        tileMap.prepareDone(stageConfig);
    }

    /**
     * AI的一步棋
     */
    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class AiAction {
        boolean capitulated;
        int score;
        ChessRuntimeData from;
        ChessRuntimeData to;
    }

    public enum ChessState {
        WAIT_SELECT_FROM,
        WAIT_SELECT_TO,
        WAIT_COMMIT,
        ;

    }

    @Getter
    public enum PlayerMode {
        PVP("双人对战"),
        PVC("人机对战"),
        ;
        final String chinese;
        PlayerMode(String chinese){
            this.chinese = chinese;
        }
    }

    @Getter
    public enum ChessShowMode {
        MING_QI("明棋"),
        AN_QI("暗棋"),
        ;
        final String chinese;
        ChessShowMode(String chinese){
            this.chinese = chinese;
        }
    }



}
