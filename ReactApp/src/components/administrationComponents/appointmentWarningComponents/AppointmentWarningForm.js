//author: Patrick Venturini
import React from "react"
import { useHistory } from "react-router-dom";
import { Container, Form, Col, Button} from "react-bootstrap"
import {getWarningsAsString} from "../../Warnings"
import {handleDeleteAppointment, getBookedElements} from "../../appointmentComponents/AppointmentForm"


function AppointmentWarningForm({
    onCancel, 
    edit, 
    selected,
    handleExceptionChange,
    handlePreferredAppointmentChange,
    userStore}) {

    let history = useHistory();


    const handleDelete = () => {
        handleDeleteAppointment(
            selected.id,
            onCancel, 
            handleExceptionChange, 
            handlePreferredAppointmentChange,
            userStore)
    }


    const rebookingAppointments = () => {
        history.push(`/booking/${selected.id}/`)
    }

   
    return (
        <div className="page">
            <Container>
                <Form>   
                    <Form.Row>
                       <h3>Behandlung der Terminkonflikte</h3>
                    </Form.Row>
                    <hr/>
                    <Form.Row>
                        <Form.Group as={Col} md="12">
                            <Form.Label>Konflikte mit:</Form.Label>
                            <Form.Control
                                readOnly
                                style={{background: "white", color: "red", fontWeight: "bold"}}
                                name="warnings"
                                type="text"
                                value={getWarningsAsString(selected.warnings)} 
                            />
                        </Form.Group>
                    </Form.Row>
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
                                value={selected.description} 
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
                                value={getBookedElements(selected, "customer")} 
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
                                value={getBookedElements(selected, "procedure")} 
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
                                value={getBookedElements(selected, "employee")} 
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
                                value={getBookedElements(selected, "resource")} 
                            />
                        </Form.Group>
                    </Form.Row >
                    <hr/>
                    <div style={{ textAlign: "right" }}>
                        <Button variant="secondary" onClick={onCancel} style={{ marginLeft: "10px" }}>Abbrechen</Button>
                        <Button variant="danger" onClick={handleDelete} style={{ marginLeft: "10px" }}>Termin löschen</Button>
                        <Button variant="success" onClick={rebookingAppointments} style={{ marginLeft: "10px" }}>Zur Umbuchung</Button>
                    </div>
                </Form>
            </Container>
        </div >
    )
}
export default AppointmentWarningForm