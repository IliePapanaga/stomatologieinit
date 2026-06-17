/*eslint-disable no-unused-vars*/
import React from 'react';
/*eslint-enable no-unused-vars*/
import {connect} from 'react-redux';

import BaseView, {baseViewDispatcherMap, baseViewStateMap} from '../../../common/BaseView';
import {
    BUTTON_TYPE_ADD,
    BUTTON_TYPE_DELETE,
    BUTTON_TYPE_EDIT,
    CANDIDATE_ANY,
    DEFAULT_CELL_STYLE,
    FILTER_TYPE_DATE_RANGE,
    FILTER_TYPE_SELECT,
    LANGUAGE_ENGLISH,
    NUMBER_CELL_STYLE,
    POSTING_TYPE_COMPLEX, POSTING_TYPE_WEEKLY,
    SELECTION_MODE_SINGLE,
    STATUS_ACTIVE,
    STATUS_FILLED,
    STATUS_PARTIALLY_FILLED
} from '../../../../../utils/Constants';

import AddPosting, {dialogInfo as addDialogInfo} from '../../../dialogs/postings/AddPosting';
import EditPosting, {dialogInfo as editDialogInfo} from '../../../dialogs/postings/EditPosting';
import ViewApplicants, {dialogViewApplicants} from '../../../dialogs/postings/ViewApplicants';
import Renderer from '../../../../../utils/Renderer';
import {TEMPORARY_POSTING} from '../../../../../data/Statuses';
import Remote from "../../../../../utils/Remote";
import {getDataPromise} from "../../../../../actions/common/getData";
import {PublishSimpleTemporaryJobPostingInput} from "../../../../../models/postings/PublishSimpleTemporaryJobPostingInput";
import {PublishWeeklyTemporaryJobPostingInput} from "../../../../../models/postings/PublishWeeklyTemporaryJobPostingInput";
import {PublishComplexTemporaryJobPostingInput} from "../../../../../models/postings/PublishComplexTemporaryJobPostingInput";
import {SimpleTemporaryJobPostingInput} from "../../../../../models/postings/SimpleTemporaryJobPostingInput";
import {WeeklyTemporaryJobPostingInput} from "../../../../../models/postings/WeeklyTemporaryJobPostingInput";
import {ComplexTemporaryJobPostingInput} from "../../../../../models/postings/ComplexTemporaryJobPostingInput";
import {serverShortDateFormat} from "../../../../../utils/DateHelper";
import moment from "moment/moment";
import {DAYS_OF_WEEK} from "../../../../../data/DaysOfWeek";
import {toastr} from "react-redux-toastr";

export class TemporaryPostingJob extends BaseView {
    practice: undefined;
    recordLabel = "posting";

    async componentDidMount() {
        let me = this,
            id = me.props.userId,
            fields = Remote.getFieldsByModel(me.props.metaInfo, "PracticeModel", [], false);
        let data = await getDataPromise('practice', id, fields);

        me.practice = data;

    }

    generateAddQueryName(managedObject) {
        if (managedObject.jobPosting instanceof PublishSimpleTemporaryJobPostingInput) {
            return "publishSimpleTemporary";
        } else if (managedObject.jobPosting instanceof PublishComplexTemporaryJobPostingInput) {
            return "publishComplexTemporary";
        } else if (managedObject.jobPosting instanceof PublishWeeklyTemporaryJobPostingInput) {
            return "publishWeeklyTemporary";
        }
    }

    generateUpdateQueryName(managedObject) {
        if (!managedObject.jobPosting.id) {
            return this.addQueryName(managedObject);
        }
        if (managedObject.jobPosting instanceof SimpleTemporaryJobPostingInput) {
            return "updateToSimpleTemporary";
        } else if (managedObject.jobPosting instanceof ComplexTemporaryJobPostingInput) {
            return "updateToComplexTemporary";
        } else if (managedObject.jobPosting instanceof WeeklyTemporaryJobPostingInput) {
            return "updateToWeeklyTemporary";
        }
    }

    initView(props) {
        let me = this;
        let configuration = {
            additionalFields: ['startTime', 'endTime'],
            requestInfo: {
                fetchQueryName: 'practiceOwnerTemporaryJobPostings',
                getQueryName: "jobPosting",
                addQueryName: me.generateAddQueryName,
                updateQueryName: me.generateUpdateQueryName,
                deleteQueryName: "cancelJobPosting",
                getResponseModel: "JobPosting",
                updateResponseModel: undefined,
                reloadAfterClose: true,
            },
            columns: [
                {
                    dataIndex: 'id', name: 'id', title: 'Id', hidden: true, cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'name',
                    name: 'name',
                    title: 'Posting Title',
                    orderName: 'NAME',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getCertificateTypeTitle()
                },
                {
                    dataIndex: 'status',
                    name: 'status',
                    title: 'Status',
                    orderName: 'STATUS',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getStatusRenderer(TEMPORARY_POSTING.practice)
                },
                {
                    dataIndex: 'startDate',
                    name: 'startDateTime',
                    title: 'Start Date',
                    orderName: 'START_DATE',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getDatePostingRenderer
                },
                {
                    dataIndex: 'endDate',
                    name: 'endDateTime',
                    title: 'End Date',
                    orderName: 'END_DATE',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getDatePostingRenderer
                },
                {
                    dataIndex: 'practiceLocationName',
                    name: 'practiceLocationName',
                    title: 'Location',
                    orderName: 'PRACTICE_LOCATION_NAME',
                    cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'applicants', name: 'applicants', title: 'Applicants', cellClass: `${NUMBER_CELL_STYLE} text__red`
                },
                {
                    dataIndex: 'postedDate',
                    name: 'postedDate',
                    title: 'Posted Date',
                    orderName: 'POSTED_DATE',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getDateRenderer
                }
            ],
            actions: [
                {label: "Add", type: BUTTON_TYPE_ADD, dialog: AddPosting, dialogInfo: addDialogInfo},
                {label: "Edit", type: BUTTON_TYPE_EDIT, dialog: EditPosting, dialogInfo: editDialogInfo},
                {
                    label: "Clone",
                    type: BUTTON_TYPE_EDIT,
                    dialog: EditPosting,
                    dialogInfo: editDialogInfo,
                    name: "clone"
                },
                {label: "Delete", type: BUTTON_TYPE_DELETE, dialogInfo: editDialogInfo},
                {
                    label: "View Applicants",
                    type: BUTTON_TYPE_EDIT,
                    //onClick: me.onViewApplicants,
                    allowedValues: [STATUS_ACTIVE, STATUS_PARTIALLY_FILLED, STATUS_FILLED],
                    allowedByFieldName: "status",
                    dialog: ViewApplicants,
                    dialogInfo: dialogViewApplicants,
                    name: "viewApplicants"
                },
                {
                    label: "View Posting",
                    type: BUTTON_TYPE_EDIT,
                    dialog: EditPosting,
                    dialogInfo: editDialogInfo,
                    name: "viewPosting"
                }
            ],
            filters: [
                {
                    type: FILTER_TYPE_SELECT,
                    name: "status",
                    menuItems: TEMPORARY_POSTING.practice,
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
                    allowedFuture: true
                }
            ],

            selectionMode: SELECTION_MODE_SINGLE
        }
        return configuration;
    }

    prepareManagedObject(loadedObject, target) {
        let me = this;

        if (!loadedObject.id) {
            loadedObject = {
                category: me.props.references.categories[0].id,
                requiredLanguages: [LANGUAGE_ENGLISH]
            }
        } else {
            loadedObject['category'] = me.props.references.categories.find(function (category) {
                return category.subCategories.findIndex(function (subCategory) {
                    return subCategory.id === loadedObject.requiredSubcategories[0]
                }) >= 0
            }).id;
            loadedObject['postingType'] = loadedObject.jobDayStrategy;
            loadedObject['candidate'] = CANDIDATE_ANY;
            if (loadedObject.jobDayStrategy === POSTING_TYPE_COMPLEX) {
                loadedObject['jobDays'] = loadedObject['jobDays'].sort(function (item1, item2) {
                    let date1 = moment(item1.date, serverShortDateFormat);
                    let date2 = moment(item2.date, serverShortDateFormat);
                    if (date1.isBefore(date2)) {
                        return -1;
                    }
                    if (date1.isAfter(date2)) {
                        return 1;
                    }
                    return 0;
                });
            }
            if (loadedObject.jobDayStrategy === POSTING_TYPE_WEEKLY) {
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
            }
        }


        loadedObject['practice'] = me.practice;

        if (target.getAttribute('action-name') === 'clone') {
            delete loadedObject['id'];
        }

        return loadedObject;
    }

    onClone() {
        alert("Clone");
    }

    isEnabledAction(element, selection) {
        let result = super.isEnabledAction(element, selection);
        if (result && element.name === "viewApplicants") {
            result = selection && selection.length === 1 && selection[0].applicants > 0
        }
        return result;
    }

    dialogProps(dlg, button) {
        switch (button.getAttribute('action-name')) {
            case 'viewPosting':
                return {readOnly: true};
            default:
                return null;
        }
    }

    onDeleteObject(dialogInfo, e) {
        let me = this;
        let selection = me.state.selection;

        if (selection.length > 0 &&  [STATUS_FILLED, STATUS_PARTIALLY_FILLED].includes(selection[0].status)) {
            toastr.warning('DELETE', 'It is not possible to delete a posting until you cancel the job offer for the professional. To do so, please go to "View Applicants" and scroll to cancel a professional');
            return false;
        }

        super.onDeleteObject(dialogInfo, e);

    }
}

const TemporaryJob = TemporaryPostingJob;

const TemporaryJobConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseViewStateMap(state, ownProps), {
            userId: state.context.currentUser ? state.context.currentUser.id : undefined,
            metaInfo: state.context.metaInfo
        });
    },
    baseViewDispatcherMap)(TemporaryJob);

export default TemporaryJobConnector;