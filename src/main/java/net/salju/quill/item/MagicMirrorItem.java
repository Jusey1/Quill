package net.salju.quill.item;

import net.salju.quill.init.QuillData;
import net.salju.quill.item.component.MagicMirrorTeleport;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import java.util.Optional;
import java.util.function.Consumer;

public class MagicMirrorItem extends Item {
	public MagicMirrorItem(Item.Properties props) {
		super(props);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, context, display, list, flag);
		MagicMirrorTeleport data = stack.getOrDefault(QuillData.TELEPORT, MagicMirrorTeleport.EMPTY);
		list.accept(Component.literal(data.getPos().getX() + ", " + data.getPos().getY() + ", " + data.getPos().getZ()).withStyle(ChatFormatting.DARK_PURPLE));
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity target) {
		return 64;
	}

	@Override
	public ItemUseAnimation getUseAnimation(ItemStack stack) {
		return ItemUseAnimation.BLOCK;
	}

	@Override
	public ItemStack getDefaultInstance() {
		ItemStack stack = super.getDefaultInstance();
		stack.set(QuillData.TELEPORT, MagicMirrorTeleport.EMPTY);
		return stack;
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel lvl, Entity target, EquipmentSlot slot) {
		super.inventoryTick(stack, lvl, target, slot);
		MagicMirrorTeleport data = stack.getOrDefault(QuillData.TELEPORT, MagicMirrorTeleport.EMPTY);
		if (target instanceof ServerPlayer ply) {
			if (ply.getRespawnConfig() == null) {
				if (!BlockPos.containing(data.getPos().getX(), data.getPos().getY(), data.getPos().getZ()).equals(lvl.getLevelData().getSpawnPos())) {
					stack.set(QuillData.TELEPORT, new MagicMirrorTeleport(Optional.of(GlobalPos.of(Level.OVERWORLD, lvl.getLevelData().getSpawnPos()))));
				}
			} else {
				if (!BlockPos.containing(data.getPos().getX(), data.getPos().getY(), data.getPos().getZ()).equals(ply.getRespawnConfig().pos())) {
					stack.set(QuillData.TELEPORT, new MagicMirrorTeleport(Optional.of(GlobalPos.of(ply.getRespawnConfig().dimension(), ply.getRespawnConfig().pos()))));
				}
			}
		}
	}

	@Override
	public InteractionResult use(Level world, Player player, InteractionHand hand) {
		player.startUsingItem(hand);
		return InteractionResult.CONSUME;
	}

	@Override
	public boolean releaseUsing(ItemStack stack, Level world, LivingEntity target, int i) {
		MagicMirrorTeleport data = stack.getOrDefault(QuillData.TELEPORT, MagicMirrorTeleport.EMPTY);
		if (i <= 10 && target instanceof ServerPlayer ply && world instanceof ServerLevel lvl && data.target().isPresent()) {
			double x = data.getPos().getX() + 0.5;
			double y = data.getPos().getY() + (ply.getRespawnConfig() != null ? 0.7 : 0.0);
			double z = data.getPos().getZ() + 0.5;
			ServerLevel loc = ply.getServer().getLevel(data.target().get().dimension());
			if (loc != null) {
				lvl.playSound(null, ply.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, (float) (0.8F + (Math.random() * 0.2)));
				lvl.sendParticles(ParticleTypes.PORTAL, ply.getX(), ply.getY(), ply.getZ(), 12, 0.5, 0.5, 0.5, 0.65);
				ply.teleport(new TeleportTransition(loc, new Vec3(x, y, z), ply.getDeltaMovement(), ply.getYRot(), ply.getXRot(), TeleportTransition.DO_NOTHING));
				loc.playSound(null, BlockPos.containing(x, y, z), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, (float) (0.8F + (Math.random() * 0.2)));
				loc.sendParticles(ParticleTypes.PORTAL, x, y, z, 12, 0.5, 0.5, 0.5, 0.65);
			}
			ply.getCooldowns().addCooldown(stack, 200);
		}
		return super.releaseUsing(stack, world, target, i);
	}

	@Override
	public void onUseTick(Level world, LivingEntity target, ItemStack stack, int i) {
		super.onUseTick(world, target, stack, i);
		if (world instanceof ServerLevel lvl) {
			if (i <= 1) {
				this.releaseUsing(stack, world, target, i);
			} else {
				lvl.sendParticles(ParticleTypes.PORTAL, target.getX(), target.getY(), target.getZ(), 1, 0.5, 0.5, 0.5, 0.65);
			}
		}
	}
}