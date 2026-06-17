import {APP_TYPE_DESKTOP} from '../../utils/Constants'

const AppContext = {
    appType: APP_TYPE_DESKTOP,
    views: undefined,
    currentUser: undefined,
    logged: false,
    metaInfo:undefined,
    recaptchaHash:undefined
}

export default AppContext;