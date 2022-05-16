fun main() {
    val (a, b, c, n) = readln().split(" ").map { it.toInt() }
    val uppercases = extractFromRange(a, 'A'..'Z')
    val lowercases = extractFromRange(b, 'a'..'z')
    val numbers = extractFromRange(c, '0'..'9')
    val strings = (uppercases + lowercases + numbers).toMutableList()
    val currentSize = strings.joinToString("").length
    if (currentSize < n) {
        strings += extractFromRange(n - currentSize, 'A'..'z')
    }
    do {
        val password: List<String> = strings.shuffled()
        if (valid(password)) {
            println(password.joinToString(""))
            return
        }
    } while (true)
}

fun valid(password: List<String>): Boolean {
    for (i in 1..password.lastIndex) {
        if (password[i - 1] == password[i]) return false
    }
    return true
}

fun extractFromRange(a: Int, charRange: CharRange): List<String> {
    return if (a == 0) emptyList() else List(a) { charRange.random().toString() }
}
