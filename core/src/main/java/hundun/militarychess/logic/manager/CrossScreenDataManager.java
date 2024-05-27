package hundun.militarychess.logic.manager;

import hundun.militarychess.logic.LogicContext;
import hundun.militarychess.logic.LogicContext.AiAction;
import hundun.militarychess.logic.LogicContext.ChessShowMode;
import hundun.militarychess.logic.LogicContext.ChessState;
import hundun.militarychess.logic.LogicContext.PlayerMode;
import hundun.militarychess.logic.chess.ChessType;
import hundun.militarychess.logic.chess.GridPosition;
import hundun.militarychess.logic.data.ArmyRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData.ChessBattleStatus;
import hundun.militarychess.logic.data.ChessRuntimeData.ChessSide;
import hundun.militarychess.logic.StageConfig;
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
public class CrossScreenDataManager implements IManager {
    final LogicContext logicContext;
    final MilitaryChessGame game;
    PlayerMode playerMode;
    ChessShowMode chessShowMode;
    ChessSide pvcPlayerSide;
    ChessSide currentSide;
    ChessState currentState;
    AiAction aiAction;
    Set<ChessSide> currentChessShowSides;


    public CrossScreenDataManager(LogicContext logicContext) {
        this.logicContext = logicContext;
        this.game = logicContext.getGame();
    }

    @Override
    public void updateAfterFightOrStart() {
        // PVC时生成aiAction
        if (this.playerMode == PlayerMode.PVC) {
            if (this.currentSide != this.pvcPlayerSide) {
                aiAction = game.getLogicContext().getAiLogic().generateAiAction(
                    logicContext.getChessTileManager().getArmyMap().get(ChessSide.BLUE_SIDE),
                    logicContext.getChessTileManager().getArmyMap().get(ChessSide.RED_SIDE),
                    this
                );
            } else {
                aiAction = null;
            }
        }
        // 更新暗棋影响
        this.currentChessShowSides.clear();
        this.currentChessShowSides.add(ChessSide.EMPTY);
        if (this.chessShowMode == ChessShowMode.MING_QI) {
            this.currentChessShowSides.addAll(logicContext.getChessTileManager().getArmyMap().keySet());
        } else {
            if (this.playerMode == PlayerMode.PVP) {
                this.currentChessShowSides.add(this.currentSide);
            } else {
                this.currentChessShowSides.add(this.pvcPlayerSide);
            }
        }
    }

    @Override
    public void commitFightResult() {
       // 更新当前方
        if (this.currentSide == ChessSide.RED_SIDE) {
            this.currentSide = ChessSide.BLUE_SIDE;
        } else {
            this.currentSide = ChessSide.RED_SIDE;
        }
        game.getFrontend().log(this.getClass().getSimpleName(), "currentSide change to " + this.currentSide);
        // 更新阶段
        this.setCurrentState(ChessState.WAIT_SELECT_FROM);
    }


    /**
     * 当前执棋方统计耗时
     */
    public void currentSideAddTime(int second) {
        ArmyRuntimeData target = logicContext.getChessTileManager().getArmyMap()
            .get(this.currentSide);
        target.setUsedTime(target.getUsedTime() + second);
    }

    @Override
    public void prepareDone(StageConfig stageConfig) {
        this.playerMode = stageConfig.getPlayerMode();
        this.chessShowMode = stageConfig.getChessShowMode();
        this.pvcPlayerSide = stageConfig.getPvcPlayerSide();
        this.currentSide = stageConfig.getCurrentSide();
        this.currentState = ChessState.WAIT_SELECT_FROM;
        this.currentChessShowSides = new HashSet<>();
    }
}
