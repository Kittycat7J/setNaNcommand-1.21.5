package kittycat7j.nancmd;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;

import java.util.Collection;
import java.util.function.Supplier;

public class NaNCommands {
	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(
					CommandManager.literal("setNaNVelocity")
							.requires(source -> source.hasPermissionLevel(2)) // Requires permission level 2
							.then(
									CommandManager.argument("targets", EntityArgumentType.entities())
											.then(
													CommandManager.argument("xvelocity", BoolArgumentType.bool())
															.then(
																	CommandManager.argument("yvelocity", BoolArgumentType.bool())
																			.then(
																					CommandManager.argument("zvelocity", BoolArgumentType.bool())
																							.executes(NaNCommands::execute)
																			)
															)
											)
							)
			);
		});
	}

	private static int execute(CommandContext<ServerCommandSource> context) {
		try {
			Collection<? extends Entity> entities = EntityArgumentType.getEntities(context, "targets");
			boolean xVelocity = BoolArgumentType.getBool(context, "xvelocity");
			boolean yVelocity = BoolArgumentType.getBool(context, "yvelocity");
			boolean zVelocity = BoolArgumentType.getBool(context, "zvelocity");

			for (Entity entity : entities) {
				setVelocityToNaN(entity, xVelocity, yVelocity, zVelocity);

				Vec3d velocity = entity.getVelocity();
				String motion = String.format("Motion: [%.1fd, %.1fd, %.1fd]", velocity.x, velocity.y, velocity.z);

				context.getSource().sendFeedback(
						() -> Text.of(entity.getName().getString() + " has the following motion data: " + motion),
						true
				);
			}

			context.getSource().sendFeedback(
					() -> Text.of("Set velocity to NaN for " + entities.size() + " entities."),
					true
			);

			return entities.size();
		} catch (Exception e) {
			context.getSource().sendError(
					Text.of("An error occurred while executing the command: " + e.getMessage())
			);
			e.printStackTrace();
			return 0;
		}
	}




	private static void setVelocityToNaN(Entity entity, boolean xVelocity, boolean yVelocity, boolean zVelocity) {
		Vec3d velocity = entity.getVelocity();
		double x = xVelocity ? Double.NaN : velocity.x;
		double y = yVelocity ? Double.NaN : velocity.y;
		double z = zVelocity ? Double.NaN : velocity.z;

		entity.setVelocity(x, y, z);

		if (entity instanceof ServerPlayerEntity) {
			((ServerPlayerEntity) entity).networkHandler.requestTeleport(entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch());
		}
	}
}