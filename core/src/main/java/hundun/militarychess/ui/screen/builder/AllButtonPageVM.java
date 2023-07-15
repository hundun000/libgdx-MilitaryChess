package hundun.militarychess.ui.screen.builder;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import hundun.militarychess.logic.chess.ChessRule.FightResultType;
import hundun.militarychess.logic.data.ChessRuntimeData.ChessSide;
import hundun.militarychess.ui.screen.PlayScreen;
import hundun.militarychess.ui.screen.shared.ChessVM;
import lombok.Getter;

public class AllButtonPageVM extends Table {

    PlayScreen screen;
    @Getter
    ChessVM fromChessVM;
    @Getter
    ChessVM toChessVM;
    FightResultType fightResultPreview;
    ChessSide currentSide;

    Label currentSideLabel;
    Label fromLabel;
    Label toLabel;
    Label fightResultPreviewLabel;
    TextButton commitButton;
    TextButton clearButton;

    public AllButtonPageVM(PlayScreen screen) {
        this.screen = screen;

        int pad = 20;

        this.currentSideLabel = new Label("", screen.getGame().getMainSkin());
        this.add(currentSideLabel).padBottom(pad).row();

        this.fromLabel = new Label("", screen.getGame().getMainSkin());
        this.add(fromLabel).padBottom(pad).row();

        this.toLabel = new Label("", screen.getGame().getMainSkin());
        this.add(toLabel).padBottom(pad).row();

        this.fightResultPreviewLabel = new Label("", screen.getGame().getMainSkin());
        this.add(fightResultPreviewLabel).padBottom(pad).row();

        this.commitButton = new TextButton("确认", screen.getGame().getMainSkin());
        this.commitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                screen.onCommitButtonClicked();
            }
        });
        this.add(commitButton).padBottom(pad).row();

        this.clearButton = new TextButton("清空", screen.getGame().getMainSkin());
        this.clearButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                screen.onClearButtonClicked();
            }
        });
        this.add(clearButton).padBottom(pad).row();
    }

    public void setFrom(ChessVM chessVM) {
        this.fromChessVM = chessVM;
        updateUI();
    }

    public void setTo(ChessVM chessVM, FightResultType fightResultPreview) {
        this.toChessVM = chessVM;
        this.fightResultPreview = fightResultPreview;
        this.commitButton.setDisabled(fightResultPreview == null || fightResultPreview == FightResultType.CAN_NOT);
        updateUI();
    }


    public void updateUI() {
        if (fromChessVM != null) {
            this.fromLabel.setText("发起者: "
                + fromChessVM.getDeskData().getChessType().getChinese()
                + fromChessVM.getDeskData().getMainLocation().toText()
            );
        } else {
            this.fromLabel.setText("发起者: 待选择");
        }
        if (toChessVM != null) {
            this.toLabel.setText("目标: "
                + toChessVM.getDeskData().getChessType().getChinese()
                + toChessVM.getDeskData().getMainLocation().toText()
            );
        } else {
            this.toLabel.setText("目标: 待选择");
        }
        if (fightResultPreview != null) {
            this.fightResultPreviewLabel.setText("预测: "
                + fightResultPreview.getChinese()
            );
        } else {
            this.fightResultPreviewLabel.setText("");
        }
        currentSideLabel.setText("当前操作方: " + currentSide.getChinese());
    }

    public void updateForNewSide(ChessSide currentSide) {
        this.fromChessVM = null;
        this.toChessVM = null;
        this.fightResultPreview = null;
        this.commitButton.setDisabled(true);
        this.currentSide = currentSide;
        updateUI();

    }
}
