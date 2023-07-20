package hundun.militarychess.logic;

import hundun.militarychess.logic.chess.AiLogic;
import hundun.militarychess.logic.chess.ChessRule.FightResultType;
import hundun.militarychess.logic.chess.ChessType;
import hundun.militarychess.logic.chess.GameboardPosRule.SimplePos;
import hundun.militarychess.logic.data.ArmyRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData.ChessSide;
import hundun.militarychess.ui.MilitaryChessGame;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

public class LogicContext {

    MilitaryChessGame game;
    @Setter
    @Getter
    CrossScreenDataPackage crossScreenDataPackage;

    public LogicContext(MilitaryChessGame game) {
        this.game = game;
    }

    public void lazyInitOnCreateStage1() {
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

    /**
     * 所有逻辑类数据的集合，UI类从本类中读写数据。
     */
    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class CrossScreenDataPackage {
        MilitaryChessGame game;

        PlayerMode playerMode;
        ChessShowMode chessShowMode;
        ChessSide pvcPlayerSide;
        ChessSide currentSide;
        ChessState currentState;
        Set<ChessSide> currentChessShowSides;
        Map<ChessSide, ArmyRuntimeData> armyMap;
        AiAction aiAction;
        ChessSide loseSide;
        String loseReason;
        /**
         * 连续未吃子计数器
         */
        int notKillTurnCount;
        public ChessRuntimeData findAtPos(SimplePos pos) {
            for (var armyRuntimeData : armyMap.values()) {
                var result = armyRuntimeData.getChessRuntimeDataList().stream()
                    .filter(chessRuntimeData -> chessRuntimeData.getPos().equals(pos))
                    .findAny()
                    .orElse(null);
                if (result != null) {
                    return result;
                }
            }
            return null;
        }

        /**
         * 战斗后或开局时调用一次，更新数据
         */
        public void updateAfterFightOrStart() {
            // PVC时生成aiAction
            if (playerMode == PlayerMode.PVC) {
                if (currentSide != pvcPlayerSide) {
                    aiAction = AiLogic.generateAiAction(
                        armyMap.get(ChessSide.BLUE_SIDE),
                        armyMap.get(ChessSide.RED_SIDE),
                        this
                    );
                } else {
                    aiAction = null;
                }
            }
            // 更新暗棋影响
            currentChessShowSides.clear();
            currentChessShowSides.add(ChessSide.EMPTY);
            if (chessShowMode == ChessShowMode.MING_QI) {
                currentChessShowSides.addAll(armyMap.keySet());
            } else {
                if (playerMode == PlayerMode.PVP) {
                    currentChessShowSides.add(currentSide);
                } else {
                    currentChessShowSides.add(pvcPlayerSide);
                }
            }
            // 检查是否已结束
            armyMap.forEach((key, value) -> {
                boolean noneCanMove = value.getChessRuntimeDataList().stream()
                    .noneMatch(chess -> chess.getChessType().isCanMove());
                if (noneCanMove) {
                    loseSide = key;
                    loseReason = "没有棋子可走";
                }
                boolean junqiDied = value.getChessRuntimeDataList().stream()
                    .noneMatch(chess -> chess.getChessType() == ChessType.JUN_QI);
                if (junqiDied) {
                    loseSide = key;
                    loseReason = "军棋已死亡";
                }
                boolean timeout = value.getUsedTime() > 30 * 60;
                if (timeout) {
                    loseSide = key;
                    loseReason = "累计用时超过30分钟";
                }
            });
            if (notKillTurnCount == 31) {
                loseSide = currentSide;
                loseReason = "连续31步未吃子";
            }
        }

        public void afterFight(FightResultType fightResultType) {
            // 更新当前方
            if (currentSide == ChessSide.RED_SIDE) {
                currentSide = ChessSide.BLUE_SIDE;
            } else {
                currentSide = ChessSide.RED_SIDE;
            }
            // 更新阶段
            this.setCurrentState(ChessState.WAIT_SELECT_FROM);
            // 更新连续未吃子计数器
            if (fightResultType != FightResultType.FROM_WIN
                && fightResultType != FightResultType.TO_WIN
                && fightResultType != FightResultType.BOTH_DIE) {
                notKillTurnCount++;
            } else {
                notKillTurnCount = 0;
            }
            updateAfterFightOrStart();
        }


        /**
         * 当前执棋方统计耗时
         */
        public void currentSideAddTime(int second) {
            armyMap.get(currentSide).setUsedTime(armyMap.get(currentSide).getUsedTime() + second);
        }
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
