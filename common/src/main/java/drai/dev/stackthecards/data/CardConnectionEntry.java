package drai.dev.stackthecards.data;

import com.google.gson.stream.*;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import drai.dev.stackthecards.data.components.*;
import drai.dev.stackthecards.items.*;
import net.minecraft.network.*;
import net.minecraft.network.codec.*;
import net.minecraft.world.item.*;
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
    public static final CardConnectionEntry EMPTY = new CardConnectionEntry(null, 0, 0, 0,null, null);
    public CardIdentifier self = new CardIdentifier();
    public float xModifier= 0;
    public float yModifier = 0;
    public int layer = 0;
    public CardConnectingDirection connectingDirection = CardConnectingDirection.BOTTOM;
    public CardRotation rotation = CardRotation.UPRIGHT;

    public static final StreamCodec<FriendlyByteBuf, CardConnectionEntry> SYNC_CODEC = new StreamCodec<FriendlyByteBuf, CardConnectionEntry>() {
        @Override
        public void encode(FriendlyByteBuf buffer, CardConnectionEntry value) {
            CardIdentifier.STREAM_CODEC.encode(buffer, value.self);
            ByteBufCodecs.FLOAT.encode(buffer, value.xModifier);
            ByteBufCodecs.FLOAT.encode(buffer, value.yModifier);
            ByteBufCodecs.INT.encode(buffer, value.layer);
            CardConnectingDirection.STREAM_CODEC.encode(buffer, value.connectingDirection);
            CardRotation.STREAM_CODEC.encode(buffer, value.rotation);
        }

        @Override
        public CardConnectionEntry decode(FriendlyByteBuf buffer) {
            CardIdentifier self = CardIdentifier.STREAM_CODEC.decode(buffer);
            float xModifier = ByteBufCodecs.FLOAT.decode(buffer);
            float yModifier = ByteBufCodecs.FLOAT.decode(buffer);
            int layer = ByteBufCodecs.INT.decode(buffer);
            CardConnectingDirection connectingDirection = CardConnectingDirection.STREAM_CODEC.decode(buffer);
            CardRotation rotation = CardRotation.STREAM_CODEC.decode(buffer);

            return new CardConnectionEntry(self, xModifier, yModifier, layer, connectingDirection, rotation);
        }
    };


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

    public static CardConnectionEntryData createConnectionData(CardConnectionEntry connectionEntry) {
//        CompoundTag nbtCompound = (CompoundTag) CardConnectionData.createNbt(connectionEntry.self);
//        nbtCompound.putString(CardConnectionEntry.CONNECTION_X_MODIFIER, String.valueOf(connectionEntry.xModifier));
//        nbtCompound.putString(CardConnectionEntry.CONNECTION_Y_MODIFIER, String.valueOf(connectionEntry.yModifier));
//        nbtCompound.putString(CardConnectionEntry.CONNECTION_DIRECTION, String.valueOf(connectionEntry.connectingDirection));
//        nbtCompound.putString(CardConnectionEntry.CONNECTION_ROTATION, String.valueOf(connectionEntry.rotation));
        return CardConnectionEntryData.from(connectionEntry);
    }

    public static final Codec<CardConnectionEntryData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    CardIdentifier.CODEC.fieldOf("self").forGetter(CardConnectionEntryData::getSelf),
                    Codec.FLOAT.fieldOf("xModifier").forGetter(CardConnectionEntryData::getxModifier),
                    Codec.FLOAT.fieldOf("yModifier").forGetter(CardConnectionEntryData::getyModifier),
                    CardConnectingDirection.CODEC.fieldOf("connectingDirection").forGetter(CardConnectionEntryData::getConnectingDirection),
                    CardRotation.CODEC.fieldOf("rotation").forGetter(CardConnectionEntryData::getRotation)
            ).apply(instance, CardConnectionEntryData::new)
    );
    public static final StreamCodec<FriendlyByteBuf, CardConnectionEntryData> STREAM_CODEC = StreamCodec.composite(
            CardIdentifier.STREAM_CODEC, CardConnectionEntryData::getSelf,
            ByteBufCodecs.FLOAT, CardConnectionEntryData::getxModifier,
            ByteBufCodecs.FLOAT, CardConnectionEntryData::getyModifier,
            CardConnectingDirection.STREAM_CODEC, CardConnectionEntryData::getConnectingDirection,
            CardRotation.STREAM_CODEC, CardConnectionEntryData::getRotation,
            CardConnectionEntryData::new);


    public static Card.CardRecord getOrCreateCardRecord(ItemStack stack) {
        var cardRecord = stack.get(StackTheCardsComponentTypes.CARD_DATA_COMPONENT.get());
        if(cardRecord == null) cardRecord = new Card.CardRecord();
        return cardRecord;
    }

    public static class CardConnectionEntryData {
        public CardIdentifier self = new CardIdentifier();
        public float xModifier= 0;
        public float yModifier = 0;
        //attachmentdirection if up then remove all y-offset calcs, do y-offset again, if
        public CardConnectingDirection connectingDirection = CardConnectingDirection.BOTTOM;
        public CardRotation rotation = CardRotation.UPRIGHT;

        public CardConnectionEntryData(CardIdentifier self, float xModifier, float yModifier, CardConnectingDirection connectingDirection, CardRotation rotation) {
            this.self = self;
            this.self.fixMissingRarity();
            this.xModifier = xModifier;
            this.yModifier = yModifier;
            this.connectingDirection = connectingDirection;
            this.rotation = rotation;
        }

        public static CardConnectionEntryData from(CardConnectionEntry connectionEntry) {
            return new CardConnectionEntryData(connectionEntry.self, connectionEntry.xModifier, connectionEntry.yModifier, connectionEntry.connectingDirection, connectionEntry.rotation);
        }

        public CardIdentifier getSelf() {
            return self;
        }

        public float getxModifier() {
            return xModifier;
        }

        public float getyModifier() {
            return yModifier;
        }

        public CardConnectingDirection getConnectingDirection() {
            return connectingDirection;
        }

        public CardRotation getRotation() {
            return rotation;
        }
    }

    //item frame rotation values
    //clockwise
    //upright = 0.0
    //rotated left = 2
    //upside down = 4
    //rotated right = 6


}
