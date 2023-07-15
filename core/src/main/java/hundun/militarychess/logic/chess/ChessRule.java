package hundun.militarychess.logic.chess;

import hundun.militarychess.logic.data.ChessRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData.ChessSide;
import lombok.Getter;

import java.util.List;
import java.util.Vector;

public class ChessRule {




    public static FightResultType canMove(ChessRuntimeData from, ChessRuntimeData to) {
        if (from.getChessSide() == to.getChessSide()) {
            return FightResultType.CAN_NOT;
        }
        if (!from.getChessType().isCanMove()) {
            return FightResultType.CAN_NOT;
        }
        return getFightResult(from, to);
    }

    @Getter
    public enum FightResultType {
        CAN_NOT("不合法"),
        JUST_MOVE("移动"),
        FROM_WIN("发起者胜"),
        TO_WIN("发起者败"),
        BOTH_DIE("同尽"),
        ;
        final String chinese;
        FightResultType(String chinese){
            this.chinese = chinese;
        }

    }

    public static void fight(ChessRuntimeData from, ChessRuntimeData to) {
        FightResultType fightResultType = getFightResult(from, to);
        if (fightResultType == FightResultType.BOTH_DIE || fightResultType == FightResultType.TO_WIN) {
            setAsDead(from);
        }
        if (fightResultType == FightResultType.BOTH_DIE || fightResultType == FightResultType.FROM_WIN) {
            setAsDead(to);
        }
        if (fightResultType == FightResultType.FROM_WIN || fightResultType == FightResultType.JUST_MOVE) {
            switchPos(from, to);
        }
    }

    private static void setAsDead(ChessRuntimeData target) {
        target.setChessSide(ChessSide.EMPTY);
        target.setChessType(ChessType.EMPTY);
    }

    private static void switchPos(ChessRuntimeData from, ChessRuntimeData to) {
        var temp = from.getMainLocation();
        from.setMainLocation(to.getMainLocation());
        to.setMainLocation(temp);
    }

    private static FightResultType getFightResult(ChessRuntimeData from, ChessRuntimeData to) {
        if (from.getChessType() == ChessType.ZHA_DAN || to.getChessType() == ChessType.ZHA_DAN) {
            return FightResultType.BOTH_DIE;
        }
        if (to.getChessType() == ChessType.DI_LEI) {
            if (from.getChessType() == ChessType.GONG_BING) {
                return FightResultType.FROM_WIN;
            } else {
                return FightResultType.BOTH_DIE;
            }
        }
        if (to.getChessType() == ChessType.EMPTY) {
            return FightResultType.JUST_MOVE;
        }
        int delta = from.getChessType().getCode().charAt(0) - to.getChessType().getCode().charAt(0);
        if (delta < 0) {
            return FightResultType.FROM_WIN;
        } else if (delta > 0) {
            return FightResultType.TO_WIN;
        } else {
            return FightResultType.BOTH_DIE;
        }
    }
}
