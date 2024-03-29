package drai.dev.stackthecards.data;

import drai.dev.stackthecards.client.*;
import net.minecraft.client.texture.*;
import net.minecraft.text.*;
import net.minecraft.util.*;

import java.util.*;

public class CardData {
    private CardSet cardSet;
    private String cardId;
//    private NativeImage cardImage = ;

    public CardSet getCardSet() {
        return cardSet;
    }
    public NativeImage getCardImage(){
        return StackTheCardsClient.TEST;
    }
    public String getCardId() {
//        return cardSet.getSetIdentifier() + "_" + cardId; //TODO
        return "stc_card";
    }

    public Identifier getIdentifier() {
        return new Identifier("stack_the_cards",getCardId().replaceAll("_", "/"));
    }

    public String getCardName() {
        return "Charizard";
    }

    public int getMaxSide() {
        return Math.max(getCardImage().getHeight(), getWidth());
    }

    public int getWidth() {
        return getCardImage().getWidth();
    }

    public double getYOffset() {
        return (getMaxSide()-(double) getHeight())/2;
    }

    public double getXOffset() {
        return (getMaxSide()-(double) getWidth())/2;
    }

    public int getHeight() {
        return getCardImage().getHeight();
    }

    public static Text NEW_LINE = Text.literal(" ");
    public static Text TAB = Text.literal("      ");
    public Collection<? extends Text> getLoreToolTips() {
        var tooltips = new ArrayList<Text>();
        MutableText label = Text.literal("Pokemon Power: Energy Burn");
        label.fillStyle(Style.EMPTY.withColor(Formatting.DARK_PURPLE).withBold(true));
        CardMutableText abilityLore = new CardMutableText(Text.literal("As often as you like during your turn (before your attack), you may turn all Energy attached to Charizard into Fire Energy for the rest of the turn. This power can’t be used if Charizard is Asleep, Confused, or Paralyzed."));
        abilityLore.setStyle(Style.EMPTY.withColor(Formatting.GRAY));
        tooltips.add(label);
        tooltips.add(abilityLore);
        tooltips.add(NEW_LINE);

        MutableText attack = Text
                .literal("Fire Spin").fillStyle(Style.EMPTY.withColor(Formatting.WHITE))
                .append(Text.literal(" - ").fillStyle(Style.EMPTY.withColor(Formatting.GRAY)))
                .append(Text.literal("100").fillStyle(Style.EMPTY.withColor(Formatting.WHITE).withBold(true)));
        attack.fillStyle(Style.EMPTY.withColor(Formatting.WHITE));
        CardMutableText attackLore = new CardMutableText(Text.literal("Discard 2 Energy cards attached to Charizard in order to use this attack."));
        attackLore.setStyle(Style.EMPTY.withColor(Formatting.GRAY).withItalic(true));
        MutableText cost = Text.literal("Cost: ");
        cost.fillStyle(Style.EMPTY.withColor(Formatting.WHITE));
        cost.append(Text.literal("Fire ").fillStyle(Style.EMPTY.withColor(Formatting.RED)));
        cost.append(Text.literal("x 4").fillStyle(Style.EMPTY.withColor(Formatting.GRAY)));
        tooltips.add(attack);
        tooltips.add(attackLore);
        tooltips.add(cost);
        tooltips.add(NEW_LINE);
        MutableText weakness = Text
                .literal("Weakness: ").fillStyle(Style.EMPTY.withColor(Formatting.RED).withBold(true))
                .append(Text.literal("Water").fillStyle(Style.EMPTY.withColor(Formatting.BLUE).withBold(false)));
        MutableText resistance = Text
                .literal("Resistance: ").fillStyle(Style.EMPTY.withColor(Formatting.GREEN).withBold(true))
                .append(Text.literal("Fighting ").fillStyle(Style.EMPTY.withColor(Formatting.BLUE).withBold(false)))
                .append(Text.literal("-30").fillStyle(Style.EMPTY.withColor(Formatting.GRAY).withBold(false)));
        MutableText retreatCost = Text
                .literal("Retreat Cost: ").fillStyle(Style.EMPTY.withColor(Formatting.BLUE).withBold(true))
                .append(Text.literal("Energy ").fillStyle(Style.EMPTY.withColor(Formatting.BLUE).withBold(false)))
                .append(Text.literal("x 3").fillStyle(Style.EMPTY.withColor(Formatting.GRAY).withBold(false)));
        CardMutableText pokedex = new CardMutableText(Text.literal("Spits fire that is hot enough to melt boulders. Known to unintentionally cause forest fires.").fillStyle(Style.EMPTY.withItalic(true).withColor(Formatting.GRAY)));
        tooltips.add(weakness);
        tooltips.add(resistance);
        tooltips.add(retreatCost);
        tooltips.add(pokedex);
        return tooltips;
    }

    public Text getCardNameLabel() {
        if(!StackTheCardsClient.cardLoreKeyPressed){
            return Text.literal(getCardName()).fillStyle(Style.EMPTY.withColor(Formatting.WHITE));
        } else {
            return Text.literal(getCardName()).fillStyle(Style.EMPTY.withColor(Formatting.WHITE).withBold(true))
                    .append(Text.literal(" - ").fillStyle(Style.EMPTY.withColor(Formatting.GRAY)))
                    .append(Text.literal("120 HP").fillStyle(Style.EMPTY.withColor(Formatting.WHITE).withBold(StackTheCardsClient.cardLoreKeyPressed)));
        }
    }

    public List<Text> getTooltipsDescriptors(){
        var tooltips = new ArrayList<Text>();
        MutableText stage = Text.literal("Stage 2 - Evolves from Charmeleon").fillStyle(Style.EMPTY.withColor(Formatting.GRAY));
        MutableText rarity = Text.literal("Rare Holo").fillStyle(Style.EMPTY.withColor(Formatting.GRAY).withBold(true));
        MutableText set = Text.literal("Base Set - ").fillStyle(Style.EMPTY.withItalic(true).withColor(Formatting.WHITE))
                .append(Text.literal("4 / 102").fillStyle(Style.EMPTY.withItalic(true).withColor(Formatting.GRAY)));

        tooltips.add(rarity);
        tooltips.add(stage);
        tooltips.add(set);
        return tooltips;
    }
}
