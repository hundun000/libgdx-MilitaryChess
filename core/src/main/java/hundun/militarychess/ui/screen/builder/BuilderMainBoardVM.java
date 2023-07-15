package hundun.militarychess.ui.screen.builder;

import hundun.militarychess.ui.MilitaryChessGame;
import hundun.militarychess.ui.screen.PlayScreen;
import hundun.militarychess.ui.screen.shared.BasePageableTable;

public class BuilderMainBoardVM extends BasePageableTable {

    PlayScreen builderScreen;

    AllButtonPageVM allButtonPageVM;


    private enum BuilderMainBoardState {
        PAGE1,
        PAGE2
    }

    public BuilderMainBoardVM(PlayScreen screen) {
        super(screen);
        init("builder", screen.getGame());

        this.builderScreen = screen;

        this.allButtonPageVM = new AllButtonPageVM(screen);

        addPage(BuilderMainBoardState.PAGE1.name(),
                "读写数据",
                allButtonPageVM
        );

    }

    public void updateForShow() {

        updateByState(BuilderMainBoardState.PAGE1.name());
    }

}
