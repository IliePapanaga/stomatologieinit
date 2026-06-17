/*eslint-disable no-unused-vars*/
import React from 'react';
/*eslint-enable no-unused-vars*/
import {connect} from 'react-redux';

import BaseView, {baseViewDispatcherMap, baseViewStateMap} from '../../../common/BaseView';
import {
    BUTTON_TYPE_ADD,
    BUTTON_TYPE_DELETE,
    BUTTON_TYPE_EDIT,
    DEFAULT_CELL_STYLE,
    FILTER_TYPE_DATE,
    FILTER_TYPE_SELECT,
    LANGUAGE_ENGLISH,
    NUMBER_CELL_STYLE,
    SELECTION_MODE_SINGLE,
    STATUS_FILLED
} from '../../../../../utils/Constants';

import {DAYS_OF_WEEK} from '../../../../../data/DaysOfWeek'

import AddPermanentPosting, {dialogInfo} from '../../../dialogs/postings/AddPermanentPosting';
import {dialogInfo as editDialogInfo} from '../../../dialogs/postings/EditPosting';
import ViewPermanentPosting, {dialogInfo as editViewPermanentPostingDialogInfo} from '../../../dialogs/postings/ViewPermanentPosting';
import ViewPermanentApplicants, {dialogInfo as viewPermanentApplicantsDialogInfo} from '../../../dialogs/postings/ViewPermanentApplicants';
import Renderer from '../../../../../utils/Renderer';
import {PERMANENT_POSTING} from '../../../../../data/Statuses';
import Remote from "../../../../../utils/Remote";
import {getDataPromise} from "../../../../../actions/common/getData";
import {toastr} from "react-redux-toastr";


export class PermanentPostingJob extends BaseView {

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
        return "publishSimplePermanent";
    }

    generateUpdateQueryName(managedObject) {
        if (!managedObject.jobPosting.id) {
            return this.addQueryName(managedObject);
        }
        return "updateToSimplePermanent";
    }

    initView(props) {
        let me = this;
        let configuration = {
            requestInfo: {
                fetchQueryName: 'practiceOwnerPermanentJobPostings',
                getQueryName: "jobPosting",
                addQueryName: me.generateAddQueryName,
                updateQueryName: me.generateUpdateQueryName,
                deleteQueryName: "cancelJobPosting",
                getResponseModel: "JobPosting",
                updateResponseModel: undefined
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
                    renderer: Renderer.getStatusRenderer(PERMANENT_POSTING.practice)
                },
                {
                    dataIndex: 'startDate',
                    name: 'startDateTime',
                    title: 'Start Date And Time',
                    orderName: 'START_DATE',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getDateRenderer
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
                {label: "Add", type: BUTTON_TYPE_ADD, dialog: AddPermanentPosting, dialogInfo: dialogInfo},
                {label: "Edit", type: BUTTON_TYPE_EDIT, dialog: AddPermanentPosting, dialogInfo: dialogInfo},
                {
                    label: "Clone",
                    type: BUTTON_TYPE_EDIT,
                    dialog: AddPermanentPosting,
                    dialogInfo: dialogInfo,
                    name: "clone"
                },
                {label: "Delete", type: BUTTON_TYPE_DELETE, dialogInfo: editDialogInfo},
                {
                    label: "View Applicants",
                    type: BUTTON_TYPE_EDIT,
                    dialog: ViewPermanentApplicants,
                    dialogInfo: viewPermanentApplicantsDialogInfo,
                    name: "viewApplicants"
                },
                {
                    label: "View Posting",
                    type: BUTTON_TYPE_EDIT,
                    dialog: ViewPermanentPosting,
                    dialogInfo: editViewPermanentPostingDialogInfo,
                    name: "viewPosting"
                },
            ],
            filters: [
                {type: FILTER_TYPE_DATE, name: "startDate", placeholder: "Start Date", onlyDate: true},
                {
                    type: FILTER_TYPE_SELECT,
                    name: "status",
                    menuItems: PERMANENT_POSTING.practice,
                    multiple: false,
                    required: false,
                    title: 'Status'
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
        }

        loadedObject['practice'] = me.practice;
        loadedObject['workSchedules'] = loadedObject['workSchedules'] || [];

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

        if (target.getAttribute('action-name') === 'clone') {
            delete loadedObject['id'];
        }

        if (target.getAttribute('action-name') === 'viewApplicants') {
            if(me.state.selection.length>0){
                loadedObject['status']=me.state.selection[0].status;
            }
        }

        return loadedObject;
    }

    onViewApplicants() {
        alert("View Applicants");
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

        if (selection.length > 0 &&  [STATUS_FILLED].includes(selection[0].status)) {
            toastr.warning('DELETE', 'It is not possible to delete a posting until you cancel the job offer for the professional. To do so, please go to "View Applicants" and scroll to cancel a professional');
            return false;
        }

        super.onDeleteObject(dialogInfo, e);

    }
}


const PermanentJob = PermanentPostingJob;

const PermanentJobConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseViewStateMap(state, ownProps), {
            userId: state.context.currentUser ? state.context.currentUser.id : undefined,
            metaInfo: state.context.metaInfo
        });
    },
    baseViewDispatcherMap)(PermanentJob);

export default PermanentJobConnector;