package hundun.militarychess.ui.screen.board;

import hundun.militarychess.ui.screen.PlayScreen;
import hundun.militarychess.ui.screen.shared.BasePageableTable;
import lombok.Getter;

public class MainBoardVM extends BasePageableTable {

    PlayScreen builderScreen;

    @Getter
    FirstPageVM firstPageVM;
    @Getter
    SecondPageVM secondPageVM;
    public void updateForNewSide() {
        firstPageVM.updateForNewSide();
        secondPageVM.updateForNewSide();
    }


    private enum BuilderMainBoardState {
        PAGE1,
        PAGE2
    }

    public MainBoardVM(PlayScreen screen) {
        super(screen);
        init("TITLE", screen.getGame());

        this.builderScreen = screen;

        this.firstPageVM = new FirstPageVM(screen);
        this.secondPageVM = new SecondPageVM(screen);

        addPage(BuilderMainBoardState.PAGE1.name(),
                "PAGE1",
            firstPageVM
        );
        addPage(BuilderMainBoardState.PAGE2.name(),
            "PAGE2",
            secondPageVM
        );

        this.debugAll();
    }

    public void updateForShow() {

        updateByState(BuilderMainBoardState.PAGE1.name());
    }

}
