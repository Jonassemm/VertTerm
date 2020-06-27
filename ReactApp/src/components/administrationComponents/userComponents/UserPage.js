import React, {useState, useEffect} from 'react'
import OverviewPage from "../../OverviewPage"
import UserForm from "./UserForm"
import {ExceptionModal} from "../../ExceptionModal"


import {
    getEmployeeList,
    getCustomerList
  } from "./UserRequests";


export default function UserList(props) {

    const [userList, setUserList] = useState([])

    //exception needs overriding (ExceptionModal)
    const [exception, setException] = useState(null)
    const [showExceptionModal, setShowExceptionModal] = useState(false)

    useEffect( () => {
        loadUserList()
    },[])


    //-------------------------------ExceptionModal--------------------------------
    const handleExceptionChange = (newException) => {
        setException(newException)
        setShowExceptionModal(true)
    }


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
                }
            break;
          }

        //don't save object with status="deleted"
        var reducedData = []
        console.log("original data:")
        console.log(data)
        data.map((singleUser) => {
            if(singleUser.systemStatus != "deleted") {
                reducedData.push(singleUser)
            }
        })
        setUserList(reducedData)
    }

    const tableBody = 
        userList.map((item, index) => { 
            var status //translated status
            switch(item.systemStatus) {
                case "active":
                    status = "Aktiviert"
                break;
                case "inactive":
                    status = "Deaktiviert"
                break;
                case "deleted":
                    status = "Gelöscht"
                break;
                default: status = "UNDIFINED"
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
            <UserForm //EmployeeForm
                onCancel={onCancel}
                edit={edit}
                selected={selectedItem}
                type={"employee"}
                setException={handleExceptionChange}
            />
        )
    }

    const modalCustomer = (onCancel,edit,selectedItem) => {
        return (
            <UserForm //CustomerForm
                onCancel={onCancel}
                edit={edit}
                selected={selectedItem}
                type={"customer"}
                setException={handleExceptionChange}
            />
        )
    }

    return (
        <React.Fragment>
            {exception != null && 
                <ExceptionModal
                    showExceptionModal={showExceptionModal} 
                    setShowExceptionModal={setShowExceptionModal} 
                    exception={exception}
                    warning={"AvailabilityWarning"}
                />
            }
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
                    pageTitle="Kunden"
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