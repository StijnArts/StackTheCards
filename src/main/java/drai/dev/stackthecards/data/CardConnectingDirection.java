package drai.dev.stackthecards.data;

public enum CardConnectingDirection {
    TOP(0,-1),
    BOTTOM(0,1),
    LEFT(1,0),
    RIGHT(-1,0);
    CardConnectingDirection(int xMod, int yMod){
        this.xMod = xMod;
        this.yMod = yMod;
    }

    public final int xMod;
    public final int yMod;
}
