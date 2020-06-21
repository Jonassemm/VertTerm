import React from "react"
import SearchAptCalendar from "./calendarComponents/SearchCalendar/SearchAptCalendar"

export function TestComponent() {
    const resourceTypes = [{ id: "5ee92d390c3d3973b03b2e03" }]
    const positions = [{ id: "5ee65ad80205b94fdf925982" }, { id: "5ee65adb0205b94fdf925983" }]
    const procedure = {
        "name": "TERMIN",
        "description": "",
        "duration": null,
        "pricePerInvocation": 0,
        "pricePerHour": 0,
        "status": "active",
        "precedingRelations": [],
        "subsequentRelations": [],
        "neededResourceTypes": [],
        "neededEmployeePositions": [{ "id": "5ee65ad80205b94fdf925982", "ref": "position" }],
        "restrictions": [{ "id": "5ee91edaccb27165343e9b41", "ref": "restriction" }, { "id": "5ee91ececcb27165343e9b40", "ref": "restriction" }],
        "availabilities": [
            { "id": "5ee65cec7a253c2581630dc6", "startDate": "14.06.2020 00:00", "endDate": "14.06.2020 15:00", "rhythm": "daily", "frequency": 1, "endOfSeries": null }],
        "id": "5ee65cec7a253c2581630dc7"
    }
    return (
        <SearchAptCalendar neededPositions={positions} neededResourceTypes={resourceTypes} procedure={procedure} />
    )
}