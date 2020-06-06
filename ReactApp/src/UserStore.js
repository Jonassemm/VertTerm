import { observable, action, decorate } from "mobx"
import { getAllUserData } from "./components/administrationComponents/userComponents/UserRequests"

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
            this.setRoles(res.data.roles.map(item => {
                        return (item.name)

                    }))
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