import React from 'react';
import {connect} from 'react-redux';
import {formValueSelector, reduxForm} from 'redux-form';
import Field from '../../common/form/Field';
import DateField from '../../common/form/DateField';
import {maxOrEqualsThanDate} from '../../../../utils/Validators';

import DateHelper, {serverShortDateFormat} from '../../../../utils/DateHelper';
import JobDayGroup from "./form/JobDayGroup";


let PostingComplexPage = props => {
    const {dialog, handleSubmit, handleNextPage, handlePreviousPage, renderNavigation, change, handleCancel, invalid, node, managedObject, readOnly} = props;
    let navigation = renderNavigation(dialog, handleSubmit, handleNextPage, handlePreviousPage, change, handleCancel, invalid, node, managedObject);
    let startDate = managedObject.startDate;
    let endDate = managedObject.endDate;
    let jobDays = managedObject.jobDays;
    if (startDate && endDate && (!jobDays || jobDays.length <= 0 || (jobDays[0].date !== startDate || jobDays[jobDays.length - 1].date !== endDate))) {
        let jobDays = DateHelper.getJobDays(startDate, endDate, serverShortDateFormat);
        change('managedObject.jobDays', jobDays);
    }

    return (
        <form onSubmit={handleSubmit}>
            <div class="modal__gray-main modal12" style={{position: 'inherit'}}>
                <div class="modal__gray-main-item">
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
                                   validate={maxOrEqualsThanDate("managedObject.startDate")} disabled={readOnly} initiallyChoosenDate={managedObject.startDate} minDate={DateHelper.getCurrentDate()}/>
                        </div>
                    </div>
                </div>

                <div class="apptemp__main-complex-item">
                    <div class="item__box item__box-header">
                        <div class="text">
                            <p>Required dates:</p>
                        </div>
                        <div class="text">
                            <p>Starts:</p>
                        </div>
                        <div class="text">
                            <p>Ends:</p>
                        </div>
                    </div>

                    <Field name="managedObject.jobDays" required={true} component={JobDayGroup} disabled={readOnly}/>

                </div>
            </div>
            <div class="footer__btn-wrapper">
                {navigation}
            </div>
        </form>
    )
}

PostingComplexPage = reduxForm({
    form: 'addposting',
    destroyOnUnmount: false,
    forceUnregisterOnUnmount: true
})(PostingComplexPage);


const selector = formValueSelector('addposting');

PostingComplexPage = connect(state => {
    //const certificateDetails = selector(state, 'cprDetails');
    const managedObject = selector(state, 'managedObject');

    return {
        //certificateDetails: certificateDetails,
        managedObject: managedObject
    }
})(PostingComplexPage)

export default PostingComplexPage