package dev.amblelabs.brightestday.core.blockentities;

import dev.amblelabs.brightestday.core.BrightestDayBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LanternBlockEntity extends BlockEntity {
    public LanternBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BrightestDayBlockEntities.LANTERN.value(), blockPos, blockState);
    }
}
