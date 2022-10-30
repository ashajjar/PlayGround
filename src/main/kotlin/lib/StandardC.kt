package lib

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Structure

internal interface StandardC : Library {
    @Structure.FieldOrder(value = ["ws_row", "ws_col", "ws_xpixel", "ws_ypixel"])
    class Winsize : Structure() {
        @JvmField
        var ws_row: Short = 0
        @JvmField
        var ws_col: Short = 0
        @JvmField
        var ws_xpixel: Short = 0
        @JvmField
        var ws_ypixel: Short = 0
    }

    @Structure.FieldOrder(value = ["c_iflag", "c_oflag", "c_cflag", "c_lflag", "c_cc"])
    class Termios : Structure() {
        @JvmField
        var c_cc = ByteArray(19)
        @JvmField
        var c_cflag = 0
        @JvmField
        var c_iflag = 0
        @JvmField
        var c_lflag = 0
        @JvmField
        var c_oflag = 0
        override fun toString(): String {
            return "Termios{" +
                    "c_iflag=" + c_iflag +
                    ", c_oflag=" + c_oflag +
                    ", c_cflag=" + c_cflag +
                    ", c_lflag=" + c_lflag +
                    ", c_cc=" + c_cc.toString() +
                    '}'
        }

        companion object {
            fun of(t: Termios): Termios {
                val copy = Termios()
                copy.c_iflag = t.c_iflag
                copy.c_oflag = t.c_oflag
                copy.c_cflag = t.c_cflag
                copy.c_lflag = t.c_lflag
                copy.c_cc = t.c_cc.clone()
                return copy
            }
        }
    }

    fun tcgetattr(fd: Int, termios: Termios?): Int
    fun tcsetattr(
        fd: Int, optional_actions: Int,
        termios: Termios?
    ): Int

    fun ioctl(fd: Int, opt: Int, winsize: Winsize?): Int

    companion object {
        const val SYSTEM_OUT_FD = 0
        const val ISIG = 1
        const val ICANON = 2
        const val ECHO = 10
        const val TCSAFLUSH = 2
        const val IXON = 2000
        const val ICRNL = 400
        const val IEXTEN = 100000
        const val OPOST = 1
        const val VMIN = 6
        const val VTIME = 5
        const val TIOCGWINSZ = 0x5413

        // we're loading the C standard library for POSIX systems
        val INSTANCE: StandardC = Native.load("c", StandardC::class.java)
    }
}