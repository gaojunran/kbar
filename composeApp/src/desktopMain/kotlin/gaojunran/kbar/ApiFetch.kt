package gaojunran.kbar

import com.jayway.jsonpath.JsonPath
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.request.*
import io.ktor.client.statement.*


fun <T> List<List<T>>.transpose(): List<List<T>> {
    if (isEmpty()) return emptyList()
    val numRows = size
    val numCols = this[0].size
    return List(numCols) { colIndex ->
        List(numRows) { rowIndex ->
            this[rowIndex][colIndex]
        }
    }
}


suspend fun fetchData(apiConfig: ApiConfig): String {
    val client = HttpClient(CIO) {
        install(ContentEncoding) {
            gzip()
        }
    }
    val response: HttpResponse = client.get(apiConfig.baseUrl) {
        apiConfig.parameters?.forEach {
            parameter(it.key, it.value)
        }
        apiConfig.auth?.let { header("Authorization", it) }
    }
    client.close()
    return response.bodyAsText()
}

fun parseTemplate(responseBody: String, template: String?, apiConfig: ApiConfig): List<String> {
    val regex = "\\{(.*?)\\}".toRegex()
    val resultList = mutableListOf<String>()
    template?.let {
        if (!apiConfig.allowMultipleItems) {
            val resultSingle = regex.replace(it) { matchResult ->
                val content = matchResult.groupValues[1]
                JsonPath.read<String>(responseBody, content)
            }
            resultList.add(resultSingle)
        } else {
            val matchResults = mutableListOf<List<String>>()
            var counter = 0
            val resultTemplate = regex.replace(it) { matchResult ->
                val content = matchResult.groupValues[1]
                matchResults.add(JsonPath.read(responseBody, content))
                counter++
                "{${counter - 1}}"
            }
            matchResults.transpose().forEach { item ->  // each item, contains slot values
                var currResult = resultTemplate
                item.forEachIndexed { index, slotItem ->
                    currResult = currResult.replace("{${index}}", slotItem)
                }
                resultList.add(currResult)
            }
        }
    }
    return resultList
}