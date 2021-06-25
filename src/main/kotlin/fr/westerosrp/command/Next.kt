package fr.westerosrp.command

import fr.westerosrp.game.Month
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class Next : CommandExecutor, TabCompleter {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (args.isNotEmpty()) {
			Month.nextMonth(args[0].toInt())
		} else {
			Month.nextMonth()
		}
		return true
	}

	override fun onTabComplete(
		sender: CommandSender,
		command: Command,
		alias: String,
		args: Array<out String>
	): MutableList<String>? {
		return if (args.size == 1) (0..1000).map { it.toString() }.toMutableList() else null
	}
}

