package hundun.militarychess.logic.map.tile;

import hundun.militarychess.logic.chess.GameboardPosRule.GridPosition;

import java.util.Map;

public interface ITileNode<T> {
    GridPosition getPosition();
    Map<TileNeighborDirection, ITileNode<T>> getNeighbors();
    void setNeighbors(Map<TileNeighborDirection, ITileNode<T>> value);
}
