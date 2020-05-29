import React ,{useState, useEffect} from 'react'
import {Form, Table, Card, Col, Container, Button, InputGroup} from 'react-bootstrap'
import ObjectPicker from "../ObjectPicker"

import {
  addCustomer,
  updateCustomer,
  deleteCustomer,
  getAllRoles,
  getCustomer,
  getAllRestrictions
} from "./UserRequests";



function CustomerForm({ onCancel, edit, selected, userType }) {
  
  const [edited, setEdited] = useState(false)

  const [firstName, setFirstname] = useState("")
  const [lastName, setLastname] = useState("")
  const [username, setUsername] = useState("") 
  const [password, setPassword] = useState("")
  const [systemStatus, setSystemStatus] = useState("active")

  let firstSelectedRole = null // to add the first role wich is listed when click on "Hinzufügen"
  const [selectedRole, setSelectedRole] = useState(null)
  const [roles, setRoles] = useState([])
  const [choosableRoles, setChoosableRoles] = useState([])//([{name: "Standard"}])

  const [restrictions, setRestrictions] = useState([])
  const [choosableRestrictions, setChoosableRestrictions] = useState([])

  //HANDEL CHANGE
  const handleFirstnameChange = data => {setFirstname(data.target.value); setEdited(true)}
  const handleLastnameChange = data => {setLastname(data.target.value); setEdited(true)}
  const handleUsernameChange = data => {setUsername(data.target.value); setEdited(true)}
  const handlePasswordChange = data => {setPassword(data.target.value); setEdited(true)}
  const handleSelectedRoleChange = data => {setSelectedRole(data.target.value); setEdited(true)}
  const handleSystemStatusChange = data => {setSystemStatus(data.target.value); setEdited(true)}


  useEffect(() => {
    console.log("useEffect-Call: loadChoosableRoles");
    loadChoosableRoles();
    if(edit) {
      console.log("useEffect-Call: loadUser");
      loadUser();
    } 
  }, [])

  //---------------------------------SUBMIT---------------------------------
  const handleSubmit = async event => {
    event.preventDefault();//reload the page after clicking "Enter"
    if(edit) {
      console.log("useEffect-Call: loadUser");
      loadUser();
      var id = selected.id
      const updateData = {id, firstName, lastName, username, password, systemStatus, roles}
      try {
        console.log("AXIOS: updateCustomer()")
        console.log(updateData)
        await updateCustomer(id, updateData);
      } catch (error) {
        console.log(Object.keys(error), error.message)
        alert("An error occoured while updating a customer")
      } 
    } else {
        const customerData = {firstName, lastName, username, password, systemStatus, roles}
        try {
          console.log("AXIOS: addCustomer()")
          await addCustomer(customerData);
        } catch (error) {
          console.log(Object.keys(error), error.message)
          alert("An error occoured while adding a customer")
        } 
    } 
    onCancel()
  };

  //DELETE USER
  const handleDeleteUser = async  (index) => {
    const answer = confirm("Möchten Sie diesen Kunden wirklich löschen? ")
    console.log(answer)
        if (answer) {
            const res = await deleteCustomer(selected.id)
            console.log(res)
        }
        onCancel()
  }

  //---------------------------------CURRENT_USER---------------------------------
  //LOAD USER
  const loadUser = async () => {
  var data = [];
    try {
      console.log("AXIOS: loadUser()");
      //Load response-data
      const response = await getCustomer(selected.id);
      data = response.data;
    } catch (error) {
      console.log(Object.keys(error), error.message);
      alert("An error occoured while loading a user");
    }
    //Extract response-data
    const {firstName, lastName, username, password, systemStatus, roles} = data;
    //Save response-data
    setFirstname(firstName);
    setLastname(lastName);
    setUsername(username);
    setPassword(password);
    setSystemStatus(systemStatus);
    if(roles != null) {
      roles.map((role) => {
        if(role.name != null) //ensure not to save empty roles
        setRoles(choosableRoles => [...choosableRoles, role]);
      }) 
    }
  };


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
    console.table(roles);
    roles.splice((index),1); // remove role at "index" and just remove "1" role
    setRoles([...roles]);
    console.table(roles);
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
      if(role.name != "ADMIN_ROLE") {
        setChoosableRoles(choosableRoles => [...choosableRoles, role]);
      }
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
      alert("An error occoured while loading choosable restrictions")
    }
    data.map((restriction) => {
      setChoosableRestrictions(choosableRestrictions => [...choosableRestrictions, restriction]);
    })
  };

  //---------------------------------RENDERING---------------------------------
  // DYNAMIC ROLE-DROPDOWN
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
  };



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
        <Form id="customerAdd" onSubmit={(e) => handleSubmit(e)}>
          <h5 style={{fontWeight: "bold"}}>{edit ? "Kunde bearbeiten" : "Kunde hinzufügen"}</h5>
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


              <Form.Group as={Col} md="5">
                <Form.Label>Einschränkungen:</Form.Label>
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
        <Form.Row>
        <Container style={{textAlign: "right"}}>
          {edit ? 
                <Button variant="danger" onClick={handleDeleteUser} style={{marginRight: "20px"}}>Löschen</Button> :
                null
          }
          <Button variant="secondary" onClick={onCancel} style={{marginRight: "20px"}}>Abbrechen</Button>
          {(edit ? edited ? 
            <Button variant="success" type="submit">Übernehmen</Button>:
            null : <Button variant="success"  type="submit">Kunde anlegen</Button>)
          }
          </Container>
        </Form.Row>
      </Form>
    </Container>
  </React.Fragment>
  )
}

export default CustomerForm


    

