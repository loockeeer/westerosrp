package fr.westerosrp.game

import fr.westerosrp.WesterosRP
import fr.westerosrp.utils.Scoreboard
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

enum class Month(val message: String) {
	NETHER("Le nether est maintenant activé !") {
		private val month = 1
		override fun isMonth(currentMonth: Int) = currentMonth == month


		override fun isActivated(currentMonth: Int) = currentMonth >= month

	},
	PVP("Le PvP est maintenant activé !") {
		private val month = 2
		override fun isMonth(currentMonth: Int) = currentMonth == month


		override fun isActivated(currentMonth: Int) = currentMonth >= month

	},
	ASSAULTS("Les assauts sont maintenant activés !") {
		private val month = 3
		override fun isMonth(currentMonth: Int) = currentMonth == month

		override fun isActivated(currentMonth: Int) = currentMonth >= month

	},
	END("L'end est maintenant activé !") {
		private val month = 5
		override fun isMonth(currentMonth: Int) = currentMonth == month

		override fun isActivated(currentMonth: Int) = currentMonth >= month
	},


	TAX("Vos vassaux ont déposé leur taxe") {
		override fun isMonth(currentMonth: Int) = true

		override fun isActivated(currentMonth: Int) = true

		override fun messageInhibitor(players: Collection<Player>) = players

		override fun execute(currentMonth: Int) = Territory.values().forEach(Territory::spendSalary)
	};

	companion object {
		var currentMonth: Int = WesterosRP.instance.config.getInt("month")
		fun nextMonth(month: Int): Int {
			currentMonth = month
			rollMonth()
			save()
			return currentMonth
		}

		fun nextMonth(): Int {
			currentMonth++
			rollMonth()
			save()
			return currentMonth
		}

		fun save() {
			WesterosRP.instance.config.set("month", currentMonth)
			WesterosRP.instance.saveConfig()
		}

		fun rollMonth() {
			Bukkit.broadcastMessage("${WesterosRP.prefix}${ChatColor.GOLD} Fin du mois ${ChatColor.GRAY}${currentMonth - 1}${ChatColor.GOLD} !")

			values().filter { it.isMonth(currentMonth) }.forEach { modifier ->
				modifier.messageInhibitor(Bukkit.getOnlinePlayers()).forEach {
					it.sendMessage("${WesterosRP.prefix}${ChatColor.GOLD} ${modifier.message}")
				}
				modifier.execute(currentMonth)
			}

			Bukkit.broadcastMessage("${WesterosRP.prefix}${ChatColor.GOLD} Début du mois ${ChatColor.GRAY}${currentMonth}${ChatColor.GOLD} !")

			Bukkit.getOnlinePlayers().forEach(Scoreboard::updateBoard)
		}
	}

	abstract fun isMonth(currentMonth: Int): Boolean
	abstract fun isActivated(currentMonth: Int): Boolean
	open fun messageInhibitor(players: Collection<Player>) = players

	open fun execute(currentMonth: Int) {
		return
	}
}