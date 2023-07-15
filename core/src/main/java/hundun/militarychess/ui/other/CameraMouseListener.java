package hundun.militarychess.ui.other;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;

public class CameraMouseListener extends InputListener {

    private final CameraDataPackage cameraDataPackage;

    public CameraMouseListener(CameraDataPackage cameraDataPackage) {
        this.cameraDataPackage = cameraDataPackage;
    }

    @Override
    public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
        float deltaValue = (amountX + amountY) * 0.1f;
        cameraDataPackage.modifyCurrentCameraZoomWeight(deltaValue);
        return super.scrolled(event, x, y, amountX, amountY);
    }

}
