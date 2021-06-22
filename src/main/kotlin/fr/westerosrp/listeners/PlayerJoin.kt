package fr.westerosrp.listeners

import fr.westerosrp.game.Scoreboard
import fr.westerosrp.game.Team
import fr.westerosrp.updatePlayer
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

        Scoreboard.updateBoard(e.player)

        updatePlayer(e.player)
    }
}