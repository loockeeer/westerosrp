package fr.westerosrp

import org.bukkit.entity.Player

object Utils {
    fun updatePlayer(player: Player) {
        val playerTeam = Team.getPlayerTeam(player) ?: return
        player.setPlayerListName(playerTeam.getPlayerName(player))
        player.setDisplayName(playerTeam.getPlayerName(player))
    }
}