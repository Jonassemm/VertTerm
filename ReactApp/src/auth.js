//author: Jonas Semmler
export const isAuthenticated = userID => !!userID;

export const hasRight = (userStore, rights) => {
  if(rights) {
    return  rights.some(right => userStore.rights.includes(right)); 
  } else {
    return false
  }
}

export const hasRole = (userStore, roles) => 
  roles.some(role => userStore.roles.includes(role))
