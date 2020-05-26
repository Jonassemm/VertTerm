import React, { useEffect, useState, useRef } from "react"
import { Container, Form, Col, Tabs, Tab, Button } from "react-bootstrap"
import ObjectPicker from "../ObjectPicker"
import { addProcedure, deleteProcedure, editProcedure } from "./ProcedureRequests"
import Availability from "../availability/Availability"

function ProcedureForm({ onCancel, edit, selected }) {
    const [tabKey, setTabKey] = useState('general')
    const [edited, setEdited] = useState(false)
    const [enterPrice, setEnterPrice] = useState(false)
    const initialized = useRef(0)

    const [name, setName] = useState("")
    const [description, setDescription] = useState("")
    const [duration, setDuration] = useState(0)
    const [status, setStatus] = useState("active")
    const [precedingRelations, setPrecedingRelations] = useState([])
    const [subsequentRelations, setSubsequentRelations] = useState([])
    const [positions, setPositions] = useState([])
    const [resourceTypes, setResourceTypes] = useState([])
    const [availabilities, setAvailability] = useState([])
    const [pricePerHour, setPricePerHour] = useState(0)
    const [pricePerInvocation, setPricePerInvocation] = useState(0)
    const [restrictions, setRestrictions] = useState([])

    useEffect(() => {
        buildInitialValues()
    }, [])

    useEffect(() => {
        if (initialized.current >= 2) {
            setEdited(true)
        } else { initialized.current = initialized.current + 1 }
    }, [name, description, duration, status, precedingRelations, subsequentRelations, positions, resourceTypes, availabilities, pricePerHour, pricePerInvocation, restrictions])

function buildInitialValues() {
    if (edit) {
        setName(selected.name)
        setDescription(selected.description)
        setDuration(selected.duration)
        setStatus(selected.status)
        setPrecedingRelations(selected.precedingRelations)
        setSubsequentRelations(selected.subsequentRelations)
        setPositions(selected.positions)
        setResourceTypes(selected.resourceTypes)
        setAvailability(selected.availabilities)
        setPricePerHour(selected.pricePerHour)
        setPricePerInvocation(selected.pricePerInvocation)
        setRestrictions(selected.restrictions)
        if(selected.pricePerHour != 0 || selected.pricePerInvocation != 0){
            setEnterPrice(true)
        }
    }
}

const addAvailability = (newAvailability) => {
    setAvailability(availability => [...availability, newAvailability]);
}

const editedAvailabilities = (isEdited) => {
    setEdited(isEdited)
} 

const handleDelete = async () => {
    const res = await deleteProcedure(selected.id)
    onCancel()
}

async function handleStatusChange() {
    let status = ""
    if(selected.status == "active"){
        status = "inactive"
    }else{
        status = "active"
    }
    const data = {...selected, status: status}
    console.log(selected)
    console.log(data)
    const res = await editProcedure(selected.id, data)
    onCancel()
}

const handleSubmit = async event => {
    event.preventDefault()
    const data = {
        name: name,
        description: description,
        durationInMinutes: duration,
        pricePerInvocation: pricePerInvocation,
        pricePerHour: pricePerHour,
        status: status,
        precedingRelations: precedingRelations,
        subsequentRelations: subsequentRelations,
        neededResourceTypes: resourceTypes,
        neededEmployeePositions: positions,
        restrictions: restrictions,
        availabilities: availabilities
    }
    if (!edit) {
        const res = await addProcedure(data)
    } else {
        data.id = selected.id
        const res = await editProcedure(selected.id, data)
    }
    onCancel()
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
                            value={name || ""}
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
                            value={description || ""}
                            onChange={e => setDescription(e.target.value)}
                        />
                    </Form.Row>
                    <Form.Row>
                        <Form.Label>
                            Dauer
                            </Form.Label>
                        <Form.Control
                            type="number"
                            min="0"
                            name="duration"
                            placeholder="Dauer in Minuten eingeben"
                            value={duration || 0}
                            onChange={e => setDuration(event.target.value)}
                        />
                    </Form.Row>
                    <Form.Row style={{ marginTop: "10px" }}>
                        <Form.Check
                            id="switchEnabled"
                            type="switch"
                            name="enterPrice"
                            value={enterPrice || 0}
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
                                    value={pricePerHour || 0}
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
                                    value={pricePerInvocation || 0}
                                    onChange={e => setPricePerInvocation(e.target.value)}
                                />
                            </Form.Group>
                        </Form.Row>
                    }
                </Tab>
                <Tab eventKey="availability" title="Verfügbarkeit" style={TabStyle}>
                    <Availability availabilities={availabilities} addAvailability={addAvailability} editedAvailability={editedAvailabilities} />
                </Tab>
                <Tab eventKey="dependencies" title="Abhängigkeiten" style={TabStyle}>

                </Tab>
                <Tab eventKey="resources" title="Ressourcen" style={TabStyle}>
                    <Form.Label style={{ marginTop: "5px" }}>Benötigte Ressourcentypen:</Form.Label>
                    <ObjectPicker DbObject="resourceType" initial={edit ? selected.neededResourceTypes : []} setState={setResourceTypes} multiple={true}></ObjectPicker>
                    <Form.Label style={{ marginTop: "10px" }}>Benötigte Positionen:</Form.Label>
                    <ObjectPicker DbObject="position" initial={edit ? selected.neededEmployeePositions : []} setState={setPositions} multiple={true}></ObjectPicker>

                </Tab>
            </Tabs>
            <hr />
            {(edited || !edit) && <Button variant="success" style={{ marginRight: "10px" }} type="submit">Bestätigen</Button>}
            {edit && (selected.status != "deleted") && <Button style={{ marginRight: "10px" }} variant="danger" onClick={handleDelete}>Löschen</Button>}
            {edit && (selected.status != "deleted") && <Button onClick={handleStatusChange} style={{ marginRight: "10px"}}>{selected.status == "active" ? "Deaktivieren" : "Aktivieren"}</Button>}
            <Button onClick={onCancel} variant="secondary">Abbrechen</Button>
        </Form>
    </Container>
)
}

export default ProcedureForm