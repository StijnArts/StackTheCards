package drai.dev.stackthecards.registry;

import drai.dev.stackthecards.data.cardpacks.*;
import drai.dev.stackthecards.items.*;
import net.fabricmc.fabric.api.itemgroup.v1.*;
import net.minecraft.network.chat.*;
import net.minecraft.world.item.*;

import java.util.*;
import java.util.stream.*;

import static drai.dev.stackthecards.registry.Items.*;

public class ItemGroups {
    public static final CreativeModeTab CARD_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(CARD_BINDER))
            .title(Component.translatable("itemGroup.stack_the_cards.item_group"))
            .displayItems((context, entries) -> {
                entries.accept(CARD_BINDER);
                entries.accept(WHITE_CARD_BINDER);
                entries.accept(ORANGE_CARD_BINDER);
                entries.accept(MAGENTA_CARD_BINDER);
                entries.accept(LIGHT_BLUE_CARD_BINDER);
                entries.accept(YELLOW_CARD_BINDER);
                entries.accept(LIME_CARD_BINDER);
                entries.accept(PINK_CARD_BINDER);
                entries.accept(GRAY_CARD_BINDER);
                entries.accept(LIGHT_GRAY_CARD_BINDER);
                entries.accept(CYAN_CARD_BINDER);
                entries.accept(BLUE_CARD_BINDER);
                entries.accept(GREEN_CARD_BINDER);
                entries.accept(RED_CARD_BINDER);
                entries.accept(BLACK_CARD_BINDER);
                entries.accept(PURPLE_CARD_BINDER);
                CardGameRegistry.cardGames.values().forEach(game -> {
                    var cardPacks = game.cardSets.values().stream().map(set -> set.getCardPacks().values()).flatMap(Collection::stream)
                            .sorted(Comparator.comparingInt(CardPack::getOrdering)).toList();
                    for (var cardPack :
                            cardPacks) {
                        entries.accept(CardPackItem.of(cardPack));
                    }
                });
            })
            .build();
}
