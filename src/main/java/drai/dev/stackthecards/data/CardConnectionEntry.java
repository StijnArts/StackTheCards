package drai.dev.stackthecards.data;

import net.minecraft.util.math.*;

public class CardConnectionEntry {
    public static final String CONNECTION_X_MODIFIER = "connection_x_mod";
    public static final String CONNECTION_Y_MODIFIER = "connection_y_mod";
    public static final String CONNECTION_DIRECTION = "connection_direction";
    public static final String CONNECTION_ROTATION = "connection_rotation";
    public static final CardConnectionEntry EMPTY = new CardConnectionEntry(null, 0, 0, 0,null, null);
    public CardIdentifier self;
    //1F to move it one whole block over
    //xmodifier is from the center of the block
    public float xModifier;
    public float yModifier;
    public int layer;
    //attachmentdirection if up then remove all y-offset calcs, do y-offset again, if
    public CardConnectingDirection connectingDirection;
    public CardRotation rotation;

    public CardConnectionEntry(CardIdentifier self, float xModifier, float yModifier, int layer, CardConnectingDirection connectingDirection, CardRotation rotation) {
        this.self = self;
        this.xModifier = xModifier;
        this.yModifier = yModifier;
        this.layer = layer;
        this.connectingDirection = connectingDirection;
        this.rotation = rotation;
    }
    //item frame rotation values
    //clockwise
    //upright = 0.0
    //rotated left = 2
    //upside down = 4
    //rotated right = 6


}
