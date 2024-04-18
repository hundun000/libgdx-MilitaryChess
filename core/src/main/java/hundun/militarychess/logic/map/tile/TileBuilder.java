package hundun.militarychess.logic.map.tile;

import hundun.militarychess.logic.chess.LogicFlag;
import hundun.militarychess.logic.chess.GridPosition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TileBuilder {
    GridPosition position;
    List<LogicFlag> logicFlags;
    boolean hasDiagonalNeighbor;





    public static TileBuilder create(int x, int y, boolean hasDiagonalNeighbor) {
        return create(x, y, hasDiagonalNeighbor, LogicFlag.NORMAL);
    }

    public static TileBuilder create(int x, int y, boolean hasDiagonalNeighbor, LogicFlag... logicFlags) {
        return TileBuilder.builder()
            .logicFlags(List.of(logicFlags))
            .hasDiagonalNeighbor(hasDiagonalNeighbor)
            .position(GridPosition.builder()
                .x(x)
                .y(y)
                .build())
            .build();
    }

}
