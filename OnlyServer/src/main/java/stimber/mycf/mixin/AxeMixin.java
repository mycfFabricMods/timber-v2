package stimber.mycf.mixin;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import timber.mycf.ToggleNotification;
import timber.mycf.Toggleable;

@Mixin(Item.class)
abstract public class AxeMixin {

    @Inject(at = @At("HEAD"), method = "use")
    void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        // with commands this is totally fine.
        if (!world.isClient()) {
            final var mode = ((Toggleable) user).getToggleMode$mycftimber();
            if (user.getStackInHand(hand).getItem() instanceof AxeItem && mode) {
                final var stack = (ToggleNotification) (Object) user.getStackInHand(hand);
                stack.toggleAndNotify$mycftimber(user);
            }
        }
    }
}