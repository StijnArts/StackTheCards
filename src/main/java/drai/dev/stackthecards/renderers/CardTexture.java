package drai.dev.stackthecards.renderers;

import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.data.*;
import net.minecraft.client.*;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.*;
import net.minecraft.client.util.math.*;
import net.minecraft.resource.*;
import net.minecraft.util.*;
import org.joml.*;

import java.io.*;
import java.lang.*;
import java.lang.Math;
import java.util.*;

public class CardTexture {
    private final CardData cardData;
    private final NativeImageBackedTexture texture;
    private final RenderLayer renderLayer;
    private int originalImageWidth = 0;
    private int originalImageHeight = 0;

    public CardTexture(CardData cardData) {
        //TODO add option to shift cards into a direction for cards that connect with each other
        this.cardData = cardData;
        this.texture = createCardTexture(cardData, this);
        Identifier identifier = MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("stc_card/"+this.cardData.getCardId(), this.texture);
        this.renderLayer = RenderLayer.getText(identifier);
        this.texture.upload();
    }

    private static NativeImageBackedTexture createCardTexture(CardData cardData, CardTexture texture) {
        var foundTexture = getCardTextureFromData(cardData);
        NativeImageBackedTexture nativeImageBackedTexture;/* = new NativeImageBackedTexture(1024, 1024, true);*/
        if(foundTexture == null){
            nativeImageBackedTexture = new NativeImageBackedTexture(16,16, true);
        } else {
            texture.originalImageWidth = foundTexture.getWidth();
            texture.originalImageHeight = foundTexture.getHeight();
            int sizeToMatch = Math.max(foundTexture.getWidth(), foundTexture.getHeight());
            nativeImageBackedTexture = new NativeImageBackedTexture(sizeToMatch, sizeToMatch, true);
            int yOffset = (sizeToMatch- foundTexture.getHeight())/2;
            int xOffset = (sizeToMatch- foundTexture.getWidth())/2;
            for (int i = 0; i < foundTexture.getHeight(); i++) {
                for (int j = 0; j < foundTexture.getWidth(); j++) {

                    int y = i + yOffset;
                    int x = j + xOffset;
                    try {
                        Objects.requireNonNull(nativeImageBackedTexture.getImage()).setColor(x, y, foundTexture.getColor(j, i));
                    } catch (Exception e){
                        System.out.println("Tried to apply color out of image bounds");
                    }
                }
            }
            if(cardData.hasRoundedCorners()) {
                roundCorners(foundTexture, nativeImageBackedTexture, xOffset, yOffset);
            }
        }
        return nativeImageBackedTexture;
    }

    public static void draw(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, RenderLayer renderLayer, int layersHigh, int amountOfCardsAttached, double scale, CardGame game) {
        var cardDistance = game.cardStackingDistance;
        var y = 0F;
        var x = 0F;
        if(layersHigh != 0){
            x = cardDistance * game.cardStackingDirection.xMod;
            y = cardDistance * game.cardStackingDirection.yMod;
        }

        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        matrix4f.translate(x, y, amountOfCardsAttached*0.1F);
//        matrix4f Maybe rotate a little if cards attached
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
        vertexConsumer.vertex(matrix4f, 0.0F, (float) (128.0F * scale), -0.01F).color(255, 255, 255, 255).texture(0.0F, 1.0F).light(light).next();
        vertexConsumer.vertex(matrix4f, (float) (128.0F * scale), (float) (128.0F * scale), -0.01F).color(255, 255, 255, 255).texture(1.0F, 1.0F).light(light).next();
        vertexConsumer.vertex(matrix4f, (float) (128.0F * scale), 0.0F, -0.01F).color(255, 255, 255, 255).texture(1.0F, 0.0F).light(light).next();
        vertexConsumer.vertex(matrix4f, 0.0F, 0.0F, -0.01F).color(255, 255, 255, 255).texture(0.0F, 0.0F).light(light).next();
    }

    private static void roundCorners(NativeImage foundTexture, NativeImageBackedTexture nativeImageBackedTexture, int xOffset, int yOffset) {
        int radius = 40; // adjust the radius as needed for the rounded corners
        int xSize = foundTexture.getWidth();
        int ySize = foundTexture.getHeight();

        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                /*double distance = Math.sqrt(Math.pow(x - xSize / 2.0, 2) + Math.pow(y - ySize / 2.0, 2));

                // If the distance is greater than the radius, set the pixel to transparent
                if (distance < radius) {
                    nativeImageBackedTexture.getImage().setColor(x+xOffset, y+yOffset, 0); // Set pixel to transparent
                }*/
                if(y < ySize/2 && x < xSize/2){
                    //correctly placed
                    //top left corner
                    double distance = Math.sqrt(Math.pow(x - radius, 2) + Math.pow(y - radius, 2));
                    if (distance > radius && (x < radius && y < radius)) {
                        nativeImageBackedTexture.getImage().setColor(x+xOffset, y+yOffset, 0); // Set pixel to transparent
                    }
                } else {
                    double distanceFromBottom = Math.pow(y - (ySize - radius), 2);
                    if(y > ySize/2 && x < xSize/2){
                        //bottom left corner
                        double distance = Math.sqrt(Math.pow(x - radius, 2) + distanceFromBottom);
                        if (distance > radius && (x < radius && y > ySize-radius)) {
                            nativeImageBackedTexture.getImage().setColor(x+xOffset, y+yOffset, 0); // Set pixel to transparent
                        }
                    } else {
                        double distanceFromRightSide = Math.pow(x - (xSize - radius), 2);
                        if(y < ySize/2 && x > xSize/2){
                            //top right corner
                            double distance = Math.sqrt(distanceFromRightSide + Math.pow(y - radius, 2));
                            if (distance > radius && (x > xSize-radius && y < radius)) {
                                nativeImageBackedTexture.getImage().setColor(x+xOffset, y+yOffset, 0); // Set pixel to transparent
                            }
                        } else if(y > ySize/2 && x > xSize/2){
                            //correctly placed
                            //bottom right corner
                            double distance = Math.sqrt(distanceFromRightSide + distanceFromBottom);
                            if (distance > radius && (x > xSize-radius && y > ySize-radius)) {
                                nativeImageBackedTexture.getImage().setColor(x+xOffset, y+yOffset, 0); // Set pixel to transparent
                            }
                        }
                    }
                }
            }
        }
    }

    private static NativeImage getCardTextureFromData(CardData cardData) {
        ResourceManager testResourceManager = MinecraftClient.getInstance().getResourceManager();

        Identifier textureId = new Identifier("stack_the_cards", "stc_cards/cards/"+cardData.getCardId()+".png");
        NativeImage textureImage = null;
        try {
            Optional<Resource> resource = testResourceManager.getResource(textureId);
            if(resource.isPresent()){
                InputStream inputStream = resource.get().getInputStream();
                textureImage = NativeImage.read(inputStream);
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return textureImage;
    }

    public void close() {
        texture.close();
    }
    public static void draw(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, RenderLayer renderLayer, int layersHigh, int amountOfCardsAttached, CardGame game) {
        draw(matrices, vertexConsumers, light, renderLayer, layersHigh,  amountOfCardsAttached,1, game);
    }


    public RenderLayer getRenderLayer() {
        return renderLayer;
    }

    public NativeImageBackedTexture getTexture() {
        return texture;
    }

    public int getOriginalImageWidth() {
        return originalImageWidth;
    }

    public int getOriginalImageHeight() {
        return originalImageHeight;
    }
}
