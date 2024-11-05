package net.salju.quill.network;

import net.salju.quill.Quill;
import net.salju.quill.events.QuillClientManager;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.core.BlockPos;

public record Fireworks(BlockPos pos) implements CustomPacketPayload {
	public static final Type<Fireworks> CREEPER_FIREWORKS = new Type<>(ResourceLocation.fromNamespaceAndPath(Quill.MODID, "creeper_fireworks"));
	public static final StreamCodec<RegistryFriendlyByteBuf, Fireworks> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, Fireworks message) -> {
		buffer.writeBlockPos(message.pos);
	}, (RegistryFriendlyByteBuf buffer) -> new Fireworks(buffer.readBlockPos()));

	@Override
	public Type<Fireworks> type() {
		return CREEPER_FIREWORKS;
	}

	public static void handleData(final Fireworks message, final IPayloadContext context) {
		if (context.flow() == PacketFlow.CLIENTBOUND) {
			if (context.player().level() instanceof ClientLevel lvl) {
				QuillClientManager.creeperFireworks(lvl, message.pos.getX(), message.pos.getY(), message.pos.getZ());
			}
		}
	}
}