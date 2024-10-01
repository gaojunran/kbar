package gaojunran.kbar

data class ApiResult(
    val title: List<String>,
    val desc: List<String>,
    val actionURL: List<String>
) {
    fun toGeneralItemList(): List<GeneralItem> {
        return title.zip(desc).zip(actionURL).map {
            GeneralItem(
                title = it.first.first,
                desc = it.first.second,
                action = Action.BrowseUrl(it.second)
            )
        }
    }
}

data class GeneralItem(
    var title: String,
    var desc: String,
    var action: Action,
)


