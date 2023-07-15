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
    String room;
    String area;
    int areaIndex;
    int x;
    int y;

    public static class Extension {
        public static String SPLIT = ";";

        public static String toLine(String room, String area, int areaIndex) {
            return room + SPLIT + area + SPLIT + areaIndex;
        }

        public static String toLine(GenericPosData thiz) {
            return thiz.getRoom() + SPLIT + thiz.getArea() + SPLIT + thiz.getAreaIndex();
        }
    }

    public static class Factory {



    }
}
