import React ,{useState, useEffect} from 'react'
import {Form, Table, Col, Container, Button, InputGroup, Tabs, Tab } from 'react-bootstrap'
import Availability from "../availabilityComponents/Availability"
import ObjectPicker from "../ObjectPicker"

import {
  addEmployee,
  updateEmployee,
  addCustomer,
  updateCustomer
} from "./UserRequests";

import {
  getAllOptionalAttributes
} from "../optionalAttributesComponents/optionalAttributesRequests"


function UserForm({onCancel, edit, selected, type}) {
  //Editing
  const [edited, setEdited] = useState(false)

  //Switch
  var initialTypeIsEmployee = false
  if(type == "employee") {
      initialTypeIsEmployee = true
  }
  const [isEmployee, setIsEmployee] = useState(initialTypeIsEmployee)

  //Tabs
  const [tabKey, setTabKey] = useState('general')

  //User
  const [firstName, setFirstname] = useState("")
  const [lastName, setLastname] = useState("")
  const [username, setUsername] = useState("") 
  const [password, setPassword] = useState("")
  const [systemStatus, setSystemStatus] = useState("active")
  //Availability
  const [availabilities, setAvailabilities] = useState([])
  //Position
  const [positions, setPositions] = useState([])
  //Role
  const [roles, setRoles] = useState([])
  //Restrictions
  const [restrictions, setRestrictions] = useState([])
  //extended userInformation
  const [extUserInfoAttList, setExtUserInfoAttList] = useState([])
  const [extUserInfoData, setExtUserInfoData] = useState([])
  const [extUserInfoInput, setUserInfoInput] = useState() //needed to render page when editing any extUserInfo
  
  //HANDEL CHANGE
  const handleFirstnameChange = event => {setFirstname(event.target.value); setEdited(true)}
  const handleLastnameChange = event => {setLastname(event.target.value); setEdited(true)}
  const handleUsernameChange = event => {setUsername(event.target.value); setEdited(true)}
  const handlePasswordChange = event => {setPassword(event.target.value); setEdited(true)}
  const handleSystemStatusChange = event => {setSystemStatus(event.target.value); setEdited(true)}
  const handlePositionsChange = data => {setPositions(data); setEdited(true)}
  const handleRoleChange = data => {setRoles(data); setEdited(true)}
  const handleRestrictionChange = data => {setRestrictions(data); setEdited(true)}
  const handleExtUserInfoDataChange = (e, name) => {
    setExtUserInfoDataValue(name, e.target.value); 
    setUserInfoInput([e.target.value, name]); 
    console.log(extUserInfoData)
    setEdited(true) 
  };

  useEffect(() => { 
    console.log("useEffect-Call: loadExtandUserInformation")
    loadExtUserInformation();
    if(edit) {
        setFirstname(selected.firstName)
        setLastname(selected.lastName)
        setUsername(selected.username)
        setPassword(selected.password)
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
    } 
  }, [])

  //---------------------------------LOAD---------------------------------

  //EXTUSERINFO
  const loadExtUserInformation = async () => {
    var data = [];
    try{ 
      const response = await getAllOptionalAttributes();
      data = response.data;
    }catch (error) {
      data = [{id:"1", name:"E-Mail", isRequired: true},{id:"2", name:"Telefon-Nr.", isRequired: false}]
      console.log(Object.keys(error), error.message)
      //alert("An error occoured while loading extendUserInformation")
    }
    data.map((info) => {
        setExtUserInfoAttList(extUserInfoAttList => [...extUserInfoAttList, info]);
    })
    //initial the ExtUserInfoData-Array for submit
    const initialData = data.map(name => {
      return {
        ...name,
        value: ""
      }
    })
    setExtUserInfoData(initialData)
  };

  function validation() {
    var result = true;
    var errorMsg 
    //check position
    if(isEmployee){
      if(positions.length == 0) { 
        result = false
        errorMsg = "noPosition"
      }
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


  //---------------------------------SUBMIT---------------------------------
  //ADD USER
  const handleSubmit = async event => {
    event.preventDefault();//reload the page after clicking "Enter"
    if(validation()) {
      if(edit) {
        var id = selected.id
        var updateData = {}
        try {
          if(isEmployee){
              console.log("AXIOS: updateEmployee()")
              updateData = {id, firstName, lastName, username, password, systemStatus, roles, positions, availabilities, restrictions}
              await updateEmployee(id, updateData);
          }else {
              console.log("AXIOS: updateCustomer()")
              updateData = {id, firstName, lastName, username, password, systemStatus, roles, restrictions}
              await updateCustomer(id, updateData);
          }
        } catch (error) {
          console.log(Object.keys(error), error.message)
          alert("An error occoured while updating a user")
        } 
      } else {
          var newData = {}
          try {
          if(isEmployee){
              console.log("AXIOS: addEmployee()")
              newData = {firstName, lastName, username, password, systemStatus, roles, positions, availabilities, restrictions}
              await addEmployee(newData);
          }else {
              console.log("AXIOS: addCustomer()")
              newData = {firstName, lastName, username, password, systemStatus, roles, restrictions}
              await addCustomer(newData);
          }
          } catch (error) {
            console.log(Object.keys(error), error.message)
            alert("An error occoured while adding a user")
          } 
      } 
      onCancel()
    } 
  };

  //DELETE USER
  const handleDeleteUser = async () => {
    const deleteStatus = "deleted" // fix delteStatus
    const id = selected.id
    var data = {}
    const answer = confirm("Möchten Sie diesen Mitarbeiter wirklich löschen? ")
      if (answer) {
        try {
          if(isEmployee) { //delete employee
              data = {id, firstName: "", lastName: "", username: "", password: "", systemStatus: deleteStatus, roles: [], positions: [], availabilities: [], restrictions: []}
              await updateEmployee(id, data)
          } else { //delete customer
              data = {id, firstName: "", lastName: "", username: "", password: "", systemStatus: deleteStatus, roles: [], restrictions: []}
              await updateCustomer(id, data);
          }
        } catch (error) {
            console.log(Object.keys(error), error.message)
            alert("An error occoured while deleting a user")
        }
      }
        onCancel()
  }

  //---------------------------------ExtUserInfoData---------------------------------
  const setExtUserInfoDataValue = (name, value) => {
    extUserInfoData.map((info)=> {
      if(info.name == name) {
        info.value = value
      }
    })
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

  //---------------------------------RENDER---------------------------------
  // DYNAMIC extendetUserInforamtion table
  function renderExpandUserInformationTable() {
    if(extUserInfoAttList.length > 0)
    {
      return ( 
        extUserInfoAttList.map((info, index) =>(
          <tr key={index}>
            <td>{info.name}:</td>
            <td><Form.Control required={info.isRequired} onChange={e => handleExtUserInfoDataChange(e, info.name)} value={extUserInfoData.find(item => item.name == info.name).value || ""} name={"userInfo-value"+ index} type="text"/></td>
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
             <h5 style={{fontWeight: "bold"}}>{initialTypeIsEmployee ? edit ? "Mitarbeiter bearbeiten" : "Benutzer hinzufügen": edit ? "Kunde bearbeiten" : "Benutzer hinzufügen"}</h5>
          </Form.Group>
          <Form.Group as={Col} style={{textAlign: "right"}}>
            {!edit  && 
              <Form.Check //not needed while editing an user
                id="switchIsEmployee"
                type="switch"
                name="isEmployee"
                value={isEmployee}
                onChange={e => setIsEmployee(!isEmployee)}
                checked={isEmployee}
                label={isEmployee ? "Als Mitarbeiter anlegen": "Als Kunde anlegen"}
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
                          //pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}" //min 8 characters with one upper and one lower case letter
                          //title="Das Passwort muss mindestens 8 Zeichen lang sein und einen Groß- sowie Klein-Buchstaben enthalten"
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
                          {renderExpandUserInformationTable()}
                          </tbody>
                      </Table> 
                  </Form.Group>
              </Form.Row>
              <Form.Row>
              <Form.Group as={Col} md="5">
                  <Form.Label>Rollen:</Form.Label>
                  <Container style={{display: "flex", flexWrap: "nowrap"}}>
                    {isEmployee &&
                      <ObjectPicker 
                        setState={handleRoleChange}
                        DbObject="employeeRole"
                        initial={roles} 
                        multiple={true}
                      />
                    }{!isEmployee &&
                      <ObjectPicker // customer cannot choose ADMIN_ROLE
                        setState={handleRoleChange}
                        DbObject="customerRole"
                        initial={roles} 
                        multiple={true}
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
                    editedAvailabilities={setEdited}/>
                </Form.Row>
                </Tab>
            }
          </Tabs>
          <hr style={{ border: "0,5px solid #999999" }}/>
              <Form.Row>
                <Container style={{textAlign: "right"}}>
                {edit ? 
                  <Button variant="danger" onClick={handleDeleteUser} style={{marginRight: "20px"}}>Löschen</Button> :
                  null
                }
                <Button variant="secondary" onClick={onCancel} style={{marginRight: "20px"}}>Abbrechen</Button>
                {(edit ? edited ? 
                  <Button variant="success" type="submit">Übernehmen</Button>:
                  null : <Button variant="success"  type="submit">Benutzer anlegen</Button>)
                }
                </Container>
              </Form.Row>
        </Form>
      </Container>
    </React.Fragment>
  )
}

export default UserForm



    

