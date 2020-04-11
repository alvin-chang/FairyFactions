package fairies.client.render;

import fairies.entity.EntityFairy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class LayerFairyRogue implements LayerRenderer<EntityFairy> {

	private final RenderFairy renderer;
	private final ModelFairyProps2 model = new ModelFairyProps2();

	public LayerFairyRogue(RenderFairy livingEntityRendererIn) {
		this.renderer = livingEntityRendererIn;
	}

	@Override
	public void doRenderLayer(EntityFairy entitylivingbaseIn, float limbSwing, float limbSwingAmount,
			float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		// TODO Auto-generated method stub
		if (!entitylivingbaseIn.queen() && !entitylivingbaseIn.normal() && entitylivingbaseIn.rogue()) {
			GlStateManager.pushMatrix();
			GlStateManager.rotate(180, 0, 0, 1);
			GlStateManager.scale(0.6, 0.6, 0.6);
			GlStateManager.rotate((ageInTicks) / 20.0F * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(16, entitylivingbaseIn.height - 0.3, 0);
			Minecraft.getMinecraft().getRenderItem().renderItem(new ItemStack(Items.APPLE),
					ItemCameraTransforms.TransformType.FIXED);
			GlStateManager.popMatrix();
			this.renderer.bindTexture(RenderFairy.getRes("fairy_props2"));

			this.model.flymode = entitylivingbaseIn.flymode();
			this.model.retract = 0F;
			this.model.isSneak = entitylivingbaseIn.isSneaking();
			this.model.sinage = entitylivingbaseIn.sinage;
			this.model.venom = entitylivingbaseIn.canHeal();
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
