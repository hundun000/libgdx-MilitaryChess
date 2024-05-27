package hundun.militarychess.logic.manager;

import hundun.militarychess.logic.LogicContext;
import hundun.militarychess.logic.data.ChessRuntimeData.ChessBattleStatus;
import hundun.militarychess.logic.map.TileModel;
import hundun.militarychess.logic.chess.ChessType;
import hundun.militarychess.logic.chess.LogicFlag;
import hundun.militarychess.logic.chess.GridPosition;
import hundun.militarychess.logic.data.ArmyRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData.ChessSide;
import hundun.militarychess.logic.StageConfig;
import hundun.militarychess.logic.map.tile.ITileNode;
import hundun.militarychess.logic.map.tile.ITileNodeMap;
import hundun.militarychess.logic.map.tile.SquareTileNodeUtils;
import hundun.militarychess.logic.map.tile.TileNeighborDirection;
import hundun.militarychess.ui.MilitaryChessGame;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MilitaryChessTileManager implements ITileNodeMap<Void>, IManager {

    public static class SpecialRuleTileConfig {
        boolean linkDiagonal;
    }

    public Map<String, SpecialRuleTileConfig> specialRuleTileConfigMap;
    @Getter
    public Map<String, TileModel> tileModelMap = new HashMap<>();
    @Getter
    private List<ChessRuntimeData> moreChessList;
    @Getter
    private Map<ChessSide, ArmyRuntimeData> armyMap;
    final LogicContext logicContext;
    final MilitaryChessGame game;
    public MilitaryChessTileManager(LogicContext logicContext) {
        this.logicContext = logicContext;
        this.game = logicContext.getGame();
    }

    @Override
    public void commitFightResult() {

    }

    @Override
    public void updateAfterFightOrStart() {

    }

    public ChessRuntimeData findAtPos(GridPosition pos) {
        ChessRuntimeData result = null;
        for (var armyRuntimeData : armyMap.values()) {
            result = armyRuntimeData.getChessRuntimeDataList().stream()
                .filter(chessRuntimeData -> chessRuntimeData.getPos().equals(pos))
                .findAny()
                .orElse(null);
            if (result != null) {
                return result;
            }
        }
        result = moreChessList.stream()
            .filter(chessRuntimeData -> chessRuntimeData.getPos().equals(pos))
            .findAny()
            .orElse(null);
        return result;
    }

    @Override
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

        this.armyMap = stageConfig.getArmyMap();

        List<GridPosition> armyPosList = Stream.concat(
                this.armyMap.get(ChessSide.RED_SIDE).getChessRuntimeDataList().stream(),
                this.armyMap.get(ChessSide.BLUE_SIDE).getChessRuntimeDataList().stream()
            )
            .map(it -> it.getPos())
            .collect(Collectors.toList());

        this.moreChessList = stageConfig.getTileBuilders().stream()
            .map(it -> it.getPosition())
            .filter(it -> !armyPosList.contains(it))
            .map(it -> {
                final String id = UUID.randomUUID().toString();
                return ChessRuntimeData.builder()
                    .id(id)
                    .pos(it)
                    .chessType(ChessType.EMPTY)
                    .chessSide(ChessSide.EMPTY)
                    .build();
            })
            .peek(it -> {
                it.updateUiPos(game.getScreenContext().getLayoutConst());
                it.setChessBattleStatus(ChessBattleStatus.createStatus(it.getChessType()));
            })
            .collect(Collectors.toList());
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
        CrossScreenDataManager crossScreenDataManager
    ) {
        Set<GridPosition> dirtyRailPosList = new HashSet<>();
        Set<GridPosition> result = new HashSet<>();

        GridPosition currentPos = fromChess.getPos();
        boolean canTurnDirection = fromChess.getChessType() == ChessType.GONG_BING;
        TileModel currentGameboardPos = tileModelMap.get(currentPos.toId());
        // 搜索相邻的可移动目的地
        currentGameboardPos.getLogicalNeighbors().values().forEach(checkingPos -> {
            ChessRuntimeData checkingChess = this.findAtPos(checkingPos.getPosition());
            if (checkingChess != null && logicContext.getChessRule().canMove(fromChess, checkingChess)) {
                result.add(checkingPos.getPosition());
            }
        });
        if (currentGameboardPos.getLogicFlags().contains(LogicFlag.RAIL)) {
            findRailMoveCandidates(fromChess, null, currentPos, canTurnDirection, crossScreenDataManager, result, dirtyRailPosList);
        }
        result.remove(currentPos);
        return result;
    }

    private void findRailMoveCandidates(ChessRuntimeData fromChess, Object o, GridPosition currentPos, boolean canTurnDirection, CrossScreenDataManager crossScreenDataManager, Set<GridPosition> result, Set<GridPosition> dirtyRailPosList) {
        // TODO
    }


}
