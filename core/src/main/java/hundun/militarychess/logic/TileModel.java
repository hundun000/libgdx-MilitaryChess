package hundun.militarychess.logic;

import hundun.militarychess.logic.chess.GameboardPosType;
import hundun.militarychess.logic.chess.GridPosition;
import hundun.militarychess.logic.map.tile.ITileNode;
import hundun.militarychess.logic.map.tile.TileNeighborDirection;
import lombok.Getter;

import java.util.Map;

public class TileModel implements ITileNode<Void> {

    GridPosition position;
    @Getter
    GameboardPosType gameboardPosType;
    Map<TileNeighborDirection, ITileNode<Void>> neighbors;

    @Override
    public GridPosition getPosition() {
        return position;
    }

    public void setPosition(GridPosition position) {
        this.position = position;
    }

    @Override
    public Map<TileNeighborDirection, ITileNode<Void>> getNeighbors() {
        return neighbors;
    }

    @Override
    public void setNeighbors(Map<TileNeighborDirection, ITileNode<Void>> value) {
        this.neighbors = value;
    }
}
