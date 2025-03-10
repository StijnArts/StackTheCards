package drai.dev.stackthecards.tooltips.parts;

import com.google.gson.stream.*;
import drai.dev.stackthecards.data.*;
import net.minecraft.network.*;
import net.minecraft.network.chat.*;
import net.minecraft.network.codec.*;
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

    public static final StreamCodec<FriendlyByteBuf, CardTooltipLine> SYNC_CODEC = new StreamCodec<FriendlyByteBuf, CardTooltipLine>() {

        @Override
        public void encode(FriendlyByteBuf buffer, CardTooltipLine value) {
            // Encode the card text formatting
            CardTextFormatting.SYNC_CODEC.encode(buffer, value.cardTextFormatting);

            // Encode the text
            ByteBufCodecs.STRING_UTF8.encode(buffer, value.text);

            // Encode the line segments recursively
            // Start by writing the size of the lineSegments list
            buffer.writeInt(value.lineSegments.size());

            // Then encode each line segment
            for (CardTooltipLine lineSegment : value.lineSegments) {
                encode(buffer, lineSegment);  // Recursive call for lineSegments
            }
        }

        @Override
        public CardTooltipLine decode(FriendlyByteBuf buffer) {
            // Decode the card text formatting
            CardTextFormatting cardTextFormatting = CardTextFormatting.SYNC_CODEC.decode(buffer);

            // Decode the text
            String text = ByteBufCodecs.STRING_UTF8.decode(buffer);

            // Decode the line segments recursively
            // First, read the size of the list
            int lineSegmentCount = buffer.readInt();
            List<CardTooltipLine> lineSegments = new ArrayList<>();

            // Then decode each line segment
            for (int i = 0; i < lineSegmentCount; i++) {
                lineSegments.add(decode(buffer));  // Recursive call for lineSegments
            }

            // Return a new CardTooltipLine object
            return new CardTooltipLine(cardTextFormatting, text, lineSegments);
        }
    };


    public CardTooltipLine(CardTextFormatting cardTextFormatting, String text, List<CardTooltipLine> lineSegments) {
        this.cardTextFormatting = cardTextFormatting;
        this.text = text;
        this.lineSegments = lineSegments;
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
                throw new MalformedJsonException("Component formatting id was malformed: "+e.getMessage());
            }
        } else {
            part.cardTextFormatting = new CardTextFormatting();
        }
        if(json.containsKey(JSON_IS_BOLD_KEY)){
            try{
                part.cardTextFormatting.isBold = (Boolean) json.get(JSON_IS_BOLD_KEY);
            } catch (Exception e){
                throw new MalformedJsonException("Component format isBold was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_IS_ITALIC_KEY)){
            try{
                part.cardTextFormatting.isItalic = (Boolean) json.get(JSON_IS_ITALIC_KEY);
            } catch (Exception e){
                throw new MalformedJsonException("Component format isItalic was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_COLOR_KEY)){
            try{
                part.cardTextFormatting.argbColorValue = (Integer) json.get(JSON_COLOR_KEY);
            } catch (Exception e){
                throw new MalformedJsonException("Component format isItalic was malformed: "+e.getMessage());
            }
        }

        return part;
    }

    public Component getTextComponent(){
        if(lineSegments.size()==0){
            return Component.literal(text).setStyle(cardTextFormatting.getStyle());
        } else {
            MutableComponent mutableText = Component.literal("");
            for (var lineSegment : lineSegments) {
                mutableText.append(lineSegment.getTextComponent());
            }
            return mutableText;
        }
    }

    public CardTextFormatting getCardTextFormatting() {
        return cardTextFormatting;
    }

    public String getText() {
        return text;
    }

    public List<CardTooltipLine> getLineSegments() {
        return lineSegments;
    }
}
