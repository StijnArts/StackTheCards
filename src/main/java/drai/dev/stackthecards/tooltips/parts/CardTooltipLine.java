package drai.dev.stackthecards.tooltips.parts;

import com.google.gson.stream.*;
import drai.dev.stackthecards.data.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.json.simple.*;

import java.util.*;

import static drai.dev.stackthecards.data.CardTextFormatting.*;

public class CardTooltipLine {
    private static final String JSON_TEXT_KEY = "text";
    private static final String JSON_FORMATTING_KEY = "formatting";
    public CardTextFormatting cardTextFormatting;
    public String text = "";
    public List<CardTooltipLine> lineSegments = new ArrayList<>();

    public CardTooltipLine(String text) {
        this.text = text;
    }

    public CardTooltipLine() {
    }

    public static CardTooltipLine parse(JSONObject json, CardGame game) throws MalformedJsonException{
        if(json.isEmpty() || !json.containsKey(JSON_TEXT_KEY)) throw new MalformedJsonException("Card Game Json was empty");
        var part = new CardTooltipLine();
        var textContents = json.get(JSON_TEXT_KEY);
        if(textContents instanceof String contentsAsString){
            part.text = contentsAsString;
        } else if(textContents instanceof JSONArray contentsAsJsonArray){
            for (var textSegment: contentsAsJsonArray) {
                part.lineSegments.add(CardTooltipLine.parse((JSONObject) textSegment, game));
            }
        }
        if(json.containsKey(JSON_FORMATTING_KEY)){
            try{
                String formattingId = (String) json.get(JSON_FORMATTING_KEY);
                if(game.formatting.containsKey(formattingId)){
                    part.cardTextFormatting = game.formatting.get(formattingId);
                } else {
                    part.cardTextFormatting = new CardTextFormatting();
                }
            } catch (Exception e){
                throw new MalformedJsonException("Text formatting id was malformed");
            }
        } else {
            part.cardTextFormatting = new CardTextFormatting();
        }
        if(json.containsKey(JSON_IS_BOLD_KEY)){
            try{
                part.cardTextFormatting.isBold = (Boolean) json.get(JSON_IS_BOLD_KEY);
            } catch (Exception e){
                throw new MalformedJsonException("Text format isBold was malformed");
            }
        }
        if(json.containsKey(JSON_IS_ITALIC_KEY)){
            try{
                part.cardTextFormatting.isItalic = (Boolean) json.get(JSON_IS_ITALIC_KEY);
            } catch (Exception e){
                throw new MalformedJsonException("Text format isItalic was malformed");
            }
        }
        if(json.containsKey(JSON_COLOR_KEY)){
            try{
                part.cardTextFormatting.argbColorValue = (Integer) json.get(JSON_COLOR_KEY);
            } catch (Exception e){
                throw new MalformedJsonException("Text format isItalic was malformed");
            }
        }

        return part;
    }

    public Text getTextComponent(){
        MutableText label = Text.literal("Pokemon Power: Energy Burn");
        label.fillStyle(Style.EMPTY.withColor(Formatting.DARK_PURPLE).withBold(true));
        MutableText abilityLore = Text.literal("As often as you like during your turn (before your attack), you may turn all Energy attached to Charizard into Fire Energy for the rest of the turn. This power can’t be used if Charizard is Asleep, Confused, or Paralyzed.");
        abilityLore.setStyle(Style.EMPTY.withColor(Formatting.GRAY));

        MutableText attack = Text
                .literal("Fire Spin").fillStyle(Style.EMPTY.withColor(Formatting.WHITE))
                .append(Text.literal(" - ").fillStyle(Style.EMPTY.withColor(Formatting.GRAY)))
                .append(Text.literal("100").fillStyle(Style.EMPTY.withColor(Formatting.WHITE).withBold(true)));
        attack.fillStyle(Style.EMPTY.withColor(Formatting.WHITE));
        MutableText attackLore = Text.literal("Discard 2 Energy cards attached to Charizard in order to use this attack.");
        attackLore.setStyle(Style.EMPTY.withColor(Formatting.GRAY).withItalic(true));
        MutableText cost = Text.literal("Cost: ");
        cost.fillStyle(Style.EMPTY.withColor(Formatting.WHITE));
        cost.append(Text.literal("Fire ").fillStyle(Style.EMPTY.withColor(Formatting.RED)));
        cost.append(Text.literal("x 4").fillStyle(Style.EMPTY.withColor(Formatting.GRAY)));

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

        MutableText pokedex = Text.literal("Spits fire that is hot enough to melt boulders. Known to unintentionally cause forest fires.").fillStyle(Style.EMPTY.withItalic(true).withColor(Formatting.GRAY));
        MutableText stage = Text.literal("Stage 2 - Evolves from Charmeleon").fillStyle(Style.EMPTY.withColor(Formatting.GRAY));
        MutableText rarity = Text.literal("Rare Holo").fillStyle(Style.EMPTY.withColor(Formatting.GRAY).withBold(true));
        MutableText set = Text.literal("Base Set - ").fillStyle(Style.EMPTY.withItalic(true).withColor(Formatting.WHITE))
                .append(Text.literal("4 / 102").fillStyle(Style.EMPTY.withItalic(true).withColor(Formatting.GRAY)));

        if(lineSegments.size()==0){
            return Text.literal(text).fillStyle(cardTextFormatting.getStyle());
        } else {
            MutableText mutableText = Text.literal("");
            for (var lineSegment : lineSegments) {
                mutableText.append(lineSegment.getTextComponent());
            }
            return mutableText;
        }
    }
}
