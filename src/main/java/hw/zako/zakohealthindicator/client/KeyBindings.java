package hw.zako.zakohealthindicator.client;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

public class KeyBindings {

    static {
        try {
            for (Field f : KeyBinding.class.getDeclaredFields()) {
                if (Map.class.isAssignableFrom(f.getType()) && Modifier.isStatic(f.getModifiers())) {
                    f.setAccessible(true);
                    Map<?, ?> rawMap = (Map<?, ?>) f.get(null);
                    if (rawMap != null && !rawMap.isEmpty()
                            && rawMap.values().iterator().next() instanceof Integer) {
                        @SuppressWarnings("unchecked")
                        Map<String, Integer> map = (Map<String, Integer>) rawMap;
                        map.put("HealthIndicator", map.size());
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[CHI] Category registration failed: " + e);
        }
    }

    public static final KeyBinding OPEN_SETTINGS = new KeyBinding(
            "HealthIndicator",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "HealthIndicator"
    );
}
