package dev.amblelabs.brightestday.core;

import dev.amblelabs.brightestday.core.blockentities.LanternBlockEntity;
import dev.amblelabs.brightestday.platform.Services;
import dev.amblelabs.brightestday.platform.services.PlatformHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Arrays;
import java.util.stream.Collectors;

public class BrightestDayBlockEntities {
    private static final PlatformHelper.Registrar<BlockEntityType<?>> REGISTRAR = Services.PLATFORM.createRegistrar(Registries.BLOCK_ENTITY_TYPE);

    public static final Holder<BlockEntityType<LanternBlockEntity>> LANTERN = register("lantern", LanternBlockEntity::new,
            BrightestDayBlocks.GREEN_LANTERN_BLOCK,
            BrightestDayBlocks.YELLOW_LANTERN_BLOCK,
            BrightestDayBlocks.ORANGE_LANTERN_BLOCK,
            BrightestDayBlocks.RED_LANTERN_BLOCK,
            BrightestDayBlocks.STAR_SAPPHIRE_LANTERN_BLOCK,
            BrightestDayBlocks.INDIGO_LANTERN_BLOCK,
            BrightestDayBlocks.BLUE_LANTERN_BLOCK
    );

    @SafeVarargs
    private static <T extends BlockEntity> Holder<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> factory, Holder<Block>... blocks) {
        return REGISTRAR.registerHolder(name, () -> new BlockEntityType<>(factory,
                Arrays.stream(blocks).map(Holder::value).collect(Collectors.toUnmodifiableSet())));
    }

    public static void register() {
        REGISTRAR.registerAll();
    }
}
