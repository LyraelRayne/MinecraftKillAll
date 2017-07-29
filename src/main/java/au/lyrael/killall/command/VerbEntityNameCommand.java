package au.lyrael.killall.command;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static au.lyrael.killall.KillAll.MOD_ID;
import static au.lyrael.killall.command.CommandTargetMatchers.matchByName;

public class VerbEntityNameCommand implements ICommand {

	private static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	private final String verbName;
	private final BiConsumer<ICommandSender, EntityLiving> verb;

	public VerbEntityNameCommand(String verbName, BiConsumer<ICommandSender, EntityLiving> verb) {
		this.verbName = verbName;
		this.verb = verb;
	}

	@Override
	public String getCommandName() {
		return verbName + "all";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return getCommandName() + " <entityid>";
	}

	@Override
	public List getCommandAliases() {
		return Collections.emptyList();
	}

	@Override
	public void processCommand(ICommandSender sender, String... args) {
		World world = sender.getEntityWorld();

		if (world.isRemote) {
			// Don't bother on client side
		} else {
			if (args.length == 0) {
				sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
			} else {
				final String entityName = args[0];
				VerbAllProcessor.doVerb(sender, verb, entity -> matchByName(entityName, entity), verbName, entityName);
			}
		}
	}

	/**
	 * Returns true if the given command sender is allowed to use this command.
	 */
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	/**
	 * Adds the strings available in this command to the given list of tab completion options.
	 */
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String... args) {
		return Collections.emptyList();
	}

	/**
	 * Return whether the specified command parameter index is a username parameter.
	 */
	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return false;
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}
}
