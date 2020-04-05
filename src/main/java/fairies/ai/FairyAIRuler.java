package fairies.ai;

import net.minecraft.entity.ai.EntityAIBase;

import java.util.List;

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
		// Look for fairies with no ruler
		if (!this.theFairy.queen()) {
			EntityFairy ruler = (EntityFairy) this.theFairy.getRuler();
			if (ruler != null) {
				if (ruler.isDead) {
					this.theFairy.setRuler(null);
					return true;
				}
			} else {
				return true;
			}
		}
		return false;
	}

	@Override
	public void updateTask() {
		// Look for a ruler, ot another fairy with no ruler
		// List<EntityFairy> fairyList = this.theFairy.findFairies(false,true);
		EntityFairy aFairy = null;
		EntityFairy newQueen = null;
//		for (int i = 0; i < fairyList.size(); i++) {
//			aFairy = fairyList.get(i);
//			int job = aFairy.getJob();
//			if (this.theFairy != aFairy) {
//				if (aFairy.queen() || job == 0) {
//					newQueen = aFairy;
//
//				} else if (this.theFairy.getFaction() > 0
//						&& this.theFairy.getFaction() == aFairy.getFaction()) {
//					if (aFairy.getRuler() != null) {
//						newQueen = (EntityFairy) aFairy.getRuler();
//					}
//				}
//
//				if (newQueen != null && newQueen.registerFactionMember(this.theFairy)) {
//					this.theFairy.setRuler(newQueen);
//					break;
//				}
//			}
//		}
	}

}
