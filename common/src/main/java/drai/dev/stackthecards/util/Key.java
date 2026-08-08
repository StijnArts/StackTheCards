package drai.dev.stackthecards.util;


import com.mojang.blaze3d.platform.*;
import net.fabricmc.api.*;
import org.jetbrains.annotations.*;
import org.lwjgl.glfw.*;

//@Environment(EnvType.CLIENT)
public final class Key {
    public static final Key UNKNOWN_KEY = new Key(InputConstants.UNKNOWN);

    InputConstants.Key inner;

    public Key(InputConstants.Key key) {
        this.inner = key;
    }

    public static Key rightCardLoreKey() {
        return new Key(InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_RIGHT_CONTROL));
    }

    public InputConstants.Key get() {
        return this.inner;
    }

    public void set(InputConstants.Key key) {
        this.inner = key;
    }

    @Nullable
    public static Key cardLoreKey() {
            return new Key(InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_LEFT_CONTROL));
    }

    @Nullable
    public static Key cardPlacementKey() {
        return new Key(InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_LEFT_SHIFT));
    }
    @Nullable
    public static Key flipCardKey() {
            return new Key(InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_LEFT_CONTROL));
    }
}
