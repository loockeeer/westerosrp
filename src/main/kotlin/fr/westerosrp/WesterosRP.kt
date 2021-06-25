package fr.westerosrp

import fr.westerosrp.command.*
import fr.westerosrp.game.Month
import fr.westerosrp.game.Scoreboard
import fr.westerosrp.game.Team
import fr.westerosrp.game.Territory
import fr.westerosrp.listeners.*
import net.luckperms.api.LuckPerms
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class WesterosRP : JavaPlugin() {
	var running = false

	companion object {
		lateinit var instance: WesterosRP
			private set
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

		if(running) {
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