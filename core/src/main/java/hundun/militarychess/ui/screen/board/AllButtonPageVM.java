package hundun.militarychess.ui.screen.board;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import hundun.militarychess.logic.LogicContext;
import hundun.militarychess.logic.LogicContext.ChessShowMode;
import hundun.militarychess.logic.manager.CrossScreenDataManager;
import hundun.militarychess.logic.LogicContext.PlayerMode;
import hundun.militarychess.logic.chess.ChessRule.BattleResultType;
import hundun.militarychess.logic.data.ChessRuntimeData.ChessSide;
import hundun.militarychess.ui.screen.PlayScreen;
import hundun.militarychess.ui.screen.shared.GridVM;
import lombok.Getter;

public class AllButtonPageVM extends Table {

    PlayScreen screen;
    @Getter
    GridVM fromGridVM;
    @Getter
    GridVM toGridVM;
    BattleResultType fightResultPreview;
    ChessSide currentSide;

    Label timeLabel;
    Label currentSideLabel;
    Label fromLabel;
    Label toLabel;
    Label fightResultPreviewLabel;
    TextButton commitButton;
    TextButton clearButton;
    TextButton capitulateButton;
    public AllButtonPageVM(PlayScreen screen) {
        this.screen = screen;

        int pad = 20;

        this.timeLabel = new Label("", screen.getGame().getMainSkin());
        this.add(timeLabel).padBottom(pad).row();

        this.currentSideLabel = new Label("", screen.getGame().getMainSkin());
        this.add(currentSideLabel).padBottom(pad).row();

        this.fromLabel = new Label("", screen.getGame().getMainSkin());
        this.add(fromLabel).padBottom(pad).row();

        this.toLabel = new Label("", screen.getGame().getMainSkin());
        this.add(toLabel).padBottom(pad).row();

        this.fightResultPreviewLabel = new Label("", screen.getGame().getMainSkin());
        this.add(fightResultPreviewLabel).padBottom(pad).row();

        this.commitButton = new TextButton("确认", screen.getGame().getMainSkin());
        this.commitButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screen.onBattleStartButtonClicked();
            }
        });
        this.add(commitButton).padBottom(pad).row();

        this.clearButton = new TextButton("清空", screen.getGame().getMainSkin());
        this.clearButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screen.onClearButtonClicked();
            }
        });
        this.add(clearButton).padBottom(pad).row();
        this.capitulateButton = new TextButton("认输", screen.getGame().getMainSkin());
        this.capitulateButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screen.onCapitulated();
            }
        });
        this.add(capitulateButton).padTop(pad * 3).row();
    }

    public void setFrom(GridVM gridVM) {
        this.fromGridVM = gridVM;
        updateByChess();
    }

    public void setTo(GridVM gridVM, BattleResultType fightResultPreview) {
        this.toGridVM = gridVM;
        this.fightResultPreview = fightResultPreview;
        this.commitButton.setDisabled(fightResultPreview == null || fightResultPreview == BattleResultType.CAN_NOT);
        updateByChess();
    }


    /**
     * From/To棋子设置/清空后，更新对应UI
     */
    private void updateByChess() {
        CrossScreenDataManager crossScreenDataManager = screen.getGame().getLogicContext().getCrossScreenDataManager();
        if (fromGridVM != null) {
            if (crossScreenDataManager.getCurrentChessShowSides().contains(fromGridVM.getDeskData().getChessSide())) {
                this.fromLabel.setText("发起者: "
                    + fromGridVM.getDeskData().toText()
                );
            } else {
                this.fromLabel.setText("发起者: 已隐藏");
            }
        } else {
            this.fromLabel.setText("发起者: 待选择");
        }
        if (toGridVM != null) {
            if (crossScreenDataManager.getCurrentChessShowSides().contains(toGridVM.getDeskData().getChessSide())) {
                this.toLabel.setText("目标: "
                    + toGridVM.getDeskData().toText()
                );
            } else {
                this.toLabel.setText("目标: 已隐藏");
            }
        } else {
            this.toLabel.setText("目标: 待选择");
        }
        if (fightResultPreview != null) {
            if (crossScreenDataManager.getChessShowMode() == ChessShowMode.MING_QI
                || fightResultPreview == BattleResultType.JUST_MOVE
                || fightResultPreview == BattleResultType.CAN_NOT
            ) {
                this.fightResultPreviewLabel.setText("预测: "
                    + fightResultPreview.getChinese()
                );
            } else {
                this.fightResultPreviewLabel.setText("预测: 已隐藏");
            }
        } else {
            this.fightResultPreviewLabel.setText("");
        }
        currentSideLabel.setText("当前操作方: " + currentSide.getChinese());
    }

    /**
     * 当前操作方变动时调用
     */
    public void updateForNewSide() {
        CrossScreenDataManager crossScreenDataManager = screen.getGame().getLogicContext().getCrossScreenDataManager();
        ChessSide currentSide = crossScreenDataManager.getCurrentSide();
        boolean isAiSide = crossScreenDataManager.getPlayerMode() == PlayerMode.PVC && currentSide != crossScreenDataManager.getPvcPlayerSide();
        this.fromGridVM = null;
        this.toGridVM = null;
        this.fightResultPreview = null;
        this.commitButton.setDisabled(true);
        // 不能帮ai按按钮
        this.clearButton.setDisabled(isAiSide);
        this.capitulateButton.setDisabled(isAiSide);

        this.currentSide = currentSide;
        updateByChess();

    }

    /**
     * 时间变化后，更新对应UI
     */
    public void updateTime(LogicContext logicContext) {
        StringBuilder stringBuilder = new StringBuilder();
        logicContext.getChessTileManager().getArmyMap().forEach((k, v) -> {
            int minute = v.getUsedTime() / 60;
            int second = v.getUsedTime() % 60;
            stringBuilder.append(k.getChinese()).append("累计用时：")
                .append(minute).append("分")
                .append(second).append("秒")
                .append("\n");
        });
        timeLabel.setText(stringBuilder.toString());
    }
}
