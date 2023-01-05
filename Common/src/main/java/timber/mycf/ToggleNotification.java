package timber.mycf;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public interface ToggleNotification extends Toggleable {

    /**
     * toggles and sends a translatable Text to the provided player.
     * @param yes the translatable text that gets send if the new toggle value is true
     * @param no the translatable text that gets send if the new goggle value is false
     */
    default void toggleAndNotify$mycftimber(PlayerEntity player, String yes, String no) {
        final var newToggle = this.toggleMode$mycftimber();
        if (newToggle)
            player.sendMessage(Text.translatable(yes), true);
        else
            player.sendMessage(Text.translatable(no), true);
    }

    /**
     * Default implementation for the stack toggle
     */
    default void toggleAndNotify$mycftimber(PlayerEntity player) {
        final var newToggle = this.toggleMode$mycftimber();
        if (newToggle)
            player.sendMessage(Text.translatable("item.timber.axe.chopall"), true);
        else
            player.sendMessage(Text.translatable("item.timber.axe.chopone"), true);
    }
}
