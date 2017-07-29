package au.lyrael.killall.command;

import au.lyrael.killall.utility.EntityListUtil;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.StringUtils;

public class CommandTargetMatchers {
	public static boolean matchByName(String targetEntityName, EntityLiving entity) {
		final String toMatch = targetEntityName.toLowerCase();
		final String entityName = EntityListUtil.getEntityName(entity);
		final boolean isTamed = EntityTameable.class.isAssignableFrom(entity.getClass()) && ((EntityTameable) entity).isTamed();
		final boolean isNamed = !StringUtils.isNullOrEmpty(entity.getCustomNameTag());
		return !isTamed && !isNamed && entityName.contains(toMatch);
	}
}
