package gaojunran.kbar

enum class Category(val value: Int, val prefix: String? = null) {
    None(0),
    Normal(1),
//    Eval(2),
    Api(2),
    Find(3)
;

    companion object {
        fun fromTable(value: Int): Category {
            return entries.find { it.value == value } ?: throw IllegalArgumentException("Invalid value: $value")
        }
    }


}