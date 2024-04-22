package hundun.militarychess.logic;

import hundun.militarychess.logic.chess.ChessType;
import hundun.militarychess.logic.chess.LogicFlag;
import hundun.militarychess.logic.chess.GridPosition;
import hundun.militarychess.logic.data.ChessRuntimeData;
import hundun.militarychess.logic.map.StageConfig;
import hundun.militarychess.logic.map.tile.ITileNode;
import hundun.militarychess.logic.map.tile.ITileNodeMap;
import hundun.militarychess.logic.map.tile.SquareTileNodeUtils;
import hundun.militarychess.logic.map.tile.TileNeighborDirection;
import lombok.Getter;

import java.util.*;

public class MilitaryChessTileMap implements ITileNodeMap<Void> {

    public static class SpecialRuleTileConfig {
        boolean linkDiagonal;
    }

    public Map<String, SpecialRuleTileConfig> specialRuleTileConfigMap;
    @Getter
    public Map<String, TileModel> tileModelMap = new HashMap<>();

    final LogicContext logicContext;
    public MilitaryChessTileMap(LogicContext logicContext) {
        this.logicContext = logicContext;
    }

    public void prepareDone(StageConfig stageConfig) {

        stageConfig.getTileBuilders().forEach(it -> {
            TileModel tileModel = new TileModel(
                it.getPosition(),
                it.getLogicFlags(),
                it.isHasDiagonalNeighbor()
            );
            tileModelMap.put(tileModel.getPosition().toId(), tileModel);

            SquareTileNodeUtils.updatePhysicalNeighborsAllStep(tileModel, this);
        });

        tileModelMap.values().forEach(it -> {
            Map<TileNeighborDirection, ITileNode<Void>> logicalNeighbors = calculateLogicalNeighbors(it);
            it.setLogicalNeighbors(logicalNeighbors);
        });
    }


    public TileModel getWorldConstructionAt(GridPosition target)
    {
        return tileModelMap.get(target.toId());
    }

    @Override
    public ITileNode<Void> getValidNodeOrNull(ITileNode<Void> origin, TileNeighborDirection direction, GridPosition position) {
        TileModel originTileModel = getWorldConstructionAt(origin.getPosition());
        TileModel destinationTileModel = getWorldConstructionAt(position);
        /*if (destinationTileModel != null) {
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
        }*/
        return destinationTileModel;
    }

    static final List<TileNeighborDirection> DIAGONAL_DIRECTIONS = List.of(
        TileNeighborDirection.LEFT_UP,
        TileNeighborDirection.LEFT_DOWN,
        TileNeighborDirection.RIGHT_UP,
        TileNeighborDirection.RIGHT_DOWN
    );


    private Map<TileNeighborDirection, ITileNode<Void>> calculateLogicalNeighbors(TileModel tileModel) {
        Map<TileNeighborDirection, ITileNode<Void>> result = new HashMap<>(tileModel.getPhysicalNeighbors());
        if (!tileModel.isHasDiagonalNeighbor()) {
            result.entrySet().removeIf(it -> DIAGONAL_DIRECTIONS.contains(it.getKey()));
        }
        result.entrySet().removeIf(it -> {
            TileModel destinationTileModel = getWorldConstructionAt(it.getValue().getPosition());
            if (destinationTileModel.getLogicFlags().contains(LogicFlag.NO_PASS)) {
                return true;
            }
            if (DIAGONAL_DIRECTIONS.contains(it.getKey()) && !destinationTileModel.isHasDiagonalNeighbor()) {
                return true;
            }
            return false;
        });
        return result;
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
        currentGameboardPos.getLogicalNeighbors().values().forEach(checkingPos -> {
            ChessRuntimeData checkingChess = crossScreenDataPackage.findAtPos(checkingPos.getPosition());
            if (checkingChess != null && logicContext.getChessRule().canMove(fromChess, checkingChess)) {
                result.add(checkingPos.getPosition());
            }
        });
        if (currentGameboardPos.getLogicFlags().contains(LogicFlag.RAIL)) {
            findRailMoveCandidates(fromChess, null, currentPos, canTurnDirection, crossScreenDataPackage, result, dirtyRailPosList);
        }
        result.remove(currentPos);
        return result;
    }

    private void findRailMoveCandidates(ChessRuntimeData fromChess, Object o, GridPosition currentPos, boolean canTurnDirection, CrossScreenDataPackage crossScreenDataPackage, Set<GridPosition> result, Set<GridPosition> dirtyRailPosList) {
        // TODO
    }


}
