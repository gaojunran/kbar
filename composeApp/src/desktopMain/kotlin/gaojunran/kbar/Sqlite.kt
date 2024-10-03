package gaojunran.kbar

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction

fun initSqlite() {
    try {
        Class.forName("org.sqlite.JDBC");
    } catch (e: ClassNotFoundException) {
        System.err.println("Could not init JDBC driver - driver not found");
    }

    Database.connect("jdbc:sqlite:../data/items.db", "org.sqlite.JDBC")
}

fun main() {
   initSqlite()
//    transaction {
//        SchemaUtils.create(GeneralMatch)
//        val itemId = GeneralMatch.insert {
//            it[keyword] = "keyword"
//            it[title] = "title"
//            it[desc] = "desc"
//            it[category] = Category.Api.value
//            it[type] = 0
//            it[content] = "content"
//        } get GeneralMatch.id
//        println(itemId)
//    }
    val generalItems = search("ke")
    println(generalItems)
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

fun search(keyword: String): List<GeneralItem> {
    return transaction {
        GeneralMatch.selectAll().where { GeneralMatch.keyword.like("%$keyword%") }.map {
            GeneralItem(
                keyword = it[GeneralMatch.keyword],
                title = it[GeneralMatch.title],
                desc = it[GeneralMatch.desc],
                category = Category.fromTable(it[GeneralMatch.category]),
                action = Action.fromTable(it[GeneralMatch.type], it[GeneralMatch.content])
            )
        }
    }
}

