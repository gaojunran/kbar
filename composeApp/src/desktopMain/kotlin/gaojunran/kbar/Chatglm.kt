//package gaojunran.kbar
//
//import io.ktor.client.*
//import io.ktor.client.engine.cio.*
//import io.ktor.client.plugins.compression.*
//import io.ktor.client.request.*
//import io.ktor.client.statement.*
//
//const val GLM_BASE_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions"
//const val GLM_API_KEY = ""
//
//suspend fun fetchData(question: String, new): String {
//    val client = HttpClient(CIO) {
//        install(ContentEncoding) {
//            gzip()
//        }
//    }
//    val response: HttpResponse = client.post {
//        setBody(mapOf(
//            "model" to "glm-4-flash",
//            "request_id" to
//            ))
//        bearerAuth()
//    }
//    client.close()
//    return response.bodyAsText()
//}