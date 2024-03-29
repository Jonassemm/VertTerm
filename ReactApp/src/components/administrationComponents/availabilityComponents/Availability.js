//author: Patrick Venturini
import React , {useState, useEffect, forwardRef, useImperativeHandle} from 'react'
import {Form, Table, Col, Container, Button} from 'react-bootstrap';
import DatePicker from "react-datepicker"
import "react-datepicker/dist/react-datepicker.css"
import {renderAvailabilityTable} from "./AvailabilityRenderHelp"
import {setDate} from "../../TimeComponents/TimeFunctions"
import {getOpeningHours} from "../../requests"
import { hasRight } from "../../../auth"
import {ownAppointmentRights, availabilityRights} from "../../Rights"

var moment = require('moment'); 


/* ---------------------------------------------------------FOR USAGE-------------------------------------------------------
-> Usage in your Form:
    <AvailabilityForm  
        availabilities={availabilities} 
        addAvailability={addAvailability}
        updateAvailabilities={updateAvailabilities} 
        editedAvailabilities={setEdited}
        userStore={userStore}
        selected={selected}
        withOpeningHours={false}
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
    //props.availabilities          //the availabilities of the parent component
    //props.addAvailability()       //function of the parent component to add an availability 
    //props.editedAvailabilities()  //function of the parent component to pass the information that something has edited
    //props.updateAvailabilities()  //function of the parent component to update an availability
    //props.userStore               //to check the rights
    //props.selected                //optional prop which is the selected item that contains this availability component
    //props.withOpeningHours        //optional prop to reduce the switch for adding the availabilities of the opening hours

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
    const rightName = availabilityRights[1] //write right
    const rightNameOwn = ownAppointmentRights[1] //write right
    const [startDateString, setStartDateString] = useState(setDate())
    const [endDateString, setEndDateString] = useState(setValidEndDateString(setDate()))
    const [rhythm, setRhythm] = useState(availabilityRhythm.oneTime)
    const [frequency, setFrequency] = useState(1)
    const [endOfSeriesDateString, setEndOfSeriesDateString] = useState(null)
    const [withSeriesEnd, setWithSeriesEnd] = useState(false)
    //needed to separate new added availavilities to the submitted availabilities
    const [addedAvailabilities, setAddedAvailabilities] = useState([])
    const [openingHoursAvailabilities, setOpeningHoursAvailabilites] = useState([])
    const [openingHoursAdded, setOpeningHoursAdded] = useState(false)
    const [openingHoursIndex, setOpeningHoursIndex] = useState(null)
    const [countOfIncludedOpeningHours, setCountOfIncludedOpeningHours] = useState(null)
    const [changeAvailabilityAllowed, setChangeAvailabilityAllowed] = useState(false)
    

    useEffect(() => {
        loadOpeningHoursAvailabilities()
        if(props.userStore != undefined) {
            if(hasRight(props.userStore, [rightName])){
                setChangeAvailabilityAllowed(true)
            }else if(props.selected != undefined && props.selected.id == props.userStore.userID && hasRight(props.userStore, [rightNameOwn])){
                setChangeAvailabilityAllowed(true)
            }else if(props.selected == undefined && hasRight(props.userStore, [rightNameOwn])){
                setChangeAvailabilityAllowed(true)
            }
        }
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
        const startDate = moment(startDateString, "DD.MM.yyyy HH:mm").toDate()
        const endOfSeriesAsDate = moment(endOfSeriesDateString, "DD.MM.yyyy HH:mm").toDate()

        var endDateTime = startDate
        endDateTime.setHours(date.getHours())
        endDateTime.setMinutes(date.getMinutes())
        endDateTime.setSeconds(0)

        var valideDate = false
        if(startDate.getFullYear() == date.getFullYear() &&
            startDate.getMonth() == date.getMonth() &&
            startDate.getDate() == date.getDate()) 
        {
            valideDate = true
        }

        if(valideDate) {
            setEndDateString(newDateString)
        }else {
            setEndDateString(endDateTime)
        }

        if(withSeriesEnd &&  endOfSeriesAsDate.getTime() < date.getTime()){
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

    
    //------------------------------------LOAD--------------------------------------
    const loadOpeningHoursAvailabilities = async () => {
        var data = []
        try {
            const response = await getOpeningHours();
            data = response.data.availabilities
        }catch (error) {
            console.log(Object.keys(error), error.message)
        }
        var dataWithoutId = []
        var singleAvailability = null
        data.map(availability =>{
            singleAvailability = {
                startDate: availability.startDate, 
                endDate: availability.endDate, 
                frequency: availability.frequency,
                rhythm: availability.rhythm,
                endOfSeries: availability.endOfSeries}

            if(availability.endOfSeries != null) {
                //check if endOfSeries < actual time
                if(moment(availability.endOfSeries, "DD.MM.yyyy HH:mm").toDate().getTime() > moment().toDate().getTime()) {
                    dataWithoutId.push(singleAvailability)
                }
            }else {// endOfSeries == null
                dataWithoutId.push(singleAvailability)
            }
        })
        setOpeningHoursAvailabilites(dataWithoutId)
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
        props.availabilities.map((singleAvailability, index) => {
            if(index == data.target.value) {
                if((index >= indexDifference)) { //the availabilities which are added, but not submitted   
                    //reduce opening hours
                    const reducedIndex = 1
                    if(index <= openingHoursIndex && index > openingHoursIndex - countOfIncludedOpeningHours){
                        counter -= reducedIndex
                        setCountOfIncludedOpeningHours(countOfIncludedOpeningHours - reducedIndex)
                        setOpeningHoursIndex(openingHoursIndex-reducedIndex)
                    }else  //reduce before opening hours
                    if(index <= openingHoursIndex - counter){
                        setOpeningHoursIndex(openingHoursIndex-reducedIndex)
                    }
                    props.availabilities.splice((index),1) // remove  at "index" and just remove "1" 
                    addedAvailabilities.splice((index-indexDifference),1)
                    props.updateAvailabilities(props.availabilities)

                }else { //the submitted availabilities
                    props.availabilities.map((singleAvailability, index) => {
                        if(index == data.target.value) {
                            singleAvailability.endOfSeries = moment().format("DD.MM.YYYY HH:mm").toString()
                        }
                    })
                    props.updateAvailabilities(props.availabilities)
                    props.editedAvailabilities(true)   
                }
            }
        })
    }


    //-----------------------------------Help-Functions------------------------------------------
    function setValidEndDateString(startDateString) {
        var validEndDate = moment(startDateString, "DD.MM.yyyy HH:mm").toDate()
        var startEndDifference = 5
        validEndDate.setMinutes(validEndDate.getMinutes() + startEndDifference)
        return moment(validEndDate).format("DD.MM.YYYY HH:mm").toString()
    }
    
    
    function validateDates(startDateString, endDateString, endOfSeriesString) {
        var validation = false
        var startDate = moment(startDateString, "DD.MM.yyyy HH:mm").toDate();
        var endDate = moment(endDateString, "DD.MM.yyyy HH:mm").toDate();
    
        if(startDate > endDate) {
            alert("Ungültiges Ende! Das Ende darf nicht vor dem Start sein!")
            return false
        }else if(startDate.getTime() == endDate.getTime()) {
            alert("Ungültiges Ende! Das Ende darf nicht gleich dem Start sein!")
            return false
        } else {
            validation = true
        }
    
        if(endOfSeriesString != null) {
            var endOfSeries = moment(endOfSeriesString, "DD.MM.yyyy HH:mm").toDate();
            if(endOfSeries.getTime() <= startDate.getTime() || endOfSeries.getTime() < endDate.getTime()) {
                alert("Ungültiges Serienende! Das Serienende darf nicht vor dem Star und vor dem Ende einer Verfügbarkeit sein!")
                return false
            }
        } else {
            validation = true
        }
        return validation;
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


    //needed to refresh the addesAvailabilities after submit
    useImperativeHandle(ref, () => ({
        submitted()  {
            setAddedAvailabilities([])
        }
    }))


    return (
        <React.Fragment>
            <Container>
                <Form.Row style={{ alignItems: "baseline" }}>
                <Form.Group as={Col} style={{textAlign: "right"}}>
                    {props.withOpeningHours && changeAvailabilityAllowed &&
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
            { changeAvailabilityAllowed &&
            <Form.Row>
                <Form.Label><h5>Erster verfügbarer Zeitraum</h5></Form.Label>
            </Form.Row>
            }
            {changeAvailabilityAllowed &&
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
                            dateFormat="dd.MM.yyyy / HH:mm"
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
                            dateFormat="dd.MM.yyyy / HH:mm"
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
                            {changeAvailabilityAllowed &&
                                <Button onClick={handleAdd}>Verfügbarkeit hinzufügen</Button>
                            }
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
                            handleCancleAvailability,
                            changeAvailabilityAllowed)
                        }
                    </tbody>
                </Table> 
            </Form.Row>
        </Container>
    </React.Fragment>
    )
})
export default Availability