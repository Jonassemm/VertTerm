import React ,{useState, useEffect} from 'react'
import {Form, Table, Card, Col, Container, Button, InputGroup} from 'react-bootstrap'
import {useParams} from "react-router-dom"
import {observer} from "mobx-react"
import Layout from "./Layout"
import {useHistory} from 'react-router-dom';

import Availability from "../availability/AvailabilityForm"
import {
  addEmployee,
  getRolesOfUser,
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

  const [firstname, setFirstname] = useState("")
  const [lastname, setLastname] = useState("")
  const [username, setUsername] = useState("") 
  const [password, setPassword] = useState("")
  const [position, setPosition] = useState("")
  const [systemStatus, setSystemStatus] = useState("")

  let firstSelectedRole = null // to add the first role wich is listed when click on "Hinzufügen"
  const [selectedRole, setSelectedRole] = useState(null)
  const [userRoles, setUserRoles] = useState([])
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
  
  //---------------------------------SUBMIT---------------------------------
  const employeeData = {firstname, lastname, username, password, position, systemStatus, userRoles}
    try {
      console.log("AXIOS: addEmployee()")
      await addEmployee(employeeData);
    } catch (error) {
      console.log(Object.keys(error), error.message)
      alert("An error occoured while adding a user")
    }
  };

  //---------------------------------CURRENT_USER---------------------------------
  //LOAD USER
  const loadUser = async () => {
  var data = [];
    /*try {
      console.log("AXIOS: loadUser()");
      const response = await getUser(userId);
      data = response.data.map(properties => {
          return {
              ...properties,
          }
      })
      console.log("load User: " + data + " / " + [data] +  " / " + {data});
    } catch (error) {
      console.log(Object.keys(error), error.message);
      alert("An error occoured while loading a user");
    }*/
    //REMOVE testdata for real usage
    const testdata = {currentId: 1, currentFirstname: "Test-Vorname", currentLastname: "Test-Nachname", currentUsername: "Test-Username", currentPassword: "Test-Passwort", currentPosition: "Test-Position", currentSystemStatus:"inactive", currentRoles:[{name:"Admin"}, {name:"Gast"}]};
    const {currentId, currentFirstname: currentFirstname, currentLastname: currentLastname, currentUsername, currentPassword, currentPosition, currentSystemStatus, currentRoles,} = testdata;
    setFirstname(currentFirstname);
    setLastname(currentLastname);
    setPassword(currentPassword);
    setUsername(currentUsername);
    setPosition(currentPosition);
    setSystemStatus(currentSystemStatus);
    setUserRoles(currentRoles);
  };


  //---------------------------------ROLES---------------------------------
  //LOAD USER ROLES
  /*
  const loadUserRoles = async () => {
    const response = await getRolesOfUser(userId);
    const data = response.data.map(role => {
        return {
            ...role,
        }
    })
    console.log("load Role: " + data + " / " + [data] +  " / " + {data})
    setUserRoles(data)
  }
  */
  //ADD USER ROLES
  const addRole = () => {
    if(userRoles.some(roles => roles.name === selectedRole) || (userRoles.some(roles => roles.name === firstSelectedRole) && (selectedRole==null))) {
      alert("Rolle bereits vorhanden!");
      } else if (selectedRole == null) {
          if(firstSelectedRole == "" || firstSelectedRole == null )
          {
            alert("Keine Rolle zum hinzufügen gefunden");
          }else {
            setUserRoles([...userRoles, {name: firstSelectedRole}]); 
            console.log("first role added: " + firstSelectedRole);
          }
      } else {
      console.log("add Role:" + selectedRole);
      setUserRoles([...userRoles, {name: selectedRole}]);
      }
  };

  //REMOVE USER ROLES
  const removeRole = (index) => {
    console.table(userRoles);
    userRoles.splice((index),1);
    setUserRoles([...userRoles]);
    console.table(userRoles);
  };

  //LOAD CHOOSABLE ROLES
  const loadChoosableRoles = async () => {
    var data = [];
    /*try{ 
      const response = await getRoles();
      data = response.data.map(role => {
          return {
              ...role,
          }
      })
    }catch (error) {
      console.log(Object.keys(error), error.message)
      alert("An error occoured while loading choosable roles")
    }*/
    
    //console.log("load Role: " + data + " / " + [data] +  " / " + {data})
    const testdata = [{name: "Admin"},{name: "Gast"},{name: "Benutzerdefinierte Rolle"}];
    //const testdata = []
    testdata.map((role, index) => {
      setChoosableRoles(choosableRoles => [...choosableRoles, role]);
    })

  };

  //UPDATE CHOSSABLE ROLES
  const updateChoosableRoles = (index) => {
    
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
    return ( 
      userRoles.map((role, index) =>(
        <tr key={index}>
          <td><Form.Control readOnly type="text" name={"Rolle"+ index} value={role.name}/></td>
          <td><Button onClick={()=>removeRole(index)} id={role.name}>Entfernen</Button></td>
        </tr>
      ))
    );
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
                  value={firstname || ""}
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
                  value={lastname || ""}
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
                      checked={systemStatus === 'active'}
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
                      checked={systemStatus === 'inactive'}
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
            {/*<Availability/> */}
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


    

