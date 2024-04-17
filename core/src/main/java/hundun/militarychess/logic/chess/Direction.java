package hundun.militarychess.logic.chess;

import java.util.List;

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
