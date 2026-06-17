import React from 'react';
import {connect, Provider} from 'react-redux';

import {baseViewDispatcherMap, baseViewStateMap} from '../../../common/BaseView';
import FindByLocation from '../../common/FindByLocation';
import {
    BUTTON_TYPE_EDIT,
    BUTTON_TYPE_EDIT_ON_SELECTION,
    DEFAULT_CELL_STYLE,
    EVENT_AUTH_IMPERSONATE,
    FILTER_TYPE_DATE_RANGE,
    FILTER_TYPE_LOCATION,
    FILTER_TYPE_SEARCH,
    FILTER_TYPE_SELECT,
    FILTER_YES,
    NUMBER_CELL_STYLE,
    SELECTION_MODE_SINGLE,
    STATUS_ACTIVE,
    STATUS_INACTIVE
} from '../../../../../utils/Constants';
import {SPECIALTY} from '../../../../../data/Specialty';
import {BLACKLISTED, NEWCLIENTS} from '../../../../../data/Filters';
import AddEditPractice, {dialogInfo} from '../../../dialogs/practice/AddEditPractice';
import ReviewsForPractice, {dialogInfo as dialogInfoReviewsForPractic} from '../../../dialogs/practice/ReviewsForPractice';
import ViewLocations from '../../../dialogs/location/ViewLocations';
import Renderer from '../../../../../utils/Renderer';
import DateHelper from '../../../../../utils/DateHelper';
import ObjectHelper from '../../../../../utils/Object';
import Remote from '../../../../../utils/Remote';
import UiView from '../../../../../utils/UiView';
import {impersonate} from "../../../../../actions/common/authorization";

class Offices extends FindByLocation {
    initView(props) {
        let me = this;
        let configuration = {
            requestInfo: {
                fetchQueryName: 'systemUserPractices',
                getQueryName: "practice",
                //addQueryName: "addPracticeLocation",
                updateQueryName: "updatePracticeOwnerGeneral",
                //deleteQueryName: "deletePracticeLocation",
                getResponseModel: "PracticeModelInput",
                getResponseModelMode: 'input',
                //addResponseModel: "PracticeLocationModel",
                updateResponseModel: undefined
            },

            additionalFields: ['city', 'state', 'zipCode', 'officeRating', 'totalFeedback'],
            columns: [
                {
                    dataIndex: 'id', name: 'id', title: 'Id', hidden: true, cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'lastName',
                    name: 'lastName',
                    title: 'Client Last Name',
                    orderName: 'LAST_NAME',
                    cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'firstName',
                    name: 'firstName',
                    title: 'Client First Name',
                    orderName: 'FIRST_NAME',
                    cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'status',
                    name: 'status',
                    title: 'Status',
                    orderName: 'STATUS',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getStatusRenderer()
                },
                {
                    dataIndex: 'officeName',
                    name: 'officeName',
                    title: 'Company Name',
                    orderName: 'OFFICE_NAME',
                    cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'street', name: 'street', title: 'Address', cellClass: DEFAULT_CELL_STYLE,
                    renderer(street, cellClass, row, columnIndex, rowIndex) {
                        let value = `${street} ${row.city} ${row.state}  ${row.zipCode}`;
                        return <div class={cellClass}>{value}</div>
                    }
                },
                {
                    dataIndex: 'locations', name: 'locations', title: '#Locations', cellClass: NUMBER_CELL_STYLE,
                    renderer(value, cellClass, row, columnIndex, rowIndex) {
                        return <div class={cellClass}>
                            {value > 0 && <a style={{'cursor': 'pointer', textDecoration: 'underline'}}
                                             onClick={me.onShowLocations.bind(me, row)}>{value}</a>}
                            {value <= 0 && value}
                        </div>
                    }
                },
                {
                    dataIndex: 'officePhone',
                    name: 'officePhone',
                    title: 'Office Phone',
                    orderName: "OFFICE_PHONE",
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getPhoneRenderer
                },
                {
                    dataIndex: 'officeManagerName',
                    name: 'officeManagerName',
                    title: 'Office Manager',
                    orderName: "OFFICE_MANAGER",
                    cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'lastActivity',
                    name: 'lastActivity',
                    title: 'Last Activity Date',
                    orderName: "LAST_ACTIVITY",
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getDateRenderer
                }
            ],
            actions: [
                {label: "Edit", type: BUTTON_TYPE_EDIT, dialog: AddEditPractice, dialogInfo: dialogInfo},
                {
                    label: "Activate",
                    onClick: me.onActivate,
                    allowedValues: [STATUS_INACTIVE],
                    allowedByFieldName: "status"
                },
                {
                    label: "Deactivate",
                    onClick: me.onDeactivate,
                    allowedValues: [STATUS_ACTIVE],
                    allowedByFieldName: "status"
                },
                {
                    label: "On Behalf Of",
                    type: BUTTON_TYPE_EDIT,
                    hasSelectedRows: 1,
                    dialogInfo: {},
                    disallowedValues: [STATUS_INACTIVE],
                    disallowedByFieldName: "status"
                },
                {label: "See reviews", type: BUTTON_TYPE_EDIT_ON_SELECTION, dialog: ReviewsForPractice, dialogInfo: dialogInfoReviewsForPractic}
            ],
            filters: [
                {
                    type: FILTER_TYPE_SELECT,
                    name: "specialties",
                    menuItems: SPECIALTY,
                    multiple: false,
                    required: false,
                    title: 'Specialty'
                },
                {
                    type: FILTER_TYPE_SELECT,
                    name: "newclients",
                    menuItems: NEWCLIENTS,
                    multiple: false,
                    required: false,
                    title: 'New Clients'
                },
                {
                    type: FILTER_TYPE_SELECT,
                    name: "blacklisted",
                    menuItems: BLACKLISTED,
                    multiple: false,
                    required: false,
                    title: 'Blacklisted'
                },
                {
                    type: FILTER_TYPE_DATE_RANGE,
                    fromName: "lastActivityFrom",
                    toName: "lastActivityTo",
                    fromPlaceholder: "Last Activity From",
                    toPlaceholder: "Last Activity To"
                },
                {
                    type: FILTER_TYPE_SEARCH,
                    name: "textSearch",
                    multiple: false,
                    required: false,
                },
                {type: FILTER_TYPE_LOCATION}
            ],
            selectionMode: SELECTION_MODE_SINGLE,
            params: {nameStartsWith: undefined}
        }
        return configuration;
    }

    prepareManagedObject(loadedObject) {
        let practiceOwner = loadedObject.practiceOwner
        delete loadedObject['practiceOwner'];
        return {
            practice: loadedObject,
            practiceOwner: practiceOwner,
        };
    }

    onShowLocations(office, ev) {
        let me = this,
            requestFields = Remote.getFieldsByModel(me.props.metaInfo, 'PracticeModel', [], false);

        me.props.onLoadRecord({
            queryName: me.requestInfo.getQueryName,
            requestFields: requestFields
        }, office, function (object) {
            if (ObjectHelper.isObject(object)) {
                UiView.showDialog(<Provider store={UiView.createDialogStore()}><ViewLocations actions={{}}
                                                                                              managedObject={object}/></Provider>);
            }
        });
        ev.stopPropagation();
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
            },    <div class="modal57">
                <div className={classHeader}><h2>{subStr}</h2></div>
                <div class="body">Are you sure you want to {subStr} {selection[0].firstName}, {selection[0].lastName} account?</div></div>,
                'activateDeactivatePracticeOwner', 'UserActivateDeactivateResult', function (returnObject, selectedObject) {
                let updatedObject = Object.assign({}, selectedObject);
                updatedObject.status = returnObject.status;
                me.baseGrid.updateRow(selectedObject, updatedObject);
            });
        }
    }


    showDialog(scope, Dialog, Provider, target, object, actions) {
        let me=this;
        if(Dialog){
            super.showDialog(scope, Dialog, Provider, target, object, actions);
        }else{
            me.props.onImpersonate(object.practiceOwner.contact.email);
        }
    }

    convertFilterDataBeforeFiltering(data) {
        let returnData = {};

        Object.assign(returnData, data);

        if (returnData.blacklisted) {
            returnData.blacklisted = returnData.blacklisted === FILTER_YES;
        }
        if (returnData.lastActivityFrom && !returnData.lastActivityTo) {
            returnData.lastActivityTo = DateHelper.convertMomentToServerDateString(DateHelper.getCurrentDate());
        }

        if (returnData.lastActivityTo && !returnData.lastActivityFrom) {
            returnData.lastActivityFrom = returnData.lastActivityTo;
        }

        if (!returnData.lastActivityFrom) {
            //     returnData.lastActivityTo = undefined;
        } else {
            returnData.lastActivityFrom = DateHelper.setServerDateToStartTime(returnData.lastActivityFrom);
        }

        if (!returnData.lastActivityTo) {
            //     returnData.lastActivityFrom = undefined;
        } else {
            returnData.lastActivityTo = DateHelper.setServerDateToEndTime(returnData.lastActivityTo);
        }

        let newclients = returnData.newclients;

        delete returnData['newclients'];

        if (newclients) {
            DateHelper.initNewClientsFiltersData(newclients, returnData, 'newClientsTo', 'newClientsFrom');
        }

        return returnData;
    }

    prepareUpdatedObjectBeforeDisplay(oldObject, forUpdateObject, newObject) {
        return {
            firstName: forUpdateObject.practiceOwner.contact.name.first,
            lastName: forUpdateObject.practiceOwner.contact.name.last,
            officeName: forUpdateObject.practice.name,
            street: forUpdateObject.practiceOwner.contact.address.street,
            state: forUpdateObject.practiceOwner.contact.address.state,
            zipCode: forUpdateObject.practiceOwner.contact.address.zipCode,
            officePhone: forUpdateObject.practice.phone,
            officeManagerName: forUpdateObject.practice.officeManagerName
        };
    }
}

const OfficesConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseViewStateMap(state, ownProps), {
            metaInfo: state.context.metaInfo
        });
    },
    function (dispatch) {
        return Object.assign(baseViewDispatcherMap(dispatch), {
            onImpersonate: (username) => {
                dispatch(impersonate(username)).then(
                    function (result) {
                        if (result) {
                            dispatch({type: EVENT_AUTH_IMPERSONATE, result});
                            window.location.reload();
                        }
                    });
            }
        });
    })(Offices);

export default OfficesConnector;