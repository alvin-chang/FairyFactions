package fairies.proxy;

import fairies.FairyFactions;
import fairies.Version;
import fairies.entity.EntityFairy;
import fairies.entity.FairyEntityFishHook;
import fairies.event.FairyEventListener;
import fairies.event.PacketSetFairyName;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketEntityAttach;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
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
      player.connection.sendPacket(new SPacketChat(new TextComponentString(s)));
    }
  }

  public void sendToClient(FMLProxyPacket packet, EntityPlayerMP player) {
    eventChannel.sendTo(packet, player);
  }

  public void sendToServer(FMLProxyPacket packet) {
    eventChannel.sendToServer(packet);
  }

  public void sendToAllPlayers(Packet<?> packet) {
    List<EntityPlayerMP> players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers();
/*
    List<EntityPlayerMP> players = MinecraftServer.getEntityWorld().getPlayers(); */
    for (EntityPlayerMP player : players) {
      player.connection.sendPacket(packet);
    } 
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
      // rider.mountEntity(newVehicle); addPassenger??
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

    if (player != null) {
      final SPacketChat packet = new SPacketChat(new TextComponentString(s));
      player.connection.sendPacket(packet);
    }

    // Shouldn't enable this by default, could be spammy.
    // MinecraftServer.logger.info(s);
  }
}
