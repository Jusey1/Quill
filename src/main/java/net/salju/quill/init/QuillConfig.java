package net.salju.quill.init;

import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec;

public class QuillConfig {
	public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
	public static final IConfigSpec CONFIG;

	public static final ModConfigSpec.BooleanValue USER;
	public static final ModConfigSpec.BooleanValue POTS;
	public static final ModConfigSpec.BooleanValue FARMER;
	public static final ModConfigSpec.BooleanValue WEAPONS;
	public static final ModConfigSpec.BooleanValue AXES;
	public static final ModConfigSpec.BooleanValue SPEARS;
	public static final ModConfigSpec.BooleanValue LADDER;
	public static final ModConfigSpec.BooleanValue VAULT;

	public static final ModConfigSpec.BooleanValue DEATH;
	public static final ModConfigSpec.BooleanValue CREEPER;
	public static final ModConfigSpec.BooleanValue FROGGO;
	public static final ModConfigSpec.BooleanValue PEACEFUL;
	public static final ModConfigSpec.BooleanValue NO;
	public static final ModConfigSpec.BooleanValue TAXI;
	public static final ModConfigSpec.BooleanValue KICK;
	public static final ModConfigSpec.BooleanValue CAMPFIRE;
	public static final ModConfigSpec.IntValue CAMPRANGE;

	public static final ModConfigSpec.BooleanValue UNBREAKABLE;
	public static final ModConfigSpec.BooleanValue UNBREAKING;
	public static final ModConfigSpec.BooleanValue REPAIR;
	public static final ModConfigSpec.BooleanValue ANBOOK;
	public static final ModConfigSpec.IntValue MAXANBOOKCOST;
	public static final ModConfigSpec.BooleanValue RENAME;
	public static final ModConfigSpec.BooleanValue GRIND;
	public static final ModConfigSpec.IntValue MAGRANGE;
	
	static {
		BUILDER.push("Block & Item Configuration");
		USER = BUILDER.comment("Should basic items get a cooldown during use after being hit by an enemy?").define("Basic Cooldown", true);
		POTS = BUILDER.comment("Should potions be stackable?").define("Stackable Potions", true);
		FARMER = BUILDER.comment("Should hoes have the ability to harvest crops?").define("Hoe Harvesting", true);
		WEAPONS = BUILDER.comment("Should unique weapons have their stats tweaked by this mod?").define("Weapons Rebalanced", true);
		AXES = BUILDER.comment("Should axes have their stats tweaked by this mod?").define("Axes Rebalanced", true);
		SPEARS = BUILDER.comment("Should spears have their stats tweaked by this mod?").define("Spears Rebalanced", true);
		LADDER = BUILDER.comment("Should improved ladder placing be enabled?").define("Better Ladders", true);
		VAULT = BUILDER.comment("Should Vaults be reusable by the player?").define("Better Vaults", false);
		BUILDER.pop();
		BUILDER.push("Player & Entity Configuration");
		DEATH = BUILDER.comment("Should hotbar & equipment be kept on death?").define("Death Protection", true);
		CREEPER = BUILDER.comment("Should creepers be prideful?").define("Prideful Creepers", true);
		FROGGO = BUILDER.comment("Should witches turn Nitwits into frogs?").define("Frog Transfiguration", true);
		PEACEFUL = BUILDER.comment("Should tagged mobs be peaceful by default unless provoked?").define("Peaceful Mobs", false);
		NO = BUILDER.comment("Should tagged mobs be unallowed to grief blocks?").define("Stop That", false);
		TAXI = BUILDER.comment("Should villagers join a player's camel or boat by simply being nearby?").define("Taxi Camel", true);
		KICK = BUILDER.comment("Should the player be able to crouch right-click entities off of what they are riding?").define("Kick Them Off", true);
		CAMPFIRE = BUILDER.comment("Should campfires disable enemy spawning if lit?").define("Campfire Protection", true);
		CAMPRANGE = BUILDER.comment("Campfire's range for disabling enemy spawning.").defineInRange("Campfire Range", 64, 0, Integer.MAX_VALUE);
		BUILDER.pop();
		BUILDER.push("Enchantment Configuration");
		UNBREAKABLE = BUILDER.comment("Should enchanted equipment be unbreakable?").define("Unbreakable Protection", false);
		UNBREAKING = BUILDER.comment("Should equipment be unbreakable with the Unbreaking Enchantment?").define("Unbreaking Protection", false);
		REPAIR = BUILDER.comment("Should repairing equipment be better & only costing 1 level?").define("Better Anvil Repair", true);
		ANBOOK = BUILDER.comment("Should this mod adjust combining enchanted items on an anvil?").define("Better Anvil Enchanting", true);
		MAXANBOOKCOST = BUILDER.comment("Maximum cost on an anvil when combining?").defineInRange("Max Anvil Cost", 15, 0, 45);
		RENAME = BUILDER.comment("Should renaming always only cost 1 level?").define("Better Anvil Renaming", true);
		GRIND = BUILDER.comment("Should the grindstone be usable to disenchant items onto an enchanted book?").define("Grindstone Disenchant", true);
		MAGRANGE = BUILDER.comment("Magnetic Enchantment's range for pulling items per level of the enchantment.").defineInRange("Magnetic Range", 6, 0, Integer.MAX_VALUE);
		BUILDER.pop();
		CONFIG = BUILDER.build();
	}
}