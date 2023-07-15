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
 * @author hundun
 * Created on 2023/05/09
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArmyRuntimeData {

    List<ChessRuntimeData> chessRuntimeDataList;



}
