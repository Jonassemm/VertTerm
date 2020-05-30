import React, { useState, useEffect } from "react"
import { getAllPositions } from "./PositionRequests"
import PositionForm from "./PositionForm"
import OverviewPage from "../OverviewPage"

function PositionPage() {
    const [positions, setPositions] = useState([])

    useEffect(() => {
        loadPositions()
    }, [])

    const loadPositions = async () => {
        var data = [];
        try{ 
          const response = await getAllPositions();
          data = response.data;
        }catch (error) {
          console.log(Object.keys(error), error.message)
          alert("An error occoured while loading all positions")
        }
        setPositions(data);
    };

    const tableBody =
        positions.map((item, index) => {
            return ([
                index + 1,
                item.name,
                item.description]
            )
        })

    const modal = (onCancel,edit,selectedItem) => {
        return (
            <PositionForm
                onCancel={onCancel}
                edit={edit}
                selected={selectedItem}
            />
        )
    }

    return (
        <React.Fragment>
            <OverviewPage
                pageTitle="Positionen"
                newItemText="Neue Position"
                tableHeader={["#", "Bezeichnung", "Beschreibung"]}
                tableBody={tableBody}
                modal={modal}
                data={positions}
                refreshData={loadPositions}
            />
        </React.Fragment>
    )
}

export default PositionPage