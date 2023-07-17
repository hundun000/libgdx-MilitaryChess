package hundun.militarychess.logic.chess;

import hundun.militarychess.logic.data.ChessRuntimeData.ChessSide;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PosRule {


    public static Map<ChessSide, List<SimplePos>> XING_YING_POS_MAP = Map.of(
        ChessSide.FIRST_SIDE,
        List.of(
            new SimplePos(7, 1),
            new SimplePos(7, 3),
            new SimplePos(8, 2),
            new SimplePos(9, 1),
            new SimplePos(9, 3)
        ),
        ChessSide.SECOND_SIDE,
        List.of(
            new SimplePos(2, 1),
            new SimplePos(2, 3),
            new SimplePos(3, 2),
            new SimplePos(4, 1),
            new SimplePos(4, 3)
        )
    );

    public static Map<ChessSide, List<SimplePos>> DA_BEN_YING_POS_MAP = Map.of(
        ChessSide.FIRST_SIDE,
        List.of(
            new SimplePos(11, 1),
            new SimplePos(11, 3)
        ),
        ChessSide.SECOND_SIDE,
        List.of(
            new SimplePos(0, 1),
            new SimplePos(0, 3)
        )
    );

    public static StepData calculate(SimplePos currentPos) {
        List<SimplePos> notRailDestinationPosList = new ArrayList<>();
        List<SimplePos> railDestinationPosList = new ArrayList<>();



        return StepData.builder()
            .currentPos(currentPos)
            .notRailDestinationPosList(notRailDestinationPosList)
            .railDestinationPosList(railDestinationPosList)
            .build();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class StepData {
        SimplePos currentPos;
        List<SimplePos> notRailDestinationPosList;
        List<SimplePos> railDestinationPosList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SimplePos {
        int row;
        int col;
    }


}
