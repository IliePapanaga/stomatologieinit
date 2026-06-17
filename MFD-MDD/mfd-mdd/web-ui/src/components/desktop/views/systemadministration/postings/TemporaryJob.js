import React from 'react';
import {connect} from 'react-redux';

import {baseViewDispatcherMap, baseViewStateMap} from '../../../common/BaseView';
import {
    BUTTON_TYPE_EDIT,
    CANDIDATE_ANY,
    DEFAULT_CELL_STYLE,
    FILTER_TYPE_DATE,
    FILTER_TYPE_LOCATION,
    FILTER_TYPE_SELECT,
    NUMBER_CELL_STYLE,
    POSTING_TYPE_COMPLEX,
    POSTING_TYPE_WEEKLY,
    SELECTION_MODE_SINGLE,
    STATUS_ACTIVE,
    STATUS_CANCELED,
    STATUS_FILLED,
    STATUS_PARTIALLY_FILLED,
    STATUS_REJECTED,
    SUBCATEGORY_ORDERS
} from '../../../../../utils/Constants';
import FindByLocation from '../../common/FindByLocation';
import {SimpleTemporaryJobPostingInput} from "../../../../../models/postings/SimpleTemporaryJobPostingInput";
import {WeeklyTemporaryJobPostingInput} from "../../../../../models/postings/WeeklyTemporaryJobPostingInput";
import {ComplexTemporaryJobPostingInput} from "../../../../../models/postings/ComplexTemporaryJobPostingInput";
import Renderer from '../../../../../utils/Renderer';
import {serverShortDateFormat} from '../../../../../utils/DateHelper';
import {TEMPORARY_POSTING} from '../../../../../data/Statuses';
import {DAYS_OF_WEEK} from '../../../../../data/DaysOfWeek';
import UiView from '../../../../../utils/UiView';
import ViewTemporaryPosting, {dialogInfo as editDialogInfo} from "../../../dialogs/postings/ViewTemporaryPosting";
import moment from "moment/moment";
import {getDataPromise} from "../../../../../actions/common/getData";
import Remote from "../../../../../utils/Remote";
import ViewApplicants, {dialogViewApplicants} from "../../../dialogs/postings/ViewApplicants";

class TemporaryJob extends FindByLocation {
    recordLabel = "posting";

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
                fetchQueryName: 'systemUserTemporaryJobPostings',
                getQueryName: "jobPosting",
                //addQueryName: undefined,
                updateQueryName: me.generateUpdateQueryName,
                deleteQueryName: "deleteJobPosting",
                getResponseModel: "JobPosting",
                //addResponseModel: "PracticeLocationModel",
                updateResponseModel: undefined
            },
            columns: [
                {
                    dataIndex: 'id', name: 'id', title: 'Id', hidden: true, cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'name',
                    name: 'name',
                    title: 'Posting',
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
                    renderer: Renderer.getStatusRenderer(TEMPORARY_POSTING.system)
                },
                {
                    dataIndex: 'practiceOwnerLastName',
                    name: 'practiceOwnerLastName',
                    title: 'Client Last Name',
                    orderName: 'PRACTICE_OWNER_LAST_NAME',
                    cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'practiceOwnerFirstName',
                    name: 'practiceOwnerFirstName',
                    title: 'Client First Name',
                    orderName: 'PRACTICE_OWNER_FIRST_NAME',
                    cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'practiceName',
                    name: 'practiceName',
                    title: 'Office',
                    orderName: 'PRACTICE_NAME',
                    cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'practiceLocationName',
                    name: 'practiceLocationName',
                    title: 'Location',
                    orderName: 'PRACTICE_LOCATION_NAME',
                    cellClass: DEFAULT_CELL_STYLE
                },
                // {
                //     dataIndex: 'phone', name: 'phone', title: 'Region', orderName: 'PHONE', cellClass: DEFAULT_CELL_STYLE
                // },
                {
                    dataIndex: 'startDate',
                    name: 'startDate',
                    title: 'Start Date',
                    orderName: 'START_DATE',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getDatePostingRenderer
                },
                {
                    dataIndex: 'endDate',
                    name: 'endDate',
                    title: 'End Date',
                    orderName: 'END_DATE',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getDatePostingRenderer
                },
                {
                    dataIndex: 'applicants',
                    name: 'applicants',
                    orderName: 'APPLICANTS',
                    title: 'Applicants',
                    cellClass: NUMBER_CELL_STYLE
                },
                {
                    dataIndex: 'postedDate',
                    name: 'postedDate',
                    title: 'Posted Day',
                    orderName: 'POSTED_DATE',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getDateRenderer
                }
            ],
            actions: [
                {
                    label: "View Posting",
                    type: BUTTON_TYPE_EDIT,
                    dialog: ViewTemporaryPosting,
                    dialogInfo: editDialogInfo
                },
                {
                    label: "View Applicants",
                    type: BUTTON_TYPE_EDIT,
                    //onClick: me.onViewApplicants,
                    dialog: ViewApplicants,
                    dialogInfo: dialogViewApplicants,
                    name: "viewApplicants"
                },
                /*       {label: "Delete", type: BUTTON_TYPE_DELETE},*/
                {
                    label: "Dismiss SOS",
                    onClick: me.onDismissSOS,
                    hasSelectedRows: 1,
                    disallowedValues: [STATUS_REJECTED, STATUS_ACTIVE, STATUS_FILLED, STATUS_PARTIALLY_FILLED, STATUS_CANCELED],
                    disallowedByFieldName: "status",
                    name: "dismissSos"
                }
            ],
            selectionMode: SELECTION_MODE_SINGLE
        }
        return configuration;
    }

    onLoad() {
        let me = this;
        me.subCategories = me.subCategories || [];

        me.props.onLoadRefs(['categories'], me.props.references, me.props.metaInfo, function (result) {
            try {
                me.props.references.categories.forEach(function (category) {
                    let cats = category.subCategories.sort(function (a, b) {
                        let value1 = SUBCATEGORY_ORDERS[category.id].indexOf(a.id);
                        let value2 = SUBCATEGORY_ORDERS[category.id].indexOf(b.id);

                        if (value2 > value1) {
                            return -1;
                        }
                        if (value1 > value2) {
                            return 1;
                        }
                        return 0;
                    });
                    me.subCategories.push({group: category.name, options: cats});
                });

                me.filters = [
                    {
                        type: FILTER_TYPE_SELECT,
                        name: "specialties",
                        menuItems: me.subCategories,
                        multiple: false,
                        required: false,
                        title: 'Specialty',
                        grouped: true
                    },
                    {
                        type: FILTER_TYPE_SELECT,
                        name: "status",
                        menuItems: TEMPORARY_POSTING.system,
                        multiple: false,
                        required: false,
                        title: 'Status'
                    },
                    {
                        type: FILTER_TYPE_DATE,
                        placeholder: "Start Date",
                        //minDate: DateHelper.getCurrentDate(),
                        name: "startDate",
                        onlyDate: true
                    },
                    {type: FILTER_TYPE_LOCATION}
                ];
                me.initBaseFilters();
                me.setInitialized(true);
            } catch (ex) {
            }
        });
    }

    onDismissSOS(btn) {
        let me = this;

        let selection = me.state.selection;
        if (selection.length > 0) {
            me.onExecuteOperation(btn, {
                    attendance: {jobPostingId: selection[0].id}
                },
                < div class="modal57">
                    < div class="header">< h2> DismissSOS </h2></div>
                    < div class="body"> Are you sure you want to dismiss
                        SOS {selection[0].practiceName} -{selection[0].practiceOwnerFirstName}, {selection[0].practiceOwnerLastName} ?
                    </div>
                </div>, 'dismissSos', undefined, function (returnObject, selectedObject) {
                    // let updatedObject = Object.assign({}, selectedObject);
                    // updatedObject.blackListed = !selectedObject.blackListed;
                    // me.baseGrid.updateRow(selectedObject, updatedObject);
                }
            )
            ;
        }
    }

    prepareManagedObject(loadedObject, target) {
        let me = this;
        //let fields = Remote.getFieldsByModel(me.props.metaInfo, "PracticeLocationModel", [], false);
        //let data = await getDataPromise('practiceLocation', loadedObject.practiceLocationId, fields);

        //loadedObject['practice'] = {locations:[{id:loadedObject.practiceLocationId}]};

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


        if (target.getAttribute('action-name') === 'clone') {
            delete loadedObject['id'];
        }

        return loadedObject;
    }

    showDialog(me, Dialog, Provider, target, object, actions) {
        let fields = Remote.getFieldsByModel(me.props.metaInfo, "PracticeLocationModel");

        getDataPromise('practiceLocation', object.practiceLocationId, fields).then(function (practiceLocation) {
            object.practice = {
                locations: [{
                    name: practiceLocation.name,
                    id: practiceLocation.id
                }]
            };
            UiView.showDialog(< Provider
                    store={UiView.createDialogStore()
                    }><
                    Dialog
                    {...
                        me.dialogProps(Dialog, target)
                    }
                    managedObject={me.prepareManagedObject(object, target)
                    }
                    actions={actions}
                    references={me.props.references
                    }
                    metaInfo={me.props.metaInfo
                    }
                /></
                    Provider>
            )
            ;
        });
    }

    dialogProps(dlg, button) {
        switch (button.getAttribute('action-name')) {
            case 'viewApplicants':
                return {readOnly: true};
            default:
                return null;
        }
    }

    isEnabledAction(element, selection) {
        let result = super.isEnabledAction(element, selection);
        if (result && (element.name === "viewApplicants" || element.name === "dismissSos")) {
            result = selection && selection.length === 1 && selection[0].applicants > 0
        }
        return result;
    }
}

const TemporaryJobConnector = connect(
    baseViewStateMap,
    baseViewDispatcherMap)(TemporaryJob);

export default TemporaryJobConnector;