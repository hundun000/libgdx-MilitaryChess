package hundun.militarychess.logic;

import hundun.militarychess.logic.chess.AiLogic;
import hundun.militarychess.logic.chess.GameboardPosRule.SimplePos;
import hundun.militarychess.logic.data.ArmyRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData.ChessSide;
import hundun.militarychess.ui.MilitaryChessGame;
import hundun.militarychess.ui.screen.shared.ChessVM;
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

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class AiAction {
        /**
         * 认输
         */
        boolean failed;
        int score;
        ChessRuntimeData from;
        ChessRuntimeData to;
    }

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

        ChessVM fromChessVM;
        ChessVM toChessVM;
        AiAction aiAction;

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

        public void update() {
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
        }

        public void afterFight() {
            // 更新当前方
            if (currentSide == ChessSide.FIRST_SIDE) {
                currentSide = ChessSide.SECOND_SIDE;
            } else {
                currentSide = ChessSide.FIRST_SIDE;
            }
            // 更新阶段
            this.setCurrentState(ChessState.WAIT_SELECT_FROM);
            // PVC时生成aiAction
            if (playerMode == PlayerMode.PVC) {
                if (currentSide == ChessSide.SECOND_SIDE) {
                    aiAction = AiLogic.generateAiAction(
                        armyMap.get(ChessSide.SECOND_SIDE),
                        armyMap.get(ChessSide.FIRST_SIDE),
                        this
                    );
                } else {
                    aiAction = null;
                }
            }
            update();
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
