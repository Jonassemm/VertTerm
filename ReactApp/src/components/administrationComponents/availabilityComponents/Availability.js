import React , {useState, useEffect, forwardRef, useImperativeHandle} from 'react'
import {Form, Table, Col, Container, Button} from 'react-bootstrap';
import DatePicker from "react-datepicker"
import "react-datepicker/dist/react-datepicker.css"

import {setValidEndDateString, validateDates, renderAvailabilityTable} from "./AvailabilityHelpFunctions"
import {setDate} from "../../TimeComponents/TimeFunctions"

import {getOpeningHours} from "../openingHoursComponents/OpeningHoursRequests"

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

const Availability = forwardRef((props, ref) => {
    //props.availabilities
    //props.addAvailability()
    //props.editedAvailabilities()
    //props.updateAvailabilities()
    //props.withOpeningHours
    
    if(props.withOpeningHours == undefined) {
        props.withOpeningHours = true //default value
    }

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

    //needed to separate new added availavilities with the submitted availabilities
    const [addedAvailabilities, setAddedAvailabilities] = useState([])

    const [openingHoursAvailabilities, setOpeningHoursAvailabilites] = useState([])
    const [openingHoursAdded, setOpeningHoursAdded] = useState(false)
    const [openingHoursIndex, setOpeningHoursIndex] = useState(null)
    const [countOfIncludedOpeningHours, setCountOfIncludedOpeningHours] = useState(null)
    

    useEffect(() => {
        loadOpeningHoursAvailabilities()
    }, [])


    //---------------------------------HandleChange---------------------------------
    const handleStartDateStringChange = date => {
        const newDateString = moment(date).format("DD.MM.YYYY HH:mm").toString()
        const startDate = moment(startDateString, "DD.MM.yyyy HH:mm").toDate()
        const endDate = moment(endDateString, "DD.MM.yyyy HH:mm").toDate()
        const endOfSeriesDate = moment(endOfSeriesDateString, "DD.MM.yyyy HH:mm").toDate()
        
        var newEndDate = new Date
        newEndDate.setFullYear(date.getFullYear())
        newEndDate.setMonth(date.getMonth())
        newEndDate.setDate(date.getDate())
        newEndDate.setHours(endDate.getHours())
        newEndDate.setMinutes(endDate.getMinutes())
        newEndDate.setSeconds(0)

        if(endDate.getTime() <= date.getTime()) {
            setEndDateString(setValidEndDateString(newDateString)) 
        }else if(startDate.getDate != date.getDate()){
            setEndDateString(newEndDate) 
        }

        if(withSeriesEnd && endOfSeriesDate.getTime() <= date.getTime()) {
            setEndOfSeriesDateString(setValidEndDateString(newDateString))
        }
        setStartDateString(newDateString)
    }


    const handleEndDateStringChange = date => {
        const newDateString = moment(date).format("DD.MM.YYYY HH:mm").toString()
        const endOfSeriesAsDate = moment(endOfSeriesDateString, "DD.MM.yyyy HH:mm").toDate()

        setEndDateString(newDateString)
        if(withSeriesEnd &&  endOfSeriesAsDate.getTime() < date.getTime()) {
            setEndOfSeriesDateString(newDateString)
        }
    }


    const handleEndOfSeriesDateStringChange = date => {
        const newDateString = moment(date).format("DD.MM.YYYY HH:mm").toString()
        
        setEndOfSeriesDateString(newDateString)

    }


    const handleRhythmChange = data => {
        if(data.target.value != availabilityRhythm.oneTime) {
            setFrequency(1)
        }else {
            setFrequency(null)
        }
        setRhythm(data.target.value)
    }


    const handleFrequencyChange = data =>  {    
        if(rhythm == availabilityRhythm.oneTime) {
            setFrequency(1)
        }else {
            setFrequency(data.target.value)
        }
    }


    const handleWithSeriesEndChange = data =>  {    
        if(data.target.value == "false"){
            setWithSeriesEnd(false);
            setEndOfSeriesDateString(null)
        } else{
            setWithSeriesEnd(true);
            setEndOfSeriesDateString(endDateString)
        }
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

    //---------------------------------Load---------------------------------
    const loadOpeningHoursAvailabilities = async () => {
        var data = []
        try {
            const response = await getOpeningHours();
            data = response.data.availabilities
        }catch (error) {
            console.log(Object.keys(error), error.message)
        }
        setOpeningHoursAvailabilites(data)
    }


    //---------------------------------Availability---------------------------------
    const handleAdd = () => {
        const newAvailability = {startDate: startDateString, endDate: endDateString, rhythm, frequency, endOfSeries: endOfSeriesDateString}
        props.addAvailability(newAvailability)
        props.editedAvailabilities(true)
        setAddedAvailabilities(addedAvailabilities => [...addedAvailabilities, newAvailability])
    }
    

    const handleCancleAvailability = data => {
        const indexDifference = props.availabilities.length - addedAvailabilities.length
        var counter = countOfIncludedOpeningHours
        console.log("delete:")
        console.log(openingHoursIndex)
        var reducedIndex = 0
        props.availabilities.map((singleAvailability, index) => {
            if(index == data.target.value) {
                if((index >= indexDifference)) { //the availabilities which are added, but not submitted   

                    //reduce opening hours
                    if(index <= openingHoursIndex && index > openingHoursIndex - countOfIncludedOpeningHours){
                        console.log("IN-Öffnungszeiten")
                        counter -= 1
                        console.log("REDUCE: countOfIncludeded")
                        setCountOfIncludedOpeningHours(countOfIncludedOpeningHours - 1)
                        setOpeningHoursIndex(openingHoursIndex-1)
                    }else  //reduce before opening hours
                    if(index <= openingHoursIndex - counter){
                        console.log("VOR-Öffnungszeiten")
                        console.log("REDUCE: openingHoursIndex")
                        setOpeningHoursIndex(openingHoursIndex-1)
                    }
                    props.availabilities.splice((index),1) // remove  at "index" and just remove "1" 
                    addedAvailabilities.splice((index-indexDifference),1)
                    props.updateAvailabilities(props.availabilities)

                }else { //the submitted availabilities
                    const answer = confirm("Möchten Sie diese Verfügbarkeit wirklich deaktivieren? ")
                    if (answer) {
                        props.availabilities.map((singleAvailability, index) => {
                            if(index == data.target.value) {
                                console.log("set endSeries")
                                singleAvailability.endOfSeries = moment().format("DD.MM.YYYY HH:mm").toString()
                            }
                        })
                        props.updateAvailabilities(props.availabilities)
                        props.editedAvailabilities(true)   
                    }
                }
            }
        })
    }

    const setOpeningHours = (asOpeningHours) => {
        setOpeningHoursAdded(asOpeningHours)

        //add opening hours
        if(asOpeningHours && openingHoursAvailabilities.length > 0) {
            setOpeningHoursIndex(props.availabilities.length + openingHoursAvailabilities.length - 1) //reduce 1 cause of index 0
            //add all availabilities of opening hours
            openingHoursAvailabilities.map(singleAvailability => {
                //For pass the value not the reference of an object JSON.parse(JSON.stringify(singleAvailability))
                props.addAvailability(JSON.parse(JSON.stringify(singleAvailability)))
                setAddedAvailabilities(addedAvailabilities => [...addedAvailabilities, JSON.parse(JSON.stringify(singleAvailability))])
                setCountOfIncludedOpeningHours(openingHoursAvailabilities.length)
            })
        }else {//reduce opening hours
            var availabilitiesWithoutOpeningHours = []
            props.availabilities.map((singleAvailability, index)=>{
                //if(index >= openingHoursIndex || index < (openingHoursIndex - openingHoursAvailabilities.length)){
                if(index > openingHoursIndex || index <= (openingHoursIndex - countOfIncludedOpeningHours)){    
                    availabilitiesWithoutOpeningHours.push(singleAvailability)
                }
            })
            const indexDifference = props.availabilities.length - addedAvailabilities.length
            const addedAvailabilitiesIndex = openingHoursIndex - indexDifference
            //remove the openingHoursAvailabilities from addedAvailability
            addedAvailabilities.splice((addedAvailabilitiesIndex-(countOfIncludedOpeningHours-1)),countOfIncludedOpeningHours)
            //set availabilities without opening hours
            props.updateAvailabilities(availabilitiesWithoutOpeningHours)
        }
        props.editedAvailabilities(true)   
    }


    useImperativeHandle(ref, () => ({
        submitted()  {
            setAddedAvailabilities([])
        }
    }))


    const render = () =>{
        console.log("____RENDER____")
        console.log(props.availabilities)
    }


    return (
        <React.Fragment>
            {render()}
            <Container>
                <Form.Row style={{ alignItems: "baseline" }}>
                <Form.Group as={Col} style={{textAlign: "right"}}>
                    {props.withOpeningHours &&
                        <Form.Check
                            id="switchAsOpeningHours"
                            type="switch"
                            name="asOpeningHours"
                            value={openingHoursAdded || true}
                            onChange={e => setOpeningHours(!openingHoursAdded)}
                            checked={openingHoursAdded}
                            label={"Öffnungszeiten hinzufügen"}
                        />
                    }
                </Form.Group>
            </Form.Row>
            <Form.Row>
                <Form.Label><h5>Erster verfügbarer Zeitraum</h5></Form.Label>
            </Form.Row>
            {
            <Container>
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
                            showTimeSelectOnly
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
            </Container>
            }
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
                        {renderAvailabilityTable(
                            props.availabilities, 
                            addedAvailabilities.length, 
                            availabilityRhythm, 
                            handleCancleAvailability)
                        }
                    </tbody>
                </Table> 
            </Form.Row>
        </Container>
    </React.Fragment>
    )
})
export default Availability