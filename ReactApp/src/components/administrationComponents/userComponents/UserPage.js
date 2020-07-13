//author: Patrick Venturini
import React, { useState, useEffect } from 'react'
import OverviewPage, { modalTypes } from "../../OverviewPage"
import UserForm from "./UserForm"
import { ExceptionModal } from "../../ExceptionModal"
import { getEmployees, getCustomers } from "../../requests";

export default function UserPage({ userStore, userType }) {
    const [userList, setUserList] = useState([])
    const [exception, setException] = useState(null)
    const [showExceptionModal, setShowExceptionModal] = useState(false)


    useEffect(() => {
        loadUserList()
    }, [])


    const handleExceptionChange = (newException) => {
        setException(newException)
        setShowExceptionModal(true)
    }


    //---------------------------------LOAD---------------------------------
    const loadUserList = async () => {
        var response = []
        try {
            switch (userType) {
                case "employee":
                    const EmployeeData = await getEmployees("NOTDELETED");
                    response = EmployeeData.data
                    break;
                case "customer":
                    const CustomerData = await getCustomers("NOTDELETED");
                    response = CustomerData.data
            }
            console.log(response)
            setUserList(response)
        } catch (error) {
            console.log(Object.keys(error), error.message)
        }
    }


    //------------------------OverviewPage-Components-----------------------
    const tableBody =
        userList.map((item, index) => {
            var status //translated status
            switch (item.systemStatus) {
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


    const modal = (onCancel, edit, selectedItem) => {
        return (
            <UserForm
                onCancel={onCancel}
                edit={edit}
                selected={selectedItem}
                type={userType == "employee" ? "employee" : "customer"}
                setException={handleExceptionChange}
                userStore={userStore}
            />
        )
    }

    return (
        <React.Fragment>
            <OverviewPage
                pageTitle={userType == "employee" ? "Mitarbeiter" : "Kunden"}
                newItemText={userType == "employee" ? "Neuer Mitarbeiter" : "Neuer Kunde"}
                tableHeader={["#", "Benutzername", "Nachname", "Vorname", "Status"]}
                tableBody={tableBody}
                modal={modal}
                data={userList}
                modalSize="xl"
                refreshData={loadUserList}
                userStore={userStore}
                modalType={modalTypes.user}
            />
            {exception != null &&
                <ExceptionModal
                    showExceptionModal={showExceptionModal}
                    setShowExceptionModal={setShowExceptionModal}
                    exception={exception}
                    warning={"AvailabilityWarning"}
                />
            }
        </React.Fragment>
    )
}