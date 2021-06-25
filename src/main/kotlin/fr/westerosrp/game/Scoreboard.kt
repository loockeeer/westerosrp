package fr.westerosrp.game

import fr.mrmicky.fastboard.FastBoard
import fr.westerosrp.WesterosRP
import fr.westerosrp.boolToString
import fr.westerosrp.timeUntilMidnight
import org.apache.commons.lang.time.DurationFormatUtils
import org.bukkit.ChatColor
import org.bukkit.Statistic
import org.bukkit.entity.Player
import java.util.*


object Scoreboard {
	private val board = HashMap<UUID, FastBoard>()
	private val boardTrigger = HashMap<UUID, Boolean>()

	fun triggerBoard(player: Player) = triggerBoard(player, !(boardTrigger[player.uniqueId] ?: false))

	fun triggerBoard(player: Player, state: Boolean): Boolean {
		boardTrigger[player.uniqueId] = state

		if (!state) removeBoard(player)

		return state
	}

	private fun generateBoard(player: Player) = FastBoard(player).also { this.board[player.uniqueId] = it }


	fun removeBoard(player: Player) = this.board.remove(player.uniqueId).also { it?.delete() }

	fun updateBoard(player: Player) =
		if (boardTrigger[player.uniqueId] != false) updateBoard(getBoard(player)) else null

	private fun updateBoard(board: FastBoard): FastBoard {
		val team = Team.getPlayerTeam(board.player) ?: return board
		val midnight = DurationFormatUtils.formatDuration(timeUntilMidnight(), "HH:mm:ss")
		board.updateTitle("${ChatColor.BOLD}${ChatColor.GRAY}Westeros RP")
		board.updateLines(
			"",
			"${ChatColor.GOLD}Equipe: ${team.color}${team.humanName}",
			"${ChatColor.GOLD}Mois:${ChatColor.GRAY} ${Month.currentMonth}",
			"${ChatColor.GOLD}Temps:${ChatColor.GRAY} ${if (WesterosRP.instance.running) midnight else "--:--:--"}",
			"${ChatColor.DARK_GRAY}--------------------",
			"${ChatColor.GOLD}Nether:${ChatColor.RESET} ${boolToString(Month.NETHER.isActivated(Month.currentMonth))}",
			"${ChatColor.GOLD}PvP:${ChatColor.RESET} ${boolToString(Month.PVP.isActivated(Month.currentMonth))}",
			"${ChatColor.GOLD}Assauts:${ChatColor.RESET} ${boolToString(Month.ASSAULTS.isActivated(Month.currentMonth))}",
			"${ChatColor.GOLD}End:${ChatColor.RESET} ${boolToString(Month.END.isActivated(Month.currentMonth))}",
			"${ChatColor.DARK_GRAY}--------------------",
			"${ChatColor.GOLD}Kills:${ChatColor.GRAY} ${board.player.getStatistic(Statistic.PLAYER_KILLS)}",
			"${ChatColor.GOLD}Morts:${ChatColor.GRAY} ${board.player.getStatistic(Statistic.DEATHS)}",
			"${ChatColor.DARK_GRAY}--------------------",
			"${ChatColor.GOLD}${ChatColor.BOLD}map.lescoupains.tk",
		)
		return board
	}

	private fun getBoard(player: Player) = this.board[player.uniqueId] ?: generateBoard(player)

}