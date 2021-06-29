package fr.westerosrp

import com.sk89q.worldedit.math.BlockVector2
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import fr.westerosrp.game.Month
import fr.westerosrp.game.Team
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*
import java.util.ArrayList



val currentTime
get() = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris")).timeInMillis

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
	return midnight.timeInMillis - currentTime
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

fun sendActionBar(player: Player, message: BaseComponent) {
	player.spigot().sendMessage(ChatMessageType.ACTION_BAR, message)
}

fun getRandomLocations(loc: Location, radius: Int, chance: Int): List<Location> {
	val range: MutableList<Location> = ArrayList<Location>()
	val w = loc.world
	val rand = Random()
	val cx = loc.blockX
	val cz = loc.blockZ
	val rs = radius * radius
	for (x in cx - radius..cx + radius) {
		for (y in 0..2) {
			for (z in cz - radius..cz + radius) {
				if ((cx - x) * (cx - x) + (cz - z) * (cz - z) <= rs) {
					val randomLoc = Location(w, (x + rand.nextInt(chance)).toDouble(), (y + rand.nextInt(chance)).toDouble(),
						(z + rand.nextInt(chance)).toDouble()
					)
					if (!w?.getBlockAt(randomLoc)?.type?.isSolid!!) {
						range.add(randomLoc)
					}
				}
			}
		}
	}
	return range
}

fun checkPosition(region: ProtectedRegion, loc: Location): Boolean {
	return region.contains(BlockVector2.at(loc.x, loc.z))
}

val errorMessageCooldownHistory = HashMap<UUID, Long>()
val errorMessageCooldown = 5000L

fun CommandSender.sendErrorMessage(message: String) {
	sendMessage("${WesterosRP.prefix}${ChatColor.RED} $message")
}

fun CommandSender.sendCoolErrorMessage(message: String) {
	if(this is Player) {
		if (!errorMessageCooldownHistory.containsKey(uniqueId)) {
			errorMessageCooldownHistory[uniqueId] = currentTime
		} else if ((errorMessageCooldownHistory[uniqueId]?.plus(errorMessageCooldown))!! > currentTime) return
		else errorMessageCooldownHistory.remove(uniqueId)
	}
	sendErrorMessage(message)
}

fun CommandSender.sendInfoMessage(message: String) {
	sendMessage("${WesterosRP.prefix}${ChatColor.GOLD} $message")
}