import {
    EVENT_CHANGE_SELECTION_IN_GRID,
    EVENT_FINISH_LOADING_DATA_INTO_GRID,
    EVENT_START_LOADING_DATA_INTO_GRID
} from '../utils/Constants';

const initialState = {
    selection: [],

};

export default function view(state = initialState, action) {
    switch (action.type) {
        case EVENT_START_LOADING_DATA_INTO_GRID:
            break;
        case EVENT_FINISH_LOADING_DATA_INTO_GRID:
            break;
        case EVENT_CHANGE_SELECTION_IN_GRID:
            let update = [];
            if (action.selection) {
                action.selection.forEach(function(element) {
                    update.push(element);
                }, this);
            }
            //state.selection = update;
         return {
         ...state, selection:update
         }

        default:
            return state;
    }

    return state;
}

