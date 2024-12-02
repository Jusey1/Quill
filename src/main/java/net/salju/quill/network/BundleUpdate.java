package net.salju.quill.network;

import net.salju.quill.Quill;
import net.salju.quill.init.QuillData;
import net.salju.quill.item.BundleHoldingItem;
import net.salju.quill.item.component.BundleHoldingContents;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public record BundleUpdate(int s, int i) implements CustomPacketPayload {
	public static final Type<BundleUpdate> BUNDLE_UPDATE = new Type<>(ResourceLocation.fromNamespaceAndPath(Quill.MODID, "bundle_update"));
	public static final StreamCodec<RegistryFriendlyByteBuf, BundleUpdate> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, BundleUpdate message) -> {
		buffer.writeInt(message.s);
		buffer.writeInt(message.i);
	}, (RegistryFriendlyByteBuf buffer) -> new BundleUpdate(buffer.readInt(), buffer.readInt()));

	@Override
	public Type<BundleUpdate> type() {
		return BUNDLE_UPDATE;
	}

	public static void handleData(final BundleUpdate message, final IPayloadContext context) {
		if (context.flow() == PacketFlow.SERVERBOUND) {
			ItemStack stack = context.player().containerMenu.getSlot(message.s).getItem();
			if (stack.getItem() instanceof BundleHoldingItem target) {
				stack.set(QuillData.BUNDLE, new BundleHoldingContents(target.getContents(stack), message.i));
			}
		}
	}
}