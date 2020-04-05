package fairies.ai;

import java.util.Collections;
import java.util.List;

import fairies.entity.EntityFairy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.init.PotionTypes;
import net.minecraft.pathfinding.Path;

public class FairyAIGuard extends EntityAITarget {

	protected final EntityFairy taskOwner;

	public FairyAIGuard(EntityFairy creature, boolean checkSight, boolean onlyNearby) {
		super(creature, checkSight, onlyNearby);
		this.taskOwner = creature;
		this.setMutexBits(1);
		this.unseenMemoryTicks = 60;

	}

	@Override
	public boolean shouldExecute() {
		EntityLivingBase ruler = this.taskOwner.getRuler();
		if (ruler == null) {
			return false;
		}
		if (this.taskOwner.guard() && this.taskOwner.getRNG().nextBoolean()) {
			List list = this.taskOwner.findLiveVisibleEntities();
			Collections.shuffle(list, this.taskOwner.getRNG());
			for (int j = 0; j < list.size(); j++) {
				Entity entity = (Entity) list.get(j);
				if (entity instanceof EntityCreature && (!this.taskOwner.peacefulAnimal(entity))) {
					EntityCreature creature = (EntityCreature) entity;
					if (creature.getHealth() > 0) {
						if (creature.getAttackTarget() != null && this.taskOwner.isOnSameTeam(creature.getAttackTarget())) {
							this.taskOwner.setTarget((Entity) creature);
							break;
						}
						if(!this.taskOwner.friendly((EntityLivingBase) creature)) {
							this.taskOwner.setTarget((Entity) creature);
						}
					}
				}
			}
		}
		return false;
	}

//	@Override
//	public void updateTask() {
//
//	}

}