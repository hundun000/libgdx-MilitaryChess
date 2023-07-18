package hundun.militarychess.ui.screen;

import hundun.militarychess.logic.data.ChessRuntimeData;

public class LayoutConst {


    public final int DESK_SMALL_COL_PADDING = 30;
    public final int DESK_BIG_COL_PADDING = 200;
    public final int DESK_WIDTH = 400;
    public final int DESK_HEIGHT = 200;

    public final int CHESS_AND_DESK_SPACE = 20;

    public final int DESK_STAR_SIZE = 50;
    public final int GOOD_IMAGE_SIZE = 150;
    public final int GOOD_NODE_HEIGHT = GOOD_IMAGE_SIZE + 50;
    public final int GOOD_NODE_WIDTH = 400;
    public final int GOOD_NODE_PAD = 10;

    public final int ANY_EXTRA_TOTAL_WIDTH = 400;

    public final int DESK_EXTRA_AREA_LEFT_PART_WIDTH = 300;
    public final int DESK_EXTRA_AREA_RIGHT_PART_WIDTH = ANY_EXTRA_TOTAL_WIDTH - DESK_EXTRA_AREA_LEFT_PART_WIDTH;
    public final int DESK_EXTRA_IMAGE_SIZE = 300;


    public final int ROOM_SWITCH_NODE_WIDTH = 250;
    public final int ROOM_SWITCH_NODE_HEIGHT = 75;

    public final int PLAY_WIDTH = 5 * DESK_WIDTH;
    public final int RIVER_HEIGHT = (int) (DESK_HEIGHT * 1);
    public final int PLAY_HEIGHT = 12 * DESK_HEIGHT + RIVER_HEIGHT;

    public static void updatePos(
        ChessRuntimeData thiz,
        LayoutConst layoutConst
    ) {
        int x = thiz.getMainLocation().getPos().getCol() * layoutConst.DESK_WIDTH;
        int y = (12 - thiz.getMainLocation().getPos().getRow()) * layoutConst.DESK_HEIGHT;
        if (thiz.getMainLocation().getPos().getRow() >= 6) {
            y -= layoutConst.RIVER_HEIGHT;
        }
        thiz.setUiX(x);
        thiz.setUiY(y);
    }
}
