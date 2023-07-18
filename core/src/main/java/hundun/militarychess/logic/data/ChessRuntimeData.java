package hundun.militarychess.logic.data;

import hundun.militarychess.logic.chess.ChessType;
import hundun.militarychess.logic.chess.PosRule;
import hundun.militarychess.logic.chess.PosRule.ChessPosType;
import hundun.militarychess.logic.chess.PosRule.PosRelationData;
import hundun.militarychess.logic.chess.PosRule.SimplePos;
import hundun.militarychess.ui.screen.LayoutConst;
import lombok.*;

import java.util.*;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChessRuntimeData {
    SimplePos pos;
    int uiX;
    int uiY;
    ChessType chessType;
    ChessSide chessSide;

    public String toText() {
        return this.getChessType().name()
            + this.getPos().toText();
    }

    public void updateUiPos(
        LayoutConst layoutConst
    ) {
        int x = this.getPos().getCol() * layoutConst.DESK_WIDTH;
        int y = (12 - this.getPos().getRow()) * layoutConst.DESK_HEIGHT;
        if (this.getPos().getRow() >= 6) {
            y -= layoutConst.RIVER_HEIGHT;
        }
        this.setUiX(x);
        this.setUiY(y);
    }

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
        for (int i = 0; i < codes.length(); ) {
            ChessRuntimeData chessRuntimeData;
            ChessType chessType;
            final int tempCol = col;
            final int tempRow = row;
            if (xingyingList.stream().anyMatch(it -> it.getCurrentPos().getCol() == tempCol
                && it.getCurrentPos().getRow() == tempRow)) {
                chessType = ChessType.EMPTY;
                chessRuntimeData = ChessRuntimeData.builder()
                    .pos(new SimplePos(row, col))
                    .chessType(chessType)
                    .chessSide(ChessSide.EMPTY)
                    .build();

            } else {
                String code = String.valueOf(codes.charAt(i));
                chessType = ChessType.fromCode(code);
                chessRuntimeData = ChessRuntimeData.builder()
                    .pos(new SimplePos(row, col))
                    .chessType(chessType)
                    .chessSide(chessSide)
                    .build();
                i++;
            }
            chessRuntimeData.updateUiPos(layoutConst);
            result.add(chessRuntimeData);
            col++;
            if (col > 4) {
                col = 0;
                row++;
            }
        }
        return result;
    }






}
