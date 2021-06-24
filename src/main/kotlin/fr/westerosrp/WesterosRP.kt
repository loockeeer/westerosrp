package fr.westerosrp

import fr.westerosrp.command.Invsee
import fr.westerosrp.command.Next
import fr.westerosrp.command.ScoreboardTrigger
import fr.westerosrp.command.Start
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
            var instance = Start()
            it.setExecutor(instance)
        }
        
        getCommand("wnext")?.also {
            var instance = Next()
            it.setExecutor(instance)
            it.tabCompleter = instance
        }

        getCommand("wscoreboard")?.also {
            var instance = ScoreboardTrigger()
            it.setExecutor(instance)
            it.tabCompleter = instance
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

        logger.info("${description.name} version ${description.version} enabled!")
    }

    override fun onDisable() {
        logger.info("${description.name} version ${description.version} disabled!")
    }

    fun start() {
        this.running = true
        Month.nextMonth()
        scheduleMonthRoll(this)
    }
}