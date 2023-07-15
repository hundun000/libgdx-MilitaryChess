package hundun.militarychess.logic.data.generic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenericPosData {
    int row;
    int col;
    int x;
    int y;

    public String toText() {
        char rowChar = (char)('A' + row);
        return "(" + rowChar + col + ")";
    }
}
