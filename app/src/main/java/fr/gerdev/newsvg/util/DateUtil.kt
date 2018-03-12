package fr.gerdev.newsvg.util

import fr.gerdev.newsvg.model.AbstractParser
import org.joda.time.DateTime
import org.joda.time.Period

// ger 07/03/18
class DateUtil {
    companion object {
        fun agoWithinOneWeek(date: DateTime): String {
            val now = DateTime()
            val period = Period(date, now)
            return when {
                period.days > 7 -> AbstractParser.DAY_FORMATTER.print(date)
                period.days > 1 -> "Il y a ${period.days} jours"
                period.days == 1 -> "Hier"
                period.hours > 1 -> "Il y a ${period.hours} heures"
                period.hours == 1 -> "Il y a une heure"
                period.minutes > 1 -> "Il y a ${period.minutes} minutes"
                period.minutes == 1 -> "Il y a une minute"
                period.seconds > 1 -> "Il y a ${period.seconds} secondes"
                else -> "A l'instant"
            }
        }
    }
}