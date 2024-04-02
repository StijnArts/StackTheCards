package drai.dev.stackthecards.data;

import net.minecraft.util.*;

public enum CardRotation {
    UPRIGHT(new Pair<>(0F,128F),new Pair<>(128F,128F),new Pair<>(128F,0F),new Pair<>(0F,0F)),
    LEFT(new Pair<>(0F,0F),new Pair<>(0F,128F),new Pair<>(128F,128F),new Pair<>(128F,0F)),
    UPSIDE_DOWN(new Pair<>(128F,0F),new Pair<>(0F,0F),new Pair<>(0F,128F),new Pair<>(128F,128F)),
    RIGHT(new Pair<>(128F,128F),new Pair<>(128F,0F),new Pair<>(0F,0F),new Pair<>(0F,128F));

    CardRotation(Pair<Float, Float> bottomLeft, Pair<Float, Float> bottomRight, Pair<Float, Float> topRight, Pair<Float, Float> topLeft) {
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
        this.topLeft = topLeft;
        this.topRight = topRight;
    }

    public Pair<Float, Float> bottomLeft;
    public Pair<Float, Float> bottomRight;
    public Pair<Float, Float> topRight;
    public Pair<Float, Float> topLeft;
}
