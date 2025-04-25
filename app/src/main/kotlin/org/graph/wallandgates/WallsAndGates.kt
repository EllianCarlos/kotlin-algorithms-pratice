package org.graph.wallandgates

import kotlin.math.min

private const val INF = Int.MAX_VALUE;

class GridItemLocation(
  val rowIndex: Int,
  val columnIndex: Int,
) {
  override fun equals(other: Any?): Boolean {
    if (other == null) return false
    if (this === other) return true
    if (other !is GridItemLocation) return false
    if (other.rowIndex != this.rowIndex) return false
    if (other.columnIndex != this.columnIndex) return false
    return true
  }

  override fun hashCode(): Int {
    return 31 * this.rowIndex + this.columnIndex
  }
}

infix fun Pair<Int, Int>.isIn(grid: Array<Array<Int>>): Boolean {
  return this.first in grid.indices && this.second in grid[0].indices
}

fun Int.isPath() = this == INF

fun fillGridWithDistances(grid: Array<Array<Int>>) {
  val locationsToEvaluate  = ArrayDeque<GridItemLocation>()

  for (row in grid.indices) {
    for (column in grid[0].indices) {
      if (grid[row][column] == 0) {
        locationsToEvaluate.add(GridItemLocation(row, column))
      }
    }
  }

  val directions = listOf(1 to 0, -1 to 0, 0 to 1, 0 to -1)

  while (locationsToEvaluate.isNotEmpty()) {
    val location = locationsToEvaluate.removeFirst()

    for ((dx, dy) in directions) {
      val newRow = location.rowIndex + dx
      val newColumn = location.columnIndex + dy

      if ((newRow to newColumn) isIn grid &&  grid[newRow][newColumn].isPath()) {
        grid[newRow][newColumn] = grid[location.rowIndex][location.columnIndex] + 1
        locationsToEvaluate.addLast(GridItemLocation(newRow, newColumn))
      }
    }
  }
}

fun printGrid(grid: Array<Array<Int>>) {
  for (row in grid) {
    println(row.joinToString(", "))
  }
}

fun main() {
  val rooms = arrayOf(
      arrayOf(INF, -1, 0, INF),
      arrayOf(INF, INF, INF, -1),
      arrayOf(INF, -1, INF, -1),
      arrayOf(0, -1, INF, INF)
  )

  printGrid(rooms)
  println()

  fillGridWithDistances(rooms)

  printGrid(rooms)
  println()

  val otherRoom = arrayOf(arrayOf(INF))

  printGrid(otherRoom)
  println()

  fillGridWithDistances(otherRoom)

  printGrid(otherRoom)
  println()
}