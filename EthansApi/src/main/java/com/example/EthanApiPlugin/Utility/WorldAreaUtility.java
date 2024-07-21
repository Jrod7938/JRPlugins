package com.example.EthanApiPlugin.Utility;

import com.example.PacketUtils.PacketReflection;
import net.runelite.api.CollisionData;
import net.runelite.api.CollisionDataFlag;
import net.runelite.api.GameObject;
import net.runelite.api.Point;
import net.runelite.api.TileObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.List;


public class WorldAreaUtility {

    public static List<WorldPoint> objectInteractableTiles(TileObject e){
        Point p;
        int x = 1;
        int y = 1;
        if (e instanceof GameObject) {
            GameObject gameObject = (GameObject) e;
            x = gameObject.sizeX();
            y = gameObject.sizeY();
            p = gameObject.getSceneMinLocation();
        } else {
            p = new Point(e.getLocalLocation().getSceneX(), e.getLocalLocation().getSceneY());
        }
        LocalPoint objectMinLocation = new LocalPoint(p.getX(), p.getY());
        WorldPoint objectMinWorldPoint = WorldPoint.fromScene(PacketReflection.getClient(), objectMinLocation.getX(), objectMinLocation.getY(), e.getPlane());
        LocalPoint areaMinCorner = new LocalPoint(p.getX() - 1, p.getY() - 1);
        WorldPoint areaMinCornerWorldPoint = WorldPoint.fromScene(PacketReflection.getClient(), areaMinCorner.getX(), areaMinCorner.getY(), e.getPlane());
        List<WorldPoint> objectArea = new WorldArea(objectMinWorldPoint, x, y).toWorldPointList();
        int sx = x + 2;
        int sy = y + 2;
        ArrayList<WorldPoint> grownArea = new ArrayList<>(new WorldArea(areaMinCornerWorldPoint, sx, sy).toWorldPointList());
        grownArea.remove(grownArea.size() - 1);
        grownArea.remove((sx * sy) - sx);
        grownArea.remove(sx - 1);
        grownArea.remove(0);
        grownArea.removeAll(objectArea);

        CollisionData[] collisionData = PacketReflection.getClient().getCollisionMaps();
        if (collisionData == null){
            return grownArea;
        }

        int[][] planeData = collisionData[PacketReflection.getClient().getPlane()].getFlags();

        for (int i = grownArea.size() - 1; i >= 0; i --){
            WorldPoint testPoint = grownArea.get(i);
            LocalPoint localScenePoint = LocalPoint.fromWorld(PacketReflection.getClient(), testPoint);
            if (localScenePoint == null){
                continue;
            }

            int flags = planeData[localScenePoint.getSceneX()][localScenePoint.getSceneY()];
            if ((flags & CollisionDataFlag.BLOCK_MOVEMENT_FULL) != 0){
                grownArea.remove(testPoint);
            }
        }

        return grownArea;
    }
}
