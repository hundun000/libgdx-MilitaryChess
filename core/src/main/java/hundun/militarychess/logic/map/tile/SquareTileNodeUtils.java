package hundun.militarychess.logic.map.tile;

import hundun.militarychess.logic.chess.GridPosition;

import java.util.HashMap;
import java.util.List;

public class SquareTileNodeUtils {
    private static final List<TileNeighborDirection> values = List.of(TileNeighborDirection.values());

    public static <T> void updatePhysicalNeighborsAllStep(ITileNode<T> target, ITileNodeMap<T> map) {
        // update self
        updatePhysicalNeighborsOneStep(target, map);
        // update new neighbors
        target.getPhysicalNeighbors().values().stream()
            .filter(it -> it != null)
            .forEach(it -> updatePhysicalNeighborsOneStep(it, map));
    }

    private static <T> void updatePhysicalNeighborsOneStep(ITileNode<T> target, ITileNodeMap<T> map)
    {

        target.setPhysicalNeighbors(new HashMap<>());

        values.forEach(it -> {
            GridPosition position = tileNeighborPosition(target.getPosition(), it);
            ITileNode<T> neighbor = map.getValidNodeOrNull(target, it, position);
            if (neighbor != null) {
                target.getPhysicalNeighbors().put(it, neighbor);
            }
        });

    }


    private static <T> GridPosition tileNeighborPosition(GridPosition gridPosition, TileNeighborDirection direction)
    {
        switch (direction) {
            case LEFT_UP:
                return tileLeftUpNeighbor(gridPosition);
            case LEFT_MID:
                return tileLeftMidNeighbor(gridPosition);
            case LEFT_DOWN:
                return tileLeftDownNeighbor(gridPosition);
            case VERTICAL_UP:
                return tileVerticalUpNeighbor(gridPosition);
            case VERTICAL_DOWN:
                return tileVerticalDownNeighbor(gridPosition);
            case RIGHT_UP:
                return tileRightUpNeighbor(gridPosition);
            case RIGHT_MID:
                return tileRightMidNeighbor(gridPosition);
            case RIGHT_DOWN:
                return tileRightDownNeighbor(gridPosition);
        }
        return null;
    }

    private static GridPosition tileVerticalDownNeighbor(GridPosition gridPosition) {
        int y;
        int x;

        y = gridPosition.getY() - 1;
        x = gridPosition.getX();

        return new GridPosition(x, y);
    }

    private static GridPosition tileVerticalUpNeighbor(GridPosition gridPosition) {
        int y;
        int x;

        y = gridPosition.getY() + 1;
        x = gridPosition.getX();

        return new GridPosition(x, y);
    }


    private static <T> GridPosition tileRightMidNeighbor(GridPosition gridPosition)
    {
        int y;
        int x;

        y = gridPosition.getY();
        x = gridPosition.getX() + 1;

        return new GridPosition(x, y);
    }


    private static <T> GridPosition tileRightUpNeighbor(GridPosition gridPosition)
    {
        int y;
        int x;

        y = gridPosition.getY() + 1;
        x = gridPosition.getX() + 1;

        return new GridPosition(x, y);
    }


    private static <T> GridPosition tileRightDownNeighbor(GridPosition gridPosition)
    {
        int y;
        int x;

        y = gridPosition.getY() - 1;
        x = gridPosition.getX() + 1;

        return new GridPosition(x, y);
    }

    private static <T> GridPosition tileLeftUpNeighbor(GridPosition gridPosition)
    {
        int y;
        int x;

        y = gridPosition.getY() + 1;
        x = gridPosition.getX() - 1;

        return new GridPosition(x, y);
    }

    private static <T> GridPosition tileLeftMidNeighbor(GridPosition gridPosition)
    {
        int y;
        int x;

        y = gridPosition.getY();
        x = gridPosition.getX() - 1;

        return new GridPosition(x, y);
    }

    private static <T> GridPosition tileLeftDownNeighbor(GridPosition gridPosition)
    {
        int y;
        int x;

        y = gridPosition.getY() - 1;
        x = gridPosition.getX() - 1;

        return new GridPosition(x, y);
    }
}
