package com.example.PacketUtils;

public final class ObfuscatedNames {

    public static final String EVENT_MOUSE_CLICK_OBFUSCATEDNAME = "cv";
    public static final String EVENT_MOUSE_CLICK_WRITE1 = "mouseInfo";
    public static final String EVENT_MOUSE_CLICK_METHOD_NAME1 = "xg";
    public static final String EVENT_MOUSE_CLICK_WRITE2 = "mouseY";
    public static final String EVENT_MOUSE_CLICK_METHOD_NAME2 = "xg";
    public static final String EVENT_MOUSE_CLICK_WRITE3 = "mouseX";
    public static final String EVENT_MOUSE_CLICK_METHOD_NAME3 = "xg";
    public static final String[][] EVENT_MOUSE_CLICK_WRITES = new String[][] {
            {"r 8", "v"},
            {"r 8", "v"},
            {"r 8", "v"}
    };

    // when officially live we switch to MOUSE_V2 packet
    //    public static final String EVENT_MOUSE_CLICK_OBFUSCATEDNAME = "ch";
    //    public static final String EVENT_MOUSE_CLICK_WRITE1 = "mouseX";
    //    public static final String EVENT_MOUSE_CLICK_METHOD_NAME1 = "dy";
    //    public static final String EVENT_MOUSE_CLICK_WRITE2 = "mouseInfo";
    //    public static final String EVENT_MOUSE_CLICK_METHOD_NAME2 = "kl";
    //    public static final String EVENT_MOUSE_CLICK_WRITE3 = "0";
    //    public static final String EVENT_MOUSE_CLICK_METHOD_NAME3 = "bg";
    //    public static final String EVENT_MOUSE_CLICK_WRITE4 = "mouseY";
    //    public static final String EVENT_MOUSE_CLICK_METHOD_NAME4 = "kl";
    //    public static final String[][] EVENT_MOUSE_CLICK_WRITES = new String[][] {
    //            {"v", "r 8"},
    //            {"a 128", "r 8"},
    //            {"v"},
    //            {"a 128", "r 8"},
    //    };

    public static final String IF_BUTTONT_OBFUSCATEDNAME = "cn";
    public static final String IF_BUTTONT_WRITE1 = "destinationItemId";
    public static final String IF_BUTTONT_METHOD_NAME1 = "kl";
    public static final String IF_BUTTONT_WRITE2 = "destinationWidgetId";
    public static final String IF_BUTTONT_METHOD_NAME2 = "eg";
    public static final String IF_BUTTONT_WRITE3 = "sourceItemId";
    public static final String IF_BUTTONT_METHOD_NAME3 = "mp";
    public static final String IF_BUTTONT_WRITE4 = "destinationSlot";
    public static final String IF_BUTTONT_METHOD_NAME4 = "kl";
    public static final String IF_BUTTONT_WRITE5 = "sourceWidgetId";
    public static final String IF_BUTTONT_METHOD_NAME5 = "eg";
    public static final String IF_BUTTONT_WRITE6 = "sourceSlot";
    public static final String IF_BUTTONT_METHOD_NAME6 = "mp";
    public static final String[][] IF_BUTTONT_WRITES = new String[][]{
            {"a 128", "r 8"},
            {"r 8", "v", "r 24", "r 16"},
            {"r 8", "a 128"},
            {"a 128", "r 8"},
            {"r 8", "v", "r 24", "r 16"},
            {"r 8", "a 128"},
    };

    public static final String IF_BUTTONX_OBFUSCATEDNAME = "al";
    public static final String IF_BUTTONX_WRITE1 = "widgetId";
    public static final String IF_BUTTONX_METHOD_NAME_1 = "bu";
    public static final String IF_BUTTONX_WRITE2 = "slot";
    public static final String IF_BUTTONX_METHOD_NAME_2 = "bt";
    public static final String IF_BUTTONX_WRITE3 = "itemId";
    public static final String IF_BUTTONX_METHOD_NAME_3 = "bt";
    public static final String IF_BUTTONX_WRITE4 = "opCode";
    public static final String IF_BUTTONX_METHOD_NAME_4 = "bg";
    public static final String[][] IF_BUTTONX_WRITES = {
            {"r 24", "r 16", "r 8", "v"},
            {"r 8", "v"},
            {"r 8", "v"},
            {"v"},
    };

    public static final String IF_SUBOP_OBFUSCATEDNAME = "dj";
    public static final String IF_SUBOP_WRITE1 = "widgetId";
    public static final String IF_SUBOP_METHOD_NAME1 = "bu";
    public static final String IF_SUBOP_WRITE2 = "slot";
    public static final String IF_SUBOP_METHOD_NAME2 = "bt";
    public static final String IF_SUBOP_WRITE3 = "itemId";
    public static final String IF_SUBOP_METHOD_NAME3 = "bt";
    public static final String IF_SUBOP_WRITE4 = "menuIndex";
    public static final String IF_SUBOP_METHOD_NAME4 = "bg";
    public static final String IF_SUBOP_WRITE5 = "subActionIndex";
    public static final String IF_SUBOP_METHOD_NAME5 = "bg";
    public static final String[][] IF_SUBOP_WRITES = new String[][]{
            {"r 24", "r 16", "r 8", "v"},
            {"r 8", "v"},
            {"r 8", "v"},
            {"v"},
            {"v"}
    };

    public static final String MOVE_GAMECLICK_OBFUSCATEDNAME = "bd";
    public static final String MOVE_GAMECLICK_WRITE1 = "5";
    public static final String MOVE_GAMECLICK_METHOD_NAME1 = "bg";
    public static final String MOVE_GAMECLICK_WRITE2 = "worldPointX";
    public static final String MOVE_GAMECLICK_METHOD_NAME2 = "dy";
    public static final String MOVE_GAMECLICK_WRITE3 = "worldPointY";
    public static final String MOVE_GAMECLICK_METHOD_NAME3 = "dy";
    public static final String MOVE_GAMECLICK_WRITE4 = "ctrlDown";
    public static final String MOVE_GAMECLICK_METHOD_NAME4 = "bg";
    public static final String[][] MOVE_GAMECLICK_WRITES = new String[][]{
            {"v"},
            {"v", "r 8"},
            {"v", "r 8"},
            {"v"},
    };
    public static final String OPLOC1_OBFUSCATEDNAME = "bi";
    public static final String OPLOC1_WRITE1 = "objectId";
    public static final String OPLOC1_METHOD_NAME1 = "bt";
    public static final String OPLOC1_WRITE2 = "ctrlDown";
    public static final String OPLOC1_METHOD_NAME2 = "db";
    public static final String OPLOC1_WRITE3 = "worldPointX";
    public static final String OPLOC1_METHOD_NAME3 = "dy";
    public static final String OPLOC1_WRITE4 = "worldPointY";
    public static final String OPLOC1_METHOD_NAME4 = "bt";
    public static final String[][] OPLOC1_WRITES = new String[][]{
            {"r 8", "v"},
            {"s 0"},
            {"v", "r 8"},
            {"r 8", "v"},
    };
    public static final String OPLOC2_OBFUSCATEDNAME = "bw";
    public static final String OPLOC2_WRITE1 = "worldPointX";
    public static final String OPLOC2_METHOD_NAME1 = "mp";
    public static final String OPLOC2_WRITE2 = "objectId";
    public static final String OPLOC2_METHOD_NAME2 = "bt";
    public static final String OPLOC2_WRITE3 = "worldPointY";
    public static final String OPLOC2_METHOD_NAME3 = "mp";
    public static final String OPLOC2_WRITE4 = "ctrlDown";
    public static final String OPLOC2_METHOD_NAME4 = "dg";
    public static final String[][] OPLOC2_WRITES = new String[][]{
            {"r 8", "a 128"},
            {"r 8", "v"},
            {"r 8", "a 128"},
            {"a 128"},
    };
    public static final String OPLOC3_OBFUSCATEDNAME = "by";
    public static final String OPLOC3_WRITE1 = "worldPointY";
    public static final String OPLOC3_METHOD_NAME1 = "dy";
    public static final String OPLOC3_WRITE2 = "ctrlDown";
    public static final String OPLOC3_METHOD_NAME2 = "dg";
    public static final String OPLOC3_WRITE3 = "worldPointX";
    public static final String OPLOC3_METHOD_NAME3 = "kl";
    public static final String OPLOC3_WRITE4 = "objectId";
    public static final String OPLOC3_METHOD_NAME4 = "kl";
    public static final String[][] OPLOC3_WRITES = new String[][]{
            {"v", "r 8"},
            {"a 128"},
            {"a 128", "r 8"},
            {"a 128", "r 8"},
    };
    public static final String OPLOC4_OBFUSCATEDNAME = "dk";
    public static final String OPLOC4_WRITE1 = "objectId";
    public static final String OPLOC4_METHOD_NAME1 = "bt";
    public static final String OPLOC4_WRITE2 = "worldPointX";
    public static final String OPLOC4_METHOD_NAME2 = "bt";
    public static final String OPLOC4_WRITE3 = "ctrlDown";
    public static final String OPLOC4_METHOD_NAME3 = "bg";
    public static final String OPLOC4_WRITE4 = "worldPointY";
    public static final String OPLOC4_METHOD_NAME4 = "mp";
    public static final String[][] OPLOC4_WRITES = new String[][]{
            {"r 8", "v"},
            {"r 8", "v"},
            {"v"},
            {"r 8", "a 128"},
    };
    public static final String OPLOC5_OBFUSCATEDNAME = "cl";
    public static final String OPLOC5_WRITE1 = "objectId";
    public static final String OPLOC5_METHOD_NAME1 = "bt";
    public static final String OPLOC5_WRITE2 = "ctrlDown";
    public static final String OPLOC5_METHOD_NAME2 = "db";
    public static final String OPLOC5_WRITE3 = "worldPointY";
    public static final String OPLOC5_METHOD_NAME3 = "kl";
    public static final String OPLOC5_WRITE4 = "worldPointX";
    public static final String OPLOC5_METHOD_NAME4 = "bt";
    public static final String[][] OPLOC5_WRITES = new String[][]{
            {"r 8", "v"},
            {"s 0"},
            {"a 128", "r 8"},
            {"r 8", "v"},
    };
    public static final String OPLOCT_OBFUSCATEDNAME = "ah";
    public static final String OPLOCT_WRITE1 = "ctrlDown";
    public static final String OPLOCT_METHOD_NAME1 = "dh";
    public static final String OPLOCT_WRITE2 = "slot";
    public static final String OPLOCT_METHOD_NAME2 = "dy";
    public static final String OPLOCT_WRITE3 = "worldPointX";
    public static final String OPLOCT_METHOD_NAME3 = "dy";
    public static final String OPLOCT_WRITE4 = "objectId";
    public static final String OPLOCT_METHOD_NAME4 = "kl";
    public static final String OPLOCT_WRITE5 = "worldPointY";
    public static final String OPLOCT_METHOD_NAME5 = "bt";
    public static final String OPLOCT_WRITE6 = "itemId";
    public static final String OPLOCT_METHOD_NAME6 = "dy";
    public static final String OPLOCT_WRITE7 = "widgetId";
    public static final String OPLOCT_METHOD_NAME7 = "eg";
    public static final String[][] OPLOCT_WRITES = new String[][]{
            {"s 128"},
            {"v", "r 8"},
            {"v", "r 8"},
            {"a 128", "r 8"},
            {"r 8", "v"},
            {"v", "r 8"},
            {"r 8", "v", "r 24", "r 16"},
    };
    public static final String OPNPC1_OBFUSCATEDNAME = "ac";
    public static final String OPNPC1_WRITE1 = "npcIndex";
    public static final String OPNPC1_METHOD_NAME1 = "dy";
    public static final String OPNPC1_WRITE2 = "ctrlDown";
    public static final String OPNPC1_METHOD_NAME2 = "dh";
    public static final String[][] OPNPC1_WRITES = new String[][]{
            {"v", "r 8"},
            {"s 128"},
    };
    public static final String OPNPC2_OBFUSCATEDNAME = "cr";
    public static final String OPNPC2_WRITE1 = "ctrlDown";
    public static final String OPNPC2_METHOD_NAME1 = "bg";
    public static final String OPNPC2_WRITE2 = "npcIndex";
    public static final String OPNPC2_METHOD_NAME2 = "kl";
    public static final String[][] OPNPC2_WRITES = new String[][]{
            {"v"},
            {"a 128", "r 8"},
    };
    public static final String OPNPC3_OBFUSCATEDNAME = "dw";
    public static final String OPNPC3_WRITE1 = "npcIndex";
    public static final String OPNPC3_METHOD_NAME1 = "dy";
    public static final String OPNPC3_WRITE2 = "ctrlDown";
    public static final String OPNPC3_METHOD_NAME2 = "dh";
    public static final String[][] OPNPC3_WRITES = new String[][]{
            {"v", "r 8"},
            {"s 128"},
    };
    public static final String OPNPC4_OBFUSCATEDNAME = "ar";
    public static final String OPNPC4_WRITE1 = "npcIndex";
    public static final String OPNPC4_METHOD_NAME1 = "mp";
    public static final String OPNPC4_WRITE2 = "ctrlDown";
    public static final String OPNPC4_METHOD_NAME2 = "dh";
    public static final String[][] OPNPC4_WRITES = new String[][]{
            {"r 8", "a 128"},
            {"s 128"},
    };
    public static final String OPNPC5_OBFUSCATEDNAME = "ab";
    public static final String OPNPC5_WRITE1 = "npcIndex";
    public static final String OPNPC5_METHOD_NAME1 = "dy";
    public static final String OPNPC5_WRITE2 = "ctrlDown";
    public static final String OPNPC5_METHOD_NAME2 = "dg";
    public static final String[][] OPNPC5_WRITES = new String[][]{
            {"v", "r 8"},
            {"a 128"},
    };
    public static final String OPNPCT_OBFUSCATEDNAME = "cf";
    public static final String OPNPCT_WRITE1 = "slot";
    public static final String OPNPCT_METHOD_NAME1 = "kl";
    public static final String OPNPCT_WRITE2 = "widgetId";
    public static final String OPNPCT_METHOD_NAME2 = "es";
    public static final String OPNPCT_WRITE3 = "itemId";
    public static final String OPNPCT_METHOD_NAME3 = "dy";
    public static final String OPNPCT_WRITE4 = "npcIndex";
    public static final String OPNPCT_METHOD_NAME4 = "bt";
    public static final String OPNPCT_WRITE5 = "ctrlDown";
    public static final String OPNPCT_METHOD_NAME5 = "db";
    public static final String[][] OPNPCT_WRITES = new String[][]{
            {"a 128", "r 8"},
            {"r 16", "r 24", "v", "r 8"},
            {"v", "r 8"},
            {"r 8", "v"},
            {"s 0"},
    };
    public static final String OPOBJ1_OBFUSCATEDNAME = "ct";
    public static final String OPOBJ1_WRITE1 = "ctrlDown";
    public static final String OPOBJ1_METHOD_NAME1 = "dg";
    public static final String OPOBJ1_WRITE2 = "worldPointY";
    public static final String OPOBJ1_METHOD_NAME2 = "dy";
    public static final String OPOBJ1_WRITE3 = "objectId";
    public static final String OPOBJ1_METHOD_NAME3 = "dy";
    public static final String OPOBJ1_WRITE4 = "worldPointX";
    public static final String OPOBJ1_METHOD_NAME4 = "bt";
    public static final String[][] OPOBJ1_WRITES = new String[][]{
            {"a 128"},
            {"v", "r 8"},
            {"v", "r 8"},
            {"r 8", "v"},
    };
    public static final String OPOBJ2_OBFUSCATEDNAME = "be";
    public static final String OPOBJ2_WRITE1 = "worldPointX";
    public static final String OPOBJ2_METHOD_NAME1 = "mp";
    public static final String OPOBJ2_WRITE2 = "worldPointY";
    public static final String OPOBJ2_METHOD_NAME2 = "dy";
    public static final String OPOBJ2_WRITE3 = "ctrlDown";
    public static final String OPOBJ2_METHOD_NAME3 = "bg";
    public static final String OPOBJ2_WRITE4 = "objectId";
    public static final String OPOBJ2_METHOD_NAME4 = "bt";
    public static final String[][] OPOBJ2_WRITES = new String[][]{
            {"r 8", "a 128"},
            {"v", "r 8"},
            {"v"},
            {"r 8", "v"},
    };
    public static final String OPOBJ3_OBFUSCATEDNAME = "cu";
    public static final String OPOBJ3_WRITE1 = "worldPointY";
    public static final String OPOBJ3_METHOD_NAME1 = "mp";
    public static final String OPOBJ3_WRITE2 = "ctrlDown";
    public static final String OPOBJ3_METHOD_NAME2 = "bg";
    public static final String OPOBJ3_WRITE3 = "worldPointX";
    public static final String OPOBJ3_METHOD_NAME3 = "kl";
    public static final String OPOBJ3_WRITE4 = "objectId";
    public static final String OPOBJ3_METHOD_NAME4 = "mp";
    public static final String[][] OPOBJ3_WRITES = new String[][]{
            {"r 8", "a 128"},
            {"v"},
            {"a 128", "r 8"},
            {"r 8", "a 128"},
    };
    public static final String OPOBJ4_OBFUSCATEDNAME = "ao";
    public static final String OPOBJ4_WRITE1 = "worldPointX";
    public static final String OPOBJ4_METHOD_NAME1 = "dy";
    public static final String OPOBJ4_WRITE2 = "ctrlDown";
    public static final String OPOBJ4_METHOD_NAME2 = "dh";
    public static final String OPOBJ4_WRITE3 = "worldPointY";
    public static final String OPOBJ4_METHOD_NAME3 = "dy";
    public static final String OPOBJ4_WRITE4 = "objectId";
    public static final String OPOBJ4_METHOD_NAME4 = "dy";
    public static final String[][] OPOBJ4_WRITES = new String[][]{
            {"v", "r 8"},
            {"s 128"},
            {"v", "r 8"},
            {"v", "r 8"},
    };
    public static final String OPOBJ5_OBFUSCATEDNAME = "cy";
    public static final String OPOBJ5_WRITE1 = "worldPointY";
    public static final String OPOBJ5_METHOD_NAME1 = "bt";
    public static final String OPOBJ5_WRITE2 = "objectId";
    public static final String OPOBJ5_METHOD_NAME2 = "dy";
    public static final String OPOBJ5_WRITE3 = "worldPointX";
    public static final String OPOBJ5_METHOD_NAME3 = "mp";
    public static final String OPOBJ5_WRITE4 = "ctrlDown";
    public static final String OPOBJ5_METHOD_NAME4 = "db";
    public static final String[][] OPOBJ5_WRITES = new String[][]{
            {"r 8", "v"},
            {"v", "r 8"},
            {"r 8", "a 128"},
            {"s 0"},
    };
    public static final String OPOBJT_OBFUSCATEDNAME = "cd";
    public static final String OPOBJT_WRITE1 = "itemId";
    public static final String OPOBJT_METHOD_NAME1 = "dy";
    public static final String OPOBJT_WRITE2 = "worldPointY";
    public static final String OPOBJT_METHOD_NAME2 = "kl";
    public static final String OPOBJT_WRITE3 = "ctrlDown";
    public static final String OPOBJT_METHOD_NAME3 = "db";
    public static final String OPOBJT_WRITE4 = "worldPointX";
    public static final String OPOBJT_METHOD_NAME4 = "mp";
    public static final String OPOBJT_WRITE5 = "objectId";
    public static final String OPOBJT_METHOD_NAME5 = "kl";
    public static final String OPOBJT_WRITE6 = "widgetId";
    public static final String OPOBJT_METHOD_NAME6 = "eg";
    public static final String OPOBJT_WRITE7 = "slot";
    public static final String OPOBJT_METHOD_NAME7 = "mp";
    public static final String[][] OPOBJT_WRITES = new String[][]{
            {"v", "r 8"},
            {"a 128", "r 8"},
            {"s 0"},
            {"r 8", "a 128"},
            {"a 128", "r 8"},
            {"r 8", "v", "r 24", "r 16"},
            {"r 8", "a 128"},
    };
    public static final String OPPLAYER1_OBFUSCATEDNAME = "am";
    public static final String OPPLAYER1_WRITE1 = "playerIndex";
    public static final String OPPLAYER1_METHOD_NAME1 = "dy";
    public static final String OPPLAYER1_WRITE2 = "ctrlDown";
    public static final String OPPLAYER1_METHOD_NAME2 = "bg";
    public static final String[][] OPPLAYER1_WRITES = new String[][]{
            {"v", "r 8"},
            {"v"},
    };
    public static final String OPPLAYER2_OBFUSCATEDNAME = "bx";
    public static final String OPPLAYER2_WRITE1 = "playerIndex";
    public static final String OPPLAYER2_METHOD_NAME1 = "bt";
    public static final String OPPLAYER2_WRITE2 = "ctrlDown";
    public static final String OPPLAYER2_METHOD_NAME2 = "dg";
    public static final String[][] OPPLAYER2_WRITES = new String[][]{
            {"r 8", "v"},
            {"a 128"},
    };
    public static final String OPPLAYER3_OBFUSCATEDNAME = "cc";
    public static final String OPPLAYER3_WRITE1 = "playerIndex";
    public static final String OPPLAYER3_METHOD_NAME1 = "mp";
    public static final String OPPLAYER3_WRITE2 = "ctrlDown";
    public static final String OPPLAYER3_METHOD_NAME2 = "db";
    public static final String[][] OPPLAYER3_WRITES = new String[][]{
            {"r 8", "a 128"},
            {"s 0"},
    };
    public static final String OPPLAYER4_OBFUSCATEDNAME = "bv";
    public static final String OPPLAYER4_WRITE1 = "ctrlDown";
    public static final String OPPLAYER4_METHOD_NAME1 = "bg";
    public static final String OPPLAYER4_WRITE2 = "playerIndex";
    public static final String OPPLAYER4_METHOD_NAME2 = "mp";
    public static final String[][] OPPLAYER4_WRITES = new String[][]{
            {"v"},
            {"r 8", "a 128"},
    };
    public static final String OPPLAYER5_OBFUSCATEDNAME = "bc";
    public static final String OPPLAYER5_WRITE1 = "ctrlDown";
    public static final String OPPLAYER5_METHOD_NAME1 = "bg";
    public static final String OPPLAYER5_WRITE2 = "playerIndex";
    public static final String OPPLAYER5_METHOD_NAME2 = "kl";
    public static final String[][] OPPLAYER5_WRITES = new String[][]{
            {"v"},
            {"a 128", "r 8"},
    };
    public static final String OPPLAYER6_OBFUSCATEDNAME = "ai";
    public static final String OPPLAYER6_WRITE1 = "playerIndex";
    public static final String OPPLAYER6_METHOD_NAME1 = "kl";
    public static final String OPPLAYER6_WRITE2 = "ctrlDown";
    public static final String OPPLAYER6_METHOD_NAME2 = "bg";
    public static final String[][] OPPLAYER6_WRITES = new String[][]{
            {"a 128", "r 8"},
            {"v"},
    };
    public static final String OPPLAYER7_OBFUSCATEDNAME = "ce";
    public static final String OPPLAYER7_WRITE1 = "playerIndex";
    public static final String OPPLAYER7_METHOD_NAME1 = "kl";
    public static final String OPPLAYER7_WRITE2 = "ctrlDown";
    public static final String OPPLAYER7_METHOD_NAME2 = "dg";
    public static final String[][] OPPLAYER7_WRITES = new String[][]{
            {"a 128", "r 8"},
            {"a 128"},
    };
    public static final String OPPLAYER8_OBFUSCATEDNAME = "bp";
    public static final String OPPLAYER8_WRITE1 = "ctrlDown";
    public static final String OPPLAYER8_METHOD_NAME1 = "bg";
    public static final String OPPLAYER8_WRITE2 = "playerIndex";
    public static final String OPPLAYER8_METHOD_NAME2 = "kl";
    public static final String[][] OPPLAYER8_WRITES = new String[][]{
            {"v"},
            {"a 128", "r 8"},
    };
    public static final String OPPLAYERT_OBFUSCATEDNAME = "ds";
    public static final String OPPLAYERT_WRITE1 = "playerIndex";
    public static final String OPPLAYERT_METHOD_NAME1 = "mp";
    public static final String OPPLAYERT_WRITE2 = "slot";
    public static final String OPPLAYERT_METHOD_NAME2 = "dy";
    public static final String OPPLAYERT_WRITE3 = "widgetId";
    public static final String OPPLAYERT_METHOD_NAME3 = "es";
    public static final String OPPLAYERT_WRITE4 = "ctrlDown";
    public static final String OPPLAYERT_METHOD_NAME4 = "dh";
    public static final String OPPLAYERT_WRITE5 = "itemId";
    public static final String OPPLAYERT_METHOD_NAME5 = "kl";
    public static final String[][] OPPLAYERT_WRITES = new String[][]{
            {"r 8", "a 128"},
            {"v", "r 8"},
            {"r 16", "r 24", "v", "r 8"},
            {"s 128"},
            {"a 128", "r 8"},
    };

    public static final String OPHELDD_OBFUSCATEDNAME = "di";
    public static final String OPHELDD_WRITE1 = "selectedItemId";
    public static final String OPHELDD_METHOD_NAME1 = "bt";
    public static final String OPHELDD_WRITE2 = "destChildIndex";
    public static final String OPHELDD_METHOD_NAME2 = "dy";
    public static final String OPHELDD_WRITE3 = "destItemId";
    public static final String OPHELDD_METHOD_NAME3 = "dy";
    public static final String OPHELDD_WRITE4 = "selectedId";
    public static final String OPHELDD_METHOD_NAME4 = "en";
    public static final String OPHELDD_WRITE5 = "selectedChildIndex";
    public static final String OPHELDD_METHOD_NAME5 = "kl";
    public static final String OPHELDD_WRITE6 = "destId";
    public static final String OPHELDD_METHOD_NAME6 = "eg";
    public static final String[][] OPHELDD_WRITES = new String[][] {
            {"r 8", "v"},
            {"v", "r 8"},
            {"v", "r 8"},
            {"v", "r 8", "r 16", "r 24"},
            {"a 128", "r 8"},
            {"r 8", "v", "r 24", "r 16"}
    };
    public static final String RESUME_COUNTDIALOG_OBFUSCATEDNAME = "au";
    public static final String RESUME_COUNTDIALOG_WRITE1 = "var0";
    public static final String RESUME_COUNTDIALOG_METHOD_NAME1 = "bu";
    public static final String[][] RESUME_COUNTDIALOG_WRITES = new String[][]{
            {"r 24", "r 16", "r 8", "v"},
    };
    public static final String RESUME_PAUSEBUTTON_OBFUSCATEDNAME = "bj";
    public static final String RESUME_PAUSEBUTTON_WRITE1 = "var1";
    public static final String RESUME_PAUSEBUTTON_METHOD_NAME1 = "kl";
    public static final String RESUME_PAUSEBUTTON_WRITE2 = "var0";
    public static final String RESUME_PAUSEBUTTON_METHOD_NAME2 = "es";
    public static final String[][] RESUME_PAUSEBUTTON_WRITES = new String[][]{
            {"a 128", "r 8"},
            {"r 16", "r 24", "v", "r 8"}
    };

    public static final String offsetMultiplier = "944624261";
    public static final String indexMultiplier = "-602712499";
    public static final String addNodeGarbageValue = "-41";
    public static final String getPacketBufferNodeGarbageValue = "-26977627";
    public static final String packetWriterFieldName = "cv";
    public static final String isaacCipherFieldName = "av";
    public static final String addNodeMethodName = "ac";
    public static final String clientPacketClassName = "mi";
    public static final String packetWriterClassName = "dw";
    public static final String classContainingGetPacketBufferNodeName = "lh";
    public static final String packetBufferNodeClassName = "mb";
    public static final String packetBufferFieldName = "ap";
    public static final String bufferOffsetField = "aa";
    public static final String bufferArrayField = "ay";
    public static final String MouseHandler_lastPressedTimeMillisClass = "bx";
    public static final String MouseHandler_lastPressedTimeMillisField = "ag";
    public static final String clientMillisField = "jp";
    public static final String mouseHandlerMillisMultiplier = "4013550640572753771";
    public static final String clientMillisMultiplier = "7881463686914378693";
    public static final int getAnimationMultiplier = 2116322795;
    public static final int skullIconMultiplier = 0;
    public static final String skullIconField = "null";

    public static final int pathLengthMultiplier = 0;
    public static final String pathLengthFieldName = "null";
    public static final String doActionClassName = "fy";
    public static final String doActionMethodName = "lu";
}