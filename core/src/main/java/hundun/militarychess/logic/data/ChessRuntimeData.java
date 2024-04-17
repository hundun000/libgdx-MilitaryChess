package hundun.militarychess.logic.data;

import hundun.gdxgame.gamelib.base.util.JavaFeatureForGwt;
import hundun.militarychess.logic.chess.ChessType;
import hundun.militarychess.logic.chess.GameboardPosRule;
import hundun.militarychess.logic.chess.GameboardPosRule.GameboardPosType;
import hundun.militarychess.logic.chess.GameboardPosRule.GameboardPos;
import hundun.militarychess.logic.chess.GameboardPosRule.GridPosition;
import hundun.militarychess.ui.screen.LayoutConst;
import lombok.*;

import java.util.*;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChessRuntimeData {
    String id;
    GridPosition pos;
    int uiX;
    int uiY;
    ChessType chessType;
    ChessSide chessSide;
    ChessBattleStatus chessBattleStatus;

    public String toText() {
        return this.getChessType().getChinese()
            + this.getPos().toText();
    }

    public void updateUiPos(
        LayoutConst layoutConst
    ) {
        int x = this.getPos().getX() * layoutConst.TILE_WIDTH;
        int y =  (12 - this.getPos().getY()) * layoutConst.TILE_HEIGHT;
        this.setUiX(x);
        this.setUiY(y);
    }

    @Getter
    public enum ChessSide {
        RED_SIDE("红方"),
        BLUE_SIDE("蓝方"),
        EMPTY(""),
        ;

        final String chinese;
        ChessSide(String chinese){
            this.chinese = chinese;
        }

        public static ChessSide getOpposite(ChessSide thiz) {
            switch (thiz) {
                case RED_SIDE:
                    return BLUE_SIDE;
                case BLUE_SIDE:
                    return RED_SIDE;
            }
            return EMPTY;
        }
    }

    public static List<ChessRuntimeData> fromCodes(String codes, LayoutConst layoutConst, ChessSide chessSide) {
        List<GameboardPos> xingyingList = GameboardPosRule.gameboardPosMap.values().stream()
            .filter(it -> it.getGameboardPosType() == GameboardPosType.XING_YING)
            .collect(Collectors.toList());

        List<ChessRuntimeData> result = new ArrayList<>();
        int row = chessSide == ChessSide.RED_SIDE ? 6 : 0;
        int col = 0;
        for (int i = 0; i < codes.length(); ) {
            ChessRuntimeData chessRuntimeData;
            ChessType chessType;
            final int tempCol = col;
            final int tempRow = row;
            final String id = UUID.randomUUID().toString();
            boolean isXingying = xingyingList.stream()
                .anyMatch(it -> it.getPos().getX() == tempCol
                    && it.getPos().getY() == tempRow
                );
            if (isXingying) {
                // 向行营位置放置空白
                chessType = ChessType.EMPTY;
                chessRuntimeData = ChessRuntimeData.builder()
                    .id(id)
                    .pos(new GridPosition(row, col))
                    .chessType(chessType)
                    .chessSide(ChessSide.EMPTY)
                    .build();

            } else {
                // 放置棋子
                String code = String.valueOf(codes.charAt(i));
                chessType = ChessType.fromCode(code);
                chessRuntimeData = ChessRuntimeData.builder()
                    .id(id)
                    .pos(new GridPosition(row, col))
                    .chessType(chessType)
                    .chessSide(chessSide)
                    .build();
                i++;
            }
            chessRuntimeData.updateUiPos(layoutConst);
            chessRuntimeData.setChessBattleStatus(ChessBattleStatus.createStatus(chessRuntimeData.getChessType()));
            result.add(chessRuntimeData);
            col++;
            if (col > 4) {
                col = 0;
                row++;
            }
        }
        return result;
    }



    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChessBattleStatus {
        int hp;
        int maxHp;
        int atk;
        int def;

        public static ChessBattleStatus createStatus(ChessType chessType) {
            switch (chessType) {
                case EMPTY:
                case DI_LEI:
                case ZHA_DAN:
                case JUN_QI:
                    return new ChessBattleStatus();
                default:
                    int maxHp = 200 - chessType.ordinal() * 10;
                    int atk = 20 - chessType.ordinal();
                    int def = (int) (10 - chessType.ordinal() * 0.25);
                    return new ChessBattleStatus(maxHp, maxHp, atk, def);
            }
        }


        public String getChinese() {
            return JavaFeatureForGwt.stringFormat("%s/%s", hp, maxHp);
        }
    }



}
