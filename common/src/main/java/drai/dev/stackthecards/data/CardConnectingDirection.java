package drai.dev.stackthecards.data;

import com.mojang.serialization.*;
import io.netty.buffer.*;
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

//    public static final Codec<CardConnectingDirection> CODEC = Codec.STRING.comapFlatMap(
//            name -> {
//                try {
//                    return DataResult.success(CardConnectingDirection.valueOf(name.toUpperCase()));
//                } catch (IllegalArgumentException e) {
//                    return DataResult.error(() -> "Unknown CardType: " + name);
//                }
//            },
//            CardConnectingDirection::name // Convert back to String
//    );

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

//    public static final StreamCodec<ByteBuf, CardConnectingDirection> STREAM_CODEC = StreamCodec.of(
//            (buf, type) -> buf.writeCharSequence(type.name(), java.nio.charset.StandardCharsets.UTF_8), // Store as string
//            buf -> CardConnectingDirection.valueOf(buf.readCharSequence(10, java.nio.charset.StandardCharsets.UTF_8).toString()) // Read as string
//    );
}
