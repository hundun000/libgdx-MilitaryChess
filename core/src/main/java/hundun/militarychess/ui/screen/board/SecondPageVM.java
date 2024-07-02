package hundun.militarychess.ui.screen.board;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import hundun.militarychess.logic.LogicContext;
import hundun.militarychess.logic.LogicContext.ChessShowMode;
import hundun.militarychess.logic.LogicContext.PlayerMode;
import hundun.militarychess.logic.chess.ChessRule.BattleResultType;
import hundun.militarychess.logic.data.ChessRuntimeData.ChessSide;
import hundun.militarychess.logic.manager.CrossScreenDataManager;
import hundun.militarychess.ui.screen.PlayScreen;
import hundun.militarychess.ui.screen.shared.GridVM;
import lombok.Getter;

public class SecondPageVM extends Table {

    PlayScreen screen;


    TextButton capitulateButton;
    public SecondPageVM(PlayScreen screen) {
        this.screen = screen;

        int pad = 5;

        this.capitulateButton = new TextButton("认输", screen.getGame().getMainSkin());
        this.capitulateButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screen.onCapitulated();
            }
        });
        this.add(capitulateButton).padTop(pad * 2).row();
    }

    /**
     * 当前操作方变动时调用
     */
    public void updateForNewSide() {
        CrossScreenDataManager crossScreenDataManager = screen.getGame().getLogicContext().getCrossScreenDataManager();
        ChessSide currentSide = crossScreenDataManager.getCurrentSide();
        boolean isAiSide = crossScreenDataManager.getPlayerMode() == PlayerMode.PVC && currentSide != crossScreenDataManager.getPvcPlayerSide();

        this.capitulateButton.setDisabled(isAiSide);
    }
}
