package hundun.militarychess.logic;

import hundun.militarychess.logic.chess.AiLogic;
import hundun.militarychess.logic.chess.ChessRule;
import hundun.militarychess.logic.data.ChessRuntimeData;
import hundun.militarychess.logic.manager.AfterBattleManager;
import hundun.militarychess.logic.manager.CrossScreenDataManager;
import hundun.militarychess.logic.manager.IManager;
import hundun.militarychess.logic.manager.MilitaryChessTileManager;
import hundun.militarychess.ui.MilitaryChessGame;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class LogicContext {
    @Getter
    MilitaryChessGame game;
    @Getter
    CrossScreenDataManager crossScreenDataManager;
    @Getter
    ChessRule chessRule;
    @Getter
    MilitaryChessTileManager chessTileManager;
    @Getter
    AiLogic aiLogic;
    @Getter
    AfterBattleManager afterBattleManager;

    List<IManager> managers;

    public LogicContext(MilitaryChessGame game) {
        this.game = game;
        this.chessRule = new ChessRule(this);
        this.chessTileManager = new MilitaryChessTileManager(this);
        this.aiLogic = new AiLogic(this);
        this.afterBattleManager = new AfterBattleManager(this);
        this.crossScreenDataManager = new CrossScreenDataManager(this);

        this.managers = List.of(
            crossScreenDataManager,
            chessTileManager,
            afterBattleManager
        );
    }

    public void lazyInitOnCreateStage1() {
    }

    public void prepareDone(StageConfig stageConfig) {

        managers.forEach(manager -> manager.prepareDone(stageConfig));
        this.updateAfterFightOrStart();
    }

    public void commitFightResult() {
        managers.forEach(manager -> manager.commitFightResult());
        this.updateAfterFightOrStart();
    }

    public void updateAfterFightOrStart() {
        managers.forEach(manager -> manager.updateAfterFightOrStart());
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
