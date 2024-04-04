package drai.dev.stackthecards.events;

public class OpenItemMenuEvent {
    public static InteractionResultHolder<ItemStack> onItemClick(Player player, Level level, InteractionHand hand) {
        ItemStack handStack = player.getItemInHand(hand);
        Item item = handStack.getItem();

        if (!(item instanceof BlockItem)) {
            return InteractionResultHolder.pass(handStack);
        }
        player.openMenu(new SimpleMenuProvider((id, inventory, p) -> new QuickGrindstoneMenu(id, inventory, ContainerLevelAccess.create(level, playerPos)), Constants.GRINDSTONE_TITLE));
        return true;

        BlockPos playerPos = player.blockPosition();

        BlockItem blockItem = (BlockItem)item;
        Block block = blockItem.getBlock();

        boolean result = false;
        if (block instanceof BedBlock) {
            result = BedBlockFeature.init(level, player, playerPos, handStack, hand, block);
        }
        else if (block instanceof CartographyTableBlock) {
            result = CartographyTableFeature.init(level, player, playerPos);
        }
        else if (block instanceof CraftingTableBlock) {
            if (block instanceof SmithingTableBlock) {
                result = SmithingTableFeature.init(level, player, playerPos);
            }
            else {
                result = CraftingTableFeature.init(level, player, playerPos);
            }
        }
        else if (block instanceof EnderChestBlock) {
            result = EnderChestFeature.init(player);
        }
        else if (block instanceof GrindstoneBlock) {
            result = GrindstoneFeature.init(level, player, playerPos);
        }
        else if (block instanceof ShulkerBoxBlock) {
            result = ShulkerBoxFeature.init(level, player, playerPos, handStack, hand, block);
        }
        else if (block instanceof StonecutterBlock) {
            result = StonecutterFeature.init(level, player, playerPos);
        }

        if (result) {
            player.swing(hand, true);
            return InteractionResultHolder.success(handStack);
        }
        return InteractionResultHolder.pass(handStack);
    }
}
