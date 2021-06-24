package fr.westerosrp.listeners

import com.sk89q.worldguard.protection.regions.ProtectedRegion
import fr.westerosrp.game.Territory
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import java.util.*

class PlayerMove : Listener {
    private fun checkPosition(region: ProtectedRegion, loc: Location): Boolean {
        return region.contains(loc.x.toInt(), loc.y.toInt(), loc.z.toInt())
    }

    @EventHandler
    fun handle(e: PlayerMoveEvent) {
        Territory.values().filter(Territory::isCorrect).forEach {
            if(e.to?.let { to -> checkPosition(it.region!!, to) } == true) {
                if(it.entered.contains(e.player.uniqueId)) return
                it.entered[e.player.uniqueId] = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris")).timeInMillis
                e.player.sendMessage(it.enter(e.player))
            } else if(checkPosition(it.region!!, e.from)) {
                if(!it.entered.contains(e.player.uniqueId)) return
                e.player.sendMessage(it.leave(e.player))
                it.entered.remove(e.player.uniqueId)
            }
        }
    }
}