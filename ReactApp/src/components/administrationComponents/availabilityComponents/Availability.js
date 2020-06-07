import React ,{useState} from 'react'
import {Form, Table, Col, Container, Button} from 'react-bootstrap';
import DatePicker from "react-datepicker"
import "react-datepicker/dist/react-datepicker.css"

import {setValidEndDateString, validateDates, renderAvailabilityTable} from "./AvailabilityHelpFunctions"
import {setDate} from "../../TimeComponents/TimeFunctions"

var moment = require('moment'); 

/* ---------------------------------------------------------FOR USAGE---------------------------------------------------------
-> Usage in your Form:
    <AvailabilityForm  
        availabilities={availabilities} 
        addAvailability={addAvailability}
        updateAvailabilities={updateAvailabilities} 
        editedAvailabilities={setEdited}
    />

-> Make sure you have a state for your availabilities as an array like this: 
    const [availabilities, setAvailabilities] = useState([])

-> Make sure you have a state for representing an edit:
    const [edited, setEdited] = useState(false)

-> Create the following two functions in your Form

    const addAvailability = (newAvailability) => {
        setAvailabilities(availabilities => [...availabilities, newAvailability]);
    }

    const updateAvailabilities = (newAvailabilities) => {
      setAvailabilities([]) 
      newAvailabilities.map((SingleAvailability)=> {
        setAvailabilities(availabilities => [...availabilities, SingleAvailability]);
      })
    }

----------------------------------------------------------------------------------------------------------------------------*/

const Availability = (props) => {
    //props.addAvailability
    //props.availabilities
    //props.editedAvailabilities
    //props.updateAvailabilities

    //Availability
    const availabilityRhythm ={
        oneTime: "oneTime",
        daily: "daily",
        weekly: "weekly",
        monthly: "monthly",
        yearly: "yearly",
    }
    const [startDateString, setStartDateString] = useState(setDate())
    const [endDateString, setEndDateString] = useState(setValidEndDateString(setDate()))
    const [rhythm, setRhythm] = useState(availabilityRhythm.oneTime)
    const [frequency, setFrequency] = useState(1)
    const [endOfSeriesDateString, setEndOfSeriesDateString] = useState(null)
    const [withSeriesEnd, setWithSeriesEnd] = useState(false)
    

    //---------------------------------HandleChange---------------------------------
    const handleStartDateStringChange = date => {
        const newDateString = moment(date).format("DD.MM.YYYY HH:mm").toString()
        const endDate = moment(endDateString, "DD.MM.yyyy HH:mm").toDate()
        const endOfSeriesDate = moment(endOfSeriesDateString, "DD.MM.yyyy HH:mm").toDate()

        setStartDateString(newDateString)

        if(endDate.getTime() <= date.getTime()) {
           setEndDateString(setValidEndDateString(newDateString)) 
        }
        if(withSeriesEnd && endOfSeriesDate.getTime() <= date.getTime()) {
            setEndOfSeriesDateString(setValidEndDateString(newDateString))
        }
        props.editedAvailabilities(true)
    }

    const handleEndDateStringChange = date => {
        const newDateString = moment(date).format("DD.MM.YYYY HH:mm").toString()
        const endOfSeriesAsDate = moment(endOfSeriesDateString, "DD.MM.yyyy HH:mm").toDate()

        setEndDateString(newDateString)
        if(withSeriesEnd &&  endOfSeriesAsDate.getTime() < date.getTime()) {
            setEndOfSeriesDateString(newDateString)
        }
        props.editedAvailabilities(true)

    }

    const handleEndOfSeriesDateStringChange = date => {
        const newDateString = moment(date).format("DD.MM.YYYY HH:mm").toString()
        
        setEndOfSeriesDateString(newDateString)
        props.editedAvailabilities(true)

    }
    const handleRhythmChange = data => {
        if(data.target.value != availabilityRhythm.oneTime) {
            setFrequency(1)
        }else {
            setFrequency(null)
        }
        setRhythm(data.target.value)
        props.editedAvailabilities(true)
    }
    const handleFrequencyChange = data =>  {    
        if(rhythm == availabilityRhythm.oneTime) {
            setFrequency(1)
        }else {
            setFrequency(data.target.value)
        }
        props.editedAvailabilities(true)
    }
    const handleWithSeriesEndChange = data =>  {    
        if(data.target.value == "false"){
            setWithSeriesEnd(false);
            setEndOfSeriesDateString(null)
        } else{
            setWithSeriesEnd(true);
            setEndOfSeriesDateString(endDateString)
        }
        props.editedAvailabilities(true)
    }

    const handleEndDateFocusChange = () => {
        if(!validateDates(startDateString, endDateString, endOfSeriesDateString))
        {
            setEndDateString(setValidEndDateString(startDateString))
        }
    }

    const handleEndOfSeriesDateFocusChange = () => {
        if(!validateDates(startDateString, endDateString, endOfSeriesDateString))
        {
            setEndOfSeriesDateString(endDateString)
        }
    }


    //---------------------------------Availability---------------------------------
    const handleAdd = () => {
        const newAvailability = {startDate: startDateString, endDate: endDateString, rhythm, frequency, endOfSeries: endOfSeriesDateString}
        props.addAvailability(newAvailability)
        props.editedAvailabilities(true)
    }

    const handleCancleAvailability = data => {
        const answer = confirm("Möchten Sie diese Verfügbarkeit wirklich deaktivieren? ")
        if (answer) {
            props.availabilities.map((singleAvailability, index) => {
                if(index == data.target.value) {
                    singleAvailability.endOfSeries = moment().format("DD.MM.YYYY HH:mm").toString()
                }
            })
            props.updateAvailabilities(props.availabilities)
            props.editedAvailabilities(true)   
        }
    }

    return (
        <React.Fragment>
            <Container>
                    <Form.Row>
                        <Form.Label><h5>Erster verfügbarer Zeitraum</h5></Form.Label>
                    </Form.Row>
                    <Form.Row>
                        <Form.Group style={{display: "flex", flexWrap: "nowrap"}} as={Col} md="6">
                            <Form.Label style={{marginRight: "20px"}}>Start</Form.Label>
                            <DatePicker
                                required
                                selected={moment(startDateString, "DD.MM.yyyy HH:mm").toDate()}
                                onChange={handleStartDateStringChange}
                                showTimeSelect
                                timeFormat="HH:mm"
                                timeIntervals={5}
                                timeCaption="Uhrzeit"
                                dateFormat="dd.M.yyyy / HH:mm"
                            />
                        </Form.Group>
                        <Form.Group style={{display: "flex", flexWrap: "nowrap"}} as={Col} md="6">
                            <Form.Label style={{marginRight: "20px"}}>Ende</Form.Label>
                            <DatePicker 
                                required
                                selected={moment(endDateString, "DD.MM.yyyy HH:mm").toDate()}
                                onChange={handleEndDateStringChange}
                                showTimeSelect
                                timeFormat="HH:mm"
                                timeIntervals={5}
                                timeCaption="Uhrzeit"
                                dateFormat="dd.M.yyyy / HH:mm"
                                onCalendarClose={e => handleEndDateFocusChange()}
                            />
                        </Form.Group>
                    </Form.Row>
                        <hr style={{ border: "0,5px dashed #999999" }}/>
                    <Form.Row>
                        <Form.Label><h5>Serienoptionen</h5></Form.Label>
                    </Form.Row>
                    <Form.Row >
                        <Form.Group style={{display: "flex", flexWrap: "nowrap"}} as={Col} md="7">
                            <div style={{borderStyle: "solid"}} key={`inline-radio-rhythm`} className="mb-3">
                                <Form.Label style={{margin: "0px 10px 0px 10px"}}>Intervall:</Form.Label>
                                <Form.Check inline 
                                    defaultChecked
                                    name="rhythm" 
                                    label="Einmalig"
                                    type='radio' 
                                    value={availabilityRhythm.oneTime}
                                    onChange={handleRhythmChange} 
                                    id={`rhythm-non`} />
                                <Form.Check inline 
                                    name="rhythm" 
                                    label="Täglich" 
                                    type='radio' 
                                    value={availabilityRhythm.daily} 
                                    onChange={handleRhythmChange} 
                                    id={`rhythm-dayly`} />
                                <Form.Check inline 
                                    name="rhythm" 
                                    label="Wöchentlich" 
                                    type='radio' 
                                    value={availabilityRhythm.weekly} 
                                    onChange={handleRhythmChange} 
                                    id={`rhythm-weekly`} />
                                <Form.Check inline 
                                    name="rhythm" 
                                    label="Monatlich" 
                                    type='radio'
                                    value={availabilityRhythm.monthly} 
                                    onChange={handleRhythmChange} 
                                    id={`rhythm-monthly`} />
                                <Form.Check inline 
                                    name="rhythm" 
                                    label="Jährlich" 
                                    type='radio' 
                                    value={availabilityRhythm.yearly} 
                                    onChange={handleRhythmChange} 
                                    id={`rhythm-yearly`} />
                            </div>
                        </Form.Group>
                        <Form.Group style={{display: "flex", flexWrap: "nowrap"}} as={Col} md="5">
                            <Form.Label style={{margin: "3px 20px 0px 0px"}}>Wiederholungsintervall:</Form.Label>
                            <Form.Control
                                disabled={rhythm == availabilityRhythm.oneTime}
                                style={{width: "70px", height: "30px"}}
                                name="frequency"
                                type="text"
                                placeholder="1"
                                value={frequency || 1}
                                onChange={handleFrequencyChange}
                            />
                        </Form.Group>
                    </Form.Row>
                    <Form.Row>
                        <Form.Group as={Col} md="4">
                            <Form.Check
                                disabled={rhythm == availabilityRhythm.oneTime}
                                style={{marginRight: "20px"}}
                                name="endOfSeries" 
                                label="Mit Ende" 
                                type='radio' 
                                onChange={handleWithSeriesEndChange}
                                value={true} 
                                checked={withSeriesEnd}
                                id={`withSeriesEnd`} />
                            <DatePicker
                                disabled={!withSeriesEnd || rhythm == availabilityRhythm.oneTime}
                                selected={endOfSeriesDateString != null ? moment(endOfSeriesDateString, "DD.MM.yyyy HH:mm").toDate() : null}
                                onChange={handleEndOfSeriesDateStringChange}
                                showTimeSelect
                                timeFormat="HH:mm"
                                timeIntervals={5}
                                timeCaption="Uhrzeit"   
                                dateFormat="dd.M.yyyy / HH:mm"
                                onCalendarClose={e => handleEndOfSeriesDateFocusChange()}
                            />
                        </Form.Group>
                        <Form.Group as={Col} md="3">
                            <Form.Check
                                disabled={rhythm == availabilityRhythm.oneTime}
                                name="endOfSeries" 
                                label="Ohne Ende" 
                                type='radio' 
                                onChange={handleWithSeriesEndChange}
                                value={false} 
                                checked={!withSeriesEnd}
                                id={`noSeriesEnd`} />
                        </Form.Group>
                    </Form.Row>
                        <hr style={{ border: "0,5px solid #999999" }}/>
                    <Form.Row>
                        <Form.Group as={Col} md="12">
                            <div style={{textAlign: "center"}}>
                                <Button onClick={handleAdd}>Verfügbarkeit hinzufügen</Button>
                            </div>
                        </Form.Group>
                    </Form.Row>
                    <Form.Row>
                    <Table style={{border: "2px solid #AAAAAA", marginTop: "10px", width: "100%", borderCollapse: "collapse"}} striped variant="ligth">
                            <thead>
                                <tr>
                                    <th colSpan="6" style={{textAlign: "center"}}>Verfügbarkeiten</th>
                                </tr>
                                <tr>
                                    <th style={{width: "200px"}}>Start</th>
                                    <th style={{width: "200px"}}>Ende</th>
                                    <th>Intervall</th>
                                    <th>Wdh-Intervall</th>
                                    <th style={{width: "200px"}}>Serienende</th>
                                    <th style={{width: "25px"}}></th>
                                </tr>
                            </thead>
                            <tbody>
                                {renderAvailabilityTable(props.availabilities, availabilityRhythm, handleCancleAvailability)}
                            </tbody>
                        </Table> 
                    </Form.Row>
        </Container>
    </React.Fragment>
    )
}
export default Availability