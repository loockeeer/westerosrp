package fr.westerosrp.listeners

import fr.westerosrp.Utils
import fr.westerosrp.WesterosRP
import net.luckperms.api.LuckPerms
import net.luckperms.api.event.EventBus
import net.luckperms.api.event.node.NodeAddEvent
import net.luckperms.api.event.node.NodeRemoveEvent
import net.luckperms.api.model.user.User
import net.luckperms.api.node.types.InheritanceNode
import net.luckperms.api.node.types.PermissionNode
import net.luckperms.api.node.types.PrefixNode
import net.luckperms.api.node.types.SuffixNode
import org.bukkit.entity.Player


data class LuckPermsListener(val plugin: WesterosRP, val luckPerms: LuckPerms) {
    fun register() {
        val eventBus: EventBus = luckPerms.eventBus
        eventBus.subscribe(plugin, NodeAddEvent::class.java, this::onNodeAdd)
        eventBus.subscribe(plugin, NodeRemoveEvent::class.java, this::onNodeRemove)
    }

    fun onNodeAdd(e: NodeAddEvent) {
        if(!e.isUser) return
        val target = e.target as User

        plugin.server.scheduler.runTask(plugin, Runnable {
            val player: Player = plugin.server.getPlayer(target.uniqueId)
                ?: return@Runnable
            Utils.updatePlayer(player)
        })
    }

    fun onNodeRemove(e: NodeRemoveEvent) {
        if(!e.isUser) return
        val target = e.target as User

        plugin.server.scheduler.runTask(plugin, Runnable {
            val player: Player = plugin.server.getPlayer(target.uniqueId)
                ?: return@Runnable
            Utils.updatePlayer(player)
        })
    }
}