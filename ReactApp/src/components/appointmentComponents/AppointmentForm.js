import React, { useState, useEffect } from "react"
import { Container, Form, Col, Row, Button } from "react-bootstrap"
import {Link} from 'react-router-dom';
import {setDate} from "../TimeComponents/TimeFunctions"
import {
    deleteAppointment,
    getAllAppointments,
    getGroupOfAppointment,
    updateCustomerIsWaiting
} from "../appointmentComponents/AppointmentRequests"

var moment = require('moment'); 


function AppointmentForm({onCancel, edit, selected, selectedUser}) {
    //Editing
    const [edited, setEdited] = useState(false)

    const AppointmentStatus ={
        planned: "planned",
        done: "done",
        deleted: "deleted"
    }

    const [calendarEvents, setCalendarEvents] = useState([])
    
    const [appointments, setAppointments] = useState([])
    const [appointmentGroupID, setAppointmentGroupID] = useState(null)

    const [procedure, setProcedure] = useState(null)
    const [bookedEmployees, setBookedEmployees] = useState([])
    const [bookedResources, setBookedResources] = useState([])
    const [customer, setCustomer] = useState(null) 
    const [plannedStarttime, setPlannedStarttime] = useState(setDate())
    const [plannedEndtime, setPlannedEndtime] = useState(setDate())
    const [actualStarttime, setActualStarttime] = useState(null)
    const [actualEndtime, setActualEndtime] = useState(null)
    const [description, setDescription] = useState(null )
    const [status, setStatus] = useState("null") //string will be overwrite but necessary for first rendering
    const [warning, setWarning] = useState(null)
    const [customerIsWaiting, setCustomerIsWaiting] = useState(false)
    const [responseOfEarlyStart, setResponseOfEarlyStart] = useState(false)

    const handleCustomerIsWaitingChange = () => {
        loadAppointmentGroup()
            if(customerIsWaiting) {
                setCustomerIsWaiting(false) 
            } else {
                setCustomerIsWaiting(true) 
            }
            setEdited(true)
    }

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


    const loadAppointmentGroup = async () => {
        var data
        try{
            const response = await getGroupOfAppointment(selected.id)
            data = response.data
        } catch (error){
            console.log(Object.keys(error), error.message)
        }
        setAppointmentGroupID(data.id)
    }


    const handleSubmit = async () => {
        try{
            const response = await updateCustomerIsWaiting(selected.id, customerIsWaiting);
          } catch (error){
            console.log(Object.keys(error), error.message)
          }
    }

    
    const handleStart = async () =>{
        var data = true
        try{
            /* const response = await startAppointment();
            data = response.data; */
          } catch (error){
            console.log(Object.keys(error), error.message)
          }
        if(!data){
            alert("Termin kann nicht vorzeitig gestartet werden")
        }

        setActualStarttime(moment(new Date).format("DD.MM.YYYY / HH:mm").toString()) //set the actual time 
        //onCancel()
    }

    const handleEnd = async() =>{

        const newEndtime = moment(new Date).format("DD.MM.YYYY / HH:mm").toString()
        const newStatus = AppointmentStatus.done
        var updateData = {actualEndTime: newEndtime, status: newStatus}
        
        try{
            /*await updateAppointment();*/
          } catch (error){
            console.log(Object.keys(error), error.message)
          }

        setActualEndtime(newEndtime) 
        setStatus(newStatus)
        //Call to change Status
    }


    const handleDelete = async () => {
        const answer = confirm("Termin wirklich löschen? ")
        if(answer) {
            try{
                await deleteAppointment(selected.id)
              } catch (error){
                console.log(Object.keys(error), error.message)
              }
        }
        refreshEvents()
        onCancel()
    }

    async function refreshEvents(){
        const response = await getAllAppointments()
        const evts = response.data
        calendarStore.setCalendarEvents(evts)
    }

    const translateStatus = () => {
        switch(status) {
        case AppointmentStatus.planned:
            return "Gebucht"
        break;
        case AppointmentStatus.done:
            return "Erledigt"
        break;
        case AppointmentStatus.deleted:
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
                                   style={  status == AppointmentStatus.deleted ? {background: "white", color: "red"}:
                                            status == AppointmentStatus.done ? {background: "white", color: "#009938"}: {background: "white"}}
                                   name="status"
                                   type="text"
                                   value={translateStatus()} 
                                />
                        </Form.Group>
                    </Form.Row>
                    <Form.Row>
                        <Form.Group as={Col} md="4"> 
                        {status == AppointmentStatus.planned ? 
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
                        <Form.Group as={Col} md="8">
                            <div style={{ textAlign: "right" }}>         
                                {/* (actualStarttime != null && status == AppointmentStatus.planned) &&
                                    <Button variant="secondary" onClick={handleUndoStart} style={{ marginLeft: "4px" }}>Zurücksetzen</Button> */
                                }
                                {(actualStarttime != null && actualEndtime == null && 
                                moment(actualStarttime, "DD.MM.yyyy HH:mm").toDate().getTime() < moment().toDate().getTime()) ?
                                        <Button variant="success" onClick={handleEnd} style={{ marginLeft: "4px" }}>Termin beenden</Button>:
                                (customerIsWaiting && status == AppointmentStatus.planned) ?
                                        <Button variant="success" onClick={handleStart} style={{ marginLeft: "4px" }}>Termin starten</Button>:
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
                        {(edited && actualStarttime == null) &&
                            <Button variant="secondary" onClick={onCancel}>Abbrechen</Button>
                        }
                        {(status == AppointmentStatus.planned && actualStarttime == null) && 
                            <Button variant="danger" onClick={handleDelete} style={{marginLeft: "3px"}}>Termin löschen</Button> 
                        }
                        {(status == AppointmentStatus.planned && actualStarttime == null) &&
                          <Link to={`/booking/${selected.id}/${appointmentGroupID}`}>
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