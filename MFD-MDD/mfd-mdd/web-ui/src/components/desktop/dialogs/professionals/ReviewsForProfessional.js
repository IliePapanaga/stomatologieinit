import React from 'react';
import {connect} from 'react-redux';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import {FieldArray, reduxForm} from 'redux-form';
import {
    BUTTON_TYPE_DELETE,
    BUTTON_TYPE_EDIT,
    DEFAULT_CELL_STYLE,
    SELECTION_MODE_SINGLE,
    REST_API_PREFIX_GET_USER_PHOTO,
} from '../../../../utils/Constants';
import {Logger} from 'react-logger-lib';
import ProfessionalReviewsEdit, {dialogInfo as dialogInfoProfessionalReviewsEdit} from '../reviews/ProfessionalReviewsEdit';
import Renderer from "../../../../utils/Renderer";
import BaseView, {baseViewDispatcherMap} from "../../common/BaseView";
import Stars from "../../common/form/Stars";

export const dialogReviews = {}

class ReviewsView extends BaseView {
    initView(props) {
        let configuration = {
            requestInfo: {
                fetchQueryName: 'professionalReviews',
                getQueryName: "professionalReview",
                updateQueryName: "updateProfessionalReview",
                deleteQueryName: "deleteProfessionalReview",
                getResponseModel: "LocationToProfessionalReview",
                updateResponseModel: undefined
            },

            additionalFields: [],
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
                    dataIndex: 'practiceOwnerLastName',
                    name: 'practiceOwnerLastName',
                    title: 'Client Last Name',
                    cellClass: DEFAULT_CELL_STYLE,
                    orderName: "PRACTICE_OWNER_LAST_NAME"
                },
                {
                    dataIndex: 'practiceOwnerFirstName',
                    name: 'practiceOwnerFirstName',
                    title: 'Client First Name',
                    cellClass: DEFAULT_CELL_STYLE,
                    orderName: "PRACTICE_OWNER_FIRST_NAME"
                },
                {
                    dataIndex: 'practiceLocationName',
                    name: 'practiceLocationName',
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
                    dataIndex: 'professionalismRate',
                    name: 'professionalismRate',
                    title: 'Professionalism',
                    cellClass: DEFAULT_CELL_STYLE,
                    orderName: "JOB_POSTING_END_DATE",
                    renderer: Renderer.getRatingRenderer
                },
                {
                    dataIndex: 'communicationRate',
                    name: 'communicationRate',
                    title: 'Communication',
                    cellClass: DEFAULT_CELL_STYLE,
                    orderName: "PROFESSIONALISM_RATE",
                    renderer: Renderer.getRatingRenderer
                },
                {
                    dataIndex: 'workQualityRate',
                    name: 'workQualityRate',
                    title: 'Work Quality',
                    cellClass: DEFAULT_CELL_STYLE,
                    orderName: "WORK_QUALITY_RATE",
                    renderer: Renderer.getRatingRenderer
                },
                {
                    dataIndex: 'punctualityRate',
                    name: 'punctualityRate',
                    title: 'Punctuality',
                    cellClass: DEFAULT_CELL_STYLE,
                    orderName: "PUNCTUALITY_RATE",
                    renderer: Renderer.getRatingRenderer
                },
                {
                    dataIndex: 'appearanceRate',
                    name: 'appearanceRate',
                    title: 'Appearance',
                    cellClass: DEFAULT_CELL_STYLE,
                    orderName: "APPEARANCE_RATE",
                    renderer: Renderer.getRatingRenderer
                },
                {
                    dataIndex: 'totalScore',
                    name: 'totalScore',
                    title: 'Total Score',
                    cellClass: DEFAULT_CELL_STYLE,
                    orderName: "TOTAL_SCORE",
                    renderer: Renderer.getRatingRenderer
                },
                {
                    dataIndex: 'wouldHire',
                    name: 'wouldHire',
                    title: 'Wish to hire',
                    cellClass: DEFAULT_CELL_STYLE,
                    orderName: "WOULD_HIRE"
                },
                {
                    dataIndex: 'blackListed',
                    name: 'blackListed',
                    title: 'Blacklisted',
                    cellClass: DEFAULT_CELL_STYLE,
                    orderName: "BLACKLISTED"
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
                    orderName: "FEEDBACK_DATE",
                    renderer: Renderer.getDateRenderer
                },
            ],
            actions: [
                {
                    label: "Edit review",
                    type: BUTTON_TYPE_EDIT,
                    dialog: ProfessionalReviewsEdit,
                    dialogInfo: dialogInfoProfessionalReviewsEdit
                },
                {label: "Delete review", type: BUTTON_TYPE_DELETE}
            ],
            selectionMode: SELECTION_MODE_SINGLE,
            params: {professionalId: props.managedObject.id}
        }
        return configuration;
    }


    prepareManagedObject(loadedObject) {
        let me=this;

        let managedObject = {
            professionalReview: loadedObject,
            postingInfo: me.state.selection[0],
            professionalInfo: me.props.managedObject
        }

        managedObject.professionalism = [{_enabled: false}, {_enabled: false}, {_enabled: false}, {_enabled: false}, {_enabled: false}];
        managedObject.communication = [{_enabled: false}, {_enabled: false}, {_enabled: false}, {_enabled: false}, {_enabled: false}];
        managedObject.workQuality = [{_enabled: false}, {_enabled: false}, {_enabled: false}, {_enabled: false}, {_enabled: false}];
        managedObject.punctuality = [{_enabled: false}, {_enabled: false}, {_enabled: false}, {_enabled: false}, {_enabled: false}];
        managedObject.appearance = [{_enabled: false}, {_enabled: false}, {_enabled: false}, {_enabled: false}, {_enabled: false}];

        if (loadedObject) {
            let initRate = function (parentTo, parentFrom, field) {
                let fromField = `${field}Rate`;
                if (parentFrom[fromField]) {
                    for (let i = 0; i < parentFrom[fromField]; i++) {
                        parentTo[field][i]._enabled = true;
                    }
                }
            }
            initRate(managedObject, loadedObject, 'professionalism');
            initRate(managedObject, loadedObject, 'communication');
            initRate(managedObject, loadedObject, 'workQuality');
            initRate(managedObject, loadedObject, 'punctuality');
            initRate(managedObject, loadedObject, 'appearance');

            managedObject.comment = loadedObject.comment;
        }
        managedObject.postingInfo.jobPostingApplicationId = loadedObject.applicationId;
        return managedObject;
    }
}

const ReviewsViewConnector = connect(
    (state, ownProps) => ({}),
    function (dispatch) {
        return Object.assign(baseViewDispatcherMap(dispatch), {});
    })(ReviewsView);


let Form = props => {
    const {handleSubmit, handleCancel, metaInfo} = props;
    const managedObject = props.initialValues.managedObject;
    const photoUrl = REST_API_PREFIX_GET_USER_PHOTO.replace('{0}', managedObject.id);


    return (
        <form onSubmit={handleSubmit}>

            <div class="modal__gray-main modal21">


                <div class="modal__info">
                    <div class="item__box">
                        <div class="title">
                            <p>Professional:</p>
                        </div>
                        <div class="data">
                            <div class="img"><img src={photoUrl} alt=""/></div>
                            <div class="text"><p>{managedObject.firstName}</p><p>{managedObject.lastName}</p></div>
                        </div>
                    </div>

                    <div class="item__box">
                        <div class="title">
                            <p>Total Feedback:</p>
                        </div>
                        <div class="data">
                            <p>{managedObject.totalFeedback}</p>
                        </div>
                    </div>

                    <div class="item__box">
                        <div class="title">
                            <p>Total Rating:</p>
                        </div>
                        <div class="data">
                            <div class="digit"><p>{managedObject.rating}</p></div>
                            <FieldArray name="managedObject.rate" component={Stars} readOnly={true}/>
                        </div>
                    </div>
                    <div class="modal__table">
                    <ReviewsViewConnector managedObject={managedObject} metaInfo={metaInfo}/>
                    </div>
                </div>
            </div>
            <div class="footer__btn-wrapper">
                <div class="footer__btn">
                    <button class="blue white" type="button" onClick={handleCancel}>Close</button>
                </div>
            </div>

        </form>)
}

Form = reduxForm({
    form: 'reviewsfor professional'
})(Form);


class ReviewsForProfessional extends BaseDialog {
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
            title: "reviews for professional"
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
            rate[i]._enabled = this.props.managedObject.rating >= i + 1;
            rate[i].value = rate[i]._enabled;
        }

        this.props.managedObject.rate = rate;

        return (<Form onSubmit={this.onSave} handleCancel={this.close} dialog={this}
                      initialValues={{managedObject: this.props.managedObject}} references={this.props.references}
                      metaInfo={this.props.metaInfo}/>);
    }
}

const ReviewsForProfessionalDialogConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {});
    },
    function (dispatch) {
        return Object.assign(baseDispatcherMap(dispatch), {});
    })(ReviewsForProfessional);

export default ReviewsForProfessionalDialogConnector;