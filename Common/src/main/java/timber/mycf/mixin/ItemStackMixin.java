package timber.mycf.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import timber.mycf.TimberModes;
import timber.mycf.ToggleNotification;
import timber.mycf.Toggleable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements Toggleable, ToggleNotification {

    @Shadow
    public abstract NbtCompound getOrCreateNbt();

    @Override
    public boolean getToggleMode$mycftimber() {
        return this.getOrCreateNbt().getBoolean(TimberModes.AXES.id);
    }

    @Override
    public void setTimberMode$mycftimber(boolean mode) {
        this.getOrCreateNbt().putBoolean(TimberModes.AXES.id, mode);
    }
}


