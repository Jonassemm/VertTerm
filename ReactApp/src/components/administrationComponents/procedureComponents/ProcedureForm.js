import React, { useEffect, useState, useRef } from "react"
import { Container, Form, Row, Col, Tabs, Tab, Button, Table } from "react-bootstrap"
import ObjectPicker from "../../ObjectPicker"
import { addProcedure, deleteProcedure, editProcedure } from "./ProcedureRequests"
import Availability from "../availabilityComponents/Availability"
import "./ProcedureStyles.css"

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
    const [selectedPrecedingRelation, setSelectedPrecedingRelation] = useState()
    const [precedingRelationTime, setPrecedingRelationTime] = useState(["",""])
    const [subsequentRelations, setSubsequentRelations] = useState([])
    const [selectedSubsequentRelation, setSelectedSubsequentRelation] = useState()
    const [subsequentRelationTime, setSubsequentRelationTime] = useState(["",""])
    const [positions, setPositions] = useState([])
    const [resourceTypes, setResourceTypes] = useState([])
    const [availabilities, setAvailability] = useState([])
    const [pricePerHour, setPricePerHour] = useState(0)
    const [pricePerInvocation, setPricePerInvocation] = useState(0)
    const [restrictions, setRestrictions] = useState([])
    const [positionsCount, setPositionsCount] = useState([])
    const [resourceTypesCount, setResourceTypesCount] = useState([])

    useEffect(() => {
        buildInitialValues()
    }, [])

    useEffect(() => {
        if (initialized.current >= 2) {
            setEdited(true)
        } else { initialized.current = initialized.current + 1 }
    }, [name, description, duration, status, precedingRelations, subsequentRelations, positions, resourceTypes, availabilities, pricePerHour, pricePerInvocation, restrictions, resourceTypesCount, positionsCount])

    function buildInitialValues() {
        if (edit) {
            setName(selected.name)
            setDescription(selected.description)
            setDuration(selected.durationInMinutes)
            setStatus(selected.status)
            setPrecedingRelations(selected.precedingRelations)
            setSubsequentRelations(selected.subsequentRelations)
            setResourceTypes(selected.neededResourceTypes)
            setAvailability(selected.availabilities)
            setPricePerHour(selected.pricePerHour)
            setPricePerInvocation(selected.pricePerInvocation)
            setRestrictions(selected.restrictions)
            if (selected.pricePerHour != 0 || selected.pricePerInvocation != 0) {
                setEnterPrice(true)
            }
            // sorting out duplicate items
            removeDuplicates(selected.neededEmployeePositions, setPositions, setPositionsCount)
            removeDuplicates(selected.neededResourceTypes, setResourceTypes, setResourceTypesCount)
        }
    }

    function removeDuplicates(selectedArray, setFunction, setFunctionCount) {
        const tempCount = []
        const temp = []
        for (let i = 0; i < selectedArray.length; i++) {
            let found = false
            for (let c = 0; c < temp.length; c++) {
                if (selectedArray[i].id == temp[c].id) found = true
            }
            if (!found) temp.push(selectedArray[i])
        }
        for (let i = 0; i < temp.length; i++) {
            let count = 0
            for (let c = 0; c < selectedArray.length; c++) {
                if (selectedArray[c].id == temp[i].id) count++
            }
            tempCount.push(count)
        }
        setFunction(temp)
        setFunctionCount(tempCount)
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
        if (selected.status == "active") {
            status = "inactive"
        } else {
            status = "active"
        }
        const data = { ...selected, status: status }
        const res = await editProcedure(selected.id, data)
        onCancel()
    }

    const handleSubmit = async event => {
        event.preventDefault()

        //combines the selected positions with the count
        let extendedPositions = []
        for (let i = 0; i < positions.length; i++) {
            for (let p = 0; p < positionsCount[i]; p++) {
                extendedPositions.push({ id: positions[i].id, ref: "position" })
            }
        }

        let extendedResourceTypes = []
        for (let i = 0; i < resourceTypes.length; i++) {
            for (let p = 0; p < resourceTypesCount[i]; p++) {
                extendedResourceTypes.push({ id: resourceTypes[i].id, ref: "resourceType" })
            }
        }

        const data = {
            name: name,
            description: description,
            durationInMinutes: duration,
            pricePerInvocation: pricePerInvocation,
            pricePerHour: pricePerHour,
            status: status,
            precedingRelations: precedingRelations,
            subsequentRelations: subsequentRelations,
            neededResourceTypes: extendedResourceTypes,
            neededEmployeePositions: extendedPositions,
            restrictions: restrictions,
            availabilities: availabilities
        }

        console.log(data)

        if (!edit) {
            const res = await addProcedure(data)
        } else {
            data.id = selected.id
            const res = await editProcedure(selected.id, data)
        }
        onCancel()
    }

    function setResourceTypesExt(data) {
        const temp = data.map((item, index) => {
            const ix = resourceTypes.findIndex(elem => elem.name == item.name)
            if (ix >= 0) {
                return resourceTypesCount[ix]
            } else {
                return 1
            }
        })
        setResourceTypesCount(temp)
        setResourceTypes(data)
    }

    function setPositionsExt(data) {
        const temp = data.map((item, index) => {
            const ix = positions.findIndex(elem => elem.name == item.name)
            if (ix >= 0) {
                return positionsCount[ix]
            } else {
                return 1
            }
        })
        setPositionsCount(temp)
        setPositions(data)
    }

    function handleProcedureRelationChange(ident) {
        if(ident == "p"){
            const relation = {
                procedure: {selectedPrecedingRelation},
                minDifference: precedingRelationTime[0],
                maxDifference: precedingRelationTime[1]
            }
            precedingRelations.push(relation)
        }else {
            const relation = {
                procedure: {selectedSubsequentRelation},
                minDifference: subsequentRelationTime[0],
                maxDifference: subsequentRelationTime[1]
            }
            subsequentRelations.push(relation)
        }
    }

    function handleRelationDeletion(event,ident) {
        const procedureText = event.target.parentElement.parentElement.firstChild.textContent
        if(ident == "p"){
           const idx =  precedingRelations.findIndex(item => item.procedure.name == procedureText)
           const tempRelations = precedingRelations.map(item => {return {...item}})
           tempRelations.splice(idx,1)
           setPrecedingRelations(tempRelations)
        }else {
            const idx =  subsequentRelations.findIndex(item => item.procedure.name == procedureText)
            const tempRelations = subsequentRelations.map(item => {return {...item}})
            tempRelations.splice(idx,1)
            setSubsequentRelations(tempRelations)
        }
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
                        <Form.Label>Zwingende Vorläufer</Form.Label>
                        <Form.Row>
                            <Form.Group xs={6} as={Col}>
                                <ObjectPicker DbObject="procedure" setState={setSelectedPrecedingRelation} />
                            </Form.Group>
                            <Form.Group as={Col}>
                                <Form.Control
                                    type="number"
                                    min="0"
                                    max={precedingRelationTime[1] != "" && precedingRelationTime[1]}
                                    placeholder="Mindestabstand"
                                    value={precedingRelationTime[0] || ""}
                                    onChange={e => setPrecedingRelationTime(precedingRelationTime.map((item, index) => {
                                        if (index == 0) return e.target.value
                                        return item
                                    }))}
                                />
                            </Form.Group>
                            <Form.Group as={Col}>
                                <Form.Control
                                    type="number"
                                    min={precedingRelationTime[0] != "" ? precedingRelationTime[0] : "0"}
                                    placeholder="Maximalabstand"
                                    value={precedingRelationTime[1] || ""}
                                    onChange={e => setPrecedingRelationTime(precedingRelationTime.map((item, index) => {
                                        if (index == 1) return e.target.value
                                        return item
                                    }))}
                                />
                            </Form.Group>
                            <Form.Group md="auto" xs={2} as={Col}>
                                <Button onClick={e => handleProcedureRelationChange("p")}>+</Button>
                            </Form.Group>
                        </Form.Row>
                        {precedingRelations.length >= 1 && 
                        <Table bordered className="RelationTable">
                            <tbody>
                                {precedingRelations.map(item => {
                                    return (
                                        <tr>
                                            <td style={{width:"50%"}}>
                                                {item.procedure.name}
                                            </td>
                                            <td style={{width:"23%"}}>
                                                {item.minDifference}
                                            </td>
                                            <td style={{width:"23%"}}>
                                                {item.maxDifference}
                                            </td>
                                            <td className="buttonCol">
                                            <Button style={{width:"35px"}} variant="danger" onClick={e => handleRelationDeletion(e,"p")}>-</Button>
                                            </td>

                                        </tr>
                                    )
                                })}
                            </tbody>
                        </Table>
                        }

                        <Form.Label>Zwingende Nachfolger</Form.Label>
                        <Form.Row>
                            <Form.Group xs={6} as={Col}>
                                <ObjectPicker DbObject="procedure" setState={setSelectedSubsequentRelation} />
                            </Form.Group>
                            <Form.Group as={Col}>
                                <Form.Control
                                    type="number"
                                    min="0"
                                    max={subsequentRelationTime[1] != "" && subsequentRelationTime[1]}
                                    placeholder="Mindestabstand"
                                    value={subsequentRelationTime[0] || ""}
                                    onChange={e => setSubsequentRelationTime(subsequentRelationTime.map((item, index) => {
                                        if (index == 0) return e.target.value
                                        return item
                                    }))}
                                />
                            </Form.Group>
                            <Form.Group as={Col}>
                                <Form.Control
                                    type="number"
                                    min={subsequentRelationTime[0] != "" ? subsequentRelationTime[0] : "0"}
                                    placeholder="Maximalabstand"
                                    value={subsequentRelationTime[1] || ""}
                                    onChange={e => setSubsequentRelationTime(subsequentRelationTime.map((item, index) => {
                                        if (index == 1) return e.target.value
                                        return item
                                    }))}
                                />
                            </Form.Group>
                            <Form.Group md="auto" xs={2} as={Col}>
                                <Button onClick={e => handleProcedureRelationChange("s")}>+</Button>
                            </Form.Group>
                        </Form.Row>
                        {subsequentRelations.length >= 1 && 
                        <Table bordered className="RelationTable">
                            <tbody>
                                {subsequentRelations.map(item => {
                                    return (
                                        <tr>
                                            <td style={{width:"50%"}}>
                                                {item.procedure.name}
                                            </td>
                                            <td style={{width:"23%"}}>
                                                {item.minDifference}
                                            </td>
                                            <td style={{width:"23%"}}>
                                                {item.maxDifference}
                                            </td>
                                            <td className="buttonCol">
                                            <Button style={{width:"35px"}} variant="danger" onClick={e => handleRelationDeletion(e,"s")}>-</Button>
                                            </td>

                                        </tr>
                                    )
                                })}
                            </tbody>
                        </Table>
                        }

                    </Tab>
                    <Tab eventKey="resources" title="Ressourcen" style={TabStyle}>
                        <Form.Label style={{ marginTop: "5px" }}>Benötigte Ressourcentypen:</Form.Label>
                        <ObjectPicker DbObject="resourceType" initial={edit ? resourceTypes : []} setState={setResourceTypesExt} multiple={true} />
                        {resourceTypes.map((item, index) => {
                            return (
                                <Row style={{ marginTop: "5px" }}>
                                    <Col style={{ textAlign: "right", verticalAlign: "bottom" }}>
                                        <span>Anzahl {item.name}:</span>
                                    </Col>
                                    <Col>
                                        <Form.Control
                                            type="number"
                                            min="1"
                                            name={index}
                                            value={resourceTypesCount[index] || "1"}
                                            onChange={e => {
                                                setResourceTypesCount(resourceTypesCount.map((item, index) => {
                                                    if (index == e.target.name) {
                                                        return Number(e.target.value)
                                                    } else {
                                                        return item
                                                    }
                                                }))
                                            }}
                                        />
                                    </Col>
                                </Row>
                            )
                        })}
                        <Form.Label style={{ marginTop: "10px" }}>Benötigte Positionen:</Form.Label>
                        <ObjectPicker DbObject="position" initial={edit ? positions : []} setState={setPositionsExt} multiple={true}></ObjectPicker>
                        {positions.map((item, index) => {
                            return (
                                <Row style={{ marginTop: "5px" }}>
                                    <Col style={{ textAlign: "right", verticalAlign: "bottom" }}><span>Anzahl {item.name}:</span>
                                    </Col>
                                    <Col>
                                        <Form.Control
                                            type="number"
                                            min={1}
                                            id={index}
                                            name={index}
                                            value={positionsCount[index] || 1}
                                            onChange={e => {
                                                setPositionsCount(positionsCount.map((item, index) => {
                                                    console.log(typeof (e.target.value))
                                                    if (index == e.target.name) {
                                                        return Number(e.target.value)
                                                    } else {
                                                        return item
                                                    }
                                                }))
                                            }}
                                        />
                                    </Col>
                                </Row>
                            )
                        })}
                    </Tab>
                </Tabs>
                <hr />
                {(edited || !edit) && <Button variant="success" style={{ marginRight: "10px" }} type="submit">Bestätigen</Button>}
                {edit && (selected.status != "deleted") && <Button style={{ marginRight: "10px" }} variant="danger" onClick={handleDelete}>Löschen</Button>}
                {edit && (selected.status != "deleted") && <Button onClick={handleStatusChange} style={{ marginRight: "10px" }}>{selected.status == "active" ? "Deaktivieren" : "Aktivieren"}</Button>}
                <Button onClick={onCancel} variant="secondary">Abbrechen</Button>
            </Form>
        </Container>
    )
}

export default ProcedureForm