package org.graph.findclosestdrivertopoint

import kotlin.math.sqrt

class Point(
    val x: Double,
    val y: Double,
) {
    override fun toString(): String {
        return "Point(x=$x, y=$y)"
    }
}

public infix fun Point.distanceTo(other: Point): Double {
    val xDistance = this.x - other.x
    val yDistance = this.y - other.y
    return sqrt(xDistance * xDistance + yDistance * yDistance)
}

class Driver(
    private val name: String,
    val id: Number,
    val position: Point // Composition over inheritance
);

class DriverFactory(var driversMap: Map<Number, Driver>) {
    fun createDriver(name: String, id: Number, position: Point): Driver {
        if (driversMap.containsKey(id)) throw IllegalArgumentException("Driver with id=$id already exists")
        val newDriver = Driver(name, id, position)
        driversMap = driversMap + (id to newDriver)

        return newDriver
    }
}

fun findClosestDriver(
    point: Point,
    drivers: List<Driver>,
): Driver {
    return drivers.minBy { deliver -> deliver.position distanceTo point }
}

fun main() {
    val driverFactory = DriverFactory(mapOf())
    val driver1 = driverFactory.createDriver("Delivery A", 1, Point(1.0, 2.0))
    val driver2 = driverFactory.createDriver("Delivery B", 2, Point(4.0, 6.0))
    val driver3 = driverFactory.createDriver("Delivery C", 3, Point(-3.0, 0.0))

    println(findClosestDriver(Point(0.0, -3.0), listOf(driver1, driver2, driver3)).id)
}
