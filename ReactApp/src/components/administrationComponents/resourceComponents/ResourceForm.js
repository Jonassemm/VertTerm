import React, { useState, useEffect } from "react"
import { Container, Form, Table, Col, Tabs, Tab, Button} from "react-bootstrap"
import {addResource, editResource, addConsumable, editConsumable } from "./ResourceRequests"
import ObjectPicker from "../../ObjectPicker"
import Availability from "../availabilityComponents/Availability"

const RessourceForm = ({ onCancel, edit, selected }) => {
    const [tabKey, setTabKey] = useState('general')  
    const [edited, setEdited] = useState(false)

    const [name, setName] = useState("")
    const [description, setDescription] = useState("")
    const [status, setStatus] = useState("active")
    const [resourceTypes, setResourceTypes] = useState([]) // Array with only one Item (for ObjectPicker)
    const [childResources, setChildResources] = useState([])
    const [restrictions, setRestrictions] = useState([])
    const [availabilities, setAvailabilities] = useState([])

    const [isConsumable, setIsConsumable] = useState(false)
    const [amountInStock, setAmountInStock] = useState(null)
    const [numberOfUses, setNumberOfUses] = useState(null)
    const [pricePerUnit, setPricePerUnit] = useState(null)

    useEffect(() => {
      //if one item was selected update the attribute "isConsumable"
      if(selected != undefined) {
        //check if this item is a resource or a consumable
        if(selected.amountInStock == undefined && 
          selected.numberOfUses == undefined &&
          selected.pricePerUnit == undefined) {
          setIsConsumable(false) //item is resource
          } else {
            setIsConsumable(true) //item is consumable
            if(edit) {
              setAmountInStock((selected.amountInStock).toString())
              setNumberOfUses((selected.numberOfUses).toString())
              setPricePerUnit(((selected.pricePerUnit / 100).toFixed(2)).toString())
            }
          }
      }
      if (edit) {
          setName(selected.name)
          setDescription(selected.description)
          setStatus(selected.status)
          setResourceTypes(selected.resourceTypes)
          setChildResources(selected.childResources)
          setAvailabilities(selected.availabilities)
          setRestrictions(selected.restrictions)
      }
    }, [])
 
    const handleNameChange = event => {setName(event.target.value); setEdited(true)}
    const handleStatusChange = event => {setStatus(event.target.value); setEdited(true)}
    const handleDescriptionChange = event => {setDescription(event.target.value); setEdited(true)}
    //with ObjectPicker
    const handleResourceTypeChange = data => {setResourceTypes(data); setEdited(true)} //setResourceTypes(resourceTypes => [...resourceTypes, data]);
    const handleChildResourcesChange = data => {setChildResources(data); setEdited(true)}
    const handleRestrictionChange = data => {setRestrictions(data); setEdited(true)}

    const handleAmountInStockChange = event => {console.log(parseInt(event.target.value)); setAmountInStock(event.target.value); setEdited(true)}
    const handleNumberOfUsesChange = event => {console.log(parseInt(event.target.value)); setNumberOfUses(event.target.value); setEdited(true)}
    const handlePricePerUnitChange = event => {console.log(parseInt((parseFloat(event.target.value)*100).toFixed(1))); setPricePerUnit(event.target.value); setEdited(true)}


    function validation() {
      var result = true;
      var errorMsg 
      //check resourceType
      if(resourceTypes.length == 0) { 
        result = false
        errorMsg = "noResourceType"
      }
      //print error
      switch(errorMsg) {
        case "noResourceType": 
          alert("Fehler: Bitte wählen Sie einen Ressourcentyp aus!")
          break;
      }
      return result
    }


    //---------------------------------SUBMIT---------------------------------
    //ADD RESOURCE
    const handleSubmit = async event => {
        event.preventDefault()
        if(validation()) {
          const amountInStockAsInt = parseInt(amountInStock)
          const numberOfUsesAsInt = parseInt(numberOfUses)
          const pricePerUnitAsInt = parseInt((parseFloat(pricePerUnit)*100).toFixed(1))
          var data 
          if(isConsumable) {
            data = {name, description, status, childResources, availabilities, restrictions, resourceTypes,
                    amountInStock: amountInStockAsInt,
                    numberOfUses: numberOfUsesAsInt,
                    pricePerUnit: pricePerUnitAsInt}
            if(edit) {
              await editConsumable(selected.id, data)
            }else {
              await addConsumable(data)
            }
          }else {
            data = {name, description, status, childResources, availabilities, restrictions, resourceTypes}
            if (edit){
              await editResource(selected.id, data)
            }else{
              await addResource(data)
            }
          }
          onCancel()
        }
    }


    //DELETE RESOURCE
    const handleDeleteRessource = async () => {
      const answer = confirm("Möchten Sie diese Ressource wirklich löschen? ")
      if (answer) {
        const deleteStatus = "deleted" // fix delteStatus
        const amountInStockAsInt = parseInt(amountInStock)
        const numberOfUsesAsInt = parseInt(numberOfUses)
        const pricePerUnitAsInt = parseInt((parseFloat(pricePerUnit)*100).toFixed(1))
        var data
        try {
          if(isConsumable) {
            data = {name, description, status,  childResources, availabilities, restrictions, resourceTypes,
              amountInStock: amountInStockAsInt,
              numberOfUses: numberOfUsesAsInt,
              pricePerUnit: pricePerUnitAsInt}
            await editConsumable(selected.id, data)
          } else {
            data = {name, description, status, childResources, availabilities, restrictions, resourceTypes}
            await editResource(selected.id, data)
          }
        } catch (error) {
          console.log(Object.keys(error), error.message)
        } 
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
          <Form.Row style={{ alignItems: "baseline" }}>
            <Form.Group as={Col}>
              <h5 style={{fontWeight: "bold"}}>{edit ? "Ressource bearbeiten" : "Ressource hinzufügen"}</h5>
            </Form.Group>
            <Form.Group as={Col} style={{textAlign: "right"}}>
              {!edit && 
                  <Form.Check
                    id="switchIsCunsumable"
                    type="switch"
                    name="isConsumable"
                    value={isConsumable || true}
                    onChange={e => setIsConsumable(!isConsumable)}
                    checked={isConsumable}
                    label={isConsumable ? "Als verbrauchbare Ressource anlegen": "Als normale Ressource anlegen"}
                  />
              }
            </Form.Group>
          </Form.Row>
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
                      pattern=".{1,50}"//everything allowed but min 1 and max 50 letters
                      title="Die Bezeichnung muss zwischen 1 und 50 Zeichen beinhalten!"
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
                      setState={handleResourceTypeChange}
                      DbObject="resourceType"
                      initial={resourceTypes}
                      multiple={true} />
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
                      {edit && <ObjectPicker 
                          setState={handleChildResourcesChange}
                          DbObject="childResource"
                          initial={childResources} 
                          multiple={true}
                          selectedItem={selected}/>
                      }
                      {!edit && <ObjectPicker 
                          setState={handleChildResourcesChange}
                          DbObject="childResource"
                          initial={childResources} 
                          multiple={true}/>
                      }
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
                {isConsumable &&
                  <Form.Row>
                    <Form.Group as={Col} md="2" >
                      <Form.Label>Bestandsmenge:</Form.Label>
                          <Form.Control
                            required={isConsumable ? true : false}
                            pattern="[0-9]{1,}" //Only numbers but at least one
                            title="Die Bestandsmenge muss mindestens 1 betragen!"
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
                            pattern="[0-9]{1,}" //Only numbers but at least one
                            title="Die Anzahl bis eine Ressource aufgebraucht ist muss mindestens 1 sein!"
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
                            pattern="(?:[1-9]{1}[0-9]{0,3}|0)[.]{1}[0-9]{2}"//only prices without leading zero
                            title="Der Preis muss das Format darf keien führende 0 besietzten (Bsp.: 0.00 - 9999,99)!"
                            name="pricePerUnit"
                            type="text"
                            placeholder="0.00 bis 9999.99"
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