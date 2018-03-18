package com.dizzyd.prospects;

import com.dizzyd.prospects.world.WorldData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.server.command.CommandTreeBase;

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
}
