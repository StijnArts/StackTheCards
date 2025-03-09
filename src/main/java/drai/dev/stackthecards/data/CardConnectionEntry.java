package drai.dev.stackthecards.data;

import com.google.gson.stream.*;
import org.json.simple.*;

public class CardConnectionEntry {
    public static final String JSON_SELF_GAME_ID_KEY = "gameId";
    public static final String JSON_SELF_SET_ID_KEY = "setId";
    public static final String JSON_SELF_CARD_ID_KEY = "cardId";
    public static final String JSON_SELF_RARITY_ID_KEY = "rarityId";
    public static final String JSON_ROTATION_KEY = "rotated";
    public static final String JSON_CONNECTION_DIRECTION_KEY = "connectionDirection";
    public static final String JSON_X_MODIFIER_KEY = "xTranslationModifier";
    public static final String JSON_Y_MODIFIER_KEY = "yTranslationModifier";
    public static final String JSON_LAYER_KEY = "layer";
    public static final String CONNECTION_X_MODIFIER = "connection_x_mod";
    public static final String CONNECTION_Y_MODIFIER = "connection_y_mod";
    public static final String CONNECTION_DIRECTION = "connection_direction";
    public static final String CONNECTION_ROTATION = "connection_rotation";
    public static final CardConnectionEntry EMPTY = new CardConnectionEntry(null, 0, 0, 0,null, null);
    public CardIdentifier self = new CardIdentifier();
    //1F to move it one whole block over
    //xmodifier is from the center of the block
    public float xModifier= 0;
    public float yModifier = 0;
    public int layer = 0;
    //attachmentdirection if up then remove all y-offset calcs, do y-offset again, if
    public CardConnectingDirection connectingDirection = CardConnectingDirection.BOTTOM;
    public CardRotation rotation = CardRotation.UPRIGHT;

    public CardConnectionEntry(CardIdentifier self, float xModifier, float yModifier, int layer, CardConnectingDirection connectingDirection, CardRotation rotation) {
        this.self = self;
        this.xModifier = xModifier;
        this.yModifier = yModifier;
        this.layer = layer;
        this.connectingDirection = connectingDirection;
        this.rotation = rotation;
    }

    public CardConnectionEntry(CardIdentifier self) {
        this.self = self;
    }

    public static CardConnectionEntry parse(JSONObject json) throws MalformedJsonException {
        if(json.isEmpty() ||
                (!json.containsKey(JSON_SELF_GAME_ID_KEY) && !json.containsKey(JSON_SELF_SET_ID_KEY) && !json.containsKey(JSON_SELF_CARD_ID_KEY))) {
            throw new MalformedJsonException("Card Game Json was invalid");
        }
        CardConnectionEntry connectionEntry;
        try{
            connectionEntry = new CardConnectionEntry(new CardIdentifier((String) json.get(JSON_SELF_GAME_ID_KEY),
                    (String) json.get(JSON_SELF_SET_ID_KEY),(String) json.get(JSON_SELF_CARD_ID_KEY), (String) json.get(JSON_SELF_RARITY_ID_KEY)));
        } catch (Exception e){
            throw new MalformedJsonException("Card identifier was malformed: "+e.getMessage());
        }

        if(json.containsKey(JSON_ROTATION_KEY)){
            try{
                connectionEntry.rotation = CardRotation.valueOf((String) json.get(JSON_ROTATION_KEY));
            } catch (Exception e){
                throw new MalformedJsonException("Card rotation was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_CONNECTION_DIRECTION_KEY)){
            try{
                connectionEntry.connectingDirection = CardConnectingDirection.valueOf((String) json.get(JSON_CONNECTION_DIRECTION_KEY));
            } catch (Exception e){
                throw new MalformedJsonException("Card connecting direction was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_X_MODIFIER_KEY)){
            try{
                connectionEntry.xModifier = (float)(long) json.get(JSON_X_MODIFIER_KEY);
            } catch (Exception e){
                throw new MalformedJsonException("Component format isItalic was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_Y_MODIFIER_KEY)){
            try{
                connectionEntry.yModifier = (float)(long) json.get(JSON_Y_MODIFIER_KEY);
            } catch (Exception e){
                throw new MalformedJsonException("Component format isItalic was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_LAYER_KEY)) {
            try {
                connectionEntry.layer = (int)(long) json.get(JSON_LAYER_KEY);
            } catch (Exception e) {
                throw new MalformedJsonException("Component format isItalic was malformed: "+e.getMessage());
            }
        }
        return connectionEntry;
    }


    //item frame rotation values
    //clockwise
    //upright = 0.0
    //rotated left = 2
    //upside down = 4
    //rotated right = 6


}
