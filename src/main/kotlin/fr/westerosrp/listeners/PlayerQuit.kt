package fr.westerosrp.listeners

import fr.westerosrp.game.Scoreboard
import fr.westerosrp.game.Team
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerQuit : Listener {
	@EventHandler
	fun handle(e: PlayerQuitEvent) {
		val playerTeam = Team.getPlayerTeam(e.player) ?: return
		e.quitMessage = null

		Scoreboard.removeBoard(e.player)

		e.player.server.broadcastMessage(
			"[${ChatColor.RED}-${ChatColor.RESET}] ${playerTeam.getPlayerName(e.player)}"
		)

	}
}