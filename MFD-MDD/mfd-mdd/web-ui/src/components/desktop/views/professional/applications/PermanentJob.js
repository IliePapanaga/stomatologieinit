import React from 'react';
import {connect, Provider} from 'react-redux';

import BaseView, {baseViewDispatcherMap, baseViewStateMap} from '../../../common/BaseView';
import {
    BUTTON_TYPE_EDIT,
    DEFAULT_CELL_STYLE,
    FILTER_TYPE_DATE,
    FILTER_TYPE_SELECT,
    INTERVIEW_TYPE_PERSONAL,
    INTERVIEW_TYPE_WORKING,
    NUMBER_CELL_STYLE,
    SELECTION_MODE_SINGLE,
    STATUS_ACCEPTED,
    STATUS_ACTIVE,
    STATUS_BOOKED,
    STATUS_INVITED,
    STATUS_NEW,
    STATUS_SCHEDULED
} from '../../../../../utils/Constants';
import {PERMANENT_POSTING} from '../../../../../data/Statuses';
import PermanentApplyPosting, {dialogInfo as permanentApplyPosting} from '../../../dialogs/professionals/PermanentApplyPosting';
import PermanentSelectInterviewDate from '../../../dialogs/professionals/PermanentSelectInterviewDate';
import Renderer from '../../../../../utils/Renderer';
import Remote from "../../../../../utils/Remote";
import {getDataPromise} from "../../../../../actions/common/getData";
import {DAYS_OF_WEEK} from "../../../../../data/DaysOfWeek";
import UiView from "../../../../../utils/UiView";
import {saveDataPromise} from "../../../../../actions/common/saveData";

class PermanentJob extends BaseView {
    recordLabel = "posting";
    professionalInfo = undefined;

    async componentDidMount() {
        let me = this,
            userId = me.props.userId,
            fields = Remote.getFieldsByModel(me.props.metaInfo, "ProfessionalModel", []);

        let professionalInfo = await getDataPromise('professional', userId, fields);

        this.professionalInfo = professionalInfo;

    }

    initView(props) {
        let me = this;

        let configuration = {
            additionalFields: ['applicationId', 'interviewId', "practiceLocationAddressCity"],
            requestInfo: {
                fetchQueryName: 'professionalPermanentJobPostings',
                getQueryName: "jobPosting",
                //addQueryName: "addPracticeLocation",
                updateQueryName: "apply",
                //deleteQueryName: "deletePracticeLocation",
                getResponseModel: "JobPosting",
                //addResponseModel: "PracticeLocationModel",
                updateResponseModel: undefined
            },
            columns: [
                {
                    dataIndex: 'id', name: 'id', title: 'Id', hidden: true, cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'name', name: 'name', title: 'Name', orderName: 'NAME', cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'applicationStatus',
                    name: 'applicationStatus',
                    title: 'Status',
                    orderName: 'APPLICATION_STATUS',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getStatusRenderer(PERMANENT_POSTING.professional)
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
                    name: 'startDate',
                    title: 'Start Date',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getDatePostingRenderer,
                    orderName: 'START_DATE'
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
                    dialog: PermanentApplyPosting,
                    dialogInfo: permanentApplyPosting,
                    allowedValues: [STATUS_ACTIVE], allowedByFieldName: "applicationStatus"
                },
                {
                    label: "Cancel Assignment", onClick: me.onWithRaw,
                    allowedValues: [STATUS_NEW], allowedByFieldName: "applicationStatus"
                },
                {
                    label: "Interview Date", onClick: me.onInterviewDate,
                    allowedValues: [STATUS_INVITED, STATUS_SCHEDULED], allowedByFieldName: "applicationStatus"
                },
                {
                    label: "Cancel Interview", onClick: me.onCancelInterview,
                    allowedValues: [STATUS_INVITED, STATUS_SCHEDULED], allowedByFieldName: "applicationStatus"
                },
                {
                    label: "Accept Offer", onClick: me.onAcceptOffer,
                    allowedValues: [STATUS_BOOKED], allowedByFieldName: "applicationStatus"
                },
                {
                    label: "Reject", onClick: me.onReject,
                    allowedValues: [STATUS_ACCEPTED], allowedByFieldName: "applicationStatus"
                },
                {
                    label: "View Assignment",
                    type: BUTTON_TYPE_EDIT,
                    dialog: PermanentApplyPosting,
                    dialogInfo: permanentApplyPosting,
                    name: "viewPosting"
                }

            ],
            filters: [
                {
                    type: FILTER_TYPE_SELECT,
                    name: "status",
                    menuItems: PERMANENT_POSTING.professional,
                    multiple: false,
                    required: false,
                    title: 'Status'
                },
                {
                    type: FILTER_TYPE_DATE, placeholder: "Start Date", name: "startDate", onlyDate: true,
                    allowedHistory: false
                }
            ],
            selectionMode: SELECTION_MODE_SINGLE
        }
        return configuration;
    }

    onWithRaw(btn) {
        let me = this;
        let selection = me.state.selection;
        if (selection.length > 0) {
            me.onExecuteOperation(btn, {
                applicationId: selection[0].applicationId
            },  <div class="modal57">
                <div class="header important"><h2>Ignore Offer</h2></div>
                <div class="body">Are you sure you want to decline the offer?</div>
            </div>,
                'withdrawApplication', undefined, function (returnObject, selectedObject) {
                let updatedObject = Object.assign({}, selectedObject);
                updatedObject.applicationStatus = STATUS_ACTIVE;
                me.baseGrid.updateRow(selectedObject, updatedObject);
            });
        }
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
                    <div class="body">{messageInfo}</div></div>, 'rejectApplication', undefined, function (returnObject, selectedObject) {
                let updatedObject = Object.assign({}, selectedObject);
                updatedObject.applicationStatus = STATUS_ACTIVE;
                me.baseGrid.updateRow(selectedObject, updatedObject);
            });
        }
    }

    onCancelInterview(btn) {
        let me = this;
        let selection = me.state.selection;
        if (selection.length > 0) {
            me.onExecuteOperation(btn, {
                id: selection[0].interviewId
            },
                <div class="modal57">
                    <div class="header deactivate"><h2>Cancel</h2></div>
                    <div class="body">
                        Are you sure to cancel the scheduled interview?</div></div>,
                'rejectInterview', undefined, function (returnObject, selectedObject) {
                let updatedObject = Object.assign({}, selectedObject);
                updatedObject.applicationStatus = STATUS_NEW;
                me.baseGrid.updateRow(selectedObject, updatedObject);
            });
        }
    }

    onInterviewDate(btn) {
        let me = this;
        let selection = me.state.selection;
        if (selection.length > 0) {
            let selectedObject = selection[0];

            let fields = Remote.getFieldsByModel(me.props.metaInfo, "ScheduledJobInterview");

            getDataPromise('interview', selectedObject.interviewId, fields).then(function (interview) {
                let actions = {
                    save: function (editor, newManagedObject, successfulCallBack) {
                        saveDataPromise({
                            queryName: 'acceptInterview',
                            showLoader: true
                        }, {optionId: newManagedObject.optionId}).then(function (result) {
                            let updatedObject = Object.assign({}, selectedObject);
                            updatedObject.applicationStatus = STATUS_SCHEDULED;
                            me.baseGrid.updateRow(selectedObject, updatedObject);
                            editor.close();
                        });
                    }
                }

                let options = [];
                interview.options.forEach(function (option) {
                    options.push({code: option.id, name: 'Accept'});
                });


                let managedObject = {
                    posting: selection[0],
                    interview: interview,
                    interviewType: interview.working ? INTERVIEW_TYPE_WORKING : INTERVIEW_TYPE_PERSONAL,
                    options: options
                };

                UiView.showDialog(<Provider store={UiView.createDialogStore()}><PermanentSelectInterviewDate
                    managedObject={managedObject}
                    references={me.props.references}
                    metaInfo={me.props.metaInfo} actions={actions}/></Provider>);
            });
        }
    }

    onAcceptOffer(btn) {
        let me = this;
        let selection = me.state.selection;
        if (selection.length > 0) {
            me.onExecuteOperation(btn, {
                applicationId: selection[0].applicationId
            },
                <div class="modal57">
                    <div class="header activate"><h2>Accept offer</h2></div>
                    <div class="body">
                Please be informed that { selection[0].practiceName}, { selection[0].practiceLocationName} would like to hire you as { selection[0].name}.
 
Offer should indicate permanent job offer dates (for example, Monday, Tuesday, Wednesday). Please note that if the office asks you to work Thursday or Friday, Mayday Dental Staffing has to know about it and it has to be put in through a system as a temporary assignment. If the office want to hire you for Thursday or Friday permanently, Mayday Dental Staffing has to know about it and it has to be put through a system as a permanent assignment. 

You are obligated to notify Mayday Dental Staffing immediately of such extensions on days weather temporary or permanently by calling +1 (888) 899-4386
                    </div></div>, 'acceptApplication', undefined, function (returnObject, selectedObject) {
                let updatedObject = Object.assign({}, selectedObject);
                updatedObject.applicationStatus = STATUS_ACCEPTED;
                me.baseGrid.updateRow(selectedObject, updatedObject);
            });
        }
    }

    prepareManagedObject(loadedObject, target) {
        loadedObject['workSchedules'] = loadedObject['workSchedules'] || [];
        let workSchedules = loadedObject['workSchedules'];


        DAYS_OF_WEEK.forEach(function (day, index) {
            let row = workSchedules.find(function (v) {
                return v.weekDay === day.code
            });
            if (!row) {
                workSchedules.splice(index, 0, {
                    weekDay: day.code,
                    _enabled: false,
                    startTime: '',
                    endTime: ''
                });
            } else {
                row._enabled = true;
            }
        });



        loadedObject['workSchedules'] = loadedObject['workSchedules'].sort(function (item1, item2) {
            let index1 = DAYS_OF_WEEK.findIndex(day => day.code === item1.weekDay);
            let index2 = DAYS_OF_WEEK.findIndex(day => day.code === item2.weekDay);
            if (index1<index2) {
                return -1;
            }
            if (index1>index2) {
                return 1;
            }
            return 0;
        });


        return loadedObject;
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

const PermanentJobConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseViewStateMap(state, ownProps), {
            userId: state.context.currentUser ? state.context.currentUser.id : undefined
        });
    },
    baseViewDispatcherMap)(PermanentJob);

export default PermanentJobConnector;