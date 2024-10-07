package gaojunran.kbar

data class ApiResult(
    val title: List<String>,
    val desc: List<String>,
    val actionURL: List<String>
) {
    fun toGeneralItemList(keyword: String): List<GeneralItem> {
//        println("Debug...")
        return title.zip(desc).zip(actionURL).map {
            GeneralItem(
                keyword = keyword,
                title = it.first.first,
                desc = it.first.second,
                action = Action.BrowseUrl(it.second),
            )
        }
    }
}

data class GeneralItem(
    var keyword: String,
    var action: Action = Action.Lambda{},
    var title: String,
    var desc: String? = null,
    var category: Category = Category.None
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


