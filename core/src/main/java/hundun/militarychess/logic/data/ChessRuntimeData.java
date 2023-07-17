package hundun.militarychess.logic.data;

import hundun.militarychess.logic.chess.ChessType;
import hundun.militarychess.logic.chess.PosRule;
import hundun.militarychess.logic.chess.PosRule.ChessPosType;
import hundun.militarychess.logic.chess.PosRule.PosRelationData;
import hundun.militarychess.logic.chess.PosRule.SimplePos;
import hundun.militarychess.logic.data.generic.ChessPosData;
import hundun.militarychess.ui.screen.LayoutConst;
import lombok.*;

import java.util.*;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChessRuntimeData {
    ChessPosData mainLocation;
    int uiX;
    int uiY;
    ChessType chessType;
    ChessSide chessSide;

    @Getter
    public enum ChessSide {
        FIRST_SIDE("红方"),
        SECOND_SIDE("蓝方"),
        EMPTY("空方"),
        ;
        ;
        final String chinese;
        ChessSide(String chinese){
            this.chinese = chinese;
        }
    }

    public static List<ChessRuntimeData> fromCodes(String codes, LayoutConst layoutConst, ChessSide chessSide) {
        List<PosRelationData> xingyingList = PosRule.relationMap.values().stream()
            .filter(it -> it.getChessPosType() == ChessPosType.XING_YING)
            .collect(Collectors.toList());

        List<ChessRuntimeData> result = new ArrayList<>();
        int row = chessSide == ChessSide.FIRST_SIDE ? 6 : 0;
        int col = 0;
        for (int i = 0; i < codes.length(); i++) {
            String code = String.valueOf(codes.charAt(i));
            ChessType chessType = ChessType.fromCode(code);
            ChessPosData mainLocation =  ChessPosData.builder()
                .pos(new SimplePos(row, col))
                .build();

            ChessRuntimeData chessRuntimeData = ChessRuntimeData.builder()
                .mainLocation(mainLocation)
                .chessType(chessType)
                .chessSide(chessSide)
                .build();
            LayoutConst.updatePos(chessRuntimeData, layoutConst);
            result.add(chessRuntimeData);
            boolean needUpdate = true;
            while (needUpdate) {
                col++;
                if (col > 4) {
                    col = 0;
                    row++;
                }
                final int tempCol = col;
                final int tempRow = row;
                needUpdate = xingyingList.stream().anyMatch(it -> it.getCurrentPos().getCol() == tempCol
                    && it.getCurrentPos().getRow() == tempRow);
            }
        }
        xingyingList.forEach(it -> {
                    ChessType chessType = ChessType.EMPTY;
                    ChessPosData mainLocation = ChessPosData.builder()
                        .pos(it.getCurrentPos())
                        .build();
                    ChessRuntimeData chessRuntimeData = ChessRuntimeData.builder()
                        .mainLocation(mainLocation)
                        .chessType(chessType)
                        .chessSide(ChessSide.EMPTY)
                        .build();
                    LayoutConst.updatePos(chessRuntimeData, layoutConst);
                    result.add(chessRuntimeData);
                });
        return result;
    }






}
