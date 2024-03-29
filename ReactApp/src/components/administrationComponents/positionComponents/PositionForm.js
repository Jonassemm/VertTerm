//author: Patrick Venturini
import React, { useState, useEffect } from "react"
import {Form} from "react-bootstrap"
import { Container, Button } from "react-bootstrap"
import {addPosition, deletePosition, editPosition } from "./PositionRequests"
import { hasRight } from "../../../auth"
import {positionRights} from "../../Rights"


const PositionForm = ({onCancel, edit, selected, userStore}) => {
    const rightName = positionRights[1] //write right
    const [name, setName] = useState("")
    const [description, setDescription] = useState("")
    const [edited, setEdited] = useState(false)


    useEffect(() => {
        if (edit) {
            setName(selected.name)
            setDescription(selected.description)
        }
    }, [])


    function handleNameChange(event) {
        setName(event.target.value)
        setEdited(true)
    }

    
    function handleDescriptionChange(event) {
        setDescription(event.target.value)
        setEdited(true)
    }


    const handleSubmit = async event => {
        event.preventDefault()

        if(hasRight(userStore, [rightName])) {
            let res = {}
            if (!edit) {
                const data = {name, description}
                res = await addPosition(data)
                userStore.setMessage("Position erfolgreich hinzugefügt!")
            } else {
                var id = selected.id
                const data = {id, name, description}
                res = await editPosition(id, data)
                userStore.setMessage("Position erfolgreich geändert!")
            }
            onCancel()
        }else {//no right to submit 
            userStore.setWarningMessage("Ihnen fehlt das Recht:\n"+ rightName)
        }
    }


    const handleDeletePosition = async () => {
        if(hasRight(userStore, [rightName])){
            const answer = confirm("Möchten Sie diese Position wirklich löschen? ")
            if (answer) {
                try {
                    const res = await deletePosition(selected.id)
                    userStore.setMessage("Position erfolgreich gelöscht!")
                } catch (error) {
                    console.log(Object.keys(error), error.message)
                }
            }
        }else {//no rights!
            userStore.setWarningMessage("Ihnen fehlt das Recht:\n"+ rightName)
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
                            placeholder="Positionsname"
                            value={name || ""}
                            required
                            onChange={handleNameChange}
                        />
                    </Form.Row>
                    <Form.Row style={{ marginTop: "10px" }}>
                        <Form.Label>Beschreibung: </Form.Label>
                        <Form.Control
                            as="textarea"
                            type="text"
                            placeholder="Weitere Informationen"
                            value={description || ""}
                            onChange={handleDescriptionChange}
                        />
                    </Form.Row>
                    <hr style={{ border: "0,5px solid #999999" }}/>
                    <Container style={{textAlign: "right"}}>
                        {edit ? <Button style={{ marginRight: "10px" }} variant="danger" onClick={handleDeletePosition}>Löschen</Button> : null}
                        <Button style={{ marginRight: "10px" }} onClick={onCancel} variant="secondary">Abbrechen</Button>
                        {(edit ? edited ? 
                            <Button variant="success"  type="submit">Übernehmen</Button>:
                            null : <Button variant="success"  type="submit">Position anlegen</Button>)
                        }
                    </Container>
                </Form>
            </Container>
        </React.Fragment>
    )
}
export default PositionForm