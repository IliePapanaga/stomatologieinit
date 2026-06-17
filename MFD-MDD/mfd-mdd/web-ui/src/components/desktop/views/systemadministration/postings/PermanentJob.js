import React from 'react';
import {connect} from 'react-redux';

import {baseViewDispatcherMap, baseViewStateMap} from '../../../common/BaseView';
import {
    BUTTON_TYPE_EDIT,
    DEFAULT_CELL_STYLE,
    FILTER_TYPE_DATE,
    FILTER_TYPE_LOCATION,
    FILTER_TYPE_SELECT,
    NUMBER_CELL_STYLE,
    SELECTION_MODE_SINGLE,
    SUBCATEGORY_ORDERS
} from '../../../../../utils/Constants';
import FindByLocation from '../../common/FindByLocation';

import ViewPermanentPosting, {dialogInfo} from '../../../dialogs/postings/ViewPermanentPosting';
import Renderer from '../../../../../utils/Renderer';
import {PERMANENT_POSTING} from '../../../../../data/Statuses';
import UiView from '../../../../../utils/UiView';
import Remote from "../../../../../utils/Remote";
import {getDataPromise} from "../../../../../actions/common/getData";
import {DAYS_OF_WEEK} from "../../../../../data/DaysOfWeek";
import ViewPermanentApplicants, {dialogInfo as dialogViewApplicants} from "../../../dialogs/postings/ViewPermanentApplicants";

class PermanentJob extends FindByLocation {
    professional: undefined;
    recordLabel = "posting";

    generateUpdateQueryName(managedObject) {
        if (!managedObject.jobPosting.id) {
            return this.addQueryName(managedObject);
        }
        return "updateToSimplePermanent";
    }

    async componentDidMount() {
        let me = this,
            id = me.props.userId,
            fields = Remote.getFieldsByModel(me.props.metaInfo, "ProfessionalModel");
        let data = await getDataPromise('professional', id, fields);

        me.professional = data;

    }

    initView(props) {
        let me = this;
        let configuration = {
            requestInfo: {
                fetchQueryName: 'systemUserPermanentJobPostings',
                getQueryName: "jobPosting",
                addQueryName: me.generateAddQueryName,
                updateQueryName: me.generateUpdateQueryName,
                deleteQueryName: "deleteJobPosting",
                getResponseModel: "JobPosting",
                //addResponseModel: "PracticeLocationModel",
                updateResponseModel: undefined
            },

            additionalFields: [],
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
                    renderer: Renderer.getStatusRenderer(PERMANENT_POSTING.system)
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
                    renderer: Renderer.getDateRendererMonthDayYear
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
                {label: "View Posting", type: BUTTON_TYPE_EDIT, dialog: ViewPermanentPosting, dialogInfo: dialogInfo},
                {
                    label: "View Applicants",
                    type: BUTTON_TYPE_EDIT,
                    //onClick: me.onViewApplicants,
                    dialog: ViewPermanentApplicants,
                    dialogInfo: dialogViewApplicants,
                    name: "viewApplicants"
                }
                /*{label: "Delete", type: BUTTON_TYPE_DELETE}*/
            ],
            selectionMode: SELECTION_MODE_SINGLE
        };
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
                        menuItems: PERMANENT_POSTING.system,
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

    prepareManagedObject(loadedObject, target) {
        let me = this;


        loadedObject['category'] = me.props.references.categories.find(function (category) {
            return category.subCategories.findIndex(function (subCategory) {
                return subCategory.id === loadedObject.requiredSubcategories[0]
            }) >= 0
        }).id;


        loadedObject['workSchedules'] = loadedObject['workSchedules'] || [];

        loadedObject['workSchedules'] = loadedObject['workSchedules'].sort(function (item1, item2) {
            let index1 = DAYS_OF_WEEK.findIndex(day => day.code === item1.weekDay);
            let index2 = DAYS_OF_WEEK.findIndex(day => day.code === item2.weekDay);
            if (index1 < index2) {
                return -1;
            }
            if (index1 > index2) {
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

        return loadedObject;
    }

    onDismissSOS() {
        alert("Dismiss SOS");
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
            UiView.showDialog(<Provider
                store={UiView.createDialogStore()}><Dialog {...me.dialogProps(Dialog, target)}
                                                           managedObject={me.prepareManagedObject(object, target)}
                                                           actions={actions}
                                                           references={me.props.references}
                                                           metaInfo={me.props.metaInfo}/></Provider>);
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
        if (result && element.name === "viewApplicants") {
            result = selection && selection.length === 1 && selection[0].applicants > 0
        }
        return result;
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