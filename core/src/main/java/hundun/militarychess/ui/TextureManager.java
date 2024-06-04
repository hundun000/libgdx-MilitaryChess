package hundun.militarychess.ui;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import hundun.militarychess.logic.map.TileModel;
import hundun.militarychess.logic.chess.LogicFlag;
import hundun.militarychess.logic.map.tile.TileNeighborDirection;
import lombok.Getter;


public class TextureManager {
    MilitaryChessGame game;

    @Getter
    Drawable mcStyleTable;
    @Getter
    Drawable mcStyleTableTop;
    @Getter
    Drawable mcStyleTableBottom;
    @Getter
    Drawable deskBackground;
    protected Map<String, TextureRegion> tileNormalSubRegionMap = new HashMap<>();
    protected Map<String, TextureRegion> tileRailSubRegionMap = new HashMap<>();
    public TextureManager(MilitaryChessGame game) {
        this.game = game;
    }


    private static void fillSubRegionMap(Map<String, TextureRegion> map, TextureRegion[][] regions) {
        map.put("111,111,111", regions[0][0]);

        map.put("010,011,011", regions[0][1]);
        map.put("010,111,111", regions[0][2]);
        map.put("010,110,110", regions[0][3]);
        map.put("011,011,010", regions[1][1]);
        map.put("111,111,010", regions[1][2]);
        map.put("110,110,010", regions[1][3]);

        map.put("011,011,011", regions[0][4]);
        map.put("110,110,110", regions[0][5]);
        map.put("000,111,111", regions[1][4]);
        map.put("111,111,000", regions[1][5]);

        map.put("010,000,010", regions[0][6]);
        map.put("000,111,000", regions[1][6]);
        map.put("010,010,010", regions[1][7]);

        map.put("000,011,011", regions[0][8]);
        map.put("000,110,110", regions[0][9]);
        map.put("011,011,000", regions[1][8]);
        map.put("110,110,000", regions[1][9]);

        map.put("010,111,010", regions[2][0]);

        map.put("000,011,010", regions[2][1]);
        map.put("000,110,010", regions[2][2]);
        map.put("010,011,000", regions[2][3]);
        map.put("010,110,000", regions[2][4]);

        map.put("000,111,010", regions[2][5]);
        map.put("010,111,000", regions[2][6]);
        map.put("010,110,010", regions[2][7]);
        map.put("010,011,010", regions[2][8]);
    }

    public void lazyInitOnCreateStage1() {


        NinePatch tempNinePatch;

        tempNinePatch = new NinePatch(
                ignoreFirstLineTexture("McStyleTable.9.png"),
                20, 20, 20, 20
                );
        mcStyleTable = new NinePatchDrawable(tempNinePatch);
        tempNinePatch = new NinePatch(
                ignoreFirstLineTexture("McStyleTable-top.9.png"),
                20, 20, 20, 0
                );
        mcStyleTableTop = new NinePatchDrawable(tempNinePatch);
        tempNinePatch = new NinePatch(
                ignoreFirstLineTexture("McStyleTable-bottom.9.png"),
                20, 20, 0, 20
                );
        mcStyleTableBottom = new NinePatchDrawable(tempNinePatch);

        tempNinePatch = new NinePatch(
                new Texture(Gdx.files.internal("deskBackground.9.png")),
                20, 20, 20, 20
        );
        deskBackground = new NinePatchDrawable(tempNinePatch);

        {
            Texture texture = new Texture(Gdx.files.internal("normal-tileset.png"));
            TextureRegion[][] regions = TextureRegion.split(texture, 16, 16);
            fillSubRegionMap(tileNormalSubRegionMap, regions);
        }
        {
            Texture texture = new Texture(Gdx.files.internal("rail-tileset.png"));
            TextureRegion[][] regions = TextureRegion.split(texture, 16, 16);
            fillSubRegionMap(tileRailSubRegionMap, regions);
        }
    }

    private TextureRegion ignoreFirstLineTexture(String file) {
        Texture texture = new Texture(Gdx.files.internal(file));
        return new TextureRegion(texture,
                1, 1, texture.getWidth() -1, texture.getHeight() -1
                );
    }

    public TextureRegion getBuildingImage(TileModel tileModel) {
        if (tileModel.getLogicFlags().contains(LogicFlag.RAIL)) {
            StringBuilder code = new StringBuilder();
            code.append(tileModel.getRailNeighbors().containsKey(TileNeighborDirection.LEFT_UP) ? "1" : "0");
            code.append(tileModel.getRailNeighbors().containsKey(TileNeighborDirection.VERTICAL_UP) ? "1" : "0");
            code.append(tileModel.getRailNeighbors().containsKey(TileNeighborDirection.RIGHT_UP) ? "1" : "0");
            code.append(",");
            code.append(tileModel.getRailNeighbors().containsKey(TileNeighborDirection.LEFT_MID) ? "1" : "0");
            code.append("1");
            code.append(tileModel.getRailNeighbors().containsKey(TileNeighborDirection.RIGHT_MID) ? "1" : "0");
            code.append(",");
            code.append(tileModel.getRailNeighbors().containsKey(TileNeighborDirection.LEFT_DOWN) ? "1" : "0");
            code.append(tileModel.getRailNeighbors().containsKey(TileNeighborDirection.VERTICAL_DOWN) ? "1" : "0");
            code.append(tileModel.getRailNeighbors().containsKey(TileNeighborDirection.RIGHT_DOWN) ? "1" : "0");
            TextureRegion result = tileRailSubRegionMap.get(code.toString());
            if (result == null) {
                game.getFrontend().log(this.getClass().getSimpleName(), "RAIL getBuildingImage not found for code : " + code);
            }
            return result;
        }
        return null;
    }

    public TextureRegion getTileImage(TileModel tileModel) {
        if (!tileModel.getLogicFlags().contains(LogicFlag.NO_PASS)) {
            StringBuilder code = new StringBuilder();
            code.append(tileModel.getLogicalNeighbors().containsKey(TileNeighborDirection.LEFT_UP) ? "1" : "0");
            code.append(tileModel.getLogicalNeighbors().containsKey(TileNeighborDirection.VERTICAL_UP) ? "1" : "0");
            code.append(tileModel.getLogicalNeighbors().containsKey(TileNeighborDirection.RIGHT_UP) ? "1" : "0");
            code.append(",");
            code.append(tileModel.getLogicalNeighbors().containsKey(TileNeighborDirection.LEFT_MID) ? "1" : "0");
            code.append("1");
            code.append(tileModel.getLogicalNeighbors().containsKey(TileNeighborDirection.RIGHT_MID) ? "1" : "0");
            code.append(",");
            code.append(tileModel.getLogicalNeighbors().containsKey(TileNeighborDirection.LEFT_DOWN) ? "1" : "0");
            code.append(tileModel.getLogicalNeighbors().containsKey(TileNeighborDirection.VERTICAL_DOWN) ? "1" : "0");
            code.append(tileModel.getLogicalNeighbors().containsKey(TileNeighborDirection.RIGHT_DOWN) ? "1" : "0");
            TextureRegion result = tileNormalSubRegionMap.get(code.toString());
            if (result == null) {
                game.getFrontend().log(this.getClass().getSimpleName(), "getTileImage not found for code : " + code);
            }
            return result;
        }
        return null;
    }
}
