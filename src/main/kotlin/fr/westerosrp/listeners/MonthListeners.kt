package fr.westerosrp.listeners

import fr.westerosrp.checkPosition
import fr.westerosrp.game.Month
import fr.westerosrp.game.Team
import fr.westerosrp.game.Territory
import fr.westerosrp.sendCoolErrorMessage
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerPortalEvent
import org.bukkit.event.world.PortalCreateEvent

class MonthListeners : Listener {
	@EventHandler
	fun portalCreate(e: PortalCreateEvent) {
		if(e.entity?.type != EntityType.PLAYER) return
		if((e.reason == PortalCreateEvent.CreateReason.FIRE || e.reason == PortalCreateEvent.CreateReason.NETHER_PAIR)
					&& Month.NETHER.isActivated(Month.currentMonth)) return
		if(e.reason == PortalCreateEvent.CreateReason.END_PLATFORM && Month.END.isActivated(Month.currentMonth)) return
		if(
			Territory.values().none { territory ->
				e.blocks.all {checkPosition(territory.region!!, it.location)} && territory.isOwner(e.entity as Player)
			} && Team.getPlayerTeam(e.entity as Player)?.isAdmin() != true
		) {
			e.isCancelled = true
			(e.entity as Player).sendCoolErrorMessage("Vous ne pouvez pas créer de portail ici !")
		}
	}

	@EventHandler
	fun portalTeleport(e: PlayerPortalEvent) {
		if(e.to?.world?.name == "world_nether" && Month.NETHER.isActivated(Month.currentMonth)) return
		if(e.to?.world?.name == "world_nether" && Month.END.isActivated(Month.currentMonth)) return
		e.isCancelled = true
		e.canCreatePortal = false
	}

	@EventHandler
	fun playerDamage(e: EntityDamageByEntityEvent) {
		if(e.entity.type != EntityType.PLAYER
			|| e.damager.type != EntityType.PLAYER
		) return
		if(Month.PVP.isActivated(Month.currentMonth)) return
		e.isCancelled = true
		(e.damager as Player).sendCoolErrorMessage("Le PvP est désactivé !")
	}
}