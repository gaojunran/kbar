package gaojunran.kbar

import com.jayway.jsonpath.JsonPath
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Paths


fun loadApiConfig(path: String): List<ApiConfig> {
    val jsonString = Files.readString(Paths.get(path))
    return Json.decodeFromString<List<ApiConfig>>(jsonString)
}

suspend fun fetchData(apiConfig: ApiConfig): ApiResult {
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
    val responseBody = response.bodyAsText()

    client.close()

    val regex = "\\{(.*?)\\}".toRegex()
    val title = mutableListOf<String>()
    val desc = mutableListOf<String>()

    apiConfig.title.let {
        if (!apiConfig.allowMultipleItems) {
            val titleSingle = regex.replace(it) { matchResult ->
                val content = matchResult.groupValues[1]
                JsonPath.read<String>(responseBody, content)
            }
            title.add(titleSingle)
        } else {
            val matchResults = mutableListOf<List<String>>()
            var counter = 0
            val titleTemplate = regex.replace(it) { matchResult ->
                val content = matchResult.groupValues[1]
                matchResults.add(JsonPath.read(responseBody, content))
                counter++
                "{${counter - 1}}"
            }
            matchResults.forEach { item ->
                var currTitle = titleTemplate
                item.forEachIndexed { index, slotItem ->
                    currTitle = titleTemplate.replace("{${index}}", slotItem)
                }
                title.add(currTitle)
            }
        }
    }

    apiConfig.desc.let {
        if (!apiConfig.allowMultipleItems) {
            val descSingle = regex.replace(it) { matchResult ->
                val content = matchResult.groupValues[1]
                JsonPath.read<String>(responseBody, content)
            }
            desc.add(descSingle)
        } else {
            val matchResults = mutableListOf<List<String>>()
            var counter = 0
            val descTemplate = regex.replace(it) { matchResult ->
                val content = matchResult.groupValues[1]
                matchResults.add(JsonPath.read(responseBody, content))
                counter++
                "{${counter - 1}}"
            }
            matchResults.forEach { item ->
                var currDesc = descTemplate
                item.forEachIndexed { index, slotItem ->
                    currDesc = descTemplate.replace("{${index}}", slotItem)
                }
                desc.add(currDesc)
            }
        }
    }
    return ApiResult(title, desc)
}

suspend fun main() {
    val apiConfig = loadApiConfig("/home/nebula/Projects/kbar/config/apiConfig.json")
    apiConfig.forEach {
        println(fetchData(it))
    }
}