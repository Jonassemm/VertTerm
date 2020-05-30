import React ,{useState, useEffect} from 'react'
import {Form, Table, Col, Container, Button, InputGroup, Tabs, Tab } from 'react-bootstrap'
import Availability from "../availabilityComponents/Availability"
import ObjectPicker from "../ObjectPicker"

import {
  addEmployee,
  updateEmployee,
  addCustomer,
  updateCustomer,
  getAllRoles
} from "./UserRequests";


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
  const [position, setPosition] = useState([])
  //Role
  const [selectedRole, setSelectedRole] = useState(null)
  const [roles, setRoles] = useState([])
  const [choosableRoles, setChoosableRoles] = useState([]) //to avoid picking ADMIN_ROLE as customer
  //Restrictions
  //const [restrictions, setRestrictions] = useState([])
  //extended userInformation
  const [extUserInfoAttList, setExtUserInfoAttList] = useState([])
  const [extUserInfoData, setExtUserInfoData] = useState([])
  
  //HANDEL CHANGE
  const handleFirstnameChange = event => {setFirstname(event.target.value); setEdited(true)}
  const handleLastnameChange = event => {setLastname(event.target.value); setEdited(true)}
  const handleUsernameChange = event => {setUsername(event.target.value); setEdited(true)}
  const handlePasswordChange = event => {setPassword(event.target.value); setEdited(true)}
  const handleSystemStatusChange = event => {setSystemStatus(event.target.value); setEdited(true)}
  const handlePositionChange = data => {console.log(toString(data)); setPosition(data); setEdited(true)}
  const handleRoleChange = data => {console.log(toString(data)); setRoles(data); setEdited(true)}
  const handleExtUserInfoDataChange = (e, name) => {
    console.log(toString(name)); setExtUserInfoDataValue(name, e.target.value); setEdited(true)
  }

  useEffect(() => {
    console.log("useEffect-Call: loadChoosableRoles");
    loadChoosableRoles(); 
    console.log("useEffect-Call: loadExtandUserInformation")
    loadExtUserInformation();
    console.log("useEffect-Call: initialExtUserInfoData")
    initialExtUserInfoData();
    if(edit) {
        setFirstname(selected.firstName)
        setLastname(selected.lastName)
        setUsername(selected.username)
        setPassword(selected.password)
        setSystemStatus(selected.systemStatus)
        if(selected.roles != null) {
          setRoles(selected.roles)
        }
        if(selected.position != null) {
          setPosition(selected.position)
        }
        if(selected.availabilities != null || selected.availabilities != undefined) {
          setAvailabilities(selected.availabilities);
        }
    } 
  }, [])

  //---------------------------------ExtUserInfoData---------------------------------
  function initialExtUserInfoData() {
    extUserInfoAttList.map((attName) => {
      const data = {name: attName.name, value: ""}
      setExtUserInfoData(extUserInfoData => [...extUserInfoData, data])
    })
  }

  function setExtUserInfoDataValue(name, value) {
    extUserInfoData.map((info)=> {
      if(info.name == name) {
        info.value = value
      }
    })
  }

  function getExtUserInfoDataValue(name) {
    extUserInfoData.map((info) => {
      if(info.name == name) {
        return info.value;
      }
    })
  }



  //---------------------------------SUBMIT---------------------------------
  //ADD USER
  const handleSubmit = async event => {
    event.preventDefault();//reload the page after clicking "Enter"
    console.log(position)

    //ONLY FOR TESTING - will be removed at the time we add position as an array
    //get the first position of our position-array
    var firstPosition
    position.map((pos, index)=> {
      if(index == 0) {
        firstPosition = pos
      }
    })
    //ONLY FOR TESTING - END

    if(edit) {
      var id = selected.id
      var updateData = {}
      try {
        if(isEmployee){
            console.log("AXIOS: updateEmployee()")
            updateData = {id, firstName, lastName, username, password, systemStatus, roles, position: firstPosition, availabilities}
            await updateEmployee(id, updateData);
        }else {
            console.log("AXIOS: updateCustomer()")
            updateData = {id, firstName, lastName, username, password, systemStatus, roles}
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
            newData = {firstName, lastName, username, password, systemStatus, roles, position: firstPosition, availabilities}
            await addEmployee(newData);
        }else {
            console.log("AXIOS: addCustomer()")
            newData = {firstName, lastName, username, password, systemStatus, roles}
            await addCustomer(newData);
        }
        } catch (error) {
          console.log(Object.keys(error), error.message)
          alert("An error occoured while adding a user")
        } 
    } 
    onCancel()
  };

  //DELETE USER
  const handleDeleteUser = async () => {
    //ONLY FOR TESTING - will be removed at the time we add position as an array
    //get the first position of our position-array
    var firstPosition
    position.map((pos, index)=> {
      if(index == 0) {
        firstPosition = pos
      }
    })
    //ONLY FOR TESTING - END

    const deleteStatus = "deleted" // fix delteStatus
    const id = selected.id
    var data = {}
    const answer = confirm("Möchten Sie diesen Mitarbeiter wirklich löschen? ")
      if (answer) {
        try {
          if(isEmployee) { //delete employee
              data = {id, firstName, lastName, username, password, systemStatus: deleteStatus, roles, position: firstPosition, availabilities}
              await updateEmployee(id, data)
          } else { //delete customer
              data = {id, firstName, lastName, username, password, systemStatus: deleteStatus , roles}
              await updateCustomer(id, data);
          }
        } catch (error) {
            console.log(Object.keys(error), error.message)
            alert("An error occoured while deleting a user")
        }
      }
        onCancel()
  }

  const handleDeleteRessource = async () => {
    // fix delteStatus
    const deleteStatus = "deleted"
    //get the single type out of the array (array is needed for ObjectPicker)
    var resourceType = {}
    if(type.length > 0) { resourceType = type[0] }

    var data = {name, description, status : deleteStatus, resourceTyp: resourceType, childRessources: childResources, availabilities, restrictions, amountInStock, numberOfUses, pricePerUnit}
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

  //---------------------------------USER-ROLES---------------------------------
  //ADD USER ROLES (TABLE)
  const addRole = () => {
    if(selectedRole != null) {
      if(selectedRole.length > 0) {
        //choose the one single role in this array
        var selected = null
          selectedRole.map((role)=> {
            selected = role
          })

        if(roles.some(roles => roles.name === selected.name)) {
          alert("Rolle bereits vorhanden!");
        } else {
          if(choosableRoles.some(roles => roles.name === selected.name)) {
            choosableRoles.map((role, index) => {
              if(role.name == selected.name) {
                setRoles(roles => [...roles, role]);
                setEdited(true)
              }
            })
          }else {
            alert("Rolle darf nicht zugewiesen werden!")
          }
        }
      } else {
        alert("Bitte Rolle auswählen!")
      }
    } else {
      alert("Bitte Rolle auswählen!")
    }
  };

  //---------------------------------LOAD---------------------------------
  //ROLES
  const loadChoosableRoles = async () => {
    var data = [];
    try{ 
      const response = await getAllRoles();
      data = response.data;
    }catch (error) {
      console.log(Object.keys(error), error.message)
      alert("An error occoured while loading choosable roles")
    }
    data.map((role) => {
      if(isEmployee) {
        setChoosableRoles(choosableRoles => [...choosableRoles, role]);
      } else {
        if(role.name != "ADMIN_ROLE") { //user cannot add ADMIN_ROLE
          setChoosableRoles(choosableRoles => [...choosableRoles, role]);
        }
      }
    })
  };
  //ExtendUserInfo
  const loadExtUserInformation = async () => {
    var data = [];
    try{ 
      const response = await getAllExtUserInformation();
      data = response.data;
    }catch (error) {
      console.log(Object.keys(error), error.message)
      alert("An error occoured while loading extendUserInformation")
    }
    data.map((info) => {
        setExtUserInfoAttList(extUserInfoAttList => [...extUserInfoAttList, info]);
    })
  };

  
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


  // DYNAMIC ROLE-TABLE
  function renderExpandUserInformationTable() {
    if(extUserInfoAttList.length > 0)
    {
      return ( 
        extUserInfoAttList.map((info, index) =>(
          <tr key={index}>
            <td><Form.Control readOnly type="text" name={"userInfo-name"+ index} value={info.name}/></td>
            <td><Form.Control onChange={handleExtUserInfoDataChange(e, info.name)} value={getExtUserInfoDataValue(info.name) || ""} name={"userInfo-value"+ index} type="text"/></td>
          </tr>
        ))
      );
    }
  };

   /* //REMOVE USER ROLES (ROLE-TABLE)
   const removeRole = (index) => {
    roles.splice((index),1) // remove role at "index" and just remove "1" role
    setRoles([...roles])
    setEdited(true)
  }; */

   return (
    <React.Fragment>
      <Container>
        <Form id="employeeAdd" onSubmit={(e) => handleSubmit(e)}>
        <h5 style={{fontWeight: "bold"}}>{initialTypeIsEmployee ? edit ? "Mitarbeiter bearbeiten" : "Mitarbeiter hinzufügen": edit ? "Kunde bearbeiten" : "Kunde hinzufügen"}</h5>
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
              <Form.Row style={{margin: "10px 0px 10px 0px"}}>
                {!edit  && <Form.Check //not needed while editing an user
                    id="switchIsEmployee"
                    type="switch"
                    name="isEmployee"
                    value={isEmployee}
                    onChange={e => setIsEmployee(!isEmployee)}
                    checked={isEmployee}
                    label="Als Mitarbeiter anlegen"
                  />
                }
              </Form.Row>
              <Form.Row>
                <Form.Group as={Col} md="5">
                  <Form.Label>Rollen:</Form.Label>
                  <Container style={{display: "flex", flexWrap: "nowrap"}}>
                    <ObjectPicker 
                      setState={handleRoleChange}
                      DbObject="role"
                      initial ={roles} 
                      multiple ={true}
                    />
                  </Container>
                </Form.Group>
                {isEmployee &&
                  <Form.Group as={Col} md="5">
                    <Form.Label>Position:</Form.Label>
                    <Container style={{display: "flex", flexWrap: "nowrap"}}>       
                      <ObjectPicker 
                        setState={handlePositionChange}
                        DbObject="position"
                        initial ={position} 
                        multiple ={true}
                      />
                    </Container>
                  </Form.Group>
                }
              </Form.Row>
            </Tab>
            <Tab eventKey="expand" title="Erweitert">
              <Form.Row>
                  <Form.Group as={Col} md="5">
                      <Table style={{border: "2px solid #AAAAAA"}} striped hover variant="ligth">
                          <thead>
                              <tr>
                                  <th>Erweiterte Benutzerinformationen</th>
                              </tr>
                          </thead>
                          <tbody>
                          {renderExpandUserInformationTable()}
                          </tbody>
                      </Table> 
                  </Form.Group>
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



    

