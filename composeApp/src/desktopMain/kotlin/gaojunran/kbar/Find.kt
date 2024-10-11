package gaojunran.kbar

/**
 * Support the ability of searching for local files and directories.
 * Currently, it's powered by `fd` (https://github.com/sharkdp/fd).
 * On any platform, you need to have the binary `fd` installed first.
 */

suspend fun find(query: String): List<GeneralItem> {
    val commandResult = executeCommandOutput("fd $query /")
    return commandResult.split("\n").map {
        GeneralItem(
            keyword = "[file]",
            title = it,
            desc = "File/Dir",
            action = Action.OpenFile(it)
        )
    }
}