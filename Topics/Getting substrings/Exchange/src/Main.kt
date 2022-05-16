fun main() {
    val read = readln()
    print(read.last() + read.substring(1, read.lastIndex) + read.first())
}