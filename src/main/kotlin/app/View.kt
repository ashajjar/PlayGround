package app

import app.objects.Box
import app.objects.Person
import lib.StandardC
import java.io.IOException
import java.time.Instant
import kotlin.math.max
import kotlin.random.Random
import kotlin.system.exitProcess

class View {
    companion object {
        private const val ARROW_UP = 1000
        private const val ARROW_DOWN = 1001
        private const val ARROW_LEFT = 1002
        private const val ARROW_RIGHT = 1003
        private const val HOME = 1004
        private const val END = 1005
        private const val PAGE_UP = 1006
        private const val PAGE_DOWN = 1007
        private const val DEL = 1008
    }

    private var originalAttributes: StandardC.Termios? = null
    private var rows: Short = 20
    private var columns: Short = 120
    private var cursorX = 0
    private var cursorY = 1

    private var statusMessage = ""

    private var player: Person = Person(Position(2, 15), 0)
    private var boxes: List<Box> = mutableListOf()

    private var lastObjectTime: Instant = Instant.now()

    fun render() {
        val builder = StringBuilder()

        resetScreen(builder)
        resetCursor(builder)
        addRandomObjects()
        moveObjects()
        drawFrame(builder)
        drawStatusBar(builder)
        drawCursor(builder)

        print(builder)
    }

    private fun addRandomObjects() {
        val interval = ((Random.nextLong(0, Long.MAX_VALUE) % 10) * 1000) + 2000
        if (lastObjectTime.plusMillis(interval) > Instant.now()) {
            return
        }
        val randomPosY = Random.nextInt(1, rows - 1)
        boxes = boxes + Box(Position(118, randomPosY))
        lastObjectTime = Instant.now()
    }

    private fun moveObjects() {
        boxes.forEach {
            if (!it.move()) {
                boxes = boxes - it
            }

        }
    }

    private fun resetScreen(builder: StringBuilder) {
        builder.append("\u001b[2J")
    }

    private fun resetCursor(builder: StringBuilder) {
        builder.append("\u001b[H")
        builder.append("\u001b[?25l")
    }

    private fun drawCursor(builder: StringBuilder) {
        builder.append("\u001b[$cursorY;${cursorX + 1}H")
    }

    private fun drawFrame(builder: StringBuilder) {
        for (i in 0 until rows) {
            builder
                .append(" ".repeat(120))
                .append("\r\n")

            if (i > 15) {
                builder.append("\u001b[0m")
            }
        }
        player.draw(builder)
        player.drawName(builder, "ASH")
        boxes.forEach {
            it.draw(builder)
        }
    }

    private fun drawStatusBar(builder: StringBuilder) {
        val statusMessage = "Rows: $rows, Columns: $columns (X:$cursorX Y: $cursorY) Score=${player.score}"
        builder
            .append("\u001b[${rows + 1};1H")
            .append("\u001b[20;7m")
            .append(statusMessage)
            .append(" ".repeat(max(0, columns - statusMessage.length)))
            .append("\u001b[0m")
    }

    @Throws(IOException::class)
    private fun readKey(): Int {
        val key = System.`in`.read()
        if (key != '\u001b'.code) {
            return key
        }

        val nextKey = System.`in`.read()
        if (nextKey != '['.code && nextKey != 'O'.code) {
            return nextKey
        }

        val yetAnotherKey = System.`in`.read()

        return if (nextKey == '['.code) {
            when (yetAnotherKey) {
                'A'.code -> ARROW_UP
                'B'.code -> ARROW_DOWN
                'C'.code -> ARROW_RIGHT
                'D'.code -> ARROW_LEFT
                'H'.code -> HOME
                'F'.code -> END
                '0'.code, '1'.code, '2'.code, '3'.code, '4'.code, '5'.code, '6'.code, '7'.code, '8'.code, '9'.code -> {  // e.g: esc[5~ == page_up
                    when (yetAnotherKey) {
                        '1'.code, '7'.code -> HOME
                        '3'.code -> DEL
                        '4'.code, '8'.code -> END
                        '5'.code -> PAGE_UP
                        '6'.code -> PAGE_DOWN
                        else -> yetAnotherKey
                    }
                }

                else -> yetAnotherKey
            }
        } else {
            when (yetAnotherKey) {
                'H'.code -> HOME
                'F'.code -> END
                else -> yetAnotherKey
            }
        }
    }

    fun handleKey() {
        val key = readKey()
        if (key == 4) { //CTRL-D
            exit()
        } else if (arrayListOf(ARROW_UP, ARROW_DOWN, ARROW_LEFT, ARROW_RIGHT, HOME, END).contains(key)) {
            moveCursor(key)
        } else {
            statusMessage = "$key  -> (${key.toChar()})"
        }
    }

    private fun exit() {
        print("\u001b[2J")
        print("\u001b[H")
        StandardC.INSTANCE.tcsetattr(StandardC.SYSTEM_OUT_FD, StandardC.TCSAFLUSH, originalAttributes)
        exitProcess(0)
    }

    private fun moveCursor(key: Int) {
        when (key) {
            ARROW_UP -> {
                if (cursorY > 1) {
                    cursorY--
                }
                if (player.position.posY > 1) {
                    player.position.posY--
                }
            }

            ARROW_DOWN -> {
                if (cursorY < rows) {
                    cursorY++
                }
                if (player.position.posY < rows - 1) {
                    player.position.posY++
                }
            }

            ARROW_LEFT -> {
                if (cursorX > 0) {
                    cursorX--
                }
                if (player.position.posX > 2) {
                    player.position.posX--
                }
            }

            ARROW_RIGHT -> {
                if (cursorX < columns - 1) {
                    cursorX++
                }
                if (player.position.posX < columns - 1) {
                    player.position.posX++
                }
            }

            HOME -> cursorX = 0
            END -> cursorX = columns - 1
        }
    }

    fun enableRawMode() {
        val termios = StandardC.Termios()
        val rc = StandardC.INSTANCE.tcgetattr(StandardC.SYSTEM_OUT_FD, termios)
        if (rc != 0) {
            System.err.println("There was a problem calling tcgetattr")
            exitProcess(rc)
        }
        originalAttributes = StandardC.Termios.of(termios)
        termios.c_lflag =
            termios.c_lflag and (StandardC.ECHO or StandardC.ICANON or StandardC.IEXTEN or StandardC.ISIG).inv()
        termios.c_iflag = termios.c_iflag and (StandardC.IXON or StandardC.ICRNL).inv()
        termios.c_oflag = termios.c_oflag and StandardC.OPOST.inv()

        StandardC.INSTANCE.tcsetattr(StandardC.SYSTEM_OUT_FD, StandardC.TCSAFLUSH, termios)
    }

    fun countPoints() {
        boxes.forEach {
            if (player.meets(it)) {
                player.score(1)
                boxes = boxes - it
            }
        }
    }
}