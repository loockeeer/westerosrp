package fr.westerosrp.command

import fr.westerosrp.game.Scoreboard
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class ScoreboardTrigger : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(sender !is Player) {
            sender.sendMessage("${ChatColor.GOLD}Tu n'es pas un joueur !")
            return true
        }

        when(if(args.isEmpty()) null else args[0]) {
            "enable" -> {
                Scoreboard.triggerBoard(sender, true)
            }
            "disable" -> {
                Scoreboard.triggerBoard(sender, false)
            }
            else -> {
                Scoreboard.triggerBoard(sender)
            }
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String>? {
        return if(args.size == 1) {
            mutableListOf("enable", "disable", "")
        } else {
            null
        }
    }

}