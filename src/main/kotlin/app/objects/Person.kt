package app.objects

import app.Position
import app.View
import java.lang.StringBuilder
import kotlin.math.absoluteValue

class Person(
    val position: Position,
    var score: Int,
) {
    fun draw(builder: StringBuilder) {
        val personColorPalette = listOf(34, 70, 106, 142, 178, 214)
        val color = personColorPalette.find {
            score < it
        } ?: 214

        builder
            //Head
            .append("\u001b[${position.posY};${position.posX - 1}H")
            .append("\u001b[7m")
            .append("\u001b[38;5;${color};48;5;${color}7m")
            .append("* *")
            .append("\u001b[${position.posY + 1};${position.posX - 1}H")
            .append(" - ")
            .append("\u001b[0m")

        /*
  // Hands
           .append("\u001b[${position.posY + 2};${position.posX - 1}H")
           .append("\u001b[7m")
           .append("\u001b[32;40m")
           .append("-X-")
           .append("\u001b[0m")

           //left leg
           .append("\u001b[${position.posY + 3};${position.posX - 1}H")
           .append("\u001b[7m")
           .append("\u001b[31;31m")
           .append("|")
           .append("\u001b[0m")

           // space between legs
           .append("\u001b[${position.posY + 3};${position.posX}H")
           .append("\u001b[7m")
           .append("\u001b[47;47m")
           .append(" ")
           .append("\u001b[0m")

           //right leg
           .append("\u001b[${position.posY + 3};${position.posX + 1}H")
           .append("\u001b[7m")
           .append("\u001b[31;31m")
           .append("|")
           .append("\u001b[0m")
        */
    }

    fun drawName(builder: StringBuilder, name: String) {
        builder.append("\u001b[${position.posY + 2};${position.posX - 1}H")
            .append("\u001b[7m")
            .append("\u001b[30;47m")
            .append(name)
            .append("\u001b[0m")
    }

    fun meets(it: Thing): Boolean {
        if (it.position.posX <= position.posX + 2 && it.position.posX >= position.posX) {
            if (it.position.posY <= position.posY + 1 && it.position.posY >= position.posY - 1) {
                return true
            }
        }

        return false
    }

    fun score(worth: Int) {
        score += worth
    }

    fun handleKey(key: Int) {
        when (key) {
            View.ARROW_UP -> {
                if (position.posY > 1) {
                    position.posY--
                }
            }

            View.ARROW_DOWN -> {
                if (position.posY < View.ROWS - 1) {
                    position.posY++
                }
            }

            View.ARROW_LEFT -> {
                if (position.posX > 2) {
                    position.posX--
                }
            }

            View.ARROW_RIGHT -> {
                if (position.posX < View.COLUMNS - 1) {
                    position.posX++
                }
            }
        }
    }

}