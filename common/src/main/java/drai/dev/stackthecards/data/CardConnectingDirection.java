package drai.dev.stackthecards.data;

import com.mojang.serialization.*;
import net.minecraft.network.*;
import net.minecraft.network.codec.*;

import java.util.*;

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

    public static final Codec<CardConnectingDirection> CODEC = Codec.STRING.xmap(
            name -> {
                try {
                    return CardConnectingDirection.valueOf(name.toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Invalid enum value: " + name);
                }
            },
            CardConnectingDirection::name
    );

    public static final StreamCodec<FriendlyByteBuf, CardConnectingDirection> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public CardConnectingDirection decode(FriendlyByteBuf buf) {
            return buf.readEnum(CardConnectingDirection.class);
        }

        @Override
        public void encode(FriendlyByteBuf buf, CardConnectingDirection value) {
            buf.writeEnum(value);
        }
    };
}
