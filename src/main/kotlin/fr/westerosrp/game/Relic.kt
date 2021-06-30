package fr.westerosrp.game

import fr.westerosrp.WesterosRP
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.block.Container
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.BlockInventoryHolder
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Repairable
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffectType
import java.util.*

enum class RelicType(val humanName: String, val color: ChatColor) {
	RARE("Rare", ChatColor.AQUA),
	EPIC("Epique", ChatColor.LIGHT_PURPLE),
	LEGENDARY("Légendaire", ChatColor.GOLD),
	SPECIAL("Spécial", ChatColor.DARK_GRAY),
	DUNGEON("Donjon", ChatColor.RED),
	FRAGMENT("Fragment", ChatColor.GRAY)
}

val casinoLocations: List<Location> = listOf(Location(Bukkit.getWorld("world")!!, 0.0, 0.0, 0.0))
val dungeonLocations: List<Location> = listOf(Location(Bukkit.getWorld("world")!!, 0.0, 0.0, 0.0))

enum class Relic(
	val humanName: String,
	val lore: String,
	val type: RelicType,
	val material: Material,
	val spawn: List<Location> = casinoLocations,
	val respawn: List<Location> = dungeonLocations
) {
	WATER_SPEED_ARTIFACT(
		"Water Speed",
		"Confère des effets propices à l'exploration maraine",
		RelicType.RARE,
		Material.GLOW_INK_SAC,
		listOf(Location(Bukkit.getWorld("world")!!, 0.0, 0.0, 0.0))
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
		Material.AMETHYST_SHARD,
		listOf(Location(Bukkit.getWorld("world")!!, 0.0, 0.0, 0.0))
	) {
		override fun onInventoryAdd(player: Player) {
			player.addPotionEffect(PotionEffectType.FAST_DIGGING.createEffect(-1, 1))
		}

		override fun onInventoryRemove(player: Player) {
			player.removePotionEffect(PotionEffectType.FAST_DIGGING)
		}
	},
	JUMP_ARTIFACT(
		"High Jump",
		"Permet de sauter plus haut",
		RelicType.RARE,
		Material.RABBIT_FOOT,
		listOf(Location(Bukkit.getWorld("world")!!, 0.0, 0.0, 0.0))
	) {
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
		Material.HONEYCOMB,
		listOf(Location(Bukkit.getWorld("world")!!, 0.0, 0.0, 0.0))
	) {
		override fun onInventoryAdd(player: Player) {
			player.addPotionEffect(PotionEffectType.REGENERATION.createEffect(-1, 1))
		}

		override fun onInventoryRemove(player: Player) {
			player.removePotionEffect(PotionEffectType.REGENERATION)
		}
	},
	LUCK_ARTIFACT(
		"Luck",
		"Porte bonheur",
		RelicType.RARE,
		Material.SCUTE,
		listOf(Location(Bukkit.getWorld("world")!!, 0.0, 0.0, 0.0))
	) {
		override fun onInventoryAdd(player: Player) {
			player.addPotionEffect(PotionEffectType.LUCK.createEffect(-1, 3))
		}

		override fun onInventoryRemove(player: Player) {
			player.removePotionEffect(PotionEffectType.LUCK)
		}
	},


	LOOTING_SWORD(
		"Looting sword",
		"Double vos récompenses",
		RelicType.RARE,
		Material.IRON_SWORD,
		listOf(Location(Bukkit.getWorld("world")!!, 0.0, 0.0, 0.0))
	) {
		override fun generateItemStack(): ItemStack {
			return super.generateItemStack().also { stack ->
				stack.itemMeta = stack.itemMeta?.also {
					it.addEnchant(Enchantment.LOOT_BONUS_MOBS, 4, true)
					(it as Repairable).repairCost = -1
				}
			}
		}
	},

	FIRE_SWORD(
		"Fire sword",
		"Brûle vos ennemis jusqu'à la mort",
		RelicType.RARE,
		Material.IRON_SWORD,
		listOf(Location(Bukkit.getWorld("world")!!, 0.0, 0.0, 0.0))
	) {
		override fun generateItemStack(): ItemStack {
			return super.generateItemStack().also { stack ->
				stack.itemMeta = stack.itemMeta?.also {
					it.addEnchant(Enchantment.FIRE_ASPECT, 10, true)
					(it as Repairable).repairCost = -1
				}
			}
		}
	},

	KNOCKBACK_SWORD(
		"Knockback sword",
		"Repousse vos ennemis",
		RelicType.RARE,
		Material.IRON_SWORD,
		listOf(Location(Bukkit.getWorld("world")!!, 0.0, 0.0, 0.0))
	) {
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

	MONSTER_SWORD(
		"Monster sword",
		"Efficace contre les monstres",
		RelicType.RARE,
		Material.IRON_SWORD,
		listOf(Location(Bukkit.getWorld("world")!!, 0.0, 0.0, 0.0))
	) {
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

	PERFECT_ROD(
		"Perfect rod",
		"La canne à pêche parfaite",
		RelicType.RARE,
		Material.FISHING_ROD,
		listOf(Location(Bukkit.getWorld("world")!!, 0.0, 0.0, 0.0))
	) {
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

	FAST_PICKAXE(
		"Fast pickaxe",
		"Cooble breaker",
		RelicType.RARE,
		Material.STONE_PICKAXE,
		listOf(Location(Bukkit.getWorld("world")!!, 0.0, 0.0, 0.0))
	) {
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

	SHARP_AXE(
		"Sharp axe",
		"Super tranchante",
		RelicType.RARE,
		Material.DIAMOND_AXE,
		listOf(Location(Bukkit.getWorld("world")!!, 0.0, 0.0, 0.0))
	) {
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
	},

	FRAGMENT_GREEN(
		"Valshamr",
		"Une relique spéciale renfermant un pouvoir immense",
		RelicType.FRAGMENT,
		Material.RAW_GOLD,
		listOf(Location(Bukkit.getWorld("world")!!, 0.0, 0.0, 0.0)),
		listOf(Location(Bukkit.getWorld("world")!!, 0.0, 0.0, 0.0))
	) {
		override fun generateItemStack(): ItemStack {
			return super.generateItemStack().also { stack ->
				stack.itemMeta = stack.itemMeta?.also {
					it.setDisplayName("${ChatColor.GREEN}$humanName [${type.humanName}]")
				}
			}
		}
	},
	FRAGMENT_RED(
		"Skidbladnir",
		"Une relique spéciale renfermant un pouvoir immense",
		RelicType.FRAGMENT,
		Material.NETHERITE_SCRAP,
		listOf(Location(Bukkit.getWorld("world")!!, 0.0, 0.0, 0.0)),
		listOf(Location(Bukkit.getWorld("world")!!, 0.0, 0.0, 0.0))
		) {
		override fun generateItemStack(): ItemStack {
			return super.generateItemStack().also { stack ->
				stack.itemMeta = stack.itemMeta?.also {
					it.setDisplayName("${ChatColor.RED}$humanName [${type.humanName}]")
				}
			}
		}
	},
	FRAGMENT_BLUE(
		"Naglfar",
		"Une relique spéciale renfermant un pouvoir immense",
		RelicType.FRAGMENT,
		Material.PRISMARINE_CRYSTALS,
		listOf(Location(Bukkit.getWorld("world")!!, 0.0, 0.0, 0.0)),
		listOf(Location(Bukkit.getWorld("world")!!, 0.0, 0.0, 0.0))
	) {
		override fun generateItemStack(): ItemStack {
			return super.generateItemStack().also { stack ->
				stack.itemMeta = stack.itemMeta?.also {
					it.setDisplayName("${ChatColor.AQUA}$humanName [${type.humanName}]")
				}
			}
		}
	};

	companion object {
		val relicKey = NamespacedKey(WesterosRP.instance, "relic-id")
	}

	open fun onInventoryAdd(player: Player) {}

	open fun generateItemStack(): ItemStack {
		return ItemStack(material, 1)
			.also { stack ->
				stack.itemMeta = stack.itemMeta?.also {
					it.setDisplayName("${type.color}$humanName [${type.humanName}]")
					it.lore = listOf(
						"",
						"${ChatColor.GRAY}${ChatColor.BOLD}$lore"
					)
					it.isUnbreakable = true
					it.addEnchant(Enchantment.DURABILITY, 3, true)
				}
			}
	}

	fun generateWithID(): ItemStack {
		return generateItemStack().also { stack ->
			stack.itemMeta = stack.itemMeta?.also {
				it.persistentDataContainer.set(relicKey, PersistentDataType.INTEGER, this.ordinal)
			}
		}
	}

	private fun spawn(loc: Location) {
		if (loc.block.state !is BlockInventoryHolder) {
			loc.block.type = Material.CHEST
		}
		(loc.block.state as BlockInventoryHolder).inventory.addItem(generateWithID())
	}

	fun spawn() {
		spawn(spawn.random())
	}

	fun respawn() {
		Bukkit.broadcastMessage("${WesterosRP.prefix}${ChatColor.GOLD} La relique ${ChatColor.GRAY}${humanName}${ChatColor.GOLD} a respawn !")
		spawn(respawn.random())
	}

	open fun onInventoryRemove(player: Player) {}
}