package drai.dev.stackthecards.registry;

import drai.dev.stackthecards.data.cardpacks.*;
import drai.dev.stackthecards.items.*;
import net.fabricmc.fabric.api.itemgroup.v1.*;
import net.minecraft.item.*;
import net.minecraft.text.*;

import java.util.*;
import java.util.stream.*;

import static drai.dev.stackthecards.registry.Items.*;

public class ItemGroups {
    public static final ItemGroup CARD_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(CARD_BINDER))
            .displayName(Text.translatable("itemGroup.stack_the_cards.item_group"))
            .entries((context, entries) -> {
                entries.add(CARD_BINDER);
                entries.add(WHITE_CARD_BINDER);
                entries.add(ORANGE_CARD_BINDER);
                entries.add(MAGENTA_CARD_BINDER);
                entries.add(LIGHT_BLUE_CARD_BINDER);
                entries.add(YELLOW_CARD_BINDER);
                entries.add(LIME_CARD_BINDER);
                entries.add(PINK_CARD_BINDER);
                entries.add(GRAY_CARD_BINDER);
                entries.add(LIGHT_GRAY_CARD_BINDER);
                entries.add(CYAN_CARD_BINDER);
                entries.add(BLUE_CARD_BINDER);
                entries.add(GREEN_CARD_BINDER);
                entries.add(RED_CARD_BINDER);
                entries.add(BLACK_CARD_BINDER);
                entries.add(PURPLE_CARD_BINDER);
                CardGameRegistry.cardGames.values().forEach(game -> {
                    var cardPacks = game.cardSets.values().stream().map(set -> set.getCardPacks().values()).flatMap(Collection::stream)
                            .sorted(Comparator.comparingInt(CardPack::getOrdering)).toList();
                    for (var cardPack :
                            cardPacks) {
                        entries.add(CardPackItem.of(cardPack));
                    }
                });
            })
            .build();
}
