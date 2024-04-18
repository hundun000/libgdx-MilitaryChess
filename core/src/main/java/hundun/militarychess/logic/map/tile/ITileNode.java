package hundun.militarychess.logic.map.tile;

import hundun.militarychess.logic.chess.GridPosition;

import java.util.Map;

public interface ITileNode<T> {
    GridPosition getPosition();
    Map<TileNeighborDirection, ITileNode<T>> getPhysicalNeighbors();
    void setPhysicalNeighbors(Map<TileNeighborDirection, ITileNode<T>> value);
}
