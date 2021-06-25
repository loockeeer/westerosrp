package fr.westerosrp.command

import fr.westerosrp.WesterosRP
import org.bukkit.Bukkit
import org.bukkit.Difficulty
import org.bukkit.GameRule
import org.bukkit.Statistic
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class Start : CommandExecutor, TabCompleter {
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
				setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE, 1 / 3)
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
			player.activePotionEffects.clear()
			player.health = 20.0
			player.exp = 0.0F
			player.level = 0

			player.setStatistic(Statistic.PLAYER_KILLS, 0)
			player.setStatistic(Statistic.DEATHS, 0)

			//player.teleport(Location(overworld, 117.0, 75.0, 14.0))

			player.server.advancementIterator().forEach { advancement ->
				run {
					val progress = player.getAdvancementProgress(advancement)
					progress.awardedCriteria.forEach {
						progress.revokeCriteria(it)
					}
				}
			}

			//player.isOp = false


			//player.gameMode = GameMode.SURVIVAL
		}

		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "scoreboard players set @a SoulsMobs 0")


		WesterosRP.instance.start()
		return true
	}

	override fun onTabComplete(
		sender: CommandSender,
		command: Command,
		alias: String,
		args: Array<out String>
	): MutableList<String>? {
		return null
	}
}

