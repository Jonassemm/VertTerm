import React, {useState, useEffect, Fragment} from 'react'
import {Card, Table, Button, Modal} from 'react-bootstrap';
import {useHistory} from 'react-router-dom';
import Layout from "./Layout"


import {observer} from "mobx-react"
import {
    getEmployeeList,
    getCustomerList,
    removeEmployee,
    removeCustomer
  } from "./UserRequests";
import { remove } from 'mobx';

export default function UserList(props) {
  
    const [showRemoveConfirmModal, setShowRemoveConfirmModal] = useState(false)
    const [removeIndex, setRemoveIndex] = useState(null)

    const history = useHistory();
    const [userList, setUserList] = useState([])

    const handleRemoveIndexChange = data => {setRemoveIndex(data.target.value); showModal();}
    

    useEffect( () => {
        loadUserList()
    },[])
    
    //---------------------------------USER---------------------------------
    //LOAD USERLIST
    const loadUserList = async () => {
        var data = []
        switch(props.userType) {
            case "employee":
                try {
                    console.log("getEmployeeList")
                    const response = await getEmployeeList();
                    data = response.data.map(user => {
                        return {
                            ...user,
                        }
                    })
                }catch (error) {
                    console.log(Object.keys(error), error.message)
                    alert("An error occoured while loading userlist")
                }
              break;
            case "customer":
                try {
                    console.log("getCustomerlist")
                    const response = await getCustomerList();
                    data = response.data.map(user => {
                        return {
                            ...user,
                        }
                    })
                }catch (error) {
                    console.log(Object.keys(error), error.message)
                    alert("An error occoured while loading userlist")
                }
              break;
          }
        setUserList(data)
    }


    //REMOVE USER
    const removeUser = async  (index) => {
        switch(props.userType) {
        case "employee":
            try {
                console.log("AXIOS: removeEmployee()")
                await removeEmployee(userList[index].id);
                userList.splice((index),1)
                setUserList([...userList])
              } catch (error) {
                console.log(Object.keys(error), error.message)
                alert("An error occoured while removing a employee")
              }
          break;
        case "customer":
            try {
                console.log("AXIOS: removeCustomer()")
                await removeCustomer(userList[index].id);
                userList.splice((index),1)
                setUserList([...userList])
              } catch (error) {
                console.log(Object.keys(error), error.message)
                alert("An error occoured while removing a customer")
              }
          break;
        }
      }

    const hideModal = () => {
        setShowRemoveConfirmModal(false)
    }

    const showModal = () => {
        console.log("MODAL")
        setShowRemoveConfirmModal(true)
    }

    const handleRemoveUser = () => {
        removeUser(removeIndex)
        setShowRemoveConfirmModal(false)
    }

    //---------------------------------RENDERING---------------------------------
    //DYNAMIC TABLE
    function renderTableBody() {
        if (userList != null) {
            return userList.map((user, index) => {
                const {id, username, firstName, lastName, systemStatus} = user
                return (
                    <tr key={index}>
                        <td>{username}</td>
                        <td>{firstName}</td>
                        <td>{lastName}</td>
                        <td>{systemStatus}</td>
                        <td style={{width: "300px"}}>
                            <Button  onClick={() => history.push(props.userType == "employee" ? '/employee/edit/'+id : '/customer/edit/'+id )} style={{marginRight:"5px"}}>Ansicht</Button>
                            {//<Button onClick={() => {if(window.confirm('Wollen Sie diesen Benutzer wirklich entfernen?')){removeUser(index)};}}  id={id}>Entfernen</Button>
                            }<Button onClick={handleRemoveIndexChange} value={index} id={id}>Entfernen</Button>
                        </td>
                    </tr>
                )
            })
        } else {
            return (
                <tr align="center">
                    <td colSpan="5">Kein Benutzer vorhanden</td>
                </tr>
            )
        }
    }

    return (
    <Layout>
        <div className="page">
            <Modal show={showRemoveConfirmModal} onHide={hideModal}>
                    <Modal.Header closeButton>
                        <Modal.Title>Benutzer entfernen</Modal.Title>
                    </Modal.Header>

                    <Modal.Body>
                        <p>Soll der Benutzer wirklich gelöscht werden? </p>
                    </Modal.Body>

                    <Modal.Footer>
                        <Button variant="secondary"onClick={hideModal}>Nein</Button>
                        <Button variant="primary" onClick={handleRemoveUser}>Ja</Button>
                    </Modal.Footer>
            </Modal>
            <Card className={"border border-dark bg-dark text-white"}>
                <Card.Header>{props.heading}</Card.Header>
                <Card.Body>
                    <Table striped hover variant="dark">
                        <thead>
                            <tr>
                                <th>Benutzername</th>
                                <th>Nachname</th>
                                <th>Vorname</th>
                                <th>Status</th>
                                <th>AKTION</th>
                            </tr>
                        </thead>
                        <tbody>
                            {renderTableBody()}
                        </tbody>
                    </Table>
                </Card.Body>
            </Card>
        </div>
    </Layout>
   )
}