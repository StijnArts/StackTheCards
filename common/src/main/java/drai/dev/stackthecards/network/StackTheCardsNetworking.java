package drai.dev.stackthecards.network;

import dev.architectury.impl.*;
import dev.architectury.networking.*;
import drai.dev.stackthecards.registry.*;
import io.netty.buffer.*;
import net.minecraft.network.*;
import net.minecraft.resources.*;
import net.minecraft.server.level.*;
import net.minecraft.world.entity.player.*;

import java.util.*;

public class StackTheCardsNetworking {
    public static final ResourceLocation SYNC_REGISTRY_ID = ResourceLocation.fromNamespaceAndPath("stackthecards", "sync_registry");
//    public static final NetworkChannel CHANNEL = NetworkChannel.create(SYNC_REGISTRY_ID);

    /*public static void registerPackets() {
        NetworkManager.registerReceiver(
                NetworkManager.Side.S2C, // This is a server-to-client packet
                SYNC_REGISTRY_ID,
                (buf, context) -> {
                    RegistryFriendlyByteBuf registryBuf = new RegistryFriendlyByteBuf(buf, context.getPlayer().level().registryAccess());
                    syncRegistryFromServer(context.getPlayer(), registryBuf);
                }
        );
    }*/

    public static void registerPackets() {
        NetworkAggregator.registerS2CType(SYNC_REGISTRY_ID, List.of());
        // Define the payload type and codec
//        CustomPacketPayload.Type<CardGameRegistry> type = new CustomPacketPayload.Type<>(SYNC_REGISTRY_ID, CardGameRegistry.class);
//        StreamCodec<FriendlyByteBuf, CardGameRegistry> codec = new CardGameRegistryCodec(); // Assuming you have a codec for serializing/deserializing the registry data
//
//         Register S2C receiver with a list of transformers (could be empty)
//        List<PacketTransformer> transformers = List.of(); // Add transformers if necessary
//        NetworkManager.registerReceiver(type, codec, transformers, (payload, context) -> {
//             This is the handler when the packet is received
//            RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(payload.payload()), context.registryAccess());
//            syncRegistryFromServer(context.getPlayer(), buf); // Deserialize data
//        });
    }

    public static void syncRegistryToClient(ServerPlayer serverPlayer) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

        // Convert the registry data into a buffer
        RegistryFriendlyByteBuf registryBuf = new RegistryFriendlyByteBuf(buf, serverPlayer.server.registryAccess());
        CardGameRegistry.toBuffer(registryBuf);

        // Send the packet
        NetworkManager.sendToPlayer(serverPlayer, SYNC_REGISTRY_ID, registryBuf);
    }

    public static void syncRegistryFromServer(Player player, RegistryFriendlyByteBuf buf) {
        CardGameRegistry.fromBuffer(buf);
    }
}
