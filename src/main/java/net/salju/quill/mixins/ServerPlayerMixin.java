package net.salju.quill.mixins;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;
import net.salju.quill.init.QuillConfig;
import net.salju.quill.init.QuillTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
	@Inject(method = "restoreFrom", at = @At(value = "HEAD"))
	private void dontDropGear(ServerPlayer dead, boolean alive, CallbackInfo ci) {
		if (QuillConfig.DEATH.get()) {
			ServerPlayer ply = (ServerPlayer) (Object) this;
			for (int i = 0; i < dead.getInventory().getContainerSize(); i++) {
				ItemStack stack = dead.getInventory().getItem(i);
				if (i < 9 || i > 35 || stack.is(QuillTags.PROTECTED)) {
					ply.getInventory().setItem(i, dead.getInventory().getItem(i));
				}
			}
		}
	}
}