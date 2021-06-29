package fr.westerosrp.listeners


import com.sk89q.worldedit.math.BlockVector2
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import fr.westerosrp.currentTime

import fr.westerosrp.game.Territory
import fr.westerosrp.sendActionBar
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Location
import org.bukkit.block.Biome
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import java.util.*


class PlayerMove : Listener {
	private fun checkPosition(region: ProtectedRegion, loc: Location): Boolean {
		return region.contains(BlockVector2.at(loc.x, loc.z))
	}

	@EventHandler
	fun playerMove(e: PlayerMoveEvent) {
		if (e.to?.x == e.from.x && e.to?.y == e.from.y) return

		Territory.values().filter(Territory::isCorrect).forEach {
			val checkTo = checkPosition(it.region!!, e.to!!)
			val checkFrom = checkPosition(it.region!!, e.from)
			if (checkTo && !checkFrom) {
				if (it.entered.containsKey(e.player.uniqueId)) return
				it.entered[e.player.uniqueId] = currentTime
				sendActionBar(e.player, TextComponent(it.enter(e)))
			} else if (!checkTo && checkFrom) {
				if (!it.entered.containsKey(e.player.uniqueId)) return
				sendActionBar(e.player, TextComponent(it.leave(e)))
			}
		}
	}
}

