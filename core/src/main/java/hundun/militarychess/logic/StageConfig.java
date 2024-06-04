package hundun.militarychess.logic;

import hundun.militarychess.logic.LogicContext.ChessShowMode;
import hundun.militarychess.logic.LogicContext.PlayerMode;
import hundun.militarychess.logic.chess.GridPosition;
import hundun.militarychess.logic.manager.CrossScreenDataManager;
import hundun.militarychess.logic.chess.lose.ILoseChecker;
import hundun.militarychess.logic.data.ArmyRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData.ChessSide;
import hundun.militarychess.logic.map.tile.TileBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.math3.util.Pair;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StageConfig {
    PlayerMode playerMode;
    ChessShowMode chessShowMode;
    ChessSide pvcPlayerSide;
    ChessSide currentSide;

    ILoseChecker loseChecker;
    Map<ChessSide, ArmyRuntimeData> armyMap;
    List<TileBuilder> tileBuilders;
    List<Pair<GridPosition, GridPosition>> extraRemoveLogicNeighborPair;
    List<Pair<GridPosition, GridPosition>> extraAddLogicNeighborPair;
}
