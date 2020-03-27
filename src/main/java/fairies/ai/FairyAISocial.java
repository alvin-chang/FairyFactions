package fairies.ai;

import fairies.entity.EntityFairy;

//import net.minecraft.entity.ai.EntityAIBase;

public class FairyAISocial extends FairyAIBase {

	public FairyAISocial(EntityFairy fairy, double speedIn) {
		super(fairy, speedIn);
		// TODO Auto-generated constructor stub

		this.setMutexBits(0);
	}

	@Override
	public boolean shouldExecute() {
		// TODO Auto-generated method stub
		if (rand.nextBoolean()) {
			return true;
		}
		return false;
	}
	@Override
    public boolean shouldContinueExecuting()
    {
        return true;
    }
	@Override
	public void updateTask() {
		this.theFairy.doHandleSocial();
		//this.theFairy.handleSocial();
	}

}
/**
 * TODO: Update to new AI archicture
 *
 */

// public void updateEntityActionState() {

// // _dump_();

// if (this.theFairy.wasFishing) {
// wasFishing = false;

// if (isSitting() && getFishEntity() == null) {
// setSitting(false);
// }
// }

// if (isSitting()) {
// handlePosted(false);
// return;
// }

// flyBlocked = checkFlyBlocked();

// if (getFlyTime() > 0) {
// setFlyTime(getFlyTime() - 1);
// }

// boolean liftFlag = false;

// if (flymode()) {
// fallDistance = 0F;

// if (ridingEntity != null) {
// if (entityToAttack != null && ridingEntity == entityToAttack) {
// setFlyTime(200);
// liftFlag = true;

// if ((attackTime <= 0) || flyBlocked) {
// attackTime = 0;
// attackEntity(ridingEntity, 0F);
// liftFlag = false;
// }
// } else if (tamed()) {
// if (ridingEntity.onGround || ridingEntity.isInWater()) {
// setFlyTime((queen() || scout() ? 60 : 40));

// if (withered()) {
// setFlyTime(getFlyTime() - 10);
// }
// }
// }
// }

// if (getFlyTime() <= 0 || (flyBlocked
// && (ridingEntity == null || (entityToAttack != null && ridingEntity ==
// entityToAttack)))) {
// setCanFlap(false);
// } else {
// setCanFlap(true);
// }

// if (ridingEntity == null && (onGround || inWater)) {
// setFlymode(false);
// setFlyTime(400 + rand.nextInt(200));

// // Scouts are less likely to want to walk. if (scout()) {
// setFlyTime(getFlyTime() / 3);
// }
// } else {
// if (getFlyTime() <= 0 && !flyBlocked) {
// jump();
// setFlymode(true);
// setCanFlap(true);
// setFlyTime(400 + rand.nextInt(200));

// // Scouts are more likely to want to fly.
// if (scout()) {
// setFlyTime(getFlyTime() * 3);
// }
// }

// if (ridingEntity != null && !flymode()) {
// setFlymode(true);
// setCanFlap(true);
// }

// if (!flymode() && !onGround && fallDistance >= 0.5F && ridingEntity == null)
// {
// setFlymode(true);
// setCanFlap(true);
// setFlyTime(400 + rand.nextInt(200));
// }
// }

// setLiftOff(liftFlag);

// if (healTime > 0) {
// --healTime;
// }

// if (cryTime > 0) {
// --cryTime;

// if (cryTime <= 0) {
// entityFear = null;
// }

// if (getCryTime() > 600) {
// setCryTime(600);
// }
// }

// // TODO: break this out ++listActions;
// if (listActions >= 8) {
// listActions = rand.nextInt(3);

// if (angry()) {
// handleAnger();
// } else if (crying()) {
// handleFear();
// } else {
// handleRuler();

// if (medic()) {
// handleHealing();
// } else if (rogue()) {
// handleRogue();
// } else {
// handleSocial();
// }

// handlePosted(true);
// }
// }

// // fairies run away from players in peaceful
// if (world.getDifficulty() == EnumDifficulty.PEACEFUL && entityToAttack !=
// null
// && entityToAttack instanceof EntityPlayer)

// {
// setEntityFear(entityToAttack);
// setCryTime(Math.max(getCryTime(), 100));
// setTarget((Entity) null);
// }

// setCrying(getCryTime() > 0);
// setAngry(entityToAttack != null);
// setCanHeal(healTime <= 0);

// // _dump_(); }// end: updateEntityActionState
// }

// This handles actions concerning teammates and entities atacking their
// ruler.
