//author: Patrick Venturini
import React, { useState, useEffect } from "react"
import { Form } from "react-bootstrap"
import { Container, Button } from "react-bootstrap"
import {addResourceType, deleteResourceType, editResourceType } from "./ResourceTypeRequests"
import { hasRight } from "../../../auth"
import {resourceTypeRights} from "../../Rights"

const ResourceTypeForm = ({ onCancel, edit, selected, userStore }) => {
    const rightName = resourceTypeRights[1] //write right
    
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
            const data = { name: name, description: description}
            let res = {}
            if (!edit) {
                res = await addResourceType(data)
            } else {
                res = await editResourceType(selected.id, data)
            }
            onCancel()
        }else {//no right to submit 
            alert("Für diesen Vorgang besitzten Sie nicht die erforderlichen Rechte!\n\nBenötigtes Recht: " + rightName)
          }
    }

    const handleDeleteResourceType = async () => {
        if(hasRight(userStore, [rightName])){
            const answer = confirm("Möchten Sie diese Ressourcentyp wirklich löschen?")
            if (answer) {
                try {
                    const res = await deleteResourceType(selected.id)
                } catch (error) {
                    console.log(Object.keys(error), error.message)
                }
            }
        }else {//no rights!
            alert("Für diesen Vorgang besitzten Sie nicht die erforderlichen Rechte!\n\nBenötigtes Recht: " + rightName)
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
                            placeholder="Ressourcentypname"
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
                        {edit ? <Button style={{ marginRight: "10px" }} variant="danger" onClick={handleDeleteResourceType}>Löschen</Button> : null}
                        <Button style={{ marginRight: "10px" }} onClick={onCancel} variant="secondary">Abbrechen</Button>
                        {(edit ? edited ? 
                            <Button variant="success"  type="submit">Übernehmen</Button>:
                            null : <Button variant="success"  type="submit">Ressourcentype anlegen</Button>)
                        }
                    </Container>
                </Form>
            </Container>
        </React.Fragment>
    )
}
export default ResourceTypeForm