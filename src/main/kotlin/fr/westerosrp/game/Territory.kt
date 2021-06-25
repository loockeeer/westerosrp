package fr.westerosrp.game


import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector2
import com.sk89q.worldedit.world.weather.WeatherType
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.flags.Flags
import com.sk89q.worldguard.protection.flags.StateFlag
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import fr.westerosrp.WesterosRP
import fr.westerosrp.database.TerritoryTypes
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*


enum class Territory(
	val humanName: String,
	val color: ChatColor,
	val type: TerritoryTypes,
	val owner: Team?,
	val regionId: String
) {

	ARENA("Arène PvP", ChatColor.GRAY, TerritoryTypes.SPECIAL, null, "arena") {
		override fun initialize() {
			super.initialize()
			if(isCorrect()) {
				region?.let {
					it.setFlag(Flags.PVP, StateFlag.State.ALLOW)
					it.setFlag(Flags.BUILD,  StateFlag.State.DENY)
					it.setFlag(Flags.BLOCK_BREAK, StateFlag.State.DENY)
					it.setFlag(Flags.BLOCK_PLACE, StateFlag.State.DENY)
					it.setFlag(Flags.WEATHER_LOCK, WeatherType("clear"))
					it.setFlag(Flags.PLACE_VEHICLE, StateFlag.State.DENY)
					it.setFlag(Flags.DESTROY_VEHICLE, StateFlag.State.DENY)
					it.setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY)
					it.setFlag(Flags.MOB_DAMAGE, StateFlag.State.DENY)
					it.setFlag(Flags.DAMAGE_ANIMALS, StateFlag.State.DENY)
					it.setFlag(Flags.INTERACT, StateFlag.State.DENY)
					it.setFlag(Flags.USE, StateFlag.State.DENY)
				}
			}
		}

		override fun enter(player: Player): String {
			return super.enter(player)+" Il y a actuellement ${ChatColor.GRAY}${entered.size-1}${ChatColor.GOLD} preux guerrier${if(entered.size>2) "s" else ""} dans la zone !"
		}
	},


	LARGE_SPAWN("Contrées du Spawn", ChatColor.GRAY, TerritoryTypes.SPAWN, null, "large_spawn") {
		override fun initialize() {
			super.initialize()
			if (isCorrect()) {
				region?.let {
					it.setFlag(Flags.BLOCK_BREAK, StateFlag.State.DENY)
					it.setFlag(Flags.BLOCK_PLACE, StateFlag.State.DENY)
					it.setFlag(Flags.BUILD, StateFlag.State.DENY)
					it.setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY)
					it.setFlag(Flags.PVP, StateFlag.State.ALLOW)
					it.setFlag(Flags.CHEST_ACCESS, StateFlag.State.DENY)
					it.setFlag(Flags.CREEPER_EXPLOSION, StateFlag.State.DENY)
					it.setFlag(Flags.OTHER_EXPLOSION, StateFlag.State.DENY)
					it.setFlag(Flags.DESTROY_VEHICLE, StateFlag.State.DENY)
					it.setFlag(Flags.PLACE_VEHICLE, StateFlag.State.DENY)
					it.setFlag(Flags.CHORUS_TELEPORT, StateFlag.State.ALLOW)
					it.setFlag(Flags.ENTITY_ITEM_FRAME_DESTROY, StateFlag.State.DENY)
					it.setFlag(Flags.ENTITY_PAINTING_DESTROY, StateFlag.State.DENY)
					it.setFlag(Flags.USE, StateFlag.State.ALLOW)
					it.setFlag(Flags.INTERACT, StateFlag.State.ALLOW)
				}
			}
		}
	},

	INNER_SPAWN("Enceinte du château du Spawn", ChatColor.GRAY, TerritoryTypes.SPAWN, null, "inner_spawn") {
		override fun initialize() {
			super.initialize()
			if (isCorrect()) {
				region?.let {
					it.setFlag(Flags.PVP, StateFlag.State.DENY)
					it.setFlag(Flags.EXIT, StateFlag.State.DENY)
					it.setFlag(
						Flags.EXIT_DENY_MESSAGE,
						"${ChatColor.RED}${ChatColor.BOLD}La partie n'a pas encore commencée !"
					)
					it.setFlag(Flags.CHORUS_TELEPORT, StateFlag.State.DENY)
					it.setFlag(Flags.ITEM_DROP, StateFlag.State.DENY)

				}
			}
		}
	},

	DANGER_SPAWN(
		"Le château du Spawn",
		ChatColor.DARK_RED,
		TerritoryTypes.SPAWN,
		null,
		"danger_spawn"
	) {
		override fun initialize() {
			super.initialize()
			if (isCorrect()) {
				region?.let {
					it.setFlag(Flags.MOB_DAMAGE, StateFlag.State.ALLOW)
					it.setFlag(Flags.PVP, StateFlag.State.ALLOW)
				}
			}
		}

		override fun enter(player: Player): String {
			Bukkit.getScheduler().scheduleSyncDelayedTask(WesterosRP.instance, {
				if (region?.contains(BlockVector2.at(player.location.x, player.location.z)) == true) {
					player.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[Chef de la Garde]${ChatColor.RESET}${ChatColor.RED} Gardes, attaquez cet intrus !")
				}
			}, 20L * 10)
			return super.enter(player) + "\n${ChatColor.RED}${ChatColor.BOLD}[Chef de la Garde]${ChatColor.RESET}${ChatColor.RED} Vous avez ${ChatColor.GRAY}10 secondes${ChatColor.RED} pour partir, après quoi ce sera la guerre !"
		}

		override fun leave(player: Player): String {
			return if ((entered[player.uniqueId]?.plus(10000L))!! > Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris")).timeInMillis) {
				super.leave(player) + "\n${ChatColor.GOLD}${ChatColor.BOLD}[Chef de la Garde]${ChatColor.RESET}${ChatColor.GOLD} Bon choix..."
			} else {
				super.leave(player) + "\n${ChatColor.RED}${ChatColor.BOLD}[Chef de la Garde]${ChatColor.RESET}${ChatColor.RED} Je vous aurai !"
			}
		}
	},

	BLUE_TERRITORY(
		"Territoire Bleu",
		ChatColor.BLUE,
		TerritoryTypes.TEAM,
		Team.BLUE,
		"blue"
	),
	RED_TERRITORY(
		"Territoire Rouge",
		ChatColor.RED,
		TerritoryTypes.TEAM,
		Team.RED,
		"red"
	),
	GREEN_TERRITORY(
		"Territoire Vert",
		ChatColor.GREEN,
		TerritoryTypes.TEAM,
		Team.GREEN,
		"green"
	);

	var region: ProtectedRegion? = null
	val entered: HashMap<UUID, Long> = hashMapOf()

	fun isCorrect(): Boolean {
		return region != null
	}

	open fun enter(player: Player): String {
		return "${ChatColor.GOLD}Vous entrez dans ${this.color}${this.humanName}${ChatColor.RESET}${ChatColor.GOLD}. Ici, le ${ChatColor.GRAY}PvP${ChatColor.GOLD} est ${
			if (this.region?.getFlag(
					Flags.PVP
				) == StateFlag.State.ALLOW
			) "${ChatColor.UNDERLINE}activé" else "${ChatColor.UNDERLINE}désactivé"
		}${ChatColor.RESET}${ChatColor.GOLD}."
	}

	open fun leave(player: Player): String {
		return "${ChatColor.GOLD}Vous quittez ${this.color}${this.humanName}${ChatColor.RESET}${ChatColor.GOLD}."
	}

	open fun initialize() {
		region = WorldGuard.getInstance()?.platform?.regionContainer?.get(BukkitAdapter.adapt(Bukkit.getWorld("world")))
			?.getRegion(regionId)
		if (!isCorrect()) WesterosRP.instance.logger.warning("Territory ${this.name} region cannot be loaded successfully !")
	}
}