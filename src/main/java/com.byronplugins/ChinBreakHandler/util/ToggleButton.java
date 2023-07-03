package com.byronplugins.ChinBreakHandler.util;

import com.byronplugins.ChinBreakHandler.ChinBreakHandlerPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;
import net.runelite.client.util.Text;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class ToggleButton extends JCheckBox
{
    private static final ImageIcon ON_SWITCHER;
    private static final ImageIcon OFF_SWITCHER;
    private static final ImageIcon DISABLED_SWITCHER;

    private final Object object;

    static
    {
        BufferedImage onSwitcher = ImageUtil.loadImageResource(ChinBreakHandlerPlugin.class, "switcher_on.png");
        ON_SWITCHER = new ImageIcon(ImageUtil.recolorImage(onSwitcher, ColorScheme.BRAND_ORANGE));
        OFF_SWITCHER = new ImageIcon(ImageUtil.flipImage(
                ImageUtil.luminanceScale(
                        ImageUtil.grayscaleImage(onSwitcher),
                        0.61f
                ),
                true,
                false
        ));
        DISABLED_SWITCHER = new ImageIcon(ImageUtil.flipImage(
                ImageUtil.luminanceScale(
                        ImageUtil.grayscaleImage(onSwitcher),
                        0.4f
                ),
                true,
                false
        ));
    }

    public ToggleButton()
    {
        super(OFF_SWITCHER);
        this.object = null;

        setSelectedIcon(ON_SWITCHER);
        setDisabledIcon(DISABLED_SWITCHER);
        SwingUtil.removeButtonDecorations(this);
    }

    public ToggleButton(String text)
    {
        super(text, OFF_SWITCHER, false);
        this.object = null;

        setSelectedIcon(ON_SWITCHER);
        setDisabledIcon(DISABLED_SWITCHER);
        SwingUtil.removeButtonDecorations(this);
    }

    public ToggleButton(Object object)
    {
        super(Text.titleCase((Enum<?>) object), OFF_SWITCHER, false);
        this.object = object;

        setSelectedIcon(ON_SWITCHER);
        setDisabledIcon(DISABLED_SWITCHER);
        SwingUtil.removeButtonDecorations(this);
    }

    public Object getObject()
    {
        return this.object;
    }
}