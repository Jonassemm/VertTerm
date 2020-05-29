import React ,{useState} from 'react'
import {Form, Table, Col, Container, Button} from 'react-bootstrap';
import DatePicker from "react-datepicker"
import "react-datepicker/dist/react-datepicker.css"

import {setDate, setValidEndDate, validateDates, renderAvailabilityTable} from "./AvailabilityHelpFunctions"

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
    const [startDate, setStartDate] = useState(setDate())
    const [endDate, setEndDate] = useState(setValidEndDate(setDate()))
    const [rhythm, setRhythm] = useState(availabilityRhythm.oneTime)
    const [frequency, setFrequency] = useState(1)
    const [endOfSeries, setEndOfSeries] = useState(null)
    const [withSeriesEnd, setWithSeriesEnd] = useState(false)
    

    //---------------------------------HandleChange---------------------------------
    const handleStartDateChange = date => {
        const newDateString = moment(date).format("DD.MM.YYYY HH:mm").toString()
        setStartDate(newDateString)
        setEndDate(setValidEndDate(newDateString)) 
        if(withSeriesEnd) {
            setEndOfSeries(setValidEndDate(newDateString))
        }
        props.editedAvailabilities(true)
    }
    const handleEndDateChange = date => {
        const newDateString = moment(date).format("DD.MM.YYYY HH:mm").toString()
        if(validateDates(startDate, newDateString, endOfSeries)){
            setEndDate(newDateString)
            if(withSeriesEnd) {
                setEndOfSeries(newDateString)
            }
            props.editedAvailabilities(true)
        }
    }
    const handleEndOfSeriesChange = date => {
        const newDateString = moment(date).format("DD.MM.YYYY HH:mm").toString()
        if(validateDates(startDate, endDate, newDateString)){
            setEndOfSeries(newDateString)
            props.editedAvailabilities(true)
        }
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
            setEndOfSeries(null)
        } else{
            setWithSeriesEnd(true);
            setEndOfSeries(endDate)
        }
        props.editedAvailabilities(true)
    }


    //---------------------------------Availability---------------------------------
    const handleAdd = () => {
        const newAvailability = {startDate, endDate, rhythm, frequency, endOfSeries}
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
                                selected={moment(startDate, "DD.MM.yyyy HH:mm").toDate()}
                                onChange={handleStartDateChange}
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
                                selected={moment(endDate, "DD.MM.yyyy HH:mm").toDate()}
                                onChange={handleEndDateChange}
                                showTimeSelect
                                timeFormat="HH:mm"
                                timeIntervals={5}
                                timeCaption="Uhrzeit"
                                dateFormat="dd.M.yyyy / HH:mm"
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
                                selected={endOfSeries != null ? moment(endOfSeries, "DD.MM.yyyy HH:mm").toDate() : null}
                                onChange={handleEndOfSeriesChange}
                                showTimeSelect
                                timeFormat="HH:mm"
                                timeIntervals={5}
                                timeCaption="Uhrzeit"   
                                dateFormat="dd.M.yyyy / HH:mm"
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
                        <Button onClick={handleAdd} style={{marginBottom: "20px"}}>Verfügbarkeit hinzufügen</Button>
                    </Form.Row>
                    <Form.Row>
                    <Table striped hover variant="light">
                            <thead>
                                <tr>
                                    <th>Verfügbarkeiten</th>
                                </tr>
                                <tr>
                                    <th>Start</th>
                                    <th>Ende</th>
                                    <th>Intervall</th>
                                    <th>Wdh-Intervall</th>
                                    <th>Serienende</th>
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