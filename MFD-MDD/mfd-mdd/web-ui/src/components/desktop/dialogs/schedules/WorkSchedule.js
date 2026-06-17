import React from 'react';
import {connect} from 'react-redux';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import {FieldArray, formValueSelector, reduxForm} from 'redux-form';
import WorkScheduleGroup from "../postings/form/WorkScheduleGroup";

export const dialogInfo = {}

let PostingForm = props => {
    const {handleSubmit, handleCancel, invalid, readOnly} = props;
    return (
        <form onSubmit={handleSubmit}>
            <div class="modal__gray-main modal12 modal34 modal36">
                <div class="modal__gray-main-item">
                    <h3>Select Work Schedule</h3>
                </div>

                <div class="modal__gray-main-item">

                    <FieldArray name="managedObject.workSchedules" component={WorkScheduleGroup} required={true}
                                validate={function (values) {
                                    return values.find(workSchedule => workSchedule._enabled) ? undefined : "Work schedules haven't been specified";
                                }} disabled={readOnly}/>

                </div>
            </div>

            <div class="footer__btn-wrapper">
                <div class="footer__btn">
                    <button class="blue white" type="button" onClick={handleCancel}>{readOnly && "Close"}{!readOnly && "Cancel"}</button>
                    {!readOnly &&
                    <button class="blue" type="button" onClick={handleSubmit} disabled={invalid}>Post</button>}
                </div>
            </div>
        </form>)
}

PostingForm = reduxForm({
    form: 'posting',
    onChange: function (values, dispatch, rform) {
        let workSchedulesErrors = [];
        let hasSchedules = false;
        values.managedObject.workSchedules.forEach(function (workSchedule, index) {
            let error = undefined;
            if (workSchedule._enabled) {
                hasSchedules = true;
                if (!workSchedule.startTime) {
                    error = error || {}
                    error.startTime = 'Required';
                }
                if (!workSchedule.endTime) {
                    error = error || {}
                    error.endTime = 'Required';
                }
            }

            workSchedulesErrors.push(error);

        })
        dispatch({
            type: "@@redux-form/UPDATE_SYNC_ERRORS",
            meta: {form: rform.form},
            payload: {
                syncErrors: {managedObject: {workSchedules: hasSchedules ? workSchedulesErrors : {_error: "Work schedules haven't been specified"}}}
            }
        });
    }
})(PostingForm);


const selector = formValueSelector('addposting');

PostingForm = connect(state => {
    //const certificateDetails = selector(state, 'cprDetails');
    const managedObject = selector(state, 'managedObject');

    return {
        //certificateDetails: certificateDetails,
        managedObject: managedObject
    }
})(PostingForm)


class WorkSchedule extends BaseDialog {

    dialogProps() {
        return {
            width: 640,
            height: 700,
            className: "modal__gray",
            title: "Posting"
        }
    }

    beforeSave(dialog, managedData) {
        return true;
    }


    renderDialogContent() {
        return (
            <PostingForm onSubmit={this.onSave} handleCancel={this.close}
                         dialog={this}
                         initialValues={{managedObject: this.props.managedObject}}
                         references={this.props.references} readOnly={this.props.readOnly}/>);
    }
}

const WorkScheduleDialogConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {});
    },
    function (dispatch) {
        return Object.assign(baseDispatcherMap(dispatch), {});
    })(WorkSchedule);

export default WorkScheduleDialogConnector;