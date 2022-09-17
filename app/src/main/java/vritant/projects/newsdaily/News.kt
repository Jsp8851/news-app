package vritant.projects.newsdaily

data class News(
    val title: String,
    val author: String,
    val newsAgency : String,
    val url: String,
    val imageUrl: String,
    val content : String
)