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

    public static Key rightCardLoreKey() {
        return new Key(InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_RIGHT_CONTROL));
    }

    public InputUtil.Key get() {
        return this.inner;
    }

    public void set(InputUtil.Key key) {
        this.inner = key;
    }

    @Nullable
    public static Key cardLoreKey() {
            return new Key(InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_LEFT_CONTROL));
    }

    @Nullable
    public static Key cardPlacementKey() {
        return new Key(InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_LEFT_SHIFT));
    }
    @Nullable
    public static Key flipCardKey() {
            return new Key(InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_LEFT_CONTROL));
    }
}
