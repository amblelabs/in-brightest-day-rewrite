package dev.amble.brightestday.platform.attachment;

import net.minecraft.resources.Identifier;

public interface AttachmentRegistrar {

    String namespace();

    default <T> Attachment<T> register(String name, AttachmentBuilder<T> builder) {
        return register(Identifier.fromNamespaceAndPath(this.namespace(), name), builder);
    }

    default <T> Attachment<T> register(Identifier id, AttachmentBuilder<T> builder) {
        builder.validate();
        return this.create(id, builder);
    }

    <T> Attachment<T> create(Identifier id, AttachmentBuilder<T> builder);

    void registerAll();
}
