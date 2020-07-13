//author: Patrick Venturini
import React ,{useState, useEffect} from 'react'
import {Form, Table, Col, Container, Button, InputGroup, Tabs, Tab } from 'react-bootstrap'
import Availability from "../availabilityComponents/Availability"
import ObjectPicker from "../../ObjectPicker"
import { hasRight } from "../../../auth"
import {userRights, ownUserRights, roleRights} from "../../Rights"

import {
  addEmployee,
  updateEmployee,
  deleteEmployee,
  addCustomer,
  updateCustomer,
  deleteCustomer
} from "./UserRequests";
import {getOptionalAttributes} from "../../requests"


function UserForm({onCancel, 
  edit, 
  selected, 
  type,           //type could be "emplyee" or "customer"
  setException,   //Availability exception
  userStore}) {

  var initialTypeIsEmployee = false
  if(type == "employee") {
      initialTypeIsEmployee = true
  }

  const rightNameOwn = ownUserRights[1] //write right
  const rightName = userRights[1] //write right
  const roleRight = roleRights[1] //write right
  
  const [isEmployee, setIsEmployee] = useState(initialTypeIsEmployee)
  const [valideForm, setValideForm] = useState(false)
  const [edited, setEdited] = useState(false)
  const [tabKey, setTabKey] = useState('general')

  const [firstName, setFirstname] = useState("")
  const [lastName, setLastname] = useState("")
  const [username, setUsername] = useState("") 
  const [password, setPassword] = useState("")
  const [passwordChanged, setPasswordChanged] = useState(false)
  const [systemStatus, setSystemStatus] = useState("active")
  const [availabilities, setAvailabilities] = useState([])
  const [positions, setPositions] = useState([])
  const [roles, setRoles] = useState([])
  const [restrictions, setRestrictions] = useState([])

  const [optionalAttributesOfUser, setOptionalAttributesOfUser] = useState([])
  const [optionalAttributInput, setOptionalAttributeInput] = useState() //needed to render page when editing any optional attribute
  const [requiredOptionalAttributCounter, setRequiredOptionalAttributCounter] = useState(0)
  const [ownUserView, setOwnUserView] = useState(false)
  const [roleChangeAllow, setRoleChangeAllow] = useState(false)


  useEffect(() => { 
    if(edit) {
        setFirstname(selected.firstName)
        setLastname(selected.lastName)
        setUsername(selected.username)
        setPassword("Password1") //only for the view
        setSystemStatus(selected.systemStatus)
        if(selected.roles != null && selected.roles.length > 0) {
          setRoles(selected.roles)
        }
        if(selected.restrictions != null && selected.restrictions.length > 0) {
          setRestrictions(selected.restrictions)
        }
        if(isEmployee && selected.positions != null && selected.positions.length > 0) {
          setPositions(selected.positions)
        }
        if(selected.availabilities != null && selected.availabilities.length > 0 ) {
          setAvailabilities(selected.availabilities);
        }
        if(selected.id == userStore.userID){
          setOwnUserView(true)
        }
        if(hasRight(userStore, [roleRight])){
          setRoleChangeAllow(true)
        }else{
          setRoleChangeAllow(false)
        }
    } 
    loadOptionalAttributes()
  }, [])


  const handleFirstnameChange = event => {setFirstname(event.target.value); setEdited(true)}
  const handleLastnameChange = event => {setLastname(event.target.value); setEdited(true)}
  const handleUsernameChange = event => {setUsername(event.target.value); setEdited(true)}
  const handlePasswordChange = event => {setPasswordChanged(true); setPassword(event.target.value); setEdited(true)}
  const handleSystemStatusChange = event => {setSystemStatus(event.target.value); setEdited(true)}
  const handlePositionsChange = data => {setPositions(data); setEdited(true)}
  const handleRestrictionChange = data => {setRestrictions(data); setEdited(true)}
  
  
  const handleRoleChange = data => {
    if(hasRight(userStore, [roleRight])){
      setRoles(data); setEdited(true)
    }else{
      userStore.setWarningMessage("Ihnen fehlt das Recht:\n"+ roleRight)
    }
  }

  
  const handleOptionalAttributesChange = (e, name) => {
    setOptionalAttributeValue(name, e.target.value); 
    setOptionalAttributeInput([e.target.value, name]); //for rendering
    setEdited(true) 
  };


  const handleSubmit = async event => {
    event.preventDefault();//reload the page after clicking "Enter"
    
    var newPassword = ""
    if(passwordChanged) {
      newPassword = password
    }

    if(valideForm) {
      if(edit) { //editing-mode
        if(checkRights()){
          var id = selected.id
          var updateData = {}
          try {
            if(isEmployee){ //employee
                updateData = {id, firstName, lastName, username, password: newPassword, systemStatus, roles, positions, availabilities, restrictions, 
                              optionalAttributes: optionalAttributesOfUser}
                await updateEmployee(id, updateData)
                  .then(res => {
                    if (res.headers.exception) {
                      setException(res.headers.exception)
                    }
                    userStore.setMessage("Mitarbeiter erfolgreich geändert!")
                  })
            }else { //customer
                updateData = {id, firstName, lastName, username, password: newPassword, systemStatus, roles, restrictions,
                              optionalAttributes: optionalAttributesOfUser}
                await updateCustomer(id, updateData);
                userStore.setMessage("Kunde erfolgreich geändert!")
            }
          } catch (error) {
            console.log(Object.keys(error), error.message)
          } 
        }else {//no right -> submit not allowed 
          noRights()
        }
      } else { //creation-mode
          var newData = {}
          try {
            if(isEmployee){ //employee
                newData = {firstName, lastName, username, password, systemStatus, roles, positions, availabilities, restrictions,
                            optionalAttributes: optionalAttributesOfUser}
                await addEmployee(newData);
                userStore.setMessage("Mitarbeiter erfolgreich hinzugefügt!")
            }else { //customer
                newData = {firstName, lastName, username, password, systemStatus, roles, restrictions, 
                            optionalAttributes: optionalAttributesOfUser}
                await addCustomer(newData);
                userStore.setMessage("Kunde erfolgreich hinzugefügt!")
            }
          } catch (error) {
            console.log(Object.keys(error), error.message)
          } 
      } 
      onCancel()
    } 
  };


  const handleDeleteUser = async () => {
    if(edit) {
      if(checkRights()){
        if(isEmployee){
          const answer = confirm("Möchten Sie diesen Mitarbeiter wirklich löschen? ")
          if (answer) {
            try{
              await deleteEmployee(selected.id)
              userStore.setMessage("Mitarbeiter erfolgreich gelöscht!")
            } catch (error){
              console.log(Object.keys(error), error.message)
              userStore.setErrorMessage("Mitarbeiter kann mit bestehenden Terminen nicht gelöscht werden!")
            }
          }
        }else {
          const answer = confirm("Möchten Sie diesen Kunden wirklich löschen? ")
          if (answer) {
            try{
              await deleteCustomer(selected.id)
              userStore.setMessage("Kunde erfolgreich geändert!")
            } catch (error){
              console.log(Object.keys(error), error.message)
              userStore.setErrorMessage("Kunde kann mit bestehenden Terminen nicht gelöscht werden!")
            }
          }
        }
      }else {//no rights!
        noRights()
      }
    }
    onCancel()
  }


  //---------------------------------LOAD---------------------------------
  const loadOptionalAttributes = async () => {
    var data = [];
    try{ 
      const response = await getOptionalAttributes();
      data = response.data;
    }catch (error) {
      console.log(Object.keys(error), error.message)
    }
    //combine the loaded attributes list from user with the predefined attribute list for every user
    if(data.length > 0) {
      data.map((attributeList) => {
        if(attributeList.classOfOptionalAttribut == "User" && attributeList.optionalAttributes.length > 0) {
          attributeList.optionalAttributes.map(attribute => {
            var initialValue = ""
            //set values of user attributes into the predefined list (edit-mode)
            if(edit && selected.optionalAttributes.length > 0) {
              selected.optionalAttributes.map(loadedAttribute =>{
                if(loadedAttribute.name == attribute.name) {
                  initialValue = loadedAttribute.value //Change initialValue when loading attributes
                }
              })
            }
            //increase counter to the number of required predefined attributes
            if(attribute.mandatoryField){
              incrementRequiredCounter()
            }
            const {name, mandatoryField} = attribute
            const initializedAttribute = {name, mandatoryField, value: initialValue}
            //save the list with the new initialValue
            setOptionalAttributesOfUser(optionalAttributesUserList => [...optionalAttributesUserList, initializedAttribute]);
          })
        }
      })
    }
  };


  //---------------------------------VALIDATION---------------------------------
  function validation() {
    var result = true;
    var employeeFieldsAreSet = true
    if(isEmployee) {
      employeeFieldsAreSet = (
        positions.length != 0 &&
        roles.length != 0
      )
    }
    const generalFieldsAreSet = (
      firstName != "" &&
      lastName != "" &&
      username != "" &&
      password != "" &&
      (systemStatus == "active" || systemStatus == "inactive") &&
      requiredOptionalAttributCounter == 0 &&
      employeeFieldsAreSet
    )
    //--------------------------General-VIEW-------------------------
    if(tabKey == "general"){
      if(isEmployee){
        if(roles.length == 0) { //check roles
          result = false
          userStore.setWarningMessage("Bitte mindestens eine Rolle wählen!")

        }else if(positions.length == 0) { //check positions
          result = false
          userStore.setWarningMessage("Bitte mindestens eine Position wählen!")
        }
      }else {
        if(roles.length == 0) { //check roles
          result = false
          userStore.setWarningMessage("Bitte mindestens eine Rolle wählen!")
        }
      }
    }
    //--------------------------Availability-VIEW---------------------
    if(tabKey == "availability" && !generalFieldsAreSet){
      if(isEmployee){
        if(requiredOptionalAttributCounter != 0){
          userStore.setWarningMessage("Optionales Attribut wurde nicht eingetragen!")
        } else if(roles.length == 0) { //check roles
          userStore.setWarningMessage("Rolle wurde nicht eingetragen!")
        }else if(positions.length == 0) { //check positions
          userStore.setWarningMessage("Position wurde nicht eingetragen!")
        }else {
          userStore.setWarningMessage("Felder auf der Allgemein-Ansicht überprüfen!")
        }
      }else {
        if(requiredOptionalAttributCounter != 0){
          userStore.setWarningMessage("Optionales Attribute wurde nicht eingetragen!")
        } else if(roles.length == 0) { //check roles
          userStore.setWarningMessage("Rolle wurde nicht eingetragen!")
        }else {
          userStore.setWarningMessage("Felder auf der Allgemein-Ansicht überprüfen!")
        }
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


  //-----------------------------Help-Functions----------------------------------
  const checkRights = () => {
    var rightExists = hasRight(userStore, [rightName])
    if(ownUserView && hasRight(userStore, [rightNameOwn])){
      rightExists = true
    }
    return rightExists
  }


  const noRights = () => {
    if(ownUserView){
        userStore.setWarningMessage("Ihnen fehlt das Recht:\n"+ rightNameOwn)
    }else {
        userStore.setWarningMessage("Ihnen fehlt das Recht:\n"+ rightName)
    }
  }

  
  const incrementRequiredCounter = () => {
    setRequiredOptionalAttributCounter(requiredOptionalAttributCounter => requiredOptionalAttributCounter + 1)
  }


  const decrementRequiredCounter = () => {
    setRequiredOptionalAttributCounter(requiredOptionalAttributCounter => requiredOptionalAttributCounter - 1)
  }


  const getRoleString = () => {
    var string = ""
    roles.map((role, index) => {
      if(index == 0){
        string += role.name
      }else{
        string += ", " + role.name
      }
    })
    return string
  }


  const setOptionalAttributeValue = (name, value) => {   
    if(optionalAttributesOfUser.length > 0) {
      optionalAttributesOfUser.map(attribute=> {
        if(attribute.name == name) {
          attribute.value = value
          if(attribute.mandatoryField) {
            if(value != "") {
              decrementRequiredCounter()
            }else{
              incrementRequiredCounter()
            }
          }
        }
      })
    }
  }


  //---------------------------------RENDER---------------------------------
  function renderOptionalAttributesTable() {
    if(optionalAttributesOfUser.length > 0) {
      return ( 
        optionalAttributesOfUser.map((info, index) =>(
          <tr key={index}>
            <td>{info.name}:</td>
            <td><Form.Control 
              required={info.mandatoryField} 
              onChange={e => handleOptionalAttributesChange(e, info.name)} 
              value={optionalAttributesOfUser.find(item => item.name == info.name).value || ""} 
              name={"userInfo-value"+ index} type="text"/>
            </td>
          </tr>
        ))
      );
    }
  };


  return (
    <React.Fragment>
      <Container>
        <Form id="employeeAdd" onSubmit={(e) => handleSubmit(e)}>
        <Form.Row style={{ alignItems: "baseline" }}>
          <Form.Group as={Col}>
             <h5 style={{fontWeight: "bold"}}>{initialTypeIsEmployee ? edit ? "Mitarbeiter bearbeiten" : "Mitarbeiter hinzufügen": edit ? "Kunde bearbeiten" : "Kunde hinzufügen"}</h5>
          </Form.Group>
        </Form.Row>
          <Tabs
            id="controlled-tab"
            activekey={tabKey}
            onSelect={key => setTabKey(key)}
          >
            <Tab eventKey="general" title="Allgemein">
              <Form.Row style={{marginTop: "25px"}}>
                <Form.Group as={Col} md="5" >
                  <Form.Label>Vorname:</Form.Label>
                  <Form.Control
                    required
                    //pattern="(?=.*[A-Z]{1})(?=.*[a-z]).{2,}"
                    //title="Vorname muss am Anfang groß geschrieben werden"
                    name="firstname"
                    type="text"
                    placeholder="Vorname"
                    value={firstName || ""}
                    onChange={handleFirstnameChange}
                  />
                  <Form.Control.Feedback>Passt!</Form.Control.Feedback>
                </Form.Group>
                <Form.Group as={Col} md="5">
                  <Form.Label>Nachname:</Form.Label>
                  <Form.Control
                    required
                    name="lastname"
                    //pattern="(?=.*[A-Z]{1})(?=.*[a-z]).{2,}"
                    //title="Nachname muss am Anfang groß geschrieben werden"
                    type="text"
                    placeholder="Nachname"
                    value={lastName || ""}
                    onChange={handleLastnameChange}
                  />
                  <Form.Control.Feedback>Passt!</Form.Control.Feedback>
                </Form.Group>
                
                <Form.Group as={Col} md="2">
                    <Form.Label>Benutzerstatus:</Form.Label>
                    <Form.Check
                        required
                        type="radio"
                        label="Aktiviert"
                        name="systemStatus"
                        value="active"
                        checked={systemStatus == "active"}
                        id="SystemStatusAktive"
                        onChange={handleSystemStatusChange}
                    />
                    <Form.Check
                        required
                        type="radio"
                        label="Deaktiviert"
                        name="systemStatus"
                        id="SystemStatusInaktive"
                        value="inactive"
                        checked={systemStatus == "inactive"}
                        onChange={handleSystemStatusChange}
                    />
                </Form.Group>
              </Form.Row>
              <Form.Row>
                <Form.Group as={Col} md="5" >
                    <Form.Label>Benutzername:</Form.Label>
                    <InputGroup>
                        <Form.Control
                          required
                          //pattern="[A-Za-z0-9]{1,}"
                          //title="Benutzername darf Klein- und Groß-Buchstaben sowie Zahlen enthalten"
                          name="username"
                          type="text"
                          placeholder="Benutzername"
                          value={username || ""}
                          onChange={handleUsernameChange}
                        />
                        <Form.Control.Feedback type="invalid">
                        Bitte geben Sie ein Benutzernamen ein.
                        </Form.Control.Feedback>
                    </InputGroup>
                </Form.Group>
                <Form.Group as={Col} md="5">
                    <Form.Label>Passwort:</Form.Label>
                    <InputGroup>
                        <Form.Control
                          required
                          //pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}" //min 8 characters with one upper and one lower case letter and one digit
                          //title="Das Passwort muss mindestens 8 Zeichen lang sein und einen Groß- sowie Klein-Buchstaben enthalten und mindestens eine Zahl"
                          name="password"
                          type="password"
                          placeholder="Passwort"
                          value={password || ""}
                          onChange={handlePasswordChange}
                        />
                        <Form.Control.Feedback type="invalid">
                        Bitte gebben Sie ein Passwort ein.
                        </Form.Control.Feedback>
                    </InputGroup>
                    </Form.Group>
              </Form.Row>
              <Form.Row>
                  <Form.Group as={Col} md="10">
                      <Table style={{border: "2px solid #AAAAAA", marginTop: "10px", width: "100%", borderCollapse: "collapse", tableLayout: "fixed"}} striped variant="ligth">
                          <thead>
                              <tr>
                                  <th colSpan="2">Optionale Attribute</th>
                              </tr>
                          </thead>
                          <tbody>
                          {renderOptionalAttributesTable()}
                          </tbody>
                      </Table> 
                  </Form.Group>
              </Form.Row>
              <Form.Row>
              <Form.Group as={Col} md="5">
                  <Form.Label>Rollen:</Form.Label>
                  <Container style={{display: "flex", flexWrap: "nowrap"}}>
                    {(isEmployee && roleChangeAllow || (isEmployee && !edit)) ?
                      <ObjectPicker 
                        setState={handleRoleChange}
                        DbObject="employeeRole"
                        initial={roles} 
                        multiple={true}
                      />: isEmployee &&
                      <Form.Control
                          readOnly
                          type="role"
                          placeholder="Keine Rolle"
                          value={getRoleString()}
                          onChange={handlePasswordChange}
                        />
                    }{(!isEmployee && roleChangeAllow || (!isEmployee && !edit)) ?
                      <ObjectPicker // customer cannot choose ADMIN_ROLE
                        setState={handleRoleChange}
                        DbObject="customerRole"
                        initial={roles} 
                        multiple={true}
                      />: (!isEmployee) &&
                      <Form.Control
                          readOnly
                          type="role"
                          placeholder="Keine Rolle"
                          value={getRoleString()}
                          onChange={handlePasswordChange}
                        />
                    }
                  </Container>
                </Form.Group>
                <Form.Group as={Col} md="5">
                  <Form.Label>Einschränkungen:</Form.Label>
                  <Container style={{display: "flex", flexWrap: "nowrap"}}>
                    <ObjectPicker 
                      setState={handleRestrictionChange}
                      DbObject="restriction"
                      initial={restrictions} 
                      multiple={true}
                    />
                  </Container>
                </Form.Group>
              </Form.Row>
              <Form.Row>
                {isEmployee &&
                  <Form.Group as={Col} md="5">
                    <Form.Label>Position:</Form.Label>
                    <Container style={{display: "flex", flexWrap: "nowrap"}}>       
                      <ObjectPicker 
                        setState={handlePositionsChange}
                        DbObject="position"
                        initial ={positions} 
                        multiple ={true}
                      />
                    </Container>
                  </Form.Group>
                }
              </Form.Row>
            </Tab>
            {isEmployee &&
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
            }
          </Tabs>
          <hr style={{ border: "0,5px solid #999999" }}/>
              <Form.Row>
                <Container style={{textAlign: "right"}}>
                {edit ? 
                  checkRights() &&
                  <Button variant="danger" onClick={handleDeleteUser} style={{marginRight: "20px"}}>Löschen</Button> :
                  null
                }
                <Button variant="secondary" onClick={onCancel} style={{marginRight: "20px"}}>Abbrechen</Button>
                {edit && edited && checkRights() &&
                  <Button variant="success" type="submit" onClick={() => validation()}>Übernehmen</Button>
                }
                {!edit && 
                  <Button variant="success"  type="submit" onClick={() => validation()}>Benutzer anlegen</Button>
                }
                </Container>
              </Form.Row>
        </Form>
      </Container>
    </React.Fragment>
  )
}
export default UserForm



    

