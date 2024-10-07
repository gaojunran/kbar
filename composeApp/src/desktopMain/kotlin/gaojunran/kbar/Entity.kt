package gaojunran.kbar

fun <A, B, C> zipLongest(list1: List<A>, list2: List<B>, list3: List<C>): List<Triple<A?, B?, C?>> {
    val maxLength = maxOf(list1.size, list2.size, list3.size)
    return List(maxLength) { index ->
        Triple(list1.getOrNull(index), list2.getOrNull(index), list3.getOrNull(index))
    }
}

data class ApiResult(
    val title: List<String>,
    val desc: List<String>,
    val actionURL: List<String>
) {
    fun toGeneralItemList(keyword: String): List<GeneralItem> {
        return zipLongest(title, desc, actionURL).map { it ->
            GeneralItem(
                keyword = keyword,
                title = it.first!!,
                desc = it.second,
                action = it.third ?.let { Action.BrowseUrl(it) } ?: Action.Lambda{},
            )
        }
    }
}

data class GeneralItem(
    var keyword: String,
    var action: Action = Action.Lambda{},
    var title: String,
    var desc: String? = null,
    var category: Category = Category.None,
    var order: Int = 0
){
    constructor(keyword: String, action: Action):
            this(keyword = keyword, action = action, title = keyword, desc = "")

    constructor(keyword: String, action: Action, desc: String):
            this(keyword = keyword, action = action, title = keyword, desc = desc)

    fun toDynamicItem(replacer: String): GeneralItem {
        return GeneralItem(
            keyword = keyword.replace("{}", replacer),
            title = title.replace("{}", replacer),
            desc = desc?.replace("{}", replacer),
            action = action.toDynamicAction(replacer),
            category = category
        )
    }
}


