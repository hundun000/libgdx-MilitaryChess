package hundun.militarychess.logic.manager;

import hundun.militarychess.logic.LogicContext;
import hundun.militarychess.logic.chess.ChessRule.BattleResult;
import hundun.militarychess.logic.chess.ChessRule.BattleResultType;
import hundun.militarychess.logic.chess.lose.ILoseChecker;
import hundun.militarychess.logic.data.ChessRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData.ChessSide;
import hundun.militarychess.logic.StageConfig;
import hundun.militarychess.ui.MilitaryChessGame;
import lombok.Getter;
import lombok.Setter;

public class AfterBattleManager implements IManager {
    final LogicContext logicContext;
    final MilitaryChessGame game;
    @Getter
    ILoseChecker loseChecker;
    @Getter
    ChessSide loseSide;
    @Getter
    String loseReason;
    /**
     * 连续未吃子计数器
     */
    int notKillTurnCount;
    @Getter
    @Setter
    ChessRuntimeData battleFromChess;
    @Getter
    @Setter
    ChessRuntimeData battleToChess;
    @Getter
    @Setter
    BattleResult battleResult;
    public AfterBattleManager(LogicContext logicContext) {
        this.logicContext = logicContext;
        this.game = logicContext.getGame();
    }

    @Override
    public void commitFightResult() {
        logicContext.getChessRule().onBattleCommit(this.battleResult);

        var commitedBattleResultType = battleResult.getBattleResultType();
        game.getFrontend().log(this.getClass().getSimpleName(), "commitedBattleResultType " + commitedBattleResultType);
        // 更新连续未吃子计数器
        if (commitedBattleResultType != BattleResultType.FROM_WIN
            && commitedBattleResultType != BattleResultType.TO_WIN
            && commitedBattleResultType != BattleResultType.BOTH_DIE) {
            notKillTurnCount++;
        } else {
            notKillTurnCount = 0;
        }
        updateAfterFightOrStart();
    }

    @Override
    public void updateAfterFightOrStart() {
        // 检查是否已结束
        logicContext.getChessTileManager().getArmyMap().forEach((key, value) -> {
            boolean noneCanMove = value.getChessRuntimeDataList().stream()
                .noneMatch(chess -> chess.getChessType().isCanMove());
            if (noneCanMove) {
                loseSide = key;
                loseReason = "没有棋子可走";
                return;
            }
            boolean timeout = value.getUsedTime() > 30 * 60;
            if (timeout) {
                loseSide = key;
                loseReason = "累计用时超过30分钟";
            }
            var tempLoseReason = loseChecker.checkLoseByArmy(value);
            if (tempLoseReason != null) {
                loseSide = key;
                loseReason = tempLoseReason;
            }
        });
        if (notKillTurnCount == 31) {
            loseSide = logicContext.getCrossScreenDataManager().getCurrentSide();
            loseReason = "连续31步未吃子";
        }
    }

    @Override
    public void prepareDone(StageConfig stageConfig) {
        this.loseChecker = stageConfig.getLoseChecker();
    }

}
