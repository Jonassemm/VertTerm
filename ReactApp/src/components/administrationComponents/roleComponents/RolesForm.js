import React, { useState, useEffect } from "react"
import { Form, Table } from "react-bootstrap"
import { Container, Button } from "react-bootstrap"
import { getRights, addRole, deleteRole, editRole } from "./RoleRequests"
import { hasRight } from "../../../auth"
import {roleRights} from "../../Rights"

const RolesForm = ({ onCancel, edit, selected, userStore }) => {
    const rightName = roleRights[1] //write right
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
        if(hasRight(userStore, [rightName])) {
            const rightsData = []
            for (let i = 0; i < selectedRights.length; i++) {
                if (selectedRights[i]) rightsData.push(rights[i])
            }
            const data = { name: rolename, description: description, rights: rightsData }
            let res = {}
            if (!edit) {
                res = await addRole(data)
                userStore.setMessage("Rolle erfolgreich hinzugefügt!")
            } else {
                res = await editRole(selected.id, data)
                userStore.setMessage("Rolle erfolgreich geändert!")
            }
        }else {//no rights!
            userStore.setWarningMessage("Ihnen fehlt das Recht:\n"+ rightName)
        }
        onCancel()
    }

    const handleDelete = async () => {
        if(hasRight(userStore, [rightName])) {
            const answer = confirm("Möchten Sie diese Rolle wirklich löschen? ")
            if (answer) {
                const res = await deleteRole(selected.id)
                userStore.setMessage("Rolle erfolgreich gelöscht!")
                console.log(res)
            }
        }else {//no rights!
            userStore.setWarningMessage("Ihnen fehlt das Recht:\n"+ rightName)
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
                            required
                            pattern=".{1,50}"//everything allowed but min 1 and max 50 letters
                            title="Die Bezeichnung muss zwischen 1 und 50 Zeichen beinhalten!"
                            type="text"
                            placeholder="Rollenname"
                            disabled={rolename=="Anonymous_role" || rolename=="Admin_role"}
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
                    <Container style={{textAlign: "right"}}>
                        <Button variant="secondary" style={{ marginRight: "10px" }} onClick={onCancel}>Abbrechen</Button>
                        {hasRight(userStore, [rightName]) && 
                            <Button variant="danger" style={{ marginRight: "10px" }} onClick={handleDelete}>Löschen</Button>
                        }
                        {(edited || !edit) && hasRight(userStore, [rightName]) && 
                            <Button variant="success" style={{ marginRight: "10px" }} type="submit">Übernehmen</Button>
                        }
                    </Container>
                </Form>
            </Container>
        </React.Fragment>
    )
}

export default RolesForm