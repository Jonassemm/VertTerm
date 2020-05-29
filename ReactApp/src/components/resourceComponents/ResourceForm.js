import React, { useState, useEffect } from "react"
import { Container, Form, Table, Col, Tabs, Tab, Button} from "react-bootstrap"
import {addResource, editResource } from "./ResourceRequests"
import ObjectPicker from "../ObjectPicker"
import Availability from "../availabilityComponents/Availability"

const RessourceForm = ({ onCancel, edit, selected }) => {
    const [tabKey, setTabKey] = useState('general')  
    const [edited, setEdited] = useState(false)

    const [name, setName] = useState("")
    const [description, setDescription] = useState("")
    const [status, setStatus] = useState("active")
    const [type, setType] = useState([]) // Array with only one Item (for ObjectPicker)
    const [childResources, setChildResources] = useState([])
    const [restrictions, setRestrictions] = useState([])
    const [availabilities, setAvailabilities] = useState([])

    const [isConsumable, setIsConsumable] = useState(false)
    const [amountInStock, setAmountInStock] = useState(null)
    const [numberOfUses, setNumberOfUses] = useState(null)
    const [pricePerUnit, setPricePerUnit] = useState(null)

    useEffect(() => {
        if (edit) {
            setName(selected.name)
            setDescription(selected.description)
            setStatus(selected.status)
            setType(type => [selected.resourceTyp])
            setChildResources(selected.childResources)
            setAvailabilities(selected.availabilities)
        }
    }, [])
 
    const handleNameChange = event => {setName(event.target.value); setEdited(true)}
    const handleStatusChange = event => {setStatus(event.target.value); setEdited(true)}
    const handleDescriptionChange = event => {setDescription(event.target.value); setEdited(true)}
    //with ObjectPicker
    const handleTypeChange = data => {setType(data); setEdited(true)}
    const handleChildResourcesChange = data => {setChildResources(data); setEdited(true)}
    const handleRestrictionChange = data => {setRestrictions(data); setEdited(true)}

    const handleAmountInStockChange = event => {setAmountInStock(event.target.value); setEdited(true)}
    const handleNumberOfUsesChange = event => {setNumberOfUses(event.target.value); setEdited(true)}
    const handlePricePerUnitChange = event => {setPricePerUnit(event.target.value); setEdited(true)}

    const handleSubmit = async event => {
        event.preventDefault()
        //get the single type out of the array (array is needed for ObjectPicker)
        var resourceType = {}
        if(type.length > 0) { resourceType = type[0] }
        
        const data = {name, description, status, resourceTyp: resourceType, childRessources: childResources, availabilities, restrictions, amountInStock, numberOfUses, pricePerUnit}
       
        if (!edit) {
            await addResource(data)
        } else {
            await editResource(selected.id, data)
        }
        onCancel()
    }

    const handleDeleteRessource = async () => {
      // fix delteStatus
      const deleteStatus = "deleted"
      //get the single type out of the array (array is needed for ObjectPicker)
      var resourceType = {}
      if(type.length > 0) { resourceType = type[0] }

      data = {name, description, status : deleteStatus, resourceTyp: resourceType, childRessources: childResources, availabilities, restrictions, amountInStock, numberOfUses, pricePerUnit}
      const answer = confirm("Möchten Sie diese Ressource wirklich löschen? ")
      try {
        if (answer) {
          await editResource(selected.id, data)
        }
      } catch (error) {
        console.log(Object.keys(error), error.message)
        alert("An error occoured while deleting a resource")
      } 
      onCancel()
    }

    //---------------------------------Availability---------------------------------
    const addAvailability = (newAvailability) => {
      setAvailabilities(availabilities => [...availabilities, newAvailability]);
    }
  
    const updateAvailabilities = (newAvailabilities) => {
      setAvailabilities([])
      newAvailabilities.map((SingleAvailability)=> {
        setAvailabilities(availabilities => [...availabilities, SingleAvailability]);
      })
    }

    return (
        <React.Fragment>
      <Container>
        <Form id="resourceAdd" onSubmit={(e) => handleSubmit(e)}>
        <h5 style={{fontWeight: "bold"}}>{edit ? "Ressource bearbeiten" : "Ressource hinzufügen"}</h5>
          <Tabs
            id="controlled-tab"
            activekey={tabKey}
            onSelect={key => setTabKey(key)}
          >
            <Tab eventKey="general" title="Allgemein">
              <Form.Row style={{marginTop: "25px"}}>
                <Form.Group as={Col} md="6" >
                  <Form.Label>Bezeichnung:</Form.Label>
                  <Form.Control
                    required
                    name="name"
                    type="text"
                    placeholder="Ressourcenname"
                    value={name || ""}
                    onChange={handleNameChange}
                  />
                </Form.Group>
                <Form.Group as={Col} md="4">
                  <Form.Label>Ressourcentyp</Form.Label>
                  <ObjectPicker 
                          setState={handleTypeChange}
                          DbObject="resourceType"
                          initial={type}
                          multiple={false} />
                </Form.Group>
                
                <Form.Group as={Col} md="2">
                    <Form.Label>Ressourcenstatus:</Form.Label>
                    <Form.Check
                        required
                        type="radio"
                        label="Aktiviert"
                        name="status"
                        value="active"
                        checked={status == "active"}
                        id="ResourceStatusAktive"
                        onChange={handleStatusChange}
                    />
                    <Form.Check
                        required
                        type="radio"
                        label="Deaktiviert"
                        name="status"
                        id="ResourceStatusInaktive"
                        value="inactive"
                        checked={status == "inactive"}
                        onChange={handleStatusChange}
                    />
                </Form.Group>
              </Form.Row>
              <Form.Row>
                <Form.Group as={Col} md="12" >
                    <Form.Label>Beschreibung:</Form.Label>
                        <Form.Control
                        required
                        name="description"
                        type="text"
                        placeholder="Beschreibung der Ressource"
                        value={description || ""}
                        onChange={handleDescriptionChange}
                        />
                </Form.Group>
              </Form.Row>
              <Form.Row>
                <Form.Group as={Col} md="6">
                  <Form.Label>Unterressourcen:</Form.Label>
                    <ObjectPicker 
                        setState={handleChildResourcesChange}
                        DbObject="resource"
                        initial={childResources} 
                        multiple={true}/>
                </Form.Group>
                <Form.Group as={Col} md="6">
                  <Form.Label>Einschränkungen:</Form.Label>
                    <ObjectPicker 
                        setState={handleRestrictionChange}
                        DbObject="restriction"
                        initial={restrictions} 
                        multiple={true}/>
                </Form.Group>
              </Form.Row>
              <Form.Row style={{margin: "10px 0px 10px 0px"}}>
                <Form.Check
                  id="switchIsCunsumable"
                  type="switch"
                  name="isConsumable"
                  value={isConsumable || true}
                  onChange={e => setIsConsumable(!isConsumable)}
                  checked={isConsumable}
                  label="Als verbrauchbare Ressource anlegen"
                />
              </Form.Row>
              {isConsumable &&
                <Form.Row>
                  <Form.Group as={Col} md="2" >
                    <Form.Label>Bestandsmenge:</Form.Label>
                        <Form.Control
                          required={isConsumable ? true : false}
                          name="amountInStock"
                          type="text"
                          placeholder="10"
                          value={amountInStock || ""}
                          onChange={handleAmountInStockChange}
                        />
                  </Form.Group>
                  <Form.Group as={Col} md="2" >
                    <Form.Label>Verwendungsanzahl:</Form.Label>
                        <Form.Control
                          required={isConsumable ? true : false}
                          name="numberOfUses"
                          type="text"
                          placeholder="1"
                          value={numberOfUses || ""}
                          onChange={handleNumberOfUsesChange}
                        />
                  </Form.Group>
                  <Form.Group as={Col} md="2" >
                    <Form.Label>Einzelpreis:</Form.Label>
                        <Form.Control
                          required={isConsumable ? true : false}
                          name="pricePerUnit"
                          type="number"
                          step="any"
                          min="0"
                          placeholder="1,99"
                          value={pricePerUnit || ""}
                          onChange={handlePricePerUnitChange}
                        />
                  </Form.Group>
                </Form.Row>
              }
            </Tab>
            <Tab eventKey="availability" title="Verfügbarkeit">
              <Form.Row style={{marginTop: "25px"}}>
                <Availability 
                  availabilities={availabilities} 
                  addAvailability={addAvailability}
                  updateAvailabilities={updateAvailabilities} 
                  editedAvailabilities={setEdited}/>
              </Form.Row>
            </Tab>
          </Tabs>
          <hr style={{ border: "0,5px solid #999999" }}/>
              <Form.Row>
                <Container style={{textAlign: "right"}}>
                {edit ? 
                  <Button variant="danger" onClick={handleDeleteRessource} style={{marginRight: "20px"}}>Löschen</Button> :
                  null
                }
                <Button variant="secondary" onClick={onCancel} style={{marginRight: "20px"}}>Abbrechen</Button>
                {(edit ? edited ? 
                  <Button variant="success" type="submit">Übernehmen</Button>:
                  null : <Button variant="success"  type="submit">Ressource anlegen</Button>)
                }
                </Container>
              </Form.Row>
        </Form>
      </Container>
    </React.Fragment>
    )
}

export default RessourceForm