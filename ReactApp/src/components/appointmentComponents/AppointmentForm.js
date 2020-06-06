import React, { useState, useEffect } from "react"
//import moment from "moment"
import { Container, Form, Col, Row, Button } from "react-bootstrap"
import "react-big-calendar/lib/css/react-big-calendar.css"
import ObjectPicker from "../ObjectPicker"
import "../calendarComponents/BookingForm.css"
import DatePicker from "react-datepicker"

import {setDate} from "../administrationComponents/TimeComponents/TimeFunctions"

var moment = require('moment'); 


function AppointmentForm({onCancel, edit, selected, selectedUser}) {
    //Editing
    const [edited, setEdited] = useState(false)

    const [calendarEvents, setCalendarEvents] = useState([])
    
    const [appointments, setAppointments] = useState([])

    const [procedure, setProcedure] = useState(null)
    const [bookedEmployees, setBookedEmployees] = useState([])
    const [bookedResources, setBookedResources] = useState([])
    const [customer, setCustomer] = useState(null) 
    const [plannedStarttime, setPlannedStarttime] = useState(setDate())
    const [plannedEndtime, setPlannedEndtime] = useState(setDate())
    const [actualStarttime, setActualStarttime] = useState(null)
    const [actualEndtime, setActualEndtime] = useState(null)
    const [description, setDescription] = useState(null )
    const [status, setStatus] = useState("null")
    const [warning, setWarning] = useState(null)

    const handleBookedEmployeesChange = data => {setBookedEmployees(data); setEdited(true)}
    const handleBookedResourcesChange = data => {setBookedResources(data); setEdited(true)}


    useEffect(() => { 
        if(edit) {
            setProcedure(selected.bookedProcedure)
            setCustomer(selected.bookedCustomer)
            setPlannedStarttime(selected.plannedStarttime)
            setPlannedEndtime(selected.plannedEndtime)
            setActualStarttime(selected.actualStarttime)
            setActualEndtime(selected.actualEndTime)
            setDescription(selected.description)
            setStatus(selected.status)
            setWarning(selected.warning)   
            setBookedEmployees(selected.bookedEmployees)     
            setBookedResources(selected.bookedResources)
        } 
    }, [])

    //moment(date).format("DD.MM.YYYY HH:mm").toString()
    const handleStart = () =>{
        setActualStarttime(moment(new Date).format("DD.MM.YYYY / HH:mm").toString()) //set the actual time 
        setStatus("active")
    }

    const handleEnd = () =>{
        setActualEndtime(moment(new Date).format("DD.MM.YYYY / HH:mm").toString()) 
        setStatus("done")
        //Call to change Status
    }

    const handleUndoStart = () => {
        setActualStarttime(null)
        setStatus("planned")
    }

    const handleAprove = () =>{
        setStatus("planned")
        //Call to change Status
    }

    const handleDeny = () =>{
        const answer = confirm("Termin wirklich ablehnen? Dieser wird anschließend gelöscht! ")
        if(answer) {
            setStatus("deleted") 
            //Call to change Status
        }
    }

    const handleActive = () =>{
        setStatus("planned") 
        //Call to change Status
    }

    const handleDeactivate = () =>{
        setStatus("deactivated") 
        //Call to change Status
    }

    const handleCancele = () => {
        const answer = confirm("Termin wirklich stornieren? Dieser Vorgang kann nicht rückgängig gemacht werden!")
        if(answer) {
        setStatus("cancelled") 
        //Call to change Status
        }
    }

    const handleDelete = () => {
        const answer = confirm("Termin wirklich löschen? ")
        if(answer) {
            setStatus("deleted") 
            //Call to change Status
        }
    }

    const translateStatus = () => {
        switch(status) {
        case "active":
            return "Laufend"
        break;
        case "planned":
            return "Ausstehend"
        break;
        case "done":
            return "Erledigt"
        break;
        case "created":
            return "Buchung ausstehend"
        break;
        case "cancelled":
            return "Storniert"
        break;
        case "deactivated":
            return "Deaktiviert"
        break;
        case "deleted":
            return "Gelöscht"
        break;
        default: return "UNDIFINED"
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
                    }
                })
            break;
        }
        return text
    }

   const rendertest = () => {
       console.log("render")
       //console.log(status)
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
                                   style={  (status == "deleted" || status == "cancelled") ? {background: "white", color: "red"}:
                                            status == "done" ? {background: "white", color: "#009938"}: {background: "white"}}
                                   name="status"
                                   type="text"
                                   value={translateStatus()} 
                                />
                        </Form.Group>
                    </Form.Row>
                    <Form.Row>
                        <Form.Group as={Col} md="12">
                            <div style={{ textAlign: "right" }}>
                                {status == "planned" &&
                                    <Button variant="danger" onClick={handleCancele} style={{marginLeft: "4px"}}>Stornieren</Button>
                                }
                                {status == "planned" &&
                                    <Button variant="warning" onClick={handleDeactivate} style={{marginLeft: "4px"}}>Deaktivieren</Button>
                                }
                                
                                {status == "created" &&
                                    <Button variant="danger" onClick={handleDeny} style={{ marginLeft: "4px" }}>Ablehnen</Button>
                                }
                                {status == "active" &&
                                    <Button variant="secondary" onClick={handleUndoStart} style={{ marginLeft: "4px" }}>Zurücksetzen</Button>
                                }
                                {((actualStarttime != null && actualEndtime == null) && 
                                    (moment(actualStarttime, "DD.MM.yyyy HH:mm").toDate().getTime() < moment().toDate().getTime())) ?
                                        <Button variant="success" onClick={handleEnd} style={{ marginLeft: "4px" }}>Beenden</Button>:
                                    status == "planned" ?
                                        <Button variant="success" onClick={handleStart} style={{ marginLeft: "4px" }}>Starten</Button>:
                                    status == "deactivated" ?
                                        <Button variant="success" onClick={handleActive} style={{ marginLeft: "4px" }}>Aktivieren</Button>:
                                    status == "created" ? 
                                        <Button variant="success" onClick={handleAprove} style={{ marginLeft: "4px" }}>Genehmigen</Button>:
                                    null
                                }
                            </div>
                        </Form.Group>
                    <hr />
                    </Form.Row>
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
                           {/*  <ObjectPicker
                            initial={bookedEmployees}
                            DbObject="employee"
                            setState={handleBookedEmployeesChange} />    */}
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
                            {/* <ObjectPicker
                                initial={bookedResources}
                                DbObject="resource"
                                setState={handleBookedResourcesChange} /> */}
                        </Form.Group>
                    </Form.Row >
                    <hr />
                    <div style={{ textAlign: "right" }}>
                        <Button variant="secondary" onClick={onCancel}>Abbrechen</Button>
                        {(status != null && (status == "planned" || status == "cancelled" || status == "deactivated")) && 
                            <Button variant="danger" onClick={handleDelete} style={{marginLeft: "3px"}}>löschen</Button> 
                        }
                        <Button variant="primary"style={{marginLeft: "3px"}}>Mitarbeiter/Ressourcen ändern</Button>
                        <Button variant="primary" style={{marginLeft: "3px"}} >Zeit verschieben</Button>
                    </div>
                    <hr />
                </Form>
            </Container>
        </div >
    )
}

export default AppointmentForm