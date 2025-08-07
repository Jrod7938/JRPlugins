package com.example.Packets;

import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.PacketUtils.PacketDef;
import com.example.PacketUtils.PacketReflection;
import lombok.SneakyThrows;
import net.runelite.api.ItemComposition;
import net.runelite.api.widgets.Widget;
import net.runelite.client.util.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WidgetPackets {
    @SneakyThrows
    public static void queueWidgetActionPacket(int actionFieldNo, int widgetId, int itemId, int childId) {
        PacketReflection.sendPacket(PacketDef.getIfButtonX(), widgetId, childId, itemId, actionFieldNo & 65535);
    }

    @SneakyThrows
    public static void queueWidgetSubAction(Widget widget, String menu, String action) {
        if (widget == null || widget.getItemId() == -1) {
            return;
        }

        ItemComposition composition = EthanApiPlugin.getClient().getItemDefinition(widget.getItemId());
        String[][] subOps = composition.getSubops();
        List<String> actions = Arrays.stream(widget.getActions()).collect(Collectors.toList());

        int menuIndex = -1;
        int actionIndex = -1;

        if (subOps == null) {
            return;
        }

        for (String[] subOp : subOps) {
            if (actionIndex != -1) {
                break;
            }
            if (subOp != null) {
                for (int i = 0; i < subOp.length; i++) {
                    String op = subOp[i];
                    if (op != null && op.equalsIgnoreCase(action)) {
                        actionIndex = i;
                        break;
                    }
                }
            }
        }

        for (int i = 0; i < actions.size(); i++) {
            String a = actions.get(i);
            if (a != null && a.equalsIgnoreCase(menu)) {
                menuIndex = i + 1;
                break;
            }
        }

        if (menuIndex == -1 || actionIndex == -1) {
            return;
        }

        PacketReflection.sendPacket(PacketDef.getIfSubOp(), widget.getId(), widget.getIndex(),
                widget.getItemId(), menuIndex, actionIndex);
    }

    @SneakyThrows
    public static void queueWidgetAction(Widget widget, String... actionlist) {
        if (widget == null) {
            return;
        }
        List<String> actions = Arrays.stream(widget.getActions()).collect(Collectors.toList());
        for (int i = 0; i < actions.size(); i++) {
            if (actions.get(i) == null)
                continue;
            actions.set(i, actions.get(i).toLowerCase());
        }
        int num = -1;
        for (String action : actions) {
            for (String action2 : actionlist) {
                if (action != null && Text.removeTags(action).equalsIgnoreCase(action2)) {
                    num = actions.indexOf(action.toLowerCase()) + 1;
                }
            }
        }

        if (num < 1 || num > 10) {
            return;
        }
        queueWidgetActionPacket(num, widget.getId(), widget.getItemId(), widget.getIndex());
    }

    public static void queueWidgetOnWidget(Widget srcWidget, Widget destWidget) {
        queueWidgetOnWidget(srcWidget.getId(), srcWidget.getIndex(), srcWidget.getItemId(),
                destWidget.getId(), destWidget.getIndex(), destWidget.getItemId());
    }

    public static void queueWidgetOnWidget(int sourceWidgetId, int sourceSlot, int sourceItemId,
                                           int destinationWidgetId, int destinationSlot, int destinationItemId) {
        PacketReflection.sendPacket(PacketDef.getIfButtonT(), sourceWidgetId, sourceSlot, sourceItemId, destinationWidgetId,
                destinationSlot, destinationItemId);
    }

    public static void queueResumePause(int widgetId, int childId) {
        PacketReflection.sendPacket(PacketDef.getResumePausebutton(), widgetId, childId);
    }

    public static void queueResumeCount(int id) {
        PacketReflection.sendPacket(PacketDef.getResumeCountDialog(), id);
    }

    public static void queueDragAndDrop(Widget src, Widget dest) {
        PacketReflection.sendPacket(PacketDef.getOpHeldd(), src.getId(), src.getIndex(),
                src.getItemId(), dest.getId(), dest.getIndex(), dest.getItemId());
    }
}