package ctimber.mycf.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.AxeItem;
import org.lwjgl.glfw.GLFW;

public class ClientEntryPoint implements ClientModInitializer {

    private static final KeyBinding totalToggleBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            // The translation key of the keybinding's name
                "key.timber.totalToggle",
            // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            InputUtil.Type.KEYSYM,
            // The keycode of the key
            GLFW.GLFW_MOUSE_BUTTON_RIGHT,
            // The translation key of the keybinding's category.
                "category.timber"
    ));

    final private static KeyBinding basicToggleBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.timber.basicToggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_L,
            "category.timber"
    ));

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (totalToggleBinding.wasPressed()) {
                ClientPlayNetworking.send(TimberWithClient.TOGGLE_PLAYER, PacketByteBufs.empty());
            }

            if (basicToggleBinding.wasPressed()) {
                var player = client.player;
                var itemStack = player.getMainHandStack();
                if (itemStack.getItem() instanceof AxeItem) {
                    ClientPlayNetworking.send(TimberWithClient.TOGGLE_STACK, PacketByteBufs.empty());
                }
            }
        });
    }
}
