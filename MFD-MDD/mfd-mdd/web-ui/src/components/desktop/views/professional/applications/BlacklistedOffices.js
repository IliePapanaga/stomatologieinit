/*eslint-disable no-unused-vars*/
import React from 'react';
/*eslint-enable no-unused-vars*/
import {connect} from 'react-redux';

import BaseView, {baseViewDispatcherMap, baseViewStateMap} from '../../../common/BaseView';
import {DEFAULT_CELL_STYLE, SELECTION_MODE_SINGLE} from '../../../../../utils/Constants';
import Renderer from "../../../../../utils/Renderer";

class BlacklistedOffices extends BaseView {
    initView(props) {
        let me = this;

        let configuration = {
            requestInfo: {
                fetchQueryName: 'blackListedLocationSummary',
                //getQueryName: "practice",
                //addQueryName: "addPracticeLocation",
                updateQueryName: "updatePracticeOwnerGeneral",
                //deleteQueryName: "deletePracticeLocation",
                getResponseModel: "PracticeModel",
                //addResponseModel: "PracticeLocationModel",
                updateResponseModel: undefined
            },
            additionalFields: ['practiceLocationId'],
            columns: [
                {
                    dataIndex: 'practiceName', name: 'id', title: 'Id', hidden: true, cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'practiceLocationName', name: 'practiceLocationName', title: 'Location', orderName: 'PRACTICE_LOCATION_NAME', cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'practiceName', name: 'practiceName', title: 'Office', orderName: 'PRACTICE_NAME', cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'blackListDate', name: 'blackListDate', title: 'Ban Date', orderName: 'BLACK_LIST_DATE', cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getDateRenderer
                },
            ],
            actions: [
                { label: "Unbann", onClick: me.onUnBlackListed}
            ],
            selectionMode: SELECTION_MODE_SINGLE
        }
        return configuration;
    }

    prepareManagedObject(loadedObject) {
        let practiceOwner = loadedObject.practiceOwner
        delete loadedObject['practiceOwner'];
        return {
            practice: loadedObject,
            contact: practiceOwner.contact
        };
    }


    onUnBlackListed(btn) {
        let me = this;

        let selection = me.state.selection;
        if (selection.length > 0) {
            me.onExecuteOperation(btn, {
                practiceLocationId: selection[0].practiceLocationId
            }, <div className="modal57">
                <div className="header activate"><h2>UnBlackList</h2></div>
                <div className="body">Are you sure you want to unblacklist the location?</div>
            </div>, 'unBlackListLocation', undefined, function (returnObject, selectedObject) {
                me.baseGrid.deleteRows([selectedObject]);
            });
        }
    }

    isEnabledAction(element, selection) {
        let result = super.isEnabledAction(element, selection);
        if (result) {
            result = selection && selection.length === 1;
        }
        return result;
    }

}

const BlacklistedOfficesConnector = connect(
    baseViewStateMap,
    baseViewDispatcherMap)(BlacklistedOffices);

export default BlacklistedOfficesConnector;