package azathoth.util.prospecting;

import azathoth.util.prospecting.registry.Prospector;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.command.CommandTreeBase;

import java.util.HashMap;
import java.util.Map;

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
			HashMap<String, Float> oreCounts = Prospector.getOres(sender.getEntityWorld(), cx, cz);
			for (Map.Entry<String, Float> e : oreCounts.entrySet()) {
				buf.append(" " + e.getKey() + ": " + Math.round(e.getValue()));
			}
			Command.notifyCommandListener(sender, this, "prospect.info", cx, cz, buf.toString());
		}
	}
}
