package loki.edu.yogaclassadmin.model

// đại diện cho thông tin của một lớp học yoga.
data class YogaClass(
    val id: String = "",
    val title: String = "",
    val description: String? = null,
    val date: String = "",
    val image_url: String? = null,
    val time: String = "",
    val capacity: Int = 0,
    val class_type_id: String = "",
    val price: Double = 0.0,

)
