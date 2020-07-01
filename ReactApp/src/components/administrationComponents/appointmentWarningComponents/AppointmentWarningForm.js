import React, { useState, useEffect } from "react"
import {Link} from 'react-router-dom';
import { Container, Form, Col, Row, Modal, Button } from "react-bootstrap"
import { useHistory } from "react-router-dom";
import {getTranslatedWarning, getWarningsAsString} from "../../Warnings"
import {handleDeleteAppointment} from "../../appointmentComponents/AppointmentForm"
import {ExceptionModal} from "../../ExceptionModal"

import {deleteAppointment} from "../../appointmentComponents/AppointmentRequests"

var moment = require('moment'); 


function AppointmentWarningForm({onCancel, edit, selected}) {

    //exception needs overriding
    const [exception, setException] = useState(null)
    const [showExceptionModal, setShowExceptionModal] = useState(false)
    //preferable appointment
    const [preferredAppointmentId, setPreferredAppointmentId] = useState(null)
    const [preferredAppointmentStarttime, setPreferredAppointmentStarttime] = useState(null)
    const [showPreferredAppointmentModal, setShowPreferredAppointmentModal] = useState(false)

    //let history = useHistory("/booking");
    let history = useHistory();

    useEffect(() => { 
        //loadAppointmentGroup()
    }, [])


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

    const handleExceptionChange = (newException) => {
        setException(newException)
        setShowExceptionModal(true)
    }

    const handleOverrideDelete = async () => {
        try{
            await deleteOverrideAppointment(selected.id);
        } catch (error){
            console.log(Object.keys(error), error.message)
        }
        onCancel()
    }

    //set information of preferable appointment
    const handlePreferredAppointmentChange = (id, starttime) =>{
        setPreferredAppointmentId(id)
        setPreferredAppointmentStarttime(starttime)
        setShowPreferredAppointmentModal(true)
    }

    const rendertest = () => {
        console.log("---------Render-FORM------")
    }
   
    return (
        <div className="page">
            <Container>
                {rendertest()}
                {exception != null &&
                <ExceptionModal //modal for deleting appointments
                    showExceptionModal={showExceptionModal} 
                    setShowExceptionModal={setShowExceptionModal} 
                    overrideSubmit={handleOverrideDelete}
                    exception={exception}
                    overrideText="Trotzdem löschen"
                />
                }
                <Modal show={showPreferredAppointmentModal} onHide={() => setShowPreferredAppointmentModal(false)}>
                    <Modal.Header>
                        <Modal.Title>
                            Es ist möglich einen Termin zeitlich vorzuziehen!
                        </Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        Den betreffende Termin sehen Sie bei der Terminumbuchug.
                    </Modal.Body>
                    <Modal.Footer>
                        <div style={{ textAlign: "right" }}>
                            <Button onClick={() => setShowPreferredAppointmentModal(false)} variant="secondary">Nicht vorziehen</Button>
                            <Link to={`/buchung/${preferredAppointmentId}/${preferredAppointmentStarttime}`}>
                                <Button variant="success" style={{ marginLeft: "10px" }}>Terminumbuchung</Button>
                            </Link>
                        </div>
                    </Modal.Footer>
                </Modal>
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