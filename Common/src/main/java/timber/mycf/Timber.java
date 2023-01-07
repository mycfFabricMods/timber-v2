package timber.mycf;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;

import static net.minecraft.server.command.CommandManager.literal;


public class Timber implements ModInitializer {

    public static final String timber$nevermodeOn  = "item.timber.axe.nevermodeon";
    public static final String timber$nevermodeOff = "item.timber.axe.nevermodeoff";

    // TODO: this smells, change the whole interface to something else
    public static void toggleAndNotifyPlayer$mycftimber(PlayerEntity player) {
        ((ToggleNotification) player).toggleAndNotify$mycftimber(player, timber$nevermodeOn, timber$nevermodeOff);
    }

    private static void toggles(CommandDispatcher<ServerCommandSource> dispatcher) {

        dispatcher.register(literal("timber") // all timber commands
                .then(literal("lock_or_unlock_all_players") // unlocks or locks all players again
                        .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
                        .then(CommandManager.argument("enable", BoolArgumentType.bool())
                                .executes(context -> {
                                    context.getSource().getServer().getPlayerManager().getPlayerList()
                                            .forEach(serverPlayerEntity ->
                                                    ((Toggleable)serverPlayerEntity).setTimberMode$mycftimber(
                                                            BoolArgumentType.getBool(context, "enable")
                                                    ));
                                    return 1;
                                })))
                // Toggle subcommand -- available for everyone. Just toggles the modes
                .then(literal("toggle")
                        // only works if the timber mod is enabled
                        .requires(serverCommandSource -> serverCommandSource.getWorld().getGameRules().getBoolean(ENABLE_TIMBER))
                        .then(literal("player") // locks player in one mode for each axe
                                .executes(context -> {
                                    final var player = context.getSource().getPlayer();
                                    Timber.toggleAndNotifyPlayer$mycftimber(player);
                                    return 1;
                                }))
                        .then(literal("axe") // set's axes mode
                                .executes(context -> {
                                    final var player = context.getSource().getPlayerOrThrow();
                                    final var stack = player.getMainHandStack();
                                    // only do change the mode if the item is an axe of some sort
                                    if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof AxeItem)) throw new SimpleCommandExceptionType(
                                                                Text.of("You must hold the axe in your mainhand")
                                                            ).create();
                                    ((ToggleNotification) (Object) stack).toggleAndNotify$mycftimber(player);
                                    return 1;
                                }))));
    }

    public static final GameRules.Key<GameRules.BooleanRule> ENABLE_TIMBER =
            GameRuleRegistry.register("enableTimber", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(true));

    @Override
    public void onInitialize() {

        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            toggles(dispatcher);
        }));

    }
}
