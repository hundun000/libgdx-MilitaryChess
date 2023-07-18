package hundun.militarychess.logic;

import hundun.militarychess.logic.chess.ChessRule;
import hundun.militarychess.logic.chess.PosRule.SimplePos;
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
    public static class AiAction {
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
        ChessSide currentSide;
        ChessState currentState;
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


        public void afterFight() {
            if (currentSide == ChessSide.FIRST_SIDE) {
                currentSide = ChessSide.SECOND_SIDE;
            } else {
                currentSide = ChessSide.FIRST_SIDE;
            }
            this.setCurrentState(ChessState.WAIT_SELECT_FROM);
            if (playerMode == PlayerMode.PVC) {
                if (currentSide == ChessSide.SECOND_SIDE) {
                    aiAction = ChessRule.generateAiAction(
                        armyMap.get(ChessSide.SECOND_SIDE),
                        armyMap.get(ChessSide.FIRST_SIDE)
                    );
                } else {
                    aiAction = null;
                }
            }
        }


    }

    public enum ChessState {
        WAIT_SELECT_FROM,
        WAIT_SELECT_TO,
        WAIT_COMMIT,
        ;

    }

    public enum PlayerMode {
        PVP,
        PVC,
        ;

    }


    public void loadEmpty() {
    }

    public void updateCrossScreenDataPackage() {
        this.crossScreenDataPackage = CrossScreenDataPackage.builder()
            .game(game)
            .playerMode(PlayerMode.PVP)
            .currentSide(ChessSide.FIRST_SIDE)
            .currentState(ChessState.WAIT_SELECT_FROM)
            .armyMap(Map.of(
                    ChessSide.FIRST_SIDE,
                    ArmyRuntimeData.builder()
                    .chessRuntimeDataList(ChessRuntimeData.fromCodes(
                        "abccddeeffggghhhiiijjkklj",
                        game.getScreenContext().getLayoutConst(),
                        ChessSide.FIRST_SIDE))
                        .build(),
                    ChessSide.SECOND_SIDE,
                    ArmyRuntimeData.builder()
                        .chessRuntimeDataList(ChessRuntimeData.fromCodes(
                            "jlkkijiiihhhgggffeeddccba",
                            game.getScreenContext().getLayoutConst(),
                            ChessSide.SECOND_SIDE))
                            .build()
            ))
            .build();
    }
}
