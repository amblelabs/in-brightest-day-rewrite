package dev.amblelabs.brightestday.platform;

import dev.amblelabs.brightestday.platform.attachment.Attachment;
import dev.amblelabs.brightestday.platform.attachment.AttachmentBuilder;
import dev.amblelabs.brightestday.platform.attachment.AttachmentRegistrar;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public record FabricAttachmentRegistrar(String namespace) implements AttachmentRegistrar {

    @Override
    public <T> Attachment<T> create(Identifier id, AttachmentBuilder<T> builder) {
        AttachmentRegistry.Builder<T> fabric = AttachmentRegistry.<T>builder().initializer(builder.initializer());
        if (builder.codec() != null) fabric.persistent(builder.codec());
        if (builder.shouldCopyOnDeath()) fabric.copyOnDeath();
        if (builder.syncCodec() != null) fabric.syncWith(builder.syncCodec(), AttachmentSyncPredicate.all());
        return new FabricAttachment<>(fabric.buildAndRegister(id), builder.initializer());
    }

    @Override
    public void registerAll() { }

    public record FabricAttachment<T>(AttachmentType<T> type, Supplier<T> initializer) implements Attachment<T> {

        @Override
        public Identifier id() {
            return type.identifier();
        }

        @Override
        public T get(Entity holder) {
            return holder.getAttachedOrGet(type, initializer);
        }

        @Override
        public void set(Entity holder, T value) {
            holder.setAttached(type, value);
        }

        @Override
        public boolean has(Entity holder) {
            return holder.hasAttached(type);
        }

        @Override
        public @Nullable T remove(Entity holder) {
            return holder.removeAttached(type);
        }
    }
}
