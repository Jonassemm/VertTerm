import React ,{useState, useEffect} from 'react'
import {Form, Table, Col, Container, Button, InputGroup, Tabs, Tab } from 'react-bootstrap'
import Availability from "../availability/Availability"
import ObjectPicker from "../ObjectPicker"

import {
  addEmployee,
  updateEmployee,
  deleteEmployee,
  getAllRoles,
  getAllPositions,
  getAllRestrictions
} from "./UserRequests";
import { set } from 'mobx'


function EmployeeForm({ onCancel, edit, selected }) {
  
  //Editing
  const [edited, setEdited] = useState(false)

  //Tabs
  const [tabKey, setTabKey] = useState('general')

  //User
  const [firstName, setFirstname] = useState("")
  const [lastName, setLastname] = useState("")
  const [username, setUsername] = useState("") 
  const [password, setPassword] = useState("")
  const [systemStatus, setSystemStatus] = useState("active")
  //Availability
  const [availability, setAvailability] = useState([])
  //Position
  const [position, setPosition] = useState(null)
  const [choosablePositions, setChoosablePositions] = useState([])
  //Role
  let firstSelectedRole = null // to add the first role wich is listed when click on "Hinzufügen"
  const [selectedRole, setSelectedRole] = useState(null)
  const [roles, setRoles] = useState([])
  const [choosableRoles, setChoosableRoles] = useState([])//([{name: "Standard"}])
  //Restrictions
  const [restrictions, setRestrictions] = useState([])
  const [choosableRestrictions, setChoosableRestrictions] = useState([])
  
  //HANDEL CHANGE
  const handleFirstnameChange = event => {setFirstname(event.target.value); setEdited(true)}
  const handleLastnameChange = event => {setLastname(event.target.value); setEdited(true)}
  const handleUsernameChange = event => {setUsername(event.target.value); setEdited(true)}
  const handlePasswordChange = event => {setPassword(event.target.value); setEdited(true)}
  const handleSelectedRoleChange = event => {setSelectedRole(event.target.value); setEdited(true)}
  const handleSelectedPositionChange = event =>  {setPosition(choosablePositions[event.target.value]); setEdited(true)}
  const handleSystemStatusChange = event => {setSystemStatus(event.target.value); setEdited(true)}
  


  useEffect(() => {
    console.log("useEffect-Call: loadChoosableRoles");
    loadChoosableRoles();
    console.log("useEffect-Call: loadChoosablePositions");
    loadChoosablePosition();
    if(edit) {
        setFirstname(selected.firstName)
        setLastname(selected.lastName)
        setUsername(selected.username)
        setPassword(selected.password)
        setSystemStatus(selected.systemStatus)
        //const test_role = {"id":"5ec1243689b22604c5644173","name":"ANONYMOUS_ROLE","description":null,"rights":[{"id":"5ec1243689b22604c5644169","name":"OWN_USER_DATA_READ","description":null}]}
        setRoles(selected.roles)
        const test_position = {id:"2", name:"Position2", description:"Zweite Position"}
        //setPosition(selected.Position)
        setPosition(test_position)
        const Availability_test = {startDate: new Date(), endDate: new Date(), endOfSeries: null, frequency: null, rhythm: "Einmalig"}
        if(selected.availability != null || selected.availability != undefined) {
          setAvailability(selected.availability);
        } else {
          setAvailability([Availability_test]);
        }
    } 
  }, [])

  //---------------------------------SUBMIT---------------------------------
  //ADD USER
  const handleSubmit = async event => {
    event.preventDefault();//reload the page after clicking "Enter"
    if(edit) {
      var id = selected.id
      const updateData = {id, firstName, lastName, username, password, systemStatus, roles, position, availability}
      try {
        console.log("AXIOS: updateEmployee()")
        console.log(updateData)
        await updateEmployee(id, updateData);
      } catch (error) {
        console.log(Object.keys(error), error.message)
        alert("An error occoured while updating a user")
      } 
    } else {
        var id = Math.random() * (10000 - 0) + 0;
        const employeeData = {id, firstName, lastName, username, password, systemStatus, roles, position, availability}
        try {
          console.log("AXIOS: addEmployee()")
          await addEmployee(employeeData);
        } catch (error) {
          console.log(Object.keys(error), error.message)
          alert("An error occoured while adding a user")
        } 
    } 
    onCancel()
  };

  //DELETE USER
  const handleDeleteUser = async  (index) => {
    const answer = confirm("Möchten Sie diesen Mitarbeiter wirklich löschen? ")
        if (answer) {
            const res = await deleteEmployee(selected.id)
            console.log(res)
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

  //REMOVE USER ROLES (ROLE-TABLE)
  const removeRole = (index) => {
    console.table(roles)
    roles.splice((index),1) // remove role at "index" and just remove "1" role
    setRoles([...roles])
    setEdited(true)
    console.table(roles)
  };

  //---------------------------------ALL-ROLES---------------------------------
  //LOAD (ROLE-DROPDOWN)
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
      setChoosableRoles(choosableRoles => [...choosableRoles, role]);
    })
  };

  //---------------------------------ALL-POSITIONS---------------------------------
  //LOAD (POSITION-DROPDOWN)
  const loadChoosablePosition = async () => {
    var data = [];
    try{ 
      const response = await getAllPositions();
      data = response.data;
    }catch (error) {
      console.log(Object.keys(error), error.message)
      //alert("An error occoured while loading choosable positions")
      data = [{id:"1", name:"Position1", description:"Erste Position"},{id:"2", name:"Position2", description:"Zweite Position"}]
    }
    data.map((singlePosition, index) => {
      if(index == 0) {
        setPosition(singlePosition) // set first default selected position to be ready for submit
      }
      setChoosablePositions(choosablePositions => [...choosablePositions, singlePosition]);
    })
  };


    //---------------------------------ALL-RESTRICTIONS---------------------------------
  //LOAD (RESSTRICTION-DROPDOWN)
  const loadChoosableRestrictions = async () => {
    var data = [];
    try{ 
      const response = await getAllRestrictions();
      data = response.data; 
    }catch (error) {
      console.log(Object.keys(error), error.message)
      //alert("An error occoured while loading choosable restrictions")
    }
    data.map((restriction) => {
      setChoosableRestrictions(choosableRestrictions => [...choosableRestrictions, restriction]);
    })
  };


  //---------------------------------Availability---------------------------------
  const addAvailability = (newAvailability) => {
    setAvailability(availability => [...availability, newAvailability]);
  }

  const editedAvailability = (isEdited) => {
    setEdited(isEdited)
  }


  //---------------------------------RENDERING---------------------------------
  /* // DYNAMIC ROLE-DROPDOWN
  function renderRoleDropdown() {
    if (choosableRoles.length > 0) {
      return choosableRoles.map((role, index) => {
          const {name} = role;
          if(index == 0) {
            firstSelectedRole = name;
          }
          return (<option key={index} value={name}>{name}</option>);
      })
    } else {
        return (<option disabled key={0}>KEINE ROLLEN VORHANDEN</option>);
    }
  }; */

  // DYNAMIC POSITION-DROPDOWN
  /* function renderPositionDropdown() {
    if (choosablePositions.length > 0) {
      return choosablePositions.map((singlePosition, index) => {
          const {name} = singlePosition;
          return (<option key={index} value={index}>{name}</option>);
      })
    } else {
        return (<option disabled key={0}>KEINE POSITIONEN VORHANDEN</option>);
    }
  }; */

  // DYNAMIC ROLE-TABLE
  function renderRoleTable() {
    var emptyRole = false;
    roles.map((role) => {
      if(role.name == null)
      emptyRole = true
    })
    if(roles.length > 0 && !emptyRole)
    {
      return ( 
        roles.map((role, index) =>(
          <tr key={index}>
            <td><Form.Control readOnly type="text" name={"Rolle"+ index} value={role.name}/></td>
            <td><Button onClick={()=>removeRole(index)} id={role.name}>Entfernen</Button></td>
          </tr>
        ))
      );
    }
  };

   return (
    <React.Fragment>
      <Container>
        <Form id="employeeAdd" onSubmit={(e) => handleSubmit(e)}>
        <h5 style={{fontWeight: "bold"}}>{edit ? "Mitarbeiter bearbeiten" : "Mitarbeiter hinzufügen"}</h5>
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
                    <Form.Label>Benutzerkonto:</Form.Label>
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
              <Form.Row>
                <Form.Group as={Col} md="5">
                  <Form.Label>Rolle hinzufügen:</Form.Label>
                    <Container style={{display: "flex", flexWrap: "nowrap"}}>
                      <ObjectPicker 
                          setState={setSelectedRole}
                          DbObject="role" />
                      <Button onClick={addRole} style={{marginLeft: "20px"}}>Hinzufügen</Button>
                    </Container> 
                </Form.Group>
                <Form.Group as={Col} md="3">
                  <Form.Label>Position:</Form.Label>
                  <Container style={{display: "flex", flexWrap: "nowrap"}}>
                      {edit ? 
                          <ObjectPicker 
                            setState={setPosition}
                            DbObject="position" />:
                          <ObjectPicker // if form is loading, this picker should display the position of this user
                            setState={setPosition}
                            DbObject="position" 
                            selected={position}/>
                      }

                  </Container>
                </Form.Group>
              </Form.Row>
              <Form.Row>
                  <Form.Group as={Col} md="5">
                      <Table style={{border: "2px solid #AAAAAA"}} striped hover variant="ligth">
                        <thead>
                            <tr>
                                <th>Zugewiesene Rollen</th>
                            </tr>
                        </thead>
                        <tbody>
                          {renderRoleTable()}
                        </tbody>
                      </Table> 
                  </Form.Group>
              </Form.Row>
            </Tab>
            <Tab eventKey="availability" title="Verfügbarkeit">
              <Form.Row style={{marginTop: "25px"}}>
                <Availability availability={availability} addAvailability={addAvailability} editedAvailability={editedAvailability}/>
              </Form.Row>
            </Tab>
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
                  null : <Button variant="success"  type="submit">Mitarbeiter anlegen</Button>)
                }
                </Container>
              </Form.Row>
        </Form>
      </Container>
    </React.Fragment>
  )
}

export default EmployeeForm



    

