package loki.edu.yogaclassadmin.model

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
) {
    override fun toString(): String {
        return "SignInResult(data=$data, errorMessage=$errorMessage)"
    }
}


data class UserData (
    val userId: String,
    val userName:String?,
    val email: String?,
    val profilePictureUrl: String?
)