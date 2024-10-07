package gaojunran.kbar

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Define keywords and actions to use in the bar.
 */
@Serializable
data class NormalConfig(
    /**
     * The keyword to be searched.
     * Moreover, you can include `{}` in `keyword` to enable pattern matching.
     * The value being captured will replace all brackets in `content` while searching.
     */
    val keyword: String,
    /**
     * If `title` is not passed, it will be the same as `keyword`.
     */
    val title: String? = null,
    val desc: String? = null,
    /**
     * See how to pass `type` in [Action].
     */
    val type: String,
    /**
     * How to work with `content` depends on `type`.
     * For example, if `type` is "browse", then `content` should be a URL.
     * See more in [Action].
     * Including `{}` to enable pattern matching is available. See more in [keyword].
     */
    val content: String,
    /**
     * The order of the item in the bar list. The items are ranked in Descending order and have a default `order` 0.
     * So if you want this item to be nearer to the top, set a positive number.
     */
    val order: Int = 0
){
    fun toGeneralItem(): GeneralItem {
        return GeneralItem(
            keyword = keyword,
            title = title ?: keyword,
            desc = desc,
            action = Action.fromConfig(type, content),
            category = Category.Normal,
            order = order
        )
    }
}


/**
 * Accept configuration to fetch data from APIs and display on the bar.
 * We only allow apis with GET-method request, JSON-format response and Authorization header(optional).
 * Template format: Use `{}` to surround JSON Path.
 * For example, `{$.data.title}` will be replaced with the value of `data.title` in the JSON response.
 * Learn more about JSON Path in https://github.com/json-path/JsonPath.
 */
@Serializable
data class ApiConfig(
    val keyword: String,
    val baseUrl: String,
    val parameters: Map<String, String>? = null,
    val title: String,   // pass a template
    val desc: String? = null,    // pass a template
    val actionURL: String? = null,  // pass a template, when the item is clicked, it'll be opened.
    val allowMultipleItems: Boolean = true,
    // If `allowMultipleItems` is true, all the results guided by JSON Path will be displayed.
    // Otherwise, only the first result will be displayed.
    val auth: String? = null,
    // If `authToken` is provided, the following sentence will be added to the request header:
    // Authorization: <auth>
)


/**
 * Accept configuration for global-scope hotkeys whether the application is shown or not.
 */
@Serializable
data class HotkeyConfig(
    /**
     * The field `key` should have the following syntax:
     *
     *     <modifiers>* (<typedID> | <pressedReleasedID>)
     *
     *     modifiers := shift | control | ctrl | meta | alt | altGraph
     *     typedID := typed <typedKey>
     *     typedKey := string of length 1 giving Unicode character.
     *     pressedReleasedID := (pressed | released) key
     *     key := KeyEvent key code name, i.e. the name following "VK_".
     *
     * Internally the function `getKeyStroke` is used to parse the string you provide.
     * See more information in https://docs.oracle.com/javase/8/docs/api/javax/swing/KeyStroke.html.
     */
    val key: String,
    /**
     * The `title` field is never shown in the application.
     * Instead, it's only necessary when you need to explicitly explain what the hotkey is meant for.
     */
    val title: String? = null,
    /**
     * See how to pass `type` and `content` in [NormalConfig].
     */
    val type: String,
    val content: String
)

inline fun <reified T> loadConfigList(path: String): List<T> {
    val jsonString = Files.readString(Paths.get(path))
    return Json.decodeFromString(jsonString)
}



