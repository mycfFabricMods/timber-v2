package timber.mycf.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import timber.mycf.TimberModes;
import timber.mycf.ToggleNotification;
import timber.mycf.Toggleable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements Toggleable, ToggleNotification {
    private boolean canToggle = true;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At(value = "TAIL"))
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean(TimberModes.PLAYERS.id, this.canToggle);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At(value = "TAIL"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        this.canToggle = nbt.getBoolean(TimberModes.PLAYERS.id);
    }

    @Override
    public boolean getToggleMode$mycftimber() {
        return this.canToggle;
    }

    @Override
    public void setTimberMode$mycftimber(boolean mode) {
        this.canToggle = mode;
    }

}