package net.salju.quill.events;

import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.client.particle.FireworkParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.component.FireworkExplosion;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import com.google.common.collect.Lists;
import java.util.List;

public class QuillClientManager {
	public static void creeperFireworks(ClientLevel lvl, double x, double y, double z) {
		Minecraft.getInstance().particleEngine.add(new FireworkParticles.Starter(lvl, x + 0.5, y + 0.5, z + 0.5, 0, 0, 0, Minecraft.getInstance().particleEngine, getPride()));
	}

	private static List<FireworkExplosion> getPride() {
		List<FireworkExplosion> fireworks = Lists.newArrayList();
		IntArrayList colors = new IntArrayList();
		int i = Mth.nextInt(RandomSource.create(), 0, 10);
		if (i <= 0) {
			colors.add(0,0x1D1D21);
			colors.add(1,0x474F52);
			colors.add(2,0xF9FFFE);
			colors.add(3,0x80C71F);
		} else if (i == 1) {
			colors.add(0,0x1D1D21);
			colors.add(1,0x474F52);
			colors.add(2,0xF9FFFE);
			colors.add(4,0x8932B8);
		} else if (i == 2) {
			colors.add(0,0xB02E26);
			colors.add(1,0xF9801D);
			colors.add(2,0xFED83D);
			colors.add(3,0x009933);
			colors.add(4,0x3C44AA);
			colors.add(5,0x8932B8);
		} else if (i == 3) {
			colors.add(0,0xF9801D);
			colors.add(1,0xF9FFFE);
			colors.add(2,0xFF00FF);
		} else if (i == 4) {
			colors.add(0,0xEE2B7A);
			colors.add(1,0x8932B8);
			colors.add(2,0x3C44AA);
		} else if (i == 5) {
			colors.add(0,0x00FFFF);
			colors.add(1,0xF38BAA);
			colors.add(2,0xF9FFFE);
		} else if (i == 6) {
			colors.add(0,0xEE2B7A);
			colors.add(1,0xFED83D);
			colors.add(2,0x3AB3DA);
		} else if (i == 7) {
			colors.add(0,0xFED83D);
			colors.add(1,0xF9FFFE);
			colors.add(2,0x8932B8);
			colors.add(3,0x1D1D21);
		} else if (i == 8) {
			colors.add(0,0xF38BAA);
			colors.add(1,0xF9FFFE);
			colors.add(2,0xDF83FF);
			colors.add(3,0x1D1D21);
			colors.add(4,0x3C44AA);
		} else if (i == 9) {
			colors.add(0,0xDF83FF);
			colors.add(1,0xF9FFFE);
			colors.add(2,0x009933);
		} else {
			colors.add(0,0xFED83D);
			colors.add(1,0x8932B8);
		}
		fireworks.add(new FireworkExplosion(FireworkExplosion.Shape.BURST, colors, colors, true, true));
		return fireworks;
	}
}