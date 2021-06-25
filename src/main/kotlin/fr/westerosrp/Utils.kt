package fr.westerosrp

import fr.westerosrp.game.Month
import fr.westerosrp.game.Team
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*


fun updatePlayer(player: Player): Team? {
	return Team.getPlayerTeam(player).also {
		updateTabList(player)
		player.setPlayerListName(it?.getPlayerName(player) ?: player.name)
		player.setDisplayName(it?.getPlayerName(player) ?: player.name)
	}
}

fun updateTabList(player: Player) {
	var s = if (Bukkit.getOnlinePlayers().size > 1) "s" else ""
	player.setPlayerListHeaderFooter(
		"${ChatColor.GRAY}${ChatColor.BOLD}Westeros RP\n",
		"""
            
            ${ChatColor.GRAY}${Bukkit.getOnlinePlayers().size}${ChatColor.GOLD} joueur${s} connecté${s}${ChatColor.RESET}
            ${ChatColor.GOLD}Votre ping: ${ChatColor.GRAY}${player.ping} ms${ChatColor.RESET}
    """.trimIndent()
	)
}

fun timeUntilMidnight(): Long {
	val midnight = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris")).apply {
		this[Calendar.HOUR_OF_DAY] = 0
		this[Calendar.MINUTE] = 0
		this[Calendar.SECOND] = 0
		this[Calendar.MILLISECOND] = 1
		this[Calendar.DAY_OF_YEAR] = this[Calendar.DAY_OF_YEAR] + 1
	}
	return midnight.timeInMillis - Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris")).timeInMillis
}

fun ticksUntilMidnight() = timeUntilMidnight() / 50

fun scheduleMonthRoll(plugin: WesterosRP) {
	Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, {
		scheduleMonthRoll(plugin)
		if (!plugin.running) return@scheduleSyncDelayedTask
		Month.nextMonth()
	}, ticksUntilMidnight())
}

fun boolToString(condition: Boolean) = if (condition) "${ChatColor.GREEN}✔" else "${ChatColor.RED}✖"