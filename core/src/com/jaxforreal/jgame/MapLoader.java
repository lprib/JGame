package com.jaxforreal.jgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.XmlReader;
import com.jaxforreal.jgame.entity.Entity;
import com.jaxforreal.jgame.entity.TestMob;
import com.jaxforreal.jgame.tile.Grass;
import com.jaxforreal.jgame.tile.Tile;
import com.jaxforreal.jgame.tile.Wood;

import java.io.IOException;
import java.util.HashMap;

/**
 * Static class that loads Map's from plaintext files
 * Also holds information about converting from strings (in plaintext file) to tiles
 */
public class MapLoader {
    GameManager gameManager;
    XmlReader xmlReader = new XmlReader();
    private ArrayMap<String, Tile> tileSaveNames;
    private HashMap<String, Entity> objectIds;

    /**
     * @param gameManager the game's GameManager.
     *                    all tiles and tilemaps that are loaded with this MapLoader will be passed this value;
     */
    public MapLoader(GameManager gameManager) {
        this.gameManager = gameManager;

        //these are all the mappings for string -> tileType when reading map txt files
        tileSaveNames = new ArrayMap<String, Tile>();
        tileSaveNames.put(".", new Grass(gameManager, 0));
        tileSaveNames.put("w", new Wood(gameManager, 1));

        for (Tile tile : tileSaveNames.values()) {
            tile.gameManager = gameManager;
        }

        //all the mappings for loading objects into map from xml
        objectIds = new HashMap<String, Entity>();
        //pass null for tilemap because these objects only serve as a template for cloning
        objectIds.put("test-mob", new TestMob(gameManager));
    }

    /**
     * map must be rectangular
     * <p/>
     * tile data format:
     * linebreaks separate rows; spaces separate columns
     * each cell in the textfile tilemap must correspond
     * to a key in the tilIds map so it can be converted to a Tile object
     * <p/>
     * object data format:
     * <objects>
     * <obj type="type to look up in objectIds map" x="0" y="0" />
     * <obj type="another" x="2" y="5" />
     * </objects>
     */
    public Map loadFromFile(String tileDataPath, String objectDataPath) {
        Map newMap = loadTiles(tileDataPath);
        loadMapObjects(newMap, objectDataPath);
        return newMap;
    }

    public void saveToFile(Map map, String tileDataPath, String objectDataPath) {

    }

    /**
     * load all static tiles from tileDataPath to a new map, returns that map
     * uses Map.setTileAt(...)
     */
    private Map loadTiles(String tileDataPath) {
        String[] mapLines = Gdx.files.internal(tileDataPath).readString().split("\n");

        int width = mapLines[0].split(" ").length;
        int height = mapLines.length;

        Map newMap = new Map(width, height);

        for (int yIter = 0; yIter < height; yIter++) {
            String[] tileStrings = mapLines[yIter].split(" ");

            for (int xIter = 0; xIter < width; xIter++) {
                String tileString = tileStrings[xIter];
                //get a new tile by cloning it from the tile database
                Tile newTile = tileSaveNames.get(tileString).getClone();

                //"newMap.getHeightInTiles() - yIter" because of y-up rendering, but y-down text
                //"-1" because off by one errors with getHeightInTiles() vs index
                newMap.setTileAt(xIter, newMap.getHeightInTiles() - yIter - 1, newTile);
            }

        }
        return newMap;
    }

    /**
     * load all tileMapObjects on map
     * uses Map.addMapObject(...)
     */
    private void loadMapObjects(Map map, String objectDataPath) {
        try {
            XmlReader.Element objectXmlData = xmlReader.parse(Gdx.files.internal(objectDataPath));

            for (XmlReader.Element childObjectXml : objectXmlData.getChildrenByName("obj")) {
                //get new object by cloning it from the String->Entity map
                Entity newMapObject = objectIds.get(childObjectXml.get("type")).getClone();
                newMapObject.getTilePosition().set(
                        childObjectXml.getInt("x"),
                        childObjectXml.getInt("y"));
                map.addMapObject(newMapObject);
            }

        } catch (IOException e) {
            Gdx.app.error("load", "Error while reading xml of Map", e);
            e.printStackTrace();
        }
    }

//    private void saveTiles(Map map, String tileDataPath) {
//        String mapString = "";
//        for (int iterY = 0; iterY < map.getHeightInTiles(); iterY++) {
//            for (int iterX = 0; iterX < map.getWidthInTiles(); iterX++) {
//                //4-level deep forloop, oh no
//                String tileString = getSaveNameById(map.getTileAt(iterX, iterY).id);
//                //TODO
//            }
//        }
//    }
//
//    @Nullable
//    public String getSaveNameById(int id) {
//        for (Tile valueTile : tileSaveNames.values()) {
//            if (valueTile.id == id) {
//
//                //get key from valueTile
//                for (String key : tileSaveNames.keySet()) {
//                    if (tileSaveNames.get(key).equals(valueTile)) {
//                        return key;
//                    }
//                }
//
//            }
//        }
//        return null;
//    }
}