package fr.westerosrp.database

import org.ktorm.dsl.isNotNull
import org.ktorm.schema.Table
import org.ktorm.schema.varchar

object RelationShips : Table<Nothing>("relation_ship") {
    val from = varchar("from").isNotNull()
    val to = varchar("to").isNotNull()
    val type = varchar("type").isNotNull()
}