import React, { useState, useEffect } from "react"
import { Form } from "react-bootstrap"
import { Container, Col, Button } from "react-bootstrap"
//import {addextUserInfo, deleteextUserInfo, editextUserInfo } from "./extUserInfoRequests"

const ExtUserInfoForm = ({ onCancel, edit, selected }) => {
    const [name, setName] = useState("")
    const [isRequired, setIsRequired] = useState(false)
    const [edited, setEdited] = useState(false)

    useEffect(() => {
        if (edit) {
            setName(selected.name)
            setIsRequired(selected.isRequired)
        }
    }, [])


    function handleNameChange(event) {
        setName(event.target.value)
        setEdited(true)
    }

    function handleIsRequiredChange(event) {
        setIsRequired(!isRequired)
        setEdited(true)
    }

    const handleSubmit = async event => {
        console.log(isRequired)
        event.preventDefault()
        let res = {}
        if (!edit) {
            const data = {name, description}
            //res = await addExtUserInfo(data)
        } else {
            var id = selected.id
            const data = {id, name, description}
            //res = await editExtUserInfo(id, data)
        }
        console.log(res)
        onCancel()
    }

    const handleDeleteExtUserInfo = async () => {
        const answer = confirm("Möchten Sie diese erweiterten Benutzerinformationen wirklich löschen? ")
        if (answer) {
            //const res = await deleteExtUserInfo(selected.id)
            console.log(res)
        }
        onCancel()
    }

    return (
        <React.Fragment>
            <Container>
                <Form onSubmit={handleSubmit}>
                    <Form.Row>
                        <Form.Group as={Col} md="12">
                            <Form.Label>Bezeichnung: </Form.Label>
                            <Form.Control
                                required
                                pattern=".{1,50}"//everything allowed but min 1 and max 50 letters
                                title="Die Bezeichnung muss zwischen 1 und 50 Zeichen beinhalten!"
                                type="text"
                                placeholder="Attributname"
                                value={name || ""}
                                required
                                onChange={handleNameChange}
                            />
                        </Form.Group>
                    </Form.Row>
                    <Form.Row>
                        <Form.Check
                            name="isRequired" 
                            label="Pflichtfeld"
                            type='checkbox' 
                            checked={isRequired || false}
                            //value={isRequired}
                            onChange={handleIsRequiredChange} 
                        />
                    </Form.Row>
                    <hr style={{ border: "0,5px solid #999999" }}/>
                    <Container style={{textAlign: "right"}}>
                        {edit ? <Button style={{ marginRight: "10px" }} variant="danger" onClick={handleDeleteExtUserInfo}>Löschen</Button> : null}
                        <Button style={{ marginRight: "10px" }} onClick={onCancel} variant="secondary">Abbrechen</Button>
                        {(edit ? edited ? 
                            <Button variant="success"  type="submit">Übernehmen</Button>:
                            null : <Button variant="success" type="submit">Attribut anlegen</Button>)
                        }
                    </Container>
                </Form>
            </Container>
        </React.Fragment>
    )
}

export default ExtUserInfoForm