package drai.dev.stackthecards.data;

import com.mojang.datafixers.util.*;
import com.mojang.serialization.*;
import net.minecraft.network.*;
import net.minecraft.network.codec.*;
import org.jetbrains.annotations.*;

import java.util.*;

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

    public static final Codec<CardRotation> CODEC = Codec.STRING.xmap(
            name -> {
                try {
                    return CardRotation.valueOf(name.toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Invalid enum value: " + name);
                }
            },
            CardRotation::name
    );

//    public static final Codec<CardRotation> CODEC = Codec.STRING.comapFlatMap(
//            name -> {
//                try {
//                    return DataResult.success(CardRotation.valueOf(name.toUpperCase())); // Convert safely
//                } catch (IllegalArgumentException e) {
//                    return DataResult.error(() -> "Unknown CardType: " + name);
//                }
//            },
//            CardRotation::name // Convert back to String
//    );

    public static final StreamCodec<FriendlyByteBuf, CardRotation> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull CardRotation decode(FriendlyByteBuf buf) {
            return buf.readEnum(CardRotation.class);
        }

        @Override
        public void encode(FriendlyByteBuf buf, CardRotation value) {
            buf.writeEnum(value);
        }
    };

//    public static final StreamCodec<ByteBuf, CardRotation> STREAM_CODEC = StreamCodec.of(
//            (buf, type) -> buf.writeCharSequence(type.name(), java.nio.charset.StandardCharsets.UTF_8), // Store as string
//            buf -> CardRotation.valueOf(buf.readCharSequence(10, java.nio.charset.StandardCharsets.UTF_8).toString()) // Read as string
//    );
}
