package fairies.ai;

import fairies.entity.EntityFairy;
import net.minecraft.entity.ai.EntityAIBase;
import java.util.Random;

public class FairyAIBase extends EntityAIBase {

    protected EntityFairy theFairy;
    protected double speed;
    protected Random rand;

    public FairyAIBase(EntityFairy fairy, double speedIn) {
        this.theFairy = fairy;
        this.speed = speedIn;
        this.rand = fairy.getRNG();

    }

    @Override
    public boolean shouldExecute() {
        // TODO Auto-generated method stub
        if (this.rand.nextBoolean()) {
            return true;
        }
        return false;
    }
}