package hundun.militarychess.ui.screen.shared;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import hundun.militarychess.ui.MilitaryChessGame;
import hundun.militarychess.ui.screen.PlayScreen;


public class DeskClickListener extends ClickListener {
    MilitaryChessGame game;
    PlayScreen screen;
    private final ChessVM vm;

    public DeskClickListener(PlayScreen screen, ChessVM vm) {
        this.game = screen.getGame();
        this.screen = screen;
        this.vm = vm;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        screen.onDeskClicked(vm);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(vm.getDeskData().toText()).append(" has been clicked.");
        var relation = game.getLogicContext().getTileMap().getWorldConstructionAt(vm.getDeskData().getPos());
        stringBuilder.append("NeighbourMap{");
        relation.getPhysicalNeighbors().forEach((k, v) -> stringBuilder.append(k).append("=").append(v != null ? v.getPosition().toText() : "null"));
        stringBuilder.append("}");
        game.getFrontend().log(this.getClass().getSimpleName(), stringBuilder.toString());
    }
}
