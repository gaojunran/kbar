package gaojunran.kbar

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

fun initSqlite() {
    try {
        Class.forName("org.sqlite.JDBC");
    } catch (e: ClassNotFoundException) {
        System.err.println("Could not init JDBC driver - driver not found");
    }

    Database.connect("jdbc:sqlite:data/items.db", "org.sqlite.JDBC")
}


fun insertSingle(generalItem: GeneralItem) = transaction {
    GeneralMatch.insert {
        it[keyword] = generalItem.keyword
        it[title] = generalItem.title
        it[desc] = generalItem.desc ?: ""
        it[category] = generalItem.category.value
        it[type] = generalItem.action.typeValue
        it[content] = generalItem.action.content
    } get GeneralMatch.id
}

fun insertBatch(generalItems: List<GeneralItem>) = transaction {
    GeneralMatch.batchInsert(generalItems) {
        this[GeneralMatch.keyword] = it.keyword
        this[GeneralMatch.title] = it.title
        this[GeneralMatch.desc] = it.desc ?: ""
        this[GeneralMatch.category] = it.category.value
        this[GeneralMatch.type] = it.action.typeValue
        this[GeneralMatch.content] = it.action.content
    }
}

fun searchDynamic(keyword: String): List<GeneralItem> {
    // Use CustomFunction to support REPLACE of SQLite
    val patternColumn =
        CustomFunction("REPLACE", TextColumnType(), GeneralMatch.keyword, stringParam("{}"), stringParam("%"))
    return transaction {
        GeneralMatch.selectAll().where {
            /*
                To match the following infix method in Exposed:
                infix fun <T : String?> Expression<T>.like(expression: ExpressionWithColumnType<String>): LikeEscapeOp = LikeEscapeOp(this, expression, true, null)
             */
            stringParam(keyword) like patternColumn
        }.limit(30).map {
            /* `replacer` depends on different patterns, so it's mutually different.
               `it[GeneralMatch.keyword]`  :: eval {}
               `keyword`                   :: eval 1+1
               `replacer` should get       :: 1+1
             */
            val prefix = it[GeneralMatch.keyword].replace("{}", "")
            val replacer = keyword.replace(prefix, "")
            GeneralItem(
                keyword = it[GeneralMatch.keyword],
                title = it[GeneralMatch.title],
                desc = it[GeneralMatch.desc],
                category = Category.fromTable(it[GeneralMatch.category]),
                action = Action.fromTable(it[GeneralMatch.type], it[GeneralMatch.content])
            ).toDynamicItem(replacer)
        }
    }
}

fun clearTable() = transaction {
    GeneralMatch.deleteAll()
}

fun getSetting(key: String): String? {
    return transaction {
        Settings.select(Settings.value).where { Settings.key eq key }.map {
            it[Settings.value]
        }.getOrNull(0)
    }
}

fun setSetting(key: String, value: String) = transaction {
    Settings.update({ Settings.key eq key }) {
        it[Settings.value] = value
    }
}
