package app.objects

import kotlin.random.Random

enum class ThingType() {
    GOOD,
    EVIL;

    companion object {
        fun toInt(type: ThingType) =
            when (type) {
                GOOD -> 1
                EVIL -> -1
            }

        fun randomType(): ThingType =
            if (Random.nextInt() % 2 == 0) {
                GOOD
            } else {
                EVIL
            }
    }
}
