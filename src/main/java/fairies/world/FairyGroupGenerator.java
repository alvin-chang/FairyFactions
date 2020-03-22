package fairies.world;

import fairies.Version;
import fairies.entity.EntityFairy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FairyGroupGenerator {
	private final int maxSize;
	private final int minSize;
	private final int faction;

	private static final int RADIUS = 8;
	private static final int HALFRAD = RADIUS / 2;
	public static final Logger LOGGER = LogManager.getFormatterLogger(Version.MOD_ID);

	public FairyGroupGenerator(int min, int max, final int faction) {
		if (max < min) {
			final int p = max;
			max = min;
			min = p;
		}

		this.minSize = min;
		this.maxSize = max;
		this.faction = faction;
	}

	public boolean generate(final World world, final Random rand, final int i, final int j, final int k) {
		final List<int[]> list = new ArrayList<int[]>();
		final IBlockState cordial = world.getBlockState(new BlockPos(i, j, k));

		for (int q = 0; q < 128 && list.size() < maxSize; q++) {
			final int y = j + rand.nextInt(HALFRAD) - rand.nextInt(HALFRAD);
			if (y < 0 || y > 126)
				continue;

			final int x = i + rand.nextInt(RADIUS) - rand.nextInt(RADIUS);
			final int z = k + rand.nextInt(RADIUS) - rand.nextInt(RADIUS);
			final BlockPos pos = new BlockPos(x, y, z);
			final IBlockState blockState = world.getBlockState(pos);
			final Block block = blockState.getBlock();
			LOGGER.debug("block is:");
			if (blockState == cordial) {
				LOGGER.debug("cordial");
			}
			if (isAirySpace(blockState, block)) {
				LOGGER.debug("airy");
			}
			if (isAirySpace(blockState, block)) {
				list.add(new int[] { x, y + 1, z });
			}
		}

		if (list.size() < minSize) {
			LOGGER.debug("list too small" + list.size());
			return false;
		}

		final int disparity = (list.size() - minSize) + 1;
		final int actualSize = minSize + rand.nextInt(disparity);
		int guards = (minSize / 4) + (rand.nextInt(maxSize - minSize + 1) < disparity ? 1 : 0);
		int scouts = (minSize / 5) + (rand.nextInt(maxSize - minSize + 1) < disparity ? 1 : 0);
		int medics = (minSize / 5) + (rand.nextInt(maxSize - minSize + 1) < disparity ? 1 : 0);
		int specialFairy = 1; // Random

		for (int q = 0; q < actualSize; q++) {
			final int coords[] = (int[]) list.get(q);
			final int x = coords[0];
			final int y = coords[1];
			final int z = coords[2];
			final double a = x + 0.45D + (rand.nextFloat() * 0.1D);
			final double b = y + 0.5D;
			final double c = z + 0.45D + (rand.nextFloat() * 0.1D);
			final EntityFairy fairy = new EntityFairy(world);
			fairy.setPosition(a, b, c);
			fairy.setFaction(faction);

			if (guards > 0) {
				guards--;
				fairy.setJob(1);
				fairy.setCower(false);
			} else if (scouts > 0) {
				scouts--;
				fairy.setJob(2);
			} else if (medics > 0) {
				medics--;
				fairy.setJob(3);
				fairy.setCanHeal(true);
				fairy.setRarePotion(rand.nextInt(4) == 0);
			} else if (specialFairy == 1) {
				specialFairy = 0;
				fairy.setJob(1);
				fairy.setCanHeal(true);
				fairy.setSpecialJob(true);
				fairy.setCower(false);
			} else {
				fairy.setJob(0);
			}

			world.spawnEntity(fairy);
		}

		return true;
	}

	public boolean isAirySpace(IBlockState blockState, Block block) {
		if (block == Blocks.AIR) {
			return true;
		} else {
			final Material matt = blockState.getMaterial();

			if (matt == null || matt == Material.AIR || matt == Material.PLANTS || matt == Material.VINE
					|| matt == Material.FIRE || matt == Material.CIRCUITS || matt == Material.SNOW) {
				return true;
			} // Material.field_35574_k is vines
		}

		return false;
	}
}
