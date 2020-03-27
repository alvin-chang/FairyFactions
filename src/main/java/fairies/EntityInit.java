package fairies;
import fairies.entity.EntityFairy;
import net.minecraft.entity.Entity;
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
    public void entityRegistration(RegistryEvent.Register<EntityEntry> event) {
      int entityID = 0;
      EntityEntry aFairy =
          EntityEntryBuilder.create()
              .entity(EntityFairy.class)
              .id(new ResourceLocation("fairies", "fairy"), entityID++)
              .name("Fairy")
              .tracker(160, 2, false)
              .egg(0x4c3e30, 0xf0f0f)
              .spawn(EnumCreatureType.AMBIENT, 10, 8, 8,
                     ForgeRegistries.BIOMES.getValuesCollection())
              .build();

      event.getRegistry().register(aFairy);
      System.out.println("Entries registered");
    }

      public static void
    registerEntities(RegistryEvent.Register<EntityEntry> event) {
  /*     MinecraftForge.EVENT_BUS.register(new CreeperSpawnEventHandler());
      EntityEntry creeperEntry =
          new EntityEntry(CustomDiscCreeper.class, "creeper");
      creeperEntry.setRegistryName("mcdisc:creeper");
      event.getRegistry().register(creeperEntry); */
 /*      int entityID = 5;
      EntityEntry aFairy =
          EntityEntryBuilder.create()
              .entity(EntityFairy.class)
              .id(new ResourceLocation("fairies", "fairy"), entityID++)
              .name("Fairy")
              .tracker(160, 2, false)
              .egg(0x4c3e30, 0xf0f0f)
              .spawn(EnumCreatureType.AMBIENT, 10, 8, 8,
                     ForgeRegistries.BIOMES.getValuesCollection())
              .build();

      event.getRegistry().register(aFairy); */
      System.out.println("registerEntities: Entries registered");
    }
  }
}