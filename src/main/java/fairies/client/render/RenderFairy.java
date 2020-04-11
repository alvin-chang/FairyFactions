package fairies.client.render;

import com.google.common.collect.Maps;
import fairies.Version;
import fairies.entity.EntityFairy;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import org.lwjgl.opengl.GL11;

public class RenderFairy extends RenderLiving<EntityFairy> {
    /**
     * TODO: Move into a more appropriate utility class.
     */
    protected static final Map<String, ResourceLocation> resMap = Maps.newHashMap();

    protected static ResourceLocation getRes(String key) {
        if (!resMap.containsKey(key)) {
            final ResourceLocation res = new ResourceLocation(Version.ASSET_PREFIX,
                    "textures/entities/" + key + ".png");
            resMap.put(key, res);
            return res;
        } else {
            return resMap.get(key);
        }
    }
    
   
    public RenderFairy(RenderManager renderManager, ModelFairy modelfairy, float f) {
        super(renderManager, modelfairy, f);

        this.addLayer(new LayerHeldItem(this));
        this.addLayer(new LayerFairyRogue(this));
        this.addLayer(new LayerFairyProps(this));
        this.addLayer(new LayerFairyEyes(this));
        this.addLayer(new LayerFairyWither(this));
        fairyModel = modelfairy;
       // fairyModel5 = new ModelFairyProps2();
    }

    @Override
    protected void preRenderCallback(EntityFairy fairy, float f) {
        float f1 = 0.875F;
        fairyModel.sinage = fairy.sinage;
        fairyModel.flymode = fairy.flymode();
        fairyModel.showCrown = fairy.isTamed() || fairy.queen();
        fairyModel.jobType = fairy.getJob();
        fairyModel.isSneak = fairy.isSneaking();
        fairyModel.scoutWings = fairy.scout();
        fairyModel.rogueParts = fairy.rogue();
        fairyModel.hairType = fairy.hairType();
        GL11.glScalef(f1, f1, f1);

        if (fairy.isSneaking()) {
            GL11.glTranslatef(0F, (5F / 16F), 0F);
        }
    }

    /*
     * NOTE: Replaced by LayerHeldItem
     *
     * @Override protected void renderEquippedItems(EntityFairy entityliving, float
     * f) { ItemStack itemstack = entityliving.getHeldItem();
     * 
     * if (itemstack != null) { GL11.glPushMatrix();
     * fairyModel.bipedRightArm.postRender(0.0625F); GL11.glTranslatef(0.0F, 0.1F,
     * 0.0F);
     * 
     * if( itemstack.getItem() instanceof ItemBlock &&
     * RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemstack.getItem()).
     * getRenderType()) ) { float f1 = 0.5F; GL11.glTranslatef(0.0F, 0.1875F,
     * -0.3125F); f1 *= 0.75F; GL11.glRotatef(20F, 1.0F, 0.0F, 0.0F);
     * GL11.glRotatef(45F, 0.0F, 1.0F, 0.0F); GL11.glScalef(f1, -f1, f1); } else if
     * (itemstack.getItem().isFull3D()) { float f2 = 0.625F; GL11.glTranslatef(0.0F,
     * 0.1875F, 0.0F); GL11.glScalef(f2, -f2, f2); GL11.glRotatef(-100F, 1.0F, 0.0F,
     * 0.0F); GL11.glRotatef(45F, 0.0F, 1.0F, 0.0F); } else { float f3 = 0.375F;
     * GL11.glTranslatef(0.25F, 0.1875F, -0.1875F); GL11.glScalef(f3, f3, f3);
     * GL11.glRotatef(60F, 0.0F, 0.0F, 1.0F); GL11.glRotatef(-90F, 1.0F, 0.0F,
     * 0.0F); GL11.glRotatef(20F, 0.0F, 0.0F, 1.0F); }
     * 
     * if (itemstack.getItem() == Items.potionitem) { int j =
     * itemstack.getItem().getColorFromItemStack(itemstack, 0); float f9 = (float)(j
     * >> 16 & 0xff) / 255F; float f10 = (float)(j >> 8 & 0xff) / 255F; float f11 =
     * (float)(j & 0xff) / 255F; GL11.glColor4f(f9, f10, f11, 1.0F);
     * renderManager.itemRenderer.renderItem(entityliving, itemstack, 0);
     * GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
     * renderManager.itemRenderer.renderItem(entityliving, itemstack, 1); } else {
     * renderManager.itemRenderer.renderItem(entityliving, itemstack, 0); }
     * 
     * GL11.glPopMatrix(); } }
     */

    /**
     *
     * TODO: Update to use new LayerRenderer system
     *
     *
     * @Override protected int shouldRenderPass(EntityFairy fairy, int i, float f) {
     *           if (i == 0 && (fairy.withered() || fairy.rogue())) //Render
     *           Withered Skin. { float transp = 0.7F;
     * 
     *           if (fairy.queen()) { if (fairy.getSkin() > 1) {
     *           bindTexture(getRes("fairyWithered3")); } else {
     *           bindTexture(getRes("fairyWithered2")); } } else {
     *           bindTexture(getRes("fairyWithered1")); }
     * 
     *           setRenderPassModel(fairyModel4); fairyModel4.sinage = fairy.sinage;
     *           fairyModel4.flymode = fairy.flymode(); fairyModel4.showCrown =
     *           fairy.tamed() || fairy.queen(); fairyModel4.isSneak =
     *           fairy.isSneaking(); fairyModel4.scoutWings = fairy.scout();
     *           fairyModel4.swingProgress = fairyModel.swingProgress;
     *           fairyModel4.rogueParts = fairy.rogue(); fairyModel4.hairType =
     *           fairy.hairType(); GL11.glColor4f(0.7F, 0.7F, 0.7F, transp);
     *           GL11.glEnable(GL11.GL_BLEND); GL11.glBlendFunc(GL11.GL_SRC_ALPHA,
     *           GL11.GL_ONE_MINUS_SRC_ALPHA); return 1; } else if (i == 1) //Render
     *           Fairy Eyes. { bindTexture(fairy.getTexture(fairy.getSkin())); float
     *           transp = 1.0F - ((float)fairy.getHealth() /
     *           (float)(fairy.getMaxHealth()));
     * 
     *           if (transp < 0.1F) { return -1; }
     * 
     *           setRenderPassModel(fairyModel3); fairyModel3.flymode =
     *           fairy.flymode(); GL11.glColor4f(1.0F, 1.0F, 1.0F, transp);
     *           GL11.glEnable(GL11.GL_BLEND); GL11.glBlendFunc(GL11.GL_SRC_ALPHA,
     *           GL11.GL_ONE_MINUS_SRC_ALPHA); return 1; } else if (i == 2 &&
     *           !fairy.queen() && !fairy.normal()) //Render Armor Overlay. {
     *           GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
     *           GL11.glDisable(GL11.GL_BLEND);
     * 
     *           if (fairy.rogue()) { setRenderPassModel(fairyModel5);
     *           bindTexture(getRes("fairyProps2")); fairyModel5.flymode =
     *           fairy.flymode(); fairyModel5.retract = 0F; fairyModel5.isSneak =
     *           fairy.isSneaking(); fairyModel5.sinage = fairy.sinage;
     *           fairyModel5.swingProgress = fairyModel.swingProgress;
     *           fairyModel5.venom = fairy.canHeal(); } else {
     *           setRenderPassModel(fairyModel2); bindTexture(getRes("fairyProps"));
     *           fairyModel2.flymode = fairy.flymode(); fairyModel2.jobType =
     *           fairy.getJob() - 1; fairyModel2.isSneak = fairy.isSneaking();
     *           fairyModel2.sinage = fairy.sinage; fairyModel2.swingProgress =
     *           fairyModel.swingProgress; }
     * 
     *           return 1; } else { GL11.glDisable(GL11.GL_BLEND); return -1; } }
     */

    /*
     * @Override public void passSpecialRender(EntityFairy fairy, double d, double
     * d1, double d2) { renderFairyName(fairy, d, d1, d2); }
     */

    @Override
    public boolean canRenderName(EntityFairy fairy) {
        return true;
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityFairy fairy) {
        return this.getRes(fairy.getTextureName(fairy.getSkin()));
    }
 

    protected ModelFairy fairyModel, fairyModel4; // Body and withered overlay
    protected ModelFairyProps fairyModel2; // Clothes and stuff
    protected ModelFairyEyes fairyModel3; // Eyes
    protected ModelFairyProps2 fairyModel5; // Rogue Clothes
}
