import {EVENT_LOADED_VERSION} from '../utils/Constants';

const initialState = { server: 'n/a', ui: 'n/a' };

export default function version(state = initialState, action) {
  switch (action.type) {
    case EVENT_LOADED_VERSION:
      if (!action.version.server) {
        action.version.server = initialState.server;
      }
      if (!action.version.ui) {
        action.version.ui = initialState.ui;
      }
      return action.version;
    default:
      break;
  }
  return state;
}