import React from 'react';
import {connect} from 'react-redux';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import {FieldArray, formValueSelector, reduxForm} from 'redux-form';
import Field from '../../common/form/Field';
import Stars from '../../common/form/Stars';

import {Logger} from 'react-logger-lib';
import DateHelper, {uiDateFormatMonthDayYear} from "../../../../utils/DateHelper";
import TextArea from "../../common/form/TextArea";
import Checkbox from "../../common/form/Checkbox";

export const dialogInfo = {}

let Form = props => {
    const {handleSubmit, handleCancel, invalid, readOnly, raiting} = props;
    const {managedObject} = props.initialValues;
    return (
        <form onSubmit={handleSubmit}>

            <div class="modal__gray-main modal22">
                <div class="modal__gray-main-item">
                    <div class="item__box">
                        <div class="title">
                            <p>Professional Name:</p>
                        </div>
                        <div class="data">
                            <p>{`${managedObject.professionalInfo.firstName} ${managedObject.professionalInfo.lastName}`}</p>
                        </div>
                    </div>

                    <div class="item__box">
                        <div class="title">
                            <p>Posting Name:</p>
                        </div>
                        <div class="data">
                            <p>{managedObject.postingInfo.jobPostingName}</p>
                        </div>
                    </div>

                    <div class="item__box">
                        <div class="title">
                            <p>Work Started:</p>
                        </div>
                        <div class="data">
                            <p>{DateHelper.convertServerDateStringToString(managedObject.postingInfo.startDate, uiDateFormatMonthDayYear)}</p>
                        </div>
                    </div>

                    <div class="item__box">
                        <div class="title">
                            <p>Work Ended:</p>
                        </div>
                        <div class="data">
                            <p>{DateHelper.convertServerDateStringToString(managedObject.postingInfo.endDate, uiDateFormatMonthDayYear)}</p>
                        </div>
                    </div>

                    <div class="item__box">
                        <div class="title">
                            <p>Category:</p>
                        </div>
                        <div class="data">
                            <p>Assistants</p>
                        </div>
                    </div>

                    <div class="item__box">
                        <div class="title">
                            <p>Specialty:</p>
                        </div>
                        <div class="data">
                            <p>RDAF</p>
                        </div>
                    </div>
                </div>

                <div class="modal__gray-main-item">
                    <div class="item__box">
                        <div class="title">
                            <p>Professionalism:</p>
                        </div>
                        <div class="data">
                            <FieldArray name="managedObject.professionalism" component={Stars} readOnly={readOnly}/>
                        </div>
                    </div>

                    <div class="item__box">
                        <div class="title">
                            <p>Communication:</p>
                        </div>
                        <div class="data">
                            <FieldArray name="managedObject.communication" component={Stars} readOnly={readOnly}/>
                        </div>
                    </div>

                    <div class="item__box">
                        <div class="title">
                            <p>Work Quality:</p>
                        </div>
                        <div class="data">
                            <FieldArray name="managedObject.workQuality" component={Stars} readOnly={readOnly}/>
                        </div>
                    </div>

                    <div class="item__box">
                        <div class="title">
                            <p>Punctuality:</p>
                        </div>
                        <div class="data">
                            <FieldArray name="managedObject.punctuality" component={Stars} readOnly={readOnly}/>
                        </div>
                    </div>

                    <div class="item__box">
                        <div class="title">
                            <p>Appearance:</p>
                        </div>
                        <div class="data">
                            <FieldArray name="managedObject.appearance" component={Stars} readOnly={readOnly}/>
                        </div>
                    </div>

                    <div class="item__box">
                        <div class="title">
                            <p>Total Score:</p>
                        </div>
                        <div class="data">
                            <p>{raiting.toFixed(1)}</p>
                        </div>
                    </div>
                </div>

                <div class="modal__gray-main-item">
                    <div className="item__box">
                        <Field name="managedObject.wouldHire"
                               component={Checkbox} disabled={readOnly}/>
                        <div className="text text__red">
                            <p> I want to hire this professional permanently</p>
                        </div>
                    </div>
                </div>

                <div class="modal__gray-main-item">
                    <div class="item__box">
                        <div class="text">
                            <p>Comments:</p>
                        </div>
                        <Field name="managedObject.comment" component={TextArea} required={false} readOnly={readOnly}/>
                    </div>
                </div>

            </div>
            <div class="footer__btn-wrapper">
                <div class="footer__btn">
                    <button className="blue white" type="button" onClick={handleCancel}>Cancel</button>
                    {!readOnly&&<button className="blue" type="button" onClick={handleSubmit} disabled={invalid}>Ok</button>}
                </div>
            </div>

        </form>)
}

Form = reduxForm({
    form: 'professional_review'
})(Form);


const selector = formValueSelector('professional_review');

let rate = function (arr) {
    let result = 0;
    for (var i = 0; i < 5; i++) {
        if (arr[i]._enabled) {
            result++;
        }
    }
    return result;
}

Form = connect(state => {
    const managedObject = selector(state, 'managedObject');

    let rate = function (arr) {
        let result = 0;
        for (var i = 0; i < 5; i++) {
            if (arr[i]._enabled) {
                result++;
            }
        }
        return result;
    }
    let raiting = 0;
    if (managedObject) {
        raiting = rate(managedObject.professionalism) / 5 +
            rate(managedObject.communication) / 5 +
            rate(managedObject.workQuality) / 5 +
            rate(managedObject.punctuality) / 5 +
            rate(managedObject.appearance) / 5;
    }
    return {
        raiting: raiting
    }
})(Form)

class ProfessionalReviewsEdit extends BaseDialog {
    dialogProps() {
        return {
            width: 470,
            height: 630,
            className: "modal__gray",
            title: "edit review"
        }
    }

    /**
     * TODO change component for skiping this logic
     */
    convertData(managedObject) {
        return managedObject;
    }

    /**
     * TODO change component for skiping this logic
     */
    beforeSave(dialog, managedData) {
        let managedObject = managedData.managedObject;
        managedData.managedObject = {
            review: {
                applicationId: managedObject.postingInfo.jobPostingApplicationId,
                workQualityRate: rate(managedObject.workQuality),
                professionalismRate: rate(managedObject.professionalism),
                comment: managedObject.comment,
                communicationRate: rate(managedObject.communication),
                wouldHire: managedObject.wouldHire,
                punctualityRate: rate(managedObject.punctuality),
                appearanceRate: rate(managedObject.appearance)

            }
        };

        Logger.of('App.AddEditLocation.beforeSave').info('ManagedObject data:', managedData);
        return true;
    }

    renderDialogContent() {
        return (<Form onSubmit={this.onSave} handleCancel={this.close} dialog={this}
                      initialValues={{managedObject: this.props.managedObject}}
                      references={this.props.references}
                      readOnly={this.props.readOnly}/>);
    }
}


const ProfessionalReviewsEditDialogConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {});
    },
    function (dispatch) {
        return Object.assign(baseDispatcherMap(dispatch), {});
    })(ProfessionalReviewsEdit);

export default ProfessionalReviewsEditDialogConnector;