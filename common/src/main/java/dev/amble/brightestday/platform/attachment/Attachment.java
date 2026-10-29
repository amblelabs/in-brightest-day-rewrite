package dev.amble.brightestday.platform.attachment;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;

public interface Attachment<T> {

    Identifier id();

    T get(Entity holder);

    void set(Entity holder, T value);

    boolean has(Entity holder);

    @Nullable T remove(Entity holder);
}
