//author: Jonas Semmler
import React, { useState, useEffect } from "react"
import {Button} from "react-bootstrap"
import { getRoles } from "../../requests"
import RoleForm from "./RolesForm"
import OverviewPage, {modalTypes} from "../../OverviewPage"

function RolePage({userStore}) {
    const [roles, setRoles] = useState([])
    
    async function prepareRoles() {

        const {data} = await getRoles("NOTDELETED")
        // chainging the rights array of the role to a string with the names
        const result = data.map(item => {
            const rightStrings = item.rights.map(item => {
                return item['name']
            })
            return {
                ...item,
                rights: rightStrings
            }
        }
        )
        setRoles(result)
    }

    useEffect(() => {
        prepareRoles()
    },[])
    

    const tableBody =
        roles.map((item, index) => {
            return ([
                index + 1,
                item.name,
                item.description,
                item.rights.join(", ")]
            )
        })

    const modal = (onCancel,edit,selectedItem) => {
        return (
            <RoleForm
                onCancel={onCancel}
                edit={edit}
                selected={selectedItem}
                userStore={userStore}
            />
        )
    }

    return (
        <React.Fragment>
            <OverviewPage
                pageTitle="Rollen"
                newItemText="Neue Rolle"
                tableHeader={["#", "Bezeichnung", "Beschreibung", "Rechte"]}
                tableBody={tableBody}
                modal={modal}
                data={roles}
                refreshData={prepareRoles}
                modalSize="lg"
                scrollable={true}
                userStore={userStore}
                modalType={modalTypes.role}
            />
        </React.Fragment>
    )
}

export default RolePage