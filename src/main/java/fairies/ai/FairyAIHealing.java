package fairies.ai;

import fairies.entity.EntityFairy;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.MathHelper;

public class FairyAIHealing extends FairyAIBase {

  private float distanceBeforePotionThrow = 3F;
  private EntityLivingBase patient = null;

  public FairyAIHealing(EntityFairy fairy, double speedIn) {
    super(fairy, speedIn);
    // TODO Auto-generated constructor stub
    this.setMutexBits(3);
  }

  @Override
  public boolean shouldExecute() {
    if (this.theFairy.medic() && this.theFairy.canHeal()) {
      if (this.theFairy.getHealTarget() == null) {  
        patient = (EntityLivingBase) this.theFairy.getEntityToHeal();
        if (patient == null) {
          return false;
        }
        this.theFairy.setHealTarget(patient);
      }
      return true;
    }
    return false;
  }

  @Override
  public boolean shouldContinueExecuting() {
    if (patient != null) {
      if (patient.getHealth() == patient.getMaxHealth()) {
        this.theFairy.setHealTarget(null);
        patient = null;
        return false;
      }
      return true;
    }

    return !this.theFairy.getNavigator().noPath();
  }

  @Override
  public void updateTask() {
    /*
     * Path dest = world.getPathEntityToEntity( this, entityHeal, 16F, false, false,
     * true, true);
     *
     * if (dest != null) { setPathToEntity(dest); }
     */
    if (null != this.theFairy.getHealTarget()) {
      this.theFairy.LOGGER.debug("medic has target");
      float dist = this.theFairy.getDistance(patient);
      if (dist > distanceBeforePotionThrow) {
        getToPatient(patient);
      } else {
        potion(patient, this.theFairy, distanceBeforePotionThrow + 0.1F, PotionTypes.HEALING);
      }
    }
  }

  public void getToPatient(Entity patient) {

    this.theFairy.getLookHelper().setLookPositionWithEntity(patient, 10.0F,
        (float) this.theFairy.getVerticalFaceSpeed());
    this.theFairy.LOGGER.debug(this.theFairy.toString() + "trying to get to " + patient.toString());
    if (!this.theFairy.getNavigator().tryMoveToEntityLiving(patient,
        /* this.theFairy.getAIMoveSpeed() */
        this.speed)) {
      this.theFairy.LOGGER.debug("Failed to set path");
    }
  }

  public void potion(Entity target, EntityFairy host, float distanceFactor, PotionType potiontype) {
    double d0 = target.posY + (double) target.getEyeHeight() - 1.100000023841858D;
    double d1 = target.posX + target.motionX - host.posX;
    double d2 = d0 - host.posY;
    double d3 = target.posZ + target.motionZ - host.posZ;
    float f = MathHelper.sqrt(d1 * d1 + d3 * d3);

    EntityPotion entitypotion = new EntityPotion(host.world, host,
        PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), potiontype));
    entitypotion.rotationPitch -= -20.0F;
    entitypotion.shoot(d1, d2 + (double) (f * 0.2F), d3, 0.75F, 8.0F);

    host.world.playSound((EntityPlayer) null, host.posX, host.posY, host.posZ, SoundEvents.ENTITY_WITCH_THROW,
        host.getSoundCategory(), 1.0F, 0.8F + host.getRNG().nextFloat() * 0.4F);
    host.world.spawnEntity(entitypotion);
  }
}
