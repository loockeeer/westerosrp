package fr.westerosrp

import fr.westerosrp.command.Invsee
import fr.westerosrp.command.Start
import fr.westerosrp.game.Month
import fr.westerosrp.game.Scoreboard
import fr.westerosrp.game.Team
import fr.westerosrp.listeners.LuckPermsListener
import fr.westerosrp.listeners.PlayerChat
import fr.westerosrp.listeners.PlayerJoin
import fr.westerosrp.listeners.PlayerQuit
import net.luckperms.api.LuckPerms
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class WesterosRP : JavaPlugin {
    var running = false
    companion object {
        lateinit var instance: WesterosRP
        private set
    }
    constructor() {
        WesterosRP.instance = this
    }

    override fun onEnable() {
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


        val luckPerms = server.servicesManager.load(LuckPerms::class.java)

        Team.values().forEach {
            if(!it.isCorrect()) {
                logger.warning("Team ${it.name} group cannot be loaded successfully !")
            }
        }

        server.pluginManager.registerEvents(PlayerChat(), this)
        server.pluginManager.registerEvents(PlayerJoin(), this)
        server.pluginManager.registerEvents(PlayerQuit(), this)

        if (luckPerms != null) {
            LuckPermsListener(this, luckPerms).register()
        } else {
            logger.warning("Unable to load correctly luckperms.")
        }

        Bukkit.getOnlinePlayers().forEach {
            Scoreboard.updateBoard(it)
            updatePlayer(it)
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, {
            Bukkit.getOnlinePlayers().forEach {
                Scoreboard.updateBoard(it)
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