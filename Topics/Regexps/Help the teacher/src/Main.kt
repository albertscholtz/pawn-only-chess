fun main() {
    val regex = "[Kotlin][7-9]".toRegex()
    println("Kotlin8".matches(regex))
}