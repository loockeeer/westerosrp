package fr.westerosrp.listeners

import fr.westerosrp.command.RelicList
import fr.westerosrp.game.Relic
import fr.westerosrp.game.Team
import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityCombustEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.ItemDespawnEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerItemBreakEvent
import org.bukkit.persistence.PersistentDataType

class RelicListeners : Listener {
	@EventHandler
	fun inventoryClick(e: InventoryClickEvent) {
		if(e.view.title != RelicList.inventoryTitle)  return
		val player = (if(e.inventory.viewers.isEmpty()) null else e.inventory.viewers[0]) ?: return
		if(Team.getPlayerTeam(player as Player)?.isAdmin() != true) {
			e.isCancelled = true
		}
	}

	@EventHandler
	fun itemBreak(e: PlayerItemBreakEvent) {
		val relicOrdinal = e.brokenItem.itemMeta?.persistentDataContainer?.get(Relic.relicKey, PersistentDataType.INTEGER)
			?: return
		val relic = Relic.values()[relicOrdinal]
		relic.respawn()
	}

	@EventHandler
	fun itemDespawn(e: ItemDespawnEvent) {
		val relicOrdinal = e.entity.itemStack.itemMeta?.persistentDataContainer?.get(Relic.relicKey, PersistentDataType.INTEGER)
			?: return
		val relic = Relic.values()[relicOrdinal]
		relic.respawn()
	}

	@EventHandler
	fun entityCombust(e: EntityCombustEvent) {
		if(e.entity.type != EntityType.DROPPED_ITEM) return
		val relicOrdinal = (e.entity as Item).itemStack.itemMeta?.persistentDataContainer?.get(Relic.relicKey, PersistentDataType.INTEGER)
			?: return
		val relic = Relic.values()[relicOrdinal]
		relic.respawn()
	}
}