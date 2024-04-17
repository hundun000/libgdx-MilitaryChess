package hundun.militarychess.logic;

import hundun.militarychess.logic.LogicContext.CrossScreenDataPackage;
import hundun.militarychess.logic.chess.ChessRule;
import hundun.militarychess.logic.chess.ChessType;
import hundun.militarychess.logic.chess.GameboardPosType;
import hundun.militarychess.logic.chess.GridPosition;
import hundun.militarychess.logic.data.ChessRuntimeData;
import hundun.militarychess.logic.map.tile.ITileNode;
import hundun.militarychess.logic.map.tile.ITileNodeMap;
import hundun.militarychess.logic.map.tile.TileNeighborDirection;
import lombok.Getter;

import java.util.*;

public class MilitaryChessTileMap implements ITileNodeMap<Void> {

    public static class SpecialRuleTileConfig {
        boolean linkDiagonal;
    }

    public Map<String, SpecialRuleTileConfig> specialRuleTileConfigMap = new HashMap<>();
    @Getter
    public Map<String, TileModel> tileModelMap = new HashMap<>();

    final LogicContext logicContext;
    public MilitaryChessTileMap(LogicContext logicContext) {
        this.logicContext = logicContext;
    }

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

    public Set<GridPosition> finaAllMoveCandidates(
        ChessRuntimeData fromChess,
        CrossScreenDataPackage crossScreenDataPackage
    ) {
        Set<GridPosition> dirtyRailPosList = new HashSet<>();
        Set<GridPosition> result = new HashSet<>();

        GridPosition currentPos = fromChess.getPos();
        boolean canTurnDirection = fromChess.getChessType() == ChessType.GONG_BING;
        TileModel currentGameboardPos = tileModelMap.get(currentPos.toId());
        // 搜索相邻的可移动目的地
        currentGameboardPos.getNeighbors().values().forEach(checkingPos -> {
            ChessRuntimeData checkingChess = crossScreenDataPackage.findAtPos(checkingPos.getPosition());
            if (checkingChess != null && logicContext.getChessRule().canMove(fromChess, checkingChess)) {
                result.add(checkingPos.getPosition());
            }
        });
        if (currentGameboardPos.getGameboardPosType() == GameboardPosType.RAIL) {
            findRailMoveCandidates(fromChess, null, currentPos, canTurnDirection, crossScreenDataPackage, result, dirtyRailPosList);
        }
        result.remove(currentPos);
        return result;
    }

    private void findRailMoveCandidates(ChessRuntimeData fromChess, Object o, GridPosition currentPos, boolean canTurnDirection, CrossScreenDataPackage crossScreenDataPackage, Set<GridPosition> result, Set<GridPosition> dirtyRailPosList) {
        // TODO
    }


}
