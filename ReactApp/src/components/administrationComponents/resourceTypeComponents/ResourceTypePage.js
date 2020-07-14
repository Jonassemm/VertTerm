//author: Patrick Venturini
import React, { useState, useEffect } from "react"
import {getResourceTypes } from "../../requests"
import ResourceTypeForm from "./ResourceTypeForm"
import OverviewPage, {modalTypes} from "../../OverviewPage"

function ResourceTypePage({userStore}) {
    const [resourceTypes, setResourceTypes] = useState([])

    useEffect(() => {
        loadResourceTypes()
    }, [])

    //--------------------------------LOAD-----------------------------
    const loadResourceTypes = async () => {
        try{ 
          const {data} = await getResourceTypes("NOTDELETED");
          setResourceTypes(data);
        }catch (error) {
          console.log(Object.keys(error), error.message)
        }
    }


    //----------------------OverviewPage-Components---------------------
    const tableBody =
        resourceTypes.map((item, index) => {
            return ([
                index + 1,
                item.name,
                item.description]
            )
        })


    const modal = (onCancel,edit,selectedItem) => {
        return (
            <ResourceTypeForm
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
                pageTitle="Ressourcen-Typ"
                newItemText="Neuen Ressourcentyp"
                tableHeader={["#", "Bezeichnung", "Beschreibung"]}
                tableBody={tableBody}
                modal={modal}
                data={resourceTypes}
                refreshData={loadResourceTypes}
                userStore={userStore}
                modalType={modalTypes.resourceType}
            />
        </React.Fragment>
    )
}
export default ResourceTypePage