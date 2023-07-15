package hundun.militarychess.ui.screen.shared;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.HashSet;
import java.util.Set;

import hundun.militarychess.logic.data.ChessRuntimeData;
import hundun.militarychess.logic.data.generic.GenericPosData;
import hundun.militarychess.ui.MilitaryChessGame;

import lombok.Getter;

/**
 * @author hundun
 * Created on 2023/05/09
 */
public class ChessVM extends Table {

    MilitaryChessGame game;

    DeskAreaVM deskAreaVM;
    @Getter
    ChessRuntimeData deskData;

    Table tagImageTable;

    private ChessVM(DeskAreaVM deskAreaVM, ChessRuntimeData deskData) {
        this.game = deskAreaVM.screen.getGame();
        this.deskAreaVM = deskAreaVM;
        this.deskData = deskData;


        Image image = new Image(game.getTextureManager().getDeskBackground());
        image.setBounds(0, 0, this.game.getScreenContext().getLayoutConst().DESK_WIDTH, this.game.getScreenContext().getLayoutConst().DESK_HEIGHT);
        this.addActor(image);
        /*this.setBackground(new TextureRegionDrawable(new TextureRegion(TextureFactory.getSimpleBoardBackground(
                this.game.getScreenContext().getLayoutConst().DESK_WIDTH,
                this.game.getScreenContext().getLayoutConst().DESK_HEIGHT
        ))));*/
    }


    public static ChessVM typeMain(DeskAreaVM deskAreaVM, ChessRuntimeData deskData, GenericPosData location) {
        ChessVM thiz = new ChessVM(deskAreaVM, deskData);

        thiz.tagImageTable = new Table();
        thiz.add(new Label(
                deskData.getUiName(),
                thiz.game.getMainSkin()));
        thiz.add(thiz.tagImageTable)
                ;

        return thiz;
    }



}
