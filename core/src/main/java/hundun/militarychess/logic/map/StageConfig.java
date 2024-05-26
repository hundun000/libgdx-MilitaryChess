package hundun.militarychess.logic.map;

import hundun.militarychess.logic.chess.lose.ILoseChecker;
import hundun.militarychess.logic.data.ArmyRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData.ChessSide;
import hundun.militarychess.logic.map.tile.TileBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StageConfig {
    ILoseChecker loseChecker;
    Map<ChessSide, ArmyRuntimeData> armyMap;
    List<TileBuilder> tileBuilders;

}
