package fr.westerosrp.game

import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.context.DefaultContextKeys
import net.luckperms.api.model.group.Group
import net.luckperms.api.model.group.GroupManager
import net.luckperms.api.node.Node
import net.luckperms.api.node.NodeType
import net.luckperms.api.node.types.ChatMetaNode
import net.luckperms.api.node.types.InheritanceNode
import org.bukkit.ChatColor
import org.bukkit.entity.Player


private operator fun GroupManager.get(s: String): Group? {
    return this.getGroup(s)
}

enum class Team(val humanName: String, val group: Group?, val color: ChatColor) {
    ADMIN("Administrateur", LuckPermsProvider.get().groupManager["admin"], ChatColor.DARK_RED),
    RED("Rouge", LuckPermsProvider.get().groupManager["rouge"], ChatColor.RED),
    GREEN("Vert", LuckPermsProvider.get().groupManager["vert"] ,ChatColor.GREEN),
    BLUE("Bleu", LuckPermsProvider.get().groupManager["bleu"], ChatColor.BLUE);

    companion object {
        fun getPlayerTeam(player: Player): Team? {
            val user = LuckPermsProvider.get().userManager.getUser(player.uniqueId)
            return Team.values().find {
                it.group?.name == user?.getInheritedGroups(user.queryOptions)?.stream()
                ?.sorted { a, b -> b.weight.orElse(0) - a.weight.orElse(0) }
                ?.findFirst()
                ?.orElse(null)?.name
            }
        }
    }

    fun isAdmin(): Boolean {
        return group?.nodes?.contains(Node.builder("westerosrp.admin").build()) == true
    }

    fun isCorrect(): Boolean {
        return group != null
    }

    fun getPrefix(): String {
        return "[${humanName}]"
    }
    fun getPlayerName(player: Player, reset: ChatColor = ChatColor.RESET): String {
        return "${color}${getPrefix()} ${player.name}${reset}"
    }
}

