import React from 'react';
import {connect} from 'react-redux';
//import Dialog from 'material-ui/Dialog';
import TextArea from '../../common/form/TextArea';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import {reduxForm} from 'redux-form';
import Field from '../../common/form/Field';
import {INTERVIEW_TYPE_PERSONAL, INTERVIEW_TYPE_WORKING} from '../../../../utils/Constants';
import {Logger} from 'react-logger-lib';
import RadioGroup from "../../common/form/RadioGroup";
import DateField from "../../common/form/DateField";
import TimeField from "../../common/form/TimeField";
import DateHelper from "../../../../utils/DateHelper";

export const dialogInfo = {
    references: ['categories', 'languages']
}

let Form = props => {
    const {handleSubmit, handleCancel, invalid} = props;
    const {managedObject} = props.initialValues;
    return (
        <form onSubmit={handleSubmit}>

            <div class="modal__gray-main modal3 modal50">
                <div class="modal__gray-main-item">
                    <h3>Information</h3>
                    <div class="item__box">
                        <div class="title">
                            <p>Candidate Name:</p>
                        </div>
                        <div class="data">
                            <p>{managedObject.applicant.firstName} {managedObject.applicant.lastName}</p>
                        </div>
                    </div>
                    <div class="item__box">
                        <div class="title">
                            <p>Posting Name:</p>
                        </div>
                        <div class="data">
                            <p>{managedObject.posting.name}</p>
                        </div>
                    </div>
                    <div class="item__box">
                        <div class="title">
                            <p>Location:</p>
                        </div>
                        <div class="data">
                            <p>{managedObject.posting.practiceLocationName}</p>
                        </div>
                    </div>
                </div>
                <div class="modal__gray-main-item">


                    <div class="item__box">
                        <div class="title">
                            <p>Interview Type:</p>
                        </div>
                        <Field name="interviewType"
                               items={[
                                   {
                                       code: INTERVIEW_TYPE_WORKING,
                                       name: 'Working Interview'
                                   },
                                   {
                                       code: INTERVIEW_TYPE_PERSONAL,
                                       name: 'Personal Interview'
                                   }]}
                               required={true}
                               component={RadioGroup} itemClassWrapper="text" classWrapper={"item__box"}/></div>

                </div>
                <div class="modal__gray-main-item">
                    <h3>Date & Time Interview options</h3>
                    <div class="description">
                        <p>You should select 4 Dates&Time option for interview</p>
                    </div>
                    <div class="item__box-wrapper">
                        <div class="item__box">
                            <div class="title">
                                <p>Option 1<span class="star">*</span></p>
                            </div>
                            <div class="input input__date">
                                <Field name="option1.date" class="date" placeholder="Select date"
                                       component={DateField} onlyDate={true} dateFormat="MM/DD/YYYY" showYearDropdown="{true}"
                                       minDate={DateHelper.getCurrentDate()}
                                       required={true}/>
                            </div>
                            <Field name="option1.time" component={TimeField} step={15} required={true}/>
                        </div>

                        <div class="item__box">
                            <div class="title">
                                <p>Option 2<span class="star">*</span></p>
                            </div>
                            <div class="input input__date">
                                <Field name="option2.date" class="date" placeholder="Select date"
                                       component={DateField}  onlyDate={true} dateFormat="MM/DD/YYYY"  showYearDropdown="{true}"
                                       minDate={DateHelper.getCurrentDate()}
                                       required={true}/>
                            </div>
                            <Field name="option2.time" component={TimeField} step={15} required={true}/>
                        </div>


                        <div class="item__box">
                            <div class="title">
                                <p>Option 3<span class="star">*</span></p>
                            </div>
                            <div class="input input__date">
                                <Field name="option3.date" class="date" placeholder="Select date"
                                       component={DateField}  onlyDate={true} dateFormat="MM/DD/YYYY"  showYearDropdown="{true}"
                                       minDate={DateHelper.getCurrentDate()}
                                       required={true}/>
                            </div>
                            <Field name="option3.time" component={TimeField} step={15} required={true}/>
                        </div>


                        <div class="item__box">
                            <div class="title">
                                <p>Option 4<span class="star">*</span></p>
                            </div>
                            <div class="input input__date">
                                <Field name="option4.date" class="date" placeholder="Select date"
                                       component={DateField}  onlyDate={true} dateFormat="MM/DD/YYYY"  showYearDropdown="{true}"
                                       minDate={DateHelper.getCurrentDate()}
                                       required={true}/>
                            </div>
                            <Field name="option4.time" component={TimeField} step={15} required={true}/>
                        </div>
                    </div>

                    <div class="description description__bottom">
                        <p>Candidate will be able to chose one option and you will receive a notification.</p>
                    </div>

                </div>

                <div class="modal__gray-main-item">
                    <div class="item__box">
                        <div class="text">
                            <p>Comment:</p>
                        </div>
                        <Field name="comments" component={TextArea}/>
                    </div>
                </div>
            </div>

            <div class="footer__btn-wrapper">
                <div class="footer__btn">
                    <button class="blue white" type="button" onClick={handleCancel}>Cancel</button>
                    <button class="blue" type="button" onClick={handleSubmit} disabled={invalid}>Ok</button>
                </div>
            </div>

        </form>)
}

Form = reduxForm({
    form: 'practiceplaceinterviewdate'
})(Form);


class PermanentSelectInterviewDate extends BaseDialog {
    dialogProps() {
        return {
            width: 640,
            height: 780,
            className: "modal__gray",
            title: "Schedule Job Interview"
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
        Logger.of('App.AddEditLocation.beforeSave').info('ManagedObject data:', managedData);
        let options = [];
        for (let i = 1; i < 5; i++) {
            if (managedData[`option${i}`]) {
                let date = managedData[`option${i}`].date;
                let time = managedData[`option${i}`].time;
                if (date && time) {
                    options.push({date: date, time: time});
                }
            }
        }

        let object = {
            interview: {
                options: options,
                working: managedData.interviewType === INTERVIEW_TYPE_WORKING,
                comments: managedData.comments,
                applicationId: managedData.managedObject.applicant.id
            }
        };

        managedData.managedObject=object;
        return true;
    }

    renderDialogContent() {
        return (<Form onSubmit={this.onSave} handleCancel={this.close} dialog={this}
                      initialValues={{managedObject: this.props.managedObject}} references={this.props.references}/>);
    }
}

const PermanentSelectInterviewDateDialogConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {});
    },
    function (dispatch) {
        return Object.assign(baseDispatcherMap(dispatch), {
            onSend: (sendData, d, form) => {
// dispatch(requestResetPassword(sendData.email, form.recaptchaHash)).then(function (result) {
//     if (result) {
//            toastr.success("Resset Password", "Please, check your email, we sent you request for reset your password.");
//         dispatch({ type: EVENT_CHANGED_RECAPTCHA_HASH, recaptchaHash: undefined });
//         form.handleCancel();
//     }
// });
            }
        });
    })(PermanentSelectInterviewDate);

export default PermanentSelectInterviewDateDialogConnector;
