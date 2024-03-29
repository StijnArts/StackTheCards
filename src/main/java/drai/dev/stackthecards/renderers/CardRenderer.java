package drai.dev.stackthecards.renderers;

import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.data.*;
import net.fabricmc.api.*;
import net.minecraft.client.*;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.*;
import net.minecraft.client.util.math.*;
import net.minecraft.util.*;
import org.joml.*;

import java.lang.Math;
import java.util.*;
@Environment(EnvType.CLIENT)
public class CardRenderer {
    TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
    private final HashMap<CardData, CardTexture> cardTextures = new HashMap<>();
    public void draw(MatrixStack matrices, VertexConsumerProvider vertexConsumers, CardData cardData, int light){
        if(textureManager == null) textureManager = MinecraftClient.getInstance().getTextureManager();
        CardTexture.draw(matrices, vertexConsumers, light, this.getCardTexture(cardData).renderLayer);
    }

    public void draw(MatrixStack matrices, VertexConsumerProvider vertexConsumers, CardData cardData,int light, double scale){
        if(textureManager == null) textureManager = MinecraftClient.getInstance().getTextureManager();
        CardTexture.draw(matrices, vertexConsumers, light, this.getCardTexture(cardData).renderLayer, scale);
    }

    private CardTexture getCardTexture(CardData cardData) {
        return this.cardTextures.compute(cardData, ((cardData1, texture) -> Objects.requireNonNullElseGet(texture, () -> new CardTexture(cardData1))));
    }

    public void clearStateTextures() {
        for (CardTexture cardTexture :
                cardTextures.values()) {
            cardTexture.close();
        }

        cardTextures.clear();
    }

    @Environment(EnvType.CLIENT)
    private class CardTexture {
        private final CardData cardData;
        private final NativeImageBackedTexture texture;
        private final RenderLayer renderLayer;

        public CardTexture(CardData cardData) {
            this.cardData = new CardData();
            this.texture = createCardTexture(cardData);
            Identifier identifier = CardRenderer.this.textureManager.registerDynamicTexture("stc_card/"+this.cardData.getCardId(), this.texture);
            this.renderLayer = RenderLayer.getText(identifier);
            this.texture.upload();
        }

        private static NativeImageBackedTexture createCardTexture(CardData cardData) {
            var foundTexture = cardData.getCardImage();
            NativeImageBackedTexture nativeImageBackedTexture;/* = new NativeImageBackedTexture(1024, 1024, true);*/
            if(foundTexture == null){
                nativeImageBackedTexture = new NativeImageBackedTexture(16,16, true);
            } else {
                int sizeToMatch = cardData.getMaxSide();
                nativeImageBackedTexture = new NativeImageBackedTexture(sizeToMatch, sizeToMatch, true);
                int yOffset = (int) cardData.getYOffset();
                int xOffset = (int) cardData.getXOffset();
                for (int i = 0; i < foundTexture.getHeight(); i++) {
                    for (int j = 0; j < foundTexture.getWidth(); j++) {
                        int y = i + yOffset;
                        int x = j + xOffset;
                        try {
                            Objects.requireNonNull(nativeImageBackedTexture.getImage()).setColor(x, y, foundTexture.getColor(j, i));
                        } catch (Exception e){
                            System.out.println("Tried to apply image texture but was out of bounds");
                        }
                    }
                }
            }
            return nativeImageBackedTexture;
        }

        public void close() {
            texture.close();
        }
        public static void draw(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, RenderLayer renderLayer) {
            draw(matrices, vertexConsumers, light, renderLayer, 1);
        }
        public static void draw(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, RenderLayer renderLayer, double scale) {
            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
            vertexConsumer.vertex(matrix4f, 0.0F, (float) (128.0F * scale), -0.01F).color(255, 255, 255, 255).texture(0.0F, 1.0F).light(light).next();
            vertexConsumer.vertex(matrix4f, (float) (128.0F * scale), (float) (128.0F * scale), -0.01F).color(255, 255, 255, 255).texture(1.0F, 1.0F).light(light).next();
            vertexConsumer.vertex(matrix4f, (float) (128.0F * scale), 0.0F, -0.01F).color(255, 255, 255, 255).texture(1.0F, 0.0F).light(light).next();
            vertexConsumer.vertex(matrix4f, 0.0F, 0.0F, -0.01F).color(255, 255, 255, 255).texture(0.0F, 0.0F).light(light).next();
        }
    }
}
