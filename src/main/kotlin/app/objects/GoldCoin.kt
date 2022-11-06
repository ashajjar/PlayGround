package app.objects

import app.Position
import java.lang.StringBuilder

class GoldCoin(
    val position: Position
) {
    fun draw(builder: StringBuilder) {
        erase(builder)
        builder
            .append("\u001b[${position.posY};${position.posX}H")
            .append("\u001b[93m")
            .append("\u25E2\u25E3")
            .append("\u001b[${position.posY + 1};${position.posX}H")
            .append("\u25E5\u25E4")
            .append("\u001b[0m")
    }

    fun erase(builder: StringBuilder) {
        builder
            .append("\u001b[${position.posY};${position.posX + 1}H")
            .append("\u001b[30m")
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
