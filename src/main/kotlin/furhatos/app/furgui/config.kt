package furhatos.app.furgui

import furhatos.event.Event
import furhatos.skills.HostedGUI
import kotlin.reflect.jvm.internal.impl.incremental.components.Position

/*
    Variables and events
 */
val PORT = 1234 // GUI Port
val SPEECH_DONE = "SpeechDone"

// Event used to pass data to GUI
class DataDelivery(
        val buttons : List<String>,
        val inputFields: List<String>,
        val sliderPosition: Int
) : Event()



