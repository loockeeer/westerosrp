package fr.westerosrp.game

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Repairable
import org.bukkit.potion.PotionEffectType
import java.util.*

enum class RelicType(val humanName: String, val color: ChatColor) {
	RARE("Rare", ChatColor.AQUA),
	EPIC("Epique", ChatColor.LIGHT_PURPLE),
	LEGENDARY("Légendaire", ChatColor.GOLD),
}

enum class Relic(val humanName: String, val lore: String, val type: RelicType, val material: Material) {
	WATER_SPEED_ARTIFACT(
		"Water Speed",
		"Confère des effets propices à l'exploration maraine",
		RelicType.RARE,
		Material.GLOW_INK_SAC
	) {
		override fun onInventoryAdd(player: Player) {
			player.addPotionEffect(PotionEffectType.DOLPHINS_GRACE.createEffect(-1, 1))
		}

		override fun onInventoryRemove(player: Player) {
			player.removePotionEffect(PotionEffectType.DOLPHINS_GRACE)
		}
	},
	MINING_SPEED_ARTIFACT(
		"Mining Speed",
		"Confère des effets propices au minage",
		RelicType.RARE,
		Material.AMETHYST_SHARD
	) {
		override fun onInventoryAdd(player: Player) {
			player.addPotionEffect(PotionEffectType.FAST_DIGGING.createEffect(-1, 1))
		}

		override fun onInventoryRemove(player: Player) {
			player.removePotionEffect(PotionEffectType.FAST_DIGGING)
		}
	},
	JUMP_ARTIFACT("High Jump", "Permet de sauter plus haut", RelicType.RARE, Material.RABBIT_FOOT) {
		override fun onInventoryAdd(player: Player) {
			player.addPotionEffect(PotionEffectType.JUMP.createEffect(-1, 1))
		}

		override fun onInventoryRemove(player: Player) {
			player.removePotionEffect(PotionEffectType.FAST_DIGGING)
		}
	},
	REGENERATION_ARTIFACT(
		"Regeneration",
		"Confère un effet de régénération constant",
		RelicType.RARE,
		Material.HONEYCOMB
	) {
		override fun onInventoryAdd(player: Player) {
			player.addPotionEffect(PotionEffectType.REGENERATION.createEffect(-1, 1))
		}

		override fun onInventoryRemove(player: Player) {
			player.removePotionEffect(PotionEffectType.REGENERATION)
		}
	},
	LUCK_ARTIFACT("Luck", "Porte bonheur", RelicType.RARE, Material.SCUTE) {
		override fun onInventoryAdd(player: Player) {
			player.addPotionEffect(PotionEffectType.LUCK.createEffect(-1, 3))
		}

		override fun onInventoryRemove(player: Player) {
			player.removePotionEffect(PotionEffectType.LUCK)
		}
	},


	LOOTING_SWORD("Looting sword", "Double vos récompenses", RelicType.RARE, Material.IRON_SWORD) {
		override fun generateItemStack(): ItemStack {
			return super.generateItemStack().also { stack ->
				stack.itemMeta = stack.itemMeta?.also {
					it.addEnchant(Enchantment.LOOT_BONUS_MOBS, 4, true)
					(it as Repairable).repairCost = -1
				}
			}
		}
	},

	FIRE_SWORD("Fire sword", "Brûle vos ennemis jusqu'à la mort", RelicType.RARE, Material.IRON_SWORD) {
		override fun generateItemStack(): ItemStack {
			return super.generateItemStack().also { stack ->
				stack.itemMeta = stack.itemMeta?.also {
					it.addEnchant(Enchantment.FIRE_ASPECT, 10, true)
					(it as Repairable).repairCost = -1
				}
			}
		}
	},

	KNOCKBACK_SWORD("Knockback sword", "Repousse vos ennemis", RelicType.RARE, Material.IRON_SWORD) {
		override fun generateItemStack(): ItemStack {
			return super.generateItemStack().also { stack ->
				stack.itemMeta = stack.itemMeta?.also {
					it.addEnchant(Enchantment.KNOCKBACK, 5, true)
					it.addEnchant(Enchantment.SWEEPING_EDGE, 3, true)
					(it as Repairable).repairCost = -1
				}
			}
		}
	},

	MONSTER_SWORD("Monster sword", "Efficace contre les monstres", RelicType.RARE, Material.IRON_SWORD) {
		override fun generateItemStack(): ItemStack {
			return super.generateItemStack().also { stack ->
				stack.itemMeta = stack.itemMeta?.also {
					it.addEnchant(Enchantment.DAMAGE_ARTHROPODS, 7, true)
					it.addEnchant(Enchantment.DAMAGE_UNDEAD, 7, true)
					(it as Repairable).repairCost = -1
				}
			}
		}
	},

	PERFECT_ROD("Perfect rod", "La canne à pêche parfaite", RelicType.RARE, Material.FISHING_ROD) {
		override fun generateItemStack(): ItemStack {
			return super.generateItemStack().also { stack ->
				stack.itemMeta = stack.itemMeta?.also {
					it.addEnchant(Enchantment.LUCK, 5, true)
					it.addEnchant(Enchantment.LURE, 5, true)
					(it as Repairable).repairCost = -1
				}
			}
		}
	},

	FAST_PICKAXE("Fast pickaxe", "Cooble breaker", RelicType.RARE, Material.STONE_PICKAXE) {
		override fun generateItemStack(): ItemStack {
			return super.generateItemStack().also { stack ->

				stack.itemMeta = stack.itemMeta?.also {
					it.addEnchant(Enchantment.DIG_SPEED, 8, true)
					it.addEnchant(Enchantment.MENDING, 1, true)
					(it as Repairable).repairCost = -1
					it.isUnbreakable = false
				}
			}
		}
	},

	SHARP_AXE("Sharp axe", "Super tranchante", RelicType.RARE, Material.DIAMOND_AXE) {
		override fun generateItemStack(): ItemStack {
			return super.generateItemStack().also { stack ->
				stack.itemMeta = stack.itemMeta?.also { meta ->
					meta.addEnchant(Enchantment.DAMAGE_ALL, 7, true)
					meta.addAttributeModifier(
						Attribute.GENERIC_ATTACK_SPEED,
						AttributeModifier(
							UUID.randomUUID(),
							"generic.attackSpeed",
							-1.0,
							AttributeModifier.Operation.ADD_NUMBER,
							EquipmentSlot.HAND
						)
					)
					meta.addAttributeModifier(
						Attribute.GENERIC_ATTACK_SPEED,
						AttributeModifier(
							UUID.randomUUID(),
							"generic.attackSpeed",
							-1.0,
							AttributeModifier.Operation.ADD_NUMBER,
							EquipmentSlot.OFF_HAND
						)
					)
				}
			}
		}
	};

	open fun onInventoryAdd(player: Player) {}

	open fun generateItemStack(): ItemStack {
		return ItemStack(material, 1)
			.also { stack ->
				stack.itemMeta = stack.itemMeta?.also {
					it.setDisplayName("${type.color}$humanName")
					it.lore = listOf(
						"${ChatColor.GOLD}Type: ${type.color}${type.humanName}",
						"${ChatColor.GRAY}${ChatColor.BOLD}$lore"
					)
					it.isUnbreakable = true
					it.addEnchant(Enchantment.DURABILITY, 3, true)
				}
			}
	}

	open fun onInventoryRemove(player: Player) {}
}