import {EVENT_UPDATED_REFERENCES} from '../utils/Constants';

const initialState = {};

export default function references(state = initialState, action) {
    switch (action.type) {
        case EVENT_UPDATED_REFERENCES:
            return Object.assign(state,action.references);
        default:
            return state;
    }
}

