package drai.dev.stackthecards.commands;

import com.mojang.brigadier.*;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.*;
import com.mojang.brigadier.exceptions.*;
import net.minecraft.command.*;
import net.minecraft.command.argument.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import net.minecraft.server.command.*;
import net.minecraft.server.network.*;
import net.minecraft.sound.*;
import net.minecraft.text.*;

import java.util.*;

public class GiveCardCommand {
    public static final int MAX_STACKS = 100;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("giveCard").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.argument("targets", EntityArgumentType.players())
                .then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("item", ItemStackArgumentType.itemStack(commandRegistryAccess))
                        .executes(context -> GiveCardCommand.execute((ServerCommandSource)context.getSource(), ItemStackArgumentType.getItemStackArgument(context, "item"), EntityArgumentType.getPlayers(context, "targets"), 1)))
                        .then(CommandManager.argument("count", IntegerArgumentType.integer(1))
                        .executes(context -> GiveCardCommand.execute((ServerCommandSource)context.getSource(), ItemStackArgumentType.getItemStackArgument(context, "item"), EntityArgumentType.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "count")))))));
    }

    private static int execute(ServerCommandSource source, ItemStackArgument item, Collection<ServerPlayerEntity> targets, int count) throws CommandSyntaxException {
        int i = item.getItem().getMaxCount();
        int j = i * 100;
        ItemStack itemStack = item.createStack(count, false);
        if (count > j) {
            source.sendError(Text.translatable("commands.give.failed.toomanyitems", j, itemStack.toHoverableText()));
            return 0;
        }
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            int k = count;
            while (k > 0) {
                ItemEntity itemEntity;
                int l = Math.min(i, k);
                k -= l;
                ItemStack itemStack2 = item.createStack(l, false);
                boolean bl = serverPlayerEntity.getInventory().insertStack(itemStack2);
                if (!bl || !itemStack2.isEmpty()) {
                    itemEntity = serverPlayerEntity.dropItem(itemStack2, false);
                    if (itemEntity == null) continue;
                    itemEntity.resetPickupDelay();
                    itemEntity.setOwner(serverPlayerEntity.getUuid());
                    continue;
                }
                itemStack2.setCount(1);
                itemEntity = serverPlayerEntity.dropItem(itemStack2, false);
                if (itemEntity != null) {
                    itemEntity.setDespawnImmediately();
                }
                serverPlayerEntity.getWorld().playSound(null, serverPlayerEntity.getX(), serverPlayerEntity.getY(), serverPlayerEntity.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, ((serverPlayerEntity.getRandom().nextFloat() - serverPlayerEntity.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f);
                serverPlayerEntity.currentScreenHandler.sendContentUpdates();
            }
        }
        if (targets.size() == 1) {
            source.sendFeedback(() -> Text.translatable("commands.give.success.single", count, itemStack.toHoverableText(), ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()), true);
        } else {
            source.sendFeedback(() -> Text.translatable("commands.give.success.single", count, itemStack.toHoverableText(), targets.size()), true);
        }
        return targets.size();
    }
}
