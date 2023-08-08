package timber.mycf.mixin;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import timber.mycf.Timber;
import timber.mycf.Toggleable;


@Mixin(AxeItem.class)
abstract public class AxeMixin extends MiningToolItem {
    protected AxeMixin(float attackDamage, float attackSpeed, ToolMaterial material, TagKey<Block> effectiveBlocks, Settings settings) {
        super(attackDamage, attackSpeed, material, effectiveBlocks, settings);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {

        final boolean mode = ((Toggleable)(Object) stack).getToggleMode$mycftimber();
        final boolean isEnabled = world.getGameRules().getBoolean(Timber.ENABLE_TIMBER);

        // the damage done to the axe
        int damage = 1;

        if (!world.isClient() && mode && isEnabled && state.isIn(BlockTags.LOGS)) {
            // the damage the axe can take until it breaks
            final int trueDamage = Math.abs(stack.getMaxDamage() - stack.getDamage());

            BlockPos.Mutable mutableUp = pos.mutableCopy();
            BlockPos.Mutable mutableDown = pos.mutableCopy();

            while (world.getBlockState(mutableUp.move(Direction.UP)).isIn(BlockTags.LOGS) && damage < trueDamage) {
                world.breakBlock(mutableUp, true);
                damage++;
            }
            while (world.getBlockState(mutableDown.move(Direction.DOWN)).isIn(BlockTags.LOGS) && damage < trueDamage) {
                world.breakBlock(mutableDown, true);
                damage++;
            }

            for (BlockPos blockPos : BlockPos.iterateOutwards(mutableUp.offset(Direction.UP), 8, 8, 8)) {
                final var blockState = world.getBlockState(blockPos);
                if (blockState.isIn(BlockTags.LEAVES)) {
                    ((LeavesBlock) blockState.getBlock()).scheduledTick(blockState, (ServerWorld) world, blockPos, world.getRandom());
                }
             }
        }

        if (state.getHardness(world, pos) != 0.0F)
            stack.damage(damage, miner, (PlayerEntity) -> PlayerEntity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));

        return true;
    }
}