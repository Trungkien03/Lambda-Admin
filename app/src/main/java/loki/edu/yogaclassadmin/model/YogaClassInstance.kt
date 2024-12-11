package loki.edu.yogaclassadmin.model


data class YogaClassInstance(
    val id: String = "",
    val class_id: String = "",
    val title: String = "",
    val description: String = "",
    val instance_date: String = "",
    val instructor_id: String = "",
    val notes: String? = null
)
