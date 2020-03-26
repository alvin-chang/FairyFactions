package fairies;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = Version.MOD_ID)
public final class FairySounds {

  public static final SoundEvent FAIRY_DEATH = createEvent("entity.fairy.death");
  public static final SoundEvent FAIRY_IDLE = createEvent("entity.fairy.idle");
  public static final SoundEvent FAIRY_HURT = createEvent("entity.fairy.hurt");

  private static SoundEvent createEvent(String sound) {
    ResourceLocation name = new ResourceLocation(Version.MOD_ID, sound);
    return new SoundEvent(name).setRegistryName(name); // not sound?
  }

  @SubscribeEvent
  public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
    event.getRegistry().registerAll(FAIRY_DEATH, FAIRY_IDLE, FAIRY_HURT);
  }
}
