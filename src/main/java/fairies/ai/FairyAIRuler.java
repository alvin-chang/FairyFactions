package fairies.ai;

import net.minecraft.entity.ai.EntityAIBase;
import fairies.FairyConfig;
import fairies.FairyFactions;
import fairies.entity.EntityFairy;

public class FairyAIRuler extends EntityAIBase {

	private EntityFairy theFairy;
	protected double speed;

	public FairyAIRuler(EntityFairy fairy, double speedIn) {
		this.theFairy = fairy;
		this.speed = speedIn;

		this.setMutexBits(0);
	}

	@Override
	public boolean shouldExecute() {
		// TODO Auto-generated method stub
		//FairyFactions.LOGGER.debug(this.theFairy.toString() + ": shouldexec Queen");
		if (this.theFairy.queen()) {
		//	FairyFactions.LOGGER.debug(this.theFairy.toString() + ": starting Queen");
			return true;
		}
		return false;
	}

}
