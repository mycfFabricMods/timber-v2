package ctimber.mycf.client;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.AxeItem;
import net.minecraft.util.Identifier;
import timber.mycf.ToggleNotification;
import timber.mycf.Toggleable;


public class TimberWithClient implements ModInitializer {

    public static final Identifier TOGGLE_PLAYER = new Identifier("timber", "toggle_player");
    public static final Identifier TOGGLE_STACK = new Identifier("timber", "toggle_stack");

    @Override
    public void onInitialize() {
        ServerPlayNetworking.registerGlobalReceiver(TOGGLE_PLAYER, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                if (player instanceof ToggleNotification tplayer) {
                    tplayer.toggleAndNotify$mycftimber(player, "item.timber.axe.nevermodeon", "item.timber.axe.nevermodeoff");
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(TOGGLE_STACK, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                var itemStack = player.getMainHandStack();
                if (itemStack.getItem() instanceof AxeItem && player instanceof Toggleable tplayer && tplayer.getToggleMode$mycftimber()) {
                    ((ToggleNotification) (Object) itemStack).toggleAndNotify$mycftimber(player);
                }
            });
        });
    }
}
