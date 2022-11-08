package app

import app.objects.Thing
import app.objects.Person
import app.objects.ThingType
import lib.StandardC
import java.io.IOException
import java.time.Instant
import kotlin.math.max
import kotlin.random.Random
import kotlin.system.exitProcess

class View {
    companion object {
        const val ARROW_UP = 1000
        const val ARROW_DOWN = 1001
        const val ARROW_LEFT = 1002
        const val ARROW_RIGHT = 1003
        const val HOME = 1004
        const val END = 1005
        const val PAGE_UP = 1006
        const val PAGE_DOWN = 1007
        const val DEL = 1008
        const val ROWS: Short = 20
        const val COLUMNS: Short = 120
        const val GAME_OVER_THRESHOLD = 3
    }

    private var originalAttributes: StandardC.Termios? = null

    private var player: Person = Person(Position(2, 15), 0)
    private var thingsInView: List<Thing> = mutableListOf()

    private var lastObjectTime: Instant = Instant.now()

    private var missed = 0

    private var userName = ""

    fun render() {
        val builder = StringBuilder()

        resetScreen(builder)
        resetCursor(builder)
        addRandomObjects()
        moveObjects()
        drawFrame(builder)
        drawStatusBar(builder)

        print(builder)
    }

    private fun addRandomObjects() {
        val interval = ((Random.nextLong(0, Long.MAX_VALUE) % 10) * 1000) + 2000
        if (lastObjectTime.plusMillis(interval) > Instant.now()) {
            return
        }
        val randomPosY = Random.nextInt(1, ROWS - 1)
        thingsInView =
            thingsInView +
                    Thing(
                        Position(COLUMNS.toInt() - Thing.WIDTH, randomPosY),
                        ThingType.randomType()
                    )
        lastObjectTime = Instant.now()
    }

    private fun moveObjects() {
        thingsInView.forEach {
            if (!it.move()) {
                if (it.type == ThingType.GOOD) {
                    missed++
                }
                thingsInView = thingsInView - it
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

    fun handleSplashScreen() {

        var key: Int
        var keyCount = 0

        val fileContent = View::class.java.getResource("/splash.txt")!!.readText()
        val values = arrayListOf(Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE)
        val nameLine = 12
        val namePositions = arrayListOf(60, 63, 66)
        while (userName == "") {
            val builder = StringBuilder()
            builder.append("\u001b[H")
            resetScreen(builder)
            fileContent.lines().forEach {
                builder
                    .append(it)
                    .append("\r\n")
            }

            builder.append("\u001b[$nameLine;${namePositions[0]}H")

            values
                .filter { it != Char.MIN_VALUE }
                .forEachIndexed { i, c ->
                    builder
                        .append("\u001b[1m")
                        .append(c)
                        .append("\u001b[0m")
                    if (i < 2) {
                        builder.append("\u001b[$nameLine;${namePositions[i + 1]}H")
                    } else {
                        builder.append("\u001b[$nameLine;${namePositions[2]}H")
                    }
                }

            print(builder)

            key = readKey()
            when (key) {
                4 -> exit()
                127 -> {
                    if (keyCount > 0) {
                        values[--keyCount] = Character.MIN_VALUE

                    }
                }

                13 -> {
                    if (keyCount >= 2) {
                        userName = values.joinToString(separator = "")
                    }
                }

                in 97..122, in 65..90 -> {
                    if (keyCount <= 2) {
                        values[keyCount] = key.toChar()
                        keyCount++
                    }
                }

            }

        }
    }

    private fun drawFrame(builder: StringBuilder) {
        for (i in 0 until ROWS) {
            builder
                .append(" ".repeat(COLUMNS.toInt()))
                .append("\r\n")
        }
        player.draw(builder)
        player.drawName(builder, userName)
        thingsInView.forEach {
            it.draw(builder)
        }
    }

    private fun drawStatusBar(builder: StringBuilder) {
        val statusMessage =
            "\u001B[42;1mScore=${player.score} \u001B[101;1mMissed=$missed"
        builder
            .append("\u001b[${ROWS + 1};1H")
            .append("\u001b[20;7m")
            .append(statusMessage)
            .append(" ".repeat(max(0, COLUMNS - statusMessage.length - 31)))
            .append("\u001B[40m(EXIT = CTRL+D)")
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
            player.handleKey(key)
        }
    }

    private fun exit() {
        print("\u001b[2J")
        print("\u001b[H")
        print("\u001b[?25h")
        StandardC.INSTANCE.tcsetattr(StandardC.SYSTEM_OUT_FD, StandardC.TCSAFLUSH, originalAttributes)
        exitProcess(0)
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
        thingsInView.forEach {
            if (player.meets(it)) {
                player.score(it.worth)
                print(it.worth)
                // todo make sounds optional
                // print("\u0007")
                thingsInView = thingsInView - it
            }
        }
    }

    fun checkGameOver(): Boolean {
        return missed >= GAME_OVER_THRESHOLD
    }
}