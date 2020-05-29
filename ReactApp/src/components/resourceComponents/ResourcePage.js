import React, { useState, useEffect } from "react"
import OverviewPage from "../OverviewPage"
import ResourceForm from "./ResourceForm"
import {getAllResources} from "./ResourceRequests"

function ResourcePage() {
    const [resources, setResources] = useState([])

    const loadResources= async () => {
        var data = [];
        try{ 
            const response = await getAllResources();
            data = response.data;
        }catch (error) {
            console.log(Object.keys(error), error.message)
            alert("An error occoured while loading all resources")
        }

        reducedData = []

        data.map((singleResource) => {
            if(singleResource.status != "deleted") {
                reducedData.push(singleResource)
            }
          })
        setResources(data);
    }

    useEffect(() => {
        loadResources()
    }, [])

    const tableBody =
        resources.map((item, index) => {
            return (
                [index + 1,
                item.name,
                item.description,
                item.type,
                item.amountInStock,
                item.numberOfUses,
                item.pricePerUnit,
                item.status]
            )
        })

    const modal = (onCancel, edit, selectedItem) => {
        return (
            <ResourceForm
                onCancel={onCancel}
                edit={edit}
                selected={selectedItem}
            />
        )
    }

    return (
        <React.Fragment>
            <OverviewPage 
                pageTitle="Ressourcen"
                newItemText="Neue Ressource"
                tableHeader={["#", "Bezeichnung", "Beschreibung", "Ressourcentyp", "Bestandsmenge", "Verwendungsanzahl", "Einzelpreis", "Status"]}
                tableBody={tableBody}
                modal={modal}
                data={resources}
                refreshData={loadResources} 
                modalSize="xl"
            />
        </React.Fragment>
    )
}

export default ResourcePage