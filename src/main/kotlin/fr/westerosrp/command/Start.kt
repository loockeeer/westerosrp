package fr.westerosrp.command

import fr.westerosrp.WesterosRP
import fr.westerosrp.game.Month
import org.bukkit.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class Start : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        sender.server.worlds.forEach {
            it.apply {
                difficulty = Difficulty.HARD
                setGameRule(GameRule.KEEP_INVENTORY, false)
                setGameRule(GameRule.NATURAL_REGENERATION, false)
                setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true)
                setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, false)
                setGameRule(GameRule.DO_MOB_SPAWNING, true)
                setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, true)
                setGameRule(GameRule.RANDOM_TICK_SPEED, 3)
                setGameRule(GameRule.MOB_GRIEFING, false)
                setGameRule(GameRule.DO_FIRE_TICK, false)
                setGameRule(GameRule.SHOW_DEATH_MESSAGES, false)
                setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE, 1/3)
                setGameRule(GameRule.DO_INSOMNIA, true)
                setGameRule(GameRule.FALL_DAMAGE, true)
                setGameRule(GameRule.FREEZE_DAMAGE, true)
                setGameRule(GameRule.LOG_ADMIN_COMMANDS, false)
                it.time = 1000
            }
        }

        val overworld = sender.server.worlds.find { it.name == "world" }

        Bukkit.getOnlinePlayers().forEach { player ->
            player.inventory.clear()
            player.activePotionEffects.removeAll(player.activePotionEffects)
            player.health = 20.0
            player.saturation = 20.0F
            player.exp = 0.0F
            player.level = 0

            player.teleport(Location(overworld, 117.0, 75.0, 14.0))

            player.server.advancementIterator().forEach { advancement ->
                run {
                    val progress = player.getAdvancementProgress(advancement)
                    progress.awardedCriteria.forEach {
                        progress.revokeCriteria(it)
                    }
                }
            }

            player.isOp = false


            player.gameMode = GameMode.SURVIVAL
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "scoreboard players set @a SoulsMobs 0")


        WesterosRP.instance.start()
        return true
    }
}

