package hundun.militarychess.logic.chess;

import hundun.militarychess.logic.LogicContext.AiAction;
import hundun.militarychess.logic.LogicContext.CrossScreenDataPackage;
import hundun.militarychess.logic.chess.ChessRule.FightResultType;
import hundun.militarychess.logic.chess.GameboardPosRule.GameboardPos;
import hundun.militarychess.logic.chess.GameboardPosRule.SimplePos;
import hundun.militarychess.logic.data.ArmyRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class AiLogic {

    private static int DIE_SCORE = -1;
    private static int killScore(ChessType chessType){
        if (chessType == ChessType.JUN_QI) {
            return 10000;
        }
        return 100 * ('z' - chessType.getCode().charAt(0));
    }

    public static AiAction generateAiAction(ArmyRuntimeData fromArmy, ArmyRuntimeData toArmy, CrossScreenDataPackage crossScreenDataPackage) {
        Set<SimplePos> allPosOfOtherArmy = new HashSet<>();
        toArmy.getChessRuntimeDataList().forEach(it -> allPosOfOtherArmy.add(it.getPos()));


        ChessRuntimeData maxScoreFromChess = null;
        SimplePos maxScoreToPos = null;
        int maxScore = 0;
        for (ChessRuntimeData checkingFromChess : fromArmy.getChessRuntimeDataList()) {
            Set<SimplePos> all = GameboardPosRule.finaAllMoveCandidates(checkingFromChess, crossScreenDataPackage);
            Map<SimplePos, Integer> scoreMap = new HashMap<>();
            for (SimplePos checkingTo : all) {
                ChessRuntimeData checkingToChess = crossScreenDataPackage.findAtPos(checkingTo);
                if (allPosOfOtherArmy.contains(checkingTo)) {
                    FightResultType fightResultType = ChessRule.fightResultPreview(checkingFromChess, checkingToChess);
                    if (fightResultType == FightResultType.FROM_WIN) {
                        scoreMap.put(checkingTo, killScore(checkingToChess.getChessType()));
                    } else if (fightResultType == FightResultType.BOTH_DIE) {
                        scoreMap.put(checkingTo, killScore(checkingToChess.getChessType()) / 2);
                    } else if (fightResultType == FightResultType.TO_WIN) {
                        scoreMap.put(checkingTo, DIE_SCORE);
                    }
                } else {
                    int minDistance = 1000;
                    for (SimplePos posOfOtherArmy : allPosOfOtherArmy) {
                        int distance = Math.abs(posOfOtherArmy.getRow() - checkingTo.getRow()) + Math.abs(posOfOtherArmy.getCol() - checkingTo.getCol());
                        if (distance < minDistance) {
                            minDistance = distance;
                        }
                    }
                    int score = 20 - minDistance;
                    scoreMap.put(checkingTo, score);
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
                .to(crossScreenDataPackage.findAtPos(maxScoreToPos))
                .score(maxScore)
                .build();
        } else {
            return AiAction.builder()
                .failed(true)
                .build();
        }
    }
}
