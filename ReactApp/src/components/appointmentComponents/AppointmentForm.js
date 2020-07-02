import React, { useState, useEffect } from "react"
import { Container, Form, Col, Button } from "react-bootstrap"
import {Link} from 'react-router-dom';
import {setDate} from "../TimeComponents/TimeFunctions"
import {getWarningsAsString} from "../Warnings"
import {appointmentStatus, translateStatus} from "./AppointmentStatus"
import {
    deleteAppointment,
    testAppointment,
    updateCustomerIsWaiting,
    startAppointment,
    stopAppointment,
    testPreferredAppointment
} from "./AppointmentRequests"

var moment = require('moment'); 
const twoMinutes = 120000


export const handleDeleteAppointment = async (id, handleOnCancel = undefined, setException, setPreferredAppointment) => {
    const answer = confirm("Termin wirklich löschen? ")
    if(answer) {
        try{
            await deleteAppointment(id)
            .then(res => {
                if(res.headers.appointmentid != undefined && res.headers.starttime != undefined) {
                    setPreferredAppointment(res.headers.appointmentid, res.headers.starttime)
                }
                if (res.headers.exception) {
                    setException(res.headers.exception)
                }
            })
            .catch((error) => {
                if(error.response.headers.exception != undefined) {
                    setException(error.response.headers.exception, id)
                }
            })
        } catch (error){
        console.log(Object.keys(error), error.message)
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
    refreshData = null,                         //optional (HomePage)
    month = null,                               //optional (HomePage)
    year = null                                 //optional (HomePage)
    }) {
    //Editing
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
        } 
    }, [])


    const handleCustomerIsWaitingChange = () => {
            if(customerIsWaiting) {
                setCustomerIsWaiting(false) 
            } else {
                setCustomerIsWaiting(true) 
            }
            setEdited(true)
    }


    const handleSubmit = async () => {
        try{
            const res = await updateCustomerIsWaiting(selected.id, customerIsWaiting)
            if(res.headers.appointmentid != undefined && res.headers.starttime != undefined) {
                //panned start > now + (2 minutes = 120000)
                if(moment(plannedStarttime, "DD.MM.yyyy HH:mm").toDate().getTime() > (moment().toDate().getTime()+twoMinutes)) {
                    handlePreferredAppointmentChange(res.headers.appointmentid, res.headers.starttime)  
                }
                
            }
        } catch (error){
            console.log(Object.keys(error), error.message)
        }

        handleOnCancel()
    }

    
    const handleStart = async () =>{
        var data = true
        
        try{
            const response = await startAppointment(selected.id);
          } catch (error){
            console.log(Object.keys(error), error.message)
          }
        if(!data){
            alert("Termin kann nicht vorzeitig gestartet werden")
        }
        setActualStarttime(moment(new Date).format("DD.MM.YYYY / HH:mm").toString()) //set the actual time 

        //refresh Appointments
        if(refreshData != null) {
            refreshData(month, year) 
        }
    }


    const handleEnd = async() =>{
        const newEndtime = moment(new Date).format("DD.MM.YYYY / HH:mm").toString()
        const newStatus = appointmentStatus.done
        var updateData = {actualEndTime: newEndtime, status: newStatus}
        
        try{
            await stopAppointment(selected.id)
            .then(res => {
                if(res.headers.appointmentid != undefined && res.headers.starttime != undefined) {
                    handlePreferredAppointmentChange(res.headers.appointmentid, res.headers.starttime)
                }
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
    }


    const handleOnCancel = () => {
        //refresh Appointments
        if(refreshData != null) {
            refreshData(month, year) 
        }
        onCancel()
    }


    const handleDelete = () => {
        handleDeleteAppointment(
            selected.id, 
            handleOnCancel, 
            handleExceptionChange, 
            handlePreferredAppointmentChange)
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
            alert("Termin kann leider nicht vorgezogen werden")
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
                   text += customer.firstName + " " + customer.lastName 
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
                    }0
                })
            break;
        }
        return text
    }


    const getActualStarttime = () => {
        return actualStarttime
    }


    const rendertest = () => {
        console.log("---------Render-FORM------")
    }
   

    return (
        <div className="page">
            <Container>
                {rendertest()}
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
                                {/* (actualStarttime != null && status == AppointmentStatus.planned) &&
                                    <Button variant="secondary" onClick={handleUndoStart} style={{ marginLeft: "4px" }}>Zurücksetzen</Button> */
                                }
                                {(getActualStarttime() != null && actualEndtime == null) && 
                                    (moment(plannedStarttime, "DD.MM.yyyy HH:mm").toDate().getTime() < moment().toDate().getTime() &&
                                    moment(actualStarttime, "DD.MM.yyyy HH:mm").toDate().getTime() < moment().toDate().getTime()) &&
                                        <Button variant="primary" onClick={handleEnd} style={{ marginLeft: "4px" }}>Termin beenden</Button>
                                }
                                {getActualStarttime() == null && customerIsWaiting && status == appointmentStatus.planned &&
                                //panned start < now + (2 minutes = 120000)
                                moment(plannedStarttime, "DD.MM.yyyy HH:mm").toDate().getTime() < (moment().toDate().getTime()+twoMinutes) &&
                                    <Button variant="primary" onClick={handleStart} style={{ marginLeft: "4px" }}>Termin starten</Button>
                                }
                            </div>
                        </Form.Group>
                    </Form.Row>
                    <hr/>
                    {warnings.length != 0 &&
                        <Form.Row>
                            <Form.Group as={Col} md="9">
                                <Form.Label>Konflikte:</Form.Label>
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
                                    <Link to={`/warning/${warnings[0]}`}>
                                        <Button variant="success" style={{ marginLeft: "10px" }}>Beheben</Button>
                                    </Link>
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
                        {(status == appointmentStatus.planned && actualStarttime == null) && 
                            <Button variant="danger" onClick={handleDelete} style={{marginLeft: "3px"}}>Termin löschen</Button> 
                        }
                        {(status == appointmentStatus.planned && actualStarttime == null) &&
                            <Link to={`/booking/${selected.id}`}>
                                <Button variant="primary"style={{marginLeft: "3px"}}>Zur Bearbeitung</Button>
                            </Link> 
                        }
                        {(edited && actualStarttime == null) ?
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
