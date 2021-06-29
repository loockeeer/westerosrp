package fr.westerosrp.command

import fr.westerosrp.sendErrorMessage
import fr.westerosrp.sendInfoMessage
import fr.westerosrp.utils.Scoreboard
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class ScoreboardTrigger : CommandExecutor, TabCompleter {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender !is Player) {
			sender.sendErrorMessage("Vous devez être un joueur pour faire cela !")
			return true
		}

		if(args.isEmpty()) {
			sender.sendErrorMessage("Un argument est requis : ${ChatColor.GRAY}enable${ChatColor.RED}, ${ChatColor.GRAY}disable")
			return true
		}

		Scoreboard.triggerBoard(sender, args[0] == "enable")
		sender.sendInfoMessage("Votre scoreboard a été basculé sur ${ChatColor.GRAY}${if(args[0] == "enable") "activé" else "désactivé"}")
		return true
	}

	override fun onTabComplete(
		sender: CommandSender,
		command: Command,
		alias: String,
		args: Array<out String>
	): MutableList<String>? {
		return if (args.size == 1) {
			mutableListOf("enable", "disable")
		} else {
			null
		}
	}

}