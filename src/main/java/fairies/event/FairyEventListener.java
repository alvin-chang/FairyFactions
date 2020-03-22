package fairies.event;

import java.util.Map;

import com.google.common.collect.Maps;

import fairies.FairyFactions;
import fairies.Version;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public class FairyEventListener {

	public enum PacketType {
		SET_FAIRY_NAME(0, PacketSetFairyName.class);
		
		public final byte packet_id;
		protected final Class<?extends FairyPacket> packet_class;
		private PacketType(final int id, Class<?extends FairyPacket> clazz) {
			packet_id = (byte)id;
			packet_class = clazz;
		}
		
		public static PacketType get(final byte id) {
			return map.get(id);
		}
		
		public static final Map<Byte,PacketType> map = Maps.newHashMap();
		static {
			for( PacketType type : PacketType.values() ) {
				map.put(type.packet_id, type);
			}
		}
	}
	
	@SubscribeEvent
	public void onPacket(ServerCustomPacketEvent event) {
		if( event.getPacket().channel().equals(Version.CHANNEL) ) {
			final NetHandlerPlayServer handler = (NetHandlerPlayServer)event.getHandler();
			handlePacket( event.getPacket(), handler.player );
		}
	}
	
	protected void handlePacket(FMLProxyPacket proxyPacket, EntityPlayerMP player) {
		final ByteBuf payload = proxyPacket.payload();
		if( payload.readableBytes() > 0 ) {
			final PacketBuffer buf = new PacketBuffer(payload);
			
			final byte id = buf.readByte();
			final PacketType type = PacketType.get(id);
			if( type == null ) {
				FairyFactions.LOGGER.error("Got unknown packet type "+id);
				return;
			}
			
			final FairyPacket packet;
			try {
				packet = type.packet_class.newInstance();
			} catch (Exception e) {
				// ERROR, unable to actually process
				e.printStackTrace();
				return;
			}
			
			packet.init(buf);
			packet.handle(proxyPacket.getOrigin());
		}
	}
	
}
