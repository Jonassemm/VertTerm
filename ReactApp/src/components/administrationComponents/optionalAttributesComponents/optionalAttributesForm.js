//author: Patrick Venturini
import React, { useState, useEffect } from "react"
import {Form, Table, Container, Col, Button } from "react-bootstrap"
import {editOptionalAttributes} from "./optionalAttributesRequests"


const optionalAttributesForm = ({ onCancel, edit, selected, userStore }) => {
    const [name, setName] = useState("")
    const [mandatoryField, setMandatoryField] = useState(false)
    const [edited, setEdited] = useState(false)
    const [optionalAttributes, setOptionalAttributes] = useState([])
    

    useEffect(() => {
        if (edit) {
            setOptionalAttributes(selected.optionalAttributes)
        }
    }, [])
    

    const handleNameChange = (event) => {setName(event.target.value)}
    const handleMandatoryFieldChange = () =>{setMandatoryField(!mandatoryField)}


    const handleSubmit = async event => {
        event.preventDefault()
        let res = {}
        const data = {id: selected.id, optionalAttributes, classOfOptionalAttribut: selected.classOfOptionalAttribut}
        try{
            res = await editOptionalAttributes(data, selected.id)
            userStore.setMessage("Optionale Attribute erfolgreich geändert!")
        }catch (error) {
            console.log(Object.keys(error), error.message)
        }
        onCancel() 
    }


    //-----------------------------------Help-Functions----------------------------------
    const addAttribute = () => {
        var newAttribute = {name, mandatoryField}
        if(name != "") {
            if(optionalAttributes.some(optionalAttributes => optionalAttributes.name === newAttribute.name)) {
                userStore.setWarningMessage("Attribut bereits vorhanden!")
            } else {
                setOptionalAttributes(optionalAttributes => [...optionalAttributes, newAttribute])
                setEdited(true)
            }
        } else {
        userStore.setInfoMessage("Bitte Attribut eingeben!")
        }
        setName("")//reset inputfield
    };


    const removeAttribute = (index) => {
        optionalAttributes.splice((index),1) // remove attribute at "index" and just remove "1" attribute
        setOptionalAttributes([...optionalAttributes])
        setEdited(true)
    };


    //---------------------------------RENDER---------------------------------
    function renderOptionalAttributesTable() {
        if(optionalAttributes.length > 0) {
            return ( 
                optionalAttributes.map((attribute, index) =>(
                <tr key={index}>
                    <td>{attribute.name}</td>
                    <td>{attribute.mandatoryField ? "Ja" : "Nein"}</td>
                    <td><Button onClick={()=>removeAttribute(index)} id={attribute.name}>Entfernen</Button></td>
                </tr>
                ))
            );
        } else {
            return (
                <tr>
                    <td colSpan="3" style={{textAlign: "center"}}>Keine Attribute definiert</td>  
                </tr>
            )
        }
    };


    return (
        <React.Fragment>
            <Container>
                <Form onSubmit={handleSubmit}>
                    <Form.Row>
                        <Form.Group as={Col} md="8">
                            <Form.Label>Bezeichnung: </Form.Label>
                            <Form.Control
                                type="text"
                                placeholder="Attributname"
                                value={name || ""}
                                onChange={handleNameChange}
                            />
                        </Form.Group>
                        <Form.Group as={Col} md="2">
                            <Form.Check
                                style={{marginTop:"40px"}}
                                name="mandatoryField" 
                                label="Pflichtfeld"
                                type='checkbox' 
                                checked={mandatoryField || false}
                                onChange={handleMandatoryFieldChange} 
                            />
                        </Form.Group>
                        <Button style={{height:"40px", marginTop:"32px"}}onClick={addAttribute}>Hinzufügen</Button>
                    </Form.Row>
                    <Form.Row>
                        <Form.Group as={Col} md="12">
                            <Table style={{border: "2px solid #AAAAAA", marginTop: "10px", width: "100%", borderCollapse: "collapse", tableLayout: "fixed"}} bordered striped variant="ligth">
                                <thead>
                                    <tr>
                                        <th>Bezeichnung</th>
                                        <th style={{width: "240px"}}>Pflichtfeld</th>
                                        <th style={{width: "120px"}}></th>
                                    </tr>
                                </thead>
                                <tbody>
                                {renderOptionalAttributesTable()}
                                </tbody>
                            </Table> 
                        </Form.Group>
                    </Form.Row>
                    <hr style={{ border: "0,5px solid #999999" }}/>
                    <Container style={{textAlign: "right"}}>
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
export default optionalAttributesForm