package dev.amble.brightestday.platform;

import dev.amble.brightestday.BrightestDayNeoForge;
import dev.amble.brightestday.platform.attachment.Attachment;
import dev.amble.brightestday.platform.attachment.AttachmentBuilder;
import dev.amble.brightestday.platform.attachment.AttachmentRegistrar;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public record NeoForgeAttachmentRegistrar(String namespace, DeferredRegister<AttachmentType<?>> register) implements AttachmentRegistrar {

    public NeoForgeAttachmentRegistrar(String namespace) {
        this(namespace, DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, namespace));
    }

    @Override
    public <T> Attachment<T> create(Identifier id, AttachmentBuilder<T> builder) {
        DeferredHolder<AttachmentType<?>, AttachmentType<T>> holder = register.register(id.getPath(), () -> {
            AttachmentType.Builder<T> neo = AttachmentType.builder(builder.initializer());
            if (builder.codec() != null) neo.serialize(builder.codec().fieldOf("value"));
            if (builder.shouldCopyOnDeath()) neo.copyOnDeath();
            if (builder.syncCodec() != null) neo.sync(builder.syncCodec());
            return neo.build();
        });
        return new NeoForgeAttachment<>(id, holder, builder.initializer());
    }

    @Override
    public void registerAll() {
        register.register(BrightestDayNeoForge.EVENT_BUS); // TODO: dont depend on brightestday
    }

    public record NeoForgeAttachment<T>(Identifier id, Supplier<AttachmentType<T>> type, Supplier<T> initializer) implements Attachment<T> {

        @Override
        public T get(Entity holder) {
            T value = holder.getExistingDataOrNull(type);
            return value != null ? value : initializer.get();
        }

        @Override
        public void set(Entity holder, T value) {
            holder.setData(type, value);
        }

        @Override
        public boolean has(Entity holder) {
            return holder.hasData(type);
        }

        @Override
        public @Nullable T remove(Entity holder) {
            return holder.removeData(type);
        }
    }
}
