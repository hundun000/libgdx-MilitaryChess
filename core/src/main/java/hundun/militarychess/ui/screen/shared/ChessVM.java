package hundun.militarychess.ui.screen.shared;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import hundun.gdxgame.corelib.base.util.DrawableFactory;
import hundun.militarychess.logic.CrossScreenDataPackage;
import hundun.militarychess.logic.TileModel;
import hundun.militarychess.logic.data.ChessRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData.ChessSide;
import hundun.militarychess.ui.MilitaryChessGame;

import lombok.Getter;


public class ChessVM extends Table {

    MilitaryChessGame game;

    DeskAreaVM deskAreaVM;
    @Getter
    ChessRuntimeData deskData;

    Label chessTypeLabel;
    Label chessStatusLabel;
    Image tileImage;
    Image colorImage;

    public ChessVM(DeskAreaVM deskAreaVM, ChessRuntimeData deskData) {
        this.game = deskAreaVM.screen.getGame();
        this.deskAreaVM = deskAreaVM;
        this.deskData = deskData;

        this.tileImage = new Image();
        tileImage.setBounds(
            0,
            0,
            this.game.getScreenContext().getLayoutConst().TILE_WIDTH,
            this.game.getScreenContext().getLayoutConst().TILE_HEIGHT
        );
        this.addActor(tileImage);

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

        updateUIForChessChanged();
        // ------ ui for tile ------
        TileModel gameboardPos = game.getLogicContext().getTileMap().getWorldConstructionAt(deskData.getPos());
        TextureRegion textureRegion = game.getTextureManager().getTileImage(gameboardPos);
        if (textureRegion != null) {
            tileImage.setDrawable(new TextureRegionDrawable(textureRegion));
        } else {
            game.getFrontend().log(this.getClass().getSimpleName(), "TileImage not found " + gameboardPos.getPosition().toText());
        }
    }


    private void updateUIAsEmpty() {
        this.chessTypeLabel.setText("");
        chessStatusLabel.setText("");
        colorImage.setDrawable(DrawableFactory.createAlphaBoard(1, 1, Color.WHITE, 0.5f));
    }

    private void updateUIAsNotEmpty(CrossScreenDataPackage crossScreenDataPackage) {
        if (crossScreenDataPackage.getCurrentChessShowSides().contains(deskData.getChessSide())) {
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
            colorImage.setDrawable(DrawableFactory.createAlphaBoard(1, 1, Color.GRAY, 0.8f));
        }
    }

    public void updateUIForChessChanged(){
        CrossScreenDataPackage crossScreenDataPackage = game.getLogicContext().getCrossScreenDataPackage();

        if (deskData.getChessSide() == ChessSide.EMPTY) {
            updateUIAsEmpty();
        } else {
            updateUIAsNotEmpty(crossScreenDataPackage);
        }

        this.getDeskData().updateUiPos(game.getScreenContext().getLayoutConst());
        this.setBounds(
            deskData.getUiX(),
            deskData.getUiY(),
            game.getScreenContext().getLayoutConst().TILE_WIDTH,
            game.getScreenContext().getLayoutConst().TILE_HEIGHT
        );
    }


    public enum MaskType {
        EMPTY,
        MOVE_CANDIDATE,
        FROM
    }

    public void updateMask(MaskType maskType) {
        CrossScreenDataPackage crossScreenDataPackage = game.getLogicContext().getCrossScreenDataPackage();
        if (maskType == MaskType.MOVE_CANDIDATE) {
            this.setBackground(DrawableFactory.createAlphaBoard(1, 1, Color.YELLOW, 0.5f));
        } else if (maskType == MaskType.FROM) {
            this.setBackground(DrawableFactory.createAlphaBoard(1, 1, Color.ORANGE, 0.5f));
        } else {
            this.setBackground((Drawable) null);
        }

    }
}
