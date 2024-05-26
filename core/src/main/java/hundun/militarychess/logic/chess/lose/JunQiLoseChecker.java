package hundun.militarychess.logic.chess.lose;

import hundun.militarychess.logic.chess.ChessType;
import hundun.militarychess.logic.data.ArmyRuntimeData;

public class JunQiLoseChecker implements ILoseChecker {
    @Override
    public String checkLoseByArmy(ArmyRuntimeData value) {
        boolean junqiDied = value.getChessRuntimeDataList().stream()
            .noneMatch(chess -> chess.getChessType() == ChessType.JUN_QI);
        if (junqiDied) {
            return "军棋已死亡";
        }
        return null;
    }
}
