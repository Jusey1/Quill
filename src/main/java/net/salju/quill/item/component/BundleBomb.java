package net.salju.quill.item.component;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;

public record BundleBomb(boolean check) {
	public static final BundleBomb EMPTY = new BundleBomb(false);
	public static final Codec<BundleBomb> CODEC = RecordCodecBuilder.create(codec -> codec.group(Codec.BOOL.fieldOf("check").forGetter(BundleBomb::check)).apply(codec, BundleBomb::new));
	public static final StreamCodec<ByteBuf, BundleBomb> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, BundleBomb::check, BundleBomb::new);

	public BundleBomb(boolean check) {
		this.check = check;
	}

	public boolean isBomb() {
        return check;
    }
}