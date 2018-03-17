package com.dizzyd.prospects;

import com.dizzyd.prospects.registry.ProspectingSavedData;
import com.dizzyd.prospects.registry.Prospector;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.command.CommandTreeBase;

public class Command extends CommandTreeBase {

	@Override
	public String getName() {
		return "prospect";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "prospect.usage";
	}

	public Command() {
		addSubcommand(new CommandInfo());
	}

	public static class CommandInfo extends CommandBase {

		@Override
		public String getName() {
			return "info";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return "prospect.info.usage";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			int cx = sender.getPosition().getX() >> 4;
			int cz = sender.getPosition().getZ() >> 4;
			StringBuilder buf = new StringBuilder();
			ProspectingSavedData.ChunkInfo info = Prospector.getChunkInfo(sender.getEntityWorld(), cx, cz);
			for (String ore : info.ores.keySet()) {
				float oreCount = info.ores.get(ore);
				int nuggetCount = info.nuggets.getOrDefault(ore, 0);
				buf.append(" " + ore + ": " + Math.round(oreCount) + '(' + nuggetCount + ')');
			}
			Command.notifyCommandListener(sender, this, "prospect.info", cx, cz, buf.toString());
		}
	}
}
