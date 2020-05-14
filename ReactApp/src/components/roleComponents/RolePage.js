import React, { useState, useEffect } from "react"
import {Button} from "react-bootstrap"
import { getRoles } from "./RoleRequests"
import RoleForm from "./RolesForm"
import OverviewPage from "../OverviewPage"

function RolePage(userStore) {
    const [roles, setRoles] = useState([])
    
    async function prepareRoles() {
        const res = await getRoles()
        const result = res.data.map(item => {
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
            />
        </React.Fragment>
    )
}

export default RolePage