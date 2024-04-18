package hundun.militarychess.logic.map;

import hundun.militarychess.logic.MilitaryChessTileMap.SpecialRuleTileConfig;
import hundun.militarychess.logic.chess.GridPosition;
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
    String redArmyCode;
    String blueArmyCode;
    List<TileBuilder> tileBuilders;

}
