package au.lyrael.killall.utility;

import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static au.lyrael.killall.KillAll.MOD_ID;

public class EntityListUtil {

	private static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static List<EntityLiving> getMatchingEntities(Predicate<EntityLiving> matcher, List<EntityLiving> livingEntities) {
		return livingEntities.stream().
				filter(matcher).collect(Collectors.toList());
	}

	public static List<EntityLiving> getLivingEntities(List<Entity> loadedEntityList) {
		return loadedEntityList.stream().
				filter(EntityListUtil::isLivingEntity).
				map(EntityListUtil::toEntityLiving).collect(Collectors.toList());
	}

	protected static EntityLiving toEntityLiving(Entity entity) {
		return (EntityLiving) entity;
	}

	protected static boolean isLivingEntity(Entity entity) {
		return entity instanceof EntityLiving;
	}

	public static String getEntityName(EntityLiving entity) {
		String entityName;
		final EntityRegistry.EntityRegistration entityRegistration = EntityRegistry.instance().lookupModSpawn(entity.getClass(), true);

		if (entityRegistration != null) {
			entityName = entityRegistration.getEntityName().toLowerCase().replaceAll("entity", "");
		} else {
			entityName = entity.getClass().getSimpleName().toLowerCase().replaceAll("entity", "");
		}
		LOGGER.trace("Got entity name [{}]", entityName);
		return entityName;
	}
}
