package fr.westerosrp.database

import org.ktorm.dsl.isNotNull
import org.ktorm.schema.Table
import org.ktorm.schema.enum
import org.ktorm.schema.varchar

object Territories : Table<Nothing>("territory") {
    val name = varchar("name").isNotNull()
    val humanName = varchar("humanName").isNotNull()
    val type = varchar("")
    val owner = varchar("owner")
}