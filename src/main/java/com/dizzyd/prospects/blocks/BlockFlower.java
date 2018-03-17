package com.dizzyd.prospects.blocks;

import com.dizzyd.prospects.Prospects;
import com.dizzyd.prospects.registry.Prospector;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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

	public void placeAt(World world, BlockPos pos, EnumType flowerType) {
		world.setBlockState(pos, this.getDefaultState().withProperty(TYPE, flowerType));
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

	public void registerItems(IForgeRegistry<Item> registry) {
		Item item = new ItemMultiTexture(BlockFlower.INSTANCE, BlockFlower.INSTANCE,
				stack -> EnumType.byMetadata(stack.getMetadata()).getName())
				.setRegistryName(this.getRegistryName())
				.setUnlocalizedName(this.getUnlocalizedName());
		registry.register(item);
	}

	public static enum EnumType implements IStringSerializable {
		AFFINE(0, "affine", "Aluminum"),
		CAMELLIA(1, "camellia", "Florite"),
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
				Prospector.registerFlower(t.getOre(), t);
			}
		}
	}

}
