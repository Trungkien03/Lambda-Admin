package loki.edu.yogaclassadmin.model

// quản lý các loại lớp học riêng biệt
data class ClassType(
    val id: String = "",
    val name: String = "",
    val image: String ="",
    val description: String? = null,
    val benefits: List<String> = emptyList<String>()
)
