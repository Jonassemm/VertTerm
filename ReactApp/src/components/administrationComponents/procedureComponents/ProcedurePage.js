import React, { useState, useEffect } from "react"
import OverviewPage from "../../OverviewPage"
import ProcedureForm from "./ProcedureForm"
import { getProcedures } from "./ProcedureRequests"

function ProcedurePage() {
    const [procedures, setProcedudres] = useState([])

    async function prepareProcedures() {
        const res = await getProcedures()
        console.log(res)
        const result = res.data.map(item => {
            return { ...item }
        })
        setProcedudres(result)
    }

    useEffect(() => {
        prepareProcedures()
    }, [])

    const tableBody =
        procedures.map((item,index) => {
            return ([
               index + 1,
               item.name,
               item.description,
               item.status 
            ])
        })

    const modal = (onCancel, edit, selectedItem) => {
        return (
            <ProcedureForm
                onCancel={onCancel}
                edit={edit}
                selected={selectedItem}
            />
        )
    }

    return (
        <React.Fragment>
            <OverviewPage 
                pageTitle="Prozeduren"
                newItemText="Neue Prozedur"
                tableHeader={["#", "Bezeichnung", "Beschreibung", "Status"]}
                tableBody={tableBody}
                modal={modal}
                data={procedures}
                refreshData={prepareProcedures} 
                modalSize="xl"
            />
        </React.Fragment>
    )

}

export default ProcedurePage