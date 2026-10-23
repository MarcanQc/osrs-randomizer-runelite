package com.osrsrandomizer;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/** Small bundled PNGs, cached for the lifetime of the panel. No network access. */
final class ActivityIcons
{
    private final Map<String, ImageIcon> cache = new HashMap<>();

    ImageIcon activity(Category category, Activity activity, int size)
    {

        return load(category.getKey() + "/" + activity.getId(), size);
    }

    ImageIcon category(Category category)
    {
        return load("categories/" + category.getKey(), 20);
    }

    private ImageIcon load(String path, int size)
    {
        return cache.computeIfAbsent(path + ":" + size, key ->
        {
            BufferedImage target = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            try (InputStream stream = ActivityIcons.class.getResourceAsStream("/com/osrsrandomizer/icons/" + path + ".png"))
            {
                BufferedImage source = stream == null ? null : ImageIO.read(stream);
                if (source != null)
                {
                    Graphics2D g = target.createGraphics();
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    double scale = Math.min((double) size / source.getWidth(), (double) size / source.getHeight());
                    int width = Math.max(1, (int) Math.round(source.getWidth() * scale));
                    int height = Math.max(1, (int) Math.round(source.getHeight() * scale));
                    g.drawImage(source, (size - width) / 2, (size - height) / 2, width, height, null);
                    g.dispose();
                }
            }
            catch (IOException ignored)
            {
                // Keep activity names and selection usable if a resource cannot be decoded.
            }
            return new ImageIcon(target);
        });
    }
}
