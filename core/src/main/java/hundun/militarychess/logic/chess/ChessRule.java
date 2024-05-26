package hundun.militarychess.logic.chess;

import com.badlogic.gdx.utils.Json;
import hundun.militarychess.logic.LogicContext;
import hundun.militarychess.logic.TileModel;
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

    final LogicContext logicContext;
    public ChessRule(LogicContext logicContext) {
        this.logicContext = logicContext;
    }

    public boolean canMove(ChessRuntimeData from, ChessRuntimeData to) {
        // 不能重叠自己的棋子
        if (from.getChessSide() == to.getChessSide()) {
            return false;
        }
        // 某些ChessType不可移动
        if (!from.getChessType().isCanMove()) {
            return false;
        }
        TileModel fromGameboardPos = logicContext.getTileMap().getWorldConstructionAt(from.getPos());
        TileModel toGameboardPos = logicContext.getTileMap().getWorldConstructionAt(to.getPos());
        // 不能从大本营移出
        if (fromGameboardPos.getLogicFlags().contains(LogicFlag.DA_BEN_YING)) {
            return false;
        }
        // 不能移入非空行营
        if (toGameboardPos.getLogicFlags().contains(LogicFlag.XING_YING) && to.getChessSide() != ChessSide.EMPTY) {
            return false;
        }
        return true;
    }

    @Getter
    public enum BattleResultType {
        CAN_NOT("不合法"),
        JUST_MOVE("移动"),
        FROM_WIN("发起者胜"),
        TO_WIN("发起者败"),
        BOTH_DIE("同尽"),
        NO_WIN("各有损失"),
        ;
        final String chinese;
        BattleResultType(String chinese){
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
        BattleResultType battleResultType;
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

    public BattleResult getFightV2Result(ChessRuntimeData from, ChessRuntimeData to) {
        BattleResultType battleResultType;
        boolean specialBattle;
        List<BattleDamageFrame> subFrames = new ArrayList<>();
        Map<String, BattleFrameTempData> tempDataMap = Map.of(
            from.getId(), BattleFrameTempData.fromRuntimeData(from),
            to.getId(), BattleFrameTempData.fromRuntimeData(to)
        );

        if (!canMove(from, to)) {
            battleResultType = BattleResultType.CAN_NOT;
            specialBattle = false;
        } else if (from.getChessType() == ChessType.ZHA_DAN || to.getChessType() == ChessType.ZHA_DAN) {
            battleResultType = BattleResultType.BOTH_DIE;
            specialBattle = true;
        } else if (to.getChessType() == ChessType.DI_LEI) {
            specialBattle = true;
            if (from.getChessType() == ChessType.GONG_BING) {
                battleResultType = BattleResultType.FROM_WIN;
            } else {
                battleResultType = BattleResultType.BOTH_DIE;
            }
        } else if (to.getChessType() == ChessType.EMPTY) {
            specialBattle = true;
            battleResultType = BattleResultType.JUST_MOVE;
        } else {
            specialBattle = false;

            List<Pair<ChessRuntimeData, ChessRuntimeData>> subFramePairs = new ArrayList<>(
                List.of(
                    Pair.create(from, to),
                    Pair.create(to, from)
                )
            );

            battleResultType = BattleResultType.NO_WIN;
            for (int i = 0; i < subFramePairs.size(); i++) {
                Pair<ChessRuntimeData, ChessRuntimeData> pair = subFramePairs.get(i);
                BattleDamageFrame subFrame = calculateBattleDamageSubFrame(tempDataMap, pair.getFirst(), pair.getSecond());
                subFrames.add(subFrame);

                boolean fromDead = tempDataMap.get(from.getId()).getHp() == 0;
                boolean toDead = tempDataMap.get(to.getId()).getHp() == 0;

                if (fromDead || toDead) {
                    if (!fromDead) {
                        battleResultType = BattleResultType.FROM_WIN;
                    } else if (!toDead) {
                        battleResultType = BattleResultType.TO_WIN;
                    } else {
                        battleResultType = BattleResultType.BOTH_DIE;
                    }
                    break;
                }
            }

        }
        return BattleResult.builder()
            .from(from)
            .to(to)
            .battleResultType(battleResultType)
            .frames(subFrames)
            .specialBattle(specialBattle)
            .build();
    }

    public void onBattleCommit(BattleResult battleResult) {
        if (!battleResult.getFrames().isEmpty()) {
            Map<String, BattleFrameTempData> lastMap = battleResult.getFrames().get(battleResult.getFrames().size() - 1).getTempDataMapSnapshot();
            battleResult.from.getChessBattleStatus().setHp(lastMap.get(battleResult.from.getId()).getHp());
            battleResult.to.getChessBattleStatus().setHp(lastMap.get(battleResult.to.getId()).getHp());
        }

        if (battleResult.battleResultType == BattleResultType.BOTH_DIE || battleResult.battleResultType == BattleResultType.TO_WIN) {
            setAsDead(battleResult.from);
        }
        if (battleResult.battleResultType == BattleResultType.BOTH_DIE || battleResult.battleResultType == BattleResultType.FROM_WIN) {
            setAsDead(battleResult.to);
        }
        if (battleResult.battleResultType == BattleResultType.FROM_WIN || battleResult.battleResultType == BattleResultType.JUST_MOVE) {
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
    private void switchPos(ChessRuntimeData from, ChessRuntimeData to) {
        logicContext.getCrossScreenDataPackage().getGame().getFrontend().log(
            this.getClass().getSimpleName(),
            "switchPos from %s to %s",
            from.toText(),
            to.toText()
        );
        var temp = from.getPos();
        from.setPos(to.getPos());
        to.setPos(temp);
    }
}
