package hundun.militarychess.logic.data;

import hundun.militarychess.logic.data.generic.GenericPosData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChessRuntimeData {
    String uiName;
    GenericPosData mainLocation;
}
