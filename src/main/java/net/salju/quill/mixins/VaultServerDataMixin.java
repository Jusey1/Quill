package net.salju.quill.mixins;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;
import net.salju.quill.init.QuillConfig;
import net.minecraft.world.level.block.entity.vault.VaultServerData;

@Mixin(VaultServerData.class)
public abstract class VaultServerDataMixin {
	@Inject(method = "addToRewardedPlayers", at = @At("HEAD"), cancellable = true)
	private void data(CallbackInfo ci) {
		if (QuillConfig.VAULT.get()) {
			ci.cancel();
		}
	}
}