package fr.westerosrp.listeners

import com.sk89q.worldedit.math.BlockVector2
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import fr.westerosrp.WesterosRP
import fr.westerosrp.game.Territory
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import java.util.*


class PlayerMove : Listener {
	private fun checkPosition(region: ProtectedRegion, loc: Location): Boolean {
		return region.contains(BlockVector2.at(loc.x, loc.z))
	}

	@EventHandler
	fun handle(e: PlayerMoveEvent) {
		if(e.to?.equals(e.from) == null) return
		Territory.values().filter(Territory::isCorrect).forEach {
			val checkTo = checkPosition(it.region!!, e.to!!)
			val checkFrom = checkPosition(it.region!!, e.from)
			if (checkTo && !checkFrom) {
				if (it.entered.containsKey(e.player.uniqueId)) return
				it.entered[e.player.uniqueId] = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris")).timeInMillis
				e.player.sendMessage(it.enter(e.player))
			} else if (!checkTo && checkFrom) {
				if (!it.entered.containsKey(e.player.uniqueId)) return
				e.player.sendMessage(it.leave(e.player))
				it.entered.remove(e.player.uniqueId)
			}
		}
	}
}

