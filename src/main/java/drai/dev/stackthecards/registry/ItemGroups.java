package drai.dev.stackthecards.registry;

import net.fabricmc.fabric.api.itemgroup.v1.*;
import net.minecraft.item.*;
import net.minecraft.text.*;

import static drai.dev.stackthecards.registry.Items.*;

public class ItemGroups {
    private static final ItemGroup CARD_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(CARD_BINDER))
            .displayName(Text.translatable("itemGroup.tutorial.test_group"))
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
            })
            .build();
}
