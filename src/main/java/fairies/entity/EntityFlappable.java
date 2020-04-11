package fairies.entity;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import fairies.ai.EntityFairyFlyHelper;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityFlyHelper;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class EntityFlappable extends EntityTameable {
	private static final DataParameter<Integer> VARIANT = EntityDataManager.<Integer>createKey(EntityFlappable.class,
			DataSerializers.VARINT);
	private static final Item DEADLY_ITEM = Items.COOKIE;
	protected static final Set<Item> TAME_ITEMS = Sets.newHashSet(Items.SPECKLED_MELON, Items.APPLE, Items.MELON,
			Items.SUGAR, Items.CAKE, Items.COOKIE);
	protected static final Set<Item> VILE_ITEMS = Sets.newHashSet(Items.SLIME_BALL, Items.ROTTEN_FLESH,
			Items.SPIDER_EYE, Items.FERMENTED_SPIDER_EYE);
	public static final int MAX_VARIANT = 3;

	public float flapMaxEnergy = 200F;
	public float flapDecay = 1.0F;
	public int flapDelay = 15;

	public float flap;
	public float flapSpeed;
	public float oFlapSpeed;
	public float oFlap;
	public float flapEnergy;
	public int delayFlap;

	public float flapping = 1.0F;

	public EntityFlappable(World worldIn) {
		super(worldIn);
		this.moveHelper = new EntityFairyFlyHelper(this);
	}

	/**
	 * Called only once on an entity when first time spawned, via egg, mob spawner,
	 * natural spawning etc, but not called when entity is reloaded from nbt. Mainly
	 * used for initializing attributes and inventory.
	 * 
	 * The livingdata parameter is used to pass data between all instances during a
	 * pack spawn. It will be null on the first call. Subclasses may check if it's
	 * null, and then create a new one and return it if so, initializing all
	 * entities in the pack with the contained data.
	 * 
	 * @return The IEntityLivingData to pass to this method for other instances of
	 *         this entity class within the same pack
	 * 
	 * @param difficulty The current local difficulty
	 * @param livingdata Shared spawn data. Will usually be null. (See return value
	 *                   for more information)
	 */
	@Nullable
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		this.setVariant(this.rand.nextInt(4));
		this.flapEnergy = this.flapMaxEnergy;
		return super.onInitialSpawn(difficulty, livingdata);
	}

	/*
	 * protected void initEntityAI() { this.aiSit = new EntityAISit(this);
	 * this.tasks.addTask(0, new EntityAIPanic(this, 1.25D)); this.tasks.addTask(0,
	 * new EntityAISwimming(this)); this.tasks.addTask(1, new
	 * EntityAIWatchClosest(this, EntityPlayer.class, 8.0F)); this.tasks.addTask(2,
	 * this.aiSit); this.tasks.addTask(2, new EntityAIFollowOwnerFlying(this, 1.0D,
	 * 5.0F, 1.0F)); this.tasks.addTask(2, new EntityAIWanderAvoidWaterFlying(this,
	 * 1.0D)); this.tasks.addTask(3, new EntityAIFollow(this, 1.0D, 3.0F, 7.0F)); }
	 */
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(6.0D);
		this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(0.4000000059604645D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224D);
	}

	/**
	 * Returns new PathNavigateGround instance
	 */
	protected PathNavigate createNavigator(World worldIn) {
		PathNavigateFlying pathnavigateflying = new PathNavigateFlying(this, worldIn);
		pathnavigateflying.setCanOpenDoors(false);
		pathnavigateflying.setCanFloat(true);
		pathnavigateflying.setCanEnterDoors(true);
		return pathnavigateflying;
	}

	/**
	 * Called frequently so the entity can update its state every tick as required.
	 * For example, zombies and skeletons use this to react to sunlight and start to
	 * burn.
	 */
	public void onLivingUpdate() {
		super.onLivingUpdate();
		this.calculateFlapping();
		this.calculateFlyOrWalk();
	}

	public boolean canFlap() {
		if (this.flapDecay > 0) {
			if (this.flapEnergy <= 0) {
				return false;
			}
		}
		return true;
	}

	private void calculateFlyOrWalk() {

		if (!this.isFlying()) {
			if (rand.nextInt(50) == 0) {
				this.jump();
			}
		}

	}

	private void calculateFlapping() {
		if (canFlap() && this.delayFlap == 0) {
			this.oFlap = this.flap;
			this.oFlapSpeed = this.flapSpeed;
			this.flapSpeed = (float) ((double) this.flapSpeed + (double) (this.onGround ? -1 : 4) * 0.3D);
			this.flapSpeed = MathHelper.clamp(this.flapSpeed, 0.0F, 1.0F);
			if (this.flapDecay > 0) {
				this.flapEnergy = MathHelper.clamp(this.flapEnergy - this.flapSpeed, 0, this.flapMaxEnergy);

			}
			if (!this.onGround && this.flapping < 1.0F) {
				this.flapping = 1.0F;
			}

			this.flapping = (float) ((double) this.flapping * 0.9D);

			if (!this.onGround && this.motionY < 0.0D) {
		//		this.motionY += this.flapSpeed;
			}

			this.flap += this.flapping * 2.0F;
			this.delayFlap = MathHelper.clamp(this.delayFlap--, 0, this.flapDelay);
		}
		// regen flap health
		if ((!this.isFlying() || !this.canFlap()) && this.flapEnergy < this.flapMaxEnergy) {
			this.flapEnergy = MathHelper.clamp(this.flapEnergy + this.flapDecay, 0, this.flapMaxEnergy);
		}
	}

	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		ItemStack itemstack = player.getHeldItem(hand);

		if (!this.isTamed() && TAME_ITEMS.contains(itemstack.getItem())) {
			if (!player.capabilities.isCreativeMode) {
				itemstack.shrink(1);
			}

			if (!this.isSilent()) {
				this.world.playSound((EntityPlayer) null, this.posX, this.posY, this.posZ,
						SoundEvents.ENTITY_PARROT_EAT, this.getSoundCategory(), 1.0F,
						1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
			}

			if (!this.world.isRemote) {
				if (this.rand.nextInt(10) == 0
						&& !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player)) {
					this.setTamedBy(player);
					this.playTameEffect(true);
					this.world.setEntityState(this, (byte) 7);
				} else {
					this.playTameEffect(false);
					this.world.setEntityState(this, (byte) 6);
				}
			}

			return true;
		} else if (itemstack.getItem() == DEADLY_ITEM) {
			if (!player.capabilities.isCreativeMode) {
				itemstack.shrink(1);
			}

			this.addPotionEffect(new PotionEffect(MobEffects.POISON, 900));

			if (player.isCreative() || !this.getIsInvulnerable()) {
				this.attackEntityFrom(DamageSource.causePlayerDamage(player), Float.MAX_VALUE);
			}

			return true;
		} else {
			if (!this.world.isRemote && !this.isFlying() && this.isTamed() && this.isOwner(player)) {
				this.aiSit.setSitting(!this.isSitting());
			}

			return super.processInteract(player, hand);
		}
	}

	/**
	 * Checks if the parameter is an item which this animal can be fed to breed it
	 * (wheat, carrots or seeds depending on the animal type)
	 */
	public boolean isBreedingItem(ItemStack stack) {
		return false;
	}

	/**
	 * Checks if the entity's current position is a valid location to spawn this
	 * entity.
	 */
	public boolean getCanSpawnHere() {
		int i = MathHelper.floor(this.posX);
		int j = MathHelper.floor(this.getEntityBoundingBox().minY);
		int k = MathHelper.floor(this.posZ);
		BlockPos blockpos = new BlockPos(i, j, k);
		Block block = this.world.getBlockState(blockpos.down()).getBlock();
		return block instanceof BlockLeaves || block == Blocks.GRASS || block instanceof BlockLog
				|| block == Blocks.AIR && this.world.getLight(blockpos) > 8 && super.getCanSpawnHere();
	}

	public void fall(float distance, float damageMultiplier) {
	}

	protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
	}

	/**
	 * Returns true if the mob is currently able to mate with the specified mob.
	 */
	public boolean canMateWith(EntityAnimal otherAnimal) {
		return false;
	}

	@Nullable
	public EntityAgeable createChild(EntityAgeable ageable) {
		return null;
	}

	public boolean attackEntityAsMob(Entity entityIn) {
		return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 3.0F);
	}

	protected float playFlySound(float p_191954_1_) {
		this.playSound(SoundEvents.ENTITY_PARROT_FLY, 0.15F, 1.0F);
		return p_191954_1_ + this.flapSpeed / 2.0F;
	}

	protected boolean makeFlySound() {
		return true;
	}

	/**
	 * Returns true if this entity should push and be pushed by other entities when
	 * colliding.
	 */
	public boolean canBePushed() {
		return true;
	}

	protected void collideWithEntity(Entity entityIn) {
		if (!(entityIn instanceof EntityPlayer)) {
			super.collideWithEntity(entityIn);
		}
	}

	/**
	 * Called when the entity is attacked.
	 */
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isEntityInvulnerable(source)) {
			return false;
		} else {
			if (this.aiSit != null) {
				this.aiSit.setSitting(false);
			}

			return super.attackEntityFrom(source, amount);
		}
	}

	public int getVariant() {
		return MathHelper.clamp(((Integer) this.dataManager.get(VARIANT)).intValue(), 0, 4);
	}

	public void setVariant(int variantIn) {
		this.dataManager.set(VARIANT, Integer.valueOf(variantIn));
	}

	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(VARIANT, Integer.valueOf(0));
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger("Variant", this.getVariant());
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		this.setVariant(compound.getInteger("Variant"));
	}

	@Nullable
	protected ResourceLocation getLootTable() {
		return LootTableList.ENTITIES_PARROT;
	}

	public boolean isFlying() {
		return !this.onGround;
	}
}