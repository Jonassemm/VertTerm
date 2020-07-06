//author: Patrick Venturini
import React, { useState, useEffect } from "react"
import { Container, Form, Col, Button } from "react-bootstrap"
import {Link} from 'react-router-dom';
import {setDate} from "../TimeComponents/TimeFunctions"
import {getWarningsAsString} from "../Warnings"
import { hasRight } from "../../auth"
import {ownAppointmentRights, appointmentRights} from "../Rights"
import {appointmentStatus, translateStatus} from "./AppointmentStatus"
import {
    deleteAppointment,
    testAppointment,
    updateCustomerIsWaiting,
    startAppointment,
    stopAppointment,
    testPreferredAppointment,
    isBlocker,
    deleteBlocker
} from "./AppointmentRequests"

var moment = require('moment'); 
const twoMinutes = 120000


export const handleDeleteAppointment = async (id, 
    handleOnCancel = undefined, 
    setException, 
    setPreferredAppointment, 
    userStore) => {

    var answer = false
    var blockerSelected = false
    try{
        const response = await isBlocker(id)
        blockerSelected = response.data
    }catch (error){
        console.log(Object.keys(error), error.message)
    }

    if(blockerSelected){
        answer = confirm("Blocker wirklich löschen? ")
    }else{
        answer = confirm("Termin wirklich löschen? ")
    }

    if(answer) {
        if(blockerSelected){
            try{
                await deleteBlocker(id)
                userStore.setMessage("Blocker erfolgreich gelöscht!")
            }catch (error){
                console.log(Object.keys(error), error.message)
            }
        }else {//is appointment
            try{
                await deleteAppointment(id)
                .then(res => {
                    if(res.headers.appointmentid != undefined && res.headers.starttime != undefined) {
                        setPreferredAppointment(res.headers.appointmentid, res.headers.starttime)
                    }
                    if (res.headers.exception) {
                        setException(res.headers.exception)
                    }
                    userStore.setMessage("Termin erfolgreich gelöscht!")
                })
                .catch((error) => {
                    if(error.response.headers.exception != undefined) {
                        setException(error.response.headers.exception, id)
                    }
                })
            } catch (error){
                console.log(Object.keys(error), error.message)
            }
        }

        //only necessary for rerender calendar
        if(handleOnCancel!=undefined){
            handleOnCancel()
        }
    }
}


function AppointmentForm({
    onCancel,
    edit, 
    selected, 
    handleExceptionChange,
    handlePreferredAppointmentChange,
    userStore,
    refreshData = null,                         //optional (for CalendarPage)
    month = null,                               //optional (for CalendarPage)
    year = null                                 //optional (for CalendarPage)
    }) {
    const rightNameOwn = ownAppointmentRights[1] //write right
    const rightName = appointmentRights[1] //write right
    const [edited, setEdited] = useState(false)
    const [procedure, setProcedure] = useState(null)
    const [bookedEmployees, setBookedEmployees] = useState([])
    const [bookedResources, setBookedResources] = useState([])
    const [customer, setCustomer] = useState(null) 
    const [plannedStarttime, setPlannedStarttime] = useState(setDate())
    const [plannedEndtime, setPlannedEndtime] = useState(setDate())
    const [actualStarttime, setActualStarttime] = useState(null)
    const [actualEndtime, setActualEndtime] = useState(null)
    const [description, setDescription] = useState(null )
    const [status, setStatus] = useState("null") //string will be overwritten but necessary for first rendering
    const [warnings, setWarnings] = useState([])
    const [customerIsWaiting, setCustomerIsWaiting] = useState(false)
    const [ownAppointmentView, setOwnAppointmentView] = useState(false)
    

    useEffect(() => { 
        if(edit) {
            setProcedure(selected.bookedProcedure)
            setCustomer(selected.bookedCustomer)
            setPlannedStarttime(selected.plannedStarttime)
            setPlannedEndtime(selected.plannedEndtime)
            setActualStarttime(selected.actualStarttime)
            setActualEndtime(selected.actualEndtime)
            setDescription(selected.description)
            setStatus(selected.status)
            setWarnings(selected.warnings)   
            setBookedEmployees(selected.bookedEmployees)     
            setBookedResources(selected.bookedResources)
            setCustomerIsWaiting(selected.customerIsWaiting)
            //check if the selected is an appointment of the logged in user 
            if(selected.bookedCustomer != null && selected.bookedCustomer.id == userStore.userID){
                setOwnAppointmentView(true)
            }
            if(selected.bookedEmployees.length > 0){
                selected.bookedEmployees.map(employee => {
                    if(employee.id == userStore.userID){
                        setOwnAppointmentView(true)
                    }
                })
            }
        } 
    }, [])


    const handleCustomerIsWaitingChange = () => {
        if(checkRights()){
            if(customerIsWaiting) {
                setCustomerIsWaiting(false) 
            } else {
                setCustomerIsWaiting(true) 
            }
            setEdited(true)
        }
    }


    const handleSubmit = async () => {
        if(checkRights()){
            try{
                const res = await updateCustomerIsWaiting(selected.id, customerIsWaiting)
                if(customerIsWaiting){
                    userStore.setMessage("Kunde wartet vor Ort!")
                }else{
                    userStore.setMessage("Kunde nicht vor Ort!")
                }
                if(res.headers.appointmentid != undefined && res.headers.starttime != undefined) {
                    //panned start > now + (2 minutes = 120000)
                    if(moment(plannedStarttime, "DD.MM.yyyy HH:mm").toDate().getTime() > (moment().toDate().getTime()+twoMinutes)) {
                        handlePreferredAppointmentChange(res.headers.appointmentid, res.headers.starttime)  
                    }
                }
            } catch (error){
                console.log(Object.keys(error), error.message)
            }
        }else {//no right -> submit not allowed 
            noRights()
        }

        handleOnCancel()
    }

    
    const handleStart = async () =>{
        var data = true
        if(checkRights()){
            try{
                const response = await startAppointment(selected.id);
                userStore.setMessage("Termin erfolgreich gestartet!")
            } catch (error){
                console.log(Object.keys(error), error.message)
            }
            if(!data){
                userStore.setWarningMessage("Termin kann nicht vorzeitig gestartet werden")
            }
            setActualStarttime(moment(new Date).format("DD.MM.YYYY / HH:mm").toString()) //set the actual time 

            //refresh Appointments
            if(refreshData != null) {
                refreshData(month, year) 
            }
        }else {//no right -> submit not allowed 
            noRights()
        }
    }


    const handleEnd = async() =>{
        const newEndtime = moment(new Date).format("DD.MM.YYYY / HH:mm").toString()
        const newStatus = appointmentStatus.done
        if(checkRights()){
            try{
                await stopAppointment(selected.id)
                .then(res => {
                    if(res.headers.appointmentid != undefined && res.headers.starttime != undefined) {
                        handlePreferredAppointmentChange(res.headers.appointmentid, res.headers.starttime)
                    }
                    userStore.setMessage("Termin erfolgreich beendet!")
                })
            } catch (error){
            console.log(Object.keys(error), error.message)
            }
            setActualEndtime(newEndtime) 
            setStatus(newStatus)

            //refresh Appointments
            if(refreshData != null) {
                refreshData(month, year) 
            }
        }else {//no right -> submit not allowed 
            noRights()
        }
    }


    const handleOnCancel = () => {
        //refresh Appointments
        if(refreshData != null) {
            refreshData(month, year) 
        }
        onCancel()
    }


    const handleDelete = () => {
        if(checkRights()){
            handleDeleteAppointment(
                selected.id, 
                handleOnCancel, 
                handleExceptionChange, 
                handlePreferredAppointmentChange,
                userStore)
        }else {//no right -> submit not allowed 
            noRights()
        }
    }


    const handleTest = async() => {
        var data = []
        try{
            const response = await testAppointment(selected.id)
            data = response.data
        } catch (error){
        console.log(Object.keys(error), error.message)
        }

        if(JSON.stringify(data)!=JSON.stringify(warnings)){
            setWarnings(data)
            if(refreshData != null) {
                refreshData(month, year) 
            }
        }
    }


    //----------------------------------Help-Functions----------------------------------
    const checkRights = () => {
        var rightExists = hasRight(userStore, [rightName])
        if(ownAppointmentView && hasRight(userStore, [rightNameOwn])){
        rightExists = true
        }
        return rightExists
    }


    const noRights = () => {
        if(ownAppointmentView){
            userStore.setWarningMessage("Ihnen fehlt das Recht:\n"+ rightNameOwn)
        }else {
            userStore.setWarningMessage("Ihnen fehlt das Recht:\n"+ rightName)
        }
    }


    const preferAppointment = async () => {
        var start = null
        var id = null
        try{
            const response = await testPreferredAppointment(selected.id)
            start = response.headers.starttime
            id = response.headers.appointmentid
        } catch (error){
        console.log(Object.keys(error), error.message)
        }
        if(start != null && id != null) {
            handlePreferredAppointmentChange(id, start)
        }else{
            userStore.setInfoMessage("Termin kann leider nicht vorgezogen werden")
        }
    }


    const getBookedElements = (type) => {
        var text = ""
        switch(type) {
            case "procedure":
                if(procedure != null) {
                   text += procedure.name
                }
            break;
            case "customer":
                if(customer != null) {
                    if(customer.firstName != null && customer.lastName != null){
                        text += customer.firstName + " " + customer.lastName 
                    }else {
                        text = "anonymer Kunder"
                    }
                }
            break;
            case "employee":
                bookedEmployees.map((item, index) => {
                    if(index == 0) {
                        text += item.firstName + " " + item.lastName
                    }else {
                      text += "; " + item.firstName + " " + item.lastName  
                    }
                })
            break;
            case "resource":
                bookedResources.map((item, index) => {
                    if(index == 0) {
                        text += item.name 
                    } else {
                       text += "; " + item.name 
                    }
                })
            break;
        }
        return text
    }


    const getActualStarttime = () => {
        return actualStarttime
    }


    return (
        <div className="page">
            <Container>
                <Form>   
                    <Form.Row>
                        <Form.Group as={Col} md="4" >
                            <Form.Label>Start:</Form.Label> <br/>
                                <Form.Control
                                    readOnly
                                    style={{background: "white"}}
                                    name="plannedEndtime"
                                    type="text"
                                    value={moment(moment(plannedStarttime, "DD.MM.yyyy HH:mm").toDate()).format("DD.MM.YYYY / HH:mm").toString() + " Uhr"} 
                                />
                        </Form.Group>
                        <Form.Group as={Col} md="4" >
                            <Form.Label>Ende:</Form.Label> <br/>
                                <Form.Control
                                    readOnly
                                    style={{background: "white"}}
                                    name="plannedEndtime"
                                    type="text"
                                    value={moment(moment(plannedEndtime, "DD.MM.yyyy HH:mm").toDate()).format("DD.MM.YYYY / HH:mm").toString() + " Uhr"} 
                                />
                        </Form.Group>
                        <Form.Group as={Col} md="1" > </Form.Group>
                        <Form.Group as={Col} md="3" >
                        <Form.Label>Status:</Form.Label> <br/>
                                <Form.Control
                                   readOnly
                                   style={  status == appointmentStatus.deleted ? {background: "white", color: "red"}:
                                            status == appointmentStatus.done ? {background: "white", color: "#009938"}: {background: "white"}}
                                   name="status"
                                   type="text"
                                   value={translateStatus(status)} 
                                />
                        </Form.Group>
                    </Form.Row>
                    <Form.Row>
                        <Form.Group as={Col} md="4"> 
                        {status == appointmentStatus.planned ? 
                            actualStarttime == null ?
                                <Form.Check
                                    id="switchCustomerIsWaiting"
                                    type="switch"
                                    name="customerIsWaiting"
                                    onChange={handleCustomerIsWaitingChange}
                                    checked={customerIsWaiting}
                                    label={"Kunde vor Ort und wartet"}
                                    style={{marginBottom: "15px"}}
                                />:
                                <div style={{color: "red", fontWeight: "bold"}}>Termin läuft gerade!</div>:
                            null
                        }
                        </Form.Group>
                        <Form.Group as={Col} md="4">
                            <div style={{ textAlign: "left" }}>     
                            {selected.customerIsWaiting &&
                               <Button variant="primary" onClick={preferAppointment} style={{ marginLeft: "4px" }}>Termin vorziehbar?</Button>  
                            }    
                                  
                            </div>
                        </Form.Group>
                        <Form.Group as={Col} md="4">
                            
                            <div style={{ textAlign: "right" }}>         
                                {(getActualStarttime() != null && actualEndtime == null) &&
                                    (moment(plannedStarttime, "DD.MM.yyyy HH:mm").toDate().getTime() < moment().toDate().getTime() &&
                                    moment(actualStarttime, "DD.MM.yyyy HH:mm").toDate().getTime() < moment().toDate().getTime()) &&
                                    checkRights() &&
                                        <Button variant="primary" onClick={handleEnd} style={{ marginLeft: "4px" }}>Termin beenden</Button>
                                }
                                {getActualStarttime() == null && customerIsWaiting && status == appointmentStatus.planned &&
                                //panned start < now + (2 minutes = 120000)
                                moment(plannedStarttime, "DD.MM.yyyy HH:mm").toDate().getTime() < (moment().toDate().getTime()+twoMinutes) &&
                                checkRights() &&
                                    <Button variant="primary" onClick={handleStart} style={{ marginLeft: "4px" }}>Termin starten</Button>
                                }
                            </div>
                        </Form.Group>
                    </Form.Row>
                    <hr/>
                    {warnings.length != 0 &&
                        <Form.Row>
                            <Form.Group as={Col} md="9">
                                <Form.Label>Konflikte mit:</Form.Label>
                                <Form.Control
                                    readOnly
                                    style={{background: "white", color: "red", fontWeight: "bold"}}
                                    name="warnings"
                                    type="text"
                                    value={getWarningsAsString(warnings)} 
                                />
                            </Form.Group>  
                            <Form.Group as={Col} md="3">
                                <div style={{textAlign: "bottom", marginTop: "32px"}}>
                                    <Button variant="primary" onClick={handleTest} style={{ marginLeft: "0px" }}>Prüfen</Button>
                                    {checkRights() &&
                                        <Link to={`/warning/${warnings[0]}`}>
                                            <Button variant="success" style={{ marginLeft: "10px" }}>Lösen</Button>
                                        </Link>
                                    }
                                    
                                </div>
                            </Form.Group>   
                        </Form.Row>
                    }
                    <hr />   
                    <Form.Row>
                        <Form.Group as={Col} md="4"  style={{ textAlign: "bottom" }}>
                            <Form.Label>Beschreibung:</Form.Label>
                        </Form.Group>
                        <Form.Group as={Col} md="8" >
                            <Form.Control
                                readOnly
                                style={{background: "white"}}
                                name="customer"
                                type="text"
                                value={description} 
                            />
                        </Form.Group>
                    </Form.Row> 
                    <Form.Row>
                        <Form.Group as={Col} md="4"  style={{ textAlign: "bottom" }}>
                            <Form.Label>Kunde:</Form.Label>
                        </Form.Group>
                        <Form.Group as={Col} md="8" >
                            <Form.Control
                                readOnly
                                style={{background: "white"}}
                                name="customer"
                                type="text"
                                value={getBookedElements("customer")} 
                            />
                        </Form.Group>
                    </Form.Row>
                    <Form.Row>
                        <Form.Group as={Col} md="4"style={{ textAlign: "bottom" }}>
                            <Form.Label>Prozedur:</Form.Label>
                        </Form.Group>
                        <Form.Group as={Col} md="8">
                        <Form.Control
                                readOnly
                                style={{background: "white"}}
                                name="procedure"
                                type="text"
                                value={getBookedElements("procedure")} 
                            />
                        </Form.Group>
                    </Form.Row>
                    <hr />
                    <Form.Row>
                        <Form.Group as={Col} md="4"  style={{ textAlign: "bottom" }}>
                            <Form.Label>Gebuchte Mitarbeiter</Form.Label>
                        </Form.Group>
                        <Form.Group as={Col} md="8">
                            <Form.Control
                                readOnly
                                style={{background: "white"}}
                                name="bookedEmployees"
                                type="text"
                                value={getBookedElements("employee")} 
                            />
                        </Form.Group>
                    </Form.Row>
                    <Form.Row >
                        <Form.Group as={Col} md="4"  style={{ textAlign: "bottom" }}>
                            <Form.Label>Gebuchte Ressorucen</Form.Label>
                        </Form.Group>
                        <Form.Group as={Col} md="8">
                            <Form.Control
                                readOnly
                                style={{background: "white"}}
                                name="bookedResources"
                                type="text"
                                value={getBookedElements("resource")} 
                            />
                        </Form.Group>
                    </Form.Row >
                    <hr />
                    <div style={{ textAlign: "right" }}>
                        {(edited && actualStarttime == null) &&
                            <Button variant="secondary" onClick={onCancel}>Abbrechen</Button>
                        }
                        {(status == appointmentStatus.planned && actualStarttime == null) && checkRights() &&
                            <Button variant="danger" onClick={handleDelete} style={{marginLeft: "3px"}}>Termin löschen</Button> 
                        }
                        {(status == appointmentStatus.planned && actualStarttime == null) && checkRights() &&
                            <Link to={`/booking/${selected.id}`}>
                                <Button variant="primary" style={{marginLeft: "3px"}}>Zur Bearbeitung</Button>
                            </Link>
                        }
                        {(edited && actualStarttime == null) ?
                            checkRights() &&
                            <Button variant="success" onClick={handleSubmit} style={{ marginLeft: "4px" }}>Übernehmen</Button>:
                            <Button variant="success" onClick={onCancel} style={{ marginLeft: "4px" }}>OK</Button>
                        }
                        
                    </div>
                    <hr />
                </Form>
            </Container>
        </div >
    )
}

export default AppointmentForm
