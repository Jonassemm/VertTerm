import React, { useState, useEffect } from "react"
import {Form} from "react-bootstrap"
import {Table, Container, Col, Button } from "react-bootstrap"
import {addOptionalAttributes, editOptionalAttributes, deleteOptionalAttributes} from "./optionalAttributesRequests"
import ObjectPicker from "../ObjectPicker"

const optionalAttributesForm = ({ onCancel, edit, selected }) => {
    const [name, setName] = useState("")
    const [mandatoryField, setMandatoryField] = useState(false)
    const [edited, setEdited] = useState(false)

    const [optionalAttributes, setOptionalAttributes] = useState([])
    const [classOfOptionalAttribut, setClassOfOptionalAttribut] = useState([])

    useEffect(() => {
        if (edit) {
            setOptionalAttributes(selected.optionalAttributes)
            setClassOfOptionalAttribut(selected.classOfOptionalAttribut)
        }
    }, [])

    const handleNameChange = (event) => {setName(event.target.value); setEdited(true)}
    const handleMandatoryFieldChange = () =>{setMandatoryField(!mandatoryField); setEdited(true)}
    const handleClassOfOptionalAttributChange = data => {setClassOfOptionalAttribut(data); setEdited(true)}

    function validation() {
        var result = true;
        var errorMsg 
        //check classOfOptionalAttribut
        if(classOfOptionalAttribut.length == 0) { 
        result = false
        errorMsg = "noClass"
        }

        //check roles
        if(roles.length == 0) { 
          result = false
          errorMsg = "noRules"
        }
        //print error
        switch(errorMsg) {
          case "noRules": 
            alert("Fehler: Bitte wählen Sie mindestens eine Rolle aus!")
            break;
          case "noPosition":
            alert("Fehler: Bitte wählen Sie mindestens eine Position aus!")
            break;
        }
    
        return result
      }


    const handleSubmit = async event => {
        event.preventDefault()
        if(validation()) {
            let res = {}
            if (!edit) {
                const data = {optionalAttributes, classOfOptionalAttribut: classOfOptionalAttribut[0].name}
                res = await addOptionalAttributes(data)
            } else {
                var id = selected.id
                const data = {id, optionalAttributes, classOfOptionalAttribut: classOfOptionalAttribut[0].name}
                res = await editOptionalAttributes(id, data)
            }
            console.log(res)
            onCancel()
        }
    }

    const handleDeleteOptionalAttributes = async () => {
        /* const answer = confirm("Möchten Sie dieses optionales Attribut wirklich löschen? ")
        if (answer) {
            const res = await deleteOptionalAttributes(selected.id)
            console.log(res)
        }
        onCancel() */
        console.log("OptAttribute:")
        console.log(optionalAttributes)
        console.log("klasse:")
        console.log(classOfOptionalAttribut)
    }

  const addAttribute = () => {
    var newAttribute = {name, mandatoryField}
    if(name != "") {
        if(optionalAttributes.some(optionalAttributes => optionalAttributes.name === newAttribute.name)) {
            alert("Attribut bereits vorhanden!");
        } else {
            setOptionalAttributes(optionalAttributes => [...optionalAttributes, newAttribute])
            setEdited(true)
        }
    } else {
      alert("Bitte Attribut eingeben!")
    }
  };

  //REMOVE Attribute from Table
  const removeAttribute = (index) => {
    optionalAttributes.splice((index),1) // remove attribute at "index" and just remove "1" attribute
    setOptionalAttributes([...optionalAttributes])
    setEdited(true)
  };





    //---------------------------------RENDER---------------------------------
    // DYNAMIC extendetUserInforamtion table
    function renderOptionalAttributesTable() {
        if(optionalAttributes.length > 0)
        {
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
            <td colSpan="3">Leer</td>
        }
    };

    return (
        <React.Fragment>
            <Container>
                <Form onSubmit={handleSubmit}>
                    <Form.Row>
                    <Form.Group as={Col} md="4">
                            <Form.Label>Klasse: </Form.Label>
                            <ObjectPicker 
                                setState={handleClassOfOptionalAttributChange}
                                DbObject="objectClass"
                                initial={classOfOptionalAttribut} 
                                multiple={false}
                            />
                        </Form.Group>
                    </Form.Row>
                    <hr/>
                    <Form.Row>
                        <Form.Group as={Col} md="4">
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
                        <Form.Group as={Col} md="2">
                            <Form.Check
                                style={{marginTop:"40px"}}
                                name="mandatoryField" 
                                label="Pflichtfeld"
                                type='checkbox' 
                                checked={mandatoryField || false}
                                //value={mandatoryField}
                                onChange={handleMandatoryFieldChange} 
                            />
                        </Form.Group>
                        <Button style={{height:"40px", marginTop:"32px"}}onClick={addAttribute}>Hinzufügen</Button>
                    </Form.Row>
                    <Form.Row>
                        <Form.Group as={Col} md="12">
                            <Table style={{border: "2px solid #AAAAAA", marginTop: "10px", width: "100%", borderCollapse: "collapse", tableLayout: "fixed"}} striped variant="ligth">
                                <thead>
                                    <tr>
                                        <th>Bezeichnung</th>
                                        <th>Pflichtfeld</th>
                                        <th></th>
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
                        {edit ? <Button style={{ marginRight: "10px" }} variant="danger" onClick={handleDeleteOptionalAttributes}>Löschen</Button> : null}
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