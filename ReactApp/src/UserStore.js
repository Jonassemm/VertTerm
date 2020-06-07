import { observable, action, decorate } from "mobx"
import { getUserData } from "./components/navigationComponents/ActiveUserRequests"

class UserStore {
    username = null
    setUsername(username) {
        this.username = username;
    }

    userID = null
    setUserID(id) {
        this.userID = id
    }

    rights = []
    setRights(rights) {
        this.rights = rights
    }

    roles = []
    setRoles(roles) {
        this.roles = roles
    }

    loggedIn = false
    setLoggedIn(loggedIn) {
        if (!loggedIn) {
            this.deleteCurrentUser()
        } else {
            this.getData()
        }
        this.loggedIn = loggedIn
    }

    async getData() {
            const res = await getUserData(this.userID)
            console.log(res)
            this.setRoles(res.data.roles.map(item => {
                        return (item.name)

                    }))
            const tempRights = []
            res.data.roles.forEach((item,index,array) => {
                item.rights.forEach((item, index) => {
                    tempRights.push(item)
                })
            })
            this.setRights(tempRights) //muss noch gefiltert werden (Rechte können bei mehreren Rollen doppelt vorkommen)
    }

    deleteCurrentUser() {
        this.setUsername(null)
        this.setUserID(null)
        this.setRights([])
        this.setRoles([])
    }
}

UserStore = decorate(UserStore, {
    username: observable,
    userID: observable,
    rights: observable,
    roles: observable,
    loggedIn: observable,
    setRights: action,
    setRoles: action,
    setUsername: action,
    setUserID: action,
    setLoggedIn: action
})

export { UserStore }