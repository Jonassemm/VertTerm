import React, { useState, useEffect } from "react"
import { getAllOptionalAttributes } from "./optionalAttributesRequests"
import OptionalAttributesForm from "./optionalAttributesForm"
import OverviewPage from "../OverviewPage"

function optionalAttributesPage() {
    const [optionalAttributes, setOptionalAttributes] = useState([])

    useEffect(() => {
        loadOptionalAttributes()
    }, [])

    const loadOptionalAttributes = async () => {
        var data = [];
        try{ 
          const response = await getAllOptionalAttributes();
          data = response.data;
        }catch (error) {
          console.log(Object.keys(error), error.message)
          alert("An error occoured while loading all extUserInfo")
          data = [{id:"1", name:"E-Mail", isRequired: true},{id:"2", name:"Telefon-Nr.", isRequired: false}]
        }
        setOptionalAttributes(data);
    };

    const tableBody =
        optionalAttributes.map((item, index) => {
            var isRequired //translated boolean
            switch(item.isRequired) {
                case true:
                    isRequired = "Ja"
                break;
                case false:
                    isRequired = "Nein"
                break;
                default: isRequired = "UNDIFINED"
            }
            return ([
                index + 1,
                item.name,
                isRequired]
            )
        })

    const modal = (onCancel,edit,selectedItem) => {
        return (
            <OptionalAttributesForm
                onCancel={onCancel}
                edit={edit}
                selected={selectedItem}
            />
        )
    }

    return (
        <React.Fragment>
            <OverviewPage
                pageTitle="Optionale Attribute"
                newItemText="Neues Attribut"
                tableHeader={["#", "Bezeichnung", "Pflichtfeld"]}
                tableBody={tableBody}
                modal={modal}
                modalSize="lg"
                data={optionalAttributes}
                refreshData={loadOptionalAttributes}
            />
        </React.Fragment>
    )
}

export default optionalAttributesPage