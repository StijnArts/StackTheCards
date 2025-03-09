package drai.dev.stackthecards.data;

public enum CardStackingDirection {
    TOP(0, -1),
    BOTTOM(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0),
    TOP_LEFT(-1, -1),
    TOP_RIGHT(1, -1),
    BOTTOM_LEFT(-1, 1),
    BOTTOM_RIGHT(1, 1), CENTER(0,0);
    CardStackingDirection(int xMod, int yMod){
        this.xMod = xMod;
        this.yMod = yMod;
    }
    public final int xMod;
    public final int yMod;
}
