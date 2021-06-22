package fr.westerosrp

import fr.westerosrp.game.Month
import fr.westerosrp.game.Team
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*


fun updatePlayer(player: Player) {
    Team.getPlayerTeam(player).run {
        player.setPlayerListHeaderFooter("${ChatColor.GRAY}${ChatColor.BOLD}Westeros RP\n",
            """
            
            ${ChatColor.GOLD}Plugin: ${ChatColor.GRAY}Loockeeer
            ${ChatColor.GOLD}Build: ${ChatColor.GRAY}Lohar_
            ${ChatColor.GOLD}Chômage: ${ChatColor.GRAY}gorec
        """.trimIndent())
        player.setPlayerListName(this?.getPlayerName(player) ?: player.name)
        player.setDisplayName(this?.getPlayerName(player) ?: player.name)
    }
}

fun timeUntilMidnight(): Long {
    val midnight = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"))
    midnight[Calendar.HOUR_OF_DAY] = 0
    midnight[Calendar.MINUTE] = 0
    midnight[Calendar.SECOND] = 0
    midnight[Calendar.MILLISECOND] = 1
    midnight[Calendar.DAY_OF_YEAR] = midnight[Calendar.DAY_OF_YEAR] + 1
    return midnight.timeInMillis - Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris")).timeInMillis
}

fun ticksUntilMidnight(): Long {
    return timeUntilMidnight() / 50
}

fun scheduleMonthRoll(plugin: WesterosRP) {
    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, {
        scheduleMonthRoll(plugin)
        if(!plugin.running) return@scheduleSyncDelayedTask
        Month.nextMonth()
    }, ticksUntilMidnight())
}

fun boolToString(condition: Boolean): String {
    return if (condition) "${ChatColor.GREEN}✔" else "${ChatColor.RED}✖"
}