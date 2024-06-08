package hundun.militarychess.ui.screen.shared;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import hundun.gdxgame.corelib.base.util.DrawableFactory;
import hundun.militarychess.logic.chess.GridPosition;
import hundun.militarychess.logic.manager.CrossScreenDataManager;
import hundun.militarychess.logic.map.TileModel;
import hundun.militarychess.logic.data.ChessRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData.ChessSide;
import hundun.militarychess.ui.MilitaryChessGame;

import lombok.Getter;


public class GridVM extends Table {

    MilitaryChessGame game;

    DeskAreaVM deskAreaVM;
    @Getter
    ChessRuntimeData deskData;
    final int uiX;
    final int uiY;
    Label chessTypeLabel;
    Label chessStatusLabel;
    Image tileImage;
    Image buildingImage;
    Image colorImage;
    @Getter
    final Actor hitBox;
    @Getter
    final GridPosition position;
    public GridVM(DeskAreaVM deskAreaVM, GridPosition position) {
        this.game = deskAreaVM.screen.getGame();
        this.deskAreaVM = deskAreaVM;
        this.position = position;
        this.uiX = position.getX() * game.getScreenContext().getLayoutConst().TILE_WIDTH;
        this.uiY =  position.getY() * game.getScreenContext().getLayoutConst().TILE_HEIGHT;

        this.tileImage = new Image();
        tileImage.setBounds(
            0,
            0,
            this.game.getScreenContext().getLayoutConst().TILE_WIDTH,
            this.game.getScreenContext().getLayoutConst().TILE_HEIGHT
        );
        this.addActor(tileImage);

        this.buildingImage = new Image();
        buildingImage.setBounds(
            0,
            0,
            this.game.getScreenContext().getLayoutConst().TILE_WIDTH,
            this.game.getScreenContext().getLayoutConst().TILE_HEIGHT
        );
        this.addActor(buildingImage);

        this.colorImage = new Image();
        colorImage.setBounds(
            this.game.getScreenContext().getLayoutConst().CHESS_AND_DESK_SPACE,
            this.game.getScreenContext().getLayoutConst().CHESS_AND_DESK_SPACE,
            this.game.getScreenContext().getLayoutConst().TILE_WIDTH - this.game.getScreenContext().getLayoutConst().CHESS_AND_DESK_SPACE * 2,
            this.game.getScreenContext().getLayoutConst().TILE_HEIGHT - this.game.getScreenContext().getLayoutConst().CHESS_AND_DESK_SPACE * 2
        );
        this.addActor(colorImage);

        /*this.setBackground(new TextureRegionDrawable(new TextureRegion(TextureFactory.getSimpleBoardBackground(
                this.game.getScreenContext().getLayoutConst().DESK_WIDTH,
                this.game.getScreenContext().getLayoutConst().DESK_HEIGHT
        ))));*/

        this.chessTypeLabel = new Label("", game.getMainSkin());
        this.add(chessTypeLabel).row();
        this.chessStatusLabel = new Label("", game.getMainSkin());
        this.add(chessStatusLabel);

        this.hitBox = new Image();
        hitBox.addListener(new DeskClickListener(deskAreaVM.getScreen(), this));

        updateUIForChessChanged();
    }



    public void updateUIForChessChanged(){
        CrossScreenDataManager crossScreenDataManager = game.getLogicContext().getCrossScreenDataManager();
        TileModel tileModel = game.getLogicContext().getChessTileManager().getWorldConstructionAt(position);
        this.deskData = game.getLogicContext().getChessTileManager().findAtPos(position);

        if (deskData.getChessSide() == ChessSide.EMPTY) {
            this.chessTypeLabel.setText("");
            chessStatusLabel.setText("");
            colorImage.setDrawable(DrawableFactory.createAlphaBoard(1, 1, Color.WHITE, 0.5f));
        } else {
            if (crossScreenDataManager.getCurrentChessShowSides().contains(deskData.getChessSide())) {
                chessTypeLabel.setText(deskData.getChessType().getChinese() + deskData.getPos().toText());
                chessStatusLabel.setText(deskData.getChessBattleStatus().getChinese());
                if (deskData.getChessSide() == ChessSide.RED_SIDE) {
                    colorImage.setDrawable(DrawableFactory.createAlphaBoard(1, 1, Color.RED, 0.8f));
                } else if (deskData.getChessSide() == ChessSide.BLUE_SIDE) {
                    colorImage.setDrawable(DrawableFactory.createAlphaBoard(1, 1, Color.BLUE, 0.8f));
                }
            } else {
                chessTypeLabel.setText("");
                chessStatusLabel.setText("");
            }
        }

        this.setBounds(
            uiX,
            uiY,
            game.getScreenContext().getLayoutConst().TILE_WIDTH,
            game.getScreenContext().getLayoutConst().TILE_HEIGHT
        );

        hitBox.setBounds(
            this.getX() + game.getScreenContext().getLayoutConst().HIT_BOX_X,
            this.getY() + game.getScreenContext().getLayoutConst().HIT_BOX_Y,
            game.getScreenContext().getLayoutConst().HIT_BOX_WIDTH,
            game.getScreenContext().getLayoutConst().HIT_BOX_HEIGHT
        );

        // ------ ui for tile ------
        TextureRegion tileTextureRegion = game.getTextureManager().getTileImage(tileModel);
        if (tileTextureRegion != null) {
            tileImage.setDrawable(new TextureRegionDrawable(tileTextureRegion));
        }

        TextureRegion buildingTextureRegion = game.getTextureManager().getBuildingImage(tileModel);
        if (buildingTextureRegion != null) {
            buildingImage.setDrawable(new TextureRegionDrawable(buildingTextureRegion));
        }
    }


    public enum MaskType {
        EMPTY,
        MOVE_CANDIDATE,
        FROM
    }

    public void updateMask(MaskType maskType) {
        CrossScreenDataManager crossScreenDataManager = game.getLogicContext().getCrossScreenDataManager();
        if (maskType == MaskType.MOVE_CANDIDATE) {
            this.setBackground(DrawableFactory.createAlphaBoard(1, 1, Color.YELLOW, 0.5f));
        } else if (maskType == MaskType.FROM) {
            this.setBackground(DrawableFactory.createAlphaBoard(1, 1, Color.ORANGE, 0.5f));
        } else {
            this.setBackground((Drawable) null);
        }

    }
}
