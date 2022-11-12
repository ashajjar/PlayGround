package app.objects

import app.Position
import java.lang.StringBuilder
import kotlin.random.Random

class Thing(
    val position: Position,
    val type: ThingType,
) {

    companion object {
        const val WIDTH: Int = 2
        const val HEIGHT: Int = 2
    }

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
            ThingType.GOOD -> 51 - value// Green gradient
            ThingType.EVIL -> 201 - value // Red gradient
        }

    fun draw(builder: StringBuilder) {
        builder
            .append("\u001b[${position.posY};${position.posX}H")
            .append("\u001b[38:5:${color}m")
            .append("\u25E2\u25E3")
            .append("\u001b[${position.posY + 1};${position.posX}H")
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
