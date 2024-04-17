package hundun.militarychess.logic;

import hundun.militarychess.logic.chess.GameboardPosRule.GridPosition;
import hundun.militarychess.logic.map.tile.ITileNode;
import hundun.militarychess.logic.map.tile.ITileNodeMap;
import hundun.militarychess.logic.map.tile.TileNeighborDirection;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MilitaryChessTileMap implements ITileNodeMap<Void> {

    public static class SpecialRuleTileConfig {
        boolean linkDiagonal;
    }

    public Map<String, SpecialRuleTileConfig> specialRuleTileConfigMap = new HashMap<>();

    public Map<String, TileModel> tileModelMap = new HashMap<>();

    public TileModel getWorldConstructionAt(GridPosition target)
    {
        return tileModelMap.get(target.toId());
    }

    @Override
    public ITileNode<Void> getValidNodeOrNull(ITileNode<Void> origin, TileNeighborDirection direction, GridPosition position) {
        TileModel originTileModel = getWorldConstructionAt(origin.getPosition());
        TileModel destinationTileModel = getWorldConstructionAt(position);
        if (destinationTileModel != null) {
            SpecialRuleTileConfig originConfig = specialRuleTileConfigMap.get(origin.getPosition().toId());
            SpecialRuleTileConfig destinationConfig = specialRuleTileConfigMap.get(position.toId());
            boolean anyTileLinkDiagonal = Optional.ofNullable(originConfig).map(it -> it.linkDiagonal).orElse(false)
                || Optional.ofNullable(destinationConfig).map(it -> it.linkDiagonal).orElse(false);
            if (!anyTileLinkDiagonal) {
                if (direction == TileNeighborDirection.LEFT_UP
                    || direction == TileNeighborDirection.LEFT_DOWN
                    || direction == TileNeighborDirection.RIGHT_UP
                    || direction == TileNeighborDirection.RIGHT_DOWN
                ) {
                    return null;
                }
            }
        }
        return destinationTileModel;
    }
}
