import React, { useState, useEffect } from "react"
import { getAllRestrictions } from "./RestrictionRequests"
import RestrictionForm from "./RestrictionForm"
import OverviewPage from "../OverviewPage"

function RestrictionPage() {
    const [restrictionsa, setRestrictions] = useState([])

    useEffect(() => {
        loadRestrictions()
    }, [])

    const loadRestrictions = async () => {
        var data = [];
        try{ 
          const response = await getAllRestrictions();
          data = response.data;
        }catch (error) {
          console.log(Object.keys(error), error.message)
          alert("An error occoured while loading all restrictions")
        }
        setRestrictions(data);
    };

    console.log(restrictionsa)
    const tableBody =
        restrictionsa.map((item, index) => {
            return ([
                index + 1,
                item.name]
            )
        })

    const modal = (onCancel,edit,selectedItem) => {
        return (
            <RestrictionForm
                onCancel={onCancel}
                edit={edit}
                selected={selectedItem}
            />
        )
    }

    return (
        <React.Fragment>
            <OverviewPage
                pageTitle="Einschränkungen"
                newItemText="Neue Einschränkung"
                tableHeader={["#", "Bezeichnung"]}
                tableBody={tableBody}
                modal={modal}
                data={restrictionsa}
                refreshData={loadRestrictions}
            />
        </React.Fragment>
    )
}
export default RestrictionPage