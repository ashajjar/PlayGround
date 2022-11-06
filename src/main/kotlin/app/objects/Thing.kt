package app.objects

import app.Position
import java.lang.StringBuilder
import kotlin.random.Random

class Thing(
    val position: Position,
    val type: ThingType,
) {
    /**
     * A thing can be any value from 1 to 5
     */
    private val value: Int = (Random.nextInt(0, Int.MAX_VALUE) % 5) + 1

    /**
     * A thing can be worth negative or positive value depending on its [ThingType]
     */
    val worth: Int
        get() = value * ThingType.toInt(type)

    /**
     * Good things are green and evil things are red
     */
    private val color: Int
        get() = when (type) {
            ThingType.GOOD -> 92 // Green
            ThingType.EVIL -> 91 // Red
        }

    fun draw(builder: StringBuilder) {

        erase(builder)
        builder
            .append("\u001b[${position.posY};${position.posX}H")
            .append("\u001b[${color}m")
            .append("\u25E2\u25E3")
            .append("\u001b[${position.posY + 1};${position.posX}H")
            .append("\u25E5\u25E4")
            .append("\u001b[0m")
    }

    private fun erase(builder: StringBuilder) {
        builder
            .append("\u001b[${position.posY};${position.posX + 1}H")
            .append("\u001b[${color}m")
            .append("\u25E2\u25E3")
            .append("\u001b[${position.posY + 1};${position.posX + 1}H")
            .append("\u25E5\u25E4")
            .append("\u001b[0m")
    }

    fun move(): Boolean {
        position.posX--
        if (position.posX == 0)
            return false
        return true
    }
}
