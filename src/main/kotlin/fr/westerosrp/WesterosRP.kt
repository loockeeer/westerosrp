package fr.westerosrp

import fr.westerosrp.command.Invsee
import fr.westerosrp.listeners.LuckPermsListener
import fr.westerosrp.listeners.PlayerChat
import fr.westerosrp.listeners.PlayerJoin
import fr.westerosrp.listeners.PlayerQuit
import net.luckperms.api.LuckPerms
import org.bukkit.plugin.java.JavaPlugin


class WesterosRP : JavaPlugin {
    constructor()

    override fun onEnable() {
        saveDefaultConfig()

        getCommand("winvsee")?.also {
            var instance = Invsee()
            it.setExecutor(instance)
            it.setTabCompleter(instance)
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

        logger.info("${description.name} version ${description.version} enabled!")
    }

    override fun onDisable() {
        logger.info("${description.name} version ${description.version} disabled!")
    }
}