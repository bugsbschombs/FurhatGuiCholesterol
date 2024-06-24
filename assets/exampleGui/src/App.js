import React, {Component} from 'react'
import FurhatGUI from 'furhat-gui'
import { Grid, Row, Col } from 'react-bootstrap'
import Button from './Button'
import Input from './Input'
import Slider from './Slider'; // Sarah add Slider

class App extends Component {

    constructor(props) {
        super(props)
        this.state = {
          "speaking": false,
          "buttons": [],
          "inputFields": [],
          sliderPosition: 50, // Sarah adds: Initial slider position in the middle
          logMessages: []
        }
        this.furhat = null

    }

    setupSubscriptions() {
        // Our DataDelivery event is getting no custom name and hence gets it's full class name as event name.
        this.furhat.subscribe('furhatos.app.furgui.DataDelivery', (data) => {
            this.setState({
                ...this.state,
                buttons: data.buttons,
                inputFields: data.inputFields,
                sliderPosition: data.sliderPosition || 50 // Sarah adds anchor/slider position
            })
        })

        // This event contains to data so we defined it inline in the flow
        this.furhat.subscribe('SpeechDone', () => {
            this.setState({
                ...this.state,
                speaking: false
            })
        })
    }

    componentDidMount() {
        FurhatGUI()
            .then(connection => {
                this.furhat = connection
                this.setupSubscriptions()
            })
            .catch(console.error)
    }

    clickButton = (button) => {
        this.setState({
            ...this.state,
            speaking: true
        })
        this.furhat.send({
          event_name: "ClickButton",
          data: button
        })
    }

    variableSet = (variable, value) => {
        this.setState({
            ...this.state,
            speaking: true
        })
        this.furhat.send({
          event_name: "VariableSet",
          data: {
            variable,
            value
          }
        })
    }
    //Sarah adds Slider here:
    handleSlide = (value) => {
         this.setState({
             ...this.state,
             sliderPosition: value,
             speaking: true
         })
         this.furhat.send({
             event_name: "SliderChange",
             value: value
         })
    }

    // safe input from free text field
    handleSaveInput = (label, inputValue) => {
        const logMessage = `User input (${label}): ${inputValue}`;
        this.setState(prevState => ({
            logMessages: [...prevState.logMessages, logMessage]
        }))
        console.log(`User answer: ${inputValue}`);
        println(`User answer: ${inputValue}`);
    }



        render() {
        //console.log('App component is rendering'); // Sarah Add console log to verify rendering
            return (
                <Grid>
                    <Row>
                        <Col sm={12}>
                            <h1>Air Pollution</h1>
                        </Col>
                    </Row>
                    <Row>
                        <Col sm={6}>
                            <h2>Particulate Matter (PM)</h2>
                            <p className="text-box">PM is a common proxy indicator for air pollution. It refers to inhalable particles, composed of sulphate, nitrates, ammonia, sodium chloride, black carbon, mineral dust or water. It is the most widely used indicator for assessing the health effects of exposure to air pollution. (see WHO)</p>
                        </Col>
                        <Col sm={6}>
                            <h2>Air Pollution Explorer</h2>
                            <Slider onSlide={this.handleSlide} initialValue={this.state.sliderPosition} />
                            <Input label="How would you describe the air pollution when PM.25 reaches a level of x?" onSave={this.handleSaveInput} />
                            <div>
                                {this.state.logMessages.map((message, index) => (
                                    <p key={index}>{message}</p>
                                ))}
                            </div>
                        </Col>
                    </Row>
                </Grid>
            )
        }
    }

//    render() {
//    //console.log('App component is rendering'); // Sarah Add console log to verify rendering
//        return (
//            <Grid>
//                <Row>
//                    <Col sm={12}>
//                        <h1>Air Pollution</h1>
//                    </Col>
//                </Row>
//                <Row>
//                    <Col sm={6}>
//                        <h2>TEST</h2>
//                        {this.state.buttons.map((label) =>
//                            <Button key={label} label={label} onClick={this.clickButton} speaking={this.state.speaking} />
//                        )}
//                    </Col>
//                    <Col sm={6}>
//                        <h2>TEST TEST TEST</h2>
//                        {this.state.inputFields.map((label) =>
//                            <Input key={label} label={label} onSave={this.variableSet} speaking={this.state.speaking} />
//                        )}
//                        <h2>Particulate matter (PM)</h2>
//                        <p>PM is a common proxy indicator for air pollution.</p>
//                        <Slider onSlide={this.handleSlide} initialValue={this.state.sliderPosition} />
//                    </Col>
//                </Row>
//            </Grid>
//        )
//    }
//}

export default App;
