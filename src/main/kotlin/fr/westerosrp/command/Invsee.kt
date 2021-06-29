package fr.westerosrp.command

import fr.westerosrp.sendErrorMessage
import fr.westerosrp.sendInfoMessage
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
			sender.sendErrorMessage("Vous devez être un joueur pour faire cela !")
			return true
		}

		if (args.isEmpty()) {
			sender.sendErrorMessage("Un argument est requis : ${ChatColor.GRAY}joueur")
			return true
		}

		val target = Bukkit.getPlayer(args[0])
		if(target == null) {
			sender.sendErrorMessage("L'argument ${ChatColor.GRAY}joueur${ChatColor.RED} est incorrect")
			return true
		}

		if (target.uniqueId == sender.uniqueId) {
			sender.sendErrorMessage("Vous ne pouvez pas faire cette commande sur vous même !")
			return true
		}

		sender.openInventory(target.inventory)
		sender.sendInfoMessage("Ouverture de l'inventaire de ${target.displayName}")
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