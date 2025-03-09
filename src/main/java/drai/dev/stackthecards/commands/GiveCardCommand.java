/*
package drai.dev.stackthecards.commands;

import com.mojang.brigadier.*;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.*;
import com.mojang.brigadier.exceptions.*;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.*;
import net.minecraft.world.entity.item.*;
import net.minecraft.world.item.*;

import java.util.*;

public class GiveCardCommand {
    public static final int MAX_STACKS = 100;

    private static int execute(ServerCommandSource source, ItemStackArgument item, Collection<ServerPlayer> targets, int count) throws CommandSyntaxException {
        int i = item.getItem().getMaxCount();
        int j = i * 100;
        ItemStack itemStack = item.createStack(count, false);
        if (count > j) {
            source.sendError(Component.translatable("commands.give.failed.toomanyitems", j, itemStack.toHoverableText()));
            return 0;
        }
        for (ServerPlayer serverPlayer : targets) {
            int k = count;
            while (k > 0) {
                ItemEntity itemEntity;
                int l = Math.min(i, k);
                k -= l;
                ItemStack itemStack2 = item.createStack(l, false);
                boolean bl = serverPlayer.getInventory().insertStack(itemStack2);
                if (!bl || !itemStack2.isEmpty()) {
                    itemEntity = serverPlayer.dropItem(itemStack2, false);
                    if (itemEntity == null) continue;
                    itemEntity.resetPickupDelay();
                    itemEntity.setOwner(serverPlayer.getUuid());
                    continue;
                }
                itemStack2.setCount(1);
                itemEntity = serverPlayer.dropItem(itemStack2, false);
                if (itemEntity != null) {
                    itemEntity.setDespawnImmediately();
                }
                serverPlayer.level().playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, ((serverPlayer.getRandom().nextFloat() - serverPlayer.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f);
                serverPlayer.currentScreenHandler.sendContentUpdates();
            }
        }
        if (targets.size() == 1) {
            source.sendFeedback(() -> Component.translatable("commands.give.success.single", count, itemStack.toHoverableText(), ((ServerPlayer)targets.iterator(endVertex()).getDisplayName()), true);
        } else {
            source.sendFeedback(() -> Component.translatable("commands.give.success.single", count, itemStack.toHoverableText(), targets.size()), true);
        }
        return targets.size();
    }
}
*/
