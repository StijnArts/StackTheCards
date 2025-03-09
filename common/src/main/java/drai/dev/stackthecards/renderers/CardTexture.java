package drai.dev.stackthecards.renderers;

import com.mojang.blaze3d.platform.*;
import com.mojang.blaze3d.vertex.*;
import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.data.carddata.*;
import drai.dev.stackthecards.data.cardpacks.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.resources.*;
import net.minecraft.server.packs.resources.*;
import org.joml.*;

import java.io.*;
import java.lang.Math;
import java.util.*;

public class CardTexture {
    private CardData cardData;
    private final DynamicTexture texture;
    private final RenderType renderLayer;
    private int originalImageWidth = 0;
    private int originalImageHeight = 0;

    public CardTexture(CardData cardData) {
        this.cardData = cardData;
        var foundTexture = getCardTextureFromData(cardData);
        this.texture = createTexture(cardData.hasRoundedCorners(), foundTexture, this);
        ResourceLocation identifier = Minecraft.getInstance().getTextureManager().register("stc_card/"+cardData.getTextureId(), this.texture);
        this.renderLayer = RenderType.text(identifier);
        this.texture.upload();
    }

    public CardTexture(CardPack cardPack){
        var foundTexture = getPackTextureFromData(cardPack);
        this.texture = createTexture(false, foundTexture, this);
        ResourceLocation identifier = Minecraft.getInstance().getTextureManager().register("stc_card/"+cardPack.getTextureId(), this.texture);
        this.renderLayer = RenderType.text(identifier);
        this.texture.upload();
    }

    private static DynamicTexture createTexture(boolean hasRoundedCorners, NativeImage foundTexture, CardTexture texture) {
        DynamicTexture dynamicTexture;/* = new DynamicTexture(1024, 1024, true);*/
        if(foundTexture == null){
            dynamicTexture = new DynamicTexture(16,16, true);
        } else {
            texture.originalImageWidth = foundTexture.getWidth();
            texture.originalImageHeight = foundTexture.getHeight();
            int sizeToMatch = Math.max(foundTexture.getWidth(), foundTexture.getHeight());
            dynamicTexture = new DynamicTexture(sizeToMatch, sizeToMatch, true);
            int yOffset = (sizeToMatch- foundTexture.getHeight())/2;
            int xOffset = (sizeToMatch- foundTexture.getWidth())/2;
            for (int i = 0; i < foundTexture.getHeight(); i++) {
                for (int j = 0; j < foundTexture.getWidth(); j++) {

                    int y = i + yOffset;
                    int x = j + xOffset;
                    try {
                        Objects.requireNonNull(dynamicTexture.getPixels()).setPixelRGBA(x, y, foundTexture.getPixelRGBA(j, i));
                    } catch (Exception e){
                        System.out.println("Tried to apply setColor out of image bounds");
                    }
                }
            }
            if(hasRoundedCorners) {
                roundCorners(foundTexture, dynamicTexture, xOffset, yOffset);
            }
        }
        return dynamicTexture;
    }

    private static List<List<NativeImage>> transposeFoundTextures(List<List<NativeImage>> foundTextures) {
            // Determine the number of rows and columns
            int numRows = foundTextures.size();
            int numCols = foundTextures.isEmpty() ? 0 : foundTextures.get(0).size();

            // Create a new list to hold the transposed data
            List<List<NativeImage>> transposedList = new ArrayList<>(numCols);
            for (int col = 0; col < numCols; col++) {
                // Create a new column
                List<NativeImage> transposedColumn = new ArrayList<>(numRows);
                for (int row = 0; row < numRows; row++) {
                    // Swap rows with columns
                    transposedColumn.add(foundTextures.get(row).get(col));
                }
                // Add the transposed column to the transposed list
                transposedList.add(transposedColumn);
            }
            return transposedList;
    }

    public static void draw(PoseStack matrices, MultiBufferSource vertexConsumers, int light, RenderType renderLayer,
                            int layersHigh, int amountOfCardsAttached, double scale, CardGame game, double xOffset, double yOffset) {
        draw(matrices, vertexConsumers.getBuffer(renderLayer), light, layersHigh, amountOfCardsAttached, scale, game.cardStackingDirection, game.cardStackingDistance, xOffset, yOffset);
    }

    public static void draw(PoseStack matrices, VertexConsumer vertexConsumer, int light,
                            int layersHigh, int amountOfCardsAttached, double scale, CardStackingDirection cardStackingDirection, float cardStackingDistance, double xOffset, double yOffset) {
        var cardDistance = cardStackingDistance;
        var y = 0F;
        var x = 0F;
        if(layersHigh != 0){
            x = cardDistance * cardStackingDirection.xMod;
            y = cardDistance * cardStackingDirection.yMod;
        }
        Matrix4f matrix4f = matrices.last().pose();
        matrix4f.translate(x, y, amountOfCardsAttached*0.1F);
//        matrix4f Maybe rotate a little if cards attached

        //bottom left
        vertexConsumer.addVertex(matrix4f, (float) (0.0F + yOffset), (float) (128.0F * scale + xOffset), -0.01F).setColor(255, 255, 255, 255).setUv(0.0F, 1.0F).setLight(light);
        //bottom right
        vertexConsumer.addVertex(matrix4f, (float) (128.0F * scale+ yOffset), (float) (128.0F * scale+ xOffset), -0.01F).setColor(255, 255, 255, 255).setUv(1.0F, 1.0F).setLight(light);
        //top right
        vertexConsumer.addVertex(matrix4f, (float) (128.0F * scale+ yOffset), (float) (0.0F+ xOffset), -0.01F).setColor(255, 255, 255, 255).setUv(1.0F, 0.0F).setLight(light);
        //top left
        vertexConsumer.addVertex(matrix4f, (float) (0.0F+ yOffset), (float) (0.0F+ xOffset), -0.01F).setColor(255, 255, 255, 255).setUv(0.0F, 0.0F).setLight(light);
    }

    public static void drawConnectedCard(PoseStack matrices, MultiBufferSource vertexConsumers, int light,
                                         RenderType renderLayer, int layersHigh, int amountOfCardsAttached, CardGame game, double xOffset, double yOffset, CardRotation rotation) {
        var cardDistance = game.cardStackingDistance;
        var y = 0F;
        var x = 0F;
        if(layersHigh != 0){
            x = cardDistance * game.cardStackingDirection.xMod;
            y = cardDistance * game.cardStackingDirection.yMod;
        }
        Matrix4f matrix4f = matrices.last().pose();
        matrix4f.translate(x, y, amountOfCardsAttached*0.1F);
//        matrix4f Maybe rotate a little if cards attached
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);

        //bottom left
        vertexConsumer.addVertex(matrix4f, (float) (rotation.bottomLeft.getFirst() + xOffset),
                (float) (rotation.bottomLeft.getSecond() + yOffset), -0.01F).setColor(255, 255, 255, 255).setUv(0.0F, 1.0F).setLight(light);
        //bottom right
        vertexConsumer.addVertex(matrix4f, (float) (rotation.bottomRight.getFirst() + xOffset),
                (float) (rotation.bottomRight.getSecond() + yOffset), -0.01F).setColor(255, 255, 255, 255).setUv(1.0F, 1.0F).setLight(light);
        //top right
        vertexConsumer.addVertex(matrix4f, (float) (rotation.topRight.getFirst() + xOffset),
                (float) (rotation.topRight.getSecond() + yOffset), -0.01F).setColor(255, 255, 255, 255).setUv(1.0F, 0.0F).setLight(light);
        //top left
        vertexConsumer.addVertex(matrix4f, (float) (rotation.topLeft.getFirst() + xOffset),
                (float) (rotation.topLeft.getSecond() + yOffset), -0.01F).setColor(255, 255, 255, 255).setUv(0.0F, 0.0F).setLight(light);
        matrices.translate(0, 0, (amountOfCardsAttached*0.1F)*-1);
    }

    private static void roundCorners(NativeImage foundTexture, DynamicTexture dynamicTexture, int xOffset, int yOffset) {
        int radius = 40; // adjust the radius as needed for the rounded corners
        int xSize = foundTexture.getWidth();
        int ySize = foundTexture.getHeight();

        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                if(y < ySize/2 && x < xSize/2){
                    //correctly placed
                    //top left corner
                    double distance = Math.sqrt(Math.pow(x - radius, 2) + Math.pow(y - radius, 2));
                    if (distance > radius && (x < radius && y < radius)) {
                        dynamicTexture.getPixels().setPixelRGBA(x+xOffset, y+yOffset, 0); // Set pixel to transparent
                    }
                } else {
                    double distanceFromBottom = Math.pow(y - (ySize - radius), 2);
                    if(y > ySize/2 && x < xSize/2){
                        //bottom left corner
                        double distance = Math.sqrt(Math.pow(x - radius, 2) + distanceFromBottom);
                        if (distance > radius && (x < radius && y > ySize-radius)) {
                            dynamicTexture.getPixels().setPixelRGBA(x+xOffset, y+yOffset, 0); // Set pixel to transparent
                        }
                    } else {
                        double distanceFromRightSide = Math.pow(x - (xSize - radius), 2);
                        if(y < ySize/2 && x > xSize/2){
                            //top right corner
                            double distance = Math.sqrt(distanceFromRightSide + Math.pow(y - radius, 2));
                            if (distance > radius && (x > xSize-radius && y < radius)) {
                                dynamicTexture.getPixels().setPixelRGBA(x+xOffset, y+yOffset, 0); // Set pixel to transparent
                            }
                        } else if(y > ySize/2 && x > xSize/2){
                            //correctly placed
                            //bottom right corner
                            double distance = Math.sqrt(distanceFromRightSide + distanceFromBottom);
                            if (distance > radius && (x > xSize-radius && y > ySize-radius)) {
                                dynamicTexture.getPixels().setPixelRGBA(x+xOffset, y+yOffset, 0); // Set pixel to transparent
                            }
                        }
                    }
                }
            }
        }
    }

    private static NativeImage getCardTextureFromData(CardData cardData) {
        ResourceManager testResourceManager = Minecraft.getInstance().getResourceManager();
        ResourceLocation textureId = ResourceLocation.fromNamespaceAndPath(cardData.nameSpace, "stc_cards/cards/"+ cardData.getCardTextureLocation() +".png");
        NativeImage textureImage = null;
        try {
            Optional<Resource> resource = testResourceManager.getResource(textureId);
            if(resource.isPresent()){
                InputStream inputStream = resource.get().open();
                textureImage = NativeImage.read(inputStream);
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return textureImage;
    }

    private NativeImage getPackTextureFromData(CardPack cardPack) {
        ResourceManager testResourceManager = Minecraft.getInstance().getResourceManager();
        ResourceLocation textureId = ResourceLocation.fromNamespaceAndPath(cardPack.nameSpace, "stc_cards/packs/"+ cardPack.getPackTextureLocation() +".png");
        NativeImage textureImage = null;
        try {
            Optional<Resource> resource = testResourceManager.getResource(textureId);
            if(resource.isPresent()){
                InputStream inputStream = resource.get().open();
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
    public static void draw(PoseStack matrices, MultiBufferSource vertexConsumers, int light, RenderType renderLayer,
                            int layersHigh, int amountOfCardsAttached, CardGame game, double xOffset, double yOffset) {
        draw(matrices, vertexConsumers, light, renderLayer, layersHigh,  amountOfCardsAttached,1, game, xOffset, yOffset);
    }


    public RenderType getRenderLayer() {
        return renderLayer;
    }

    public DynamicTexture getTexture() {
        return texture;
    }

    public int getOriginalImageWidth() {
        return originalImageWidth;
    }

    public int getOriginalImageHeight() {
        return originalImageHeight;
    }

    public double getWidth() {
        return texture.getPixels().getWidth();
    }

    public double getHeight() {
        return texture.getPixels().getHeight();
    }

    public double getUnit(){
        return (double) 128/texture.getPixels().getWidth();
    }

    public double getYOffset(){
        var offset = (maxSide()-originalImageHeight)/(double)2;

        return offset /*== 0 ? originalImageHeight/2 : offset*/;
    }

    public double getXOffset(){
        var offset =  (maxSide()-originalImageWidth)/(double)2;
        return offset /*== 0 ? originalImageWidth/2 : offset*/;
    }

    public int maxSide(){
        return Math.max(originalImageWidth, originalImageHeight);
    }
}
