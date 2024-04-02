package drai.dev.stackthecards.renderers;

import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.registry.*;
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
    private CardData cardData;
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

    /*public CardTexture(CardConnection connection, List<CardIdentifier> containedCards, boolean isFlipped) {
        this.texture = createConnectedCardTexture(connection, containedCards, isFlipped, this);
        Identifier identifier = MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("stc_card/"+this.cardData.getCardId(), this.texture);
        this.renderLayer = RenderLayer.getText(identifier);
        this.texture.upload();
    }

    private NativeImageBackedTexture createConnectedCardTexture(CardConnection connection, List<CardIdentifier> containedCards, boolean isFlipped, CardTexture texture) {
        var cardData = CardConnection.getCardDataFromLayout(connection.getLayout());
        var foundTextures = getCardTexturesFromConnection(cardData);
        var transposedFoundTextures = transposeFoundTextures(foundTextures);
        NativeImageBackedTexture nativeImageBackedTexture;
        if(foundTextures.size()<1 || foundTextures.get(0).size()<1){
            nativeImageBackedTexture = new NativeImageBackedTexture(16,16, true);
        } else {
            biggestCardNativeImageBackedTexture = createCardTexture(getBiggestCard(foundTextures), this);
            nativeImageBackedTexture = new NativeImageBackedTexture(getWidestSide(foundTextures,connection.getLayout()),getTallestSide(transposedFoundTextures,connection.getLayoutByColumn()), true);
            var xCentre = nativeImageBackedTexture.getImage().getWidth()/2;
            var yCentre = nativeImageBackedTexture.getImage().getHeight()/2;
            var layout = connection.getLayout();
            var layers = connection.getLayers();
            for (int z = 0; z < layers; z++) {
                var previousRowHeight = 0;
                for (int i = 0; i < layout.size(); i++) {
                    var row = layout.get(i);
                    var rowSize = CardConnection.getSizeIgnoringEmpty(row);
                    var rowHeight = getMaxHeight(foundTextures.get(i));
                    var rowWidth = getMaxHeight(foundTextures.get(i))/2;
                    for (int j = 0; j < rowSize; j++) {
                        var connectionSlot = row.stream().filter(element-> element != CardConnectionEntry.EMPTY).toList().get(j);
                            var previousColumnWidth = 0;
                            for (int previous = 0; previous < j; previous++) {
                                previousColumnWidth += getMaxWidth(transposedFoundTextures.get(previous));
                            }
                            var rowStart = previousColumnWidth;
                            var columnStart = previousRowHeight;
                            var columnWidth = getMaxWidth(transposedFoundTextures.get(j));
                            var slotTexture = createCardTexture(CardGameRegistry.getCardData(connectionSlot.self), this).getImage();
                            var slotTextureOffsetx = (slotTexture.getWidth() - originalImageWidth)/2;
                            var slotTextureOffsety = (slotTexture.getHeight() - originalImageHeight)/2;

                            slotTexture.resizeSubRectTo();
                            drawConnectedCardOntoTexture(nativeImageBackedTexture, connectionSlot, slotTexture)

                    }
                    previousRowHeight += rowHeight;
                }
            }


        }
        return nativeImageBackedTexture;

    }

    private int getMaxHeight(List<NativeImage> nativeImages) {

    }

    private int getMaxWidth(List<NativeImage> column) {

    }

    private int getTallestSide(List<List<NativeImage>> foundTextures, List<List<CardConnectionEntry>> layoutByColumn) {

    }

    private int getWidestSide(List<List<NativeImage>> foundTextures, List<List<CardConnectionEntry>> layout) {

    }

    private List<List<NativeImage>> getCardTexturesFromConnection(List<List<CardData>> cardData) {

    }*/

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

    public static void draw(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, RenderLayer renderLayer,
                            int layersHigh, int amountOfCardsAttached, double scale, CardGame game, double xOffset, double yOffset) {
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

        //bottom left
        vertexConsumer.vertex(matrix4f, (float) (0.0F + yOffset), (float) (128.0F * scale + xOffset), -0.01F).color(255, 255, 255, 255).texture(0.0F, 1.0F).light(light).next();
        //bottom right
        vertexConsumer.vertex(matrix4f, (float) (128.0F * scale+ yOffset), (float) (128.0F * scale+ xOffset), -0.01F).color(255, 255, 255, 255).texture(1.0F, 1.0F).light(light).next();
        //top right
        vertexConsumer.vertex(matrix4f, (float) (128.0F * scale+ yOffset), (float) (0.0F+ xOffset), -0.01F).color(255, 255, 255, 255).texture(1.0F, 0.0F).light(light).next();
        //top left
        vertexConsumer.vertex(matrix4f, (float) (0.0F+ yOffset), (float) (0.0F+ xOffset), -0.01F).color(255, 255, 255, 255).texture(0.0F, 0.0F).light(light).next();
    }

    public static void drawConnectedCard(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
                                         RenderLayer renderLayer, int layersHigh, int amountOfCardsAttached, CardGame game, double xOffset, double yOffset, CardRotation rotation) {
//        var modifierRotation = (float)rotation.rotation * 360.0F / 8.0F;
//        var rotationDegrees = RotationAxis.POSITIVE_Z.rotationDegrees(modifierRotation);
//        matrices.multiply(rotationDegrees);
        /*if(rotation == CardRotation.LEFT || rotation == CardRotation.UPRIGHT){
            matrices.translate(0, -128.0F, 0.0F);
        }*/

        //128 is a block
//        matrices.translate(16,16, 0);
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

        //bottom left
        vertexConsumer.vertex(matrix4f, (float) (rotation.bottomLeft.getLeft() + xOffset),
                (float) (rotation.bottomLeft.getRight() + yOffset), -0.01F).color(255, 255, 255, 255).texture(0.0F, 1.0F).light(light).next();
        //bottom right
        vertexConsumer.vertex(matrix4f, (float) (rotation.bottomRight.getLeft() + xOffset),
                (float) (rotation.bottomRight.getRight() + yOffset), -0.01F).color(255, 255, 255, 255).texture(1.0F, 1.0F).light(light).next();
        //top right
        vertexConsumer.vertex(matrix4f, (float) (rotation.topRight.getLeft() + xOffset),
                (float) (rotation.topRight.getRight() + yOffset), -0.01F).color(255, 255, 255, 255).texture(1.0F, 0.0F).light(light).next();
        //top left
        vertexConsumer.vertex(matrix4f, (float) (rotation.topLeft.getLeft() + xOffset),
                (float) (rotation.topLeft.getRight() + yOffset), -0.01F).color(255, 255, 255, 255).texture(0.0F, 0.0F).light(light).next();
        /*if(rotation == CardRotation.LEFT || rotation == CardRotation.UPRIGHT){
            matrices.translate(0, 128.0F, 0.0F);
        }*/
        matrices.translate(0, 0, (amountOfCardsAttached*0.1F)*-1);
//        matrices.translate(-16,-16, 0);
//        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(modifierRotation*-1));
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
    public static void draw(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, RenderLayer renderLayer,
                            int layersHigh, int amountOfCardsAttached, CardGame game, double xOffset, double yOffset) {
        draw(matrices, vertexConsumers, light, renderLayer, layersHigh,  amountOfCardsAttached,1, game, xOffset, yOffset);
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

    public double getWidth() {
        return texture.getImage().getWidth();
    }

    public double getHeight() {
        return texture.getImage().getHeight();
    }

    public double getUnit(){
        return (double) 128/texture.getImage().getWidth();
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
