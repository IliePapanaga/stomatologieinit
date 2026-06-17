/*eslint-disable no-unused-vars*/
import React from 'react';
/*eslint-enable no-unused-vars*/
import {connect} from 'react-redux';

import BaseView, {baseViewDispatcherMap, baseViewStateMap} from '../../../common/BaseView';
import {
    BUTTON_TYPE_ADD,
    BUTTON_TYPE_EDIT,
    DEFAULT_CELL_STYLE,
    SELECTION_MODE_SINGLE,
    STATUS_ACTIVE,
    STATUS_INACTIVE
} from '../../../../../utils/Constants';

import AddEditUser, {dialogInfo} from '../../../dialogs/users/AddEditUser';
import EditUser from '../../../dialogs/users/EditUser';
import Renderer from '../../../../../utils/Renderer';

class Users extends BaseView {
    initView(props) {
        let me = this;
        let configuration = {
            requestInfo: {
                fetchQueryName: 'systemUsers',
                getQueryName: "systemUser",
                addQueryName: "registerSystemUser",
                updateQueryName: "updateSystemUser",
                //deleteQueryName: "deletePracticeLocation",
                getResponseModel: "SystemUserModel",
                addResponseModel: "SystemUserModel",
                updateResponseModel: undefined
            },

            additionalFields: [],
            columns: [
                {
                    dataIndex: 'id', name: 'id', title: 'Id', hidden: true, cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'contact.name.last',
                    name: 'lastName',
                    title: 'Last Name',
                    orderName: 'LAST_NAME',
                    cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'contact.name.first',
                    name: 'firstName',
                    title: 'First Name',
                    orderName: 'FIRST_NAME',
                    cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'state',
                    name: 'state',
                    title: 'Status',
                    orderName: 'STATUS',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getStatusRenderer()
                },
                // {
                //     dataIndex: 'officeName', name: 'officeName', title: 'Region', orderName: 'OFFICE_NAME', cellClass: DEFAULT_CELL_STYLE
                // },
                {
                    dataIndex: 'modified',
                    name: 'modified',
                    title: 'Modified Date and Time',
                    orderName: 'MODIFIED_DATE',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getDateRenderer
                },
            ],
            actions: [
                {label: "Add", type: BUTTON_TYPE_ADD, dialog: AddEditUser, dialogInfo: dialogInfo},
                {label: "Edit", type: BUTTON_TYPE_EDIT, dialog: EditUser, dialogInfo: {}},
                {
                    label: "Activate",
                    onClick: me.onActivate,
                    allowedValues: [STATUS_INACTIVE],
                    allowedByFieldName: "state"
                },
                {
                    label: "Deactivate",
                    onClick: me.onDeactivate,
                    allowedValues: [STATUS_ACTIVE],
                    allowedByFieldName: "state"
                }
            ],
            selectionMode: SELECTION_MODE_SINGLE
        }
        return configuration;
    }


    onActivate(btn) {
        let me = this;
        me.activateDeactivate(btn, true);
    }

    onDeactivate(btn) {
        let me = this;
        me.activateDeactivate(btn, false);
    }

    activateDeactivate(btn, activate) {
        let me = this,
            subStr = activate ? 'activate' : 'deactivate';

        let classHeader = 'header ' +  subStr;

        let selection = me.state.selection;
        if (selection.length > 0) {
            me.onExecuteOperation(btn, {
                id: undefined,
                enabled: activate
            },
                <div class="modal57">
                    <div className={classHeader}><h2>{subStr}</h2></div>
                    <div class="body">
                        Are you sure you want to {subStr} {selection[0].contact.name.first}, {selection[0].contact.name.last} account?</div></div>, 'activateDeactivateSystemUser', 'UserActivateDeactivateResult', function (returnObject, selectedObject) {
                let updatedObject = Object.assign({}, selectedObject);
                updatedObject.state = returnObject.status;
                me.baseGrid.updateRow(selectedObject, updatedObject);
            });
        }
    }


}

const UsersConnector = connect(
    baseViewStateMap,
    baseViewDispatcherMap)(Users);

export default UsersConnector;