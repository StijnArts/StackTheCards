package drai.dev.stackthecards.registry;

import dev.architectury.injectables.annotations.*;
import drai.dev.stackthecards.data.cardpacks.*;
import drai.dev.stackthecards.items.*;
import net.minecraft.network.chat.*;
import net.minecraft.world.item.*;

import java.util.*;

import static drai.dev.stackthecards.registry.StackTheCardsItems.*;
import static drai.dev.stackthecards.registry.StackTheCardsItems.PURPLE_CARD_BINDER;

public class ItemGroups {
    public static final CreativeModeTab CARD_ITEM_GROUP = cardCreativeModeTab(getBuilder());

    @ExpectPlatform
    public static CreativeModeTab.Builder getBuilder(){
        throw new AssertionError();
    }

    public static CreativeModeTab cardCreativeModeTab(CreativeModeTab.Builder builder){
        return builder.title(Component.translatable("itemGroup.stack_the_cards.item_group"))
                //Set the icon of the tab.
                .icon(() -> new ItemStack(CARD_BINDER))
                //Add your items to the tab.
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

    public static void touch() {

    }
}
