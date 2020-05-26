import React, { useState, useEffect } from "react"
import { Calendar, momentLocalizer } from "react-big-calendar"
import moment from "moment"
import { Container, Form, Col, Row, Button } from "react-bootstrap"
import { observer } from "mobx-react"
import "react-big-calendar/lib/css/react-big-calendar.css"
import ObjectPicker from "../ObjectPicker"
import "./BookingForm.css"

const localizer = momentLocalizer(moment)

function BookingForm() {
    const [calendarEvents, setCalendarEvents] = useState([])
    const [selectedProcedures, setSelectedProcedures] = useState([])
    const [selectedCustomer, setSelectedCustomer] = useState({})
    const [selectedResource, setSelectedResource] = useState([]) //?
    const [selectedEmployee, setSelectedEmployee] = useState([])
    const [custom, setCustom] = useState(false)

    return (
        <div className="page">
            <Container>
                <Form>
                    <Row style={{ alignItems: "baseline" }}>
                        <Col>
                            <h1>Termin Buchen</h1>
                        </Col>
                        <Col className="selectType">
                            <Form.Check
                                type="switch"
                                id="custom-switch"
                                label="Benutzerdefinierte Eingabe"
                                value={custom || 0}
                                onChange={e => setCustom(!custom)}
                                checked={custom || false}
                            />
                        </Col>

                    </Row>
                    <hr />
                    <Form.Row>
                        <Form.Group as={Col} style={{ textAlign: "bottom" }}>
                            <Form.Label>Kunde:</Form.Label>
                        </Form.Group>
                        <Form.Group as={Col}>
                            <ObjectPicker
                                DbObject="customer"
                                setState={setSelectedCustomer} />
                        </Form.Group>
                    </Form.Row>
                    <Form.Row>
                        <Form.Group as={Col} style={{ textAlign: "bottom" }}>
                            <Form.Label>Prozeduren:</Form.Label>
                        </Form.Group>
                        <Form.Group as={Col}>
                            <ObjectPicker
                                DbObject="procedure"
                                setState={setSelectedProcedures}
                                multiple={true} />
                        </Form.Group>
                    </Form.Row>
                    {!custom && selectedProcedures.map(item => {
                        return (
                            <React.Fragment>
                                <hr />
                                <div className="parent">

                                    <div className="nameCol">
                                        <h4>{item.name}</h4>
                                    </div>
                                    <div className="nameCol wrap">
                                        <div className="box">
                                            <div className="innerboxleft">
                                                <p>Mitarbeiter:</p>
                                            </div>
                                            <div className="innerbox">
                                                <ObjectPicker
                                                    DbObject="employee"
                                                    setState={setSelectedEmployee}
                                                    multiple={true} />
                                            </div>
                                        </div>
                                        <div className="box">
                                            <div className="innerboxleft">
                                                <p>Ressource:</p>
                                            </div>
                                            <div className="innerbox">
                                                <ObjectPicker
                                                    DbObject="resource"
                                                    setState={setSelectedResource}
                                                    multiple={true} />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </React.Fragment>
                        )
                    })}

                </Form>
                <hr />
                <Calendar
                    localizer={localizer}
                    events={calendarEvents}
                    startAccessor="start"
                    endAccessor="end"
                    selectable={true}
                    style={{ height: "80vh" }}
                />
            </Container>
        </div >
    )
}

export default BookingForm