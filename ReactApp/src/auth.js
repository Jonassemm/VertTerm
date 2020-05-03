export const isAuthenticated = userID => !!userID;

export const isAllowed = (userStore, rights) =>
  rights.some(right => userStore.rights.includes(right));

export const hasRole = (userStore, roles) => 
  roles.some(role => userStore.roles.includes(role))
