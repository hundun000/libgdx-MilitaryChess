package hundun.militarychess.logic.data;

import com.badlogic.gdx.files.FileHandle;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hundun
 * Created on 2023/05/09
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomRuntimeData {
    String name;
    FileHandle roomImage;
    List<ChessRuntimeData> chessRuntimeDataList;

    public static class Factory {
        public static RoomRuntimeData fromSaveData(
                ) {
            return null;
        }

    }

}
