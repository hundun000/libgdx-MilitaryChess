package hundun.militarychess.logic.chess;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GridPosition {
    int x;
    int y;

    public String toId() {
        return "(" + x + "," + y + ")";
    }

    public String toText() {
        return "(" + x + "," + y + ")";
    }

}
