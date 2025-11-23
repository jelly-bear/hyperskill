package tictactoe

import tictactoe.Grid.GameState
import kotlin.math.abs

class Grid(state: String?) {

    enum class FieldState(val symbol: Char) {
        X('X'),
        O('O'),
        _('_');
    }

    enum class GameState(val desc: String) {
        IMPOSSIBLE("Impossible"),
        GAME_NOT_FINISHED("Game not finished"),
        X_WINS("X wins"),
        O_WINS("O wins"),
        DRAW("Draw");
    }

    val grid: Array<Array<FieldState>> =
        when {
            state == null -> initGrid { FieldState.`_` }
            state.length != 9 -> throw Exception("State length must be 9!")
            else -> {
                state
                    .map { FieldState.valueOf("$it") }
                    .toMutableList()
                    .let {
                        initGrid { it.removeFirst() }
                    }
            }
        }

    private fun initGrid(lambda: () -> FieldState) =
        Array(3) { Array(3) { lambda() } }

    private fun gridHasFullLineOf(state: FieldState): Boolean {
        Array(grid[0].size) { row ->
            Array(grid.size) { col ->
                grid[col][row]
            }.reversedArray()
        }.let { transposedGrid ->
            val expectedRow = Array(grid.size) { state }
            return grid.any { it.contentEquals(expectedRow) } ||
                    transposedGrid.any { it.contentEquals(expectedRow) } ||
                    Array(grid.size) { i -> grid[i][i] }.contentEquals(expectedRow) ||
                    Array(transposedGrid.size) { i -> transposedGrid[i][i] }.contentEquals(expectedRow)
        }
    }

    fun printGrid() {
        println("---------")
        grid.forEach { row -> println("| ${row.joinToString(" ")} |") }
        println("---------")
    }

    fun getGameState(): GameState {
        return when {
            grid.contentDeepToString().let { grid -> abs(grid.count { it == FieldState.X.symbol } - grid.count { it == FieldState.O.symbol }) > 1 }
                -> GameState.IMPOSSIBLE

            gridHasFullLineOf(FieldState.X) && gridHasFullLineOf(FieldState.O)
                -> GameState.IMPOSSIBLE

            gridHasFullLineOf(FieldState.X)
                -> GameState.X_WINS

            gridHasFullLineOf(FieldState.O)
                -> GameState.O_WINS

            grid.none { it.contains(FieldState.`_`) }
                -> GameState.DRAW

            else
                -> GameState.GAME_NOT_FINISHED
        }
    }

    fun move(x: Int, y: Int, state: FieldState): Boolean {
        return if (grid[x-1][y-1] == FieldState.`_`) {
            grid[x - 1][y - 1] = state
            true
        } else
            false
    }
}

fun main() {
    val grid = Grid(null)
    grid.printGrid()

    val players = listOf(
        Grid.FieldState.X,
        Grid.FieldState.O,
    )

    var move = 0

    while (true) {
        val xy = readln()
        val (x, y) = try {
            xy.split(" ").map { it.toInt() }
        } catch (_: NumberFormatException) {
            println("You should enter numbers!")
            continue
        }

        if (x !in 1..3 || y !in 1..3) {
            println("Coordinates should be from 1 to 3!")
            continue
        }

        if (!grid.move(x, y, players[move % 2])) {
            println("This cell is occupied! Choose another one!")
            continue
        }

        grid.printGrid()

        if (grid.getGameState() != GameState.GAME_NOT_FINISHED)
            break

        move++
    }

    println(grid.getGameState().desc)
}