package hundun.militarychess.logic.chess;

import com.badlogic.gdx.utils.Json;
import hundun.militarychess.logic.chess.GameboardPosRule.GameboardPosType;
import hundun.militarychess.logic.chess.GameboardPosRule.GameboardPos;
import hundun.militarychess.logic.data.ChessRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData.ChessSide;
import lombok.*;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static class BattleFrameTempData {
        static Json json = new Json();
        int hp;

        public static BattleFrameTempData fromRuntimeData(ChessRuntimeData runtimeData) {
            return BattleFrameTempData.builder()
                .hp(runtimeData.getChessBattleStatus().getHp())
                .build();
        }

        public static Map<String, BattleFrameTempData> deepCopyMap(Map<String, BattleFrameTempData> tempDataMap) {
            Map<String, BattleFrameTempData> map = new HashMap<>(tempDataMap.size());
            tempDataMap.forEach((k, v) -> {
                String str = json.toJson(v);
                BattleFrameTempData vCopy = json.fromJson(BattleFrameTempData.class, str);
                map.put(k, vCopy);
            });
            return map;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BattleDamageFrame {
        ChessRuntimeData origin;
        ChessRuntimeData destination;
        int damage;
        Map<String, BattleFrameTempData> tempDataMapSnapshot;
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

    private static BattleDamageFrame calculateBattleDamageSubFrame(Map<String, BattleFrameTempData> tempDataMap, ChessRuntimeData origin, ChessRuntimeData destination) {
        int damage = origin.getChessBattleStatus().getAtk() - destination.getChessBattleStatus().getDef();
        BattleFrameTempData tempData = tempDataMap.get(destination.getId());
        tempData.hp = Math.max(tempData.hp - damage, 0);
        return BattleDamageFrame.builder()
            .origin(origin)
            .destination(destination)
            .damage(damage)
            .tempDataMapSnapshot(BattleFrameTempData.deepCopyMap(tempDataMap))
            .build();
    }

    public static BattleResult getFightV2Result(ChessRuntimeData from, ChessRuntimeData to) {
        FightResultType fightResultType = null;
        boolean specialBattle;
        List<BattleDamageFrame> subFrames = new ArrayList<>();
        Map<String, BattleFrameTempData> tempDataMap = Map.of(
            from.getId(), BattleFrameTempData.fromRuntimeData(from),
            to.getId(), BattleFrameTempData.fromRuntimeData(to)
        );

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

            List<Pair<ChessRuntimeData, ChessRuntimeData>> subFramePairs = new ArrayList<>(
                List.of(
                    Pair.create(from, to),
                    Pair.create(to, from)
                )
            );

            for (int i = 0; i < subFramePairs.size(); i++) {
                Pair<ChessRuntimeData, ChessRuntimeData> pair = subFramePairs.get(i);
                BattleDamageFrame subFrame = calculateBattleDamageSubFrame(tempDataMap, pair.getFirst(), pair.getSecond());
                subFrames.add(subFrame);

                boolean fromDead = tempDataMap.get(from.getId()).getHp() == 0;
                boolean toDead = tempDataMap.get(to.getId()).getHp() == 0;

                if (fromDead || toDead) {
                    if (!fromDead) {
                        fightResultType = FightResultType.FROM_WIN;
                    } else if (!toDead) {
                        fightResultType = FightResultType.TO_WIN;
                    } else {
                        fightResultType = FightResultType.BOTH_DIE;
                    }
                    break;
                }
            }

        }
        return BattleResult.builder()
            .from(from)
            .to(to)
            .fightResultType(fightResultType)
            .frames(subFrames)
            .specialBattle(specialBattle)
            .build();
    }

    public static void onBattleCommit(BattleResult battleResult) {
        if (!battleResult.getFrames().isEmpty()) {
            Map<String, BattleFrameTempData> lastMap = battleResult.getFrames().get(battleResult.getFrames().size() - 1).getTempDataMapSnapshot();
            battleResult.from.getChessBattleStatus().setHp(lastMap.get(battleResult.from.getId()).getHp());
            battleResult.to.getChessBattleStatus().setHp(lastMap.get(battleResult.to.getId()).getHp());
        }

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
