package fairies.client.render;

import fairies.entity.FairyEntityFishHook;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RenderFish extends Render<FairyEntityFishHook> {
  protected static final ResourceLocation texture =
      new ResourceLocation("textures/particle/particles.png");

  public RenderFish(RenderManager renderManager) { super(renderManager); }

  @Override
  public void doRender(FairyEntityFishHook entityFishHook, double x, double y,
                       double z, float entityYaw, float partialTicks) {
    GlStateManager.pushMatrix();
    GlStateManager.translate((float)x, (float)y, (float)z);
    GlStateManager.enableRescaleNormal();
    GlStateManager.scale(0.5F, 0.5F, 0.5F);
    this.bindEntityTexture(entityFishHook);
    int i = 1;
    int j = 2;
    float f = (float)(i * 8 + 0) / 128.0F;
    float f1 = (float)(i * 8 + 8) / 128.0F;
    float f2 = (float)(j * 8 + 0) / 128.0F;
    float f3 = (float)(j * 8 + 8) / 128.0F;
    float f4 = 1.0F;
    float f5 = 0.5F;
    float f6 = 0.5F;
    GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F,
                          0.0F);
    GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder worldrenderer = tessellator.getBuffer();
    worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
    worldrenderer.pos((double)(0.0F - f5), (double)(0.0F - f6), 0.0D)
        .tex((double)f, (double)f3)
        .normal(0.0F, 1.0F, 0.0F)
        .endVertex();
    worldrenderer.pos((double)(f4 - f5), (double)(0.0F - f6), 0.0D)
        .tex((double)f1, (double)f3)
        .normal(0.0F, 1.0F, 0.0F)
        .endVertex();
    worldrenderer.pos((double)(f4 - f5), (double)(1.0F - f6), 0.0D)
        .tex((double)f1, (double)f2)
        .normal(0.0F, 1.0F, 0.0F)
        .endVertex();
    worldrenderer.pos((double)(0.0F - f5), (double)(1.0F - f6), 0.0D)
        .tex((double)f, (double)f2)
        .normal(0.0F, 1.0F, 0.0F)
        .endVertex();
    tessellator.draw();
    GlStateManager.disableRescaleNormal();
    GlStateManager.popMatrix();

    if (entityFishHook.angler != null) {
      float f7 = (entityFishHook.angler.prevRotationYaw +
                  (entityFishHook.angler.rotationYaw -
                   entityFishHook.angler.prevRotationYaw) *
                      partialTicks) *
                 (float)Math.PI / 180.0F;
      float w = MathHelper.sin(MathHelper.sqrt(f7) * (float)Math.PI);
      Vec3d vec3d = new Vec3d(-0.36D, 0.03D, 0.35D);
      vec3d = vec3d.rotatePitch(-(entityFishHook.angler.prevRotationPitch +
                                  (entityFishHook.angler.rotationPitch -
                                   entityFishHook.angler.prevRotationPitch) *
                                      partialTicks) *
                                (float)Math.PI / 180.0F);
      vec3d = vec3d.rotateYaw(-(entityFishHook.angler.prevRotationYaw +
                                (entityFishHook.angler.rotationYaw -
                                 entityFishHook.angler.prevRotationYaw) *
                                    partialTicks) *
                              (float)Math.PI / 180.0F);
      vec3d = vec3d.rotateYaw(w * 0.5F);
      vec3d = vec3d.rotatePitch(-w * 0.7F);
      double d0 =
          entityFishHook.angler.prevPosX +
          (entityFishHook.angler.posX - entityFishHook.angler.prevPosX) *
              (double)partialTicks +
          vec3d.x;
      double d1 =
          entityFishHook.angler.prevPosY +
          (entityFishHook.angler.posY - entityFishHook.angler.prevPosY) *
              (double)partialTicks +
          vec3d.y;
      double d2 =
          entityFishHook.angler.prevPosZ +
          (entityFishHook.angler.posZ - entityFishHook.angler.prevPosZ) *
              (double)partialTicks +
          vec3d.z;
      double d3 = (double)entityFishHook.angler.getEyeHeight();

      if (this.renderManager.options != null &&
          this.renderManager.options.thirdPersonView > 0) {
        float f9 = (entityFishHook.angler.prevRenderYawOffset +
                    (entityFishHook.angler.renderYawOffset -
                     entityFishHook.angler.prevRenderYawOffset) *
                        partialTicks) *
                   (float)Math.PI / 180.0F;
        double d4 = (double)MathHelper.sin(f9);
        double d6 = (double)MathHelper.cos(f9);
        d0 = entityFishHook.angler.prevPosX +
             (entityFishHook.angler.posX - entityFishHook.angler.prevPosX) *
                 (double)partialTicks -
             d6 * 0.16D - d4 * 0.6D;
        d1 = entityFishHook.angler.prevPosY + d3 +
             (entityFishHook.angler.posY - entityFishHook.angler.prevPosY) *
                 (double)partialTicks +
             0.74D;
        d2 = entityFishHook.angler.prevPosZ +
             (entityFishHook.angler.posZ - entityFishHook.angler.prevPosZ) *
                 (double)partialTicks -
             d4 * 0.16D + d6 * 0.6D;
        d3 = entityFishHook.angler.isSneaking() ? -0.1875D : 0.0D;
      }

      double d13 = entityFishHook.prevPosX +
                   (entityFishHook.posX - entityFishHook.prevPosX) *
                       (double)partialTicks;
      double d5 = entityFishHook.prevPosY +
                  (entityFishHook.posY - entityFishHook.prevPosY) *
                      (double)partialTicks +
                  0.25D;
      double d7 = entityFishHook.prevPosZ +
                  (entityFishHook.posZ - entityFishHook.prevPosZ) *
                      (double)partialTicks;
      double d9 = (double)((float)(d0 - d13));
      double d11 = (double)((float)(d1 - d5)) + d3;
      double d12 = (double)((float)(d2 - d7));
      GlStateManager.disableTexture2D();
      GlStateManager.disableLighting();
      worldrenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
      int k = 16;

      for (int l = 0; l <= k; ++l) {
        float f10 = (float)l / (float)k;
        worldrenderer
            .pos(x + d9 * (double)f10,
                 y + d11 * (double)(f10 * f10 + f10) * 0.5D + 0.25D,
                 z + d12 * (double)f10)
            .color(0, 0, 0, 255)
            .endVertex();
      }

      tessellator.draw();
      GlStateManager.enableLighting();
      GlStateManager.enableTexture2D();
      super.doRender(entityFishHook, x, y, z, entityYaw, partialTicks);
    }
  }

  @Override
  protected ResourceLocation
  getEntityTexture(FairyEntityFishHook entityFishHook) {
    return texture;
  }
}