package fr.westerosrp.game

import fr.westerosrp.WesterosRP
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.model.group.Group
import net.luckperms.api.model.group.GroupManager
import net.luckperms.api.node.Node
import org.bukkit.ChatColor
import org.bukkit.entity.Player


private operator fun GroupManager.get(s: String): Group? {
	return this.getGroup(s)
}

enum class Team(val humanName: String, val group: Group?, val color: ChatColor, val mainTerritory: Territory?) {
	ADMIN("Administrateur", LuckPermsProvider.get().groupManager["admin"], ChatColor.DARK_RED, null),
	RED("Rouge", LuckPermsProvider.get().groupManager["rouge"], ChatColor.RED, Territory.RED_TERRITORY),
	GREEN("Vert", LuckPermsProvider.get().groupManager["vert"], ChatColor.GREEN, Territory.GREEN_TERRITORY),
	BLUE("Bleu", LuckPermsProvider.get().groupManager["bleu"], ChatColor.BLUE, Territory.BLUE_TERRITORY);

	fun initialize() {
		if (!this.isCorrect()) {
			WesterosRP.instance.logger.warning("Team ${this.name} group cannot be loaded successfully !")
		}
	}

	companion object {
		fun getPlayerTeam(player: Player, notAdmin: Boolean): Team? {
			val user = LuckPermsProvider.get().userManager.getUser(player.uniqueId)
			return Team.values().find { team ->
				team.group?.name == user?.getInheritedGroups(user.queryOptions)?.stream()
					?.filter { if(notAdmin) !it.nodes.contains(Node.builder("westerosrp.admin").build()) else true }
					?.sorted { a, b -> b.weight.orElse(0) - a.weight.orElse(0) }
					?.findFirst()
					?.orElse(null)?.name
			}
		}

		fun getPlayerTeam(player: Player) = getPlayerTeam(player, false)
	}

	fun isAdmin() = group?.nodes?.contains(Node.builder("westerosrp.admin").build()) == true

	private fun isCorrect() = group != null

	private fun getPrefix() = "[${humanName}]"

	fun getPlayerName(player: Player, reset: ChatColor = ChatColor.RESET) =
		"${color}${getPrefix()} ${player.name}${reset}"


}

