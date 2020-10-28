class BuildTypeConfig(
    val name: String,
    val minifyEnabled: Boolean
)

sealed class BuildConfigField {
    abstract val type: String
    abstract val name: String
    abstract val value: Any
    abstract val formattedValue: String

    data class StringType(
        override val name: String,
        override val value: String
    ) : BuildConfigField() {
        override val type: String
            get() = "String"
        override val formattedValue: String
            get() = "\"$value\""
    }
}