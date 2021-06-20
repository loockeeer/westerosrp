package fr.westerosrp.listeners

import fr.westerosrp.Team
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class PlayerChat : Listener {
    @EventHandler
    fun handle(e: AsyncPlayerChatEvent) {
        val playerTeam = Team.getPlayerTeam(e.player) ?: return
        e.format = "%s${playerTeam.color} :${ChatColor.RESET} %s"
    }
}