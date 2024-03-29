package drai.dev.stackthecards.util;


import net.fabricmc.api.*;
import net.minecraft.client.util.*;
import org.jetbrains.annotations.*;
import org.lwjgl.glfw.*;

@Environment(EnvType.CLIENT)
public final class Key {
    public static final Key UNKNOWN_KEY = new Key(InputUtil.UNKNOWN_KEY);

    InputUtil.Key inner;

    public Key(InputUtil.Key key) {
        this.inner = key;
    }

    public InputUtil.Key get() {
        return this.inner;
    }

    public void set(InputUtil.Key key) {
        this.inner = key;
    }

    @Nullable
    public static Key cardLoreKey() {
            return new Key(InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_LEFT_SHIFT));
    }
}
