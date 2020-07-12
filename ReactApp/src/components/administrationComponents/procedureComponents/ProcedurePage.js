import React, { useState, useEffect } from "react"
import OverviewPage, {modalTypes} from "../../OverviewPage"
import ProcedureForm from "./ProcedureForm"
import { getProcedures } from "../../requests"
import {ExceptionModal} from "../../ExceptionModal"

function ProcedurePage({userStore}) {
    const [procedures, setProcedudres] = useState([])

    //exception needs overriding (ExceptionModal)
    const [exception, setException] = useState(null)
    const [showExceptionModal, setShowExceptionModal] = useState(false)

    async function prepareProcedures() {
        const res = await getProcedures("NOTDELETED")
        const result = res.data.map(item => {
            return { ...item }
        })
        setProcedudres(result)
    }

    useEffect(() => {
        prepareProcedures()
    }, [])

    //-------------------------------ExceptionModal--------------------------------
    const handleExceptionChange = (newException) => {
        setException(newException)
        setShowExceptionModal(true)
    }

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
                setException={handleExceptionChange}
                userStore={userStore}
            />
        )
    }

    return (
        <React.Fragment>
            {exception != null && 
                <ExceptionModal
                    showExceptionModal={showExceptionModal} 
                    setShowExceptionModal={setShowExceptionModal} 
                    exception={exception}
                    warning={"AvailabilityWarning"}
                />
            }
            <OverviewPage 
                pageTitle="Prozeduren"
                newItemText="Neue Prozedur"
                tableHeader={["#", "Bezeichnung", "Beschreibung", "Status"]}
                tableBody={tableBody}
                modal={modal}
                data={procedures}
                refreshData={prepareProcedures} 
                userStore={userStore}
                modalType={modalTypes.procedure}
                modalSize="xl"
            />
        </React.Fragment>
    )

}

export default ProcedurePage