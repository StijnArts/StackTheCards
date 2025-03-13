package drai.dev.stackthecards.network;

import net.minecraft.client.*;

public class ClientNetworking {
   public static boolean isIntegratedServer(){
       return Minecraft.getInstance().hasSingleplayerServer();
   }
}
