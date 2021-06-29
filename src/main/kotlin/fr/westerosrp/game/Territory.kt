package fr.westerosrp.game


import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector2
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.flags.Flags
import com.sk89q.worldguard.protection.flags.StateFlag
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import fr.westerosrp.*
import fr.westerosrp.database.TerritoryTypes
import org.bukkit.*
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Zombie
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.event.vehicle.VehicleDamageEvent
import org.bukkit.event.vehicle.VehicleDestroyEvent
import org.bukkit.inventory.ItemStack
import java.util.*


enum class Territory(
	val humanName: String,
	val color: ChatColor,
	val type: TerritoryTypes,
	val owner: Team?,
	val regionId: String,
	val spawn: Location,
	val salaryChest: Location?,
	val level: Int,
) {

	ARENA(
		"Arène PvP",
		ChatColor.GRAY,
		TerritoryTypes.SPECIAL,
		null,
		"arena",
		Location(Bukkit.getWorld("world")!!, 0.0, 0.0, 0.0),
		Location(Bukkit.getWorld("world")!!, 0.0, 0.0, 0.0),
		0
	) {
		val bossBar = Bukkit.createBossBar("${entered.size} joueur${if(entered.size>1)"s" else ""}", BarColor.RED, BarStyle.SOLID)?.also { it.removeAll() }

		override fun enter(e: PlayerMoveEvent): String {
			bossBar.addPlayer(e.player)
			bossBar.setTitle("${entered.size} joueur${if(entered.size>1)"s" else ""}")
			entered.forEach { entry ->
				Bukkit.getPlayer(entry.key)?.let {
					it.playSound(it.location, Sound.BLOCK_ANVIL_LAND, SoundCategory.MASTER, 100.0f, 1.0f)
				}
			}
			return super.enter(e) + " Il y a actuellement ${ChatColor.GRAY}${entered.size - 1}${ChatColor.GOLD} preux guerrier${if (entered.size > 2) "s" else ""} dans la zone !"
		}

		override fun leave(e: PlayerMoveEvent): String {
			bossBar.removePlayer(e.player)
			bossBar.setTitle("${entered.size} joueur${if(entered.size>1)"s" else ""}")
			entered.forEach { entry ->
				Bukkit.getPlayer(entry.key)?.let {
					it.playSound(it.location, Sound.BLOCK_ANVIL_LAND, SoundCategory.MASTER, 100.0f, 1.0f)
				}
			}
			return super.enter(e)
		}

		inner class Listeners : Listener, Territory.Listeners() {
			@EventHandler(priority = EventPriority.HIGHEST)
			fun creatureSpawn(e: CreatureSpawnEvent) {
				e.isCancelled = true
			}

			@EventHandler(priority = EventPriority.HIGHEST)
			override fun playerInteract(e: PlayerInteractEvent) {
				e.isCancelled = false
			}
		}
	},


	LARGE_SPAWN(
		"Contrées du Spawn",
		ChatColor.GRAY,
		TerritoryTypes.SPAWN,
		null,
		"large_spawn",
		Location(Bukkit.getWorld("world")!!, 0.0, 0.0, 0.0),
		Location(Bukkit.getWorld("world")!!, 0.0, 0.0, 0.0),
		0
	) {
	},

	INNER_SPAWN(
		"Enceinte du château du Spawn",
		ChatColor.GRAY,
		TerritoryTypes.SPAWN,
		null,
		"inner_spawn",
		Location(Bukkit.getWorld("world")!!, 117.0, 75.0, 16.0),
		Location(Bukkit.getWorld("world")!!, 0.0, 0.0, 0.0),
		0
	) {
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

		inner class Listeners : Listener, Territory.Listeners() {
			fun playerDamage(e: EntityDamageEvent) {
				e.isCancelled = true
			}
		}
	},

	DANGER_SPAWN(
		"Le château du Spawn",
		ChatColor.DARK_RED,
		TerritoryTypes.SPAWN,
		null,
		"danger_spawn",
		Location(Bukkit.getWorld("world")!!, 0.0, 0.0, 0.0),
		Location(Bukkit.getWorld("world")!!, 0.0, 0.0, 0.0),
		0
	) {
		override fun initialize() {
			super.initialize()
			if (isCorrect) {
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
			return if ((entered[e.player.uniqueId]?.plus(10000L))!! > currentTime) {
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
		Location(Bukkit.getWorld("world")!!, -804.0, 63.0, -499.0),
		Location(Bukkit.getWorld("world")!!, 0.0, 0.0, 0.0),
		0
	) {
		inner class Listeners : Listener, Territory.Listeners() {
			@EventHandler(priority = EventPriority.NORMAL)
			override fun entityExplode(e: EntityExplodeEvent) {
				e.isCancelled = false
			}
		}
	  },
	RED_TERRITORY(
		"Territoire Rouge",
		ChatColor.RED,
		TerritoryTypes.TEAM,
		Team.RED,
		"red",
		Location(Bukkit.getWorld("world")!!, 398.0, 65.0, 1115.0),
		Location(Bukkit.getWorld("world")!!, 0.0, 0.0, 0.0),
		0
	){
		inner class Listeners : Listener, Territory.Listeners() {
			@EventHandler(priority = EventPriority.NORMAL)
			override fun entityExplode(e: EntityExplodeEvent) {
				e.isCancelled = false
			}
		}
	},
	GREEN_TERRITORY(
		"Territoire Vert",
		ChatColor.GREEN,
		TerritoryTypes.TEAM,
		Team.GREEN,
		"green",
		Location(Bukkit.getWorld("world")!!, 1165.0, 71.0, -24.0),
		Location(Bukkit.getWorld("world")!!, 0.0, 0.0, 0.0),
		0
	){
		inner class Listeners : Listener, Territory.Listeners() {
			@EventHandler(priority = EventPriority.NORMAL)
			override fun entityExplode(e: EntityExplodeEvent) {
				e.isCancelled = false
			}
		}
	};

	var region: ProtectedRegion? = null
	val entered: HashMap<UUID, Long> = hashMapOf()


	val isCorrect
		get() = region != null

	open fun enter(e: PlayerMoveEvent): String {
		var baseText = "${ChatColor.GOLD}Vous entrez dans ${this.color}${this.humanName}${ChatColor.RESET}${ChatColor.GOLD}. Ici, le ${ChatColor.GRAY}PvP${ChatColor.GOLD} est ${
			if (this.region?.getFlag(
					Flags.PVP
				) == StateFlag.State.ALLOW
			) "${ChatColor.UNDERLINE}activé" else "${ChatColor.UNDERLINE}désactivé"
		}${ChatColor.RESET}${ChatColor.GOLD}."
		when(type) {
			TerritoryTypes.VILLAGER -> {
				spawn.world?.spawn(spawn, Zombie::class.java)
				baseText += "\n${ChatColor.RED}${ChatColor.BOLD}[CHEF BARBARE]${ChatColor.RESET}${ChatColor.RED} Attaquez cet intrus !"
			}
			TerritoryTypes.DUNGEON -> {
				spawn.world?.spawn(spawn, Zombie::class.java)
				baseText += "\n${ChatColor.RED}${ChatColor.BOLD}[BOSS]${ChatColor.RESET}${ChatColor.RED} Ne le laissez pas passer !"
			}
		}
		return baseText
	}

	open fun leave(e: PlayerMoveEvent): String {
		entered.remove(e.player.uniqueId)
		return "${ChatColor.GOLD}Vous quittez ${this.color}${this.humanName}${ChatColor.RESET}${ChatColor.GOLD}."
	}

	open fun initialize() {
		region = WorldGuard.getInstance()?.platform?.regionContainer?.get(BukkitAdapter.adapt(Bukkit.getWorld("world")))
			?.getRegion(regionId)
		if (!isCorrect) WesterosRP.instance.logger.warning("Territory ${this.name} region cannot be loaded successfully !")
		else WesterosRP.instance.server.pluginManager.registerEvents(Listeners(), WesterosRP.instance)
	}

	fun contains(loc: Location) = region?.contains(BlockVector2.at(loc.x, loc.z))

	fun isOwner(player: Player) = player.isOp || Team.getPlayerTeam(player)?.isAdmin() == true

	open inner class Listeners : Listener {
		@EventHandler(priority = EventPriority.HIGHEST)
		open fun blockPlace(e: BlockPlaceEvent) {
			if(!checkPosition(region!!, e.block.location)) return
			if(isOwner(e.player)) {
				e.isCancelled = false
				return
			}
			if(e.block.type == Material.ENDER_CHEST || e.block.type.toString().contains("SHULKER_BOX")) {
				e.isCancelled = false
				return
			}  else if(!isOwner(e.player)) e.isCancelled = true
			e.player.sendCoolErrorMessage("Vous ne pouvez pas poser de blocs ici !")
		}

		@EventHandler(priority = EventPriority.HIGHEST)
		open fun blockBreak(e: BlockBreakEvent) {
			if(!checkPosition(region!!, e.block.location)) return
			if(isOwner(e.player)) {
				e.isCancelled = false
				return
			}
			if(e.block.type == Material.ENDER_CHEST || e.block.type.toString().contains("SHULKER_BOX")) {
				e.isCancelled = false
				return
			}
			else if(e.block.type.toString().contains("DOOR") && e.player.itemInUse?.itemMeta?.displayName == "Multi-Pass") {
				e.isCancelled = false
				return
			}
			else if(!isOwner(e.player)) e.isCancelled = true
			e.player.sendCoolErrorMessage("Vous ne pouvez pas casser de blocs ici !")
		}

		@EventHandler(priority = EventPriority.HIGHEST)
		open fun playerInteract(e: PlayerInteractEvent) {
			if (e.clickedBlock == null) return
			if (!checkPosition(region!!, e.clickedBlock?.location!!)) return
			if (isOwner(e.player)) {
				e.isCancelled = false
				return
			}
			if(e.material.toString().contains("DOOR")) {
				e.isCancelled = true
				e.player.sendCoolErrorMessage("Vous ne pouvez pas interagir avec des blocs ici !")
			}
		}

		@EventHandler(priority = EventPriority.HIGHEST)
		open fun vehicleDestroy(e: VehicleDestroyEvent) {
			if (!checkPosition(region!!, e.vehicle.location)) return
			if (e.attacker?.type != EntityType.PLAYER) return
			if (isOwner(e.attacker as Player)) {
				e.isCancelled = false
				return
			}
			e.isCancelled = true
			(e.attacker as Player).sendCoolErrorMessage("Vous ne pouvez pas détruire ceci ici !")
		}

		@EventHandler(priority = EventPriority.HIGHEST)
		open fun vehicleDamage(e: VehicleDamageEvent) {
			if (!checkPosition(region!!, e.vehicle.location)) return
			if (e.attacker?.type != EntityType.PLAYER) return
			if (isOwner(e.attacker as Player)) {
				e.isCancelled = false
				return
			}
			e.isCancelled = true
			(e.attacker as Player).sendCoolErrorMessage("Vous ne pouvez pas détruire ceci ici !")
		}

		@EventHandler(priority = EventPriority.HIGHEST)
		open fun blockExplode(e: BlockExplodeEvent) {
			if(!checkPosition(region!!, e.block.location)) return
			e.isCancelled = true
		}

		@EventHandler(priority = EventPriority.HIGHEST)
		open fun entityExplode(e: EntityExplodeEvent) {
			if(!checkPosition(region!!, e.entity.location)) return
			e.isCancelled = true
		}

		@EventHandler(priority = EventPriority.HIGHEST)
		open fun playerTeleport(e: PlayerTeleportEvent) {
			if(!checkPosition(region!!, e.from)) return
			if(e.cause == PlayerTeleportEvent.TeleportCause.ENDER_PEARL
				&& type != TerritoryTypes.SPAWN
				&& isCorrect && !checkPosition(region!!, e.to!!)) {
				e.isCancelled = true
				e.player.inventory.addItem(ItemStack(Material.ENDER_PEARL, 1))
				e.player.sendCoolErrorMessage("Vous ne pouvez pas vous téléporter là bas !")
			}
		}
	}
}