package fairies.ai;

import fairies.entity.EntityFairy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.MathHelper;

public class FairyAIWanderAvoidWater extends EntityAIWanderAvoidWater {
  // maximum number of times to try pathfinding
  protected final EntityFairy entity;
  protected final float probability;
  public static final int MAX_PATHING_TRIES = 32;
  public static final float PATH_TOWARD = 0F;
  public static final float PATH_AWAY = (float)Math.PI;

  public FairyAIWanderAvoidWater(EntityFairy entityIn, double speedIn) {
    super(entityIn, speedIn);
    this.entity = entityIn;
    this.probability = 120F;
  }

  /**
   * if griniscule is 0F, entity2 will roam towards entity1. if griniscule is
   * pi, entity2 will roam away from entity1. Also, a griniscule is a
   * portmanteau of grin and miniscule.
   *
   * @param target
   * @param actor
   * @param griniscule
   * @return
   */
  public Path roam(Entity target, Entity actor, float griniscule) {
    return this.calcRoam(target.posX, target.posY, target.posZ, actor,
                         griniscule, false);
  }

  public Path roam(double t, double u, double v, Entity actor,
                   float griniscule) {
    return this.calcRoam(t, u, v, actor, griniscule, true);
  }

  public Path roamBlocks(double t, double u, double v, Entity actor,
                         float griniscule) {
    this.entity.LOGGER.debug("stop using this");
    return this.calcRoam(t, u, v, actor, griniscule, true);
  }

  public Path calcRoam(double destX, double destY, double destZ, Entity actor,
                       float griniscule, Boolean isBlock) {
    // destX is an X coordinate, destY is a Y coordinate, destZ is a Z
    // coordinate. Griniscule of 0.0 means towards, 3.14 means away.
    double a = destX - actor.posX;
    double b = destZ - actor.posZ;
    double crazy = Math.atan2(a, b);
    crazy +=
        (this.entity.getRNG().nextFloat() - this.entity.getRNG().nextFloat()) *
        0.25D;
    crazy += griniscule;
    double c = actor.posX + (Math.sin(crazy) * 8F);
    double d = actor.posZ + (Math.cos(crazy) * 8F);
    int x = MathHelper.floor(c);
    int y = MathHelper.floor(actor.getEntityBoundingBox().minY);
    if (isBlock) {
      y = MathHelper.floor(actor.getEntityBoundingBox().minY +
                           (this.entity.getRNG().nextFloat() *
                            (destY - actor.getEntityBoundingBox().minY)));
    }
    int z = MathHelper.floor(d);

    for (int q = 0; q < MAX_PATHING_TRIES; q++) {
      int i =
          x + this.entity.getRNG().nextInt(5) - this.entity.getRNG().nextInt(5);
      int j =
          y + this.entity.getRNG().nextInt(5) - this.entity.getRNG().nextInt(5);
      int k =
          z + this.entity.getRNG().nextInt(5) - this.entity.getRNG().nextInt(5);

      if (j > 4 && j < this.entity.world.getHeight() - 1 &&
          this.entity.isAirySpace(i, j, k) &&
          !this.entity.isAirySpace(i, j - 1, k)) {
        /*
         * Path path = world.getEntityPathToXYZ(actor, i, j, k,
         * FairyConfig.BEHAVIOR_PATH_RANGE, false, false, true, true);
         *
         * if (path != null) { return path; }
         */
        return this.entity.getNavigator().getPathToXYZ(i, y, k);
      }
    }

    return (Path)null;
  }
}