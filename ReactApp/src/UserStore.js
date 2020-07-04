import { observable, action, decorate } from "mobx"
import { getUserData } from "./components/navigationComponents/ActiveUserRequests"

class UserStore {
    user = null
    setUser(user) {
        this.user = user
    }

    username = null
    setUsername(username) {
        this.username = username;
    }

    firstName = null
    setFirstName(firstName) {
        this.firstName = firstName
    }

    lastName = null
    setLastName(lastName) {
        this.lastName = lastName
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
        /* const tempRights = []
        res.data.roles.forEach((item,index,array) => {
            item.rights.forEach((item, index) => {
                tempRights.push(item)
            })
        }) */
        var allRights = []
        res.data.roles.map((role) => {
            role.rights.map((right)=> {
                if(!allRights.some(allRights => allRights.id == right.id)){
                    allRights.push(right.name) //only push rights which are not in allRights
                }
            })
        })
        this.setUser(res.data)
        this.setRights(allRights) //without multiple rights
        this.setUsername(res.data.username)
        this.setFirstName(res.data.firstName)
        this.setLastName(res.data.lastName)
        this.setUserID(res.data.id)
    }

    deleteCurrentUser() {
        this.setUser(null)
        this.setUsername(null)
        this.setFirstName(null)
        this.setLastName(null)
        this.setUserID(null)
        this.setRights([])
        this.setRoles([])
    }
}

UserStore = decorate(UserStore, {
    user: observable,
    username: observable,
    firstName: observable,
    lastName: observable,
    userID: observable,
    rights: observable,
    roles: observable,
    loggedIn: observable,
    setUser: action,
    setRights: action,
    setRoles: action,
    setUsername: action,
    setFirstName: action,
    setLastName: action,
    setUserID: action,
    setLoggedIn: action
})

export { UserStore }