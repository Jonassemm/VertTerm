import React, { useState, useEffect } from "react"
import { Form, Table } from "react-bootstrap"
import { Container, Button } from "react-bootstrap"
import { getRights, addRole, deleteRole, editRole } from "./RoleRequests"

const RolesForm = ({ onCancel, edit, selected }) => {
    const [rolename, setRolename] = useState("")
    const [description, setDescription] = useState("")
    const [rights, setRights] = useState([])
    const [selectedRights, setSelectedRights] = useState([])
    const [edited, setEdited] = useState(false)

    useEffect(() => {
        buildRights()
        if (edit) {
            setRolename(selected.name)
            setDescription(selected.description)
        }
    }, [])

    async function buildRights() {
        const res = await getRights()
        setRights(res.data.map(item => {
            return {
                ...item
            }
        })
        )
        let tempSelected = []
        if (edit == true) {
            for (let i = 0; i < res.data.length; i++) {
                if (selected.rights.includes(res.data[i].name)) {
                    tempSelected[i] = true
                } else {
                    tempSelected[i] = false
                }
            }
        } else {
            tempSelected = res.data.map(item => {
                return false;
            })
        }
        setSelectedRights(tempSelected)
    }

    function handleRolenameChange(event) {
        setRolename(event.target.value)
        setEdited(true)
    }

    function handleDescriptionChange(event) {
        setDescription(event.target.value)
        setEdited(true)
    }

    const handleRightSelection = event => {
        setSelectedRights(selectedRights.map((item, index) => {
            if (index == event.target.value) return !item
            else return item
        }))
        setEdited(true)
    }

    const handleSubmit = async event => {
        event.preventDefault()
        const rightsData = []
        for (let i = 0; i < selectedRights.length; i++) {
            if (selectedRights[i]) rightsData.push(rights[i])
        }
        const data = { name: rolename, description: description, rights: rightsData }
        let res = {}
        if (!edit) {
            res = await addRole(data)
        } else {
            res = await editRole(selected.id, data)
        }
        console.log(res)
        onCancel()
    }

    const handleDelete = async () => {
        const answer = confirm("Möchten Sie diese Rolle wirklich löschen? ")
        if (answer) {
            const res = await deleteRole(selected.id)
            console.log(res)
        }
        onCancel()
    }

    const tdStyle = {
        padding: "1px 0px 0px 1px",
        verticalAlign: "middle"
    }

    return (
        <React.Fragment>
            <Container>
                <Form onSubmit={handleSubmit}>
                    <Form.Row>
                        <Form.Label>Rollenbezeichnung: </Form.Label>
                        <Form.Control
                            type="text"
                            placeholder="Rollenname"
                            value={rolename || ""}
                            required
                            onChange={handleRolenameChange}
                        />
                    </Form.Row>
                    <Form.Row style={{ marginTop: "10px" }}>
                        <Form.Label>Beschreibung: </Form.Label>
                        <Form.Control
                            type="text"
                            placeholder="Beschreibung"
                            value={description || ""}
                            onChange={handleDescriptionChange}
                        />
                    </Form.Row>
                    <Form.Row style={{ marginTop: "10px" }}>
                        <Form.Label>Rechte: </Form.Label>
                    </Form.Row>
                    <Table hover style={{ marginBottom: "0px" }}>
                        <tbody>
                            {rights.map((item, index) => {
                                return (
                                    <tr key={item.id} style={{ padding: "0px" }}>
                                        <td style={tdStyle}>
                                            <Form.Check value={index} checked={selectedRights[index] || false} onChange={handleRightSelection} type="checkbox" />
                                        </td>
                                        <td style={tdStyle}>{item.description}</td>
                                        <td style={tdStyle}>{item.name}</td>
                                    </tr>
                                )
                            })}
                        </tbody>
                    </Table>
                    <hr />
                    {(edited || !edit) && <Button style={{ marginRight: "10px" }} type="submit">Bestätigen</Button>}
                    <Button style={{ marginRight: "10px" }} variant="danger" onClick={handleDelete}>Löschen</Button>
                    <Button onClick={onCancel} variant="secondary">Abbrechen</Button>
                </Form>
            </Container>
        </React.Fragment>
    )
}

export default RolesForm