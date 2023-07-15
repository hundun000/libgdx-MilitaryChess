package hundun.militarychess.ui.screen.shared;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.HashSet;
import java.util.Set;

import hundun.gdxgame.corelib.base.util.DrawableFactory;
import hundun.militarychess.logic.data.ChessRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData.ChessSide;
import hundun.militarychess.logic.data.generic.GenericPosData;
import hundun.militarychess.ui.MilitaryChessGame;

import lombok.Getter;


public class ChessVM extends Table {

    MilitaryChessGame game;

    DeskAreaVM deskAreaVM;
    @Getter
    ChessRuntimeData deskData;

    Label mainLabel;
    Image image;

    public ChessVM(DeskAreaVM deskAreaVM, ChessRuntimeData deskData) {
        this.game = deskAreaVM.screen.getGame();
        this.deskAreaVM = deskAreaVM;
        this.deskData = deskData;


        this.image = new Image();
        image.setBounds(0, 0, this.game.getScreenContext().getLayoutConst().DESK_WIDTH, this.game.getScreenContext().getLayoutConst().DESK_HEIGHT);
        this.addActor(image);
        /*this.setBackground(new TextureRegionDrawable(new TextureRegion(TextureFactory.getSimpleBoardBackground(
                this.game.getScreenContext().getLayoutConst().DESK_WIDTH,
                this.game.getScreenContext().getLayoutConst().DESK_HEIGHT
        ))));*/

        this.mainLabel = new Label("", game.getMainSkin());
        this.add(mainLabel);

        updateUI();
    }



    public void updateUI(){
        this.mainLabel.setText(deskData.getChessType().getChinese());
        if (deskData.getChessSide() == ChessSide.MY_SIDE) {
            image.setDrawable(DrawableFactory.createAlphaBoard(1, 1, Color.RED, 0.8f));
        } else if (deskData.getChessSide() == ChessSide.OTHER_SIDE) {
            image.setDrawable(DrawableFactory.createAlphaBoard(1, 1, Color.BLUE, 0.8f));
        } else {
            image.setDrawable(DrawableFactory.createAlphaBoard(1, 1, Color.WHITE, 0.5f));
        }
        GenericPosData roomPos = deskData.getMainLocation();
        this.setBounds(
            roomPos.getX(),
            roomPos.getY(),
            game.getScreenContext().getLayoutConst().DESK_WIDTH,
            game.getScreenContext().getLayoutConst().DESK_HEIGHT
        );
    }


}
