package fairies.ai;

import java.util.Collections;
import java.util.List;

import fairies.entity.EntityFairy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;

public class FairyAIRogue extends FairyAIBase {
	public FairyAIRogue(EntityFairy fairy, double speedIn) {
		super(fairy, speedIn);
		this.setMutexBits(0);
	}
	@Override
	public boolean shouldExecute() {
		if (this.theFairy.rogue() && rand.nextBoolean()) {
			return true;
		}
		return false;
	}

	  @Override
	  public void updateTask() {
	  
	  }

}


//A handler specifically for the rogue class.
//	private void handleRogue() {
//		if (rand.nextBoolean()) {
//			return;
//		}
//
//		List<?> list = world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().expand(16D, 16D, 16D));
//		Collections.shuffle(list, rand);
//
//		for (int j = 0; j < list.size(); j++) {
//			Entity entity = (Entity) list.get(j);
//
//			if (canEntityBeSeen(entity) && !entity.isDead) {
//				if ((ruler != null || queen()) && entity instanceof EntityFairy && sameTeam((EntityFairy) entity)) {
//					EntityFairy fairy = (EntityFairy) list.get(j);
//
//					if (fairy.getHealth() > 0) {
//						EntityLivingBase scary = null;
//
//						if (fairy.getEntityFear() != null) {
//							scary = fairy.getEntityFear();
//						} else if (fairy.entityToAttack != null) {
//							scary = fairy.entityToAttack;
//						}
//
//						if (scary != null) {
//							float dist = getDistance(scary);
//
//							if (dist > 16F || !canEntityBeSeen(scary)) {
//								scary = null;
//							}
//						}
//
//						if (scary != null) {
//							if (canHeal()) {
//								if (fairy.entityToAttack == scary && canEntityBeSeen(scary)) {
//									setCryTime(120);
//									this.setEntityFear(scary);
//									Path dest = roam(entity, this, (float) Math.PI);
//
//									/*
//									 * if (dest != null) { setPathToEntity(dest); }
//									 */
//									this.getNavigator().setPath(dest, this.getAIMoveSpeed());
//
//									break;
//								} else if (fairy.getCryTime() > 60) {
//									setCryTime(Math.max(fairy.getCryTime() - 60, 0));
//									this.setEntityFear(scary);
//									Path dest = roam(entity, this, (float) Math.PI);
//
//									/*
//									 * if (dest != null) { setPathToEntity(dest); }
//									 */
//									this.getNavigator().setPath(dest, this.getAIMoveSpeed());
//
//									break;
//								}
//							} else {
//								this.setTarget((Entity) scary);
//								break;
//							}
//						}
//					}
//				} else if (ruler != null && canHeal() && entity instanceof EntityCreature
//						&& !(entity instanceof EntityCreeper)
//						&& (!(entity instanceof EntityAnimal) || (!peacefulAnimal((EntityAnimal) entity)))) {
//					/**
//					 * TODO: Update AI.
//					 *
//					 * EntityCreature creature = (EntityCreature) entity;
//					 *
//					 * if (creature.getHealth() > 0 && creature.getEntityToAttack() != null &&
//					 * creature.getEntityToAttack() == ruler) { this.setTarget((Entity) creature);
//					 * break; }
//					 */
//				} else if (entity instanceof EntityTNTPrimed && !hasPath()) {
//					// Running away from lit TNT.
//					float dist = getDistance(entity);
//
//					if (dist < 8F) {
//						Path dest = roam(entity, this, (float) Math.PI);
//
//						if (dest != null) {
//							// setPathToEntity(dest);
//							this.getNavigator().setPath(dest, this.getAIMoveSpeed());
//
//							if (!flymode()) {
//								setFlymode(true);
//								jump();
//								setFlapEnergy(100);
//							}
//
//							break;
//						}
//					}
//				}
//			}
//		}
//	}