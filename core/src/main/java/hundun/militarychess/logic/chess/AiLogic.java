package hundun.militarychess.logic.chess;

import hundun.militarychess.logic.LogicContext;
import hundun.militarychess.logic.LogicContext.AiAction;
import hundun.militarychess.logic.manager.CrossScreenDataManager;
import hundun.militarychess.logic.chess.ChessRule.BattleResultType;
import hundun.militarychess.logic.data.ArmyRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AiLogic {

    private static int DIE_SCORE = -1;
    LogicContext logicContext;
    public AiLogic(LogicContext logicContext) {
        this.logicContext = logicContext;
    }

    private static int killScore(ChessType chessType){
        if (chessType == ChessType.JUN_QI) {
            return 20;
        }
        return ('z' - chessType.getCode().charAt(0));
    }

    public AiAction generateAiAction(ArmyRuntimeData fromArmy, ArmyRuntimeData toArmy, CrossScreenDataManager crossScreenDataManager) {
        Set<GridPosition> allPosOfOtherArmy = new HashSet<>();
        toArmy.getChessRuntimeDataList().forEach(it -> allPosOfOtherArmy.add(it.getPos()));

        // 目标是找到得分最高的From和To
        ChessRuntimeData maxScoreFromChess = null;
        GridPosition maxScoreToPos = null;
        int maxScore = -100;
        // 遍历每个我方棋子
        for (ChessRuntimeData checkingFromChess : fromArmy.getChessRuntimeDataList()) {
            Set<GridPosition> all = logicContext.getChessTileManager().finaAllMoveCandidates(checkingFromChess, crossScreenDataManager);
            Map<GridPosition, Integer> scoreMap = new HashMap<>();
            // 遍历每个可移动终点
            for (GridPosition checkingTo : all) {
                ChessRuntimeData checkingToChess = logicContext.getChessTileManager().findAtPos(checkingTo);
                if (allPosOfOtherArmy.contains(checkingTo)) {
                    // case 可移动终点是敌方棋子。吃子等级越高，得分越高。
                    final var battleResult = logicContext.getChessRule().getFightV2Result(checkingFromChess, checkingToChess);
                    final BattleResultType battleResultType = battleResult.getBattleResultType();
                    if (battleResultType == BattleResultType.FROM_WIN) {
                        scoreMap.put(checkingTo, killScore(checkingToChess.getChessType()) * 100);
                    } else if (battleResultType == BattleResultType.BOTH_DIE) {
                        scoreMap.put(checkingTo, killScore(checkingToChess.getChessType()) * 50);
                    } else if (battleResultType == BattleResultType.TO_WIN) {
                        scoreMap.put(checkingTo, DIE_SCORE);
                    }
                } else {
                    // case 可移动终点是空地。这个空地距离的得分，来自该位置对于所有敌军的得分以及距离的加权平均
                    int totalScore = 0;
                    for (ChessRuntimeData it : toArmy.getChessRuntimeDataList()) {
                        final var battleResult = logicContext.getChessRule().getFightV2Result(checkingFromChess, it);
                        final BattleResultType battleResultType = battleResult.getBattleResultType();
                        int distance = Math.abs(it.getPos().getY() - checkingTo.getY()) + Math.abs(it.getPos().getX() - checkingTo.getX());
                        int distanceScore = 21 - distance;
                        int baseKillScore;
                        if (battleResultType == BattleResultType.FROM_WIN) {
                            baseKillScore = killScore(checkingToChess.getChessType()) * 10;
                        } else if (battleResultType == BattleResultType.BOTH_DIE) {
                            baseKillScore = killScore(checkingToChess.getChessType()) * 5;
                        } else if (battleResultType == BattleResultType.TO_WIN) {
                            baseKillScore = DIE_SCORE;
                        } else {
                            baseKillScore = 1;
                        }
                        totalScore += distanceScore * baseKillScore / 10;
                    }
                    scoreMap.put(checkingTo, totalScore);
                }
            }
            for (var entry : scoreMap.entrySet()) {
                if (entry.getValue() > maxScore) {
                    maxScore = entry.getValue();
                    maxScoreFromChess = checkingFromChess;
                    maxScoreToPos = entry.getKey();
                }
            }
        }

        if (maxScoreFromChess != null) {
            return AiAction.builder()
                .from(maxScoreFromChess)
                .to(logicContext.getChessTileManager().findAtPos(maxScoreToPos))
                .score(maxScore)
                .build();
        } else {
            return AiAction.builder()
                .capitulated(true)
                .build();
        }
    }
}
