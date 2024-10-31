package net.salju.quill.item.component;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record MagicMirrorTeleport(int x, int y, int z) {
	public static final MagicMirrorTeleport EMPTY = new MagicMirrorTeleport(0, 0, 0);
	public static final Codec<MagicMirrorTeleport> CODEC = RecordCodecBuilder.create(codec -> codec.group(Codec.INT.fieldOf("x").forGetter(MagicMirrorTeleport::x), Codec.INT.fieldOf("y").forGetter(MagicMirrorTeleport::y), Codec.INT.fieldOf("z").forGetter(MagicMirrorTeleport::z)).apply(codec, MagicMirrorTeleport::new));
	public static final StreamCodec<ByteBuf, MagicMirrorTeleport> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, MagicMirrorTeleport::x, ByteBufCodecs.INT, MagicMirrorTeleport::y, ByteBufCodecs.INT, MagicMirrorTeleport::z, MagicMirrorTeleport::new);

	public MagicMirrorTeleport(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getZ() {
		return this.z;
	}
}