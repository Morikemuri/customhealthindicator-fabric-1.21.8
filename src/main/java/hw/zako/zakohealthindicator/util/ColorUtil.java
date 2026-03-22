package hw.zako.zakohealthindicator.util;

public class ColorUtil {

    public static int hpColor(float p) {
        p = Math.max(0f, Math.min(1f, p));
        if (p < 0.25f) {
            // HP < 5: pulse dark red (0x8C0000) → light red (0xFF0000)
            float t = 0.5f + 0.5f * (float) Math.sin(System.currentTimeMillis() * 0.006);
            int r = 0x8C + (int)((0xFF - 0x8C) * t);
            return r << 16;
        }
        // Full HP and above: dark green
        return 0x00AA00;
    }

    public static int hsvToRgb(float h, float s, float v) {
        h -= (float) Math.floor(h);
        int   i = (int)(h * 6);
        float f = h * 6 - i, p = v*(1-s), q = v*(1-f*s), t = v*(1-(1-f)*s);
        float r, g, b;
        switch (i % 6) {
            case 0: r=v; g=t; b=p; break; case 1: r=q; g=v; b=p; break;
            case 2: r=p; g=v; b=t; break; case 3: r=p; g=q; b=v; break;
            case 4: r=t; g=p; b=v; break; default: r=v; g=p; b=q; break;
        }
        return ((int)(r*255) << 16) | ((int)(g*255) << 8) | (int)(b*255);
    }
}
