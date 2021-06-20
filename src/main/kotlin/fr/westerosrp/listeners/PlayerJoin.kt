package fr.westerosrp.listeners

import fr.westerosrp.Team
import fr.westerosrp.Utils
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoin : Listener {
    @EventHandler
    fun handle(e: PlayerJoinEvent) {
        val playerTeam = Team.getPlayerTeam(e.player) ?: return
        e.joinMessage = null

        e.player.server.broadcastMessage(
            "[${ChatColor.GREEN}+${ChatColor.RESET}] ${playerTeam.getPlayerName(e.player)}"
        )

        Utils.updatePlayer(e.player)
    }
}