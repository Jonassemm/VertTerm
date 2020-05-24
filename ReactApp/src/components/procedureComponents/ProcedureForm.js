import React, { useEffect, useState, useReducer } from "react"
import { Container, Form, Col, Tabs, Tab, Button } from "react-bootstrap"
import { Formik } from "formik"
import ObjectPicker from "../ObjectPicker"
import { addProcedure } from "./ProcedureRequests"
import Availability from "../availabilityComponents/Availability"

function ProcedureForm({ onCancel, edit, selected }) {
    const [enterPrice, setEnterPrice] = useState(false)
    const [tabKey, setTabKey] = useState('general')
    const [positions, setPositions] = useState([])
    const [resourceTypes, setResourceTypes] = useState([])
    const [availability, setAvailability] = useState([])
    const [edited, setEdited] = useState(false)
    const [name, setName] = useState("")
    const [description, setDescription] = useState("")
    const [duration, setDuration] = useState(0)
    const [pricePerHour, setPricePerHour] = useState(0)
    const [pricePerInvocation, setPricePerInvocation] = useState(0)
    const [restictions, setRestrictions] = useState([])
    const [status, setStatus] = useState("")

    useEffect(() => {
        //buildInitialValues()
    }, [])
    /* 
        function buildInitialValues() {
            if (edit) {
                    setDescription(selected.description)
                    setDuration(selected.duration)
                    setName(selected.name)
                    setPricePerHour(selected.pricePerHour)
                    setPricePerInvocation(selected.pricePerInvocation)
                    setRestrictions(selected.restrictions)
                    status: selected.status
            }
        } */

    const addAvailability = (newAvailability) => {
        setAvailability(availability => [...availability, newAvailability]);
    }

    const handleDelete = () => {

    }

    const handleSubmit = async event => {
        event.preventDefault()
        const res = await addProcedure(data)
    }

    const TabStyle = { paddingTop: "10px" }

    return (
        <Container>
            <Form onSubmit={handleSubmit}>
                <Tabs
                    id="controlled-tab"
                    activekey={tabKey}
                    onSelect={k => setTabKey(k)}
                >
                    <Tab eventKey="general" title="Allgemein" style={TabStyle}>
                        <Form.Row>
                            <Form.Label>
                                Name
                            </Form.Label>
                            <Form.Control
                                type="text"
                                name="name"
                                placeholder="Prozedurnamen eingeben"
                                value={name}
                                required
                                onChange={e => setName(e.target.value)}
                            />
                        </Form.Row>
                        <Form.Row>
                            <Form.Label>
                                Beschreibung
                            </Form.Label>
                            <Form.Control
                                type="text"
                                name="description"
                                placeholder="Beschreibung eingeben"
                                value={description}
                                onChange={e => setDescription(e.target.value)}
                            />
                        </Form.Row>
                        <Form.Row>
                            <Form.Label>
                                Dauer
                            </Form.Label>
                            <Form.Control
                                type="number"
                                name="duration"
                                placeholder="Dauer in Minuten eingeben"
                                value={duration}
                                onChange={e => setDuration(event.target.value)}
                            />
                        </Form.Row>
                        <Form.Row style={{ marginTop: "10px" }}>
                            <Form.Check
                                id="switchEnabled"
                                type="switch"
                                name="enterPrice"
                                value={enterPrice}
                                onChange={e => setEnterPrice(!enterPrice)}
                                checked={enterPrice}
                                label="Preis"
                            />
                        </Form.Row>
                        {enterPrice &&
                            <Form.Row>
                                <Form.Group as={Col}>
                                    <Form.Label>
                                        Preis pro Stunde
                                        </Form.Label>
                                    <Form.Control
                                        type="number"
                                        step="any"
                                        min="0"
                                        name="pricePerHour"
                                        placeholder="Preis pro Stunde in Euro"
                                        value={pricePerHour}
                                        onChange={e => setPricePerHour(e.target.value)}
                                    />
                                </Form.Group>
                                <Form.Group as={Col}>
                                    <Form.Label>
                                        Preis je Termin
                                        </Form.Label>
                                    <Form.Control
                                        type="number"
                                        step="any"
                                        min="0"
                                        name="pricePerInvocation"
                                        placeholder="Preis pro Termin in Euro"
                                        value={pricePerInvocation}
                                        onChange={e => setPricePerInvocation(e.target.value)}
                                    />
                                </Form.Group>
                            </Form.Row>
                        }
                    </Tab>
                    <Tab eventKey="availability" title="Verfügbarkeit" style={TabStyle}>
                        <Availability availability={availability} addAvailability={addAvailability} editedAvailability={setEdited} />
                    </Tab>
                    <Tab eventKey="dependencies" title="Abhängigkeiten" style={TabStyle}>

                    </Tab>
                    <Tab eventKey="resources" title="Ressourcen" style={TabStyle}>
                        <Form.Label style={{ marginTop: "5px" }}>Benötigte Ressourcentypen:</Form.Label>
                        <ObjectPicker DbObject="resourceType" setState={setResourceTypes}></ObjectPicker>
                        <Form.Label style={{ marginTop: "10px" }}>Benötigte Positionen:</Form.Label>
                        <ObjectPicker DbObject="position" setState={setPositions}></ObjectPicker>

                    </Tab>
                </Tabs>
                <hr />
                {(edited || !edit) && <Button style={{ marginRight: "10px" }} type="submit">Bestätigen</Button>}
                <Button style={{ marginRight: "10px" }} variant="danger" onClick={handleDelete}>Löschen</Button>
                <Button onClick={onCancel} variant="secondary">Abbrechen</Button>
            </Form>
        </Container>
    )
}

export default ProcedureForm