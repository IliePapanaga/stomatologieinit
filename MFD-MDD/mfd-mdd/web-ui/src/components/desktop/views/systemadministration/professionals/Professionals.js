import React from 'react';
import {connect} from 'react-redux';
import {Enum} from 'enumify';
import {baseViewDispatcherMap, baseViewStateMap} from '../../../common/BaseView';
import {
    BUTTON_TYPE_EDIT,
    BUTTON_TYPE_EDIT_ON_SELECTION,
    DEFAULT_CELL_STYLE,
    EVENT_AUTH_IMPERSONATE,
    FILTER_TYPE_DATE_RANGE,
    FILTER_TYPE_LOCATION,
    FILTER_TYPE_SEARCH,
    FILTER_TYPE_SELECT,
    NUMBER_CELL_STYLE,
    SELECTION_MODE_SINGLE,
    STATUS_ACTIVE,
    STATUS_INACTIVE,
    SUBCATEGORY_ORDERS
} from '../../../../../utils/Constants';
import FindByLocation from '../../common/FindByLocation';
import AddEditProfessional, {dialogInfo} from '../../../dialogs/professionals/AddEditProfessional';
import ReviewsForProfessional, {dialogReviews} from '../../../dialogs/professionals/ReviewsForProfessional';
import Renderer from '../../../../../utils/Renderer';
import {PROFESSIONAL} from '../../../../../data/Statuses';
import {NEWCLIENTS, PROBLEMATIC} from '../../../../../data/Filters';
import DateHelper from '../../../../../utils/DateHelper';
import ObjectHelper from '../../../../../utils/Object';
import {impersonate} from "../../../../../actions/common/authorization";


class Professionals extends FindByLocation {
    subCategories = [];

    initView(props) {
        let me = this;
        let configuration = {
            requestInfo: {
                fetchQueryName: 'systemUserProfessionals',
                getQueryName: ["professional", "professionalSubcategoriesByProfessionalId", "professionalRequiredCertificatesByProfessionalId", "blackListedLocationDetails", "blackListedProfessionalDetails", "professionalNoShows", "professionalRejections"],
                //addQueryName: "addPracticeLocation",
                updateQueryName: "updateProfessionalGeneral",
                //deleteQueryName: "deletePracticeLocation",
                getResponseModel: ["ProfessionalModel", "ProfessionalSubcategoryModelConnection", "RequiredCertificateConnection", "BlackListedLocationDetailsConnection", "BlackListedProfessionalDetailsConnection", "NoShowModelConnection", "RejectionModelConnection"],
                //addResponseModel: "PracticeLocationModel",
                updateResponseModel: undefined,
                getQueryParameterName: ["id", "professionalId", "professionalId", "professionalId", "professionalId", "professionalId", "professionalId"],
            },

            additionalFields: ['approvedByLastName', 'rating', 'totalFeedback'],
            columns: [
                {
                    dataIndex: 'id', name: 'id', title: 'Id', hidden: true, cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'lastName',
                    name: 'lastName',
                    title: 'Last Name',
                    orderName: 'LAST_NAME',
                    cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'firstName',
                    name: 'firstName',
                    title: 'First Name',
                    orderName: 'FIRST_NAME',
                    cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'speciality',
                    name: 'speciality',
                    title: 'Position',
                    orderName: 'SPECIALITY',
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
                    dataIndex: 'documentStatus',
                    name: 'documentStatus',
                    title: 'Document Status',
                    orderName: 'DOCUMENT_STATUS',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getStatusRenderer()
                },
                // {
                //     dataIndex: 'officeName', name: 'officeName', t   itle: 'Region', orderName: 'OFFICE_NAME', cellClass: DEFAULT_CELL_STYLE
                // },
                {
                    dataIndex: 'phone',
                    name: 'phone',
                    title: 'Telephone #',
                    // orderName: 'PHONE', // disabling of sorting by phone number
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getPhoneRenderer
                },
                {
                    dataIndex: 'rph', name: 'rph', title: 'Rate($)', orderName: 'RPH', cellClass: NUMBER_CELL_STYLE
                },
                {
                    dataIndex: 'rating',
                    name: 'rating',
                    title: 'Rating',
                    orderName: 'RATING',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getRatingRenderer
                },
                {
                    dataIndex: 'lastEmploymentStartDate',
                    name: 'lastEmploymentStartDate',
                    orderName: 'LAST_EMPLOYMENT_START_DATE',
                    title: 'Last Employment Start Date',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getDateRenderer
                },
                {
                    dataIndex: 'lastActivity',
                    name: 'lastActivity',
                    title: 'Last Activity Date',
                    orderName: 'LAST_ACTIVITY',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getDateRenderer
                },
                {
                    dataIndex: 'noShow',
                    name: 'noShow',
                    title: 'No Show',
                    orderName: 'NO_SHOW',
                    cellClass: NUMBER_CELL_STYLE
                },
                {
                    dataIndex: 'cancellations',
                    name: 'cancellations',
                    title: '#Cancellations',
                    orderName: 'CANCELLATIONS',
                    cellClass: NUMBER_CELL_STYLE
                },
                {
                    dataIndex: 'approvedByFirstName',
                    name: 'approvedByFirstName',
                    title: 'Approved By',
                    orderName: 'APPROVED_BY_FIRST_NAME',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: function (value, cellClass, row, columnIndex, rowIndex) {
                        value = value || '';
                        let lastName = row['approvedByLastName'] || '';
                        return <div><span class={cellClass}>{`${value} ${lastName}`}</span></div>;
                    }
                },
                {
                    dataIndex: 'modifiedDate',
                    name: 'modifiedDate',
                    title: 'Modified Date',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getDateRenderer
                }
            ],
            actions: [
                {
                    label: "Edit",
                    type: BUTTON_TYPE_EDIT,
                    dialog: AddEditProfessional,
                    dialogInfo: dialogInfo,
                    name: "editProfessional"
                },
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
                {
                    label: "Review Docs",
                    type: BUTTON_TYPE_EDIT,
                    dialog: AddEditProfessional,
                    dialogInfo: dialogInfo,
                    name: "reviewDocs"
                },
                {
                    label: "See Reviews",
                    type: BUTTON_TYPE_EDIT_ON_SELECTION,
                    dialog: ReviewsForProfessional,
                    dialogInfo: dialogReviews
                },
                /*   { label: "Modify No Snow",type: BUTTON_TYPE_EDIT,  dialog: ModifyNoShowForProfessional, dialogInfo: dialogModify }*/
            ],
            selectionMode: SELECTION_MODE_SINGLE,
            params: {nameStartsWith: undefined}
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
                        title: 'Position',
                        grouped: true
                    },
                    {
                        type: FILTER_TYPE_SELECT,
                        name: "problematic",
                        menuItems: PROBLEMATIC,
                        multiple: false,
                        required: false,
                        title: 'Problematic'
                    },
                    {
                        type: FILTER_TYPE_SELECT,
                        name: "newclients",
                        menuItems: NEWCLIENTS,
                        multiple: false,
                        required: false,
                        title: 'New Comers'
                    },
                    {
                        type: FILTER_TYPE_SELECT,
                        name: "status",
                        menuItems: PROFESSIONAL,
                        multiple: false,
                        required: false,
                        title: 'Status'
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
                ];
                me.initBaseFilters();
                me.setInitialized(true);
            }catch(ex){}
        });
    }

    dialogProps(dlg, button) {
        let me=this;

        switch (button.getAttribute('action-name')) {
            case 'reviewDocs':
                return {anchor: 'profile__main-content-cert', updateStatus: me.updateStatus.bind(me)};
            case 'editProfessional':
                return {updateStatus: me.updateStatus.bind(me)}
            default:
                return null;
        }
    }

    updateStatus(status){
        let me=this;
        let selection = me.state.selection;
        if (selection.length > 0) {
            let updatedObject = ObjectHelper.copyObject(selection[0]);
            updatedObject.documentStatus = status;
            me.baseGrid.updateRow(selection[0], updatedObject);
        }
    }


    prepareUpdatedObjectBeforeDisplay(oldObject, forUpdateObject, newObject) {
        return {
            firstName: forUpdateObject.professional.contact.name.first,
            lastName: forUpdateObject.professional.contact.name.last,
            phone: forUpdateObject.professional.contact.phone,
            rph: forUpdateObject.jobPreference.desiredRatePerHour
        };
    }

    prepareManagedObject(loadedObject) {
        let result = loadedObject.professional;
        if (result) {
            result.profile = result.profile || {workReferences: [], workExperiences: []};
            result.subcategories = loadedObject.professionalSubcategoriesByProfessionalId.nodes;
            result.certificates = loadedObject.professionalRequiredCertificatesByProfessionalId.nodes;
            result.blackListedLocationDetails = loadedObject.blackListedLocationDetails.nodes;
            result.blackListedProfessionalDetails = loadedObject.blackListedProfessionalDetails.nodes;
            result.professionalNoShows = loadedObject.professionalNoShows.nodes;
            result.professionalRejections = loadedObject.professionalRejections.nodes;
            return result;
        } else {
            return loadedObject
        }
    }

    onActivate(btn) {
        let me = this;
        me.activateDeactivate(btn, true);
    }

    onDeactivate(btn) {
        let me = this;
        me.activateDeactivate(btn, false);
    }

    onModifyNoSnow() {
        alert("Modify No Snow");
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
                    <div class="body">Are you sure you want to {subStr} {selection[0].firstName}, {selection[0].lastName} account?</div></div>,
                'activateDeactivateProfessional', 'UserActivateDeactivateResult', function (returnObject, selectedObject) {
                let updatedObject = Object.assign({}, selectedObject);
                updatedObject.status = returnObject.status;
                me.baseGrid.updateRow(selectedObject, updatedObject);
            });
        }
    }

    convertFilterDataBeforeFiltering(data) {
        let returnData = {};

        Object.assign(returnData, data);

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
            DateHelper.initNewClientsFiltersData(newclients, returnData, 'newComersTo', 'newComersFrom');
        }
        if (returnData.problematic) {
            returnData.problematic = new Enum({name: returnData.problematic});
        }

        return returnData;

    }

    showDialog(scope, Dialog, Provider, target, object, actions) {
        let me = this;
        if (Dialog) {
            super.showDialog(scope, Dialog, Provider, target, object, actions);
        } else {
            me.props.onImpersonate(object.professional.contact.email);
        }
    }


}

const ProfessionalsConnector = connect(
    baseViewStateMap,
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
    })(Professionals);

export default ProfessionalsConnector;