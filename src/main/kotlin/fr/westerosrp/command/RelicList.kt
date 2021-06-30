package fr.westerosrp.command

import fr.westerosrp.game.Relic
import fr.westerosrp.sendErrorMessage
import fr.westerosrp.sendInfoMessage
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class RelicList : CommandExecutor {
	companion object {
		val inventoryTitle = "${ChatColor.GRAY}${ChatColor.BOLD}> ${ChatColor.GOLD}Liste des reliques"
	}
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender !is Player) {
			sender.sendErrorMessage("Vous devez être un joueur pour faire cela !")
			return true
		}

		val listInventory = Bukkit.createInventory(null, 54, inventoryTitle)
		Relic.values().map {
			listInventory.addItem(it.generateWithID())
		}
		sender.openInventory(listInventory)
		sender.sendInfoMessage("Ouverture du menu des reliques")
		return true
	}
}