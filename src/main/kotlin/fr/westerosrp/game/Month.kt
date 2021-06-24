package fr.westerosrp.game

import fr.westerosrp.WesterosRP
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

enum class Month(val message: String) {
    NETHER("Le nether est maintenant activé !") {
        private val month = 1
        override fun isMonth(currentMonth: Int): Boolean {
            return currentMonth == month
        }
        override fun isActivated(currentMonth: Int): Boolean {
            return currentMonth >= month
        }
    },
    PVP("Le PvP est maintenant activé !") {
        private val month = 2
        override fun isMonth(currentMonth: Int): Boolean {
            return currentMonth == month
        }
        override fun isActivated(currentMonth: Int): Boolean {
            return currentMonth >= month
        }
    },
    ASSAULTS("Les assauts sont maintenant activés !") {
        private val month = 3
        override fun isMonth(currentMonth: Int): Boolean {
            return currentMonth == month
        }
        override fun isActivated(currentMonth: Int): Boolean {
            return currentMonth >= month
        }
    },
    END("L'end est maintenant activé !") {
        private val month = 5
        override fun isMonth(currentMonth: Int): Boolean {
            return currentMonth == month
        }
        override fun isActivated(currentMonth: Int): Boolean {
            return currentMonth >= month
        }
    },


    TAX("Vos vassaux ont déposé leur taxe") {
        override fun isMonth(currentMonth: Int): Boolean {
            return true
        }
        override fun isActivated(currentMonth: Int): Boolean {
            return true
        }

        override fun messageInhibitor(players: Collection<Player>): Collection<Player> {
            return players
        }
    };

    companion object {
        var currentMonth: Int = 0
        fun nextMonth(month: Int): Int {
            currentMonth = month
            rollMonth()
            return currentMonth
        }

        fun nextMonth(): Int {
            currentMonth++
            rollMonth()
            return currentMonth
        }

        fun rollMonth() {
            Bukkit.broadcastMessage("${ChatColor.GOLD}Fin du mois ${ChatColor.GRAY}${currentMonth-1}${ChatColor.GOLD} !")

            values().filter { it.isMonth(currentMonth) }.forEach { modifier ->
                modifier.messageInhibitor(Bukkit.getOnlinePlayers()).forEach{
                    it.sendMessage("${ChatColor.GOLD}${modifier.message}")
                }
                modifier.execute(currentMonth)
            }

            Bukkit.broadcastMessage("${ChatColor.GOLD}Début du mois ${ChatColor.GRAY}${currentMonth}${ChatColor.GOLD} !")

            Bukkit.getOnlinePlayers().forEach(Scoreboard::updateBoard)
        }
    }

    abstract fun isMonth(currentMonth: Int): Boolean
    abstract fun isActivated(currentMonth: Int): Boolean
    open fun messageInhibitor(players: Collection<Player>): Collection<Player> {
        return players
    }
    open fun execute(currentMonth: Int) {
        return
    }
}