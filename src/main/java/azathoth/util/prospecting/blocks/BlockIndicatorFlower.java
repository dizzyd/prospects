package azathoth.util.prospecting.blocks;

import azathoth.util.prospecting.Prospecting;
import azathoth.util.prospecting.registry.Prospector;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public class BlockIndicatorFlower extends BlockBush {

	public static final PropertyEnum<EnumFlowerType> FLOWERTYPE = PropertyEnum.<EnumFlowerType>create("flowertype", EnumFlowerType.class);

	public BlockIndicatorFlower() {
		super(Material.PLANTS);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FLOWERTYPE, EnumFlowerType.AFFINE));
		this.setSoundType(SoundType.PLANT);
		this.setUnlocalizedName(Prospecting.MODID + ".flower");
		this.setRegistryName(Prospecting.MODID, "flower");
	}

	public void placeAt(World world, BlockPos pos, EnumFlowerType flowerType) {
		world.setBlockState(pos, this.getDefaultState().withProperty(FLOWERTYPE, flowerType));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {FLOWERTYPE});
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FLOWERTYPE).getMeta();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FLOWERTYPE, EnumFlowerType.byMetadata(meta));
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		for (EnumFlowerType t : EnumFlowerType.values()) {
			Item i = Item.getItemFromBlock(this);
			ModelResourceLocation mr = new ModelResourceLocation(getRegistryName(), "flowertype=" + t.getName());
			ModelLoader.setCustomModelResourceLocation(i, t.getMeta(), mr);
		}
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		for (EnumFlowerType t: EnumFlowerType.values()) {
			items.add(new ItemStack(this, 1, t.getMeta()));
		}
	}

	public void registerItems(IForgeRegistry<Item> registry) {
		Item item = new ItemMultiTexture(Prospecting.FLOWERBLOCK, Prospecting.FLOWERBLOCK,
				stack -> "flowertype=" + EnumFlowerType.byMetadata(stack.getMetadata()).getName())
				.setRegistryName(this.getRegistryName())
				.setUnlocalizedName(this.getUnlocalizedName());
		registry.register(item);
	}

	public static enum EnumFlowerType implements IStringSerializable {
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
		VALLOZIA(11, "vallozia", "Diamond");

		private static final EnumFlowerType[] META_LOOKUP = new EnumFlowerType[values().length];

		private final int meta;
		private final String name;
		private final String ore;

		private EnumFlowerType(int meta, String name, String ore) {
			this.meta = meta;
			this.name = name;
			this.ore = ore;
		}

		public int getMeta() { return this.meta; }

		public String getName() {return this.name; }

		public String getOre() { return this.ore; }

		public String toString() { return this.name; }

		public static EnumFlowerType byMetadata(int meta) {
			if (meta < 0 || meta >= META_LOOKUP.length) {
				meta = 0;
			}
			return META_LOOKUP[meta];
		}

		static {
			for (EnumFlowerType t : values()) {
				META_LOOKUP[t.meta] = t;
				Prospector.registerFlower(t.getOre(), t);
			}
		}
	}

}
