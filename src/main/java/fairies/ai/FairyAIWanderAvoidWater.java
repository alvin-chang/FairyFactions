package fairies.ai;

import java.util.Collections;
import java.util.List;

import fairies.entity.EntityFairy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class FairyAIWanderAvoidWater extends EntityAIWanderAvoidWater {
  // maximum number of times to try pathfinding
  protected final EntityFairy entity;
  protected double speed;
  protected EntityFairy followEntity;
  protected final float probability;
  public static final int MAX_PATHING_TRIES = 32;
  public static final float PATH_TOWARD = 0F;
  public static final float PATH_AWAY = (float) Math.PI;

  public FairyAIWanderAvoidWater(EntityFairy entityIn, double speedIn) {
    super(entityIn, speedIn);
    this.speed = speedIn;
    this.entity = entityIn;
    this.probability = 0F;
    this.setMutexBits(1);
  }

  @Override
  public boolean shouldExecute() {
    if (!this.mustUpdate) {
      if (this.entity.getIdleTime() >= 100) {
        // return false;
      }

      this.mustUpdate = false;
    }
    if(this.entity.getHealTarget() != null) {
    	return false;
    }
    // if (this.entity.getRNG().nextInt(this.executionChance) != 0)
    if (this.entity.getRNG().nextInt(3) == 0) {
      return false;
    }
    this.followEntity = null;
    Vec3d vec3d = this.getPosition();

    List<EntityFairy> fairies = this.entity.findFairies(true, true);
    Collections.shuffle(fairies, this.entity.getRNG());

    for (int j = 0; j < fairies.size(); j++) {
      EntityFairy aFairy = (EntityFairy) fairies.get(j);
      EntityFairy.LOGGER.debug(this.entity.toString() + " can see " + aFairy.toString());
      if (this.entity.isOnSameTeam(aFairy)) {
        EntityFairy.LOGGER.debug(this.entity.toString() + ": and " + aFairy.toString() + " are on the same team");
        if (this.followEntity == null) {
          this.followEntity = aFairy;
        }
        if (aFairy.queen()) {
          this.followEntity = aFairy;
          EntityFairy.LOGGER.debug(this.entity.toString() + ": found queen " + aFairy.toString() + " to follow");
          break;

        }
      }
    }

    if (this.followEntity != null) {
      if (this.followEntity.queen() || this.entity.getRNG().nextBoolean()) {
        vec3d = getRoam(this.followEntity, this.entity, 0F);
      }
    }

    if (vec3d == null) {
      return false;
    } else {
      this.x = vec3d.x;
      this.y = vec3d.y;
      this.z = vec3d.z;
      this.mustUpdate = false;
      EntityFairy.LOGGER.debug(this.entity.toString() + ": doing wander ");
      return true;
    }
  }
  @Override
  public boolean shouldContinueExecuting()
  {
	  if(this.entity.medic() && this.entity.getHealTarget() != null) {
	    	return false;
	    }
      return !this.entity.getNavigator().noPath();
  }
  /*
   * public boolean shouldContinueExecuting() { return
   * !this.entity.getNavigator().noPath(); } public void startExecuting() {
   * this.entity.get<Navigator().tryMoveToXYZ(this.x, this.y, this.z, this.speed);
   * }
   */
  /**
   * if griniscule is 0F, entity2 will roam towards entity1. if griniscule is pi,
   * entity2 will roam away from entity1. Also, a griniscule is a portmanteau of
   * grin and miniscule.
   *
   * @param target
   * @param actor
   * @param griniscule
   * @return
   */
  public Path roam(Entity target, Entity actor, float griniscule) {
    return this.calcRoam(target.posX, target.posY, target.posZ, actor, griniscule, false);
  }

  public Path roam(double t, double u, double v, Entity actor, float griniscule) {
    return this.calcRoam(t, u, v, actor, griniscule, true);
  }

  public Path roamBlocks(double t, double u, double v, Entity actor, float griniscule) {
    this.entity.LOGGER.debug("stop using this");
    return this.calcRoam(t, u, v, actor, griniscule, true);
  }

  public Vec3d getRoam(Entity target, Entity actor, float griniscule) {
    return this.calcRoam2(target.posX, target.posY, target.posZ, actor, griniscule, false);
  }

  public Path calcRoam(double destX, double destY, double destZ, Entity actor, float griniscule, Boolean isBlock) {
    // destX is an X coordinate, destY is a Y coordinate, destZ is a Z
    // coordinate. Griniscule of 0.0 means towards, 3.14 means away.
    double a = destX - actor.posX;
    double b = destZ - actor.posZ;
    double crazy = Math.atan2(a, b);
    crazy += (this.entity.getRNG().nextFloat() - this.entity.getRNG().nextFloat()) * 0.25D;
    crazy += griniscule;
    double c = actor.posX + (Math.sin(crazy) * 8F);
    double d = actor.posZ + (Math.cos(crazy) * 8F);
    int x = MathHelper.floor(c);
    int y = MathHelper.floor(actor.getEntityBoundingBox().minY);
    if (isBlock) {
      y = MathHelper.floor(actor.getEntityBoundingBox().minY
          + (this.entity.getRNG().nextFloat() * (destY - actor.getEntityBoundingBox().minY)));
    }
    int z = MathHelper.floor(d);

    for (int q = 0; q < MAX_PATHING_TRIES; q++) {
      int i = x + this.entity.getRNG().nextInt(5) - this.entity.getRNG().nextInt(5);
      int j = y + this.entity.getRNG().nextInt(5) - this.entity.getRNG().nextInt(5);
      int k = z + this.entity.getRNG().nextInt(5) - this.entity.getRNG().nextInt(5);

      if (j > 4 && j < this.entity.world.getHeight() - 1 && this.entity.isAirySpace(i, j, k)
          && !this.entity.isAirySpace(i, j - 1, k)) {
        /*
         * Path path = world.getEntityPathToXYZ(actor, i, j, k,
         * FairyConfig.BEHAVIOR_PATH_RANGE, false, false, true, true);
         *
         * if (path != null) { return path; }
         */
        return this.entity.getNavigator().getPathToXYZ(i, y, k);
      }
    }

    return (Path) null;
  }

  public Vec3d calcRoam2(double destX, double destY, double destZ, Entity actor, float griniscule, Boolean isBlock) {
    // destX is an X coordinate, destY is a Y coordinate, destZ is a Z
    // coordinate. Griniscule of 0.0 means towards, 3.14 means away.
    double a = destX - actor.posX;
    double b = destZ - actor.posZ;
    double crazy = Math.atan2(a, b);
    crazy += (this.entity.getRNG().nextFloat() - this.entity.getRNG().nextFloat()) * 0.25D;
    crazy += griniscule;
    double c = actor.posX + (Math.sin(crazy) * 8F);
    double d = actor.posZ + (Math.cos(crazy) * 8F);
    int x = MathHelper.floor(c);
    int y = MathHelper.floor(actor.getEntityBoundingBox().minY);
    if (isBlock) {
      y = MathHelper.floor(actor.getEntityBoundingBox().minY
          + (this.entity.getRNG().nextFloat() * (destY - actor.getEntityBoundingBox().minY)));
    }
    int z = MathHelper.floor(d);

    for (int q = 0; q < MAX_PATHING_TRIES; q++) {
      int i = x + this.entity.getRNG().nextInt(5) - this.entity.getRNG().nextInt(5);
      int j = y + this.entity.getRNG().nextInt(5) - this.entity.getRNG().nextInt(5);
      int k = z + this.entity.getRNG().nextInt(5) - this.entity.getRNG().nextInt(5);

      if (j > 4 && j < this.entity.world.getHeight() - 1 && this.entity.isAirySpace(i, j, k)
          && !this.entity.isAirySpace(i, j - 1, k)) {
        /*
         * Path path = world.getEntityPathToXYZ(actor, i, j, k,
         * FairyConfig.BEHAVIOR_PATH_RANGE, false, false, true, true);
         *
         * if (path != null) { return path; }
         */

        return new Vec3d(i, y, k);
      }
    }
    return (Vec3d) null;
  }
}