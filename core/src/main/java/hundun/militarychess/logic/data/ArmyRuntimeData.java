package hundun.militarychess.logic.data;

import com.badlogic.gdx.files.FileHandle;

import java.util.List;

import hundun.militarychess.logic.data.ChessRuntimeData.ChessSide;
import hundun.militarychess.ui.screen.LayoutConst;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 军棋里一方的所有数据
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArmyRuntimeData {
    /**
     * 已用秒数
     */
    int usedTime;

    /**
     * 所有棋子
     */
    List<ChessRuntimeData> chessRuntimeDataList;



}
