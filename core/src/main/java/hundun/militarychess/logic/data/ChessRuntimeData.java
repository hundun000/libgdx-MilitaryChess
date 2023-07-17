package hundun.militarychess.logic.data;

import hundun.militarychess.logic.chess.ChessType;
import hundun.militarychess.logic.chess.PosRule;
import hundun.militarychess.logic.chess.PosRule.SimplePos;
import hundun.militarychess.logic.data.generic.ChessPosData;
import hundun.militarychess.ui.screen.LayoutConst;
import lombok.*;

import java.util.*;

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
            if (row == 2 || row == 4 || row == 7 || row == 9) {
                col += 2;
            } else if (row == 3 || row == 8) {
                if (col == 1) {
                    col += 2;
                } else {
                    col += 1;
                }
            } else {
                col += 1;
            }
            if (col > 4) {
                col = 0;
                row++;
            }
        }
        PosRule.XING_YING_POS_MAP.get(chessSide).forEach(it -> {
            ChessType chessType = ChessType.EMPTY;
            ChessPosData mainLocation =  ChessPosData.builder()
                .pos(it)
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
