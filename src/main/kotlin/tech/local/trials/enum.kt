package tech.local.trials

open class EnumCompanion<T, V>(private val valueMap: Map<T, V>) {
    operator fun invoke(type: T) = valueMap[type]
}

enum class Environment(val code: String) {
    Prototype("prototype"),
    Development("dev"),
    Testing("test"),
    Production("prod");

    companion object : EnumCompanion<String, Environment>(values().associateBy(Environment::code))
}


fun main() {
    println(Environment("dev"))
}