package hundun.militarychess.logic.chess;

import com.badlogic.gdx.utils.Null;
import hundun.militarychess.logic.LogicContext.CrossScreenDataPackage;
import hundun.militarychess.logic.data.ChessRuntimeData;
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

/*    public static final List<SimplePos> HORIZON_RAIL_1_POS_LIST = List.of(
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
    public static final List<List<SimplePos>> ALL_RAIL;*/
    public static Map<Integer, SimplePos> simplePosMap;
    public static Map<SimplePos, PosRelationData> relationMap;
    static {
/*        VERTICAL_RAIL_LONG_LEFT_POS_LIST = new ArrayList<>();
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
        );*/

        simplePosMap = new HashMap<>();
        for (int row = 0; row <= 11; row++) {
            for (int col = 0; col <= 4; col++) {
                simplePosMap.put(simplePosMapKey(row, col), new SimplePos(row, col));
            }
        }

        relationMap = new HashMap<>();
        simplePosMap.values().forEach(it -> {
            relationMap.put(it, baseRelation(it));
        });
    }
    private static void addNeighbour(PosRelationData thiz, SimplePos pos, ChessPosType chessPosType) {
        if (pos.getCol() - 1 >= 0) {
            thiz.getNeighbourMap().put(Direction.LEFT, findSimplePos(pos.getRow(), pos.getCol() - 1));
        }
        if (pos.getCol() + 1 <= 4) {
            thiz.getNeighbourMap().put(Direction.RIGHT, findSimplePos(pos.getRow(), pos.getCol() + 1));
        }
        if (pos.getRow() - 1 >= 0) {
            // 特别的，（6,1）和（6,3）不连接UP
            if (!(pos.getRow() == 6 && pos.getCol() == 1) && !(pos.getRow() == 6 && pos.getCol() == 3)) {
                thiz.getNeighbourMap().put(Direction.UP, findSimplePos(pos.getRow() - 1, pos.getCol()));
            }
        }
        if (pos.getRow() + 1 <= 11) {
            // 特别的，（5,1）和（5,3）不连接DOWN
            if (!(pos.getRow() == 5 && pos.getCol() == 1) && !(pos.getRow() == 5 && pos.getCol() == 3)) {
                thiz.getNeighbourMap().put(Direction.DOWN, findSimplePos(pos.getRow() + 1, pos.getCol()));
            }
        }
        // 特别的，1/6/11行不连接LEFT_UP和RIGHT_UP
        if (pos.getRow() - 1 >= 0 && pos.getCol() - 1 >= 0) {
            if (pos.getRow() != 1 && pos.getRow() != 6 &&  pos.getRow() != 11) {
                thiz.getNeighbourMap().put(Direction.LEFT_UP, findSimplePos(pos.getRow() - 1, pos.getCol() - 1));
            }
        }
        if (pos.getRow() - 1 >= 0 && pos.getCol() + 1 >= 0) {
            if (pos.getRow() != 1 && pos.getRow() != 6 &&  pos.getRow() != 11) {
                thiz.getNeighbourMap().put(Direction.RIGHT_UP, findSimplePos(pos.getRow() - 1, pos.getCol() + 1));
            }
        }
        // 特别的，0/5/10行不连接LEFT_DOWN和RIGHT_DOWN
        if (pos.getRow() + 1 <= 11 && pos.getCol() - 1 >= 0) {
            if (pos.getRow() != 0 && pos.getRow() != 5 &&  pos.getRow() != 10) {
                thiz.getNeighbourMap().put(Direction.LEFT_DOWN, findSimplePos(pos.getRow() + 1, pos.getCol() - 1));
            }
        }
        if (pos.getRow() + 1 <= 11 && pos.getCol() + 1 >= 0) {
            if (pos.getRow() != 0 && pos.getRow() != 5 &&  pos.getRow() != 10) {
                thiz.getNeighbourMap().put(Direction.RIGHT_DOWN, findSimplePos(pos.getRow() + 1, pos.getCol() + 1));
            }
        }
    }


    private static PosRelationData baseRelation(SimplePos pos) {

        PosRelationData result = PosRelationData.builder()
            .currentPos(pos)
            .neighbourMap(new HashMap<>())
            .build();


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
        } else {
            if (col == 0 || col == 5) {
                chessPosType = ChessPosType.RAIL;
            } else {
                if (XING_YING_POS_MAP.contains(pos)) {
                    chessPosType = ChessPosType.XING_YING;
                } else {
                    chessPosType = ChessPosType.FRONT_NORMAL;
                }
            }
        }
        addNeighbour(result, pos, chessPosType);
        result.setChessPosType(chessPosType);
        return result;
    }





    public enum Direction {
        LEFT,
        RIGHT,
        UP,
        DOWN,
        LEFT_UP,
        RIGHT_UP,
        LEFT_DOWN,
        RIGHT_DOWN,
        ;
        public static final List<Direction> XYValues = List.of(
            LEFT,
            RIGHT,
            UP,
            DOWN
        );
        public static Direction getXYOpposite(Direction thiz) {
            switch (thiz) {
                case LEFT:
                    return RIGHT;
                case RIGHT:
                    return LEFT;
                case UP:
                    return DOWN;
                case DOWN:
                    return UP;
            }
            return null;
        }
    }

    public static Set<SimplePos> calculateCurrent(
        ChessRuntimeData fromChess,
        CrossScreenDataPackage crossScreenDataPackage
    ) {
        Set<SimplePos> dirtyRailPosList = new HashSet<>();
        Set<SimplePos> result = new HashSet<>();

        SimplePos currentPos = fromChess.getMainLocation().getPos();
        boolean canTurnDirection = fromChess.getChessType() == ChessType.GONG_BING;
        PosRelationData currentPosRelationData = PosRule.relationMap.get(currentPos);
        currentPosRelationData.getNeighbourMap().values().forEach(checkingPos -> {
            ChessRuntimeData checkingChess = crossScreenDataPackage.findAtPos(checkingPos);
            if (checkingChess != null && ChessRule.canMove(fromChess, checkingChess)) {
                result.add(checkingPos);
            }
        });

        findAloneRail(fromChess, null, currentPos, canTurnDirection, crossScreenDataPackage, result, dirtyRailPosList);
        result.remove(currentPos);
        return result;
    }

    private static void findAloneRail(
        ChessRuntimeData fromChess,
        @Null Direction currentDirection,
        SimplePos currentPos,
        boolean canTurnDirection,
        CrossScreenDataPackage crossScreenDataPackage,
        Set<SimplePos> result,
        Set<SimplePos> dirtyRailPosList
    ) {
        if (dirtyRailPosList.contains(currentPos)) {
            return;
        }
        result.add(currentPos);
        dirtyRailPosList.add(currentPos);
        PosRelationData currentPosRelationData = PosRule.relationMap.get(currentPos);
        for (Direction direction : Direction.XYValues) {
            SimplePos checkingPos = currentPosRelationData.getNeighbourMap().get(direction);
            if (checkingPos == null) {
                continue;
            }
            PosRelationData checkingPosRelationData = PosRule.relationMap.get(checkingPos);
            ChessRuntimeData checkingChess = crossScreenDataPackage.findAtPos(checkingPos);
            if (checkingPosRelationData.getChessPosType() != ChessPosType.RAIL || !ChessRule.canMove(fromChess, checkingChess)) {
                continue;
            }
            if (currentDirection == null || direction == currentDirection || Direction.getXYOpposite(direction) == currentDirection) {
                if (checkingChess.getChessSide() == ChessSide.EMPTY) {
                    findAloneRail(fromChess, direction, checkingPos, canTurnDirection, crossScreenDataPackage, result, dirtyRailPosList);
                } else {
                    if (!dirtyRailPosList.contains(checkingPos)) {
                        result.add(currentPos);
                        dirtyRailPosList.add(currentPos);
                    }
                }
            } else {
                if (canTurnDirection) {
                    if (checkingChess.getChessSide() == ChessSide.EMPTY) {
                        findAloneRail(fromChess, direction, checkingPos, canTurnDirection, crossScreenDataPackage, result, dirtyRailPosList);
                    }  else {
                        if (!dirtyRailPosList.contains(checkingPos)) {
                            result.add(currentPos);
                            dirtyRailPosList.add(currentPos);
                        }
                    }
                }
            }

        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PosRelationData {
        SimplePos currentPos;
        Map<Direction, SimplePos> neighbourMap;
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

        public String toText() {
            char rowChar = (char)('A' + this.getRow());
            return "(" + rowChar + this.getCol() + ")";
        }

    }


}
