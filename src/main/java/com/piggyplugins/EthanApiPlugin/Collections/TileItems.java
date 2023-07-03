package com.piggyplugins.EthanApiPlugin.Collections;

import com.piggyplugins.EthanApiPlugin.Collections.query.TileItemQuery;

import java.util.ArrayList;
import java.util.List;

public class TileItems {
    public static List<ETileItem> tileItems = new ArrayList<>();

    public static TileItemQuery search() {
        return new TileItemQuery(tileItems);
    }
}
