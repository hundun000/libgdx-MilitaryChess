package hundun.militarychess.ui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import de.eskalon.commons.screen.transition.impl.BlendingTransition;
import hundun.gdxgame.corelib.base.util.TextureFactory;
import hundun.gdxgame.gamelib.base.util.JavaFeatureForGwt;
import hundun.militarychess.logic.CrossScreenDataPackage;
import hundun.militarychess.logic.chess.ChessRule;
import hundun.militarychess.logic.chess.ChessRule.BattleDamageFrame;
import hundun.militarychess.logic.chess.ChessRule.BattleResult;
import hundun.militarychess.logic.data.ChessRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData.ChessSide;
import hundun.militarychess.ui.MilitaryChessGame;

import java.util.LinkedList;
import java.util.List;

public class BattleScreen extends AbstractMilitaryChessScreen {
    Image backImage;
    Label leftSideStatusLabel;
    Label damageLabel;
    Label rightSideStatusLabel;

    TextButton playNextFrameButton;
    TextButton commitFightResultButton;

    // ------ logic ------
    BattleResult battleResult;

    List<BattleDamageFrame> playFrameQueue;
    /**
     * ref leftSideStatusLabel or rightSideStatusLabel
     */
    Label fromSideStatusLabel;
    /**
     * ref leftSideStatusLabel or rightSideStatusLabel
     */
    Label toSideStatusLabel;
    boolean fromSideIsLeftSide;
    int autoPlayCount;
    final int AUTO_PLAY_START_DELAY = -2;
    final int AUTO_PLAY_DELTA_DELAY = 1;

    final int AUTO_PLAY_END_DELAY = 3;
    int endingPhase;

    public BattleScreen(MilitaryChessGame game) {
        super(game, game.getSharedViewport());


    }

    @Override
    protected void updateUIAfterRoomChanged() {
        CrossScreenDataPackage crossScreenDataPackage = game.getLogicContext().getCrossScreenDataPackage();

        this.battleResult = crossScreenDataPackage.getBattleResult();
        this.playFrameQueue = new LinkedList<>(battleResult.getFrames());

        if (battleResult.getFrom().getChessSide() == ChessSide.RED_SIDE) {
            fromSideStatusLabel = leftSideStatusLabel;
            toSideStatusLabel = rightSideStatusLabel;
            fromSideIsLeftSide = true;
        } else {
            fromSideStatusLabel = rightSideStatusLabel;
            toSideStatusLabel = leftSideStatusLabel;
            fromSideIsLeftSide = false;
        }

        autoPlayCount = AUTO_PLAY_START_DELAY;
        endingPhase = 0;

        playInit();

    }

    private String toStatusText(ChessRuntimeData data, int tempHp) {
        return JavaFeatureForGwt.stringFormat(
            "%s %s\n攻 %s 防 %s\n生命 %s / %s",
            data.getChessSide().getChinese(),
            data.getChessType().getChinese(),
            data.getChessBattleStatus().getAtk(),
            data.getChessBattleStatus().getDef(),
            tempHp,
            data.getChessBattleStatus().getMaxHp()
            );
    }

    private void playInit() {
        fromSideStatusLabel.setText(toStatusText(
            battleResult.getFrom(),
            battleResult.getFrom().getChessBattleStatus().getHp()
        ));
        toSideStatusLabel.setText(toStatusText(
            battleResult.getTo(),
            battleResult.getTo().getChessBattleStatus().getHp()
        ));
        damageLabel.setText("");
    }

    private boolean playOneFrame() {
        if (!playFrameQueue.isEmpty()) {
            BattleDamageFrame frame = playFrameQueue.remove(0);
            fromSideStatusLabel.setText(toStatusText(
                battleResult.getFrom(),
                frame.getTempDataMapSnapshot().get(battleResult.getFrom().getId()).getHp()
            ));
            toSideStatusLabel.setText(toStatusText(
                battleResult.getTo(),
                frame.getTempDataMapSnapshot().get(battleResult.getTo().getId()).getHp()
            ));
            if (battleResult.isSpecialBattle()) {
                damageLabel.setText("特殊结算");
            } else {
                damageLabel.setText(toDamageText(battleResult.getFrom(), battleResult.getTo(), frame));
            }
        }
        if (playFrameQueue.isEmpty()) {
            playNextFrameButton.setDisabled(true);
            return true;
        } else {
            playNextFrameButton.setDisabled(false);
            return false;
        }
    }

    private String toDamageText(ChessRuntimeData from, ChessRuntimeData to, BattleDamageFrame frame) {
        String line;
        boolean originIsLeftSide = (fromSideIsLeftSide && frame.getOrigin().getId().equals(from.getId()))
            || (!fromSideIsLeftSide && frame.getOrigin().getId().equals(to.getId()));
        if (originIsLeftSide) {
            line = JavaFeatureForGwt.stringFormat("-- 伤害 %s -->", frame.getDamage());
        } else {
            line = JavaFeatureForGwt.stringFormat("<-- 伤害 %s --", frame.getDamage());
        }
        return line;
    }

    @Override
    protected void create() {
        this.backImage = new Image(new TextureRegionDrawable(new TextureRegion(TextureFactory.getSimpleBoardBackground())));
        backImage.setFillParent(true);
        backUiStage.addActor(backImage);

        leftSideStatusLabel = new Label("", game.getMainSkin());
        uiRootTable.add(leftSideStatusLabel);
        damageLabel = new Label("", game.getMainSkin());
        uiRootTable.add(damageLabel);
        rightSideStatusLabel = new Label("", game.getMainSkin());
        uiRootTable.add(rightSideStatusLabel);

        uiRootTable.row();

        this.playNextFrameButton = new TextButton("下一回合", this.getGame().getMainSkin());
        this.playNextFrameButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playOneFrame();
            }
        });
        uiRootTable.add(playNextFrameButton);
        this.commitFightResultButton = new TextButton("跳过过程", this.getGame().getMainSkin());
        this.commitFightResultButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                end();
            }
        });
        uiRootTable.add(commitFightResultButton);


    }

    @Override
    public void dispose() {

    }

    @Override
    public void onLogicFrame() {
        autoPlayCount++;
        if (endingPhase == 0) {
            if (autoPlayCount >= AUTO_PLAY_DELTA_DELAY) {
                autoPlayCount -= AUTO_PLAY_DELTA_DELAY;
                boolean intoEndingPhase1 = playOneFrame();
                if (intoEndingPhase1) {
                    endingPhase = 1;
                }
            }
        } if (endingPhase == 1) {
            if (autoPlayCount >= AUTO_PLAY_DELTA_DELAY) {
                autoPlayCount -= AUTO_PLAY_DELTA_DELAY;
                endingPhase = 2;
                playInit();
            }
        } else {
            if (autoPlayCount >= AUTO_PLAY_END_DELAY) {
                end();
            }
        }
    }

    private void end() {
        CrossScreenDataPackage crossScreenDataPackage = game.getLogicContext().getCrossScreenDataPackage();
        crossScreenDataPackage.commitFightResult(game.getLogicContext());
        game.getScreenManager().pushScreen(PlayScreen.class.getSimpleName(), BlendingTransition.class.getSimpleName());
    }

    @Override
    public void show() {
        super.show();

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(popupUiStage);
        multiplexer.addProcessor(uiStage);
        Gdx.input.setInputProcessor(multiplexer);

        //Gdx.input.setInputProcessor(uiStage);
        //game.getBatch().setProjectionMatrix(uiStage.getViewport().getCamera().combined);

        updateUIAfterRoomChanged();


        Gdx.app.log(this.getClass().getSimpleName(), "show done");
    }
}
