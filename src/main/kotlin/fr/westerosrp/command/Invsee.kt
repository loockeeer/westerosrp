package fr.westerosrp.command

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class Invsee : CommandExecutor, TabCompleter {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender !is Player) {
			sender.sendMessage("${ChatColor.GOLD}Tu n'es pas un joueur !")
			return true
		}

		if (!sender.hasPermission("westerosrp.admin")) {
			sender.sendMessage("${ChatColor.RED}Tu n'as pas la permission nécessaire pour utiliser cette commande !")
			return true
		}

		if (args.isEmpty()) return false

		val target = Bukkit.getPlayer(args[0]) ?: return false

		if (target.uniqueId == sender.uniqueId) {
			sender.sendMessage("${ChatColor.RED}Vous ne pouvez pas faire cette commande sur vous même !")
			return true
		}

		sender.openInventory(target.inventory)

		return true
	}

	override fun onTabComplete(
		sender: CommandSender,
		command: Command,
		alias: String,
		args: Array<out String>
	): MutableList<String>? {
		return if (args.size == 1) {
			Bukkit.getOnlinePlayers().stream().map { it.name }.toList()
		} else {
			null
		}
	}
}