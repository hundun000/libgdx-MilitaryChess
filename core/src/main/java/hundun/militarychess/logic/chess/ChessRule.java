package hundun.militarychess.logic.chess;

import hundun.militarychess.logic.chess.GameboardPosRule.GameboardPosType;
import hundun.militarychess.logic.chess.GameboardPosRule.GameboardPos;
import hundun.militarychess.logic.data.ChessRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData.ChessSide;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 行走和战斗规则
 */
public class ChessRule {


    public static boolean canMove(ChessRuntimeData from, ChessRuntimeData to) {
        // 不能重叠自己的棋子
        if (from.getChessSide() == to.getChessSide()) {
            return false;
        }
        // 某些ChessType不可移动
        if (!from.getChessType().isCanMove()) {
            return false;
        }
        GameboardPos fromGameboardPos = GameboardPosRule.gameboardPosMap.get(from.getPos());
        GameboardPos toGameboardPos = GameboardPosRule.gameboardPosMap.get(to.getPos());
        // 不能从大本营移出
        if (fromGameboardPos.getGameboardPosType() == GameboardPosType.DA_BEN_YING) {
            return false;
        }
        // 不能移入非空行营
        if (toGameboardPos.getGameboardPosType() == GameboardPosType.XING_YING && to.getChessSide() != ChessSide.EMPTY) {
            return false;
        }
        return true;
    }

    public static FightResultType fightResultPreview(ChessRuntimeData from, ChessRuntimeData to) {
        if (!canMove(from, to)) {
            return FightResultType.CAN_NOT;
        }
        return getFightResult(from, to);
    }

    @Getter
    public enum FightResultType {
        CAN_NOT("不合法"),
        JUST_MOVE("移动"),
        FROM_WIN("发起者胜"),
        TO_WIN("发起者败"),
        BOTH_DIE("同尽"),
        ;
        final String chinese;
        FightResultType(String chinese){
            this.chinese = chinese;
        }

    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BattleDamageFrame {
        /**
         * 此帧结束后的from方HP
         */
        int tempFromHp;
        /**
         * 此帧结束后的to方HP
         */
        int tempToHp;
        /**
         * from方（对to方—）造成的伤害
         */
        int damageFrom;
        /**
         * to方（对from方）造成的伤害
         */
        int damageTo;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BattleResult {
        ChessRuntimeData from;
        ChessRuntimeData to;
        FightResultType fightResultType;
        List<BattleDamageFrame> frames;
        boolean specialBattle;
    }

    public static BattleResult getFightV2Result(ChessRuntimeData from, ChessRuntimeData to) {
        FightResultType fightResultType = null;
        boolean specialBattle;
        List<BattleDamageFrame> frames = new ArrayList<>();
        BattleDamageFrame lastFrame = BattleDamageFrame.builder()
            .tempFromHp(from.getChessBattleStatus().getHp())
            .tempToHp(to.getChessBattleStatus().getHp())
            .damageFrom(0)
            .damageTo(0)
            .build();
        // first frame for init HUD
        frames.add(lastFrame);

        if (from.getChessType() == ChessType.ZHA_DAN || to.getChessType() == ChessType.ZHA_DAN) {
            fightResultType = FightResultType.BOTH_DIE;
            specialBattle = true;
        } else if (to.getChessType() == ChessType.DI_LEI) {
            specialBattle = true;
            if (from.getChessType() == ChessType.GONG_BING) {
                fightResultType = FightResultType.FROM_WIN;
            } else {
                fightResultType = FightResultType.BOTH_DIE;
            }
        } else if (to.getChessType() == ChessType.EMPTY) {
            specialBattle = true;
            fightResultType = FightResultType.JUST_MOVE;
        } else {
            specialBattle = false;

            boolean nexFrame = true;
            while (nexFrame) {
                int damageFrom = from.getChessBattleStatus().getAtk() - to.getChessBattleStatus().getDef();
                int damageTo = to.getChessBattleStatus().getAtk() - from.getChessBattleStatus().getDef();
                int tempFromHp = lastFrame.getTempFromHp();
                int tempToHp = lastFrame.getTempToHp();
                tempFromHp = Math.max(tempFromHp - damageTo, 0);
                tempToHp = Math.max(tempToHp - damageTo, 0);

                lastFrame = BattleDamageFrame.builder()
                    .tempFromHp(tempFromHp)
                    .tempToHp(tempToHp)
                    .damageFrom(damageFrom)
                    .damageTo(damageTo)
                    .build();
                frames.add(lastFrame);

                if (tempFromHp == 0 || tempToHp == 0) {
                    nexFrame = false;
                    if (tempFromHp > 0) {
                        fightResultType = FightResultType.FROM_WIN;
                    } else if (tempToHp > 0) {
                        fightResultType = FightResultType.TO_WIN;
                    } else {
                        fightResultType = FightResultType.BOTH_DIE;
                    }
                }
            }
        }
        return BattleResult.builder()
            .from(from)
            .to(to)
            .fightResultType(fightResultType)
            .frames(frames)
            .specialBattle(specialBattle)
            .build();
    }

    public static void onBattleCommit(BattleResult battleResult) {
        if (battleResult.fightResultType == FightResultType.BOTH_DIE || battleResult.fightResultType == FightResultType.TO_WIN) {
            setAsDead(battleResult.from);
        }
        if (battleResult.fightResultType == FightResultType.BOTH_DIE || battleResult.fightResultType == FightResultType.FROM_WIN) {
            setAsDead(battleResult.to);
        }
        if (battleResult.fightResultType == FightResultType.FROM_WIN || battleResult.fightResultType == FightResultType.JUST_MOVE) {
            switchPos(battleResult.from, battleResult.to);
        }
    }

    /**
     * 死亡即变成空地
     */
    private static void setAsDead(ChessRuntimeData target) {
        target.setChessSide(ChessSide.EMPTY);
        target.setChessType(ChessType.EMPTY);
    }

    /**
     * 交换位置。和空地交换位置即为移动。
     */
    private static void switchPos(ChessRuntimeData from, ChessRuntimeData to) {
        var temp = from.getPos();
        from.setPos(to.getPos());
        to.setPos(temp);
    }

    private static FightResultType getFightResult(ChessRuntimeData from, ChessRuntimeData to) {
        if (from.getChessType() == ChessType.ZHA_DAN || to.getChessType() == ChessType.ZHA_DAN) {
            return FightResultType.BOTH_DIE;
        }
        if (to.getChessType() == ChessType.DI_LEI) {
            if (from.getChessType() == ChessType.GONG_BING) {
                return FightResultType.FROM_WIN;
            } else {
                return FightResultType.BOTH_DIE;
            }
        }
        if (to.getChessType() == ChessType.EMPTY) {
            return FightResultType.JUST_MOVE;
        }
        int delta = from.getChessType().getCode().charAt(0) - to.getChessType().getCode().charAt(0);
        if (delta < 0) {
            return FightResultType.FROM_WIN;
        } else if (delta > 0) {
            return FightResultType.TO_WIN;
        } else {
            return FightResultType.BOTH_DIE;
        }
    }
}
