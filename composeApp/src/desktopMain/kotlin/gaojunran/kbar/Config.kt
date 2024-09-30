package gaojunran.kbar

import kotlinx.serialization.Serializable



enum class ActionType {
    BROWSE,

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
    val baseUrl: String,
    val parameters: Map<String, String>? = null,
    val title: String,   // pass a template
    val desc: String,    // pass a template
    val allowMultipleItems: Boolean = true,
    // If `allowMultipleItems` is true, all the results guided by JSON Path will be displayed.
    // Otherwise, only the first result will be displayed.
    val auth: String? = null,
    // If `authToken` is provided, the following sentence will be added to the request header:
    // Authorization: <auth>
)

data class ApiResult(
    val title: List<String>,
    val desc: List<String>
)
