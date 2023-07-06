package com.piggyplugins.PiggyUtils.API;

import com.example.EthanApiPlugin.Collections.Equipment;

public class EquipmentUtil {
    public static boolean hasItem(String name) {
        return Equipment.search().nameContainsNoCase(name).first().isPresent();
    }

    public static boolean hasItems(String ...names) {
        for (String name : names) {
            if (!hasItem(name)) {
                return false;
            }
        }

        return true;
    }

    public static boolean hasItem(int id) {
        return Equipment.search().withId(id).first().isPresent();
    }
}
