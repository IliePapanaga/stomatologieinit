import React from 'react';
import {connect} from 'react-redux';
//import Dialog from 'material-ui/Dialog';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import {formValueSelector, reduxForm} from 'redux-form';
import {Logger} from 'react-logger-lib';
import DateHelper from "../../../../utils/DateHelper";
import WorkSchedule from "../schedules/WorkSchedule";
import BrowseDialogButton from "../../common/BrowseDialogButton";

export const dialogInfo = {
    references: ['categories', 'languages']
}

let Form = props => {
    const {handleSubmit, change, handleCancel, invalid, initialValues: {managedObject}, references, readOnly} = props;
    let name = managedObject.name;
    let location = name.substring(name.lastIndexOf("\\") + 1);
    let languages = references.languages.filter((item) => managedObject.requiredLanguages.includes(item.id));
    return (
        <form onSubmit={handleSubmit}>

            <div class="modal__gray-main modal2">
                <div class="modal__gray-main-item">
                    <div class="item__box">
                        <div class="text">
                            <p>Posting Name:</p>
                        </div>
                        <div class="data">
                            <p>{name}</p>
                        </div>
                    </div>

                    <div class="item__box">
                        <div class="text">
                            <p>Location:</p>
                        </div>
                        <div class="data">
                            <p>{location}</p>
                        </div>
                    </div>

                    <div class="item__box">
                        <div class="text">
                            <p>Start Date:</p>
                        </div>
                        <div class="data">
                            <p>
                                {DateHelper.convertServerDateStringToString(managedObject.startDate, "dddd, MMMM Do, YYYY")}&nbsp;
                                {managedObject.startTime && DateHelper.convertServerDateStringToString(managedObject.startTime, "hh:mm A")}
                            </p>
                        </div>
                    </div>

                    <div class="item__box">
                        <div class="text">
                            <p>Work Schedule:</p>
                        </div>
                        <div class="input">
                            <BrowseDialogButton title="View" dialog={WorkSchedule} actions={{
                                save: function (editor, updatedManagedObject, successfulCallBack) {
                                    change(`managedObject.workSchedules`, updatedManagedObject.workSchedules);
                                    editor.close();
                                }
                            }} managedObject={managedObject} browseProps={{readOnly: true}}/>
                        </div>
                    </div>
                </div>
                <div class="modal__gray-main-item">
                    <div class="item__box">
                        <div class="text">
                            <p>Language:</p>
                        </div>
                        <div class="data">
                            <p>{languages.map((language, index) => ((index > 0 ? ',' : '') + language.name))}</p>
                        </div>
                    </div>
                </div>
                <div class="modal__gray-main-item">
                    <div class="item__box">
                        <div class="text">
                            <p>Job Description/ Benefits:</p>
                        </div>
                        <div class="data">
                            <p>{managedObject.comment} </p>
                        </div>
                    </div>
                </div>
            </div>

            <div class="footer__btn-wrapper">
                <div class="footer__btn">
                    <button class="blue white" type="button" onClick={handleCancel}>Close</button>
                    {!readOnly &&
                    <button class="blue" type="button" onClick={handleSubmit} disabled={invalid}>Apply</button>}
                </div>
            </div>

        </form>)
}

Form = reduxForm({
    form: 'applypermanentposting'
})(Form);

const selector = formValueSelector('applypermanentposting');

Form = connect(state => {
    const managedObject = selector(state, 'managedObject')
    return {
        managedObject: managedObject
    }
})(Form)


class PermanentApplyPosting extends BaseDialog {
    dialogProps() {
        return {
            width: 640,
            height: 400,
            className: "modal__gray",
            title: "Posting"
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

        Logger.of('App.AddEditLocation.beforeSave').info('ManagedObject data:', managedData)
        let managedObject = managedData.managedObject;
        managedData.managedObject = {
            permanentJobApplication: {
                jobPostingId: managedObject.id
            }
        }
        return true;
    }

    renderDialogContent() {
        return (<Form onSubmit={this.onSave} handleCancel={this.close} dialog={this}
                      initialValues={{managedObject: this.props.managedObject}} references={this.props.references}
                      readOnly={this.props.readOnly}/>);
    }
}

const PermanentApplyPostingDialogConnector = connect(
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
    })(PermanentApplyPosting);

export default PermanentApplyPostingDialogConnector;
