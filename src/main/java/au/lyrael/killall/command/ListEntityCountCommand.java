package au.lyrael.killall.command;

import au.lyrael.killall.utility.EntityListUtil;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static au.lyrael.killall.KillAll.MOD_ID;
import static au.lyrael.killall.command.CommandTargetMatchers.matchByName;
import static au.lyrael.killall.utility.EntityListUtil.getLivingEntities;
import static au.lyrael.killall.utility.EntityListUtil.getMatchingEntities;

public class ListEntityCountCommand implements ICommand {

	private static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	@Override
	public String getCommandName() {
		return "listall";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return getCommandName() + "[entityname]";
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
			final String matchString = args.length > 0 ? args[0] : "";

			final List<Entity> loadedEntityList = world.getLoadedEntityList();
			final List<EntityLiving> livingEntities = getLivingEntities(loadedEntityList);
			final List<EntityLiving> matchingEntities = getMatchingEntities(entity -> matchByName(matchString, entity), livingEntities);

			final Map<String, Long> counts = matchingEntities.stream().
					collect(Collectors.groupingBy(EntityListUtil::getEntityName, Collectors.counting()));

			counts.entrySet().forEach(entry -> sender.addChatMessage(
					new ChatComponentText(String.format("%s -> %s", entry.getKey(), entry.getValue()))
			));
			sender.addChatMessage(new ChatComponentText(String.format("%s -> %s", "total", matchingEntities.size())));
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
