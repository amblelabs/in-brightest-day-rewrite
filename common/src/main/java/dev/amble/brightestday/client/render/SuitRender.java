package dev.amble.brightestday.client.render;

import com.mojang.blaze3d.platform.NativeImage;
import dev.amble.brightestday.BrightestDay;
import dev.amble.brightestday.client.render.util.SuitRenderCallback;
import dev.amble.brightestday.core.BrightestDayAttachments;
import dev.amble.brightestday.core.BrightestDayItems;
import dev.amble.brightestday.core.BrightestDayRegistries;
import dev.amble.brightestday.core.items.PowerRingItem;
import dev.amble.brightestday.core.lanterns.LanternCorps;
import dev.amble.brightestday.core.suits.Suit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.ClientAsset;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class SuitRender implements SuitRenderCallback {

    // These constants are unnecessary
    private static final int SIZE = 64;
    private static final int[] STRETCH = {0, 1, 1, 2};
    private static final int HEAD_HEIGHT = 16;
    private static final int HEAD_SIDE_TOP = 8;
    private static final int FACE_TOP = 8;
    private static final int HAT_LEFT = 40;
    private static final int HAT_FACE_ALPHA = 128;
    private static final float HAT_FACE_COVERAGE = 0.6F;

    private static @Nullable ClientLevel level;

    // So many maps, shit might be a pirates chest
    private static final Map<String, Optional<int[]>> SUITS = new HashMap<>();
    private static final Map<Identifier, Optional<Skin>> SKINS = new HashMap<>();
    private static final Map<Integer, Entry> ENTRIES = new HashMap<>();
    private static final Deque<Integer> FREE_SLOTS = new ArrayDeque<>();

    private static int nextSlot;

    // partialTicks is here for when we eventually readd the glow stuff
    public static void extract(Avatar entity, AvatarRenderState state, float partialTicks) {

        if (!(entity instanceof Player player)) return;

        Optional<LanternCorps> corps = PowerRingItem.getWornCorps(player);

        if (corps.isEmpty()) return;

        Entry entry = update(entity.getId(), state.skin, state.mainArm, corps.get());

        if (entry == null) return;

        PlayerSkin skin = state.skin;
        state.skin = new PlayerSkin(new ClientAsset.ResourceTexture(entry.bodyId, entry.bodyId), skin.cape(), skin.elytra(), skin.model(), skin.secure());
    }

    public void tick(Minecraft client) {
        if (client.level != level) {
            level = client.level;
            clear();
        }

        /*if (level == null) return;

        for (AbstractClientPlayer player : level.players()) {
            Optional<LanternCorps> corps = Optional.of(LanternCorps.GREEN);
            // tick stuff for tracking the drawing of eyes and whatnot
        }*/
    }

    static void release(int id) {
        Entry entry = ENTRIES.remove(id);
        if (entry != null) entry.close();
        SKINS.keySet().removeIf(path -> ENTRIES.values().stream().noneMatch(remaining -> remaining.key != null && remaining.key.skin().equals(path)));
    }

    static void clear() {
        ENTRIES.values().forEach(Entry::close);
        ENTRIES.clear();
        FREE_SLOTS.clear();
        nextSlot = 0;
        SUITS.clear();
        SKINS.clear();
    }

    static @Nullable Entry update(int id, PlayerSkin playerSkin, HumanoidArm arm, LanternCorps corps) {
        Identifier skinPath = playerSkin.body().texturePath();
        Skin skin = skin(skinPath);
        if (skin == null) return null;

        boolean slim = playerSkin.model() == PlayerModelType.SLIM;
        int[] suit = suit(corps, arm, slim);
        if (suit == null) return null;

        Key key = new Key(skinPath, corps, slim);
        Entry entry = ENTRIES.get(id);
        if (entry != null && entry.size != skin.size) {
            entry.close();
            entry = null;
        }

        if (entry == null) {
            entry = new Entry(claimSlot(), skin.size);
            ENTRIES.put(id, entry);
        }

        if (!key.equals(entry.key)) {
            entry.key = key;
            build(entry, skin, suit);
        }
        return entry;
    }

    private static int claimSlot() {
        Integer slot = FREE_SLOTS.pollFirst();
        return slot != null ? slot : nextSlot++;
    }

    public record Skin(int[] pixels, int size, boolean hatFace) {}

    private static @Nullable Skin skin(Identifier path) {
        return SKINS.computeIfAbsent(path, unused -> {
            AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(path);
            int[] pixels;
            int width;
            int height;
            if (texture instanceof DynamicTexture dynamic) {
                NativeImage image = dynamic.getPixels();
                width = image.getWidth();
                height = image.getHeight();
                pixels = image.getPixels();
            } else {
                Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(path);
                if (resource.isEmpty()) return Optional.empty();
                try (InputStream stream = resource.get().open(); NativeImage image = NativeImage.read(stream)) {
                    width = image.getWidth();
                    height = image.getHeight();
                    pixels = image.getPixels();
                } catch (IOException e) {
                    BrightestDay.LOGGER.warn("Failed to read skin {} for lantern suit", path, e);
                    return Optional.empty();
                }
            }
            if (width != height || width < SIZE || width % SIZE != 0) return Optional.empty();
            return Optional.of(new Skin(pixels, width, hatFace(pixels, width)));
        }).orElse(null);
    }

    private static boolean hatFace(int[] pixels, int size) {
        int scale = size / SIZE;
        int opaque = 0;
        int total = 0;
        for (int y = FACE_TOP * scale; y < (FACE_TOP + 8) * scale; y++) {
            for (int x = HAT_LEFT * scale; x < (HAT_LEFT + 8) * scale; x++) {
                total++;
                if (ARGB.alpha(pixels[y * size + x]) > HAT_FACE_ALPHA) opaque++;
            }
        }
        return opaque >= total * HAT_FACE_COVERAGE;
    }

    public static void build(Entry entry, Skin skin, int @Nullable [] suit) {
        NativeImage body = entry.body.getPixels();
        int scale = skin.size / SIZE;
        boolean outer = skin.hatFace();

        for (int y = 0; y < skin.size; y++) {
            for (int x = 0; x < skin.size; x++) {
                int sx = x / scale;
                int sy = y / scale;
                int own = sy * SIZE + sx;
                int inner = innerIndex(sx, sy);
                int base = skin.pixels[y * skin.size + x];
                boolean head = sy < HEAD_HEIGHT;

                int out = base;
                if (suit != null) {
                    int paint = suit[own];
                    if (outer && head) paint = inner < 0 ? 0 : ARGB.alpha(paint) > 0 ? paint : suit[inner];
                    if (ARGB.alpha(paint) > 0) {
                        out = mix(base, paint, 1.0F);
                    } else if (inner >= 0 && !(outer && head) && ARGB.alpha(suit[inner]) > 0) {
                        out = 0;
                    }
                }

                body.setPixel(x, y, out);
            }
        }

        entry.body.upload();
    }

    private static int mix(int from, int to, float amount) {
        if (ARGB.alpha(from) == 0) return ARGB.multiplyAlpha(to, amount);
        return ARGB.srgbLerp(amount, from, to);
    }

    private static int innerIndex(int x, int y) {
        if (y < 16 && x >= 32) return y * SIZE + x - 32;
        if (y >= 32 && y < 48 && x < 56) return (y - 16) * SIZE + x;
        if (y >= 48 && x < 16) return y * SIZE + x + 16;
        if (y >= 48 && x >= 48) return y * SIZE + x - 16;
        return -1;
    }

    private static int @Nullable [] suit(LanternCorps corps, HumanoidArm ringArm, boolean slim) {
        Suit suit = selectedSuit();
        if (suit == null) return null;
        String key = suit.id() + "/" + corps.getSerializedName() + "/" + ringArm.ordinal() + "/" + slim;
        return SUITS.computeIfAbsent(key, unused -> {
            Identifier texture = findTexture(suit, corps, ringArm);
            int[] pixels = texture == null ? null : read(texture);
            if (pixels == null) return Optional.empty();
            if (!slim) pixels = widen(pixels);
            return Optional.of(shiftHead(pixels));
        }).orElse(null);
    }

    private static int[] shiftHead(int[] pixels) {
        int[] shifted = pixels.clone();
        for (int y = HEAD_SIDE_TOP; y < HEAD_HEIGHT; y++) {
            System.arraycopy(pixels, y * 64, shifted, y * 64, SIZE);
        }
        return shifted;
    }

    private static int[] widen(int[] slim) {
        int[] wide = slim.clone();
        widenArm(slim, wide, 40, 16);
        widenArm(slim, wide, 32, 48);
        widenArm(slim, wide, 40, 32);
        widenArm(slim, wide, 48, 48);
        return wide;
    }

    private static void widenArm(int[] slim, int[] wide, int u, int v) {
        for (int y = 0; y < 16; y++) {
            int row = (v + y) * SIZE + u;
            for (int x = 0; x < 16; x++) wide[row + x] = 0;
            for (int i = 0; i < 4; i++) {
                if (y < 4) {
                    wide[row + 4 + i] = slim[row + 4 + STRETCH[i]];
                    wide[row + 8 + i] = slim[row + 7 + STRETCH[i]];
                } else {
                    wide[row + i] = slim[row + i];
                    wide[row + 4 + i] = slim[row + 4 + STRETCH[i]];
                    wide[row + 8 + i] = slim[row + 7 + i];
                    wide[row + 12 + i] = slim[row + 11 + STRETCH[i]];
                }
            }
        }
    }

    private static @Nullable Suit selectedSuit() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return null;
        Registry<Suit> suits = level.registryAccess().lookupOrThrow(BrightestDayRegistries.SUIT);
        return suits.getValue(BrightestDay.id("lanterns_suit"));
    }

    private static @Nullable Identifier findTexture(Suit suit, LanternCorps corps, HumanoidArm side) {
        Identifier directory = Suit.getCorrectedPath(suit.id());
        Map<Identifier, Resource> files = Minecraft.getInstance().getResourceManager().listResources(directory.getPath(),
                id -> id.getNamespace().equals(directory.getNamespace()) && id.getPath().endsWith(".png"));

        String own = "_" + side.getSerializedName();
        String opposite = "_" + side.getOpposite().getSerializedName();
        Identifier fallback = null;
        for (Identifier file : files.keySet()) {
            String path = file.getPath();
            String name = path.substring(path.lastIndexOf('/') + 1, path.length() - ".png".length());
            if (!isFor(name, corps.getSerializedName()) && !isFor(name, corps.emotion())) continue;
            if (name.endsWith(own)) return file;
            if (fallback == null || !name.endsWith(opposite)) fallback = file;
        }
        return fallback;
    }

    private static boolean isFor(String name, String token) {
        return name.equals(token) || name.startsWith(token + "_");
    }

    private static int @Nullable [] read(Identifier path) {
        Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(path);
        if (resource.isEmpty()) return null;
        try (InputStream stream = resource.get().open(); NativeImage image = NativeImage.read(stream)) {
            return image.getWidth() == SIZE && image.getHeight() == SIZE ? image.getPixels() : null;
        } catch (IOException e) {
            BrightestDay.LOGGER.warn("Failed to read lantern suit {}", path, e);
            return null;
        }
    }

    private record Key(Identifier skin, LanternCorps corps, boolean slim) {}

    static final class Entry {
        final Identifier bodyId;
        final int slot;
        final int size;
        final DynamicTexture body;
        @Nullable Key key;

        private Entry(int slot, int size) {
            this.slot = slot;
            this.bodyId = BrightestDay.id("suit/" + slot);
            this.size = size;
            this.body = new DynamicTexture(this.bodyId::toString, size, size, true);
            TextureManager textures = Minecraft.getInstance().getTextureManager();
            textures.register(this.bodyId, this.body);
        }

        private void close() {
            TextureManager textures = Minecraft.getInstance().getTextureManager();
            textures.release(this.bodyId);
            FREE_SLOTS.addLast(this.slot);
        }
    }
}
