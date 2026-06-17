import React from 'react';
import {connect} from 'react-redux';
//import Dialog from 'material-ui/Dialog';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import {formValueSelector, reduxForm} from 'redux-form';
import Field from '../../common/form/Field';
import {INTERVIEW_TYPE_PERSONAL, INTERVIEW_TYPE_WORKING} from '../../../../utils/Constants';
import {Logger} from 'react-logger-lib';
import RadioGroup from "../../common/form/RadioGroup";
import DateHelper, {uiDateFormat} from "../../../../utils/DateHelper";

export const dialogInfo = {}

let Form = props => {
    const {handleSubmit, handleCancel, optionId} = props;
    let {invalid} = props;
    const {managedObject} = props.initialValues;
    invalid = !optionId;
    return (
        <form onSubmit={handleSubmit}>

            <div class="modal__gray-main modal3">
                <div class="modal__gray-main-item">
                    <h3>Information</h3>
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
                        <Field name="managedObject.interviewType"
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
                               component={RadioGroup} itemClassWrapper="text" classWrapper={"item__box"}
                               disabled={true}/>
                    </div>
                </div>
                <div class="modal__gray-main-item">
                    <h3>Date & Time Interview options</h3>
                    <div class="description">
                        <p>Please choose one option and notification will go to the Doctor! Only one option can be
                            accepted!</p>
                    </div>
                    <div class="item__box-wrapper">

                        <Field name="managedObject.optionId"
                               itemRenderer={function (item, value, name, itemClassWrapper, scope) {
                                   let optionsIndex = managedObject.interview.options.findIndex(iOption => iOption.id === item.code);
                                   return <div class={itemClassWrapper}>
                                       <div class="title">
                                           <p>Option {optionsIndex + 1}</p>
                                       </div>
                                       <div class="data">
                                           <p>{DateHelper.convertServerDateStringToString(managedObject.interview.options[optionsIndex].dateTime, uiDateFormat)}</p>
                                       </div>
                                       <input type="checkbox" checked={value === item.code||(managedObject.interview.acceptedOption&&managedObject.interview.acceptedOption.id===item.code)}
                                              id={`${item.code}`} value={name}
                                              onChange={(e) => scope.handleChange(e, item.code)} disabled={managedObject.interview.acceptedOption}/>
                                       <label for={`${item.code}`}>{item.name}</label>
                                   </div>
                               }}
                               items={managedObject.options}
                               component={RadioGroup} itemClassWrapper="item__box"/>

                    </div>

                    <div class="description">
                        <p>Please be aware that if you do not accept any option for the interview, it means that you
                            reject the suggested options and a doctor has to find other options.</p>
                    </div>

                </div>

                <div class="modal__gray-main-item">
                    <div class="item__box">
                        <div class="title">
                            <h3>Comments:</h3>
                        </div>
                        <div class="data">
                            <p>{managedObject.interview.comments} </p>
                        </div>
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
    form: 'selectinterviewdate'
})(Form);

const selector = formValueSelector('selectinterviewdate');

Form = connect(state => {
    return {
        optionId: selector(state, 'managedObject.optionId')
    }
})(Form)


class PermanentSelectInterviewDate extends BaseDialog {
    dialogProps() {
        return {
            width: 640,
            height: 700,
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
