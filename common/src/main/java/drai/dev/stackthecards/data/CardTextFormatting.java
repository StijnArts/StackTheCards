package drai.dev.stackthecards.data;

import com.google.gson.stream.*;
import net.minecraft.*;
import net.minecraft.network.*;
import net.minecraft.network.chat.*;
import net.minecraft.network.codec.*;
import com.google.gson.*;

public class CardTextFormatting {
    public static final String Json_FORMAT_ID_KEY = "formatId";
    public static final String Json_IS_BOLD_KEY = "bold";
    public static final String Json_IS_ITALIC_KEY = "italic";
    public static final String Json_COLOR_KEY = "argbColorHex";
    public String formatId = "";
    public boolean isItalic = false;
    public int argbColorValue = ChatFormatting.WHITE.getColor();
    public boolean isBold = false;

    public static final StreamCodec<FriendlyByteBuf, CardTextFormatting> SYNC_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, CardTextFormatting::getFormatId,
            ByteBufCodecs.BOOL, CardTextFormatting::isItalic,
            ByteBufCodecs.INT, CardTextFormatting::getArgbColorValue,
            ByteBufCodecs.BOOL, CardTextFormatting::isBold,
            CardTextFormatting::new);

    public CardTextFormatting(String formatId, boolean isItalic, int argbColorValue, boolean isBold) {
        this.formatId = formatId;
        this.isItalic = isItalic;
        this.argbColorValue = argbColorValue;
        this.isBold = isBold;
    }

    public Style getStyle(){
        return Style.EMPTY.withItalic(isItalic).withBold(isBold).withColor(argbColorValue);
    }

    public CardTextFormatting(String formatId) {
        this.formatId = formatId;
    }

    public CardTextFormatting() {
    }

    public static CardTextFormatting parse(JsonObject json) throws MalformedJsonException{
        if(json.isEmpty() || !json.has(Json_FORMAT_ID_KEY)) throw new MalformedJsonException("Formatting is missing formatting Id");
        CardTextFormatting format;
        try{
            format = new CardTextFormatting(json.get(Json_FORMAT_ID_KEY).getAsString());
        } catch (Exception e){
            throw new MalformedJsonException("Component format id was malformed: "+e.getMessage());
        }
        if(json.has(Json_IS_BOLD_KEY)){
            try{
                format.isBold = (Boolean) json.get(Json_IS_BOLD_KEY).getAsBoolean();
            } catch (Exception e){
                throw new MalformedJsonException("Component format isBold was malformed: "+e.getMessage());
            }
        }
        if(json.has(Json_IS_ITALIC_KEY)){
            try{
                format.isItalic = (Boolean) json.get(Json_IS_ITALIC_KEY).getAsBoolean();
            } catch (Exception e){
                throw new MalformedJsonException("Component format isItalic was malformed: "+e.getMessage());
            }
        }
        if(json.has(Json_COLOR_KEY)){
            try{
                format.argbColorValue =  Integer.parseUnsignedInt(json.get(Json_COLOR_KEY).getAsString(), 16);
            } catch (Exception e){
                throw new MalformedJsonException("Component format color value was malformed:" + e.toString());
            }
        }
        return format;
    }

    public String getFormatId() {
        return formatId;
    }

    public boolean isItalic() {
        return isItalic;
    }

    public int getArgbColorValue() {
        return argbColorValue;
    }

    public boolean isBold() {
        return isBold;
    }
}
