package azathoth.util.prospecting.blocks;

import azathoth.util.prospecting.Prospecting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockBush;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.Random;

public class BlockIndicatorFlowerDouble extends BlockBush {
	public IIcon[] icons = new IIcon[2];
	
	public BlockIndicatorFlowerDouble() {
		super(Material.plants);
		this.setStepSound(soundTypeGrass);
	}

	@Override
	public void registerBlockIcons(IIconRegister r) {
		this.icons[0] = r.registerIcon(this.textureName + "_0");
		this.icons[1] = r.registerIcon(this.textureName + "_1");
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return meta == 1 ? this.icons[1] : this.icons[0];
	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random) {
		return meta == 0 ? 1 : 0;
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		if (isTop(world, x, y, z)) {
			return world.getBlock(x, y-1, z) == this && world.getBlockMetadata(x, y-1, z) == 0;
		} else {
			return super.canBlockStay(world, x, y, z);
		}
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z,  int facing, float offset_x, float offset_y, float offset_z, int meta) {
		if (!isTop(world, x, y, z) && y < 255) {
			world.setBlock(x, y+1, z, this, 1, 1);
			return 0;
		}
		return 1;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
		if (isTop(world, x, y, z)) {
			super.onNeighborBlockChange(world, x, y, z, b);
		} else if (world.getBlock(x, y+1, z) != this || world.getBlockMetadata(x, y+1, z) != 1) {
			world.setBlockToAir(x, y, z);
		}
	}

	private boolean isTop(World world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z) == 1;
	}
}
