package fairies.client.render;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import fairies.FairyFactions;

@SideOnly(Side.CLIENT)
public class ModelFairy extends ModelBiped {
	public ModelFairy() {
		this(0.0F);
	}

	public ModelFairy(final float modelSize) {
		this(modelSize, 0.0F);
	}

	public ModelFairy(final float f, final float f1) {
		this.leftArmPose = ArmPose.EMPTY;
		this.rightArmPose = ArmPose.EMPTY;
		isSneak = false;
		flymode = showCrown = false;
		bipedHead = new ModelRenderer(this, 0, 0);
		bipedHead.addBox(-3F, -6F, -3F, 6, 6, 6, f);
		bipedHead.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
		strand = (new ModelRenderer(this)).setTextureSize((byte) 64, (byte) 32);
		strand.setTextureOffset(0, 20).addBox(-3F, -5F, 3F, 6, 3, 1, f);
		strand.setTextureOffset(24, 0).addBox(-4F, -5F, -3F, 1, 3, 6, f);
		strand.setTextureOffset(24, 0).addBox(3F, -5F, -3F, 1, 3, 6, f);
		strand.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
		strand2 = (new ModelRenderer(this)).setTextureSize((byte) 64, (byte) 32);
		strand2.setTextureOffset(13, 23).addBox(-5F, -2.5F, 1.5F, 10, 3, 3, f);
		strand2.setRotationPoint(0F, 0F + f1, 0F);
		strand.addChild(strand2);
		strand3 = (new ModelRenderer(this)).setTextureSize((byte) 64, (byte) 32);
		strand3.setTextureOffset(13, 23).addBox(-3F, -1.5F, -1.5F, 3, 3, 3, f - 0.5F);
		strand3.setTextureOffset(13, 23).addBox(-5.25F, -1.5F, -1.5F, 3, 3, 3, f - 0.25F);
		strand3.setRotationPoint(-2F, -1.75F + f1, 3F);
		strand3.rotateAngleZ = -1.0F;
		strand3.rotateAngleY = 0.5F;
		strand3.isHidden = true;
		strand.addChild(strand3);
		strand4 = (new ModelRenderer(this)).setTextureSize((byte) 64, (byte) 32);
		strand4.mirror = true;
		strand4.setTextureOffset(13, 23).addBox(0F, -1.5F, -1.5F, 3, 3, 3, f - 0.5F);
		strand4.setTextureOffset(13, 23).addBox(2.25F, -1.5F, -1.5F, 3, 3, 3, f - 0.25F);
		strand4.setRotationPoint(2F, -1.75F + f1, 3F);
		strand4.rotateAngleZ = 1.0F;
		strand4.rotateAngleY = -0.5F;
		strand4.isHidden = true;
		strand.addChild(strand4);
		// jaw = new ModelRenderer(this, "jaw");
		// jaw.setRotationPoint(0.0F, 4F, 8F + f);
		// jaw.addBox("jaw", -6F, 0.0F, -16F, 12, 4, 16);
		// head.addChild(jaw);
		crown = new ModelRenderer(this, 37, 14);
		crown.addBox(-3F, -6.75F, -3F, 6, 3, 6, f + 0.25F);
		crown.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
		bipedBody = (new ModelRenderer(this)).setTextureSize((byte) 64, (byte) 32);
		bipedBody.setTextureOffset(8, 12).addBox(-2F, 0.0F, -1F, 4, 6, 2, f);
		bipedBody.setTextureOffset(15, 20).addBox(-2F, 1.0F, -2F, 4, 2, 1, f);
		bipedBody.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
		wingRight = new ModelRenderer(this, 27, 9);
		wingRight.addBox(0F, -0.75F, -1.0F, 5, 4, 1, f + 0.25F);
		wingRight.setRotationPoint(0.5F, 0.0F + f1, 1.0F);
		wingLeft = new ModelRenderer(this, 27, 9);
		wingLeft.mirror = true;
		wingLeft.addBox(-5F, -0.75F, -1.0F, 5, 4, 1, f + 0.25F);
		wingLeft.setRotationPoint(-0.5F, 0.0F + f1, 1.0F);
		bipedRightArm = new ModelRenderer(this, 0, 12);
		bipedRightArm.addBox(-1F, -1F, -1F, 2, 6, 2, f);
		bipedRightArm.setRotationPoint(-5F, 1.0F + f1, 0.0F);
		bipedLeftArm = new ModelRenderer(this, 0, 12);
		bipedLeftArm.mirror = true;
		bipedLeftArm.addBox(-1F, -1F, -1F, 2, 6, 2, f);
		bipedLeftArm.setRotationPoint(5F, 1.0F + f1, 0.0F);
		bipedRightLeg = new ModelRenderer(this, 20, 12);
		bipedRightLeg.addBox(-1F, 0.0F, -1F, 2, 6, 2, f);
		bipedRightLeg.setRotationPoint(-1F, 18F + f1, 0.0F);
		bipedLeftLeg = new ModelRenderer(this, 20, 12);
		bipedLeftLeg.mirror = true;
		bipedLeftLeg.addBox(-1F, 0.0F, -1F, 2, 6, 2, f);
		bipedLeftLeg.setRotationPoint(1.0F, 18F + f1, 0.0F);
	}

	@Override
	public void render(final Entity entityIn, final float limbSwing, final float limbSwingAmount,
			final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale) {
		GL11.glPushMatrix();
		setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		bipedHead.render(scale);
		bipedBody.render(scale);
		bipedRightArm.render(scale);
		bipedLeftArm.render(scale);
		bipedRightLeg.render(scale);
		bipedLeftLeg.render(scale);

		if (!rogueParts) {
			strand2.isHidden = hairType;
			strand3.isHidden = !hairType;
			strand4.isHidden = !hairType;
			strand.render(scale);
		}

		if (showCrown && !rogueParts) {
			crown.render(scale);
		}

		if (!scoutWings && !rogueParts) {
			wingLeft.render(scale);
			wingRight.render(scale);
		}

		GL11.glPopMatrix();
	}

	public void setRotationAngles(final float limbSwing, final float limbSwingAmount, final float f2, final float f3,
			final float f4, final float f5) {
		bipedHead.rotateAngleY = f3 / (180F / (float) Math.PI);
		bipedHead.rotateAngleX = f4 / (180F / (float) Math.PI);
		strand.rotateAngleY = bipedHead.rotateAngleY;
		strand.rotateAngleX = bipedHead.rotateAngleX;
		crown.rotateAngleY = bipedHead.rotateAngleY;
		crown.rotateAngleX = bipedHead.rotateAngleX;

		if (!flymode) {
			bipedRightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount
					* 0.5F;
			bipedLeftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
			bipedRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
			bipedLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
		} else {
			bipedRightArm.rotateAngleX = 0.0F;
			bipedLeftArm.rotateAngleX = 0.0F;
			bipedRightLeg.rotateAngleX = 0.0F;
			bipedLeftLeg.rotateAngleX = 0.0F;
		}

		bipedRightArm.rotateAngleZ = 0.05F;
		bipedLeftArm.rotateAngleZ = -0.05F;
		bipedRightLeg.rotateAngleY = 0.0F;
		bipedLeftLeg.rotateAngleY = 0.0F;
		bipedRightLeg.rotateAngleZ = 0.0F;
		bipedLeftLeg.rotateAngleZ = 0.0F;

		if ((isRiding || isSneak) && !flymode) {
			bipedRightArm.rotateAngleX += -((float) Math.PI / 5F);
			bipedLeftArm.rotateAngleX += -((float) Math.PI / 5F);
			bipedRightLeg.rotateAngleX = -((float) Math.PI * 2F / 5F);
			bipedLeftLeg.rotateAngleX = -((float) Math.PI * 2F / 5F);
			bipedRightLeg.rotateAngleY = ((float) Math.PI / 10F);
			bipedLeftLeg.rotateAngleY = -((float) Math.PI / 10F);

			if (isSneak) {
				bipedRightLeg.rotateAngleX = -((float) Math.PI / 2F);
				bipedLeftLeg.rotateAngleX = -((float) Math.PI / 2F);
			}
		}

		if (this.leftArmPose != ArmPose.EMPTY) {
			bipedLeftArm.rotateAngleX = bipedLeftArm.rotateAngleX * 0.5F - ((float) Math.PI / 10F);
		}

		if (this.rightArmPose != ArmPose.EMPTY) {
			bipedRightArm.rotateAngleX = bipedRightArm.rotateAngleX * 0.5F - ((float) Math.PI / 10F);
		}

		bipedRightArm.rotateAngleY = 0.0F;
		bipedLeftArm.rotateAngleY = 0.0F;

		if (swingProgress > -9990F) {
			float f6 = swingProgress;
			bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt(f6) * (float) Math.PI * 2.0F) * 0.2F;
			wingLeft.rotateAngleY = wingRight.rotateAngleY = MathHelper
					.sin(MathHelper.sqrt(f6) * (float) Math.PI * 2.0F) * 0.2F;
			bipedRightArm.rotationPointZ = MathHelper.sin(bipedBody.rotateAngleY) * 5F;
			bipedRightArm.rotationPointX = -MathHelper.cos(bipedBody.rotateAngleY) * 5F + 2.0F;
			bipedLeftArm.rotationPointZ = -MathHelper.sin(bipedBody.rotateAngleY) * 5F;
			bipedLeftArm.rotationPointX = MathHelper.cos(bipedBody.rotateAngleY) * 5F - 2.0F;
			bipedRightArm.rotateAngleY += bipedBody.rotateAngleY;
			bipedLeftArm.rotateAngleY += bipedBody.rotateAngleY;
			bipedLeftArm.rotateAngleX += bipedBody.rotateAngleY;
			f6 = 1.0F - swingProgress;
			f6 *= f6;
			f6 *= f6;
			f6 = 1.0F - f6;
			final float f8 = MathHelper.sin(f6 * (float) Math.PI);
			final float f9 = MathHelper.sin(swingProgress * (float) Math.PI) * -(bipedHead.rotateAngleX - 0.7F) * 0.75F;
			bipedRightArm.rotateAngleX -= f8 * 1.2D + f9;
			bipedRightArm.rotateAngleY += bipedBody.rotateAngleY * 2.0F;
			bipedRightArm.rotateAngleZ = MathHelper.sin(swingProgress * (float) Math.PI) * -0.4F;
		}

		if (flymode) {
			final float f7 = (float) Math.PI;
			bipedBody.rotateAngleX = f7 / 2.0F;
			bipedBody.rotationPointY = 19F;
			wingLeft.rotateAngleX = f7 / 2.0F;
			wingRight.rotateAngleX = f7 / 2.0F;
			wingLeft.rotationPointY = 17.5F;
			wingRight.rotationPointY = 17.5F;
			wingLeft.rotationPointZ = 1.0F;
			wingRight.rotationPointZ = 1.0F;
			bipedRightLeg.rotationPointZ = 0.0F;
			bipedLeftLeg.rotationPointZ = 0.0F;
			bipedRightArm.rotationPointY = 19F;
			bipedLeftArm.rotationPointY = 19F;
			bipedRightLeg.rotationPointY = 18F;
			bipedLeftLeg.rotationPointY = 18F;
			bipedRightLeg.rotationPointZ = 6F;
			bipedLeftLeg.rotationPointZ = 6F;
			bipedHead.rotationPointZ = -3F;
			bipedHead.rotationPointY = 19.75F;
			strand.rotationPointZ = -3F;
			strand.rotationPointY = 19.75F;
			crown.rotationPointZ = -3F;
			crown.rotationPointY = 19.75F;
		} else {
			bipedBody.rotateAngleX = 0.0F;
			bipedBody.rotationPointY = 12F;
			wingLeft.rotateAngleX = 0.0F;
			wingRight.rotateAngleX = 0.0F;
			wingLeft.rotationPointY = 12.5F;
			wingRight.rotationPointY = 12.5F;
			wingLeft.rotationPointZ = 1.0F;
			wingRight.rotationPointZ = 1.0F;
			bipedRightLeg.rotationPointZ = 0.0F;
			bipedLeftLeg.rotationPointZ = 0.0F;

			if (isRiding) {
				bipedRightArm.rotationPointY = 13F;
				bipedLeftArm.rotationPointY = 13F;
			} else {
				bipedRightArm.rotationPointY = 13F;
				bipedLeftArm.rotationPointY = 13F;
			}

			bipedRightLeg.rotationPointY = 18F;
			bipedLeftLeg.rotationPointY = 18F;
			bipedRightLeg.rotationPointZ = 0.0F;
			bipedLeftLeg.rotationPointZ = 0.0F;
			bipedHead.rotationPointZ = 0.0F;
			bipedHead.rotationPointY = 12F;
			strand.rotationPointZ = 0.0F;
			strand.rotationPointY = 12F;
			crown.rotationPointZ = 0.0F;
			crown.rotationPointY = 12F;
		}

		if (flymode) {
			bipedRightArm.rotateAngleZ += MathHelper.cos(f2 * 0.09F) * 0.1F + 0.05F;
			bipedLeftArm.rotateAngleZ -= MathHelper.cos(f2 * 0.09F) * 0.1F + 0.05F;
			bipedRightArm.rotateAngleX += MathHelper.sin(f2 * 0.067F) * 0.1F;
			bipedLeftArm.rotateAngleX -= MathHelper.sin(f2 * 0.067F) * 0.1F;
			bipedRightLeg.rotateAngleZ += MathHelper.cos(f2 * 0.09F) * 0.1F + 0.05F;
			bipedLeftLeg.rotateAngleZ -= MathHelper.cos(f2 * 0.09F) * 0.1F + 0.05F;
			bipedRightLeg.rotateAngleX = 0.1F;
			bipedLeftLeg.rotateAngleX = 0.1F;
		} else {
			bipedRightArm.rotateAngleZ += MathHelper.cos(f2 * 0.09F) * 0.05F + 0.05F;
			bipedLeftArm.rotateAngleZ -= MathHelper.cos(f2 * 0.09F) * 0.05F + 0.05F;
			bipedRightArm.rotateAngleX += MathHelper.sin(f2 * 0.067F) * 0.05F;
			bipedLeftArm.rotateAngleX -= MathHelper.sin(f2 * 0.067F) * 0.05F;
		}

		if (flymode) {
			wingLeft.rotateAngleY = 0.1F;
			wingRight.rotateAngleY = -0.1F;
			wingLeft.rotateAngleY += Math.sin(sinage) / 6F;
			wingRight.rotateAngleY -= Math.sin(sinage) / 6F;
			wingLeft.rotateAngleZ = 0.5F;
			wingRight.rotateAngleZ = -0.5F;
		} else {
			wingLeft.rotateAngleY = 0.6F;
			wingRight.rotateAngleY = -0.6F;
			wingLeft.rotateAngleY += Math.sin(sinage) / 3F;
			wingRight.rotateAngleY -= Math.sin(sinage) / 3F;
			wingLeft.rotateAngleZ = 0.125F;
			wingRight.rotateAngleZ = -0.125F;
		}

		wingLeft.rotateAngleZ += Math.cos(sinage) / (flymode ? 3F : 8F);
		wingRight.rotateAngleZ -= Math.cos(sinage) / (flymode ? 3F : 8F);
	}

	@Override
	public void postRenderArm(float scale, EnumHandSide side) {
		FairyFactions.LOGGER.debug("drawing arm overlay");
		ModelRenderer modelrenderer = this.getArmForSide(side);
		float f = 0.5F * (float) (side == EnumHandSide.RIGHT ? 1 : -1);
		// TODO: fix this properly
		modelrenderer.rotationPointX += f;
		if (this.flymode) {
			modelrenderer.postRender(scale * 0.7f);
		} else {
			modelrenderer.postRender(scale * 0.6f);
		}

		modelrenderer.rotationPointX -= f;
	}
	/*
	 * NOTE: Apparently removed in 1.8
	 *
	 * @Override public void renderEars(final float f) { }
	 * 
	 * @Override public void renderCloak(final float f) { }
	 */

	public ModelRenderer strand, strand2, strand3, strand4;
	public ModelRenderer crown;
	public ModelRenderer wingLeft;
	public ModelRenderer wingRight;
	public boolean flymode;
	public boolean showCrown;
	public boolean scoutWings;
	public boolean rogueParts;
	public int jobType;
	public boolean hairType;
	public float sinage;
}
