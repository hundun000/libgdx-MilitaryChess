package hundun.militarychess.logic;

import hundun.militarychess.logic.LogicContext.AiAction;
import hundun.militarychess.logic.LogicContext.ChessShowMode;
import hundun.militarychess.logic.LogicContext.ChessState;
import hundun.militarychess.logic.LogicContext.PlayerMode;
import hundun.militarychess.logic.chess.ChessRule.BattleResult;
import hundun.militarychess.logic.chess.ChessRule.BattleResultType;
import hundun.militarychess.logic.chess.ChessType;
import hundun.militarychess.logic.chess.LogicFlag;
import hundun.militarychess.logic.chess.GridPosition;
import hundun.militarychess.logic.data.ArmyRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData.ChessBattleStatus;
import hundun.militarychess.logic.data.ChessRuntimeData.ChessSide;
import hundun.militarychess.logic.map.StageConfig;
import hundun.militarychess.ui.MilitaryChessGame;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 所有逻辑类数据的集合，UI类从本类中读写数据。
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
public class CrossScreenDataPackage {
    MilitaryChessGame game;

    PlayerMode playerMode;
    ChessShowMode chessShowMode;
    ChessSide pvcPlayerSide;
    ChessSide currentSide;
    ChessState currentState;
    Set<ChessSide> currentChessShowSides;
    List<ChessRuntimeData> moreChessList;
    Map<ChessSide, ArmyRuntimeData> armyMap;
    AiAction aiAction;
    ChessSide loseSide;
    String loseReason;
    /**
     * 连续未吃子计数器
     */
    int notKillTurnCount;

    ChessRuntimeData battleFromChess;
    ChessRuntimeData battleToChess;
    BattleResult battleResult;

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

    /**
     * 战斗后或开局时调用一次，更新数据
     */
    public void updateAfterFightOrStart() {
        // PVC时生成aiAction
        if (playerMode == PlayerMode.PVC) {
            if (currentSide != pvcPlayerSide) {
                aiAction = game.getLogicContext().getAiLogic().generateAiAction(
                    armyMap.get(ChessSide.BLUE_SIDE),
                    armyMap.get(ChessSide.RED_SIDE),
                    this
                );
            } else {
                aiAction = null;
            }
        }
        // 更新暗棋影响
        currentChessShowSides.clear();
        currentChessShowSides.add(ChessSide.EMPTY);
        if (chessShowMode == ChessShowMode.MING_QI) {
            currentChessShowSides.addAll(armyMap.keySet());
        } else {
            if (playerMode == PlayerMode.PVP) {
                currentChessShowSides.add(currentSide);
            } else {
                currentChessShowSides.add(pvcPlayerSide);
            }
        }
        // 检查是否已结束
        armyMap.forEach((key, value) -> {
            boolean noneCanMove = value.getChessRuntimeDataList().stream()
                .noneMatch(chess -> chess.getChessType().isCanMove());
            if (noneCanMove) {
                loseSide = key;
                loseReason = "没有棋子可走";
            }
            boolean junqiDied = value.getChessRuntimeDataList().stream()
                .noneMatch(chess -> chess.getChessType() == ChessType.JUN_QI);
            if (junqiDied) {
                loseSide = key;
                loseReason = "军棋已死亡";
            }
            boolean timeout = value.getUsedTime() > 30 * 60;
            if (timeout) {
                loseSide = key;
                loseReason = "累计用时超过30分钟";
            }
        });
        if (notKillTurnCount == 31) {
            loseSide = currentSide;
            loseReason = "连续31步未吃子";
        }
    }

    public void commitFightResult(LogicContext logicContext) {
        logicContext.getChessRule().onBattleCommit(this.battleResult);

        var commitedBattleResultType = battleResult.getBattleResultType();
        // 更新当前方
        if (currentSide == ChessSide.RED_SIDE) {
            currentSide = ChessSide.BLUE_SIDE;
        } else {
            currentSide = ChessSide.RED_SIDE;
        }
        game.getFrontend().log(this.getClass().getSimpleName(), "currentSide change to " + currentSide);
        // 更新阶段
        this.setCurrentState(ChessState.WAIT_SELECT_FROM);
        // 更新连续未吃子计数器
        if (commitedBattleResultType != BattleResultType.FROM_WIN
            && commitedBattleResultType != BattleResultType.TO_WIN
            && commitedBattleResultType != BattleResultType.BOTH_DIE) {
            notKillTurnCount++;
        } else {
            notKillTurnCount = 0;
        }
        updateAfterFightOrStart();
    }


    /**
     * 当前执棋方统计耗时
     */
    public void currentSideAddTime(int second) {
        armyMap.get(currentSide).setUsedTime(armyMap.get(currentSide).getUsedTime() + second);
    }

    public void prepareDone(StageConfig stageConfig) {
        List<GridPosition> xingyingList = stageConfig.getTileBuilders().stream()
            .filter(it -> it.getLogicFlags().contains(LogicFlag.XING_YING))
            .map(it -> it.getPosition())
            .collect(Collectors.toList());
        this.armyMap = Map.of(
            ChessSide.RED_SIDE,
            ArmyRuntimeData.builder()
                .chessRuntimeDataList(ChessRuntimeData.fromCodes(
                    xingyingList,
                    stageConfig.getRedArmyCode(),
                    game.getScreenContext().getLayoutConst(),
                    ChessSide.RED_SIDE))
                .build(),
            ChessSide.BLUE_SIDE,
            ArmyRuntimeData.builder()
                .chessRuntimeDataList(ChessRuntimeData.fromCodes(
                    xingyingList,
                    stageConfig.getBlueArmyCode(),
                    game.getScreenContext().getLayoutConst(),
                    ChessSide.BLUE_SIDE))
                .build()
        );
        List<GridPosition> armyPosList = Stream.concat(
            armyMap.get(ChessSide.RED_SIDE).getChessRuntimeDataList().stream(),
            armyMap.get(ChessSide.BLUE_SIDE).getChessRuntimeDataList().stream()
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

        this.updateAfterFightOrStart();
    }
}
