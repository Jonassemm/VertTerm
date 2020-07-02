import React, { useState, useEffect } from "react"
import { getAllResourceTypes } from "./ResourceTypeRequests"
import ResourceTypeForm from "./ResourceTypeForm"
import OverviewPage from "../../OverviewPage"

function ResourceTypePage() {
    const [resourceTypes, setResourceTypes] = useState([])


    useEffect(() => {
        loadResourceTypes()
    }, [])


    //--------------------------------LOAD-----------------------------
    const loadResourceTypes = async () => {
        var data = [];
        try{ 
          const response = await getAllResourceTypes();
          data = response.data;
        }catch (error) {
          console.log(Object.keys(error), error.message)
        }
        setResourceTypes(data);
    };


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
            />
        </React.Fragment>
    )
}

export default ResourceTypePage