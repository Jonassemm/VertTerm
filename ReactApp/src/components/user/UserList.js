import React, {useState, useEffect, Fragment} from 'react'
import {Button} from 'react-bootstrap';
import {useHistory} from 'react-router-dom';
import {Dropdown, DropdownButton} from "react-bootstrap"
import CustomerForm from "./CustomerForm"
import EmployeeForm from "./EmployeeForm"
import OverviewPage from "../OverviewPage"


import {observer} from "mobx-react"
import {
    getEmployeeList,
    getCustomerList,
    deleteEmployee,
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
                await deleteEmployee(userList[index].id);
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
                        {/*<td style={{width: "300px"}}>
                            <Button  variant="info" onClick={() => history.push(props.userType == "employee" ? '/employee/edit/'+id : '/customer/edit/'+id )} style={{marginRight:"5px"}}>Ansicht</Button>
                            {//<Button onClick={() => {if(window.confirm('Wollen Sie diesen Benutzer wirklich entfernen?')){removeUser(index)};}}  id={id}>Entfernen</Button>
                            }<Button variant="danger" onClick={handleRemoveIndexChange} value={index} id={id}>Entfernen</Button>
                        </td>
                        */}
                        <td>
                        <DropdownButton id="dropdown-basic-button" title="AKTION">
                            <Dropdown.Item as="button" onClick={() => history.push(props.userType == "employee" ? '/employee/edit/'+id : '/customer/edit/'+id )}>Anzeigen</Dropdown.Item>
                            {props.userType == "employee" ? 
                                <Dropdown.Item as="button" onClick={() => history.push('/employee/availability/'+id)}>Verfügbarkeiten</Dropdown.Item>  : null}
                            <Dropdown.Divider />
                            <Dropdown.Item  as="button" onClick={handleRemoveIndexChange} value={index} id={id} >LÖSCHEN</Dropdown.Item>
                        </DropdownButton>
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


    const tableBody =
        userList.map((item, index) => { 
            var status
            if(item.systemStatus == "ACTIVE") {
                status = "Aktiviert"
            }else if (item.systemStatus == "INACTIVE") {
                status = "Deaktiviert"
            }else {
                status = "UNDIFINED"
            }
            return ([
                index + 1,
                item.username,
                item.lastName,
                item.firstName,
                status]
            )
        })

    const modalEmployee = (onCancel,edit,selectedItem) => {
        return (
            <EmployeeForm
                onCancel={onCancel}
                edit={edit}
                selected={selectedItem}
                userType={"employee"}
            />
        )
    }

    const modalCustomer = (onCancel,edit,selectedItem) => {
        return (
            <CustomerForm
                onCancel={onCancel}
                edit={edit}
                selected={selectedItem}
                userType={"customer"}
            />
        )
    }

    return (
        <React.Fragment>
            {props.userType == "employee" ? 
                <OverviewPage
                    pageTitle="Mitarbeiter"
                    newItemText="Neuer Mitarbeiter"
                    tableHeader={["#", "Benutzername", "Nachname", "Vorname", "Status"]}
                    tableBody={tableBody}
                    modal={modalEmployee}
                    data={userList}
                    modalSize="xl"
                    refreshData={loadUserList}
                /> :
                <OverviewPage
                    pageTitle="Kunde"
                    newItemText="Neuer Kunde"
                    tableHeader={["#", "Benutzername", "Nachname", "Vorname", "Status"]}
                    tableBody={tableBody}
                    modal={modalCustomer}
                    data={userList}
                    modalSize="xl"
                    refreshData={loadUserList}
                />
            }
        </React.Fragment>
   )
}