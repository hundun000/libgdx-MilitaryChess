package hundun.militarychess.logic.data.generic;

import hundun.militarychess.logic.chess.PosRule.SimplePos;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChessPosData {
    SimplePos pos;
    ChessPosType chessPosType;

    public enum ChessPosType {
        FRONT_AREA,
        XING_YING,
        BACK_AREA,
        DA_BEN_YING,
    }

    public String toText() {
        char rowChar = (char)('A' + pos.getRow());
        return "(" + rowChar + pos.getCol() + ")";
    }
}
