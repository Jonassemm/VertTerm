//author: Patrick Venturini
import React, { useState, useEffect } from "react"
import { Form } from "react-bootstrap"
import { Container, Button } from "react-bootstrap"
import {addRestriction, deleteRestriction, editRestriction } from "./RestrictionRequests"

const RestrictionForm = ({ onCancel, edit, selected }) => {
    const [name, setName] = useState("")
    const [edited, setEdited] = useState(false)


    useEffect(() => {
        if (edit) {
            setName(selected.name)
        }
    }, [])


    function handleNameChange(event) {
        setName(event.target.value)
        setEdited(true)
    }


    const handleSubmit = async event => {
        event.preventDefault()
        let res = {}
        if (!edit) {
            const data = {name}
            res = await addRestriction(data)
        } else {
            var id = selected.id
            const data = {id, name}
            res = await editRestriction(data)
        }
        onCancel()
    }


    const handleDeleteRestriction = async () => {
        const answer = confirm("Möchten Sie diese Einschränkung wirklich löschen? ")
        if (answer) {
            try {
                const res = await deleteRestriction(selected.id)
            } catch (error) {
                console.log(Object.keys(error), error.message)
            }
        }
        onCancel()
    }
    

    return (
        <React.Fragment>
            <Container>
                <Form onSubmit={handleSubmit}>
                    <Form.Row>
                        <Form.Label>Bezeichnung: </Form.Label>
                        <Form.Control
                            required
                            pattern=".{1,50}"//everything allowed but min 1 and max 50 letters
                            title="Die Bezeichnung muss zwischen 1 und 50 Zeichen beinhalten!"
                            type="text"
                            placeholder="Bezeichnung der Einschränkung"
                            value={name || ""}
                            required
                            onChange={handleNameChange}
                        />
                    </Form.Row>
                    <hr style={{ border: "0,5px solid #999999" }}/>
                    <Container style={{textAlign: "right"}}>
                        {edit ? <Button style={{ marginRight: "10px" }} variant="danger" onClick={handleDeleteRestriction}>Löschen</Button> : null}
                        <Button style={{ marginRight: "10px" }} onClick={onCancel} variant="secondary">Abbrechen</Button>
                        {(edit ? edited ? 
                            <Button variant="success"  type="submit">Übernehmen</Button>:
                            null : <Button variant="success"  type="submit">Einschränkung anlegen</Button>)
                        }
                    </Container>
                </Form>
            </Container>
        </React.Fragment>
    )
}
export default RestrictionForm