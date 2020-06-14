import React, { useState, useEffect } from "react"
import OverviewPage from "../../OverviewPage"
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
        }
        
        //don't save object with status="deleted"
        var reducedData = []
        console.log("original data:")
        console.log(data)
        data.map((singleResource) => {
            if(singleResource.status != "deleted") {
                reducedData.push(singleResource)
            }
          })
        setResources(reducedData);
    }

    useEffect(() => {
        loadResources()
    }, [])

    const tableBody =
        resources.map((item, index) => {
            var status //translated status
            switch(item.status) {
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
            var price 
            if(item.pricePerUnit >= 0) {
                price = (item.pricePerUnit/100) + " €"
            } 
            var type = ""
            item.resourceTypes.map((singleType, index) => {
                if(index == 0) {
                    type += singleType.name
                } else {
                    type += ", " + singleType.name
                }
            })
            return (
                [index + 1,
                item.name,
                item.description,
                type,
                item.amountInStock,
                price,
                status]
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
                tableHeader={["#", "Bezeichnung", "Beschreibung", "Ressourcentyp", "Bestandsmenge", "Einzelpreis", "Status"]}
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