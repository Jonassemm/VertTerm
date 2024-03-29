//author: Patrick Venturini
import React, { useState, useEffect } from "react"
import { Container, Form, Col, Tabs, Tab, Button} from "react-bootstrap"
import {addResource, editResource, deleteResource, addConsumable, editConsumable, deleteConsumable } from "./ResourceRequests"
import ObjectPicker from "../../ObjectPicker"
import Availability from "../availabilityComponents/Availability"
import { hasRight } from "../../../auth"
import {resourceRights} from "../../Rights"


const RessourceForm = ({ onCancel, 
  edit, 
  selected, 
  setException, //Availability exception
  userStore}) => {
    const rightName = resourceRights[1] //write right
    const [valideForm, setValideForm] = useState(false)
    const [tabKey, setTabKey] = useState('general')  
    const [edited, setEdited] = useState(false)

    const [name, setName] = useState("")
    const [description, setDescription] = useState("")
    const [status, setStatus] = useState("active")
    const [resourceTypes, setResourceTypes] = useState([]) // Array with only one Item (for ObjectPicker)
    const [restrictions, setRestrictions] = useState([])
    const [availabilities, setAvailabilities] = useState([])

    const [isConsumable, setIsConsumable] = useState(false)
    const [amountInStock, setAmountInStock] = useState(null)
    const [pricePerUnit, setPricePerUnit] = useState(null)


    useEffect(() => {
      if (edit) {
          setName(selected.name)
          setDescription(selected.description)
          setStatus(selected.status)
          setResourceTypes(selected.resourceTypes)
          setAvailabilities(selected.availabilities)
          setRestrictions(selected.restrictions)
          //check if this item is a resource or a consumable
          if(selected.amountInStock == undefined && selected.pricePerUnit == undefined) {
            setIsConsumable(false) //item is resource
          } else {
            setIsConsumable(true) //item is consumable
            if(edit) {
              setAmountInStock((selected.amountInStock).toString())
              setPricePerUnit(((selected.pricePerUnit / 100).toFixed(2)).toString())
            }
          }
          if(selected.id == userStore.userID){
            setOwnUserView(true)
          }
      }
    }, [])

 
    const handleNameChange = event => {setName(event.target.value); setEdited(true)}
    const handleStatusChange = event => {setStatus(event.target.value); setEdited(true)}
    const handleDescriptionChange = event => {setDescription(event.target.value); setEdited(true)}
    const handleResourceTypeChange = data => {setResourceTypes(data); setEdited(true)}
    const handleRestrictionChange = data => {setRestrictions(data); setEdited(true)}
    const handleAmountInStockChange = event => {setAmountInStock(event.target.value); setEdited(true)}
    const handlePricePerUnitChange = event => {setPricePerUnit(event.target.value); setEdited(true)}


    const handleSubmit = async event => {
      event.preventDefault()

      if(hasRight(userStore, [rightName])) {  
        if(valideForm) {
          const amountInStockAsInt = parseInt(amountInStock)
          const pricePerUnitAsInt = parseInt((parseFloat(pricePerUnit)*100).toFixed(1))
          var data 
          if(isConsumable) {
            data = {name, description, status, availabilities, restrictions, resourceTypes,
                    amountInStock: amountInStockAsInt,
                    pricePerUnit: pricePerUnitAsInt}
            if(edit) {
              await editConsumable(selected.id, data)
              .then(res => {
                if (res.headers.exception) {
                  setException(res.headers.exception)
                }
                userStore.setMessage("Verbrauchbare Ressource erfolgreich geändert!")
              })
            }else {
              await addConsumable(data)
              userStore.setMessage("Verbrauchbare Ressource erfolgreich hinzugefügt!")
            }
          }else { //is not a Consumable
            data = {name, description, status, availabilities, restrictions, resourceTypes}
            if (edit){
              await editResource(selected.id, data)
              .then(res => {
                if (res.headers.exception) {
                  setException(res.headers.exception)
                }
                userStore.setMessage("Ressource erfolgreich geändert!")
              })
            }else{
              await addResource(data)
              userStore.setMessage("Ressource erfolgreich hinzugefügt!")
            }
          }
          onCancel()
        }
      }else {//no right to submit 
        userStore.setWarningMessage("Ihnen fehlt das Recht:\n"+ rightName)
      }
  }


  const handleDeleteRessource = async () => {
    if(hasRight(userStore, [rightName])){
      const answer = confirm("Möchten Sie diese Ressource wirklich löschen? ")
      if (answer) { 
        if(isConsumable){
          try{
            await deleteConsumable(selected.id)
            userStore.setMessage("Verbrauchbare Ressource erfolgreich gelöscht!")
          } catch (error){
            console.log(Object.keys(error), error.message)
          }
        } else {
          try{
            await deleteResource(selected.id)
            userStore.setMessage("Ressource erfolgreich gelöscht!")
          } catch (error){
            console.log(Object.keys(error), error.message)
          }
        }
      }
    }else {//no rights!
      userStore.setWarningMessage("Ihnen fehlt das Recht:\n"+ rightName)
    }
    onCancel()
  }


  //---------------------------------------Help-Functions--------------------------------------
  function validation() {
    var result = true;
    var consumableFieldsAreSet = true
    if(isConsumable) {
      consumableFieldsAreSet = (
        amountInStock != null &&
        pricePerUnit != null
      )
    }
    const generalFieldsAreSet = (
      name != "" &&
      resourceTypes.length == 1 &&
      (status == "active" || status == "inactive") &&
      consumableFieldsAreSet
    )
    
    //--------------------------General-VIEW-------------------------
    if(tabKey == "general"){
      //check resourceType
      if(resourceTypes.length == 0) { 
        result = false
        userStore.setWarningMessage("Ressourcentyp wurde nicht eingetragen!")
      }
    }

    //--------------------------Availability-VIEW-------------------------
    if(tabKey == "availability" && !generalFieldsAreSet){
      if(resourceTypes.length != 1) {
        userStore.setWarningMessage("Ressourcentyp wurde nicht eingetragen!")
      } else {
        userStore.setWarningMessage("Felder auf der Allgemein-Ansicht überprüfen!")
      }
      result = false
    }

    setValideForm(result)
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
            <h5 style={{fontWeight: "bold"}}>{edit ? isConsumable ? "Verbrauchbare Ressource bearbeiten":
                                                                  "Ressource bearbeiten" : isConsumable ?
                                                                  "Verbrauchbare Ressource hinzufügen": "Ressource hinzufügen"}</h5>
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
                  label={"Als verbrauchbare Ressource anlegen"}
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
                    multiple={true}
                    status={"NOTDELETED"} />
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
                  <Form.Label>Einschränkungen:</Form.Label>
                    <ObjectPicker 
                        setState={handleRestrictionChange}
                        DbObject="restriction"
                        initial={restrictions} 
                        multiple={true}/>
                </Form.Group>
                {isConsumable &&
                  <Form.Group as={Col} md="3" >
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
                }{isConsumable &&
                  <Form.Group as={Col} md="3" >
                    <Form.Label>Einzelpreis:</Form.Label>
                        <Form.Control
                          required={isConsumable ? true : false} 
                          pattern="(?:[1-9]{1}[0-9]{0,3}|0)[.]{1}[0-9]{2}"//only prices without leading zero
                          title="Der Preis darf keien führende 0 besitzten (Bsp.: 0.00 - 9999,99)!"
                          name="pricePerUnit"
                          type="text"
                          placeholder="0.00 bis 9999.99"
                          value={pricePerUnit || ""}
                          onChange={handlePricePerUnitChange}
                        />
                  </Form.Group>
                }
              </Form.Row>
            </Tab>
            <Tab eventKey="availability" title="Verfügbarkeit">
              <Form.Row style={{marginTop: "25px"}}>
                <Availability 
                  availabilities={availabilities} 
                  addAvailability={addAvailability}
                  updateAvailabilities={updateAvailabilities} 
                  editedAvailabilities={setEdited}
                  userStore={userStore}
                  selected={selected}
                />
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
                  <Button variant="success" type="submit" onClick={() => validation()}>Übernehmen</Button>:
                  null : <Button variant="success" type="submit" onClick={() => validation()}>Ressource anlegen</Button>)
                }
                </Container>
              </Form.Row>
        </Form>
      </Container>
    </React.Fragment>
  )
}
export default RessourceForm