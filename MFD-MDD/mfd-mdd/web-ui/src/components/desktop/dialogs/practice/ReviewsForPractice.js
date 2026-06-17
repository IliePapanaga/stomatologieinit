import React from 'react';
import {connect, Provider} from 'react-redux';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import {FieldArray, reduxForm} from 'redux-form';
import BaseView, {baseViewDispatcherMap, saveObject} from '../../common/BaseView';
import {
    BUTTON_TYPE_DELETE,
    BUTTON_TYPE_EDIT,
    DEFAULT_CELL_STYLE,
    SELECTION_MODE_SINGLE
} from '../../../../utils/Constants';
import {Logger} from 'react-logger-lib';
import PracticeReviewsEdit, {dialogInfo as dialogPracticeReviewsEdit} from '../reviews/PracticeReviewsEdit';
import Renderer from "../../../../utils/Renderer";
import Stars from "../../common/form/Stars";
import Remote from "../../../../utils/Remote";
import ObjectHelper from "../../../../utils/Object";
import {FieldsInfoByModel} from "../../../../models/core/FieldsInfoByModel";
import Error from "../../../../utils/Error";
import {getMultyDataPromise} from "../../../../actions/common/getData";
import UiView from "../../../../utils/UiView";
import ViewProfessional from '../professionals/ViewProfessional';

export const dialogInfo = {references: ['categories', 'languages', 'educations', 'academicDegrees']}

class ReviewsView extends BaseView {
    initView(props) {
        let me = this;
        let configuration = {
            requestInfo: {
                fetchQueryName: 'locationReviews',
                getQueryName: "locationReview",
                updateQueryName: "updateLocationReview",
                deleteQueryName: "deleteLocationReview",
                getResponseModel: "ProfessionalToLocationReview",
                updateResponseModel: undefined
            },

            additionalFields: ['professionalId'],
            columns: [
                {dataIndex: 'id', name: 'id', title: 'Id', hidden: true, cellClass: DEFAULT_CELL_STYLE},
                {
                    dataIndex: 'jobPostingName',
                    name: 'jobPostingName',
                    title: 'Position',
                    cellClass: DEFAULT_CELL_STYLE,
                    orderName: "JOB_POSTING_NAME"
                },
                {
                    dataIndex: 'professionalLastName',
                    name: 'professionalLastName',
                    title: 'Professional Last Name',
                    cellClass: DEFAULT_CELL_STYLE,
                    orderName: "PROFESSIONAL_LAST_NAME",
                    renderer: function (value, cellClass, row, columnIndex, rowIndex) {
                        let element = me.baseActions.props.actions[0];
                        return <div action-name="viewReview" class={cellClass}>
                            <a class="underline" onClick={me.onView.bind(me, /*element.dialog, element.dialogInfo,*/ row)}>{value}</a>
                        </div>;
                    }
                },
                {
                    dataIndex: 'professionalFirstName',
                    name: 'professionalFirstName',
                    title: 'Professional First Name',
                    cellClass: DEFAULT_CELL_STYLE,
                    orderName: "PROFESSIONAL_FIRST_NAME"
                },

                {
                    dataIndex: 'practiceLocationName',
                    name: 'location',
                    title: 'Location',
                    cellClass: DEFAULT_CELL_STYLE,
                    orderName: "PRACTICE_LOCATION_NAME"
                },
                {
                    dataIndex: 'startDate',
                    name: 'startDate',
                    title: 'Start Date',
                    cellClass: DEFAULT_CELL_STYLE,
                    orderName: "JOB_POSTING_START_DATE",
                    renderer: Renderer.getDateRenderer
                },
                {
                    dataIndex: 'endDate',
                    name: 'endDate',
                    title: 'End Date',
                    cellClass: DEFAULT_CELL_STYLE,
                    orderName: "JOB_POSTING_END_DATE",
                    renderer: Renderer.getDateRenderer
                },

                {
                    dataIndex: 'rate',
                    name: 'rating',
                    title: 'Rating',
                    cellClass: DEFAULT_CELL_STYLE,
                    orderName: "RATE",
                    renderer: Renderer.getRatingRenderer
                },
                {
                    dataIndex: 'wouldWorkPermanently',
                    name: 'wouldWorkPermanently',
                    title: 'Wish to work',
                    cellClass: DEFAULT_CELL_STYLE,
                    orderName: "WOULD_WORK",
                    renderer: Renderer.getRequiredRenderer()
                },

                {
                    dataIndex: 'blackListed',
                    name: 'blackListed',
                    title: 'Blacklisted',
                    cellClass: DEFAULT_CELL_STYLE,
                    orderName: "BLACK_LISTED",
                    renderer: Renderer.getRequiredRenderer()
                },
                {
                    dataIndex: 'comment',
                    name: 'comment',
                    title: 'Comments',
                    cellClass: DEFAULT_CELL_STYLE,
                    orderName: "COMMENT"
                },
                {
                    dataIndex: 'feedbackDate',
                    name: 'feedbackDate',
                    title: 'Feedback Date',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getDateRenderer,
                    orderName: "FEEDBACK_DATE"
                }
            ],
            actions: [
                {
                    label: "Edit review",
                    type: BUTTON_TYPE_EDIT,
                    dialog: PracticeReviewsEdit,
                    dialogInfo: dialogPracticeReviewsEdit
                },
                {label: "Delete review", type: BUTTON_TYPE_DELETE}
            ],
            selectionMode: SELECTION_MODE_SINGLE,
            params: {practiceId: props.managedObject.practice.id}
        }
        return configuration;
    }

    onView(applicant, event) {
        let me = this;
        try {
            let requestFields = [];
            ["ProfessionalModel", "ProfessionalSubcategoryModelConnection", "RequiredCertificateConnection", "Questionnaire"].forEach(getResponseModel => {
                let tmpRequestFields = undefined;
                if (getResponseModel instanceof FieldsInfoByModel) {
                    tmpRequestFields = Remote.getFieldsByModel(me.props.metaInfo, getResponseModel.model, getResponseModel.ignoreFields, getResponseModel.modelMode);
                } else {
                    tmpRequestFields = Remote.getFieldsByModel(me.props.metaInfo, getResponseModel, [], false);
                }
                requestFields.push(tmpRequestFields);
            });

            let category = me.props.references.categories.find(function (category) {
                return category.subCategories.findIndex(function (subCategory) {
                    return subCategory.id === applicant.jobPostingName.substr(0,applicant.jobPostingName.indexOf("\\"))
                }) >= 0
            }).id;

            getMultyDataPromise(
                ["professional", "professionalSubcategoriesByProfessionalId", "professionalRequiredCertificatesByProfessionalId ","getQuestionnaire"],
                [{id: applicant.professionalId}, {professionalId: applicant.professionalId}, {professionalId: applicant.professionalId}, {category:category, professional: applicant.professionalId}],
                requestFields).then(
                function (loadedObject) {

                    let managedObject = loadedObject.professional;
                    managedObject.profile = managedObject.profile || {workReferences: [], workExperiences: []};
                    managedObject.professionalSubcategories = loadedObject.professionalSubcategoriesByProfessionalId.nodes;
                    managedObject.certificates = loadedObject.professionalRequiredCertificatesByProfessionalId.nodes;
                    managedObject.getQuestionnaire = loadedObject.getQuestionnaire||{};

                    UiView.showDialog(<Provider store={UiView.createDialogStore()}><ViewProfessional
                    managedObject={managedObject}
                    references={me.props.references}
                    metaInfo={me.props.metaInfo}/></Provider>);
                });
        } catch (ex) {
            Error.showErrors(ex)
        }
    }

    onViewReview(Dialog, dialogInfo, record, e) {
        let me = this;

        let editAction = me.baseActions.props.actions.find(a => a.type === BUTTON_TYPE_EDIT);

        const target = e.target ? e.target.parentElement : {
            getAttribute: function (name) {
                return editAction ? editAction.name : null;
            }
        };

        try {

            let requestFields = undefined;

            if (ObjectHelper.isArray(me.requestInfo.getQueryName)) {

                requestFields = [];
                me.requestInfo.getResponseModel.forEach(getResponseModel => {
                    let tmpRequestFields = undefined;
                    if (getResponseModel instanceof FieldsInfoByModel) {
                        tmpRequestFields = Remote.getFieldsByModel(me.props.metaInfo, getResponseModel.model, getResponseModel.ignoreFields, getResponseModel.modelMode);
                    } else {
                        tmpRequestFields = Remote.getFieldsByModel(me.props.metaInfo, getResponseModel, [], me.requestInfo.getResponseModelMode === 'input');
                    }
                    requestFields.push(tmpRequestFields);
                });

            } else {
                requestFields = dialogInfo.fields || Remote.getFieldsByModel(me.props.metaInfo, me.requestInfo.getResponseModel, [], me.requestInfo.getResponseModelMode === 'input');
            }

            me.props.onLoadRecord({
                queryName: ObjectHelper.copyObject(me.requestInfo.getQueryName),
                requestFields: requestFields,
                getQueryParameterName: me.requestInfo.getQueryParameterName,
                idParameterName: me.requestInfo.idParameterName
            }, record, function (object) {
                if (ObjectHelper.isObject(object)) {
                    me.props.onLoadRefs(dialogInfo.references || [], me.props.references, me.props.metaInfo, function () {
                        let actions = {
                            save: saveObject(me.requestInfo).bind(me)
                        }
                        me.showDialog(me, Dialog, Provider, target, object, actions);
                    });

                }
            });
        } catch (ex) {
            Error.showErrors(ex)
        }

    }


    dialogProps(dlg, button) {
        switch (button.getAttribute('action-name')) {
            case 'viewReview':
                return {readOnly: true};
            default:
                return null;
        }
    }


    prepareManagedObject(loadedObject) {
        let rate = [{_enabled: false}, {_enabled: false}, {_enabled: false}, {_enabled: false}, {_enabled: false}];
        if (loadedObject.rate) {
            for (let i = 0; i < loadedObject.rate; i++) {
                rate[i]._enabled = true;
            }
        }
        loadedObject.rate = rate;
        loadedObject.jobPostingApplicationId = loadedObject.applicationId;
        return loadedObject;
    }
}

const ReviewsViewConnector = connect(
    (state, ownProps) => ({}),
    function (dispatch) {
        return Object.assign(baseViewDispatcherMap(dispatch), {});
    })(ReviewsView);

export const dialogReviews = {}

let Form = props => {
    const {handleSubmit, handleCancel, metaInfo} = props;
    const managedObject = props.initialValues.managedObject;


    return (
        <form onSubmit={handleSubmit}>

            <div class="modal__gray-main modal21">


                <div class="modal__info">
                    <div class="item__box">
                        <div class="title">
                            <p>Client Name:</p>
                        </div>
                        <div class="data">
                            <div class="text">
                                <p>{managedObject.practice.firstName}</p>
                                <p>{managedObject.practice.lastName}</p>
                            </div>
                        </div>
                    </div>

                    <div class="item__box">
                        <div class="title">
                            <p>Total Feedback:</p>
                        </div>
                        <div class="data">
                            <p>{managedObject.practice.totalFeedback || 0}</p>
                        </div>
                    </div>

                    <div class="item__box">
                        <div class="title">
                            <p>Total Rating:</p>
                        </div>
                        <div class="data">
                            <div class="digit">
                                <p>{(managedObject.practice.officeRating || 0).toFixed(1)}</p>
                            </div>
                            <FieldArray name="managedObject.practice.rate" component={Stars} readOnly={true}/>
                        </div>
                    </div>
                    <div class="modal__table">
                        <ReviewsViewConnector managedObject={managedObject} metaInfo={metaInfo} references={props.references}/>
                    </div>
                </div>
            </div>
            <div class="footer__btn-wrapper">
                <div class="footer__btn">
                    <button class="blue white" type="button" onClick={handleCancel}>Close</button>
                </div>
            </div>

        </form>)
};

Form = reduxForm({
    form: 'location'
})(Form);


class ReviewsForPractice extends BaseDialog {
    componentDidMount() {
        try {
            let extendedJs = require('../../../../resources/js/main');
            extendedJs.modalInitialization();
        } catch (ex) {
        }

    }

    dialogProps() {
        return {
            width: 960,
            height: 800,
            className: "modal__gray",
            title: "reviews for offices"
        }
    }

    beforeSave(dialog, managedData) {
        let jobPreference = managedData.managedObject.jobPreference,
            professional = managedData.managedObject;

        delete professional['jobPreference'];
        delete professional['profile'];

        managedData.managedObject = {jobPreference: jobPreference, professional: professional};

        Logger.of('App.AddEditLocation.beforeSave').info('ManagedObject data:', managedData);
        return true;
    }

    renderDialogContent() {

        let rate = [
            {_enabled: false, value: false},
            {_enabled: false, value: false},
            {_enabled: false, value: false},
            {_enabled: false, value: false},
            {_enabled: false, value: false}
        ];
        for (var i = 0; i < 5; i++) {
            rate[i]._enabled = this.props.managedObject.practice.officeRating >= i + 1;
            rate[i].value = rate[i]._enabled;
        }

        this.props.managedObject.practice.rate = rate;
        return (<Form onSubmit={this.onSave} handleCancel={this.close} dialog={this}
                      initialValues={{managedObject: this.props.managedObject}} references={this.props.references}
                      metaInfo={this.props.metaInfo}/>);
    }
}

const ReviewsForPracticeDialogConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {});
    },
    function (dispatch) {
        return Object.assign(baseDispatcherMap(dispatch), {});
    })(ReviewsForPractice);

export default ReviewsForPracticeDialogConnector;