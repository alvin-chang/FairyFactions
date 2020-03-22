package fairies.proxy;

import fairies.FairyFactions;
import fairies.Version;
import fairies.entity.EntityFairy;
import fairies.entity.FairyEntityFishHook;
import fairies.event.FairyEventListener;
import fairies.event.PacketSetFairyName;
import java.util.List;
import java.util.logging.Logger;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Biomes;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketEntityAttach;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.common.registry.EntityRegistry;


public class CommonProxy {

  protected FairyEventListener eventListener;
  protected FMLEventChannel eventChannel;

  public void preInit() {
    this.eventChannel =
        NetworkRegistry.INSTANCE.newEventDrivenChannel(Version.CHANNEL);
  }

  public void initChannel(FairyEventListener listener) {
    this.eventListener = listener;
    this.eventChannel.register(this.eventListener);
  }

  public void initEntities() {
    int entityID = 0;
    registerEntity(entityID++, EntityFairy.class, "Fairy", 0xea8fde, 0x8658bf);
    registerEntity(entityID++, FairyEntityFishHook.class, "FairyFishhook");
  }

  private void registerEntity(int entityID, Class<? extends Entity> entityClass,
                              String entityName) {
                                
    EntityRegistry.registerModEntity(
        new ResourceLocation(Version.MOD_ID + ":" + entityName), entityClass,
        entityName, entityID, FairyFactions.INSTANCE, 64, 4, true);
  }

  private void registerEntity(int entityID, Class<? extends EntityLiving> entityClass,
                              String entityName, int backgroundEggColor,
                              int foregroundEggColor) {
   
    EntityRegistry.registerModEntity(
        new ResourceLocation(Version.MOD_ID + ":" + entityName), entityClass,
        entityName, entityID, FairyFactions.INSTANCE, 64, 4, true,
		backgroundEggColor,foregroundEggColor);
    //EntityRegistry.addSpawn(entityClass, 100, 3, 5, EnumCreatureType.CREATURE, Biomes.BIRCH_FOREST_HILLS, Biomes.BIRCH_FOREST, Biomes.PLAINS);
  }

  public void initGUI() {
    // should only ever be implemented on client
  }

  public void openRenameGUI(EntityFairy fairy) {
    // should only ever be implemented on client
  }

  public void postInit() {}

  public EntityPlayer getCurrentPlayer() { return null; }

  ////////// packet handling

  public void sendChat(EntityPlayerMP player, String s) {
    if (player != null && !s.isEmpty()) {
/*       player.NetHandlerPlayServer.sendPacket(
          new SPacketChat(new TextComponentString(s))); */
	}
  }

  public void sendToClient(FMLProxyPacket packet, EntityPlayerMP player) {
    eventChannel.sendTo(packet, player);
  }
  public void sendToServer(FMLProxyPacket packet) {
    eventChannel.sendToServer(packet);
  }
  public void sendToAllPlayers(Packet<?> packet) {
  /*   List<EntityPlayerMP> players =
        MinecraftServer.getServer().getConfigurationManager().playerEntityList;
    for (EntityPlayerMP player : players) {
      player.playerNetServerHandler.sendPacket(packet);
    } */
  }

  public void sendFairyRename(final EntityFairy fairy, final String name) {
    final PacketSetFairyName packet = new PacketSetFairyName(fairy, name);
    sendToServer(packet);
  }

  // Packet that handles fairy mounting.
  public void sendFairyMount(final Entity rider, final Entity vehicle) {
    final Entity newVehicle;
    if (rider.getRidingEntity() != null && rider.getRidingEntity() == vehicle) {
      newVehicle = null;
    } else {
      newVehicle = vehicle;
    }

    final SPacketEntityAttach packet =
        new SPacketEntityAttach(rider, newVehicle);
    sendToAllPlayers(packet);

    if (!(rider instanceof FairyEntityFishHook)) {
      //rider.mountEntity(newVehicle); addPassenger??
    }
  }

  // Packet that handles forced fairy despawning.
  public void sendFairyDespawn(Entity entity) {
    final int[] eid = new int[] {entity.getEntityId()};
    final SPacketDestroyEntities packet = new SPacketDestroyEntities(eid);
    sendToAllPlayers(packet);
    entity.setDead();
  }

  // Packet that handles sending text to specific players.
  @Deprecated
  public void sendDisband(EntityPlayerMP player, String s) {
    sendChat(player, s);
    /*
if (player != null) {
    final SPacketChat packet = new SPacketChat(new TextComponentString(s));
player.playerNetServerHandler.sendPacket(packet);
}

//Shouldn't enable this by default, could be spammy.
//MinecraftServer.logger.info(s);
*/
  }
}
