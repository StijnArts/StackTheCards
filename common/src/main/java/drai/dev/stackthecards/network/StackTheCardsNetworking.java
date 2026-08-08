package drai.dev.stackthecards.network;

import dev.architectury.impl.*;
import dev.architectury.networking.*;
import dev.architectury.platform.*;
import dev.architectury.utils.*;
import drai.dev.stackthecards.client.screen.*;
import drai.dev.stackthecards.items.*;
import drai.dev.stackthecards.registry.*;
import io.netty.buffer.*;
import net.fabricmc.api.*;
import net.minecraft.network.*;
import net.minecraft.network.codec.*;
import net.minecraft.resources.*;
import net.minecraft.server.level.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class StackTheCardsNetworking {
    public static final ResourceLocation SYNC_REGISTRY_ID = ResourceLocation.fromNamespaceAndPath("stackthecards", "sync_registry");
    public static final ResourceLocation UPDATE_BINDER_INDEX = ResourceLocation.fromNamespaceAndPath("stackthecards", "update_binder_index");
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
        if(Platform.getEnvironment() == Env.SERVER) NetworkAggregator.registerS2CType(SYNC_REGISTRY_ID, List.of());
        NetworkManager.registerReceiver(
                NetworkManager.Side.C2S, // Client to Server
                UPDATE_BINDER_INDEX,
                List.of(),
                (buf, context) -> {
                    int index = buf.readInt();
                    Player player = context.getPlayer();

                    // Update the ItemStack on the server side here
                    var binderItem = CardBinderScreenHandler.getBinderItem(player);
                    CardBinder.setIndex(binderItem, index, player);
                }
        );
    }

    public static void syncRegistryToClient(@NotNull ServerPlayer serverPlayer) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

        // Convert the registry data into a buffer
        RegistryFriendlyByteBuf registryBuf = new RegistryFriendlyByteBuf(buf, serverPlayer.server.registryAccess());
        CardGameRegistry.toBuffer(registryBuf);
        // Send the packet
        NetworkManager.sendToPlayer(serverPlayer, SYNC_REGISTRY_ID, registryBuf);
    }

    public static void updateBinderIndex(@NotNull Player player, ItemStack binderItem) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

        // Convert the registry data into a buffer
        RegistryFriendlyByteBuf registryBuf = new RegistryFriendlyByteBuf(buf, player.level().registryAccess());

        ByteBufCodecs.INT.encode(registryBuf, CardBinder.getIndex(binderItem, player));
//        ByteBufCodecs.STRING_UTF8.encode(registryBuf, player.getUUID().toString());

        NetworkManager.sendToServer(UPDATE_BINDER_INDEX, registryBuf);
    }

    public static void syncRegistryFromServer(Player player, RegistryFriendlyByteBuf buf) {
        CardGameRegistry.fromBuffer(buf);
    }

    public static boolean isIntegratedServer() {
        return Platform.getEnvironment() != Env.SERVER && ClientNetworking.isIntegratedServer();
    }
}
