import React ,{useState} from 'react'
import {Form, Table, Card, Col, Container, Button, InputGroup} from 'react-bootstrap';

import DatePicker from "react-datepicker"
import "react-datepicker/dist/react-datepicker.css"


function setDate() {
    const date = new Date();
    var changeHours = false;
    
    var minutes = date.getMinutes();
    if (minutes < 15) {
        date.setMinutes(15);
    } else if (minutes < 30) {
        date.setMinutes(30);
    } else if (minutes < 45) {
        date.setMinutes(45);
    } else if (minutes <= 59) {
        date.setMinutes(0);
        changeHours = true;
    }

    var hours = date.getHours();
    if (changeHours) {
        hours = hours + 1;
    } 
    if (hours > 23) {
        hours = 0;
    }
    date.setHours(hours);
    return date;
}

export default function AvaiabilityForm() {

    const [startDate, setStartDate] = useState(setDate)
    const [endDate, setEndDate] = useState(setDate)
 

    const handleStartDateChange = date => setStartDate(date)
    const handleEndDateChange = date => setEndDate(date)
    return (
        <Container>
            <hr style={{backgroundColor: "white"}}/>
            <h4 style={{fontWeight:"bold", margin: "20px 0px 20px 0px"}} >Verfügbarkeit</h4>
            <Form.Row>
                <Form.Group style={{display: "flex", flexWrap: "nowrap"}} as={Col} md="6">
                    <Form.Label style={{marginRight: "20px"}}>Stardatum</Form.Label>
                    <DatePicker
                    selected={startDate}
                    onChange={handleStartDateChange}
                    showTimeSelect
                    timeFormat="HH:mm"
                    timeIntervals={5}
                    timeCaption="Uhrzeit"
                    dateFormat="d.M.yyyy / HH:mm"
                    />
                </Form.Group>
                <Form.Group style={{display: "flex", flexWrap: "nowrap"}} as={Col} md="6">
                    <Form.Label style={{marginRight: "20px"}}>Enddatum</Form.Label>
                    <DatePicker
                    selected={startDate}
                    onChange={handleStartDateChange}
                    showTimeSelect
                    timeFormat="HH:mm"
                    timeIntervals={5}
                    timeCaption="Uhrzeit"
                    dateFormat="d.M.yyyy / HH:mm"
                    />
                </Form.Group>
            </Form.Row>

            <Form.Row>
                <Form.Group style={{display: "flex", flexWrap: "nowrap"}} as={Col} md="6">
                    <div style={{borderStyle: "solid"}} key={`inline-checkbox-weekday`} className="mb-3">
                        <Form.Label style={{margin: "0px 10px 0px 10px"}}>Wochentag:</Form.Label>
                        <Form.Check inline label="Mo" type='checkbox' id={`inline-checkbox-Mo`} />
                        <Form.Check inline label="Di" type='checkbox' id={`inline-checkbox-Tu`} />
                        <Form.Check inline label="Mi" type='checkbox' id={`inline-checkbox-We`} />
                        <Form.Check inline label="Do" type='checkbox' id={`inline-checkbox-Th`} />
                        <Form.Check inline label="Fr" type='checkbox' id={`inline-checkbox-Fr`} />
                        <Form.Check inline label="Sa" type='checkbox' id={`inline-checkbox-Sa`} />
                        <Form.Check inline label="So" type='checkbox' id={`inline-checkbox-So`} /> 
                    </div>
                </Form.Group>
            </Form.Row>
            <Form.Row >
                <Form.Group style={{display: "flex", flexWrap: "nowrap"}} as={Col} md="6">
                    <div style={{borderStyle: "solid"}} key={`inline-checkbox-rhythm`} className="mb-3">
                        <Form.Label style={{margin: "0px 10px 0px 10px"}}>Intervall:</Form.Label>
                        <Form.Check inline label="Täglich" type='checkbox' id={`inline-checkbox-dayly`} />
                        <Form.Check inline label="Wöchentlich" type='checkbox' id={`inline-checkbox-weekly`} />
                        <Form.Check inline label="Monatlich" type='checkbox' id={`inline-checkbox-monthly`} />
                        <Form.Check inline label="Jährlich" type='checkbox' id={`inline-checkbox-yearly`} />
                    </div>
                </Form.Group>
                <Form.Group style={{display: "flex", flexWrap: "nowrap"}} as={Col} md="6">
                    <Form.Label style={{marginRight: "10px"}}>Wiederholungsintervall:</Form.Label>
                    <Form.Control
                        style={{width: "150px", height: "30px"}}
                        name="rhythm"
                        type="text"
                        placeholder="jeden X-ten"
                    />
                </Form.Group>
            </Form.Row>
            <Form.Row>
                <Form.Group style={{display: "flex", flexWrap: "nowrap"}} as={Col} md="6">
                    <Form.Label style={{marginRight: "10px"}}>Anzahl an Wiederholung bis Enddatum:</Form.Label>
                    <Form.Control
                        style={{width: "150px", height: "30px"}}
                        name="rhythm"
                        type="text"
                        placeholder="Wiederholungen"
                    />
                </Form.Group>
            </Form.Row>
            <Form.Row>
            <Button style={{marginBottom: "20px"}}>Verfügbarkeit hinzufügen</Button>
            </Form.Row>
            <Form.Row>
            <Table striped hover variant="light">
                    <thead>
                        <tr>
                            <th>Verfügbarkeiten</th>
                        </tr>
                    </thead>
                    <tbody>
                            <tr>
                              <td><Form.Control type="text"/></td>
                              <td><Button>Entfernen</Button></td>
                            </tr>

                    </tbody>
                  </Table> 
            </Form.Row>
        </Container>
    )
}
