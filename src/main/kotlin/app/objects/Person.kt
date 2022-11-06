package app.objects

import app.Position
import java.lang.StringBuilder

class Person(
    val position: Position,
    var score: Int
) {
    fun draw(builder: StringBuilder) {
        builder
            //Head
            .append("\u001b[${position.posY};${position.posX - 1}H")
            .append("\u001b[7m")
            .append("\u001b[91;40m")
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

    fun meets(it: Box): Boolean {
        if (it.position.posX <= position.posX + 2 && it.position.posX >= position.posX) {
            if (it.position.posY <= position.posY + 1 && it.position.posY >= position.posY - 1) {
                return true
            }
        }

        return false
    }

    fun score(i: Int) {
        score += i
    }

}