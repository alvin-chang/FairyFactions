package fairies.client.render;

import fairies.entity.EntityFairy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelLlama;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class LayerFairyEyes implements LayerRenderer<EntityFairy> {

	private final RenderFairy renderer;
	private final ModelFairyEyes model = new ModelFairyEyes();

	public LayerFairyEyes(RenderFairy livingEntityRendererIn) {
		this.renderer = livingEntityRendererIn;
	}

	@Override
	public void doRenderLayer(EntityFairy entitylivingbaseIn, float limbSwing, float limbSwingAmount,
			float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		// TODO Auto-generated method stub
		if (!entitylivingbaseIn.rogue() && !entitylivingbaseIn.queen()) {
			this.renderer.bindTexture(RenderFairy.getRes("fairy_props"));
			this.model.flymode = entitylivingbaseIn.flymode();
			this.model.isSneak = entitylivingbaseIn.isSneaking();
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
