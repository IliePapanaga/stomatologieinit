import React from 'react';
import {connect} from 'react-redux';
import {FieldArray, formValueSelector, reduxForm} from 'redux-form';
import Field from '../../common/form/Field';
import DateField from '../../common/form/DateField';
import {maxOrEqualsThanDate} from '../../../../utils/Validators';
import WorkScheduleGroup from "./form/WorkScheduleGroup";
import DateHelper from "../../../../utils/DateHelper";
import ObjectHelper from "../../../../utils/Object";


let PostingWeeklyPage = props => {
    const {dialog, handleSubmit, handleNextPage, handlePreviousPage, renderNavigation, change, handleCancel, invalid, node, managedObject, readOnly=false} = props;
    let navigation = renderNavigation(dialog, handleSubmit, handleNextPage, handlePreviousPage, change, handleCancel, invalid, node, managedObject);


    return (
        <form onSubmit={handleSubmit}>
            <div class="modal__gray-main modal12 modal34">
                <div class="modal__gray-main-item">
                    <h3></h3>
                        <div class="item__box">
                        <div class="text">
                        <p>Start:</p>
                    </div>
                    <div class="input input__date">
                        <Field name="managedObject.startDate" class="date" placeholder="Select date"
                    component={DateField} dateFormat="MM/DD/YYYY" showYearDropdown="{true}"
                    required={true} onlyDate={true} disabled={readOnly} minDate={DateHelper.getCurrentDate()}/>
                    </div>
                    </div>
                    <div class="item__box">
                        <div class="text">
                        <p>End:</p>
                    </div>
                    <div class="input input__date">
                        <Field name="managedObject.endDate" class="date" placeholder="Select date"
                    component={DateField} dateFormat="MM/DD/YYYY" showYearDropdown="{true}"
                    required={true} onlyDate={true}
                    validate={maxOrEqualsThanDate("managedObject.startDate")} /*validate={maxOrEqualsThanDate("managedObject.startDate")}*/ disabled={readOnly} initiallyChoosenDate={managedObject.startDate} minDate={DateHelper.getCurrentDate()}/>
                    </div>
                    </div>
                </div>

                <div class="modal__gray-main-item">
                    <FieldArray name="managedObject.workSchedules" component={WorkScheduleGroup} required={true} disabled={readOnly}/>
                </div>
            </div>

            <div class="footer__btn-wrapper">
                {navigation}
            </div>
        </form>)
}

PostingWeeklyPage = reduxForm({
    form: 'addposting',
    destroyOnUnmount: false,
    forceUnregisterOnUnmount: true,
    onChange: function (values, dispatch, rform) {
        if (maxOrEqualsThanDate('managedObject.startDate')(ObjectHelper.getValue(values, 'managedObject.endDate'), values)) {
            return false;
        }

        let workSchedulesErrors = [];
        let hasSchedules = false;

        if(values.managedObject.workSchedules){
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

            });
        }

        dispatch({
            type: "@@redux-form/UPDATE_SYNC_ERRORS",
            meta: {form: rform.form},
            payload: {
                syncErrors: {managedObject: {workSchedules: hasSchedules ? workSchedulesErrors : {_error: "Work schedules haven't been specified"}}}
            }
        });
    }
})(PostingWeeklyPage);


const selector = formValueSelector('addposting');

PostingWeeklyPage = connect(state => {
    //const certificateDetails = selector(state, 'cprDetails');
    const managedObject = selector(state, 'managedObject');
    return {
        //certificateDetails: certificateDetails,
        managedObject: managedObject
    }
})(PostingWeeklyPage)

export default PostingWeeklyPage