package hundun.militarychess.ui.screen.builder;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.fasterxml.jackson.core.JsonProcessingException;

import hundun.gdxgame.gamelib.base.util.JavaFeatureForGwt;
import hundun.militarychess.ui.screen.PlayScreen;

public class AllButtonPageVM extends Table {

    PlayScreen screen;

    public AllButtonPageVM(PlayScreen screen) {
        this.screen = screen;


    }
}
