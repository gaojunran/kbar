package gaojunran.kbar

enum class Category(val value: Int) {
    None(0),
    Api(1);

    companion object {
        fun fromTable(value: Int): Category {
            return entries.find { it.value == value } ?: throw IllegalArgumentException("Invalid value: $value")
        }
    }


}