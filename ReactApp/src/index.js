import React from "react"
import ReactDom from "react-dom"
import App from "./App"
import {CalendarStore} from "./components/calendarComponents/Store"
import 'mobx-react-lite/batchingForReactDom'

const calendarStore = new CalendarStore()

ReactDom.render(<App calendarStore={calendarStore}/>, document.getElementById("root"))