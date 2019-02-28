package com.dizzyd.prospects;

import com.dizzyd.prospects.world.WorldData;
import com.dizzyd.prospects.world.WorldGen;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.server.command.CommandTreeBase;

import java.util.DoubleSummaryStatistics;
import java.util.HashSet;
import java.util.Set;

public class Command extends CommandTreeBase {

	@Override
	public String getName() {
		return "prospects";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "cmd.prospects.usage";
	}

	public Command() {
		addSubcommand(new CommandOreInfo());
		addSubcommand(new CommandShowConfig());
		addSubcommand(new CommandReloadConfig());
		addSubcommand(new CommandScanTime());
		addSubcommand(new CommandClearBlocks());
		addSubcommand(new CommandPlacementStats());
	}

	public static class CommandOreInfo extends CommandBase {
		@Override
		public String getName() {
			return "oreinfo";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return "cmd.prospects.oreinfo.usage";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			int cx = sender.getPosition().getX() >> 4;
			int cz = sender.getPosition().getZ() >> 4;
			StringBuilder buf = new StringBuilder();
			WorldData.ChunkInfo info = WorldData.getChunkInfo(sender.getEntityWorld(), cx, cz);
			for (String ore : info.ores.keySet()) {
				float oreCount = info.ores.get(ore);
				int nuggetCount = info.nuggets.getOrDefault(ore, 0);
				buf.append(" " + ore + ": " + Math.round(oreCount) + '(' + nuggetCount + ')');
			}
			Command.notifyCommandListener(sender, this, "cmd.prospects.oreinfo", cx, cz, buf.toString());
		}
	}

	public static class CommandShowConfig extends CommandBase {
		@Override
		public String getName() {
			return "showconfig";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return "cmd.prospects.showconfig.usage";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			StringBuilder buf = new StringBuilder();
			Configuration cfg = Prospects.config.getConfigRoot();
			for (String categoryName : cfg.getCategoryNames()) {
				ConfigCategory cat = cfg.getCategory(categoryName);
				for (Property prop : cat.getOrderedValues()) {
					buf.append("\n -" + categoryName + "." + prop.getName() + ": " + prop.getString());
				}
			}

			Command.notifyCommandListener(sender, this, "cmd.prospects.showconfig", buf.toString());
		}
	}

	public static class CommandReloadConfig extends CommandBase {
		@Override
		public String getName() {
			return "reloadconfig";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return "cmd.prospects.reloadconfig.usage";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			Prospects.config.reload();
			Command.notifyCommandListener(sender, this, "cmd.prospects.reloadconfig.ok");
		}
	}

	public static class CommandScanTime extends CommandBase {
		@Override
		public String getName() {
			return "scantime";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return "cmd.prospects.scantime.usage";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			Prospects.config.reload();
			Command.notifyCommandListener(sender, this, "cmd.prospects.scantime.ok", WorldData.getAvgChunkScanTime());
		}
	}


	public static class CommandClearBlocks extends CommandBase {
		@Override
		public String getName() {
			return "clear";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return "cmd.prospects.clear.usage";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			int radius = CommandBase.parseInt(args[0]);
			Block blockType = CommandBase.getBlockByText(sender, args[1]);

			BlockPos playerPos = sender.getPosition();

			World world = sender.getEntityWorld();
			if (world.isRemote) {
				return;
			}

			int count = 0;
			Set<Chunk> chunks = new HashSet<Chunk>();

			// Identify all block positions in the provided radius
			Iterable<BlockPos> blockPositions = BlockPos.getAllInBox(playerPos.getX() - radius, playerPos.getY() - radius, playerPos.getZ() - radius,
					playerPos.getX() + radius, playerPos.getY() + radius, playerPos.getZ() + radius);

			// For each block in the radius, get the associated chunk, change the block state
			// directly in chunk and save chunk for further processing. This side steps any
			// neighbor notifications and allow us to avoid generating a large cascade of block updates
			for (BlockPos p : blockPositions) {
				IBlockState b = sender.getEntityWorld().getBlockState(p);
				if (blockType.equals(b.getBlock())) {
					Chunk chunk = world.getChunkFromBlockCoords(p);
					chunk.setBlockState(p, Blocks.AIR.getDefaultState());
					chunks.add(chunk);
					count++;
				}
			}

			// Walk over the set of chunks and generate a chunk refresh manually; this batches all
			// the updates and ensures no block update notification
			PlayerChunkMap manager = ((WorldServer) world).getPlayerChunkMap();
			for (Chunk c : chunks) {
				PlayerChunkMapEntry watcher = manager.getEntry(c.x, c.z);
				if (watcher != null) {
					watcher.sendPacket(new SPacketChunkData(c, -1));
				}
			}

			Command.notifyCommandListener(sender, this, "cmd.prospects.clear.ok", count);
		}
	}

	public static class CommandPlacementStats extends CommandBase {

		@Override
		public String getName() {
			return "placement.stats";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return "cmd.prospects.placement.stats.usage";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			DoubleSummaryStatistics stats = WorldGen.getStats();
			String avg = String.format("%1.0f", stats.getAverage() * 100);
			String expected = String.format("%1.0f", Prospects.config.flower_chance * 100);
			Command.notifyCommandListener(sender, this, "cmd.prospects.placement.stats",
					avg, stats.getCount(), expected);
		}
	}
}
