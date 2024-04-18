package hundun.militarychess.logic;

import hundun.militarychess.logic.chess.LogicFlag;
import hundun.militarychess.logic.chess.GridPosition;
import hundun.militarychess.logic.map.tile.ITileNode;
import hundun.militarychess.logic.map.tile.TileNeighborDirection;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

public class TileModel implements ITileNode<Void> {

    GridPosition position;
    @Getter
    List<LogicFlag> logicFlags;
    @Getter
    boolean hasDiagonalNeighbor;
    Map<TileNeighborDirection, ITileNode<Void>> physicalNeighbors;
    @Setter
    @Getter
    Map<TileNeighborDirection, ITileNode<Void>> logicalNeighbors;

    TileModel(GridPosition position, List<LogicFlag> logicFlags, boolean hasDiagonalNeighbor) {
        this.position = position;
        this.logicFlags = logicFlags;
        this.hasDiagonalNeighbor = hasDiagonalNeighbor;
    }

    @Override
    public GridPosition getPosition() {
        return position;
    }

    @Override
    public Map<TileNeighborDirection, ITileNode<Void>> getPhysicalNeighbors() {
        return physicalNeighbors;
    }

    @Override
    public void setPhysicalNeighbors(Map<TileNeighborDirection, ITileNode<Void>> value) {
        this.physicalNeighbors = value;
    }
}
