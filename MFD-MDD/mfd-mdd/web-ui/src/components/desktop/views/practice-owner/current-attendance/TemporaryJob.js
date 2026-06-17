import React from 'react';
import {connect, Provider} from 'react-redux';

import {baseViewDispatcherMap, baseViewStateMap} from '../../../common/BaseView';
import {
    BUTTON_TYPE_ADD,
    CANDIDATE_DIRECT_BOOKING,
    DEFAULT_CELL_STYLE,
    REST_API_PREFIX_GET_USER_PHOTO,
    SELECTION_MODE_SINGLE,
    STATUS_NEED_CHECK_IN,
    STATUS_REJECTED
} from '../../../../../utils/Constants';
import {ATTENDANCE} from '../../../../../data/Statuses';
import Execute from '../../../dialogs/operations/Execute';
import Renderer from '../../../../../utils/Renderer';
import Field from '../../../common/form/Field';
import UiView from "../../../../../utils/UiView";
import Error from "../../../../../utils/Error";
import TextArea from "../../../common/form/TextArea";
import Checkbox from "../../../common/form/Checkbox";
import DateHelper, {uiDateFormat} from "../../../../../utils/DateHelper";
import AddPosting, {dialogInfo as addDialogInfo} from "../../../dialogs/postings/AddPosting";
import {TemporaryPostingJob} from "../postings-in-progress/TemporaryJob";

class TemporaryJob extends TemporaryPostingJob {
    initView(props) {
        let me = this;

        let configuration = {
            requestInfo: {
                fetchQueryName: 'attendances',
                addQueryName: me.generateAddQueryName,
                getResponseModel: "PracticeModel",
                //addResponseModel: "PracticeLocationModel",
                updateResponseModel: undefined
            },
            additionalFields: ['professionalId', 'attendanceEndDateTime'],
            columns: [
                {
                    dataIndex: 'jobDayId', name: 'jobDayId', title: 'Id', hidden: true, cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'attendanceStartDateTime',
                    name: 'attendanceStartDateTime',
                    title: 'Attendance Date and Time',
                    orderName: 'ATTENDANCE_DATE',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getDateRenderer
                },
                {
                    dataIndex: 'professionalFirstName',
                    name: 'professionalFirstName',
                    title: 'Professional Last Name', orderName: 'FIRST_NAME',
                    cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'professionalLastName',
                    name: 'professionalLastName',
                    title: 'Professional First Name', orderName: 'LAST_NAME',
                    cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'jobPostingName',
                    name: 'jobPostingName',
                    title: 'Posting', orderName: 'JOB_POSTING_NAME',
                    cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'jobDayStatus',
                    name: 'jobDayStatus',
                    title: 'Status', orderName: 'ATTENDANCE_STATUS',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getStatusRenderer(ATTENDANCE)
                },
                {
                    dataIndex: 'practiceLocationName',
                    name: 'practiceLocationName',
                    title: 'Location',
                    orderName: 'PRACTICE_LOCATION_NAME',
                    cellClass: DEFAULT_CELL_STYLE
                },
            ],
            actions: [
                {
                    label: "Check in", onClick: me.onExecute, name: "checkin",
                    allowedValues: [STATUS_NEED_CHECK_IN],
                    allowedByFieldName: "jobDayStatus"
                },
                {label: "Reject", onClick: me.onExecute, name: "reject", hasSelectedRows: 1},
                {label: "Alert", onClick: me.onExecute, name: "alert", hasSelectedRows: 1},
                {label: "No Show", onClick: me.onExecute, name: "noshow", hasSelectedRows: 1},
                {label: "SOS", onClick: me.onExecute, name: "sos", hasSelectedRows: 1},
                {
                    label: "Book Again",
                    type: BUTTON_TYPE_ADD,
                    dialog: AddPosting,
                    dialogInfo: addDialogInfo,
                    name: 'bookagain', hasSelectedRows: 1
                }
            ],
            selectionMode: SELECTION_MODE_SINGLE,
            hasPagination: true
        }
        return configuration;
    }


    onExecute(e) {
        let me = this;
        const target = e.target.parentElement;

        let selection = me.state.selection;
        if (selection.length > 0) {
            let selectedObject = selection[0];

            const actions = {
                save: function (editor, newManagedObject, successfulCallBack) {
                    try {
                        let requestInfo = {
                            addQueryName: undefined,
                            addResponseModel: undefined
                        }
                        let data = {};

                        switch (target.getAttribute('action-name')) {
                            case 'checkin':
                                requestInfo.addQueryName = 'checkInAttendance';
                                data.attendance = {jobDayId: selectedObject.jobDayId};
                                break;
                            case 'noshow':
                                requestInfo.addQueryName = 'addNoShow';
                                requestInfo.addResponseModel = 'NoShowModel';
                                data.noShow = {jobDayId: selectedObject.jobDayId};
                                break;
                            case 'bookagain':
                                requestInfo.addQueryName = 'checkInAttendance';
                                break;
                            case 'reject':
                                requestInfo.addQueryName = 'rejectEmployee';
                                data.attendance = {
                                    jobDayId: selectedObject.jobDayId,
                                    reason: newManagedObject.reason
                                };
                                break;
                            case 'alert':
                                requestInfo.addQueryName = 'alertAttendance';
                                data.attendance = {jobDayId: selectedObject.jobDayId};
                                break;
                            case 'sos':
                                requestInfo.addQueryName = 'sosAttendance';
                                data.attendance = {
                                    jobDayId: selectedObject.jobDayId,
                                    noShow: newManagedObject.noShow
                                };
                                break;
                            default:
                                break;
                        }

                        me.props.onSaveRecord(requestInfo, data, function (savedObject) {
                            // savedObject = me.prepareUpdatedObjectBeforeDisplay(null, newManagedObject, savedObject);
                            // me.baseGrid.addRow(savedObject);
                            me.baseGrid.onRefresh();
                            successfulCallBack.call(editor, editor);
                        }, me.props.metaInfo, false);
                    } catch (ex) {
                        Error.showErrors(ex)
                    }
                }
            }
            const mObject = {}
            switch (target.getAttribute('action-name')) {
                case 'checkin':
                    break;
                case 'noshow':
                    break;
                case 'bookagain':
                    break;
                case 'reject':
                    break;
                case 'alert':
                    break;
                case 'sos':
                    mObject.noShow = true;
                    break;
                default:
                    break;
            }
            UiView.showDialog(<Provider store={UiView.createDialogStore()}><Execute {...me.dialogProps(Execute, target)}
                                                                                    managedObject={mObject}
                                                                                    actions={actions}
                                                                                    references={me.props.references}
                                                                                    metaInfo={me.props.metaInfo}/></Provider>);

        }
    }

    dialogProps(dlg, target) {
        let me = this;
        let selection = me.state.selection;
        if (selection.length > 0) {
            let selectedRecord = selection[0];
            let src = REST_API_PREFIX_GET_USER_PHOTO.replace('{0}', selectedRecord.professionalId);

            switch (target.getAttribute('action-name')) {
                case 'checkin':
                    return {
                        title: <div class="alert__header">Check In</div>,
                        width: 470,
                        height: 440,
                        className: 'alertdialogs',
                        body: <div class="attention__main modal37">
                            <div class="attention__main-item">
                                <p>Are you going to confirm Check in of Professional to the job?</p>
                            </div>
                            <div class="attention__main-item">
                                <img src={src} alt=""/>
                                <p>{selectedRecord.professionalFirstName}, {selectedRecord.professionalLastName}</p>
                            </div>
                            <div class="attention__main-item">
                                <div class="attention__main-item-time">
                                    <div class="title">
                                        <p>Starts:</p>
                                    </div>
                                    <div class="data">
                                        <p>{DateHelper.convertServerDateStringToString(selectedRecord.attendanceStartDateTime, uiDateFormat)}</p>
                                    </div>
                                </div>
                                <div class="attention__main-item-time">
                                    <div class="title">
                                        <p>Ends:</p>
                                    </div>
                                    <div class="data">
                                        <p>{DateHelper.convertServerDateStringToString(selectedRecord.attendanceEndDateTime, uiDateFormat)}</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    };

                case 'noshow':
                    return {
                        title: <div class="alert__header">No Show</div>,
                        width: 470,
                        height: 520,
                        body: <div class="attention__main modal37 modal38">
                            <div class="attention__main-item">
                                <p>Are you going to confirm that Professional has not attended the job?</p>
                                <p>Please be informed that delete this record is not possible!</p>
                                <p>After two No Shows a candidate will be not allowed to use an application!!!</p>
                            </div>
                            <div class="attention__main-item">
                                <img src={src} alt=""/>
                                <p>{selectedRecord.professionalFirstName}, {selectedRecord.professionalLastName}</p>
                            </div>
                            <div class="attention__main-item">
                                <div class="attention__main-item-time">
                                    <div class="title">
                                        <p>Starts:</p>
                                    </div>
                                    <div class="data">
                                        <p>{DateHelper.convertServerDateStringToString(selectedRecord.attendanceStartDateTime, uiDateFormat)}</p>
                                    </div>
                                </div>
                                <div class="attention__main-item-time">
                                    <div class="title">
                                        <p>Ends:</p>
                                    </div>
                                    <div class="data">
                                        <p>{DateHelper.convertServerDateStringToString(selectedRecord.attendanceEndDateTime, uiDateFormat)}</p>
                                    </div>
                                </div>
                            </div>

                            <div class="attention__main-item">
                                <Field name="managedObject.noShow"
                                       component={Checkbox}/>
                                <div class="attention__main-item-text">
                                    <p>Cancel all job dates
                                        for {selectedRecord.professionalFirstName}, {selectedRecord.professionalLastName}</p>
                                </div>
                            </div>
                        </div>

                    };


                case 'bookagain':
                    return {
                        pageProps: {directBooking: true}
                    };


                case 'reject':
                    return {
                        title: <div class="alert__header">Reject</div>,
                        width: 470,
                        height: 410,
                        body: <div class="attention__main modal9">
                            <div class="attention__main-item">
                                <div class="text">
                                    <br/>
                                    <p>Are you sure to reject
                                        the {selectedRecord.professionalFirstName}, {selectedRecord.professionalLastName}?</p>
                                </div>
                                <div class="text">
                                    <br/>
                                    <p>Please provide the reason for it:</p>
                                </div>
                                <div class="item__box">
                                    <div class="text">
                                        <p>Comments<span class="star">*</span>:</p>
                                    </div>
                                    <div class="input">
                                        <Field name="managedObject.reason" component={TextArea}
                                               placeholder="Your text..."
                                               required={true}/>
                                    </div>
                                </div>
                                <div class="text">
                                    <p class="mandatory">Fields marked* are mandatory</p>
                                </div>
                            </div>
                        </div>
                    };


                case 'alert':
                    return {
                        title: <div class="alert__header">Alert</div>,
                        width: 470,
                        height: 410,
                        body: <div class="attention__main modal9">
                            <div class="attention__main-item">
                                <div class="text">
                                    <p>Are you sure you want to send an alert to {selectedRecord.professionalFirstName} {selectedRecord.professionalLastName}?</p>
                                </div>
                            </div>
                        </div>
                    };


                case 'sos':
                    return {
                        title: <div class="alert__header">SOS</div>,
                        width: 470,
                        height: 470,
                        body: <div class="attention__main modal9">
                            <div class="attention__main-item">
                                <div class="text">
                                    <p>Are you sure to invoke SOS action?</p>
                                </div>
                                <div class="text">
                                    <br/>
                                    <p>In this case, Mayday Dental Staffing administrator will receive a notification,
                                        that a dental office requires an urgent replacement in this position.</p>
                                    <p>Important:</p>
                                    <p>1. If “No Show” is unchecked, your office will be charged one hour after job
                                        starts. Within this hour, you may click “No Show” or “Reject” for this posting
                                        at “Current Attendance” View in order not be charged for the posting.</p>
                                    <p>2. If “No Show” is checked, your office is not charged for this posting. However,
                                        a candidate will get “No Show” scoring.</p>
                                    <p>Please be aware that a dental office is charged for a job posting.</p>
                                    <p>By clicking OK you allow Mayday Dental Staffing representative to impersonate you
                                        and find a candidate ASAP until end of assignment time</p>
                                    <br/>
                                </div>
                                <div class="attention__main-item">
                                    <Field name="managedObject.noShow"
                                           component={Checkbox} title="No Show"
                                           disabled={[STATUS_REJECTED].includes(selectedRecord.jobDayStatus)}/>
                                </div>
                            </div>
                        </div>
                    };
                default:
                    break;
            }
        }
        return null;
    }

    // isEnabledAction(element, selection) {
    //     let result = selection && selection.length === 1;
    //     return result;
    // }

    prepareManagedObject(loadedObject, target) {
        let me = this;

        let result = super.prepareManagedObject(loadedObject, target);

        let selection = me.state.selection;

        if (selection.length > 0) {
            result.preferredCandidateId = selection[0].professionalId;
            result.candidate = CANDIDATE_DIRECT_BOOKING;
        }

        return result;
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
