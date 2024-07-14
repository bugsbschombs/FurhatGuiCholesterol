package furhatos.app.furgui

import furhatos.event.senses.SenseSkillGUIConnected
import furhatos.flow.kotlin.*
import furhatos.records.Record
import furhatos.skills.HostedGUI
import furhatos.gestures.Gestures

// Sarah for log file info
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.nio.file.Paths
import java.nio.file.Files

//to define new gestures and adjust the mouth
import furhatos.gestures.ARKitParams
import furhatos.gestures.defineGesture

// Our GUI declaration
val GUI = HostedGUI("ExampleGUI", "assets/exampleGui", PORT)
val VARIABLE_SET = "VariableSet"
val CLICK_BUTTON = "ClickButton"
val SLIDER_CHANGE = "SliderChange"
val INPUT_SAVED = "InputSaved"

// Sarah Air Pollution
// character for Air Pollution - Experiment
const val character1 = "SicknessOptimal/SicknessOptimal1"
const val character2 = "SicknessNearOptI/SicknessNearOptI1"
const val character3 = "SicknessNearOptII/SicknessNearOptII1"
const val character4 = "SicknessBorderlineI/SicknessBorderlineI1"
const val character5 = "SicknessBorderlineII/SicknessBorderlineII1"
const val character6 = "SicknessBorderlineIII/SicknessBorderlineIII1"
const val character7 = "SicknessHigh/SicknessHigh1"
const val character8 = "SicknessHigh/SicknessHigh3"
const val character9 = "SicknessHigh/SicknessHigh6"
const val character10 = "SicknessHigh/SicknessHigh7"


// Sarah slider initial
val initialSliderPosition = 89

// Track the time and log data
var startTime: LocalDateTime? = null
val logData = mutableListOf<Pair<Int, String>>()

// track and print time
var sliderStartTime: LocalDateTime? = null
var timeSpentOnGUI: Long = 0

// to log files:
// Add this variable to specify the file name and path
val logFileName = "P02_log.txt"
val logFilePath = "/Users/sschombs/Project_Code/FaceVis/FurhatGuiCholesterol/log_files/$logFileName" // Change to your desired path

//adjust gestures so mouth tilts down

val SadMouth = defineGesture("SadMouth") {
    frame(0.5, persist = true) {
        ARKitParams.MOUTH_FROWN_LEFT to 0.4
        ARKitParams.MOUTH_FROWN_RIGHT to 0.4
    }
}

val HappyMouth = defineGesture("HappyMouth") {
    frame(0.5, 3.0, persist = true) {
        ARKitParams.MOUTH_SMILE_LEFT to 0.3
        ARKitParams.MOUTH_SMILE_RIGHT to 0.3
    }
}

val SadBlink = defineGesture("SadBlink") {
    frame(0.5, 2.0, persist = true) {
        ARKitParams.EYE_BLINK_LEFT to 1.0
        ARKitParams.EYE_BLINK_RIGHT to 1.0
        ARKitParams.MOUTH_FROWN_LEFT to 0.6
        ARKitParams.MOUTH_FROWN_RIGHT to 0.6
    }
    reset(3.0)
}

//val SadBrowFrown = defineGesture("SadBrowFrown") {
//    frame(0.5, 3.0, persist = true) {
//        ARKitParams.BROW_DOWN_LEFT to 1.0
//        ARKitParams.BROW_DOWN_RIGHT to 1.0
//        ARKitParams.MOUTH_FROWN_LEFT to 0.5
//        ARKitParams.MOUTH_FROWN_RIGHT to 0.5
//    }
//}


// Create a function to log messages to the file
fun logToFile(message: String) {
    val file = File(logFilePath)
    file.appendText("$message\n")
}


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
        in 89..100 -> character1
        in 100..114 -> character2
        in 115..128 -> character3
        in 129..138 -> character4
        in 139..148 -> character5
        in 149..158 -> character6
        in 159..168 -> character7
        in 169..178 -> character8
        in 179..188 -> character9
        else -> character10 // For range 201-255
    }
}



// Starting state, before our GUI has connected.
val NoGUI: State = state(null) {
    //default is Alex
    onEntry {
        furhat.character = "Alex"
    }
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
        //default character is ALex
        furhat.character = "Alex"
        furhat.attendAll()
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
            "Done" -> {
//                //track time until done clicked
//                sliderStartTime?.let {
//                    timeSpentOnGUI = java.time.Duration.between(it, LocalDateTime.now()).seconds
//                    println("Time spent on GUI: $timeSpentOnGUI seconds")
//                }
//                // end code for track time until done clicked
//                println("Done: Cholesterol") // Print "Done" to the terminal
                sliderStartTime?.let {
                    timeSpentOnGUI = java.time.Duration.between(it, LocalDateTime.now()).seconds
                    val timeMessage = "Time spent on GUI: $timeSpentOnGUI seconds"
                    println(timeMessage)
                    logToFile(timeMessage)
                }
                val doneMessage = "Done: Cholesterol"
                println(doneMessage) // Print "Done" to the terminal
                logToFile(doneMessage)
            }
        }
        // Directly respond with the value we get from the event, with a fallback
        //furhat.say("You pressed $buttonLabel")
        // Let the GUI know we're done speaking, to unlock buttons
        send(SPEECH_DONE)
    }

    // Users changed the slider position
    onEvent(SLIDER_CHANGE) {
        //val sliderValue = (it.get("value") as String).toInt() // Ensure the value is interpreted as an integer
        val sliderValue = it.get("value") as Int
        val character = getCharacterForSliderValue(sliderValue)
        furhat.character = character
        // Add facial expressions
        when (sliderValue) {
            in 89..99 -> furhat.gesture(Gestures.BigSmile(duration = 2.0))
            in 100..128 -> furhat.gesture(HappyMouth)
            in 129..158 -> furhat.gesture(SadMouth)
            in 159..188 -> {
                furhat.gesture(Gestures.Blink(duration = 3.5))
                furhat.gesture(SadMouth)
            } //furhat.gesture(Gestures.BrowFrown)
            in 189..210 -> {
                furhat.gesture(Gestures.ExpressSad(strength = 0.4))
                furhat.gesture(Gestures.Blink(duration = 3.5))
                furhat.gesture(SadMouth)
            }
        }
        //println("Slider value: $sliderValue, Mapped character: $character") // Log the character
        val logMessage = "Slider value: $sliderValue, Mapped character: $character"
        println(logMessage) // Log the character
        logToFile(logMessage)
        // Set slider start time if it is null
        if (sliderStartTime == null) {
            sliderStartTime = LocalDateTime.now()
        }
        //furhat.say("You switched face to $character")
        send(SPEECH_DONE)
    }

    //to get the input from text field printed in terminal
    onEvent(INPUT_SAVED) {
        //println("Received event INPUT_SAVED: $it") // Debugging log

        val data = it.get("data") as? Record ?: run {
            println("No data found in event")
            return@onEvent
        }

        val label = data["label"] as? String ?: "Unknown Label"
        val inputValue = data["inputValue"] as? String ?: "No Input"
        val logMessage = "User input ($label): $inputValue"
        println(logMessage) // Print the input value to the terminal
        logToFile(logMessage)
        //println("User input ($label): $inputValue") // Print the input value to the terminal
    }




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

