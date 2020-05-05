import React ,{useState, useEffect} from 'react'
import {Form, Table, Card, Col, Container, Button, InputGroup} from 'react-bootstrap'
import {useParams} from "react-router-dom"
import {observer} from "mobx-react"
import Layout from "./Layout"
import {useHistory} from 'react-router-dom';

import Availability from "../availability/AvailabilityForm"
import {
  addEmployee,
  updateEmployee,
  getRoles,
  getUser
} from "./UserRequests";

const styles = {
  radioFullBox: {

  },
  radioBox: {
    color: 'black',
    display: 'flex',
    height: '80px',
    width: '120px',
    border: '5px',
    background: '#dadada',
    flexDirection: 'column'
  },
  radioLabel: {
    background: 'blue'
  },
  radioButton: {
    background: 'green',
    flexDirection: 'row',
    flexBasis: '70%',
    margin: '0px 0px 0px 5px'
  }
}


function EmployeeAdd(props) {
  
  const history = useHistory();

  const [firstName, setFirstname] = useState("")
  const [lastName, setLastname] = useState("")
  const [username, setUsername] = useState("") 
  const [password, setPassword] = useState("")
  const [position, setPosition] = useState("")
  const [systemStatus, setSystemStatus] = useState("")

  let firstSelectedRole = null // to add the first role wich is listed when click on "Hinzufügen"
  const [selectedRole, setSelectedRole] = useState(null)
  const [roles, setRoles] = useState([])
  const [choosableRoles, setChoosableRoles] = useState([])//([{name: "Standard"}])
  //EDIT
  const [currentUser, setCurrentUser] = useState(null)
  const {userId} = useParams() //graps userId from URL

  //HANDEL CHANGE
  const handleFirstnameChange = data => setFirstname(data.target.value)
  const handleLastnameChange = data => setLastname(data.target.value)
  const handleUsernameChange = data => setUsername(data.target.value)
  const handlePasswordChange = data => setPassword(data.target.value)
  const handlePositionChange = data => setPosition(data.target.value)
  const handleRoleInputChange = data => setSelectedRole(data.target.value)
  const handleSystemStatusChange = data => setSystemStatus(data.target.value)


  useEffect(() => {
    console.log("useEffect-Call: loadChoosableRoles");
    loadChoosableRoles();
    switch(props.type) {
      case "edit":
        console.log("useEffect-Call: loadUser");
        loadUser();
        break;
      case "add":
        break;
    }
  }, [])

  //---------------------------------SUBMIT---------------------------------
  const handleSubmit = async event => {
    event.preventDefault();//reload the page after clicking "Enter"
  
    switch(props.type) {
      case "edit":
        console.log("useEffect-Call: loadUser");
        loadUser();
        var id = userId
        const updateData = {id, firstName, lastName, username, password, systemStatus, roles}
        try {
          console.log("AXIOS: updateEmployee()")
          console.log(updateData)
          await updateEmployee(userId, updateData);
        } catch (error) {
          console.log(Object.keys(error), error.message)
          alert("An error occoured while updating a user")
        }
        break;
      case "add":
        var id = Math.random() * (10000 - 0) + 0;
        const employeeData = {id, firstName, lastName, username, password, systemStatus, roles}
        try {
          console.log("AXIOS: addEmployee()")
          await addEmployee(employeeData);
        } catch (error) {
          console.log(Object.keys(error), error.message)
          alert("An error occoured while adding a user")
        }
        break;
    }
    //REDIRECT
    history.push('/employee/list');
  };

  //---------------------------------CURRENT_USER---------------------------------
  //LOAD USER
  const loadUser = async () => {
  var data = [];
    try {
      console.log("AXIOS: loadUser()");
      //Load response-data
      const response = await getUser(userId);
      data = response.data;
      console.log("load User: ");
      console.log(data);
    } catch (error) {
      console.log(Object.keys(error), error.message);
      alert("An error occoured while loading a user");
    }
    //Extract response-data
    const {firstName, lastName, username, password, systemStatus, position, roles} = data;
    //Save response-data
    setFirstname(firstName);
    setLastname(lastName);
    setUsername(username);
    setPassword(password);
    setPosition(position);
    setSystemStatus(systemStatus);
    if(roles != null) {
      roles.map((role) => {
        if(role.name != null) //ensure not to save empty roles
        setRoles(choosableRoles => [...choosableRoles, role]);
      }) 
    }
  };


  //---------------------------------ROLES---------------------------------
  //ADD USER ROLES (TABLE)
  const addRole = () => {
    if(roles.some(roles => roles.name === selectedRole) || (roles.some(roles => roles.name === firstSelectedRole) && (selectedRole==null))) {
      alert("Rolle bereits vorhanden!");
    } else if (selectedRole == null) { //NO ROLE SELECTED
        if(firstSelectedRole == null ) //NO ROLES FOR DROPDOWN
        {
          alert("Keine Rolle zum hinzufügen gefunden");
        } else { // ONE ROLE SELECTED IN DROPDOWN
          choosableRoles.map((role, index) => {
            console.log(role.name == firstSelectedRole)
            if(role.name == firstSelectedRole) { //ADD THE ROLE WICH IS SELECTED
              setRoles(roles => [...roles, role]);
              console.log("ADD FIRSTROLE:");
              console.log(role);
            }
          })
        }
    } else { // ROLE SELECTED
    console.log("add Role:");
    console.log(selectedRole);
      choosableRoles.map((role, index) => {
        if(role.name == selectedRole) {
          setRoles(roles => [...roles, role]);
          console.log("ADD ROLE:");
          console.log(role);
        }
      })
    }

  };

  //REMOVE USER ROLES (TABLE)
  const removeRole = (index) => {
    console.table(roles);
    roles.splice((index),1); // remove role at "index" and just remove "1" role
    setRoles([...roles]);
    console.table(roles);
  };

  //LOAD CHOOSABLE ROLES (DROPDOWN)
  const loadChoosableRoles = async () => {
    var data = [];
    try{ 
      const response = await getRoles();
      data = response.data;
    }catch (error) {
      console.log(Object.keys(error), error.message)
      alert("An error occoured while loading choosable roles")
    }
    data.map((role, index) => {
      setChoosableRoles(choosableRoles => [...choosableRoles, role]);
    })

  };

  //---------------------------------RENDERING---------------------------------
  // DYNAMIC DROPDOWN
  function renderDropdown() {
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
  };

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
    <Layout>
      <Card style={{marginBottom: "50px"}} className={"border border-dark bg-dark text-white"}>
        <Form id="employeeAdd" onSubmit={(e) => handleSubmit(e)}>
          <Card.Header><h3>{props.type === "edit" ? "Mitarbeiter bearbeiten" : "Mitarbeiter hinzufügen"}</h3></Card.Header>
          <Card.Body>
            <Form.Row>
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
                      value="1"
                      checked={systemStatus == 1}
                      id="SystemStatusAktive"
                      onChange={handleSystemStatusChange}
                  />
                  <Form.Check
                      required
                      type="radio"
                      label="Deaktiviert"
                      name="systemStatus"
                      id="SystemStatusInaktive"
                      value="0"
                      checked={systemStatus == 0}
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
                    {/*<Form.Control 
                      type="text"
                      placeholder="Rolle" 
                      onChange={handleRoleInputChange}
                    />*/}
                      <select onChange={handleRoleInputChange} className="custom-select">
                          {renderDropdown()}
                      </select>
                    <Button onClick={addRole} style={{marginLeft: "20px"}}>Hinzufügen</Button>
                  </Container> 
              </Form.Group>
              <Form.Group as={Col} md="3">
                <Form.Label>Position:</Form.Label>
                  <Form.Control 
                      required
                      name="position"
                      type="text"
                      placeholder="Position" 
                      value={position || ""}
                      onChange={handlePositionChange}
                      />  
              </Form.Group>
            </Form.Row>
            <Form.Row>
                <Form.Group as={Col} md="5">
                    <Table striped hover variant="light">
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
            {//<Availability/>
            }
        </Card.Body>
        <Card.Footer style={{textAlign: "right"}}>
            {props.type === "edit" ? 
              <Button variant="secondary" onClick={() => history.push('/employee/list')} style={{marginRight: "20px"}}>Abbrechen</Button> :
              null
            }
          <Button size="md" variant="success" type="submit">
            {props.type === "edit" ? "Übernehmen" : "Mitarbeiter anlegen"}
          </Button>
        </Card.Footer>
      </Form>
    </Card>
  </Layout>
  )
}

export default observer(EmployeeAdd)


    

