package fairies.client.render;

import fairies.entity.EntityFairy;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

public class LayerFairyProps implements LayerRenderer<EntityFairy> {

	private final RenderFairy renderer;
	private final ModelFairyProps model = new ModelFairyProps();

	public LayerFairyProps(RenderFairy livingEntityRendererIn) {
		this.renderer = livingEntityRendererIn;
	}

	@Override
	public void doRenderLayer(EntityFairy entitylivingbaseIn, float limbSwing, float limbSwingAmount,
			float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		// TODO Auto-generated method stub
		if (!entitylivingbaseIn.rogue()) {
			this.renderer.bindTexture(RenderFairy.getRes("fairy_props"));
			this.model.sinage = entitylivingbaseIn.sinage;
			this.model.flymode = entitylivingbaseIn.flymode();
			this.model.isSneak = entitylivingbaseIn.isSneaking();
			//this.model.isSneak = entitylivingbaseIn.getFlag(1);
			this.model.jobType = entitylivingbaseIn.getJob() - 1;
			this.model.setModelAttributes(this.renderer.getMainModel());
			this.model.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		}
	}

	@Override
	public boolean shouldCombineTextures() {
		// TODO Auto-generated method stub
		return false;
	}
}
