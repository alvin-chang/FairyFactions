package fairies.entity;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import fairies.FairyConfig;
import fairies.FairyFactions;
import fairies.FairySounds;
import fairies.Loc;
import fairies.Version;
import fairies.ai.FairyAIFear;
import fairies.ai.FairyAIGuard;
import fairies.ai.FairyAIHealing;
import fairies.ai.FairyAIRuler;
import fairies.ai.FairyAISocial;
import fairies.ai.FairyAIWanderAvoidWater;
import fairies.ai.FairyJob;
import fairies.world.FairyGroupGenerator;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSign;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityFlying;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import scala.swing.TextComponent;

public class EntityFairy extends EntityFlappable implements EntityFlying {

	private boolean cower;
	public boolean didHearts;
	public boolean didSwing;
	private boolean wasFishing;
	private int snowballin;

	private int healTime;
	private int cryTime;
	private int loseInterest;
	private int loseTeam;

	private int postX, postY, postZ; // where is our sign?

	private EntityLivingBase ruler;
	private List<EntityFairy> factionMembers = Lists.newArrayList();;
	private EntityLivingBase entityHeal;
	private EntityLivingBase entityFear;

	public static final Logger LOGGER = LogManager.getFormatterLogger(Version.MOD_ID);
	/**
	 * NEW - replaces field deprecated since 1.1 that was finally made private in
	 * 1.8
	 */
	@Deprecated
	private EntityLivingBase entityToAttack;
	public FairyEntityFishHook fishEntity;

	// TODO: deal with this in AI rewrite
	@Deprecated
	public EntityLivingBase getEntityToAttack() {
		return entityToAttack;
	}

	/** NEW - replaces field removed in 1.8, any reference is likely broken */
	@Deprecated
	public int attackTime;

	// non-persistent fields
	public float sinage; // what does this mean?
	// private boolean flag; // flagged for what, precisely?
	private boolean createGroup;
	public int witherTime;
	private ItemStack tempItem;
	private boolean isSwinging;
	private int particleCount;

	public EntityFairy(World world) {
		super(world);
		this.setSize(0.6F, 0.85F);
	}

	@Override
	protected void initEntityAI() {
		this.aiSit = new EntityAISit(this);

		this.tasks.addTask(0, new EntityAISwimming(this));
		// this.tasks.addTask(1, new EntityAIPanic(this, 1.25D));
		this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
		this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
		// this.tasks.addTask(2, new FairyJob(this));
		// this.tasks.addTask(1, new EntityAIWatchClosest(this, EntityPlayer.class,
		// 8.0F));
		this.tasks.addTask(3, this.aiSit);
		// this.tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
		// this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.0D, true));
		this.tasks.addTask(4, new FairyAIHealing(this, 0.25D));
		this.tasks.addTask(5, new EntityAIFollowOwner(this, 0.3D, 10.0F, 2.0F));
		this.tasks.addTask(6, new FairyAIGuard(this, true, true));
//		this.tasks.addTask(6, new FairyAIWanderAvoidWater(this, 0.2D));

		// this.tasks.addTask(6, new EntityAIMate(this, 1.0D));
		// this.tasks.addTask(8, new EntityAIBeg(this, 8.0F));
		this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
		this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));

		this.tasks.addTask(10, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new FairyAIRuler(this, 1.0D));
		this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true, new Class[0]));

		this.targetTasks.addTask(4,
				new EntityAITargetNonTamed(this, EntityLivingBase.class, false, new Predicate<Entity>() {
					public boolean apply(Entity p_apply_1_) {
						return p_apply_1_ instanceof EntityCreeper || p_apply_1_ instanceof EntitySlime;
					}
				}));
		/*
		 * 
		 * 
		 * this.targetTasks.addTask(5, new EntityAINearestAttackableTarget(this,
		 * EntitySkeleton.class, false));
		 */

		// this.targetTasks.addTask(1, new FairyAISocial(this, 1.0D));
		// this.targetTasks.addTask(2, new FairyAIFear(this, 2.0D));
		super.initEntityAI();
		// this.setNameEnabled(false);
		// this.setAIMoveSpeed(FairyConfig.GENERAL_SPEED_BASE);
	}

	@Override
	protected PathNavigate createNavigator(World worldIn) {
		PathNavigateGround pathnavigateground = new PathNavigateGround(this, worldIn);
		pathnavigateground.setCanSwim(true);
		pathnavigateground.setEnterDoors(true);
		return pathnavigateground;
	}

	@Override
	public float getEyeHeight() {
		if (!world.isRemote && this.onGround && rand.nextBoolean()) {
			int a = MathHelper.floor(posX);
			int b = MathHelper.floor(getEntityBoundingBox().minY);
			int c = MathHelper.floor(posZ);

			if (isAirySpace(a, b, c) && isAirySpace(a, b + 1, c)) {
				return height * 1.375F;
			}
		}
		return super.getEyeHeight();
	}

	// Obfuscated name lookups
	private static final String[] MCP_ISJUMPING = { "isJumping", "field_70703_bu" };
	private static final String[] MCP_ONIMPACT = { "onImpact", "func_70184_a" };

	// DataWatcher object indices
	// private static final DataParameter<Boolean> B_FLAGS =
	// EntityDataManager.createKey(EntityFairy.class, DataSerializers.BYTE);
	protected static final DataParameter<Optional<UUID>> RULER_UNIQUE_ID = EntityDataManager
			.createKey(EntityFairy.class, DataSerializers.OPTIONAL_UNIQUE_ID);

	protected final static DataParameter<Byte> B_FLAGS = EntityDataManager.createKey(EntityFairy.class,
			DataSerializers.BYTE);
	protected final static DataParameter<Byte> B_TYPE = EntityDataManager.createKey(EntityFairy.class,
			DataSerializers.BYTE); // skin, job, faction
	protected final static DataParameter<Byte> B_NAME_ORIG = EntityDataManager.createKey(EntityFairy.class,
			DataSerializers.BYTE); // generated original name
	protected final static DataParameter<Byte> B_FLAGS2 = EntityDataManager.createKey(EntityFairy.class,
			DataSerializers.BYTE); // capabilities, activities, etc...
	protected final static DataParameter<Byte> B_HEALTH = EntityDataManager.createKey(EntityFairy.class,
			DataSerializers.BYTE);; // NB: UNUSED currently - was used in original mod
	protected final static DataParameter<String> S_NAME_REAL = EntityDataManager.createKey(EntityFairy.class,
			DataSerializers.STRING); // custom name
	protected final static DataParameter<Integer> I_TOOL = EntityDataManager.createKey(EntityFairy.class,
			DataSerializers.VARINT); // temporary tool

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(RULER_UNIQUE_ID, Optional.absent());
		dataManager.register(B_FLAGS, new Byte((byte) 0));
		dataManager.register(B_FLAGS2, new Byte((byte) 0));
		dataManager.register(B_TYPE, new Byte((byte) 0));
		dataManager.register(B_HEALTH, new Byte((byte) 0));
		dataManager.register(B_NAME_ORIG, new Byte((byte) 0));
		dataManager.register(S_NAME_REAL, "");
		dataManager.register(I_TOOL, new Integer(0));

		// fairy-specific init
		setSkin(rand.nextInt(4));
		setFaction(0);
		setFairyName(rand.nextInt(16), rand.nextInt(16));
		setSpecialJob(false);
		setJob(rand.nextInt(4));

		setFlymode(false);
		this.sinage = rand.nextFloat();
		this.setFlapEnergy(400 + rand.nextInt(200));
		this.setCower(rand.nextBoolean());
		this.postX = this.postY = this.postZ = -1;
		// LOGGER.debug("Adding Entity at X:" + posX + " Y:" + posY + " Z:" + posZ);
	}

	@SuppressWarnings("unused")
	private void _dump_() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getEntityId());
		sb.append(" : ");
		byte b0 = dataManager.get(B_TYPE);
		byte b1 = dataManager.get(B_FLAGS);
		byte b2 = dataManager.get(B_FLAGS2);

		sb.append(String.format("%8s", Integer.toBinaryString(b0)).replace(' ', '0'));
		sb.append("-");
		sb.append(String.format("%8s", Integer.toBinaryString(b1)).replace(' ', '0'));
		sb.append("-");
		sb.append(String.format("%8s", Integer.toBinaryString(b2)).replace(' ', '0'));

		System.out.println(sb.toString());
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setByte("flags", dataManager.get(B_FLAGS));
		nbt.setByte("flags2", dataManager.get(B_FLAGS2));
		nbt.setByte("type", dataManager.get(B_TYPE));
		nbt.setByte("nameOrig", dataManager.get(B_NAME_ORIG));

		if (this.getRulerId() == null) {
			nbt.setString("ruler", "");
		} else {
			nbt.setString("ruler", this.getRulerId().toString());
		}

		nbt.setString("rulerName", rulerName());
		nbt.setString("customName", getCustomName());
		nbt.setIntArray("post", new int[] { postX, postY, postZ });

		nbt.setShort("flyTime", (short) flapEnergy);
		nbt.setShort("healTime", (short) healTime);
		nbt.setShort("cryTime", (short) cryTime);
		nbt.setShort("loseInterest", (short) loseInterest);
		nbt.setShort("loseTeam", (short) loseTeam);

		nbt.setBoolean("cower", this.cower);
		nbt.setBoolean("didHearts", this.didHearts);
		nbt.setBoolean("didSwing", this.didSwing);

		this.wasFishing = (this.fishEntity != null);
		nbt.setBoolean("wasFishing", this.wasFishing);

		nbt.setShort("snowballin", (short) snowballin);

		nbt.setBoolean("isSitting", isSitting());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		dataManager.set(B_FLAGS, nbt.getByte("flags"));
		dataManager.set(B_FLAGS2, nbt.getByte("flags2"));
		dataManager.set(B_TYPE, nbt.getByte("type"));
		dataManager.set(B_NAME_ORIG, nbt.getByte("nameOrig"));
		String s;
		if (nbt.hasKey("ruler", 8)) {
			s = nbt.getString("ruler");
		} else {
			String s1 = nbt.getString("Owner");
			s = PreYggdrasilConverter.convertMobOwnerIfNeeded(this.getServer(), s1);
		}
		if (!s.isEmpty()) {
			try {
				this.setRulerId(UUID.fromString(s));
			} catch (Throwable var4) {
			}
		}
		// dataManager.set(RULER_UNIQUE_ID,
		// Optional.fromNullable(UUID.fromString(nbt.getString("ruler"))));
		setRulerName(nbt.getString("rulerName"));
		setCustomName(nbt.getString("customName"));
		final int[] post = nbt.getIntArray("post");
		if (post.length > 0) {
			postX = post[0];
			postY = post[1];
			postZ = post[2];
		}

		flapEnergy = nbt.getShort("flyTime");
		healTime = nbt.getShort("healTime");
		cryTime = nbt.getShort("cryTime");
		loseInterest = nbt.getShort("loseInterest");
		loseTeam = nbt.getShort("loseTeam");

		cower = nbt.getBoolean("cower");
		didHearts = nbt.getBoolean("didHearts");
		didSwing = nbt.getBoolean("didSwing");
		wasFishing = nbt.getBoolean("wasFishing");
		snowballin = nbt.getShort("snowballin");

		setSitting(nbt.getBoolean("isSitting"));

		if (!this.world.isRemote) {
			setCanHeal(healTime <= 0);
			setPosted(postY > -1);
		}
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();

		final double max_hp;
		if (this.queen()) {
			max_hp = FairyConfig.GENERAL_HEALTH_QUEEN;
		} else if (this.rogue()) {
			max_hp = FairyConfig.GENERAL_HEALTH_ROGUE;
		} else {
			max_hp = FairyConfig.GENERAL_HEALTH_BASE;
		}
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(max_hp);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(FairyConfig.GENERAL_SPEED_BASE);
		// this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.0D);
		// this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(10.0D);
	}

	@Override
	public double getYOffset() {

		if (getRidingEntity() != null) {
			if (this.world.isRemote) {
				LOGGER.debug("Yword is remote");
				return super.getYOffset() - (flymode() ? 1.15F : 1.35f);
			}
			LOGGER.debug("Yword is not remote");
			return super.getYOffset() + (flymode() ? 0.65F : 0.55F)
					- (getRidingEntity() instanceof EntityChicken ? 0.0F : 0.15F);
		} else {
			LOGGER.debug("Yfallback");
			return this.getYOffset();
		}
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (this.world.isRemote) {
			final Entity currentRidingEntity = this.getRidingEntity();
			updateWithering();
			setHealth(getHealth());
			setFairyClimbing(flymode() && canFlap() && hasPath() && collidedHorizontally);
			if (isSitting() && (currentRidingEntity != null || !onGround)) {
				setSitting(false);
			}

			setPosted(postY > -1);
			/*
			 * if (flymode()) { if (!liftOff() && currentRidingEntity != null &&
			 * !currentRidingEntity.onGround && currentRidingEntity instanceof
			 * EntityLivingBase) { currentRidingEntity.fallDistance = 0F;
			 * 
			 * if (currentRidingEntity.motionY < FairyConfig.DEF_FLOAT_RATE) {
			 * currentRidingEntity.motionY = FairyConfig.DEF_FLOAT_RATE; }
			 * 
			 * final boolean isJumping =
			 * ReflectionHelper.getPrivateValue(EntityLivingBase.class, (EntityLivingBase)
			 * currentRidingEntity, MCP_ISJUMPING); if (isJumping &&
			 * currentRidingEntity.motionY < FairyConfig.DEF_FLAP_RATE && canFlap()) {
			 * currentRidingEntity.motionY = FairyConfig.DEF_FLAP_RATE; } } else { if
			 * (motionY < FairyConfig.DEF_FLOAT_RATE) { motionY =
			 * FairyConfig.DEF_FLOAT_RATE; }
			 * 
			 * if (canFlap() && checkGroundBelow() && motionY < 0D) { motionY =
			 * FairyConfig.DEF_FLOAT_RATE * FairyConfig.DEF_SOLO_FLAP_MULT; }
			 * 
			 * if (liftOff() && currentRidingEntity != null) {
			 * currentRidingEntity.fallDistance = 0F; motionY = currentRidingEntity.motionY
			 * = FairyConfig.DEF_FLAP_RATE * FairyConfig.DEF_LIFTOFF_MULT; } }
			 * 
			 * }
			 */

		}
		ItemStack helditemitem = super.getHeldItemMainhand();
		if (tempItem != null) {
			helditemitem = tempItem;
		} else if (queen()) {
			// Queens always carry the gold/iron sword, guards
			// always have the wooden sword.
			if (this.getVariant() % 2 == 1) {
				helditemitem = ironSword;
			} else {
				helditemitem = goldSword;
			}
		} else if (guard()) {
			helditemitem = woodSword;
		} else if (medic() && canHeal() && !angry()) // Medics carry potions
		{
			helditemitem = handPotion();
		} else if (scout()) // Scouts have maps now.
		{
			helditemitem = scoutMap;
		} else {
			helditemitem = super.getHeldItemMainhand();
		}
		this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, helditemitem);
	}

	public Boolean createFaction(double posX, double posY, double posZ) {
		int i = MathHelper.floor(posX);
		int j = MathHelper.floor(posY) - 1;
		int k = MathHelper.floor(posZ);

		final FairyGroupGenerator group = new FairyGroupGenerator(FairyConfig.SPAWN_FACTION_MIN_SIZE,
				FairyConfig.SPAWN_FACTION_MAX_SIZE, this);
		if (group.generate(world, rand, i, j, k)) {
			return true;
		} else {
			// issue a kill
			LOGGER.debug("Fairy followers spawn failed");
			if (!world.isRemote) {
				FairyFactions.proxy.sendFairyDespawn(this);
			}
		}
		return false;
	}

	// @SuppressWarnings("unused")
	@Override
	public void onUpdate() {
		super.onUpdate();
		if (flymode()) {
//		if (this.motionY < FairyConfig.DEF_FLOAT_RATE) {
//			this.motionY = FairyConfig.DEF_FLOAT_RATE;
//		}

		}
		final Entity currentRidingEntity = this.getRidingEntity();
		if (this.createGroup) {
			createGroup = !createFaction(posX, getEntityBoundingBox().minY, posZ);
		}

		if (scout()) {
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
					.setBaseValue(FairyConfig.GENERAL_SPEED_SCOUT);
		} else {
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
					.setBaseValue(FairyConfig.GENERAL_SPEED_BASE);
		}

		if (withered()) {
			IAttributeInstance speed = this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
			speed.setBaseValue(speed.getAttributeValue() * FairyConfig.GENERAL_SPEED_WITHER_MULT);
		}

		if (!world.isRemote) {
		}

		if (getHealth() > 0.0F) {
			// wing animations
			if (!this.onGround) {
				sinage += 0.75F;
			} else {
				sinage += 0.15F;
			}

			if (sinage > Math.PI * 2F) {
				sinage -= Math.PI * 2F;
			}

			if (hearts() != didHearts) {
				didHearts = !didHearts;
				showHeartsOrSmokeFX(isTamed());
			}

			// only render particles on clients
			if (world.isRemote) {
				if (++particleCount >= FairyConfig.DEF_MAX_PARTICLES) {
					particleCount = rand.nextInt(FairyConfig.DEF_MAX_PARTICLES >> 1);

					if (angry() || (crying() && queen())) {
						// anger smoke, queens don't cry :P
						world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posX, getEntityBoundingBox().maxY, posZ, 0D,
								0D, 0D);
					} else if (crying()) {
						// crying effect
						world.spawnParticle(EnumParticleTypes.WATER_SPLASH, posX, getEntityBoundingBox().maxY, posZ, 0D,
								0D, 0D);
					}

					if (liftOff()) {
						// liftoff effect below feet
						world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, posX, getEntityBoundingBox().minY, posZ,
								0D, 0D, 0D);
					}

					if (withered() || (rogue() && canHeal())) {
						// TODO: more proxying
						/*
						 * Offload to proxy for client-side rendering
						 *
						 * double a = posX - 0.2D + (0.4D * rand.nextDouble()); double b = posY + 0.45D
						 * + (0.15D * rand.nextDouble()); double c = posZ - 0.2D + (0.4D *
						 * rand.nextDouble()); EntitySmokeFX smoke = new EntitySmokeFX(world, a,b,c, 0D,
						 * 0D, 0D); a = 0.3D + (0.15D * rand.nextDouble()); b = 0.5D + (0.2D *
						 * rand.nextDouble()); c = 0.3D + (0.15D * rand.nextDouble());
						 * smoke.setRBGColorF((float)a, (float)b, (float)c);
						 * MC.effectRenderer.addEffect(smoke);
						 */
					}
				}

				// not sure why this was inside the check above...
				if (nameEnabled() && isTamed()) {
					if (!rulerName().equals("")) {
						FairyFactions.LOGGER.info("EntityFairy.onUpdate: calling proxy.openRenameGUI");
						FairyFactions.proxy.openRenameGUI(this);
					} else {
						FairyFactions.LOGGER.info("EntityFairy.onUpdate: tame but no ruler...");
					}
				}
			}

			// NB: this was only on the client in the original
			processSwinging();
		}
	} // end: onUpdate

	@Override
	public boolean isOnSameTeam(Entity entityIn) {
		boolean result = super.isOnSameTeam(entityIn);
		if (!result && entityIn instanceof EntityFairy) {
			result = ((EntityFairy) entityIn).getFaction() == this.getFaction();
		}
		return result;
	}

	@Override
	public boolean isEntityInsideOpaqueBlock() {
		for (int i = 0; i < 8; ++i) {
			float f = ((float) ((i >> 0) % 2) - 0.5F) * width * 0.8F;
			float f1 = ((float) ((i >> 1) % 2) - 0.5F) * 0.1F;
			float f2 = ((float) ((i >> 2) % 2) - 0.5F) * width * 0.8F;
			int j = MathHelper.floor(posX + (double) f);
			int k = MathHelper.floor(posY + (double) super.getEyeHeight() + (double) f1);
			int l = MathHelper.floor(posZ + (double) f2);

			if (world.getBlockState(new BlockPos(j, k, l))

					.isNormalCube()) {
				return true;
			}
		}
		return false;
	}

	// Fixes the head shaking glitch.
	@Override
	public void faceEntity(Entity entity, float f, float f1) {
		double d = entity.posX - posX;
		double d2 = entity.posZ - posZ;
		double d1;

		if (entity instanceof EntityLivingBase) {
			EntityLivingBase entityliving = (EntityLivingBase) entity;
			d1 = (posY + (double) (height * 0.85F)) - (entityliving.posY + (double) entityliving.getEyeHeight());
		} else {
			d1 = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2D
					- (posY + (double) (height * 0.85F));
		}

		double d3 = MathHelper.sqrt(d * d + d2 * d2);
		float f2 = (float) ((Math.atan2(d2, d) * 180D) / Math.PI) - 90F;
		float f3 = (float) (-((Math.atan2(d1, d3) * 180D) / Math.PI));
		rotationPitch = -updateRotation(rotationPitch, f3, f1);
		rotationYaw = updateRotation(rotationYaw, f2, f);
	}

	// Had to redo this because it is private.
	private float updateRotation(float f, float f1, float f2) {
		float f3;

		for (f3 = f1 - f; f3 < -180F; f3 += 360F) {
		}
		for (; f3 >= 180F; f3 -= 360F) {
		}

		if (f3 > f2) {
			f3 = f2;
		}

		if (f3 < -f2) {
			f3 = -f2;
		}

		return f + f3;
	}

	@Override
	protected void updateFallState(double y, boolean onGroundIn, IBlockState blockIn, BlockPos pos) {
		super.updateFallState(y / 6D, onGroundIn, blockIn, pos);
		int i = MathHelper.floor(posX);
		int j = MathHelper.floor(getEntityBoundingBox().minY) - 1;
		int k = MathHelper.floor(posZ);

		if (j > 0 && j < world.getHeight()) {
			// world.markBlockForUpdate(new BlockPos(i, j, k));
		}
	}

	@Override
	public void fall(float a, float b) {
		// HAH!
	}

	@Override
	public boolean canDespawn() {
		return ruler == null && !isTamed();
	}

	@Override
	protected void despawnEntity() {
		EntityPlayer entityplayer = world.getClosestPlayerToEntity(this, -1D);

		if (entityplayer != null) {
			double d = ((Entity) (entityplayer)).posX - posX;
			double d1 = ((Entity) (entityplayer)).posY - posY;
			double d2 = ((Entity) (entityplayer)).posZ - posZ;
			double d3 = d * d + d1 * d1 + d2 * d2;

			if (canDespawn() && d3 > 16384D) {
				setDead();

				// mod_FairyMod.fairyMod.sendFairyDespawn(this);
				if (queen()) {
					despawnFollowers();
				}
			}

			if (ticksExisted > 600 && rand.nextInt(800) == 0 && d3 > 1024D && canDespawn()) {
				// TODO: proxy
				setDead();

				// mod_FairyMod.fairyMod.sendFairyDespawn(this);
				if (queen()) {
					despawnFollowers();
				}
			} else if (d3 < 1024D) {
				/*
				 * todo: workout what to do with time here (used to be EntityAge) ticksExisted =
				 * 0;
				 */
			}
		}
	}

	public void despawnFollowers() {
		if (queen() && getFaction() > 0) {
			List<?> list = world.getEntitiesWithinAABB(EntityFairy.class, getEntityBoundingBox().expand(40D, 40D, 40D));

			for (int j = 0; j < list.size(); j++) {
				EntityFairy fairy = (EntityFairy) list.get(j);

				if (fairy != this && fairy.getHealth() > 0 && sameTeam(fairy)
						&& (fairy.ruler == null || fairy.ruler == this)) {
					// TODO: proxy
					fairy.setDead();
					// mod_FairyMod.fairyMod.sendFairyDespawn(fairy);
				}
			}
		}
	}

	// ---------- behaviors ----------

	// maximum number of times to try pathfinding
	public static final int MAX_PATHING_TRIES = 32;
	public static final float PATH_TOWARD = 0F;
	public static final float PATH_AWAY = (float) Math.PI;

	/**
	 * if griniscule is 0F, entity2 will roam towards entity1. if griniscule is pi,
	 * entity2 will roam away from entity1. Also, a griniscule is a portmanteau of
	 * grin and miniscule.
	 *
	 * @param target
	 * @param actor
	 * @param griniscule
	 * @return
	 */
	public Path roam(Entity target, Entity actor, float griniscule) {
		double a = target.posX - actor.posX;
		double b = target.posZ - actor.posZ;

		double crazy = Math.atan2(a, b);
		crazy += (rand.nextFloat() - rand.nextFloat()) * 0.25D;
		crazy += griniscule;

		double c = actor.posX + (Math.sin(crazy) * 8F);
		double d = actor.posZ + (Math.cos(crazy) * 8F);

		int x = MathHelper.floor(c);
		int y = MathHelper.floor(actor.getEntityBoundingBox().minY);
		int z = MathHelper.floor(d);

		for (int q = 0; q < MAX_PATHING_TRIES; q++) {
			int i = x + rand.nextInt(5) - rand.nextInt(5);
			int j = y + rand.nextInt(5) - rand.nextInt(5);
			int k = z + rand.nextInt(5) - rand.nextInt(5);

			if (j > 4 && j < world.getHeight() - 1 && isAirySpace(i, j, k) && !isAirySpace(i, j - 1, k)) {
				/*
				 * Path path = world.getEntityPathToXYZ(actor, i, j, k,
				 * FairyConfig.BEHAVIOR_PATH_RANGE, false, false, true, true);
				 *
				 * if (path != null) { return path; }
				 */
				return this.getNavigator().getPathToXYZ(i, y, k);
			}
		}

		return null;
	}

	// TODO: combine this with roam()
	public Path roamBlocks(double t, double u, double v, Entity actor, float griniscule) {
		// t is an X coordinate, u is a Y coordinate, v is a Z coordinate.
		// Griniscule of 0.0 means towards, 3.14 means away.
		double a = t - actor.posX;
		double b = v - actor.posZ;
		double crazy = Math.atan2(a, b);
		crazy += (rand.nextFloat() - rand.nextFloat()) * 0.25D;
		crazy += griniscule;
		double c = actor.posX + (Math.sin(crazy) * 8F);
		double d = actor.posZ + (Math.cos(crazy) * 8F);
		int x = MathHelper.floor(c);
		int y = MathHelper.floor(
				actor.getEntityBoundingBox().minY + (rand.nextFloat() * (u - actor.getEntityBoundingBox().minY)));
		int z = MathHelper.floor(d);

		for (int q = 0; q < MAX_PATHING_TRIES; q++) {
			int i = x + rand.nextInt(5) - rand.nextInt(5);
			int j = y + rand.nextInt(5) - rand.nextInt(5);
			int k = z + rand.nextInt(5) - rand.nextInt(5);

			if (j > 4 && j < world.getHeight() - 1 && isAirySpace(i, j, k) && !isAirySpace(i, j - 1, k)) {
				/*
				 * Path path = world.getEntityPathToXYZ(actor, i, j, k,
				 * FairyConfig.BEHAVIOR_PATH_RANGE, false, false, true, true);
				 *
				 * if (path != null) { return path; }
				 */
				return this.getNavigator().getPathToXYZ(i, y, k);
			}
		}

		return (Path) null;
	}

	private boolean canTeleportToRuler(EntityPlayer player) {
		return player.inventory != null && (player.inventory.hasItemStack(new ItemStack(Items.ENDER_PEARL))
				|| player.inventory.hasItemStack(new ItemStack(Items.ENDER_EYE)));
	}

	// Can teleport to the ruler if he has an enderman drop in his inventory.
	private void teleportToRuler(EntityLivingBase entity) {
		int i = MathHelper.floor(entity.posX) - 2;
		int j = MathHelper.floor(entity.posZ) - 2;
		int k = MathHelper.floor(entity.getEntityBoundingBox().minY);

		for (int l = 0; l <= 4; l++) {
			for (int i1 = 0; i1 <= 4; i1++) {
				if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && world.getBlockState(new BlockPos(i + l, k - 1, j + i1))

						.isNormalCube()
						&& !world.getBlockState(new BlockPos(i + l, k, j + i1))

								.isNormalCube()
						&& !world.getBlockState(new BlockPos(i + l, k + 1, j + i1))

								.isNormalCube()
						&& isAirySpace(i + l, k, j + i1)) {
					setLocationAndAngles((float) (i + l) + 0.5F, k, (float) (j + i1) + 0.5F, rotationYaw,
							rotationPitch);
					return;
				}
			}
		}
	}

	private void handleAnger() {
		setEntityFear(null);

		// Lose interest in an entity that is far away or out of sight over
		// time.
		if (entityToAttack != null) {
			final float enemy_dist = getDistance(entityToAttack);

			if (enemy_dist >= FairyConfig.BEHAVIOR_PURSUE_RANGE
					|| (rand.nextBoolean() && !canEntityBeSeen(entityToAttack))) {
				++loseInterest;

				if (loseInterest >= (isTamed() ? FairyConfig.BEHAVIOR_AGGRO_TIMER
						: FairyConfig.BEHAVIOR_AGGRO_TIMER * 3)) {
					setTarget(null);
					loseInterest = 0;
				}
			} else {
				loseInterest = 0;
			}

			// Guards can fight for a queen - will make her run away instead
			if (guard() && getFaction() > 0 && ruler != null && ruler instanceof EntityFairy) {
				EntityFairy fairy = (EntityFairy) ruler;

				if (fairy.entityToAttack != null) {
					float queen_dist = getDistance(fairy);

					if (queen_dist < FairyConfig.BEHAVIOR_DEFEND_RANGE && enemy_dist < FairyConfig.BEHAVIOR_DEFEND_RANGE
							&& canEntityBeSeen(fairy)) {
						this.setTarget(fairy.entityToAttack);
						fairy.setTarget(null);
						fairy.setCryTime(100);
						fairy.setEntityFear(entityToAttack);
					}
				}
			}
		}
	}

	private void handleRuler() {
		// TODO: create constants for all of these ranges and time limits

		if (ruler != null) {
			if (ruler.getHealth() <= 0 || ruler.isDead) {
				// get rid of that ruler.
				ruler = null;
			}
		}

		if (ruler == null) {
			// Looking for a queen to follow.
			if (!isTamed() && !queen()) {
				double d = 40D;

				if (getFaction() == 0) {
					d = 16D;
				}

				List<?> list = world.getEntitiesWithinAABB(EntityFairy.class, getEntityBoundingBox().expand(d, d, d));

				for (int j = 0; j < list.size(); j++) {
					EntityFairy fairy = (EntityFairy) list.get(j);

					if (fairy != this && fairy.getHealth() > 0 && fairy.queen()) {
						if (getFaction() > 0 && fairy.getFaction() == this.getFaction()) {
							// Fairy finds the queen of its faction, fairly
							// standard.
							ruler = fairy;
							break;
						} else if (getFaction() == 0 && fairy.getFaction() > 0 && canEntityBeSeen(fairy)) {
							// A factionless fairy may find a new ruler on its
							// own.
							ruler = fairy;
							setFaction(fairy.getFaction());
							break;
						}
					}
				}
			} else if (getFaction() == 0 && isTamed()) {
				// Looking for a player to follow.
				List<?> list = world.getEntitiesWithinAABB(EntityPlayer.class,
						getEntityBoundingBox().expand(16D, 16D, 16D));

				for (int j = 0; j < list.size(); j++) {
					EntityPlayer player = (EntityPlayer) list.get(j);

					if (player.getHealth() > 0 && isRuler(player) && canEntityBeSeen(player)) {
						ruler = player;
						break;
					}
				}
			}
		}

		// This makes fairies walk towards their ruler.
		if (ruler != null && !hasPath() && !posted()) {
			float dist = getDistance(ruler);

			// Guards and Queens walk closer to the player (Medic healing?)
			if ((guard() || queen()) && canEntityBeSeen(ruler) && dist > 5F && dist < 16F) {
				/*
				 * Path path = world.getPathEntityToEntity(this, ruler, 16F, false, false, true,
				 * true);
				 *
				 * if (path != null) { setPathToEntity(path); }
				 */
				this.getNavigator().tryMoveToEntityLiving(ruler, this.getAIMoveSpeed());
			} else {
				if (scout() && ruler instanceof EntityFairy) {
					// Scouts stay way out there on the perimeter.
					if (dist < 12F) {
						Path dest = roam(ruler, this, (float) Math.PI);

						/*
						 * if (dest != null) { setPathToEntity(dest); }
						 */
						this.getNavigator().setPath(dest, this.getAIMoveSpeed());
					} else if (dist > 24F) {
						Path dest = roam(ruler, this, 0F);

						/*
						 * if (dest != null) { setPathToEntity(dest); }
						 */
						this.getNavigator().setPath(dest, this.getAIMoveSpeed());
					}
				} else {
					// Regular fairies stay moderately close.
					if (dist > 16F && ruler instanceof EntityPlayer && canTeleportToRuler((EntityPlayer) ruler)) {
						// Can teleport to the owning player if he has an ender
						// eye or an ender pearl.
						teleportToRuler(ruler);
					} else if (dist > (ruler instanceof EntityFairy ? 12F : 6F)) {
						Path dest = roam(ruler, this, 0F);

						/*
						 * if (dest != null) { setPathToEntity(dest); }
						 */
						this.getNavigator().setPath(dest, this.getAIMoveSpeed());
					}
				}
			}
		}

		if (snowballin > 0 && attackTime <= 0 && ruler != null && entityToAttack == null && entityFear == null
				&& cryTime == 0) {
			float dist = getDistance(ruler);

			if (dist < 10F && canEntityBeSeen(ruler)) {
				tossSnowball(ruler);
			} else if (!hasPath() && dist < 16F) {
				Path dest = roam(ruler, this, 0F);

				/*
				 * if (dest != null) { setPathToEntity(dest); }
				 */
				this.getNavigator().setPath(dest, this.getAIMoveSpeed());
			}
		}

		if (getFaction() > 0) {
			// This is a method for making sure that fairies eventually realize
			// they're alone
			boolean flag = false;

			if (!queen() && (ruler == null || getDistance(ruler) > 40F)) {
				// If a follower has lost its leader
				flag = true;
			} else if (queen()) {
				// If a leader has lost her followers
				flag = true;
				List<?> list = world.getEntitiesWithinAABB(EntityFairy.class,
						getEntityBoundingBox().expand(40D, 40D, 40D));

				for (int j = 0; j < list.size(); j++) {
					EntityFairy fairy = (EntityFairy) list.get(j);

					if (fairy != this && fairy.sameTeam(this) && fairy.getHealth() > 0) {
						flag = false;
						break;
					}
				}
			} else if (ruler != null && ruler instanceof EntityFairy) {
				// If a fairy queen was tamed in peaceful mode
				EntityFairy fairy = (EntityFairy) ruler;

				if (!sameTeam(fairy)) {
					flag = true;

					if (loseTeam < 65) {
						loseTeam = 65 + rand.nextInt(8);
					}
				}
			}

			if (flag) {
				// Takes a while for it to take effect.
				++loseTeam;

				if (loseTeam >= 75) {
					ruler = null;
					disband();
					loseTeam = 0;
					setCryTime(0);
					// setPathToEntity((Path) null);
					this.getNavigator().clearPath();
				}
			} else {
				loseTeam = 0;
			}
		}
	}

	// Checks if a damage source is a snowball.
	// TODO: give this a real name
	public boolean snowballFight(DamageSource damagesource) {
		if (damagesource instanceof EntityDamageSourceIndirect) {
			EntityDamageSourceIndirect snowdamage = (EntityDamageSourceIndirect) damagesource;

			if (snowdamage.getImmediateSource() != null && snowdamage.getImmediateSource() instanceof EntitySnowball) {
				snowballin += 1;

				if (attackTime < 10) {
					attackTime = 10;
				}
			}
		}

		return snowballin <= 0;
	}

	private void tossSnowball(EntityLivingBase attackTarget) {
		EntitySnowball entitysnowball = new EntitySnowball(world, this);
		double d = attackTarget.posX - this.posX;
		double d1 = (attackTarget.posY + (double) attackTarget.getEyeHeight()) - 1.1000000238418579D
				- entitysnowball.posY;
		double d2 = attackTarget.posZ - this.posZ;
		float f = MathHelper.sqrt(d * d + d2 * d2) * 0.2F;
		entitysnowball.shoot(d, d1 + (double) f, d2, 1.6F, 12F);
		/*
		 * world.playSoundAtEntity(this, "random.bow", 1.0F, 1.0F /
		 * (this.getRNG().nextFloat() * 0.4F + 0.8F));
		 */
		world.spawnEntity(entitysnowball);
		attackTime = 30;
		armSwing(!didSwing);
		faceEntity(attackTarget, 180F, 180F);
		snowballin -= 1;

		if (snowballin < 0) {
			snowballin = 0;
		}
	}

	// This handles actions concerning teammates and entities atacking their
	// ruler.
	public void doHandleSocial() {

		List<?> list = findLiveVisibleEntities();
		Collections.shuffle(list, rand);

		for (int j = 0; j < list.size(); j++) {
			Entity entity = (Entity) list.get(j);

			if ((ruler != null || queen()) && entity instanceof EntityFairy && sameTeam((EntityFairy) entity)) {
				Boolean shouldBreak = this.handleSocialQueen((EntityFairy) entity);
				if (shouldBreak) {
					break;
				}
			} else if (ruler != null && (guard() || queen()) && entity instanceof EntityCreature
					&& !(entity instanceof EntityCreeper)
					&& (!(entity instanceof EntityAnimal) || (!peacefulAnimal((EntityAnimal) entity)))) {
				// Guards proactively seeking hostile enemies. Will add
				// slimes? Maybe dunno.
				/**
				 * TODO: Update AI
				 */
				EntityCreature creature = (EntityCreature) entity;

				if (creature.getHealth() > 0 && creature.getAttackTarget() != null
						&& creature.getAttackTarget() == ruler) {
					LOGGER.debug("target set to:" + creature.getName());
					this.setTarget((Entity) creature);
					break;
				}

			} else if (entity instanceof EntityTNTPrimed && !hasPath()) {
				// Running away from lit TNT.
				float dist = getDistance(entity);
				LOGGER.debug(this.toString() + "has seen the TNT");
				if (dist < 8F) {
					Path dest = roam(entity, this, (float) Math.PI);

					if (dest != null) {
						// setPathToEntity(dest);
						this.getNavigator().setPath(dest, this.getAIMoveSpeed());

						if (!flymode()) {
							setFlymode(true);
							jump();
							setFlapEnergy(100);
						}

						break;
					}
				}
			}
		}
	}

	public Boolean handleSocialQueen(EntityFairy fairy) {
		// LOGGER.debug("Handling" + fairy.toString());
		if (fairy.getHealth() > 0) {
			EntityLivingBase scary = null;

			if (fairy.getEntityFear() != null) {
				scary = fairy.getEntityFear();
			} else if (fairy.entityToAttack != null) {
				scary = fairy.entityToAttack;
			}

			if (scary != null) {
				float dist = getDistance(scary);

				if (dist > 16F || !canEntityBeSeen(scary)) {
					scary = null;
				}
			}

			if (scary != null) {
				if (willCower()) {
					if (fairy.entityToAttack == scary && canEntityBeSeen(scary)) {
						setCryTime(120);
						this.setEntityFear(scary);
						Path dest = roam(fairy, this, (float) Math.PI);

						/*
						 * if (dest != null) { setPathToEntity(dest); }
						 */
						this.getNavigator().setPath(dest, this.getAIMoveSpeed());

						return true;
					} else if (fairy.getCryTime() > 60) {
						setCryTime(Math.max(fairy.getCryTime() - 60, 0));
						this.setEntityFear(scary);
						Path dest = roam(fairy, this, (float) Math.PI);

						/*
						 * if (dest != null) { setPathToEntity(dest); }
						 */
						this.getNavigator().setPath(dest, this.getAIMoveSpeed());

						return true;
					}
				} else {
					this.setTarget((Entity) scary);
					return true;
				}
			}
		}
		return false;
	}

	public boolean peacefulAnimal(Entity animal) {
		if (!(animal instanceof EntityAnimal)) {
			return false;
		}
		Class<? extends EntityAnimal> thing = ((EntityAnimal) animal).getClass();
		return thing == EntityPig.class || thing == EntityCow.class || thing == EntityChicken.class
				|| thing == EntitySheep.class || thing == EntityMooshroom.class;
	}

	public boolean friendly(EntityLivingBase entity) {
		if (entity instanceof EntityCreeper || entity instanceof EntitySlime) {
			return true;
		}
		return false;
	}

	public List<Entity> findLiveEntities(boolean visible) {
		List<Entity> result = Lists.<Entity>newArrayList();
		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this,
				getEntityBoundingBox().expand(16D, 16D, 16D));

		for (int j = 0; j < list.size(); j++) {
			Entity entity = (Entity) list.get(j);
			if (!entity.isDead) {
				if (!visible || canEntityBeSeen(entity)) {
					result.add(entity);
				}
			}
		}
		return result;
	}

	public List<Entity> findLiveVisibleEntities() {
		return this.findLiveEntities(true);
	}

	// This handles actions of the medics.
	public List<EntityLivingBase> findFriendlyEntities() {
		List<EntityLivingBase> friendlies = Lists.<EntityLivingBase>newArrayList();
		List<?> list = findLiveVisibleEntities();

		for (int j = 0; j < list.size(); j++) {
			Entity entity = (Entity) list.get(j);

			if (entity instanceof EntityFairy) {
				EntityFairy fairy = (EntityFairy) list.get(j);

				if (fairy.getHealth() > 0 && sameTeam(fairy) && fairy.getHealth() < fairy.getMaxHealth()) {
					friendlies.add(fairy);
				}
			} else if (entity instanceof EntityLivingBase && ruler != null && ((EntityLivingBase) entity) == ruler) {
				if (ruler.getHealth() > 0 && ruler.getHealth() < ruler.getMaxHealth()) {
					friendlies.add(ruler);
				}
			}
		}
		return friendlies;
	}

	public List<EntityFairy> findFairies(boolean visible, boolean wantSameTeam) {
		List<EntityFairy> result = Lists.newArrayList();
		List<?> list = findLiveEntities(visible);

		for (int j = 0; j < list.size(); j++) {
			Entity entity = (Entity) list.get(j);
			if (entity instanceof EntityFairy) {
				EntityFairy fairy = (EntityFairy) list.get(j);
				if (fairy.getHealth() > 0) {
					result.add(fairy);
				}
			}
		}
		return result;
	}

	public Entity getHealTarget() {
		return entityHeal;
	}

	public void setHealTarget(Entity entity) {
		entityHeal = (EntityLivingBase) entity;
	}

	public void clearHealTarget() {
		entityHeal = null;
	}

	public boolean registerFactionMember(EntityFairy EntityIn) {
		if (EntityIn instanceof EntityFairy) {
			if (this.factionMembers.size() == 0 || this.factionMembers.indexOf(EntityIn) == -1) {
				this.factionMembers.add(EntityIn);
			}
			return true;
		}
		return false;
	}

	public List<EntityFairy> getFactionMembers() {
		return this.factionMembers;
	}

	public Object getEntityToHeal() {
		List<?> friendlies = Lists.newArrayList();
		if (this.ruler != null && this.ruler instanceof EntityFairy) {
			friendlies = ((EntityFairy) this.ruler).getFactionMembers();
		} else {
			friendlies = this.findFriendlyEntities();
		}
		if (friendlies.size() > 0) {
			EntityLivingBase illest = null;
			for (int j = 0; j < friendlies.size(); j++) {
				EntityLivingBase patient = (EntityLivingBase) friendlies.get(j);
				if (this.needsMedic(patient) && patient instanceof EntityFairy) {
					if ((this.ruler != null && this.ruler == patient)
							|| (illest == null || patient.getHealth() > illest.getHealth())) {
						illest = patient;
						if (illest == ruler) {
							break;
						}
					}
				}
			}
			return illest;
		}
		return null;
	}

	public boolean needsMedic(EntityLivingBase patient) {
		float minHealth = patient.getMaxHealth();
		if (patient.getHealth() <= 0 || patient.isDead) {
			return false;
		}
		if (patient.getHealth() < minHealth) {
			return true;
		}
		return false;
	}

	private void handleHealing() {
		if (healTime > 0) {
			return;
		}

		if (entityHeal != null) {
			if (entityHeal.getHealth() <= 0 || entityHeal.isDead) {
				entityHeal = null;
			} else if (!hasPath()) {
				/*
				 * Path dest = world.getPathEntityToEntity(this, entityHeal, 16F, false, false,
				 * true, true);
				 */
				Path dest = this.getNavigator().getPathToEntityLiving(entityHeal);

				if (dest != null) {
					// setPathToEntity(dest);
					this.getNavigator().setPath(dest, this.getAIMoveSpeed());
				} else {
					entityHeal = null;
				}
			} else {
				float g = getDistance(entityHeal);

				if (g < 2.5F && canEntityBeSeen(entityHeal)) {
					doHeal(entityHeal);
					entityHeal = null;
				}
			}
		}

		if (entityHeal == null && healTime <= 0) {
			List<?> list = world.getEntitiesWithinAABBExcludingEntity(this,
					getEntityBoundingBox().expand(16D, 16D, 16D));

			for (int j = 0; j < list.size(); j++) {
				Entity entity = (Entity) list.get(j);

				if (canEntityBeSeen(entity) && !entity.isDead) {
					if (entity instanceof EntityFairy) {
						EntityFairy fairy = (EntityFairy) list.get(j);

						if (fairy.getHealth() > 0 && sameTeam(fairy) && fairy.getHealth() < fairy.getMaxHealth()) {
							this.entityHeal = fairy;
							/*
							 * Path dest = world.getPathEntityToEntity( this, entityHeal, 16F, false, false,
							 * true, true);
							 *
							 * if (dest != null) { setPathToEntity(dest); }
							 */
							this.getNavigator().tryMoveToEntityLiving(this.entityHeal, this.getAIMoveSpeed());

							break;
						}
					} else if (entity instanceof EntityLivingBase && ruler != null
							&& ((EntityLivingBase) entity) == ruler) {
						if (ruler.getHealth() > 0 && ruler.getHealth() < ruler.getMaxHealth()) {
							this.entityHeal = ruler;
							/*
							 * Path dest = world.getPathEntityToEntity( this, entityHeal, 16F, false, false,
							 * true, true);
							 *
							 * if (dest != null) { setPathToEntity(dest); }
							 */
							this.getNavigator().tryMoveToEntityLiving(this.entityHeal, this.getAIMoveSpeed());

							break;
						}
					}
				}
			}

			if (entityHeal == null && getHealth() < getMaxHealth()) {
				doHeal(this);
			}
		}
	}

	public static final ItemStack healPotion = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM),
			PotionTypes.HEALING);

	public static final ItemStack restPotion = PotionUtils
			.addPotionToItemStack(new ItemStack(Items.POTIONITEM, 1, 16385), PotionTypes.LONG_REGENERATION);
	public static final ItemStack fishingStick = new ItemStack(Items.FISHING_ROD, 1);
	public static final ItemStack scoutMap = new ItemStack(Items.MAP, 1);

	public static final ItemStack woodSword = new ItemStack(Items.WOODEN_SWORD, 1);
	public static final ItemStack ironSword = new ItemStack(Items.IRON_SWORD, 1);
	public static final ItemStack goldSword = new ItemStack(Items.GOLDEN_SWORD, 1);

	public ItemStack handPotion() {
		return (rarePotion() ? restPotion : healPotion);
	}

	@Override
	public Item getDropItem() {
		return Items.GLOWSTONE_DUST;
	}

	/**
	 * TODO: Figure out what this turns into in 1.8
	 *
	 * @SideOnly(Side.CLIENT) @Override public IIcon getItemIcon(ItemStack
	 *                        itemstack, int i) { IIcon j =
	 *                        super.getItemIcon(itemstack, i);
	 *
	 *                        if (itemstack.getItem() == Items.potionitem) { if (i
	 *                        == 1) { return itemstack.getIconIndex(); } else { //
	 *                        TODO: figure this out :) // return 141; } }
	 *
	 *                        return j; }
	 */

	private void doHeal(EntityLivingBase guy) {
		armSwing(!didSwing); // Swings arm and heals the specified person.
		EntityPotion potion = new EntityPotion(world, this, handPotion());
		world.spawnEntity(potion);

		// potion.onImpact(new RayTraceResult(guy));
		/*
		 * try { final Method onImpact = ReflectionHelper.findMethod(EntityPotion.class,
		 * potion, MCP_ONIMPACT, RayTraceResult.class); onImpact.invoke(potion, new
		 * RayTraceResult(guy)); } catch (Exception e) { e.printStackTrace(); }
		 */
		// setPathToEntity((Path) null);
		this.getNavigator().clearPath();
		healTime = 200;
		setRarePotion(rand.nextInt(4) == 0);
	}

	// A handler specifically for the rogue class.
	private void handleRogue() {
		if (rand.nextBoolean()) {
			return;
		}

		List<?> list = world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().expand(16D, 16D, 16D));
		Collections.shuffle(list, rand);

		for (int j = 0; j < list.size(); j++) {
			Entity entity = (Entity) list.get(j);

			if (canEntityBeSeen(entity) && !entity.isDead) {
				if ((ruler != null || queen()) && entity instanceof EntityFairy && sameTeam((EntityFairy) entity)) {
					EntityFairy fairy = (EntityFairy) list.get(j);

					if (fairy.getHealth() > 0) {
						EntityLivingBase scary = null;

						if (fairy.getEntityFear() != null) {
							scary = fairy.getEntityFear();
						} else if (fairy.entityToAttack != null) {
							scary = fairy.entityToAttack;
						}

						if (scary != null) {
							float dist = getDistance(scary);

							if (dist > 16F || !canEntityBeSeen(scary)) {
								scary = null;
							}
						}

						if (scary != null) {
							if (canHeal()) {
								if (fairy.entityToAttack == scary && canEntityBeSeen(scary)) {
									setCryTime(120);
									this.setEntityFear(scary);
									Path dest = roam(entity, this, (float) Math.PI);

									/*
									 * if (dest != null) { setPathToEntity(dest); }
									 */
									this.getNavigator().setPath(dest, this.getAIMoveSpeed());

									break;
								} else if (fairy.getCryTime() > 60) {
									setCryTime(Math.max(fairy.getCryTime() - 60, 0));
									this.setEntityFear(scary);
									Path dest = roam(entity, this, (float) Math.PI);

									/*
									 * if (dest != null) { setPathToEntity(dest); }
									 */
									this.getNavigator().setPath(dest, this.getAIMoveSpeed());

									break;
								}
							} else {
								this.setTarget((Entity) scary);
								break;
							}
						}
					}
				} else if (ruler != null && canHeal() && entity instanceof EntityCreature
						&& !(entity instanceof EntityCreeper)
						&& (!(entity instanceof EntityAnimal) || (!peacefulAnimal((EntityAnimal) entity)))) {
					/**
					 * TODO: Update AI.
					 *
					 * EntityCreature creature = (EntityCreature) entity;
					 *
					 * if (creature.getHealth() > 0 && creature.getEntityToAttack() != null &&
					 * creature.getEntityToAttack() == ruler) { this.setTarget((Entity) creature);
					 * break; }
					 */
				} else if (entity instanceof EntityTNTPrimed && !hasPath()) {
					// Running away from lit TNT.
					float dist = getDistance(entity);

					if (dist < 8F) {
						Path dest = roam(entity, this, (float) Math.PI);

						if (dest != null) {
							// setPathToEntity(dest);
							this.getNavigator().setPath(dest, this.getAIMoveSpeed());

							if (!flymode()) {
								setFlymode(true);
								jump();
								setFlapEnergy(100);
							}

							break;
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private int postedCount; // flag for counting posted checks
	// The AI method which handles post-related activities.

	private void handlePosted(boolean flag) {
		/*
		 * if( tamed() ) { System.out.println("handlePosted("+flag+") - "+postedCount+"
		 * - "+this); }
		 */
		if (!isTamed() || getFaction() > 0 /* || postedCount <= ( posted() ? 2 : 5 ) */) {
			++postedCount;
			return; // Avoid processing too often or when not necessary.
		}

		postedCount = 0;
		boolean farFlag = false;

		if (postY > -1) {
			if (getRidingEntity() != null && ruler != null && getRidingEntity() == ruler) {
				abandonPost();
				return; // When a the player takes a tamed fairy away, it
						// automatically cancels the post.
			}
			final BlockPos postPos = new BlockPos(postX, postY, postZ);

			// Check to see if the chunk is loaded.
			Chunk chunk = world.getChunkFromBlockCoords(postPos);

			if (chunk != null && !(chunk instanceof EmptyChunk)) {
				Block block = world.getBlockState(postPos).getBlock();

				if (block == null || !(block instanceof BlockSign)) {
					// If the saved position is not a sign block.
					abandonPost();
				} else {
					TileEntity tileentity = world.getTileEntity(postPos);
					if (tileentity == null || !(tileentity instanceof TileEntitySign)) {
						// Make sure the tile entity is right
						abandonPost();
					} else {
						TileEntitySign sign = (TileEntitySign) tileentity;
						if (!mySign(sign)) {
							// Make sure the name still matches
							abandonPost();
						} else if (canRoamFar(sign)) {
							farFlag = true;
						}
					}
				}
			}
		} else {
			// Try to find a post. The ruler has to be nearby for it to work.
			if (ruler != null && (getRidingEntity() == null || getRidingEntity() != ruler)
					&& getDistanceSq(ruler) <= 64F && canEntityBeSeen(ruler)) {
				// Gets the fairy's relative position
				int aa = MathHelper.floor(posX);
				int bb = MathHelper.floor(getEntityBoundingBox().minY);
				int cc = MathHelper.floor(posZ);

				for (int i = 0; i < 245; i++) {
					int x = -3 + (i % 7); // Look around randomly.
					int y = -2 + (i / 49);
					int z = -3 + ((i / 7) % 7);

					if (Math.abs(x) == 3 && Math.abs(z) == 3) {
						continue;
					}

					x += aa;
					y += bb;
					z += cc;

					if (y >= 0 && y < world.getHeight()) {
						final BlockPos pos = new BlockPos(x, y, z);
						final Block block = world.getBlockState(pos).getBlock();
						if (block == Blocks.STANDING_SIGN || block == Blocks.WALL_SIGN) {
							TileEntity tileentity = world.getTileEntity(pos);

							if (tileentity != null && tileentity instanceof TileEntitySign) {
								TileEntitySign sign = (TileEntitySign) tileentity;

								if (mySign(sign)) {
									postX = x;
									postY = y;
									postZ = z;
									setPosted(true);
									break;
								}
							}
						}
					}
				}
			}
		}

		if (!flag) // Processes fishing, then returns, if sitting.
		{
			if (fishEntity != null) {
				if (fishEntity.gotBite()) {
					castRod();
					attackTime = 10;
				} else if (rand.nextFloat() < 0.1F) {
					// TODO: handle packet
					/*
					 * mod_FairyMod.setPrivateValueBoth( EntityLiving.class, this, "currentTarget",
					 * "ay", fishEntity );
					 */
					/**
					 * TODO: Update AI
					 *
					 * numTicksToChaseTarget = 10 + this.rand.nextInt(20);
					 */
				}
			} else if (rand.nextInt(2) == 0) {
				new FairyJob(this).sittingFishing(world);
			}

			return;
		}

		if (posted() && !hasPath() && !angry() && !crying()) {
			double aa = (double) postX + 0.5D;
			double bb = (double) postY + 0.5D;
			double cc = (double) postZ + 0.5D;
			double dd = posX - aa;
			double ee = getEntityBoundingBox().minY - bb;
			double ff = posZ - cc;
			double gg = Math.sqrt((dd * dd) + (ee * ee) + (ff * ff));

			if (gg >= (farFlag ? 12D : 6D)) {
				Path dest = roamBlocks(aa, bb, cc, this, 0F);

				/*
				 * if (dest != null) { setPathToEntity(dest); }
				 */
				this.getNavigator().setPath(dest, this.getAIMoveSpeed());
			}
		}

		if (posted()) {
			new FairyJob(this).discover(world);
		}
	}

	public void castRod() {
		if (fishEntity != null) {
			fishEntity.catchFish();
			armSwing(!didSwing);
			setSitting(false);
		} else {
			/*
			 * world.playSoundAtEntity(this, "random.bow", 0.5F, 0.4F / (rand.nextFloat() *
			 * 0.4F + 0.8F));
			 */
			FairyEntityFishHook hook = new FairyEntityFishHook(world, this);
			world.spawnEntity(hook);
			armSwing(!didSwing);
			setTempItem(Items.STICK);
			setSitting(true);
			isJumping = false;
			// setPathToEntity((Path) null);
			this.getNavigator().clearPath();
			setTarget((Entity) null);
			entityFear = null;
		}
	}

	private boolean signContains(TileEntitySign sign, String str) {
		// If the sign's text is messed up or something
		if (sign.signText == null) {
			return false;
		}

		// makes the subsequence
		final CharSequence mySeq = str.subSequence(0, str.length() - 1);

		// loops through for all sign lines
		for (int i = 0; i < sign.signText.length; i++) {
			// name just has to be included in full on one of the lines.
			if (sign.signText[i].getUnformattedText().contains(mySeq)) {
				return true;
			}
		}

		return false;
	}

	private boolean canRoamFar(TileEntitySign sign) {
		return signContains(sign, "~f");
	}

	private boolean mySign(TileEntitySign sign) {
		// Converts actual name
		final String actualName = getActualName(getNamePrefix(), getNameSuffix());
		return signContains(sign, actualName);
	}

	// Leave a post.
	public void abandonPost() {
		postX = postY = postZ = -1;
		setPosted(false);
	}

	// ---------- B_TYPE ----------

	public static final int MAX_JOB = 3;
	public static final int MAX_FACTION = 15;
	public static final int MAX_NAMEIDX = 15;

	@Deprecated
	public int getSkin() {
		return this.getVariant();
	}

	@Deprecated
	protected void setSkin(int skin) {
		if (skin < 0) {
			skin = 0;
		} else if (skin > MAX_VARIANT) {
			skin = MAX_VARIANT;
		}
		this.setVariant(skin);
	}

	public int getJob() {
		return (dataManager.get(B_TYPE) >> 2) & 0x03;
	}

	public void setJob(int job) {
		if (job < 0) {
			job = 0;
		} else if (job > MAX_JOB) {
			job = MAX_JOB;
		}

		// FairyFactions.LOGGER.info("setJob: " + this + " -> " + job);

		byte byte0 = dataManager.get(B_TYPE);
		byte0 = (byte) (byte0 & 0xf3);
		// byte0 |= (byte) job << 2;
		byte0 = (byte) (byte0 | (byte) job << 2);

		dataManager.set(B_TYPE, Byte.valueOf(byte0));
	}

	protected static final int NJOB_NORMAL = 0;
	protected static final int NJOB_GUARD = 1;
	protected static final int NJOB_SCOUT = 2;
	protected static final int NJOB_MEDIC = 3;
	protected static final int SJOB_QUEEN = 0;
	protected static final int SJOB_ROGUE = 1;

	public boolean normal() {
		return getJob() == NJOB_NORMAL && !specialJob();
	}

	public boolean guard() {
		return getJob() == NJOB_GUARD && !specialJob();
	}

	public boolean scout() {
		return getJob() == NJOB_SCOUT && !specialJob();
	}

	public boolean medic() {
		return getJob() == NJOB_MEDIC && !specialJob();
	}

	public boolean queen() {
		return getJob() == SJOB_QUEEN && specialJob();
	}

	public boolean rogue() {
		return getJob() == SJOB_ROGUE && specialJob();
	}

	public int getFaction() {
		return (dataManager.get(B_TYPE) >> 4) & 0x0f;
	}

	public void setFaction(int faction) {
		if (faction < 0) {
			faction = 0;
		} else if (faction > MAX_FACTION) {
			faction = MAX_FACTION;
		}

		byte byte0 = dataManager.get(B_TYPE);
		byte0 = (byte) (byte0 & 0x0f);
		byte0 |= (byte) faction << 4;

		dataManager.set(B_TYPE, Byte.valueOf(byte0));
	}

	// ---------- name ----------

	protected void setFairyName(int prefix, int suffix) {
		if (prefix < 0) {
			prefix = 0;
		} else if (prefix > 15) {
			prefix = 15;
		}

		if (suffix < 0) {
			suffix = 0;
		} else if (suffix > 15) {
			suffix = 15;
		}

		byte byte0 = (byte) (((byte) prefix & 0x0f) | (((byte) suffix & 0x0f) << 4));
		dataManager.set(B_NAME_ORIG, Byte.valueOf(byte0));
	}

	public int getNamePrefix() {
		return (byte) dataManager.get(B_NAME_ORIG) & 0x0f;
	}

	public int getNameSuffix() {
		return (byte) (dataManager.get(B_NAME_ORIG) >> 4) & 0x0f;
	}

	public String getActualName(int prefix, int suffix) {
		final String custom = getCustomName();
		if (custom != null && !custom.isEmpty())
			return custom;

		if (prefix < 0 || prefix > MAX_NAMEIDX || suffix < 0 || suffix > MAX_NAMEIDX) {
			return "Error-name";
		} else {
			return name_prefixes[prefix] + "-" + name_suffixes[suffix];
		}
	}

	public ITextComponent getQueenName(int prefix, int suffix, int faction) {
		if (faction < 0 || faction > MAX_FACTION)
			return new TextComponentTranslation("text.queen.prefix").appendText(" Error-faction");

		return new TextComponentString(faction_colors[faction])
				.appendSibling(new TextComponentTranslation("text.queen.prefix"))
				.appendText(" " + getActualName(prefix, suffix));
	}

	public String getFactionName(int faction) {
		if (faction < 0 || faction > MAX_FACTION)
			return "Error-faction";

		return faction_colors[faction] + "<" + faction_names[faction] + ">";
	}

	@Override
	public String getName() {
		if (this.hasCustomName()) {
			return this.getCustomNameTag();
		} else {
			String s = EntityList.getEntityString(this);

			if (s == null) {
				s = "generic";
			}

			return I18n.translateToLocal("gn: entity." + s + ".name");
		}
	}

	@Override
	public ITextComponent getDisplayName() {
		TextComponentString textcomponentstring = new TextComponentString("");
		textcomponentstring.getStyle().setHoverEvent(this.getHoverEvent());
		textcomponentstring.getStyle().setInsertion(this.getCachedUniqueIdString());
		if (getFaction() != 0) {
			if (queen()) {
				textcomponentstring.appendSibling(getQueenName(getNamePrefix(), getNameSuffix(), getFaction()));
//						.appendText(" " + this.factionMembers.size());
			} else {
				String noRuler = "";
				if (getRulerId() == null) {
					noRuler = "??";
				}
				textcomponentstring.appendText(getFactionName(getFaction()) + " " + noRuler
						+ (rogue() ? "rogue " : "") 
						//+ getActualName(getNamePrefix(), getNameSuffix())
						+ "job: " + getJob() + "/"
						+ this.getHealth());
			}
		} else if (isTamed()) {
			String woosh = getActualName(getNamePrefix(), getNameSuffix());

			if (queen()) {
				textcomponentstring.appendSibling(new TextComponentTranslation("text.queen.prefix"))
						.appendText(" " + woosh);
			}

			if (isRuler(FairyFactions.proxy.getCurrentPlayer())) {
				woosh = (posted() ? "§a" : "§c") + "@§f" + woosh + (posted() ? "§a" : "§c") + "@";
			}
		}
		return textcomponentstring;
	}

	public String toString() {
		return getActualName(getNamePrefix(), getNameSuffix());
	}

	// ---------- B_FLAGS | B_FLAGS2 -----------

	protected boolean getFairyFlag(DataParameter<Byte> object, int offset) {
		return (dataManager.get(object) & (1 << offset)) != 0;
	}

	protected void setFairyFlag(DataParameter<Byte> object, int offset, boolean flag) {
		byte byte0 = dataManager.get(object);
		if (flag) {
			byte0 |= 1 << offset;
		} else {
			byte0 &= ~(1 << offset);
		}
		dataManager.set(object, Byte.valueOf(byte0));
	}

	protected static final int FLAG_ARM_SWING = 0;
	protected static final int FLAG_FLY_MODE = 1;
	protected static final int FLAG_CAN_FLAP = 2;
	protected static final int FLAG_TAMED = 3;
	protected static final int FLAG_ANGRY = 4;
	protected static final int FLAG_CRYING = 5;
	protected static final int FLAG_LIFTOFF = 6;
	protected static final int FLAG_HEARTS = 7;

	protected static final int FLAG2_CAN_HEAL = 0;
	protected static final int FLAG2_RARE_POTION = 1;
	protected static final int FLAG2_SPECIAL_JOB = 2;
	protected static final int FLAG2_NAME_ENABLED = 3;
	protected static final int FLAG2_CLIMBING = 4;
	protected static final int FLAG2_POSTED = 5;
	protected static final int FLAG2_WITHERED = 6;
	protected static final int FLAG2_HAIR_TYPE = 7;

	public boolean getArmSwing() {
		return getFairyFlag(B_FLAGS, FLAG_ARM_SWING);
	}

	public void armSwing(boolean flag) {
		setFairyFlag(B_FLAGS, FLAG_ARM_SWING, flag);
		setTempItem(null);
	}

	public boolean flymode() {
		return isFlying();
	}

	protected void setFlymode(boolean flag) {
		setFairyFlag(B_FLAGS, FLAG_FLY_MODE, flag);
	}

	public boolean canFlap() {
		return getFairyFlag(B_FLAGS, FLAG_CAN_FLAP);
	}

	protected void setCanFlap(boolean flag) {
		setFairyFlag(B_FLAGS, FLAG_CAN_FLAP, flag);
	}

	public boolean angry() {
		return getFairyFlag(B_FLAGS, FLAG_ANGRY);
	}

	protected void setAngry(boolean flag) {
		setFairyFlag(B_FLAGS, FLAG_ANGRY, flag);
	}

	public boolean crying() {
		return getFairyFlag(B_FLAGS, FLAG_CRYING);
	}

	protected void setCrying(boolean flag) {
		setFairyFlag(B_FLAGS, FLAG_CRYING, flag);
	}

	public boolean liftOff() {
		return getFairyFlag(B_FLAGS, FLAG_LIFTOFF);
	}

	protected void setLiftOff(boolean flag) {
		setFairyFlag(B_FLAGS, FLAG_LIFTOFF, flag);
	}

	public boolean hearts() {
		return getFairyFlag(B_FLAGS, FLAG_HEARTS);
	}

	public void setHearts(boolean flag) {
		setFairyFlag(B_FLAGS, FLAG_HEARTS, flag);
	}

	public boolean canHeal() {
		return getFairyFlag(B_FLAGS2, FLAG2_CAN_HEAL);
	}

	public void setCanHeal(boolean flag) {
		setFairyFlag(B_FLAGS2, FLAG2_CAN_HEAL, flag);
	}

	public boolean rarePotion() {
		return getFairyFlag(B_FLAGS2, FLAG2_RARE_POTION);
	}

	public void setRarePotion(boolean flag) {
		setFairyFlag(B_FLAGS2, FLAG2_RARE_POTION, flag);
	}

	public boolean specialJob() {
		return getFairyFlag(B_FLAGS2, FLAG2_SPECIAL_JOB);
	}

	public void setSpecialJob(boolean flag) {
		setFairyFlag(B_FLAGS2, FLAG2_SPECIAL_JOB, flag);
	}

	public boolean nameEnabled() {
		return getFairyFlag(B_FLAGS2, FLAG2_NAME_ENABLED);
	}

	public void setNameEnabled(boolean flag) {
		setFairyFlag(B_FLAGS2, FLAG2_NAME_ENABLED, flag);
	}

	public boolean climbing() {
		return getFairyFlag(B_FLAGS2, FLAG2_CLIMBING);
	}

	public void setClimbing(boolean flag) {
		setFairyFlag(B_FLAGS2, FLAG2_CLIMBING, flag);
	}

	public boolean posted() {
		return getFairyFlag(B_FLAGS2, FLAG2_POSTED);
	}

	public void setPosted(boolean flag) {
		postedCount = 0;
		setFairyFlag(B_FLAGS2, FLAG2_POSTED, flag);
	}

	public boolean withered() {
		return getFairyFlag(B_FLAGS2, FLAG2_WITHERED);
	}

	public void setWithered(boolean flag) {
		setFairyFlag(B_FLAGS2, FLAG2_WITHERED, flag);
	}

	public boolean hairType() {
		return getFairyFlag(B_FLAGS2, FLAG2_HAIR_TYPE);
	}

	public void setHairType(boolean flag) {
		setFairyFlag(B_FLAGS2, FLAG2_HAIR_TYPE, flag);
	}

	// ----------

	public boolean isRuler(EntityPlayer player) {
		if (player == null)
			return false;
		return isTamed() && rulerName().equals(player.getGameProfile().getName());
	}

	public String rulerName() {
		Entity owner = getOwner();
		if (owner != null) {
			return owner.getName();
		}
		return "";
	}

	public EntityLivingBase getRuler() {
		if (this.ruler != null) {
			return ruler;
		}
		try {
			UUID uuid = this.getRulerId();
			EntityLivingBase player = uuid == null ? null : this.world.getPlayerEntityByUUID(uuid);
			if (player != null) {
				this.ruler = player;
			} else {
				if (!world.isRemote) {
					WorldServer worldserver = (WorldServer) this.world;
					Entity entity = (EntityLivingBase) worldserver.getEntityFromUuid(uuid);
					if (entity instanceof EntityLivingBase) {
						this.ruler = (EntityLivingBase) entity;
					}
				}
			}
		} catch (IllegalArgumentException var2) {
			return null;
		}
		return this.ruler;
	}

	@Override
	public EntityLivingBase getOwner() {
		if (getRuler() != null) {
			return getRuler();
		}

		try {
			UUID uuid = this.getOwnerId();
			if (uuid != null) {
				return this.world.getPlayerEntityByUUID(uuid);
			}
		} catch (IllegalArgumentException var2) {
			return null;
		}

		return null;
	}

	@Nullable
	public UUID getRulerId() {
		return (UUID) ((Optional) this.dataManager.get(RULER_UNIQUE_ID)).orNull();
	}

	public void setRulerId(@Nullable UUID uuid) {
		this.dataManager.set(RULER_UNIQUE_ID, Optional.fromNullable(uuid));
	}

	public void setRuler(EntityFairy fairy) {
		this.setTamed(fairy != null ? true : false);
		this.ruler = fairy;
	}

	public void setRulerName(String s) {
	}

	// Custom name of the fairy, enabled by paper.
	public String getCustomName() {
		return dataManager.get(S_NAME_REAL);
	}

	public void setCustomName(String s) {
		if (!s.isEmpty())
			FairyFactions.LOGGER.info("setCustomName: " + this.getEntityId() + " = " + s);
		dataManager.set(S_NAME_REAL, s);
	}

	// A temporary item shown while arm is swinging, related to jobs.
	public Item getTempItem() {
		return Item.getItemById(dataManager.get(I_TOOL));
	}

	public void setTempItem(Item item) {
		dataManager.set(I_TOOL, Item.getIdFromItem(item));
	}

	// ----------

	/**
	 * NB: These name strings must match on client and server - so cannot simply be
	 * moved into the loc system. They CAN, however, be moved into config files when
	 * the time comes.
	 */

	private static final String name_prefixes[] = { "Silly", "Fire", "Twinkle", "Bouncy", "Speedy", "Wiggle", "Fluffy",
			"Cloudy", "Floppy", "Ginger", "Sugar", "Winky", "Giggle", "Cutie", "Sunny", "Honey" };

	private static final String name_suffixes[] = { "puff", "poof", "butt", "munch", "star", "bird", "wing", "shine",
			"snap", "kins", "bee", "chime", "button", "bun", "heart", "boo" };

	private static final String faction_colors[] = { "§0", "§1", "§2", "§3", "§4", "§5", "§6", "§7", "§8", "§9", "§a",
			"§b", "§c", "§d", "§e", "§f" };

	private static final String faction_names[] = { "no queen", Loc.FACTION_1.get(), Loc.FACTION_2.get(),
			Loc.FACTION_3.get(), Loc.FACTION_4.get(), Loc.FACTION_5.get(), Loc.FACTION_6.get(), Loc.FACTION_7.get(),
			Loc.FACTION_8.get(), Loc.FACTION_9.get(), Loc.FACTION_10.get(), Loc.FACTION_11.get(), Loc.FACTION_12.get(),
			Loc.FACTION_13.get(), Loc.FACTION_14.get(), Loc.FACTION_15.get() };

	// ---------- stubs ----------

	private void processSwinging() {
		if (getArmSwing() != didSwing) {
			didSwing = !didSwing;
			// if(!isSwinging || swingProgressInt >= 3 || swingProgressInt < 0)
			// {
			swingProgressInt = -1;
			isSwinging = true;
			tempItem = null;
			// }
		}

		if (isSwinging) {
			swingProgressInt++;

			if (swingProgressInt >= 6) {
				swingProgressInt = 0;
				isSwinging = false;

				if (tempItem != null && tempItem != fishingStick) {
					tempItem = null;
				}
			} else if (tempItem == null && getTempItem() != null) {
				tempItem = new ItemStack(getTempItem(), 1, 0);
			}
		}

		swingProgress = (float) swingProgressInt / 6F;

		if (!isSitting() && tempItem != null && tempItem == fishingStick) {
			tempItem = null;
		}
	}

	private boolean checkGroundBelow() {
		int a = MathHelper.floor(posX);
		int b = MathHelper.floor(getEntityBoundingBox().minY);
		int b1 = MathHelper.floor(getEntityBoundingBox().minY - 0.5D);
		int c = MathHelper.floor(posZ);

		if (!isAirySpace(a, b - 1, c) || !isAirySpace(a, b1 - 1, c)) {
			return true;
		}

		return false;
	}

	private void showHeartsOrSmokeFX(boolean flag) {
		final EnumParticleTypes particle = (flag ? EnumParticleTypes.HEART : EnumParticleTypes.SMOKE_NORMAL);

		for (int i = 0; i < 7; i++) {
			double d = rand.nextGaussian() * 0.02D;
			double d1 = rand.nextGaussian() * 0.02D;
			double d2 = rand.nextGaussian() * 0.02D;
			world.spawnParticle(particle, (posX + (double) (rand.nextFloat() * width * 2.0F)) - (double) width,
					posY + 0.5D + (double) (rand.nextFloat() * height),
					(posZ + (double) (rand.nextFloat() * width * 2.0F)) - (double) width, d, d1, d2);
		}
	}

	/**
	 * This is really confusing. The original reads from byte 19 then writes out to
	 * byte 22.
	 *
	 * This must have been a bug.
	 *
	 * TODO: figure out what this was supposed to do
	 */
	protected void setFairyHealth(int i) {
		byte byte0 = dataManager.get(B_HEALTH);

		if (i < 0) {
			i = 0;
		} else if (i > 255) {
			i = 255;
		}

		byte0 = (byte) ((byte) i & 0xff);
		dataManager.set(B_HEALTH, Byte.valueOf(byte0));
	}

	public int fairyHealth() {
		return (byte) dataManager.get(B_HEALTH) & 0xff;
	}

	protected void setFairyClimbing(boolean flag) {
		setClimbing(flag);
	}

	private void updateWithering() {
		if (rogue()) {
			return;
		}

		witherTime++;

		if (withered()) {
			// Deplete Health Very Quickly.
			if (witherTime >= 8) {
				witherTime = 0;

				if (getHealth() > 1) {
					heal(-1);
				}

				if (world.isDaytime()) {
					final int a = MathHelper.floor(posX);
					final int b = MathHelper.floor(getEntityBoundingBox().minY);
					final int c = MathHelper.floor(posZ);
					final float f = getBrightness();
					final BlockPos pos = new BlockPos(a, b, c);

					if (f > 0.5F && world.canBlockSeeSky(pos) && rand.nextFloat() * 5F < (f - 0.4F) * 2.0F) {
						setWithered(false);

						if (isTamed()) {
							setHearts(!didHearts);
						}

						witherTime = 0;
						return;
					}
				}
			}

			setWithered(true);
		} else {
			if (witherTime % 10 == 0) {
				final int a = MathHelper.floor(posX);
				final int b = MathHelper.floor(getEntityBoundingBox().minY);
				final int c = MathHelper.floor(posZ);
				final float f = getBrightness();
				final BlockPos pos = new BlockPos(a, b, c);

				if (f > 0.05F || world.canBlockSeeSky(pos)) {
					witherTime = rand.nextInt(3);
				} else if (witherTime >= 900) {
					setWithered(true);
					witherTime = 0;
					return;
				}
			}

			setWithered(false);
		}
	}

	public boolean isAirySpace(int a, int b, int c) {
		if (b < 0 || b >= world.getHeight()) {
			return false;
		}

		IBlockState blockState = world.getBlockState(new BlockPos(a, b, c));
		Block block = blockState.getBlock();

		if (block == null || block == Blocks.AIR)
			return true;

		Material matt = blockState.getMaterial();

		if (matt == null || matt == Material.AIR || matt == Material.PLANTS || matt == Material.VINE
				|| matt == Material.FIRE || matt == Material.CIRCUITS || matt == Material.SNOW) {
			return true;
		}

		return false;
	}

	private boolean checkFlyBlocked() {
		int a = MathHelper.floor(posX);
		int b = MathHelper.floor(getEntityBoundingBox().minY);
		int c = MathHelper.floor(posZ);

		if (!isAirySpace(a, b + 1, c) || !isAirySpace(a, b + 2, c)) {
			return true;
		}

		return false;
	}

	/**
	 * TODO: Update AI
	 *
	 * // non-consistent method names
	 *
	 * @Override
	 */
	public void setTarget(Entity entity) {
		if (entity == null || entityToAttack == null || entity != entityToAttack) {
			loseInterest = 0;
		}

		/**
		 * TODO: Update AI entityToAttack = entity;
		 */
	}

	// Checks to see if a fairy is their comrade.
	private boolean sameTeam(EntityFairy fairy) {
		if (isTamed()) {
			return fairy.isTamed() && fairy.getFaction() == 0 && fairy.rulerName().equals(this.rulerName());
		} else if (getFaction() > 0) {
			return fairy.getFaction() == this.getFaction();
		}

		return false;
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		ItemStack itemstack = player.getHeldItemMainhand();

		if (!this.world.isRemote) {
			if (getRidingEntity() == null || getRidingEntity() == player
					|| getRidingEntity() instanceof EntityMinecart) {
				ItemStack stack = player.inventory.getCurrentItem();
				// TODO: spawn eggs aren't producing queens. Add a EntityAgeable
				// style interaction to get them, the way you can spawn baby cows...

				if (isRuler(player)) {
					if (stack != null && getHealth() < getMaxHealth() && acceptableFoods(stack.getItem())
							&& stack.getCount() > 0) {
						// right-clicking sugar or glistering melons will heal
						stack.shrink(1);

						if (stack.getCount() <= 0) {
							player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
						}

						setHearts(!hearts());

						if (stack.getItem() == Items.SUGAR) {
							heal(5);
						} else {
							heal(99);

							if (stack.getItem() == Items.SPECKLED_MELON) {
								setWithered(false);
								witherTime = 0;
							}
						}

						return true;
					} else if (stack != null && haircutItem(stack.getItem()) && stack.getCount() > 0 && !rogue()) {
						// right-clicking with shears will toggle haircut on non-rogues
						setHairType(!hairType());
						return true;
					} else if (stack != null && getRidingEntity() == null && !isSitting()
							&& vileSubstance(stack.getItem()) && stack.getCount() > 0) {
						// right-clicking with something nasty will untame
						dropItem(stack.getItem(), 1);
						stack.shrink(1);

						if (stack.getCount() <= 0) {
							player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
						}

						disband();
						return true;
					} else if (onGround && stack != null && namingItem(stack.getItem()) && stack.getCount() > 0) {
						FairyFactions.LOGGER.info("EntityFairy.interract: consuming paper and setting name enabled");

						// right-clicking with paper will open the rename gui
						stack.shrink(1);

						if (stack.getCount() <= 0) {
							player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
						}

						setSitting(true);
						setNameEnabled(true);
						isJumping = false;
						this.getNavigator().clearPath();
						setTarget((Entity) null);
						entityFear = null;
					} else {
						if (isSitting()) {
							if (stack != null && realFreshOysterBars(stack.getItem()) && stack.getCount() > 0) {
								// right-clicking magma cream on seated fairy invokes
								// "hydra"
								hydraFairy();
							} else {
								// right-clicking a seated fairy makes it stand up
								// setSitting(false);
							}

							return true;
						} else if (player.isSneaking()) {
							if (flymode() || !onGround) {
								// shift-right-clicking while flying aborts flight
								flapEnergy = 0;
							} else {
								// shift-right-clicking otherwise makes fairy sit down
								setSitting(true);
								isJumping = false;
								this.getNavigator().clearPath();
								setTarget((Entity) null);
								entityFear = null;
							}
						} else if (stack == null || !snowballItem(stack.getItem())) {
							// otherwise, right-clicking wears a fairy hat
							FairyFactions.proxy.sendFairyMount(this, player);
							setFlymode(true);
							flapEnergy = 200;
							setCanFlap(true);
							return true;
						}
					}
				} else {
					// faction members can be tamed in peaceful
					if ((getFaction() == 0 || world.getDifficulty() == EnumDifficulty.PEACEFUL)
							&& !((queen() || posted()) && isTamed()) && !crying() && !angry() && stack != null
							&& acceptableFoods(stack.getItem()) && stack.getCount() > 0) {
						stack.shrink(1);

						if (stack.getCount() <= 0) {
							player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
						}

						if (stack.getItem() != Items.SUGAR || rand.nextInt(4) == 0) {
							if (stack.getItem() == Items.SUGAR) {
								heal(5);
							} else {
								heal(99);
							}

							tameMe(player);
							return true;
						} else {
							setHearts(!hearts());
							return true;
						}
					} else if (!isTamed()) {
						setHearts(!hearts());
					}

					tameFailMessage(player);
					return true;
				}
			}

			itemstack.damageItem(1, player);
		}

		return super.processInteract(player, hand);
	}

	// Foods that can be used for taming
	public boolean acceptableFoods(Item i) {
		if (i == Items.SPECKLED_MELON) {
			return true;
		} else if (isTamed() || !queen()) {
			return TAME_ITEMS.contains(i);
		}
		return false;
	}

	// Things used for disbanding
	public boolean vileSubstance(Item i) {
		return VILE_ITEMS.contains(i);
	}

	// The quickest way to Daphne
	public boolean realFreshOysterBars(Item i) {
		return i == Items.MAGMA_CREAM;
	}

	// Item used to rename a fairy, paper
	public boolean namingItem(Item i) {
		return i == Items.PAPER;
	}

	// Is the item a snowball or not.
	public boolean snowballItem(Item i) {
		return i == Items.SNOWBALL;
	}

	// Can the item give a haircut.
	public boolean haircutItem(Item i) {
		return i == Items.SHEARS;
	}

	private void disband() {
		setRulerName("");
		setFaction(0);
		setHearts(!didHearts);
		cryTime = 200;
		setNameEnabled(false); // Leaving this bit set causes strange behavior
		setTamed(false);
		setCustomName("");
		abandonPost();
		snowballin = 0;

		if (ruler != null) {
			Path dest = roam(ruler, this, (float) Math.PI);

			/*
			 * if (dest != null) { setPathToEntity(dest); }
			 */
			this.getNavigator().setPath(dest, this.getAIMoveSpeed());

			if (ruler instanceof EntityPlayer) {
				//
				String s = getActualName(getNamePrefix(), getNameSuffix()) + " ";

				if (queen()) {
					s = new TextComponentTranslation("text.queen.prefix") + " " + s;
				}

				int i = rand.nextInt(6);

				if (queen() && i < 3) {
					s += Loc.DISBAND_QUEEN_1;
				} else if (queen()) {
					s += Loc.DISBAND_QUEEN_2;
				} else if (i == 0) {
					s += Loc.DISBAND_OTHER_1;
				} else if (i == 1) {
					s += Loc.DISBAND_OTHER_2;
				} else if (i == 2) {
					s += Loc.DISBAND_OTHER_3;
				} else if (i == 3) {
					s += Loc.DISBAND_OTHER_4;
				} else if (i == 4) {
					s += Loc.DISBAND_OTHER_5;
				} else {
					s += Loc.DISBAND_OTHER_6;
				}

				FairyFactions.proxy.sendChat((EntityPlayerMP) ruler, "* §9" + s);
			}
		}

		ruler = null;
	}

	public void tameMe(EntityPlayer player) {
		if (player == null) {
			return;
		}

		setFaction(0);
		setNameEnabled(false); // Leaving this bit set causes strange behavior
		setTamedBy(player);
		setRulerName(player.getGameProfile().getName());
		setHearts(!hearts());
		abandonPost();
		snowballin = 0;
		ruler = player;

		if (scout()) {
			cower = false;
		}

		String f = getActualName(getNamePrefix(), getNameSuffix()) + " ";

		if (queen()) {
			f = new TextComponentTranslation("text.queen.prefix") + " " + f;
		}

		String s = f;
		int i = rand.nextInt(6);

		if (queen() && i < 2) {
			s += Loc.TAME_QUEEN_1;
		} else if (queen() && i > 3) {
			s += Loc.TAME_QUEEN_2;
		} else if (queen()) {
			s += Loc.TAME_QUEEN_3;
		} else if (i == 0) {
			s += Loc.TAME_OTHER_1;
		} else if (i == 1) {
			s += Loc.TAME_OTHER_2;
		} else if (i == 2) {
			s += Loc.TAME_OTHER_3;
		} else if (i == 3) {
			s += Loc.TAME_OTHER_4;
		} else if (i == 4) {
			s += Loc.TAME_OTHER_5;
		} else {
			s += Loc.TAME_OTHER_6;
		}

		if (player instanceof EntityPlayerMP) {
			FairyFactions.proxy.sendChat((EntityPlayerMP) player, "* §9" + s);
		}

		FairyFactions.LOGGER.info("tameMe: " + rulerName() + ": " + this);
	}

	public void alertFollowers(Entity entity) {
		if (queen() && getFaction() > 0) {
			List<?> list = world.getEntitiesWithinAABB(EntityFairy.class, getEntityBoundingBox().expand(40D, 40D, 40D));

			for (int j = 0; j < list.size(); j++) {
				EntityFairy fairy = (EntityFairy) list.get(j);

				if (fairy != this && fairy.getHealth() > 0 && sameTeam(fairy)
						&& (fairy.ruler == null || fairy.ruler == this)) {
					if (fairy.getRidingEntity() != null) {
						FairyFactions.proxy.sendFairyMount(fairy, fairy.getRidingEntity());
					}

					fairy.setTarget((Entity) null);
					fairy.cryTime = 300;
					fairy.setFaction(0);
					// if(entity != null && entity instanceof EntityLiving) {
					fairy.entityFear = null;
					// }
					fairy.ruler = null;
				}
			}
		}
	}

	public void alertRuler(Entity entity) {
		if (getFaction() > 0 && ruler != null && ruler instanceof EntityFairy && sameTeam((EntityFairy) ruler)) {
			EntityFairy queen = ((EntityFairy) ruler);
			boolean flag = false;
			List<?> list = world.getEntitiesWithinAABB(EntityFairy.class,
					queen.getEntityBoundingBox().expand(40D, 40D, 40D));

			for (int j = 0; j < list.size(); j++) {
				EntityFairy fairy = (EntityFairy) list.get(j);

				if (fairy != queen && fairy.getHealth() > 0 && queen.sameTeam(fairy)
						&& (fairy.ruler == null || fairy.ruler == queen)) {
					flag = true;
					break;
				}
			}

			if (!flag) {
				queen.setTarget((Entity) null);
				queen.cryTime = 600;
				queen.setFaction(0);
				// if(entity != null && entity instanceof EntityLiving) {
				queen.entityFear = null;
				// }
			}
		} else if (isTamed() && ruler != null && ruler instanceof EntityPlayerMP) {
			TextComponentString named = new TextComponentString("");
			// EntityPlayerMP player = (EntityPlayerMP)ruler;

			if (queen()) {
				named.appendSibling(new TextComponentTranslation("text.queen.prefix").appendText(" "));
			}
			named.appendText(getActualName(getNamePrefix(), getNameSuffix()));
			String s = named.getUnformattedComponentText();
			int i = rand.nextInt(7);

			if (i == 0) {
				s += " " + Loc.DEATH_1;
			} else if (i == 1) {
				s += " " + Loc.DEATH_2;
			} else if (i == 2) {
				s += " " + Loc.DEATH_3;
			} else if (i == 3) {
				s += " " + Loc.DEATH_4;
			} else if (i == 4) {
				s += " " + Loc.DEATH_5;
			} else if (i == 5) {
				s += " " + Loc.DEATH_6;
			} else {
				s += " " + Loc.DEATH_7;
			}

			// mod_FairyMod.fairyMod.sendDisband(player, "* §c" + s);
			FairyFactions.proxy.sendChat((EntityPlayerMP) ruler, "* §c" + s);
		}
	}

	// Don't let that spider bite you, spider bite hurt.
	public void hydraFairy() {
		final AxisAlignedBB bb = getEntityBoundingBox();
		final double ySize = bb.maxY - bb.minY;
		final double a = (bb.minX + bb.maxX) / 2D;
		final double b = (bb.minY + (double) this.getYOffset()) - ySize;
		final double c = (bb.minZ + bb.maxZ) / 2D;
		motionX = 0D;
		motionY = -0.1D;
		motionZ = 0D;
		// Anthony stopped to tie his shoe, and they all went marching on.
		isJumping = false;
		moveForward = 0F;
		moveStrafing = 0F;
		this.getNavigator().clearPath();
		setSitting(true);
		onGround = true;
		List<?> list = world.getEntitiesWithinAABB(EntityFairy.class, getEntityBoundingBox().expand(80D, 80D, 80D));

		for (int j = 0; j < list.size(); j++) {
			EntityFairy fairy = (EntityFairy) list.get(j);

			if (fairy != this && fairy.getHealth() > 0 && sameTeam(fairy) && fairy.getRidingEntity() == null
					&& fairy.getPassengers().isEmpty()) {
				fairy.setTarget((Entity) null);
				fairy.cryTime = 0;
				fairy.entityFear = null;
				// I'll pay top dollar for that Gidrovlicheskiy in the window.
				fairy.setPosition(a, b, c);
				fairy.motionX = 0D;
				fairy.motionY = -0.1D;
				fairy.motionZ = 0D;
				fairy.isJumping = false;
				fairy.moveForward = 0F;
				fairy.moveStrafing = 0F;
				fairy.getNavigator().clearPath();
				fairy.setSitting(true);
				fairy.onGround = true;
				// It feels like I'm floating but I'm not
				fairy.setFlymode(false);
			}
		}
	}

	/**
	 * TODO: Update AI.
	 *
	 * @Override protected boolean isMovementCeased() { return isSitting(); //
	 *           renderYawOffset = prevRenderYawOffset = rotationYaw; //
	 *           rotationPitch = 10F; // return true; }
	 */

	@Override
	public boolean isOnLadder() {
		return climbing();
	}

	/**
	 * TODO: Update AI
	 *
	 * @Override
	 */
	protected void attackEntity(Entity entity, float f) {
		if (attackTime <= 0 && f < (isTamed() ? 2.5F : 2.0F)
				&& ((entity.getEntityBoundingBox().maxY > getEntityBoundingBox().minY
						&& entity.getEntityBoundingBox().minY < getEntityBoundingBox().maxY) || f == 0F)) {
			attackTime = 20;

			if (flymode() && canFlap() && scout() && entity instanceof EntityLivingBase && getRidingEntity() == null
					&& getPassengers().isEmpty() && entity.getRidingEntity() == null && entity.getPassengers().isEmpty()
					&& !(entity instanceof EntityFairy || entity instanceof EntityFlying)) {
				// Scout's Totally Leet Air Attack.
				FairyFactions.proxy.sendFairyMount(this, entity);
				setFlymode(true);
				flapEnergy = 200;
				setCanFlap(true);
				attackTime = 35;
			} else {
				if (scout() && getRidingEntity() != null && entity != null && entity == getRidingEntity()) {
					// The finish of its air attack.
					FairyFactions.proxy.sendFairyMount(this, entity);
					attackTime = 35;
				}

				// normal boring strike.
				doAttack(entity);
			}
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float damage) {
		// Prevents the fairy from recieving any damage if on the tamer's
		// head.
		if (ruler != null && getRidingEntity() != null && getRidingEntity() == ruler) {
			return false;
		}
		boolean ignoreTarget = false;
		Entity entity = damagesource.getTrueSource();

		if (entity != null) {
			if (ruler != null && ruler == entity && ruler instanceof EntityPlayer) {
				if (snowballFight(damagesource)) {
					// Prevents a fairy from being damaged by its ruler at all if
					// it's a player.
					return false;
				} else {
					ignoreTarget = true;
				}
			}

			if (isTamed() && !rulerName().equals("")) {
				if (entity instanceof EntityPlayer && isRuler((EntityPlayer) entity)) {
					if (!ignoreTarget && snowballFight(damagesource)) {
						// Another handler made for sitting fairies just in case.
						return false;
					} else {
						ignoreTarget = true;
					}
				} else if (entity instanceof EntityWolf && ((EntityWolf) entity).isTamed()
						&& isRuler((EntityPlayer) ((EntityWolf) entity).getOwner())) {
					// Protects against ruler-owned wolves.
					EntityWolf wolf = (EntityWolf) entity;
					/**
					 * TODO: Update AI
					 *
					 * wolf.setTarget((Entity) null);
					 */
					return false;
				}
			}
		}
		if ((guard() || queen()) && damage > 1) {
			// Guards and queens receive two thirds damage, won't reduce to 0 if
			// it was at least 1 to begin with.
			damage *= 2;
			damage /= 3;
			damage = Math.max(damage, 1);
		}

		boolean flag = super.attackEntityFrom(damagesource, damage);
		// Stop them from running really fast
		/**
		 * TODO: Update AI
		 *
		 * fleeingTick = 0;
		 */

		if (flag && getHealth() > 0) {
			if (entity != null) {
				if (entity instanceof EntityLivingBase && !ignoreTarget) {
					if (entityToAttack == null && cower && rand.nextInt(2) == 0) {
						// Cowering fairies will have a chance of becoming
						// offensive.
						cryTime += 120;
						entityFear = (EntityLivingBase) entity;
						Path dest = roam(entity, this, (float) Math.PI);

						/*
						 * if (dest != null) { setPathToEntity(dest); }
						 */
						this.getNavigator().setPath(dest, this.getAIMoveSpeed());
					} else {
						// Become aggressive - no more screwing around.
						setTarget((Entity) entity);
						entityFear = null;
						cryTime = 0;
					}
				} else {
					// This just makes fairies run from inanimate objects that
					// hurt them.
					Path dest = roam(entity, this, (float) Math.PI);

					/*
					 * if (dest != null) { setPathToEntity(dest); }
					 */
					this.getNavigator().setPath(dest, this.getAIMoveSpeed());
				}
			}

			// A fairy will get up if hurt while sitting.
			if (isSitting()) {
				setSitting(false);
			}

			if (getRidingEntity() != null && rand.nextInt(2) == 0) {
				FairyFactions.proxy.sendFairyMount(this, getRidingEntity());
			}
		} else if (flag) {
			if (getRidingEntity() != null) {
				FairyFactions.proxy.sendFairyMount(this, getRidingEntity());
			}

			if (queen() && !isTamed()) {
				alertFollowers(entity);
			} else {
				alertRuler(entity);
			}
		}

		return flag;
	}

	protected boolean doAttack(Entity entity) {
		// Swings arm and attacks.
		armSwing(!didSwing);

		boolean flag = entity.attackEntityFrom(DamageSource.causeMobDamage(this), attackStrength());

		if (flag && rogue() && healTime <= 0 && entity != null && entity instanceof EntityLivingBase) {
			applyPoison((EntityLivingBase) entity);
		}

		return flag;
	}

	protected int attackStrength() {
		// Self explanatory.
		if (queen()) {
			return 5;
		} else if (guard()) {
			return 4;
		} else if (rogue()) {
			return 3;
		} else {
			return 2;
		}
	}

	public void applyPoison(EntityLivingBase entityliving) {
		byte duration = 0;
		switch (world.getDifficulty()) {
		case NORMAL:
			duration = 7;
			break;
		case HARD:
			duration = 15;
			break;
		case PEACEFUL:
		case EASY:
		default:
			// no poison in peaceful or normal
			return;
		}

		int effect = rand.nextInt(3);
		/*
		 * if (effect == 0) { effect = Potion.poison.id; } else if (effect == 1) {
		 * effect = Potion.weakness.id; } else { effect = Potion.blindness.id; }
		 *
		 * entityliving.addPotionEffect(new PotionEffect(effect, duration * 20, 0));
		 */
		healTime = 100 + rand.nextInt(100);
		setTarget((Entity) null);
		entityFear = entityliving;
		cryTime = healTime;
	}

	public void tameFailMessage(EntityPlayer player) {
		String s = Loc.TAME_FAIL_PREFIX + " ";

		if (angry()) {
			s += Loc.TAME_FAIL_ANGRY;
		} else if (crying()) {
			s += Loc.TAME_FAIL_CRYING;
		} else if (getFaction() > 0) {
			if (queen()) {
				s += Loc.TAME_FAIL_HAS_FOLLOWERS;
			} else {
				s += Loc.TAME_FAIL_HAS_QUEEN;
			}
		} else if (isTamed() && queen()) {
			s += Loc.TAME_FAIL_TAME_QUEEN;
		} else if (posted()) {
			s += Loc.TAME_FAIL_POSTED;
		} else {
			ItemStack stack = (ItemStack) null;

			if (player.inventory != null) {
				stack = player.inventory.getCurrentItem();
			}

			if (stack != null && stack.getCount() > 0 && stack.getItem() == Items.GLOWSTONE_DUST) {
				s += Loc.TAME_FAIL_GLOWSTONE;
			} else if (queen()) {
				s += Loc.TAME_FAIL_NOT_MELON;
			} else {
				s += Loc.TAME_FAIL_NOT_SNACK;
			}
		}

		if (player instanceof EntityPlayerMP) {
			FairyFactions.proxy.sendChat((EntityPlayerMP) player, "* §9" + s);
		}
	}

	/**
	 * TODO: cache results similar to RenderFairy's lookup
	 *
	 * @param skin which type of fairy is this?
	 * @return
	 */
	@SideOnly(Side.CLIENT)
	public ResourceLocation getTexture(int skin) {
		final String texturePath = "textures/entities/" + this.getTextureName(skin) + ".png";
		return new ResourceLocation(Version.ASSET_PREFIX, texturePath);
	}
	@SideOnly(Side.CLIENT)
	public String getTextureName(int skin) {
		final String texturePath;
		if (getCustomName().equals("Steve")) {
			texturePath = "notFairy";
		} else {
			final int idx;
			if (skin < 0) {
				idx = 1;
			} else if (skin > 3) {
				idx = 4;
			} else {
				idx = skin + 1;
			}
			texturePath = "fairy" + (queen() ? "q" : "") + idx;
		}
		return texturePath;
	}
	public Entity getFishEntity() {
		return fishEntity;
	}

	public void setFishEntity(FairyEntityFishHook fishEntity) {
		this.fishEntity = fishEntity;
	}

	public EntityLivingBase getEntityFear() {
		return entityFear;
	}

	public void setEntityFear(EntityLivingBase entityFear) {
		this.entityFear = entityFear;
	}

	public int getCryTime() {
		return cryTime;
	}

	public void setCryTime(int cryTime) {
		this.cryTime = cryTime;
	}

	public float getFlapEnergy() {
		return flapEnergy;
	}

	public void setFlapEnergy(int flyTime) {
		this.flapEnergy = flyTime;
	}

	public boolean willCower() {
		return cower;
	}

	public void setCower(boolean cower) {
		this.cower = cower;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return FairySounds.getSoundEvent("entity.fairy.idle");
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return FairySounds.getSoundEvent("entity.fairy.hurt");
	}

	@Override
	protected SoundEvent getDeathSound() {
		return FairySounds.getSoundEvent("entity.fairy.death");
	}

	@Override
	protected void playStepSound(BlockPos pos, Block blockIn) {
		this.playSound(SoundEvents.ENTITY_CHICKEN_STEP, 0.15F, 1.0F);
	}

	@Override
	public EntityAgeable createChild(EntityAgeable parent) {
		// No fairy breeding.
		return null;
	}

	@Override
	public boolean isBreedingItem(ItemStack itemstack) {
		return false;
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 1;
	}

	@Override
	public boolean getCanSpawnHere() {
		if (super.getCanSpawnHere()) {
			final int x = MathHelper.floor(posX);
			final int z = MathHelper.floor(posZ);
			final BlockPos pos = new BlockPos(x, 64, z);
			final Biome biome = world.getBiomeForCoordsBody(pos);

			if (biome != null
					/* && (biome.minHeight > -0.25F) && (biome.maxHeight <= 0.5F) */
					&& biome.getDefaultTemperature() >= 0.1F && biome.getDefaultTemperature() <= 1.0F
					&& biome.getRainfall() > 0.0F && biome.getRainfall() <= 0.8F) {
				List<?> list = world.getEntitiesWithinAABB(EntityFairy.class,
						this.getEntityBoundingBox().expand(32D, 32D, 32D));
				if (world.isRemote) {
					LOGGER.debug("world is remote");
				}

				if ((list == null || list.size() < 1) && !world.isRemote) {
					setJob(0);
					setSpecialJob(true);
					heal(30);
					setHealth(30);
					int i = rand.nextInt(15) + 1;
					setFaction(i);
					setSkin(rand.nextInt(4));
					cower = false;
					createGroup = true;
				}

				return true;
			}
		}

		return false;
	}
}
