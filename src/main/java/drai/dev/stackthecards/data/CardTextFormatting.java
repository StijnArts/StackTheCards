package drai.dev.stackthecards.data;

import com.google.gson.stream.*;
import net.minecraft.*;
import net.minecraft.network.chat.*;
import org.json.simple.*;

public class CardTextFormatting {
    public static final String JSON_FORMAT_ID_KEY = "formatId";
    public static final String JSON_IS_BOLD_KEY = "bold";
    public static final String JSON_IS_ITALIC_KEY = "italic";
    public static final String JSON_COLOR_KEY = "argbColorHex";
    public String formatId = "";
    public boolean isItalic = false;
    public int argbColorValue = ChatFormatting.WHITE.getColor();
    public boolean isBold = false;

    public Style getStyle(){
        return Style.EMPTY.withItalic(isItalic).withBold(isBold).withColor(argbColorValue);
    }

    public CardTextFormatting(String formatId) {
        this.formatId = formatId;
    }

    public CardTextFormatting() {
    }

    public static CardTextFormatting parse(JSONObject json) throws MalformedJsonException{
        if(json.isEmpty() || !json.containsKey(JSON_FORMAT_ID_KEY)) throw new MalformedJsonException("Card Game Json was empty");
        CardTextFormatting format;
        try{
            format = new CardTextFormatting((String) json.get(JSON_FORMAT_ID_KEY));
        } catch (Exception e){
            throw new MalformedJsonException("Component format id was malformed: "+e.getMessage());
        }
        if(json.containsKey(JSON_IS_BOLD_KEY)){
            try{
                format.isBold = (Boolean) json.get(JSON_IS_BOLD_KEY);
            } catch (Exception e){
                throw new MalformedJsonException("Component format isBold was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_IS_ITALIC_KEY)){
            try{
                format.isItalic = (Boolean) json.get(JSON_IS_ITALIC_KEY);
            } catch (Exception e){
                throw new MalformedJsonException("Component format isItalic was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_COLOR_KEY)){
            try{
                format.argbColorValue =  Integer.parseUnsignedInt((String) json.get(JSON_COLOR_KEY), 16);
            } catch (Exception e){
                throw new MalformedJsonException("Component format color value was malformed:" + e.toString());
            }
        }
        return format;
    }
}
