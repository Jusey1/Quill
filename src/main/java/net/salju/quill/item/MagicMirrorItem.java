package net.salju.quill.item;

import net.salju.quill.init.QuillData;
import net.salju.quill.item.component.MagicMirrorTeleport;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.chat.Component;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import java.util.List;

public class MagicMirrorItem extends Item {
	public MagicMirrorItem(Item.Properties props) {
		super(props);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, context, list, flag);
		MagicMirrorTeleport data = stack.getOrDefault(QuillData.TELEPORT, MagicMirrorTeleport.EMPTY);
		list.add(Component.literal(data.getX() + ", " + data.getY() + ", " + data.getZ()).withStyle(ChatFormatting.DARK_PURPLE));
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
	public void inventoryTick(ItemStack stack, Level world, Entity target, int e, boolean check) {
		super.inventoryTick(stack, world, target, e, check);
		MagicMirrorTeleport data = stack.getOrDefault(QuillData.TELEPORT, MagicMirrorTeleport.EMPTY);
		if (target instanceof ServerPlayer ply && world instanceof ServerLevel) {
			if (ply.getRespawnPosition() == null) {
				if (!BlockPos.containing(data.getX(), data.getY(), data.getZ()).equals(world.getLevelData().getSpawnPos())) {
					stack.set(QuillData.TELEPORT, new MagicMirrorTeleport(world.getLevelData().getSpawnPos().getX(), world.getLevelData().getSpawnPos().getY(), world.getLevelData().getSpawnPos().getZ()));
				}
			} else {
				if (!BlockPos.containing(data.getX(), data.getY(), data.getZ()).equals(ply.getRespawnPosition())) {
					stack.set(QuillData.TELEPORT, new MagicMirrorTeleport(ply.getRespawnPosition().getX(), ply.getRespawnPosition().getY(), ply.getRespawnPosition().getZ()));
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
		if (i <= 10 && target instanceof ServerPlayer ply && world instanceof ServerLevel lvl) {
			ResourceKey<Level> dim = ply.getRespawnDimension();
			if (dim == null) {
				dim = Level.OVERWORLD;
			}
			double x;
			double y;
			double z;
			ServerLevel loc = ply.server.getLevel(dim);
			if (ply.getRespawnPosition() == null) {
				x = world.getLevelData().getSpawnPos().getX() + 0.5;
				y = world.getLevelData().getSpawnPos().getY();
				z = world.getLevelData().getSpawnPos().getZ() + 0.5;
			} else {
				x = ply.getRespawnPosition().getX() + 0.5;
				y = ply.getRespawnPosition().getY() + 0.7;
				z = ply.getRespawnPosition().getZ() + 0.5;
			}
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