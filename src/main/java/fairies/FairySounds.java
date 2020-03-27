package fairies;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Version.MOD_ID)
public final class FairySounds {
  protected static final Map<String, SoundEvent> resMap = Maps.newHashMap();
  private static final String sounds[] = {
      "entity.fairy.death", "entity.fairy.idle", "entity.fairy.hurt"};
  
  public static SoundEvent getSoundEvent(String key) {
    if (!resMap.containsKey(key)) {
      final ResourceLocation name = new ResourceLocation(Version.MOD_ID, key);
      final SoundEvent res = new SoundEvent(name).setRegistryName(name);
      resMap.put(key, res);
      return res;
    } else {
      return resMap.get(key);
    }
  }

  @SubscribeEvent
  public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
    for (int i = 0; i < sounds.length; i++) {
      event.getRegistry().register(getSoundEvent(sounds[i]));
    }
  }
}