package chess

enum class Color(val alias: String, val winner: String) {
    BLACK("B", "Black"), WHITE("W", "White"), NONE(" ", "");

    fun opposite(): Color {
        return if (this == BLACK) WHITE else BLACK
    }
}

enum class Direction {
    UP, DOWN, BOTH
}

open class Piece(open val r: Int, open val c: Char, val color: Color, val direction: Direction) {

    fun movesUp(): Boolean {
        return this.direction == Direction.UP || this.direction == Direction.BOTH
    }

    fun movesDown(): Boolean {
        return this.direction == Direction.DOWN || this.direction == Direction.BOTH
    }

    fun isWhite(): Boolean {
        return color == Color.WHITE
    }
}

data class WhitePiece(override val r: Int, override val c: Char) : Piece(r, c, Color.WHITE, Direction.UP)
data class BlackPiece(override val r: Int, override val c: Char) : Piece(r, c, Color.BLACK, Direction.DOWN)

object Board {

    private val whites = mutableListOf(
        WhitePiece(2, 'a'),
        WhitePiece(2, 'b'),
        WhitePiece(2, 'c'),
        WhitePiece(2, 'd'),
        WhitePiece(2, 'e'),
        WhitePiece(2, 'f'),
        WhitePiece(2, 'g'),
        WhitePiece(2, 'h')
    )

    private val blacks = mutableListOf(
        BlackPiece(7, 'a'),
        BlackPiece(7, 'b'),
        BlackPiece(7, 'c'),
        BlackPiece(7, 'd'),
        BlackPiece(7, 'e'),
        BlackPiece(7, 'f'),
        BlackPiece(7, 'g'),
        BlackPiece(7, 'h')
    )

    private val whiteStartingLocations = whites.toList()
    private val blackStartingLocations = blacks.toList()

    private val enPassantPieces = mutableListOf<Piece>()
    private val removeEnPassantPieces = mutableListOf<Piece>()

    fun printGameName() {
        println(" Pawns-Only Chess")
    }

    fun printBoardState() {
        printBorderLine()
        for (r in 8 downTo 1) {
            print("$r |")
            for (c in ('a'..'h')) {
                print(" ${printPawnAtPosition(r, c)} |")
            }
            println()
            printBorderLine()
        }
        printFooterLine()
    }

    private fun printPawnAtPosition(r: Int, c: Char): String {
        val pieceAtPosition = getPieceAtPosition(r, c) ?: return Color.NONE.alias
        return pieceAtPosition.color.alias
    }

    private fun getPieceAtPosition(r: Int, c: Char): Piece? {
        val predicate: (Piece) -> Boolean = { it.r == r && it.c == c }
        return whites.find(predicate) ?: blacks.find(predicate)
    }

    private fun printBorderLine() {
        println("  +---+---+---+---+---+---+---+---+")
    }

    private fun printFooterLine() {
        println("    a   b   c   d   e   f   g   h")
        println()
    }

    fun inputWithMessage(message: String): String {
        println(message)
        return readln()
    }

    private fun inputWithMessageAndRegex(message: String, regex: String, error: String = ""): String {
        while (true) {
            println(message)
            val input = readln()
            val matches = input.matches(regex.toRegex())
            if (matches) return input else println(error)
        }
    }

    fun continueGame(player1: String, player2: String) {
        var currentPlayer = player1
        var currentColor = Color.WHITE

        while (true) {
            val move =
                inputWithMessageAndRegex("$currentPlayer's turn:", "[a-h][1-8][a-h][1-8]|exit", "Invalid Input")
            if (move == "exit") {
                println("Bye!")
                return
            }
            val origin = move.substring(0, 2)
            val originColumn = origin[0]
            val originRow = origin[1].toString().toInt()

            val destination = move.substring(2)
            val destinationColumn = destination[0]
            val destinationRow = destination[1].toString().toInt()

            if (!validOrigin(currentColor, originColumn, originRow)) {
                println("No ${currentColor.name.lowercase()} pawn at $originColumn$originRow")
                continue
            }

            val piece = getPieceAtPosition(originRow, originColumn)
            if (piece != null) {
                if (!validDestination(destinationColumn, destinationRow, piece)) {
                    println("Invalid Input")
                    continue
                }
                removeEnPassantPieces.clear()
                removeEnPassantPieces.addAll(enPassantPieces)

                movePiece(piece, destinationColumn, destinationRow)

                enPassantPieces.removeAll(removeEnPassantPieces)
            }

            printBoardState()

            if (gameOver()) {
                println("${currentColor.winner} Wins!")
                println("Bye!")
                println("")
                return
            }

            if (stalemate()) {
                println("Stalemate!")
                println("Bye!")
                return
            }

            currentPlayer = if (currentPlayer == player1) player2 else player1
            currentColor = currentColor.opposite()
        }

    }

    private fun stalemate(): Boolean {
        return whites.all { determinePossibleMoves(it).isEmpty() }
                || blacks.all { determinePossibleMoves(it).isEmpty() }
    }

    private fun gameOver(): Boolean {
        if (whites.isEmpty() || blacks.isEmpty()) return true
        if (whites.any { it.r == 8 }) return true
        if (blacks.any { it.r == 1 }) return true

        return false
    }

    private fun movePiece(piece: Piece, destinationColumn: Char, destinationRow: Int) {
        if (piece.isWhite()) {
            whites.remove(piece)
            val whitePiece = WhitePiece(destinationRow, destinationColumn)
            if (pieceAtStartingPosition(piece) && (destinationRow == piece.r + 2 || destinationRow == piece.r - 2)) enPassantPieces.add(
                whitePiece
            )

            val targetPiece = getPieceAtPosition(whitePiece.r, whitePiece.c)
            if (targetPiece?.color?.opposite() == whitePiece.color) blacks.remove(targetPiece)

            val enPassantPiece = enPassantPieces.find { it.r == whitePiece.r - 1 && it.c == whitePiece.c }
            if (enPassantPiece != null) {
                blacks.remove(enPassantPiece)
            }
            whites.add(whitePiece)
        } else {
            blacks.remove(piece)
            val blackPiece = BlackPiece(destinationRow, destinationColumn)

            if (pieceAtStartingPosition(piece) && (destinationRow == piece.r + 2 || destinationRow == piece.r - 2)) enPassantPieces.add(
                blackPiece
            )
            val targetPiece = getPieceAtPosition(blackPiece.r, blackPiece.c)
            if (targetPiece?.color?.opposite() == blackPiece.color) whites.remove(targetPiece)

            val enPassantPiece = enPassantPieces.find { it.r == blackPiece.r + 1 && it.c == blackPiece.c }
            if (enPassantPiece != null) {
                whites.remove(enPassantPiece)
            }
            blacks.add(blackPiece)
        }
    }

    private fun validDestination(destinationColumn: Char, destinationRow: Int, piece: Piece): Boolean {
        val possibleMoves = determinePossibleMoves(piece)
        val predicate: (Pair<Char, Int>) -> Boolean =
            { it.first == destinationColumn && it.second == destinationRow }
        return possibleMoves.toMutableList().any(predicate)
    }

    private fun determinePossibleMoves(piece: Piece): List<Pair<Char, Int>> {
        val moves = mutableListOf<Pair<Char, Int>>()

        if (piece.movesUp()) {
            if (pieceAtStartingPosition(piece)) {
                if (spaceEmpty(piece.c, piece.r + 2) && spaceEmpty(piece.c, piece.r + 1)) {
                    moves.add(Pair(piece.c, piece.r + 2))
                }
            }
            if (spaceEmpty(piece.c, piece.r + 1)) {
                moves.add(Pair(piece.c, piece.r + 1))
            }
            if (pieceCanBeCaptured(piece, piece.c - 1, piece.r + 1)) moves.add(Pair(piece.c - 1, piece.r + 1))
            if (pieceCanBeCaptured(piece, piece.c + 1, piece.r + 1)) moves.add(Pair(piece.c + 1, piece.r + 1))
            if (pieceCanUseEnPassant(piece, piece.c - 1, piece.r)) moves.add(Pair(piece.c - 1, piece.r + 1))
            if (pieceCanUseEnPassant(piece, piece.c + 1, piece.r)) moves.add(Pair(piece.c + 1, piece.r + 1))
        } else if (piece.movesDown()) {
            if (pieceAtStartingPosition(piece)) {
                if (spaceEmpty(piece.c, piece.r - 2) && spaceEmpty(piece.c, piece.r - 1)) {
                    moves.add(Pair(piece.c, piece.r - 2))
                }
            }
            if (spaceEmpty(piece.c, piece.r - 1)) {
                moves.add(Pair(piece.c, piece.r - 1))
            }
            if (pieceCanBeCaptured(piece, piece.c - 1, piece.r - 1)) moves.add(Pair(piece.c - 1, piece.r - 1))
            if (pieceCanBeCaptured(piece, piece.c + 1, piece.r - 1)) moves.add(Pair(piece.c + 1, piece.r - 1))
            if (pieceCanUseEnPassant(piece, piece.c - 1, piece.r)) moves.add(Pair(piece.c - 1, piece.r - 1))
            if (pieceCanUseEnPassant(piece, piece.c + 1, piece.r)) moves.add(Pair(piece.c + 1, piece.r - 1))
        }

        return moves.toList()
    }

    private fun pieceCanUseEnPassant(piece: Piece, c: Char, r: Int): Boolean {
        return enPassantPieces
            .filter { it.r == r && it.c == c }
            .any { it.color == piece.color.opposite() }
    }

    private fun pieceCanBeCaptured(piece: Piece, c: Char, r: Int): Boolean {
        val pieceAtPosition: Piece? = getPieceAtPosition(r, c)
        return if (pieceAtPosition == null) {
            false
        } else {
            pieceAtPosition.color == piece.color.opposite()
        }
    }

    private fun spaceEmpty(c: Char, r: Int): Boolean {
        return if (getPieceAtPosition(r, c) == null) return true else false
    }

    private fun pieceAtStartingPosition(piece: Piece): Boolean {
        val predicate: (Piece) -> Boolean = { it.r == piece.r && it.c == piece.c }
        return if (piece.isWhite()) whiteStartingLocations.any(predicate) else blackStartingLocations.any(predicate)
    }

    private fun validOrigin(color: Color, c: Char, r: Int): Boolean {
        return getPieceAtPosition(r, c)?.color == color
    }
}

fun main() {
    Board.printGameName()
    val player1 = Board.inputWithMessage("First Player's name:")
    val player2 = Board.inputWithMessage("Second Player's name:")
    Board.printBoardState()

    Board.continueGame(player1, player2)
}