package au.lyrael.killall;

import au.lyrael.killall.command.ListEntityCountCommand;
import au.lyrael.killall.command.ListEntityDetailsCommand;
import au.lyrael.killall.command.VerbEntityNameCommand;
import au.lyrael.killall.utility.MetadataHelper;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static au.lyrael.killall.KillAll.*;
import static au.lyrael.killall.utility.MathUtility.asVec3;
import static au.lyrael.killall.utility.MathUtility.scalarMultiply;

@Mod(modid = MOD_ID, name = MOD_NAME, version = VERSION, acceptedMinecraftVersions = ACCEPTED_MC_VERSIONS)
public class KillAll {

	public static final String MOD_ID = "killall";
	public static final String MOD_NAME = "Kill All Command";
	public static final String MC_VERSION = "1.7.10";
	public static final String VERSION = "1.1.1-" + MC_VERSION;
	public static final String ACCEPTED_MC_VERSIONS = "[" + MC_VERSION + "]";
	public static final String DESC = "Adds chat command to kill all of a certain kind of entity";
	public static final String URL = "";
	public static final String CREDITS = "Code by LyraelRayne.";
	public static final String LOGO_PATH = ""; // PUT LOGO PATH HERE!
	public static final String CLIENT_PROXY_CLASS = "au.lyrael.killall.ClientProxy";
	public static final String COMMON_PROXY_CLASS = "au.lyrael.killall.CommonProxy";

	@Mod.Instance(MOD_ID)
	public static KillAll INSTANCE;

	@Mod.Metadata(MOD_ID)
	public static ModMetadata MOD_METADATA;

	@SidedProxy(clientSide = CLIENT_PROXY_CLASS, serverSide = COMMON_PROXY_CLASS)
	public static CommonProxy proxy;

	private static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static Configuration configuration;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		LOGGER.log(Level.INFO, "Pre Initialization: Starting...");

		MOD_METADATA = MetadataHelper.transformMetadata(MOD_METADATA);

		proxy.preInit(event);

		LOGGER.log(Level.INFO, "Pre Initialization: Complete");
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		LOGGER.log(Level.INFO, "Initialization: Starting...");
		proxy.init();


		LOGGER.log(Level.INFO, "Initialization: Complete");
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		LOGGER.log(Level.INFO, "Post Initialization: Starting...");

		proxy.postInit(event);

		LOGGER.log(Level.INFO, "Post Initialization: Complete");
	}

	@Mod.EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		event.registerServerCommand(new VerbEntityNameCommand("kill", (sender, entity) -> entity.setDead()));
		event.registerServerCommand(new VerbEntityNameCommand("hoist", KillAll::hoistEntityToSenderFloor));
		event.registerServerCommand(new VerbEntityNameCommand("vacuum", KillAll::vacuumEntityToSender));
		event.registerServerCommand(new ListEntityCountCommand());
		event.registerServerCommand(new ListEntityDetailsCommand());
	}

	private static double getFloorY(final ICommandSender sender, final double maxY, final double x, final double z) {
		final World world = sender.getEntityWorld();
		boolean found = false;
		double currentY = maxY;
		while (!found) {
			if (world.isSideSolid((int) x, (int) currentY, (int) z, ForgeDirection.UP)) {
				found = true;
			} else {
				currentY -= 1;
			}
		}
		return currentY + 1;
	}

	private static void hoistEntityToSenderFloor(final ICommandSender sender, final EntityLiving entity) {
		final int targetX = (int) Math.floor(entity.posX);
		final int targetZ = (int) Math.floor(entity.posZ);
		final int targetY = (int) getFloorY(sender, sender.getPlayerCoordinates().posY, targetX, targetZ) + 2;
		entity.setPosition(targetX, targetY, targetZ);
	}

	private static void vacuumEntityToSender(final ICommandSender sender, final EntityLiving entity) {
		final Vec3 playerCoordinates = asVec3(sender.getPlayerCoordinates());
		final Vec3 entityPosition = Vec3.createVectorHelper(entity.posX, entity.posY, entity.posZ);

		final Vec3 directionVector = Vec3.createVectorHelper(
				entityPosition.xCoord - playerCoordinates.xCoord,
				0,
				entityPosition.zCoord - playerCoordinates.zCoord
		).normalize();

		final Vec3 offsetVector = scalarMultiply(7, directionVector);
		final Vec3 targetPos = playerCoordinates.addVector(offsetVector.xCoord, 0, offsetVector.zCoord);

		entity.setPosition(
				targetPos.xCoord,
				getFloorY(sender, playerCoordinates.yCoord, targetPos.xCoord, targetPos.zCoord) + 2,
				targetPos.zCoord
		);
	}
}
