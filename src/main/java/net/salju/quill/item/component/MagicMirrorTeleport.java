package net.salju.quill.item.component;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Optional;

public record MagicMirrorTeleport(Optional<GlobalPos> target) {
	public static final MagicMirrorTeleport EMPTY = new MagicMirrorTeleport(Optional.empty());
	public static final Codec<MagicMirrorTeleport> CODEC = RecordCodecBuilder.create(codec -> codec.group(GlobalPos.CODEC.optionalFieldOf("target").forGetter(MagicMirrorTeleport::target)).apply(codec, MagicMirrorTeleport::new));
	public static final StreamCodec<ByteBuf, MagicMirrorTeleport> STREAM_CODEC = StreamCodec.composite(GlobalPos.STREAM_CODEC.apply(ByteBufCodecs::optional), MagicMirrorTeleport::target, MagicMirrorTeleport::new);

	public MagicMirrorTeleport(Optional<GlobalPos> target) {
		this.target = target;
	}

	public BlockPos getPos() {
		return this.target.map(GlobalPos::pos).orElseGet(() -> BlockPos.containing(0, 0, 0));
	}
}