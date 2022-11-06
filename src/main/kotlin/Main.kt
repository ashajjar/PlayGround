import app.View
import java.io.IOException
import kotlin.concurrent.thread

class Main {
    companion object {
        @Throws(IOException::class)
        @JvmStatic
        fun main(args: Array<String>) {
            val view = View()
            view.enableRawMode()
            thread {
                while (true) {
                    view.render()
                    Thread.sleep(50)
                }
            }
            thread {
                while (true) {
                    view.countPoints()
                    Thread.sleep(50)
                }
            }
            while (true) {
                view.handleKey()
            }
        }
    }
}