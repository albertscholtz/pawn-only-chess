fun main() {
    val str = readln()

    val lstOfVowels = listOf('a', 'e', 'y', 'u', 'i', 'o')
    var countVowels = 0
    var countConsonants = 0
    var countInsertions = 0

    for (symbol in str) {
        if (symbol.lowercaseChar() in lstOfVowels) {
            countVowels += 1
            countConsonants = 0
        } else {
            countVowels = 0
            countConsonants += 1
        }

        if (countVowels > 2) {
            countInsertions += 1
            countVowels = 1
        }

        if (countConsonants > 2) {
            countInsertions += 1
            countConsonants = 1
        }
    }

    print(countInsertions)
}