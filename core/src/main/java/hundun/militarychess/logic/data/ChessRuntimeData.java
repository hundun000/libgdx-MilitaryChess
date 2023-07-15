package hundun.militarychess.logic.data;

import hundun.militarychess.logic.chess.ChessType;
import hundun.militarychess.logic.data.generic.GenericPosData;
import hundun.militarychess.ui.screen.LayoutConst;
import lombok.*;

import java.util.*;
import java.util.stream.Stream;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChessRuntimeData {
    GenericPosData mainLocation;
    ChessType chessType;
    ChessSide chessSide;

    @Getter
    public enum ChessSide {
        MY_SIDE("红方"),
        OTHER_SIDE("蓝方"),
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
        int row = chessSide == ChessSide.MY_SIDE ? 6 : 0;
        int col = 0;
        for (int i = 0; i < codes.length(); i++) {
            String code = String.valueOf(codes.charAt(i));
            ChessType chessType = ChessType.fromCode(code);
            GenericPosData mainLocation =  GenericPosData.builder()
                .row(row)
                .col(col)
                .build();
            LayoutConst.updatePos(mainLocation, layoutConst);
            result.add(ChessRuntimeData.builder()
                    .mainLocation(mainLocation)
                    .chessType(chessType)
                    .chessSide(chessSide)
                    .build());
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
        XING_YING_POS_MAP.get(chessSide).forEach(it -> {
            ChessType chessType = ChessType.EMPTY;
            GenericPosData mainLocation =  GenericPosData.builder()
                .row(it.get(1))
                .col(it.get(0))
                .build();
            LayoutConst.updatePos(mainLocation, layoutConst);
            result.add(ChessRuntimeData.builder()
                .mainLocation(mainLocation)
                .chessType(chessType)
                .chessSide(ChessSide.EMPTY)
                .build());
        });
        return result;
    }


    static Map<ChessSide, List<Vector<Integer>>> XING_YING_POS_MAP = Map.of(
        ChessSide.MY_SIDE,
        List.of(
            toVector(1, 7),
            toVector(3, 7),
            toVector(2, 8),
            toVector(1, 9),
            toVector(3, 9)
        ),
        ChessSide.OTHER_SIDE,
        List.of(
            toVector(1, 2),
            toVector(3, 2),
            toVector(2, 3),
            toVector(1, 4),
            toVector(3, 4)
        )
    );

    static Map<ChessSide, List<Vector<Integer>>> DA_BEN_YING_POS_MAP = Map.of(
        ChessSide.MY_SIDE,
        List.of(
            toVector(1, 11),
            toVector(3, 11)
        ),
        ChessSide.OTHER_SIDE,
        List.of(
            toVector(1, 0),
            toVector(3, 0)
        )
    );


    private static <T> Vector<T> toVector(T... values) {
        return new Vector<>(Arrays.asList(values));
    }

}
