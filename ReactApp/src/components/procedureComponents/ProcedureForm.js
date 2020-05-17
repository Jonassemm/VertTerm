import React, { useEffect, useState } from "react"
import { Container, Form, Col, Tabs, Tab } from "react-bootstrap"
import { Formik } from "formik"
import Avaiability from "../availability/Availability"
import ObjectPicker from "../ObjectPicker"
import {addProcedure} from "./ProcedureRequests"

function ProcedureForm({ onCancel, edit, selected }) {
    const [initialValues, setInitialValues] = useState({
        description: "",
        duration: 0,
        name: "",
        pricePerHour: 0,
        pricePerInvocation: 0,
        restrictions: [],
        status: ""
    })
    const [enterPrice, setEnterPrice] = useState(false)
    const [tabKey, setTabKey] = useState('general')
    const [selectedUsers, setSelectedUsers] = useState([])

    useEffect(() => {
        buildInitialValues()
    }, [])

    function buildInitialValues() {
        if (edit) {
            setInitialValues({
                description: selected.description,
                duration: selected.duration,
                name: selected.name,
                pricePerHour: selected.pricePerHour,
                pricePerInvocation: selected.pricePerInvocation,
                restrictions: selected.restrictions,
                status: selected.status
            })
        }
    }

    function handleEnterPrice() {
        setEnterPrice(!enterPrice)
    }

    return (

        <Container>
            <Formik
                initialValues={initialValues}
                onSubmit={ async (values) => {
                    const {description,
                    duration,
                    name,
                    pricePerHour,
                    pricePerInvocation,
                    restrictions,
                    status} = values
                    const data = {description,duration,name,pricePerHour,pricePerInvocation,restrictions,status,selectedUsers}
                    const res = await addProcedure(data)
                }
                }
            >
                {({ values, handleChange, handleSubmit }) => (
                    <Form onSubmit={handleSubmit}>
                        {JSON.stringify(values)}
                        <Tabs
                            id="controlled-tab"
                            activekey={tabKey}
                            onSelect={k => setTabKey(k)}
                        >
                            <Tab eventKey="general" title="Allgemein">
                                <Form.Row>
                                    <Form.Label>
                                        Name
                                </Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="name"
                                        placeholder="Prozedurnamen eingeben"
                                        value={values.name}
                                        required
                                        onChange={handleChange}
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
                                        value={values.description}
                                        onChange={handleChange}
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
                                        value={values.duration}
                                        onChange={handleChange}
                                    />
                                </Form.Row>
                                <Form.Row>
                                    <Form.Check
                                        id="switchEnabled"
                                        type="switch"
                                        name="enterPrice"
                                        value={enterPrice}
                                        onChange={handleEnterPrice}
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
                                                value={values.pricePerHour}
                                                onChange={handleChange}
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
                                                value={values.pricePerInvocation}
                                                onChange={handleChange}
                                            />
                                        </Form.Group>
                                    </Form.Row>
                                }
                            </Tab>
                            <Tab eventKey="availability" title="Verfügbarkeit">
                                <div style={{ marginTop: "10px" }}>
                                    <ObjectPicker 
                                    handleChange={setSelectedUsers} 
                                    DbObject="user" />
                                </div>
                            </Tab>
                        </Tabs>
                    </Form>
                )}
            </Formik>
        </Container>

    )

}

export default ProcedureForm