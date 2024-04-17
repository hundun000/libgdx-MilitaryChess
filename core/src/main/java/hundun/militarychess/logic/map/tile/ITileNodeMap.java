package hundun.militarychess.logic.map.tile;

import hundun.militarychess.logic.chess.GridPosition;

public interface ITileNodeMap<T> {

    ITileNode<T> getValidNodeOrNull(ITileNode<T> target, TileNeighborDirection it, GridPosition position);
}
