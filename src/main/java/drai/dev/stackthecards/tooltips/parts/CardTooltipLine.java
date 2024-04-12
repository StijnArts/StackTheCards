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
    public CardTextFormatting cardTextFormatting = new CardTextFormatting();
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
                    System.out.println("Format: "+formattingId+" wasn't found");
                    part.cardTextFormatting = new CardTextFormatting();
                }
            } catch (Exception e){
                throw new MalformedJsonException("Text formatting id was malformed: "+e.getMessage());
            }
        } else {
            part.cardTextFormatting = new CardTextFormatting();
        }
        if(json.containsKey(JSON_IS_BOLD_KEY)){
            try{
                part.cardTextFormatting.isBold = (Boolean) json.get(JSON_IS_BOLD_KEY);
            } catch (Exception e){
                throw new MalformedJsonException("Text format isBold was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_IS_ITALIC_KEY)){
            try{
                part.cardTextFormatting.isItalic = (Boolean) json.get(JSON_IS_ITALIC_KEY);
            } catch (Exception e){
                throw new MalformedJsonException("Text format isItalic was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_COLOR_KEY)){
            try{
                part.cardTextFormatting.argbColorValue = (Integer) json.get(JSON_COLOR_KEY);
            } catch (Exception e){
                throw new MalformedJsonException("Text format isItalic was malformed: "+e.getMessage());
            }
        }

        return part;
    }

    public Text getTextComponent(){
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
