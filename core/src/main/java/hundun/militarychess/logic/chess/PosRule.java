package hundun.militarychess.logic.chess;

import hundun.militarychess.logic.data.ChessRuntimeData.ChessSide;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

public class PosRule {


    public static List<SimplePos> XING_YING_POS_MAP = List.of(
            new SimplePos(7, 1),
            new SimplePos(7, 3),
            new SimplePos(8, 2),
            new SimplePos(9, 1),
            new SimplePos(9, 3),
            new SimplePos(2, 1),
            new SimplePos(2, 3),
            new SimplePos(3, 2),
            new SimplePos(4, 1),
            new SimplePos(4, 3)
    );

    public static List<SimplePos> DA_BEN_YING_POS_MAP = List.of(
            new SimplePos(11, 1),
            new SimplePos(11, 3),
            new SimplePos(0, 1),
            new SimplePos(0, 3)
    );



    private static SimplePos findSimplePos(int row, int col) {
        return simplePosMap.get(simplePosMapKey(row, col));
    }

    private static Integer simplePosMapKey(int row, int col) {
        return row * 100 + col;
    }

    public static final List<SimplePos> HORIZON_RAIL_1_POS_LIST = List.of(
        new SimplePos(1, 0),
        new SimplePos(1, 1),
        new SimplePos(1, 2),
        new SimplePos(1, 3),
        new SimplePos(1, 4)
        );
    public static final List<SimplePos> HORIZON_RAIL_5_POS_LIST = List.of(
        new SimplePos(5, 0),
        new SimplePos(5, 1),
        new SimplePos(5, 2),
        new SimplePos(5, 3),
        new SimplePos(5, 4)
        );
    public static final List<SimplePos> HORIZON_RAIL_6_POS_LIST = List.of(
        new SimplePos(6, 0),
        new SimplePos(6, 1),
        new SimplePos(6, 2),
        new SimplePos(6, 3),
        new SimplePos(6, 4)
    );
    public static final List<SimplePos> HORIZON_RAIL_10_POS_LIST = List.of(
        new SimplePos(10, 0),
        new SimplePos(10, 1),
        new SimplePos(10, 2),
        new SimplePos(10, 3),
        new SimplePos(10, 4)
    );
    public static final List<SimplePos> VERTICAL_RAIL_UP_LEFT_POS_LIST = List.of(
        new SimplePos(1, 0),
        new SimplePos(2, 0),
        new SimplePos(3, 0),
        new SimplePos(4, 0),
        new SimplePos(5, 0)
    );
    public static final List<SimplePos> VERTICAL_RAIL_DOWN_LEFT_POS_LIST = List.of(
        new SimplePos(6, 0),
        new SimplePos(7, 0),
        new SimplePos(8, 0),
        new SimplePos(9, 0),
        new SimplePos(10, 0)
    );
    public static final List<SimplePos> VERTICAL_RAIL_UP_RIGHT_POS_LIST = List.of(
        new SimplePos(1, 4),
        new SimplePos(2, 4),
        new SimplePos(3, 4),
        new SimplePos(4, 4),
        new SimplePos(5, 4)
    );
    public static final List<SimplePos> VERTICAL_RAIL_DOWN_RIGHT_POS_LIST = List.of(
        new SimplePos(6, 4),
        new SimplePos(7, 4),
        new SimplePos(8, 4),
        new SimplePos(9, 4),
        new SimplePos(10, 4)
    );
    public static final List<SimplePos> VERTICAL_RAIL_BRIDGE_LEFT_POS_LIST = List.of(
        new SimplePos(5, 0),
        new SimplePos(6, 0)
    );
    public static final List<SimplePos> VERTICAL_RAIL_BRIDGE_MID_POS_LIST = List.of(
        new SimplePos(5, 2),
        new SimplePos(6, 2)
    );
    public static final List<SimplePos> VERTICAL_RAIL_BRIDGE_RIGHT_POS_LIST = List.of(
        new SimplePos(5, 4),
        new SimplePos(6, 4)
    );

    public static final List<SimplePos> VERTICAL_RAIL_LONG_LEFT_POS_LIST;
    public static final List<SimplePos> VERTICAL_RAIL_LONG_RIGHT_POS_LIST;
    public static final List<List<SimplePos>> ALL_RAIL;
    public static Map<Integer, SimplePos> simplePosMap;
    public static Map<SimplePos, PosRelationData> relationMap;
    static {
        VERTICAL_RAIL_LONG_LEFT_POS_LIST = new ArrayList<>();
        VERTICAL_RAIL_LONG_LEFT_POS_LIST.addAll(VERTICAL_RAIL_UP_LEFT_POS_LIST);
        VERTICAL_RAIL_LONG_LEFT_POS_LIST.addAll(VERTICAL_RAIL_DOWN_LEFT_POS_LIST);

        VERTICAL_RAIL_LONG_RIGHT_POS_LIST = new ArrayList<>();
        VERTICAL_RAIL_LONG_RIGHT_POS_LIST.addAll(VERTICAL_RAIL_UP_RIGHT_POS_LIST);
        VERTICAL_RAIL_LONG_RIGHT_POS_LIST.addAll(VERTICAL_RAIL_DOWN_RIGHT_POS_LIST);

        ALL_RAIL = List.of(
            HORIZON_RAIL_1_POS_LIST,
            HORIZON_RAIL_5_POS_LIST,
            HORIZON_RAIL_6_POS_LIST,
            HORIZON_RAIL_10_POS_LIST,
            VERTICAL_RAIL_LONG_LEFT_POS_LIST,
            VERTICAL_RAIL_LONG_RIGHT_POS_LIST,
            VERTICAL_RAIL_BRIDGE_MID_POS_LIST
        );

        simplePosMap = new HashMap<>();
        for (int row = 0; row <= 11; row++) {
            for (int col = 0; col <= 4; col++) {
                simplePosMap.put(simplePosMapKey(row, col), new SimplePos(row, col));
            }
        }

        relationMap = new HashMap<>();
        simplePosMap.values().forEach(it -> {
            relationMap.put(it, calculate(it));
        });
    }
    private static void addNeighbour(PosRelationData thiz, SimplePos pos) {
        Collection<SimplePos> result = thiz.getNormalDestinationPosList();
        if (pos.getCol() - 1 >= 0) {
            result.add(findSimplePos(pos.getRow(), pos.getCol() - 1));
        }
        if (pos.getCol() + 1 <= 4) {
            result.add(findSimplePos(pos.getRow(), pos.getCol() + 1));
        }
        if (pos.getRow() - 1 >= 0) {
            result.add(findSimplePos(pos.getRow() - 1, pos.getCol()));
        }
        if (pos.getRow() + 1 <= 11) {
            result.add(findSimplePos(pos.getRow() + 1, pos.getCol()));
        }
    }


    public static PosRelationData calculate(SimplePos pos) {

        PosRelationData result = PosRelationData.builder()
            .currentPos(pos)
            .normalDestinationPosList(new HashSet<>())
            .leftRailDestinationPosList(new HashSet<>())
            .rightRailDestinationPosList(new HashSet<>())
            .upRailDestinationPosList(new HashSet<>())
            .downRailDestinationPosList(new HashSet<>())
            .build();
        addNeighbour(result, pos);

        final int row = pos.getRow();
        final int col = pos.getCol();
        ChessPosType chessPosType;
        if (row <= 0 || row >= 11) {
            if (DA_BEN_YING_POS_MAP.contains(pos)) {
                chessPosType = ChessPosType.DA_BEN_YING;
            } else {
                chessPosType = ChessPosType.BACK_NORMAL;
            }
        } else if (row == 1 || row == 5 || row == 6 || row == 10) {
            chessPosType = ChessPosType.RAIL;
            addRail(result, pos);
        } else {
            if (col == 0 || col == 5) {
                chessPosType = ChessPosType.RAIL;
                addRail(result, pos);
            } else {
                if (XING_YING_POS_MAP.contains(pos)) {
                    chessPosType = ChessPosType.XING_YING;
                } else {
                    chessPosType = ChessPosType.FRONT_NORMAL;
                }
            }
        }
        result.setChessPosType(chessPosType);
        return result;
    }

    private static void addRail(PosRelationData thiz, SimplePos pos) {
        ALL_RAIL.stream()
            .filter(railPosList -> railPosList.contains(pos))
            .forEach(railPosList -> {
                railPosList.
                    forEach(railPos -> {
                        if (railPos.getRow() == pos.getRow()) {
                            if (railPos.getCol() > pos.getCol()) {
                                thiz.getRightRailDestinationPosList().add(railPos);
                            } else if (railPos.getCol() < pos.getCol()) {
                                thiz.getLeftRailDestinationPosList().add(railPos);
                            }
                        } else if (railPos.getCol() == pos.getCol()) {
                            if (railPos.getRow() > pos.getRow()) {
                                thiz.getDownRailDestinationPosList().add(railPos);
                            } else if (railPos.getRow() < pos.getRow()) {
                                thiz.getUpRailDestinationPosList().add(railPos);
                            }
                        }
                });
            });
    }



    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PosRelationData {
        SimplePos currentPos;
        Set<SimplePos> normalDestinationPosList;
        Set<SimplePos> leftRailDestinationPosList;
        Set<SimplePos> rightRailDestinationPosList;
        Set<SimplePos> upRailDestinationPosList;
        Set<SimplePos> downRailDestinationPosList;
        ChessPosType chessPosType;
    }

    public enum ChessPosType {
        RAIL,
        FRONT_NORMAL,
        XING_YING,
        BACK_NORMAL,
        DA_BEN_YING,
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SimplePos {
        int row;
        int col;
    }


}
