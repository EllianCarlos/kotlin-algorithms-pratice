package org.graph.wallandgates

import kotlin.math.min
import kotlin.random.Random

private const val INF = Int.MAX_VALUE;

fun generateLargeRoomGrid(
  rows: Int,
  columns: Int,
  wallProbability: Double = 0.2,
  gateProbability: Double = 0.001
): RoomGrid {
  val grid = Array(rows) { Array(columns) { INF } }

  for (i in 0 until rows) {
    for (j in 0 until columns) {
      val rand = Random.nextDouble()
      grid[i][j] = when {
        rand < gateProbability -> 0        // Gate
        rand < gateProbability + wallProbability -> -1  // Wall
        else -> INF                         // Empty room
      }
    }
  }

  // Guarantee at least one gate
  grid[rows / 2][columns / 2] = 0

  return RoomGrid(grid)
}


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

  fun neighbors(): List<GridItemLocation> =
    listOf(1 to 0, -1 to 0, 0 to 1, 0 to -1).map { (dr, dc) ->
      GridItemLocation(rowIndex + dr, columnIndex + dc)
    }
}

infix fun Pair<Int, Int>.isIn(grid: Array<Array<Int>>): Boolean {
  return this.first in grid.indices && this.second in grid[0].indices
}

infix fun GridItemLocation.isIn(grid: Array<Array<Int>>): Boolean {
  return (this.rowIndex to this.columnIndex) isIn grid
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

  while (locationsToEvaluate.isNotEmpty()) {
    val location = locationsToEvaluate.removeFirst()

    for (neighbor in location.neighbors()) {
      if (neighbor isIn grid && grid[neighbor.rowIndex][neighbor.columnIndex].isPath()) {
        grid[neighbor.rowIndex][neighbor.columnIndex] = grid[location.rowIndex][location.columnIndex] + 1
        locationsToEvaluate.addLast(GridItemLocation(neighbor.rowIndex, neighbor.columnIndex))
      }
    }
  }
}

class RoomGrid(private val grid: Array<Array<Int>>) {
  fun fillDistancesFromGates() {
    val queue = ArrayDeque<GridItemLocation>()
    forEachCell { loc, value ->
      if (value == 0) queue.add(loc)
    }

    while (queue.isNotEmpty()) {
      val current = queue.removeFirst()
      for (neighbor in current.neighbors()) {
        if (neighbor in this && this[neighbor].isPath()) {
          this[neighbor] = this[current] + 1
          queue.add(neighbor)
        }
      }
    }
  }

  fun forEachCell(action: (GridItemLocation, Int) -> Unit) {
    for (row in grid.indices) {
      for (col in grid[0].indices) {
        action(GridItemLocation(row, col), grid[row][col])
      }
    }
  }

  operator fun get(loc: GridItemLocation): Int = grid[loc.rowIndex][loc.columnIndex]
  operator fun set(loc: GridItemLocation, value: Int) {
    grid[loc.rowIndex][loc.columnIndex] = value
  }

  operator fun contains(loc: GridItemLocation): Boolean =
    loc.rowIndex in grid.indices && loc.columnIndex in grid[0].indices

  fun print() {
    for (row in grid) {
      println(row.joinToString(", ") {
        when (it) {
          Int.MAX_VALUE -> "INF"
          else -> it.toString()
        }
      })
    }
  }

}

fun main() {
  val rooms = arrayOf(
      arrayOf(INF, -1, 0, INF),
      arrayOf(INF, INF, INF, -1),
      arrayOf(INF, -1, INF, -1),
      arrayOf(0, -1, INF, INF)
  )

  val roomGrid = RoomGrid(rooms)
  roomGrid.fillDistancesFromGates()
  roomGrid.print()

  val largeRoomGrid = generateLargeRoomGrid(1000, 1000)
  largeRoomGrid.fillDistancesFromGates()
  largeRoomGrid.print()
}