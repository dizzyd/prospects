package com.dizzyd.prospects.blocks;

import com.dizzyd.prospects.Prospects;
import com.dizzyd.prospects.world.WorldGen;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public class BlockFlower extends BlockBush {

	@GameRegistry.ObjectHolder(Prospects.MODID + ":flower")
	public static BlockFlower INSTANCE;

	public static final PropertyEnum<EnumType> TYPE = PropertyEnum.<EnumType>create("type", EnumType.class);

	public BlockFlower() {
		super(Material.PLANTS);
		this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, EnumType.AFFINE));
		this.setSoundType(SoundType.PLANT);
		this.setUnlocalizedName(Prospects.MODID + ".flower");
		this.setRegistryName(Prospects.MODID, "flower");
	}

	public boolean placeFlower(World world, int x, int z, EnumType flowerType) {
		// Find the block position just above the surface
		BlockPos destPos = findSurfacePlus1(world, x, z);
		if (destPos.getY() < 1) {
			return false;
		}

		// If the surface can sustain this plant, go ahead and plant it
		IBlockState dest = world.getBlockState(destPos);
		IBlockState surface = world.getBlockState(destPos.down());
		if (!dest.getMaterial().isLiquid() && dest.getMaterial().isReplaceable() && canSustainBush(surface)) {
			return world.setBlockState(destPos, this.getDefaultState().withProperty(TYPE, flowerType), 2 | 16);
		}

		return false;
	}

	// Alternative to getTopSolidOrLiquidBlock; we need to make sure to check for both grass and wood/branches. Using
	// mods like DynamicTrees, for example, causes getTSOLB to return well above the ground and thus dramatically
	// reduces number of flowers that are able to be placed in wooded areas.
	private BlockPos findSurfacePlus1(World world, int x, int z) {
		Chunk chunk = world.getChunkFromChunkCoords(x >> 4, z >> 4);
		BlockPos pos = new BlockPos(x, chunk.getTopFilledSegment() + 16, z);

		BlockPos nextPos;
		for (; pos.getY() >= 0; pos = nextPos) {
			nextPos = pos.down();
			IBlockState bs = chunk.getBlockState(nextPos);
			Block b = bs.getBlock();
			if (bs.getMaterial().isSolid() &&
					!bs.getMaterial().isReplaceable() &&
					!b.isLeaves(bs, world, nextPos) &&
					!b.isFoliage(world, nextPos) &&
					!b.isWood(world, nextPos)) {
				break;
			}
		}

		return pos;
	}

	@Override
	protected boolean canSustainBush(IBlockState state) {
		return super.canSustainBush(state) || state.getBlock() == Blocks.SAND;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {TYPE});
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(TYPE).getMeta();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(TYPE, EnumType.byMetadata(meta));
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		for (EnumType t : EnumType.values()) {
			Item i = Item.getItemFromBlock(this);
			ModelResourceLocation mr = new ModelResourceLocation(getRegistryName(), "type=" + t.getName());
			ModelLoader.setCustomModelResourceLocation(i, t.getMeta(), mr);
		}
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		for (EnumType t: EnumType.values()) {
			items.add(new ItemStack(this, 1, t.getMeta()));
		}
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(TYPE).getMeta();
	}

	public void registerItems(IForgeRegistry<Item> registry) {
		Item item = new ItemMultiTexture(BlockFlower.INSTANCE, BlockFlower.INSTANCE,
				stack -> EnumType.byMetadata(stack.getMetadata()).getName())
				.setRegistryName(this.getRegistryName())
				.setUnlocalizedName(this.getUnlocalizedName());
		registry.register(item);
	}

	public static enum EnumType implements IStringSerializable {
		AFFINE(0, "affine", "Aluminum"),
		CAMELLIA(1, "camellia", "Coal"),
		CLOVER(2, "clover", "Zinc"),
		HAUMAN(3, "hauman", "Copper"),
		HORSETAIL(4, "horsetail", "Gold"),
		LEADPLANT(5, "leadplant", "Lead"),
		MALVA(6, "malva", "Cadmium"),
		MUSTARD(7, "mustard", "Silver"),
		POORJOE(8, "poorjoe", "Iron"),
		PRIMROSE(9, "primrose", "Uranium"),
		SHRUB_VIOLET(10, "shrub_violet", "Nickel"),
		VALLOZIA(11, "vallozia", "Diamond"),
		FLAME_LILY(12, "flame_lily", "Redstone"),
		TANSY(13, "tansy", "Tin");

		private static final EnumType[] META_LOOKUP = new EnumType[values().length];

		private final int meta;
		private final String name;
		private final String ore;

		private EnumType(int meta, String name, String ore) {
			this.meta = meta;
			this.name = name;
			this.ore = ore;
		}

		public int getMeta() { return this.meta; }

		public String getName() {return this.name; }

		public String getOre() { return this.ore; }

		public String toString() { return this.name; }

		public static EnumType byMetadata(int meta) {
			if (meta < 0 || meta >= META_LOOKUP.length) {
				meta = 0;
			}
			return META_LOOKUP[meta];
		}

		static {
			for (EnumType t : values()) {
				META_LOOKUP[t.meta] = t;
				WorldGen.registerFlower(t.getOre(), t);
			}
		}
	}

}
