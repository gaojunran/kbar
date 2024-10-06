package gaojunran.kbar

import org.jetbrains.exposed.sql.Table

object GeneralMatch : Table("general_match") {
    val id = integer("id").autoIncrement()
    val keyword = text("keyword")
    val category = integer("category")
    val title = text("title")
    val desc = text("desc")
    val type = integer("type")
    val content = text("content")
}

object Settings : Table("kbar_settings") {
    val key = text("key")
    val value = text("value")
}
