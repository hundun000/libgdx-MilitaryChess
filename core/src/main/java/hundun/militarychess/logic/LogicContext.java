package hundun.militarychess.logic;

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
    public static class CrossScreenDataPackage {
        MilitaryChessGame game;
        ChessSide currentSide;
        ChessState currentState;
        Map<ChessSide, ArmyRuntimeData> armyMap;

        @Getter
        ChessVM fromChessVM;
        @Getter
        ChessVM toChessVM;

        public void afterFight() {
            if (currentSide == ChessSide.MY_SIDE) {
                currentSide = ChessSide.OTHER_SIDE;
            } else {
                currentSide = ChessSide.MY_SIDE;
            }
        }
    }

    public enum ChessState {
        WAIT_SELECT_FROM,
        WAIT_SELECT_TO,
        WAIT_COMMIT,
        ;

    }

    public void loadEmpty() {
    }

    public void updateCrossScreenDataPackage() {
        this.crossScreenDataPackage = CrossScreenDataPackage.builder()
            .game(game)
            .currentSide(ChessSide.MY_SIDE)
            .currentState(ChessState.WAIT_SELECT_FROM)
            .armyMap(Map.of(
                    ChessSide.MY_SIDE,
                    ArmyRuntimeData.builder()
                    .chessRuntimeDataList(ChessRuntimeData.fromCodes(
                        "abccddeeffggghhhiiijjkklj",
                        game.getScreenContext().getLayoutConst(),
                        ChessSide.MY_SIDE))
                        .build(),
                    ChessSide.OTHER_SIDE,
                    ArmyRuntimeData.builder()
                        .chessRuntimeDataList(ChessRuntimeData.fromCodes(
                            "jlkkijiiihhhgggffeeddccba",
                            game.getScreenContext().getLayoutConst(),
                            ChessSide.OTHER_SIDE))
                            .build()
            ))
            .build();
    }
}
