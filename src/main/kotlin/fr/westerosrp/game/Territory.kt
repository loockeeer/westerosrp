package fr.westerosrp.game


import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector2
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.flags.Flags
import com.sk89q.worldguard.protection.flags.StateFlag
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import fr.westerosrp.WesterosRP
import fr.westerosrp.database.TerritoryTypes
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import java.util.*


enum class Territory(
	val humanName: String,
	val color: ChatColor,
	val type: TerritoryTypes,
	val owner: Team?,
	val regionId: String,
	val spawn: Location
) {

	ARENA(
		"Arène PvP",
		ChatColor.GRAY,
		TerritoryTypes.SPECIAL,
		null,
		"arena",
		Location(Bukkit.getWorld("world")!!, 0.0, 0.0, 0.0)
	) {
		override fun initialize() {
			super.initialize()
			if (isCorrect()) {
				region?.let {
					it.setFlag(Flags.INTERACT, StateFlag.State.DENY)
					it.setFlag(Flags.USE, StateFlag.State.DENY)
					it.setFlag(Flags.PVP, StateFlag.State.ALLOW)
					it.setFlag(Flags.BLOCK_PLACE, StateFlag.State.DENY)
					it.setFlag(Flags.BLOCK_BREAK, StateFlag.State.DENY)
					it.setFlag(Flags.BUILD, StateFlag.State.DENY)
					it.setFlag(Flags.ITEM_DROP, StateFlag.State.ALLOW)
					it.setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY)
					it.setFlag(Flags.USE, StateFlag.State.DENY)
					it.setFlag(Flags.CHEST_ACCESS, StateFlag.State.DENY)
				}
			}
		}

		override fun enter(e: PlayerMoveEvent): String {
			return super.enter(e) + " Il y a actuellement ${ChatColor.GRAY}${entered.size - 1}${ChatColor.GOLD} preux guerrier${if (entered.size > 2) "s" else ""} dans la zone !"
		}
	},


	LARGE_SPAWN(
		"Contrées du Spawn",
		ChatColor.GRAY,
		TerritoryTypes.SPAWN,
		null,
		"large_spawn",
		Location(Bukkit.getWorld("world")!!, 0.0, 0.0, 0.0)
	) {
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

	INNER_SPAWN(
		"Enceinte du château du Spawn",
		ChatColor.GRAY,
		TerritoryTypes.SPAWN,
		null,
		"inner_spawn",
		Location(Bukkit.getWorld("world")!!, 117.0, 75.0, 16.0)
	) {
		override fun initialize() {
			super.initialize()
			if (isCorrect()) {
				region?.let {
					it.setFlag(Flags.PVP, StateFlag.State.DENY)
					it.setFlag(Flags.CHORUS_TELEPORT, StateFlag.State.DENY)
					it.setFlag(Flags.ITEM_DROP, StateFlag.State.DENY)

				}
			}
		}

		override fun leave(e: PlayerMoveEvent): String {
			if (!WesterosRP.instance.running) {
				if (!e.player.isOp || Team.getPlayerTeam(e.player)?.equals(Team.ADMIN) != true) {
					e.setTo(spawn)
					return "${ChatColor.RED}${ChatColor.BOLD}La partie n'a pas encore commencée !"
				}
				return super.leave(e)
			} else {
				return super.leave(e)
			}
		}
	},

	DANGER_SPAWN(
		"Le château du Spawn",
		ChatColor.DARK_RED,
		TerritoryTypes.SPAWN,
		null,
		"danger_spawn",
		Location(Bukkit.getWorld("world")!!, 0.0, 0.0, 0.0)
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

		override fun enter(e: PlayerMoveEvent): String {
			Bukkit.getScheduler().scheduleSyncDelayedTask(WesterosRP.instance, {
				if (region?.contains(BlockVector2.at(e.player.location.x, e.player.location.z)) == true) {
					e.player.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[Chef de la Garde]${ChatColor.RESET}${ChatColor.RED} Gardes, attaquez cet intrus !")
				}
			}, 20L * 10)
			e.player.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[Chef de la Garde]${ChatColor.RESET}${ChatColor.RED} Vous avez ${ChatColor.GRAY}10 secondes${ChatColor.RED} pour partir, après quoi ce sera la guerre !")
			return super.enter(e)
		}

		override fun leave(e: PlayerMoveEvent): String {
			return if ((entered[e.player.uniqueId]?.plus(10000L))!! > Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris")).timeInMillis) {
				e.player.sendMessage("${ChatColor.GOLD}${ChatColor.BOLD}[Chef de la Garde]${ChatColor.RESET}${ChatColor.GOLD} Bon choix...")
				super.leave(e)
			} else {
				e.player.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[Chef de la Garde]${ChatColor.RESET}${ChatColor.RED} Je vous aurai !")
				super.leave(e)
			}
		}
	},

	BLUE_TERRITORY(
		"Territoire Bleu",
		ChatColor.BLUE,
		TerritoryTypes.TEAM,
		Team.BLUE,
		"blue",
		Location(Bukkit.getWorld("world")!!, -804.0, 63.0, -499.0)
	),
	RED_TERRITORY(
		"Territoire Rouge",
		ChatColor.RED,
		TerritoryTypes.TEAM,
		Team.RED,
		"red",
		Location(Bukkit.getWorld("world")!!, 398.0, 65.0, 1115.0)
	),
	GREEN_TERRITORY(
		"Territoire Vert",
		ChatColor.GREEN,
		TerritoryTypes.TEAM,
		Team.GREEN,
		"green",
		Location(Bukkit.getWorld("world")!!, 1165.0, 71.0, -24.0)
	);

	var region: ProtectedRegion? = null
	val entered: HashMap<UUID, Long> = hashMapOf()

	fun isCorrect(): Boolean {
		return region != null
	}

	open fun enter(e: PlayerMoveEvent): String {
		return "${ChatColor.GOLD}Vous entrez dans ${this.color}${this.humanName}${ChatColor.RESET}${ChatColor.GOLD}. Ici, le ${ChatColor.GRAY}PvP${ChatColor.GOLD} est ${
			if (this.region?.getFlag(
					Flags.PVP
				) == StateFlag.State.ALLOW
			) "${ChatColor.UNDERLINE}activé" else "${ChatColor.UNDERLINE}désactivé"
		}${ChatColor.RESET}${ChatColor.GOLD}."
	}

	open fun leave(e: PlayerMoveEvent): String {
		entered.remove(e.player.uniqueId)
		return "${ChatColor.GOLD}Vous quittez ${this.color}${this.humanName}${ChatColor.RESET}${ChatColor.GOLD}."
	}

	open fun initialize() {
		region = WorldGuard.getInstance()?.platform?.regionContainer?.get(BukkitAdapter.adapt(Bukkit.getWorld("world")))
			?.getRegion(regionId)
		if (!isCorrect()) WesterosRP.instance.logger.warning("Territory ${this.name} region cannot be loaded successfully !")
	}

	fun contains(loc: Location) = region?.contains(BlockVector2.at(loc.x, loc.z))

	open inner class Listeners : Listener
}