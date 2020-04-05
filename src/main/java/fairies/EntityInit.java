package fairies;

import fairies.entity.EntityFairy;
import fairies.entity.FairyEntityFishHook;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

@ObjectHolder(Version.MOD_ID)
public class EntityInit {
	@Mod.EventBusSubscriber(modid = Version.MOD_ID)
	public static class RegistrationHandler {
		@SubscribeEvent
		public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
			/*
			 * MinecraftForge.EVENT_BUS.register(new CreeperSpawnEventHandler());
			 * EntityEntry creeperEntry = new EntityEntry(CustomDiscCreeper.class,
			 * "creeper"); creeperEntry.setRegistryName("mcdisc:creeper");
			 * event.getRegistry().register(creeperEntry);
			 */
			int entityID = 0;
			event.getRegistry()
					.registerAll(
							EntityEntryBuilder.create().entity(EntityFairy.class)
									.id(new ResourceLocation(Version.MOD_ID, "Fairy"), entityID++).name("Fairy")
									.tracker(64, 4, true).egg(0xea8fde, 0x8658bf)
									.spawn(EnumCreatureType.CREATURE, 10, 8, 8,
											ForgeRegistries.BIOMES.getValuesCollection())
									.build(),
							EntityEntryBuilder.create().entity(FairyEntityFishHook.class)
									.id(new ResourceLocation(Version.MOD_ID, "FairyFishhook"), entityID++)
									.name("FairyFishhook").tracker(64, 4, true)
									// shouldn't have an egg
									.egg(0xea8fde, 0x865800)
									.build());

			System.out.println("registerEntities: Entries registered");
		}
	}
}