fun summator(map: Map<Int, Int>): Int {
    return map.entries.sumOf { entry -> if (entry.key % 2 == 0) entry.value else 0 }
}

fun main() {
    println(summator(mapOf(100 to 10, 55 to 3, 112 to 5)))
}