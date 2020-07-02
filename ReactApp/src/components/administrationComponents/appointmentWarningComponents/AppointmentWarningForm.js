import React from "react"
import { useHistory } from "react-router-dom";
import { Container, Form, Col, Button} from "react-bootstrap"
import {getWarningsAsString} from "../../Warnings"
import {handleDeleteAppointment} from "../../appointmentComponents/AppointmentForm"

var moment = require('moment'); 


function AppointmentWarningForm({
    onCancel, 
    edit, 
    selected,
    handleExceptionChange,
    handlePreferredAppointmentChange}) {

    let history = useHistory();


    const rebookingAppointments = () => {
        history.push(`/booking/${selected.id}/`)
    }


    const handleDelete = () => {
        handleDeleteAppointment(
            selected.id,
            onCancel, 
            handleExceptionChange, 
            handlePreferredAppointmentChange)
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
                       <h4>Wie sollen die Konflikte behandelt werden?</h4>
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