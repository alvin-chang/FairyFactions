package fairies.ai;

import fairies.entity.EntityFairy;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class FairyAIHealing extends FairyAIBase {

  public FairyAIHealing(EntityFairy fairy, double speedIn) {
    super(fairy, speedIn);
    // TODO Auto-generated constructor stub
    this.setMutexBits(3);
  }

  @Override
  public boolean shouldExecute() {
    // TODO Auto-generated method stub
    if (this.theFairy.medic() && this.theFairy.canHeal()) {
      if (this.theFairy.getHealTarget() == null) {
        this.theFairy.LOGGER.debug(this.theFairy.toString() +
                                   ": has no target");
        Entity toHeal = null;
        if (this.theFairy.getHealth() < this.theFairy.getMaxHealth()) {
          toHeal = this.theFairy;
        } else {
          toHeal = this.theFairy.getEntityToHeal();
        }
        if (toHeal == null) {
          return false;
        }
        this.theFairy.LOGGER.debug(this.theFairy.toString() +
                                   ": found target to heal");
        this.theFairy.setHealTarget(toHeal);
      }
      return true;
    }
    return false;
  }

  @Override
  public boolean shouldContinueExecuting() {
    if (this.theFairy.getHealTarget() != null) {

      return true;
    }
    return !this.theFairy.getNavigator().noPath();
  }

  @Override
  public void updateTask() {
    /*
     * Path dest = world.getPathEntityToEntity( this, entityHeal, 16F, false,
     * false, true, true);
     *
     * if (dest != null) { setPathToEntity(dest); }
     */
    if (null != this.theFairy.getHealTarget()) {
      this.theFairy.LOGGER.debug("medic has target");
      if (this.theFairy == this.theFairy.getHealTarget()) {
        // this.theFairy.doHeal(this.theFairy.getHealTarget());
        this.theFairy.setHealTarget(null);
      } else {
        Entity patient = this.theFairy.getHealTarget();
        this.theFairy.getLookHelper().setLookPositionWithEntity(patient, 10.0F, (float)this.theFairy.getVerticalFaceSpeed());
        this.theFairy.LOGGER.debug(this.theFairy.toString() + "trying to get to " +
                                   this.theFairy.getHealTarget().toString());
        if (!this.theFairy.getNavigator().tryMoveToEntityLiving(
                this.theFairy.getHealTarget(),
                /*this.theFairy.getAIMoveSpeed()*/
                this.speed)) {
                  this.theFairy.LOGGER.debug("Failed to set path");
          return;
        }
      }
    }
  }
}