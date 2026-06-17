/*eslint-disable no-unused-vars*/
import React from 'react';
/*eslint-enable no-unused-vars*/
import {connect} from 'react-redux';

import BaseView, {baseViewDispatcherMap, baseViewStateMap} from '../../../common/BaseView';
import {
    BUTTON_TYPE_EDIT,
    DEFAULT_CELL_STYLE,
    FILTER_TYPE_DATE_RANGE,
    FILTER_TYPE_SELECT,
    NUMBER_CELL_STYLE,
    SELECTION_MODE_SINGLE, STATUS_ACCEPTED,
    STATUS_ACTIVE,
    STATUS_BOOKED,
    STATUS_NEW,
    STATUS_NEED_CHECK_IN} from '../../../../../utils/Constants';
import {TEMPORARY_POSTING} from '../../../../../data/Statuses';
import TemporaryApplyPosting, {dialogInfo as temporaryApplyPosting} from '../../../dialogs/professionals/TemporaryApplyPosting';
import TemporaryAcceptPosting, {dialogInfo as  temporaryAcceptPosting} from '../../../dialogs/professionals/TemporaryAcceptPosting';
import Renderer from '../../../../../utils/Renderer';
import {getDataPromise} from "../../../../../actions/common/getData";
import Remote from "../../../../../utils/Remote";

class TemporaryJob extends BaseView {
    recordLabel = "posting";
    professionalInfo = undefined;

    async componentDidMount() {
        let me = this,
            userId = me.props.userId,
            fields = Remote.getFieldsByModel(me.props.metaInfo, "ProfessionalModel", []);

        let professionalInfo = await getDataPromise('professional', userId, fields);

        this.professionalInfo = professionalInfo;

    }
    generateUpdateQueryName(managedObject) {
        let me = this;
        let selection = me.baseGrid.state.selection;
        if (selection && selection.length === 1 && selection[0].applicationStatus === STATUS_BOOKED) {
            return 'acceptApplication';
        }
        return "apply";
    }

    initView(props) {
        let me = this;

        let configuration = {
            additionalFields: ['startTime', 'endTime', 'applicationId',"jobDayId","practiceLocationId", "practiceLocationName", "alerted", "practiceLocationAddressCity"],
            requestInfo: {
                fetchQueryName: 'professionalTemporaryJobPostings',
                getQueryName: ["jobPosting", "application"],
                //addQueryName: "addPracticeLocation",
                updateQueryName: me.generateUpdateQueryName.bind(me),
                //deleteQueryName: "deletePracticeLocation",
                getResponseModel: ["JobPosting", "JobPostingApplication"],
                //addResponseModel: "PracticeLocationModel",
                updateResponseModel: undefined,
                getQueryParameterName: ["id", {parameterName: "id", idParameterName: "applicationId"}]
            },
            columns: [
                {
                    dataIndex: 'id', name: 'id', title: 'Id', hidden: true, cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'name',
                    name: 'name',
                    title: 'Name',
                    orderName: 'NAME',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getAlertRenderer(me.onSendAlert,function(row){return row['alerted']}, me)
                },
                {
                    dataIndex: 'applicationStatus',
                    name: 'applicationStatus',
                    title: 'Status',
                    orderName: 'APPLICATION_STATUS',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getStatusRenderer(TEMPORARY_POSTING.professional)
                },
                {
                    dataIndex: 'practiceName',
                    name: 'practiceName',
                    title: 'Company Name',
                    orderName: 'PRACTICE_NAME',
                    cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'practiceLocationAddressCity',
                    name: 'practiceLocationAddressCity',
                    title: 'City Name',
                    cellClass: DEFAULT_CELL_STYLE,
                    orderName: 'PRACTICE_LOCATION_ADDRESS_CITY'
                },
                {
                    dataIndex: 'startDate',
                    name: 'startDateTime',
                    title: 'Start Date',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getDatePostingRenderer,
                    orderName: 'START_DATE'
                },
                {
                    dataIndex: 'endDate',
                    name: 'endDateTime',
                    title: 'End Date',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getDatePostingRenderer,
                    orderName: 'END_DATE'
                },
                {
                    dataIndex: 'distance', name: 'distance', title: 'Distance (mi)', cellClass: NUMBER_CELL_STYLE,
                    orderName: 'DISTANCE'
                },
                {
                    dataIndex: 'postedDate', name: 'postedDate', title: 'Posted Date', cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getDateRenderer,
                    orderName: 'POSTED_DATE'
                }
            ],
            actions: [
                {
                    label: "Apply",
                    type: BUTTON_TYPE_EDIT,
                    dialog: TemporaryApplyPosting,
                    dialogInfo: temporaryApplyPosting,
                    allowedValues: [STATUS_ACTIVE], allowedByFieldName: "applicationStatus"
                },
                {
                    label: "Accept",
                    type: BUTTON_TYPE_EDIT,
                    dialog: TemporaryAcceptPosting,
                    dialogInfo: temporaryAcceptPosting,
                    allowedValues: [STATUS_BOOKED], allowedByFieldName: "applicationStatus"
                },
                {
                    label: "Check in", onClick: me.onCheckin,
                    allowedValues: [STATUS_NEED_CHECK_IN], allowedByFieldName: "applicationStatus"
                },
                {
                    label: "Reject", onClick: me.onReject,
                    allowedValues: [STATUS_BOOKED, STATUS_ACCEPTED], allowedByFieldName: "applicationStatus"
                },
                {
                    label: "Cancel Assignment", onClick: me.onWithRaw,
                    allowedValues: [STATUS_NEW], allowedByFieldName: "applicationStatus"
                },
                {
                    label: "Ban",
                    onClick: me.onUnBlackList,
                    //allowedValues: [false],
                    //allowedByFieldName: "blackListed",
                    hasSelectedRows: 1
                },
                {
                    label: "View Assignment",
                    type: BUTTON_TYPE_EDIT,
                    dialog: TemporaryAcceptPosting,
                    dialogInfo: temporaryApplyPosting,
                    // disallowedValues: [STATUS_ACTIVE], disallowedByFieldName: "applicationStatus",
                    name: "viewPosting"
                }
            ],
            filters: [
                {
                    type: FILTER_TYPE_SELECT,
                    name: "status",
                    menuItems: TEMPORARY_POSTING.professional,
                    multiple: false,
                    required: false,
                    title: 'Status'
                },
                {
                    type: FILTER_TYPE_DATE_RANGE,
                    fromName: "startDate",
                    toName: "endDate",
                    fromPlaceholder: "Start Date",
                    toPlaceholder: "End Date",
                    onlyDate: true,
                    allowedFuture: true,
                    allowedHistory: false
                }
            ],
            selectionMode: SELECTION_MODE_SINGLE
        }
        return configuration;
    }

    prepareManagedObject(loadedObjects, target) {
        let me = this;

        let loadedObject = loadedObjects.jobPosting;

        let selection = me.state.selection;
        if (selection.length > 0) {
            loadedObject.applicationId = selection[0].applicationId;
        }

        loadedObject.application = loadedObjects.application;

        return loadedObject;
    }

    onReject(btn) {
        let me = this;
        let selection = me.state.selection;
        if (selection.length > 0) {

            let messageInfo = 'You are about to reject an accepted job. Are you sure?\n' +
                'Please be informed that after five rejections, penalties by Mayday Dental Staffing are applied and you can be excluded from the system.';
            if(me.professionalInfo.denialsCount>=4){
                messageInfo = 'You are about to reject an accepted job. Are you sure?\n' +
                    'Please be informed it is yours fifth rejection! \n' +
                    'After 5th rejection if you want to continue job search please contact Mayday Dental Staffing representative.';
            }

            me.onExecuteOperation(btn, {
                    applicationId: selection[0].applicationId
                },
                <div class="modal57">
                    <div class="header deactivate"><h2>reject</h2></div>
                    <div class="body">{messageInfo}</div>
                </div>, 'rejectApplication', undefined, function (returnObject, selectedObject) {
                    let updatedObject = Object.assign({}, selectedObject);
                    updatedObject.applicationStatus = STATUS_ACTIVE;
                    me.baseGrid.updateRow(selectedObject, updatedObject);
                });
        }
    }

    onCheckin(btn) {
        let me = this;
        let selection = me.state.selection;
        if (selection.length > 0) {
            me.onExecuteOperation(btn, {
                attendance: {jobDayId: selection[0].jobDayId}
            },
                <div class="modal57">
                    <div class="header activate"><h2>check in</h2></div>
                    <div class="body">Are you sure to check in?</div>
                </div>, 'checkInAttendance', undefined, function (returnObject, selectedObject) {
                let updatedObject = Object.assign({}, selectedObject);
                updatedObject.applicationStatus = returnObject.applicationStatus;
                me.baseGrid.updateRow(selectedObject, updatedObject);
            });
        }
    }

    onWithRaw(btn) {
        let me = this;
        let selection = me.state.selection;
        if (selection.length > 0) {
            me.onExecuteOperation(btn, {
                    applicationId: selection[0].applicationId
                },
                <div class="modal57">
                    <div class="header deactivate"><h2>Ignore Offer</h2></div>
                    <div class="body">Are you sure you want to decline the offer?</div>
                </div>,
                'withdrawApplication', undefined, function (returnObject, selectedObject) {
                    let updatedObject = Object.assign({}, selectedObject);
                    updatedObject.applicationStatus = STATUS_ACTIVE;
                    me.baseGrid.updateRow(selectedObject, updatedObject);
                });
        }
    }



    onBlackList(btn) {
        let me = this;
        me.blackUnBlackList(btn, true);
    }

    onUnBlackList(btn) {
        let me = this;
        me.blackUnBlackList(btn, false);
    }

    blackUnBlackList(btn, blackList) {
        let me = this,
          headerSubStr = blackList ? 'remove from banned list': 'add to banned list' ;

        let classHeader = 'header ' +  headerSubStr;

        let selection = me.state.selection;
        if (selection.length > 0) {
          const subStr = blackList ? `remove [${selection[0].practiceName} - ${selection[0].practiceLocationName}] from banned list`: `add [${selection[0].practiceName} - ${selection[0].practiceLocationName}] to banned list`;
            me.onExecuteOperation(btn, {
                    practiceLocationId: selection[0].practiceLocationId
                },
                <div class="modal57">
                    <div className={classHeader}><h2>{headerSubStr}</h2></div>
                    <div class="body">Are you sure you want to {subStr}?</div></div>,
                blackList?'unBlackListLocation':'blackListLocation', undefined, function (returnObject, selectedObject) {
                    let updatedObject = Object.assign({}, selectedObject);
                    updatedObject.blackListed = !selectedObject.blackListed;
                    me.baseGrid.updateRow(selectedObject, updatedObject);
                    me.baseGrid.onRefresh();
                });
        }
    }

    onSendAlert(type, record, eOpts) {
        let me = this;
        let template = type;

        me.state.selection = [record];

        me.onExecuteOperation(undefined, {
            reply: {template: template, temporaryJobPostingApplicationId: record.applicationId}
        }, <div class="modal57">
            <div class="header important"><h2>alerts</h2></div>
            <div class="body">Are you sure to respond to the Doctor?</div>
        </div>, 'replyAlertAttendance', undefined, function (returnObject, selectedObject) {
        });

        eOpts.stopPropagation();
        eOpts.preventDefault();
    }

    dialogProps(dlg, button) {
        switch (button.getAttribute('action-name')) {
            case 'viewPosting':
                return {readOnly: true};
            default:
                return null;
        }
    }
}

const TemporaryJobConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseViewStateMap(state, ownProps), {
            userId: state.context.currentUser ? state.context.currentUser.id : undefined
        });
    },
    baseViewDispatcherMap)(TemporaryJob);

export default TemporaryJobConnector;