package hundun.militarychess.logic.data;

import hundun.gdxgame.gamelib.base.util.JavaFeatureForGwt;
import hundun.militarychess.logic.chess.ChessType;
import hundun.militarychess.logic.chess.GridPosition;
import hundun.militarychess.ui.screen.LayoutConst;
import lombok.*;

import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChessRuntimeData {
    String id;
    GridPosition pos;
    //int uiX;
    //int uiY;
    ChessType chessType;
    ChessSide chessSide;
    ChessBattleStatus chessBattleStatus;

    public String toText() {
        return this.getChessType().getChinese()
            + this.getPos().toText();
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
