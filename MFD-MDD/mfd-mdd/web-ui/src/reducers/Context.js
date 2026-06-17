import AppContext from '../models/core/AppContext';
import {
    EVENT_AUTH_LOGIN,
    EVENT_AUTH_LOGIN_PROCCESSINS,
    EVENT_AUTH_LOGOUT,
    EVENT_AUTH_UNAUTHORIZED,
    EVENT_AUTH_USER_LOADED,
    EVENT_CHANGED_RECAPTCHA_HASH
} from '../utils/Constants';


let ViewsConfig = require('../configs/Views');

const initialState = AppContext;

let update = undefined;

initialState.views = ViewsConfig;

export default function context(state = initialState, action) {

  switch (action.type) {
    case EVENT_AUTH_LOGIN:
      return {
            ...state, logged: action.logged
      };
    case EVENT_CHANGED_RECAPTCHA_HASH:
      return {
            ...state, recaptchaHash: action.recaptchaHash
      };
    case EVENT_AUTH_LOGOUT:
      break;
    case EVENT_AUTH_USER_LOADED:
      update = { currentUser: action.currentUser, metaInfo: action.metaInfo, logged: !!action.payload };
      return {
            ...state, ...update
      }
    case EVENT_AUTH_UNAUTHORIZED:
      update = { currentUser: action.currentUser, metaInfo: action.metaInfo, logged: false };
      return {
            ...state, ...update
      }
    case EVENT_AUTH_LOGIN_PROCCESSINS:
      return state;
    default:
      return state;
  }


  return state;
}