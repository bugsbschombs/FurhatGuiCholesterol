package furhatos.app.furgui

import furhatos.event.senses.SenseSkillGUIConnected
import furhatos.flow.kotlin.*
import furhatos.records.Record
import furhatos.skills.HostedGUI

// Sarah for log file info
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.nio.file.Paths
import java.nio.file.Files

// Our GUI declaration
val GUI = HostedGUI("ExampleGUI", "assets/exampleGui", PORT)
val VARIABLE_SET = "VariableSet"
val CLICK_BUTTON = "ClickButton"
//Sarah add Slider
val SLIDER_CHANGE = "SliderChange"

// Sarah test
// Define the character names
const val character1 = "Mask/Mask1"
const val character2 = "Mask/Mask7"
const val character3 = "Mask/Mask8"
const val character4 = "Mask/Mask9"
const val character5 = "Mask/Mask10"
const val character6 = "Mask/Mask11"
const val character7 = "Mask/Mask12"
const val character8 = "Mask/Mask13"
const val character9 = "Mask/Mask14"
const val character10 = "Mask/Mask15"
const val character11 = "Mask/Mask16"
const val character12 = "Mask/Mask17"
const val character13 = "Mask/Mask18"
const val character14 = "Mask/Mask19"
const val character15 = "Mask/Mask20"
const val character16 = "Mask/Mask21"
const val character17 = "Mask/Mask22"
const val character18 = "Mask/Mask23"
const val character19 = "Mask/Mask24"
const val character20 = "Mask/Mask25"


// Sarah slider initial
val initialSliderPosition = 50

// Track the time and log data
var startTime: LocalDateTime? = null
val logData = mutableListOf<Pair<Int, String>>()

// Function to save log data to a file
fun saveLogData() {
    val endTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
    val fileName = "slider_log_${endTime.format(formatter)}.txt"
    val logDirectory = Paths.get("/Users/user_name/Project_Code/FaceVis/FurhatTutorials/FurGUI/assets/exampleGui/log_files")

    // Create the logs directory if it doesn't exist
    if (!Files.exists(logDirectory)) {
        Files.createDirectories(logDirectory)
    }

    val file = logDirectory.resolve(fileName).toFile()
    file.printWriter().use { out ->
        out.println("Start Time: ${startTime?.format(formatter)}")
        out.println("End Time: ${endTime.format(formatter)}")
        out.println("Slider Value, Mapped Character")
        logData.forEach { (value, character) ->
            out.println("$value, $character")
        }
    }
}

// Function to map slider value to character
fun getCharacterForSliderValue(value: Int): String {
    return when (value) {
        in 0..4 -> character1
        in 5..9 -> character2
        in 10..14 -> character3
        in 15..19 -> character4
        in 20..24 -> character5
        in 25..29 -> character6
        in 30..34 -> character7
        in 35..39 -> character8
        in 40..44 -> character9
        in 45..49 -> character10
        in 50..54 -> character11
        in 55..59 -> character12
        in 60..64 -> character13
        in 65..69 -> character14
        in 70..74 -> character15
        in 75..79 -> character16
        in 80..84 -> character17
        in 85..89 -> character18
        in 90..94 -> character19
        else -> character20 // For range 95-100
    }
}


// Starting state, before our GUI has connected.
val NoGUI: State = state(null) {
    onEvent<SenseSkillGUIConnected> {
        goto(GUIConnected)
    }
}


/*
    Here we know our GUI has connected. Since the user might close down the GUI and then reopen
    again, we inherit our handler from the NoGUI state. An edge case might be that a second GUI
    is opened, but this is not accounted for here.

 */
val GUIConnected = state(NoGUI) {
    onEntry {
        // Pass data to GUI
        send(DataDelivery(buttons = buttons, inputFields = inputFieldData.keys.toList(), sliderPosition = initialSliderPosition))
    }

    // Users clicked any of our buttons
    onEvent(CLICK_BUTTON) {
        // Directly respond with the value we get from the event, with a fallback
        //furhat.say("You pressed ${it.get("data") ?: "something I'm not aware of" }")

        // Sarah test: Change the character's face when a button is clicked
        val buttonLabel = it.get("data") as String
        when (buttonLabel) {
            "A button" -> {
                furhat.character = character1
                furhat.say("You switched face to $character1")
            }
            "Another button" -> {
                furhat.character = character2
                furhat.say("You switched face to $character2")
                saveLogData()
            }
            "Test" -> {
                furhat.character = character3
                furhat.say("You switched face to $character3")
            }
        }

        // Directly respond with the value we get from the event, with a fallback
        //furhat.say("You pressed $buttonLabel")
        // Let the GUI know we're done speaking, to unlock buttons
        send(SPEECH_DONE)
    }


    //Slider mapping segment of 10 = character
    // Users changed the slider position
    onEvent(SLIDER_CHANGE) {
        //val sliderValue = (it.get("value") as String).toInt() // Ensure the value is interpreted as an integer
        val sliderValue = it.get("value") as Int
        val character = getCharacterForSliderValue(sliderValue)
        furhat.character = character
        println("Slider value: $sliderValue, Mapped character: $character") // Log the character
        //furhat.say("You switched face to $character")
        send(SPEECH_DONE)
    }

//    onEvent(VARIABLE_SET) {
//        // Get data from event
//        val data = it.get("data") as Record
//        val variable = data.getString("variable")
//        val value = data.getString("value")
//
//        // Get answer depending on what variable we changed and what the new value is, and speak it out
//        val answer = inputFieldData[variable]?.invoke(value)
//        furhat.say(answer ?: "Something went wrong")
//
//        // Let the GUI know we're done speaking, to unlock buttons
//        send(SPEECH_DONE)
//    }


    // Sarah adds data field no answer, just log input text
    onEvent(VARIABLE_SET) {
        // Get data from event
        val data = it.get("data") as Record
        val variable = data.getString("variable")
        val value = data.getString("value")

        // Get answer depending on what variable we changed and what the new value is, and speak it out
        val answer = inputFieldData[variable]?.invoke(value)
        furhat.say(answer ?: "Something went wrong")

        // Let the GUI know we're done speaking, to unlock buttons
        //send(SPEECH_DONE)
    }
}

