package fr.westerosrp

import fr.westerosrp.command.*
import fr.westerosrp.game.Month
import fr.westerosrp.utils.Scoreboard
import fr.westerosrp.game.Team
import fr.westerosrp.game.Territory
import fr.westerosrp.listeners.*
import net.luckperms.api.LuckPerms
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.block.Biome
import org.bukkit.plugin.java.JavaPlugin

class WesterosRP : JavaPlugin() {
	var running = false

	companion object {
		lateinit var instance: WesterosRP
			private set

		val prefix = "${ChatColor.GRAY}${ChatColor.BOLD}WesterosRP |${ChatColor.RESET}"
	}

	override fun onEnable() {
		instance = this
		saveDefaultConfig()

		getCommand("winvsee")?.also {
			var instance = Invsee()
			it.setExecutor(instance)
			it.tabCompleter = instance
		}

		getCommand("wstart")?.also {
			val instance = Start()
			it.setExecutor(instance)
		}

		getCommand("wnext")?.also {
			val instance = Next()
			it.setExecutor(instance)
			it.tabCompleter = instance
		}

		getCommand("wscoreboard")?.also {
			val instance = ScoreboardTrigger()
			it.setExecutor(instance)
			it.tabCompleter = instance
		}

		getCommand("wreliclist")?.also {
			val instance = RelicList()
			it.setExecutor(instance)
		}

		server.pluginManager.registerEvents(PlayerChat(), this)
		server.pluginManager.registerEvents(PlayerJoin(), this)
		server.pluginManager.registerEvents(PlayerQuit(), this)
		server.pluginManager.registerEvents(PlayerMove(), this)

		val luckPerms = server.servicesManager.load(LuckPerms::class.java)
		if (luckPerms != null) {
			LuckPermsListener(this, luckPerms).register()
		} else {
			logger.warning("Unable to load correctly luckperms.")
		}

		running = config.getBoolean("running")

		Team.values().forEach(Team::initialize)
		Territory.values().forEach(Territory::initialize)

		Bukkit.getOnlinePlayers().forEach {
			Scoreboard.updateBoard(it)
			updatePlayer(it)
		}

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, {
			Bukkit.getOnlinePlayers().forEach {
				Scoreboard.updateBoard(it)
				updateTabList(it)
			}
		}, 0L, 20L)

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, {
			Bukkit.getOnlinePlayers().forEach {
				if(it.world.getBiome(it.location.x.toInt(), it.location.y.toInt(), it.location.z.toInt()) == Biome.SWAMP) {
					when ((1..6).random()) {
						1 -> it.playSound(it.location, Sound.AMBIENT_SOUL_SAND_VALLEY_MOOD, SoundCategory.MASTER, 100.0f, 1.0f)
						2 -> it.playSound(it.location, Sound.AMBIENT_SOUL_SAND_VALLEY_ADDITIONS, SoundCategory.MASTER, 100.0f, 1.0f)
						3 -> it.playSound(it.location, Sound.PARTICLE_SOUL_ESCAPE, SoundCategory.MASTER, 100.0f, 1.0f)
						4 -> it.playSound(it.location, Sound.AMBIENT_UNDERWATER_LOOP_ADDITIONS_ULTRA_RARE, SoundCategory.MASTER, 100.0f, 1.0f)
						5 -> it.playSound(it.location, Sound.ENTITY_VEX_CHARGE, SoundCategory.MASTER, 100.0f, 1.0f)
					}
				}
			}
		}, 0L, 20L*5)

		if (running) {
			scheduleMonthRoll(this)
		}

		logger.info("${description.name} version ${description.version} enabled!")
	}

	override fun onDisable() {
		logger.info("${description.name} version ${description.version} disabled!")
	}

	fun start() {
		this.running = true
		config.set("running", true)
		saveConfig()
		Month.nextMonth(1)
		scheduleMonthRoll(this)
	}
}