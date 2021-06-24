package fr.westerosrp.database

import org.ktorm.dsl.isNull
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.uuid
import org.ktorm.schema.varchar

object StoredPlayer : Table<Nothing>("stored_player") {
    val unique_id = uuid("unique_id")
    val pos_x = long("pos_x").isNull()
    val pos_y = long("pos_y").isNull()
    val pos_z = long("pos_z").isNull()
    val pos_world = varchar("pos_world").isNull()
}