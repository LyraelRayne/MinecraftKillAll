package au.lyrael.killall.command;

import au.lyrael.killall.utility.EntityListUtil;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import static au.lyrael.killall.KillAll.MOD_ID;

public class VerbAllProcessor {

	private static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static void doVerb(
			final ICommandSender sender,
			final BiConsumer<ICommandSender, EntityLiving> verb,
			final Predicate<EntityLiving> matcher,
			final String verbNameForLogging,
			final String queryForLogging
	) {
		World world = sender.getEntityWorld();
		final List<Entity> loadedEntityList = world.getLoadedEntityList();

		final List<EntityLiving> livingEntities = EntityListUtil.getLivingEntities(loadedEntityList);
		final List<EntityLiving> matchingEntities = EntityListUtil.getMatchingEntities(matcher, livingEntities);

		if (matchingEntities.size() > 0) {
			final String senderName = sender.getCommandSenderName();
			LOGGER.trace("[{}] will {} [{}]", senderName, verbNameForLogging, matchingEntities);
			LOGGER.info("[{}] will {} [{}] entities matching [{}] out of [{}] total",
					senderName, verbNameForLogging, matchingEntities.size(), queryForLogging, livingEntities.size());
			matchingEntities.forEach(entity -> verb.accept(sender, entity));
			sender.addChatMessage(
					new ChatComponentText(
							String.format("[%s] %sed [%d] entities matching [%s] out of [%d] total",
									senderName, verbNameForLogging, matchingEntities.size(), queryForLogging, livingEntities.size()
							)
					)
			);
			LOGGER.debug("Done killing");
		} else {
			sender.addChatMessage(new ChatComponentText("Found no matching entities for " + queryForLogging));
			LOGGER.trace("None of [{}] matched query [{}]", livingEntities, queryForLogging);
		}
	}

	protected static void killEntity(EntityLiving entity) {
		entity.setDead();
	}



}
