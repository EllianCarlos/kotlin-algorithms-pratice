package org.graph.orderingsystem

import java.time.Instant
import kotlin.math.pow
import kotlin.math.sqrt

// Tracking orders and assigning deliveries to drivers
// drivers must exist
// deliveries must be requested
// a delivery must be assigned to a driver based on a strategy
// the assignment must follow a set of rules
// - No assigning a delivered delivery to a driver
// - No assigning a delivery to a driver that already has a delivery (assign only pending)
// - Once delivered a delivery is done and the driver no longer has a delivery assigned

fun getActualTimestamp() = Instant.now()

// X and Y are defined in meters
data class Position(val x: Int, val y: Int)

infix fun Position.distanceTo(other: Position): Double {
  return sqrt((this.x - other.x.toDouble()).pow(2) + (this.y - other.y.toDouble()).pow(2))
}

// Abstract this to a service in another module (control positions and extra driver data)
data class Driver(
  private val name: String,
  val id: String,
  val meanDeliveryTimeInSeconds: Int,
  val position: Position,
  var lastDeliveryTimestamp: Instant,
)

enum class OrderStatus {
  PENDING, ASSIGNED, DELIVERED, CANCELLED
}

interface OrderI {
  val id: String
  val deliveryTimestamp: Instant?
  val cancelledTimestamp: Instant?
  val createdTimestamp: Instant?
  val assignedTimestamp: Instant?
  val status: OrderStatus
  val deliveryPoint: Position
}

// Could split into multiple classes states like:
// BaseOrder (this class)
// DeliveredOrder, CancelledOrder, AssignedOrder and CreatedOrder extending it.
data class Order(
  override val id: String,
  override var deliveryTimestamp: Instant? = null,
  override var cancelledTimestamp: Instant? = null,
  override var assignedTimestamp: Instant? = null,
  override var createdTimestamp: Instant? = getActualTimestamp(),
  override val deliveryPoint: Position
) : OrderI {
  fun cancel() {
    if (status == OrderStatus.PENDING) {
      cancelledTimestamp = getActualTimestamp()
    }
  }

  fun assigned() {
    if (status == OrderStatus.PENDING) {
      assignedTimestamp = getActualTimestamp()
    }
  }

  override val status: OrderStatus
    get() = when {
      cancelledTimestamp != null -> OrderStatus.CANCELLED
      deliveryTimestamp != null -> OrderStatus.DELIVERED
      assignedTimestamp != null -> OrderStatus.ASSIGNED
      else -> OrderStatus.PENDING
    }
}

enum class OrderAssignmentStrategy {
  NEAREST, BY_WAIT_TIME, BY_EFFIENCY
}

typealias OrderId = String
typealias DriverId = String

class DeliveryService() {
  private val orders = mutableListOf<Order>()
  private val drivers = mutableListOf<Driver>()
  private val freeDrivers = mutableListOf<Driver>()

  // TODO: Implement BIMAP class
  private val ordersToDrivers = mutableMapOf<OrderId, DriverId?>()
  private val driversToOrders = mutableMapOf<DriverId, OrderId?>()

  fun createOrder(id: String, position: Position) {
    // TODO: Create custom throwable error
    if (ordersToDrivers.containsKey(id)) throw IllegalArgumentException("Order with id $id is already created")

    val newOrder = Order(id = id, deliveryPoint = position)
    orders.addLast(newOrder)
  }

  fun assignOrder(order: Order, strategy: OrderAssignmentStrategy = OrderAssignmentStrategy.NEAREST) {
    val MAX_DISTANCE_TO_DELIVERY_POINT = 10;
    val assignedDriver = freeDrivers
      .filter { driver -> driver.position distanceTo order.deliveryPoint <= MAX_DISTANCE_TO_DELIVERY_POINT }
      .sortedWith { driver1, driver2 ->
        return@sortedWith when (strategy) {
          OrderAssignmentStrategy.NEAREST -> order.getDifferenceInDistances(driver1, driver2)
          OrderAssignmentStrategy.BY_WAIT_TIME -> {
            (
                (driver1.lastDeliveryTimestamp.toEpochMilli() - driver2.lastDeliveryTimestamp.toEpochMilli()
                    ) / 1000).toInt()
          }

          OrderAssignmentStrategy.BY_EFFIENCY ->
            -(driver1.meanDeliveryTimeInSeconds - driver2.meanDeliveryTimeInSeconds).toInt()
        }
      }
      .first()

    // Updating the order
    // Abstract this
    ordersToDrivers[order.id] = assignedDriver.id
    driversToOrders[assignedDriver.id] = order.id
    order.assigned()
  }

  private fun Order.getDifferenceInDistances(driver1: Driver, driver2: Driver): Int {
    return (
        100 * (driver1.position distanceTo this.deliveryPoint)).toInt() -
        (100 * (driver2.position distanceTo this.deliveryPoint)
        ).toInt()
  }
}

fun main() {

}

