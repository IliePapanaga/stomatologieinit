import React from 'react';
import {connect} from 'react-redux';
import {formValueSelector, reduxForm} from 'redux-form';
import Field from '../../common/form/Field';
import DateField from '../../common/form/DateField';
import {maxOrEqualsThanDate, maxOrEqualsThanTime} from '../../../../utils/Validators';
import TimeField from "../../common/form/TimeField";
import DateHelper, {defaultStartTime, defaultEndTime} from "../../../../utils/DateHelper";


let PostingSimplePage = props => {
    const {dialog, handleSubmit, handleNextPage, handlePreviousPage, renderNavigation, change, handleCancel, invalid, node, managedObject, readOnly} = props;
    let navigation = renderNavigation(dialog, handleSubmit, handleNextPage, handlePreviousPage, change, handleCancel, invalid, node, managedObject);

    return (
        <form onSubmit={handleSubmit}>
            <div class="modal__gray-main modal11">
                <div class="item__box">
                    <div class="text">
                        <p>Starts:</p>
                    </div>
                    <div class="input input__date">
                        <Field name="managedObject.startDate" class="date" placeholder="Select date"
                               component={DateField} dateFormat="MM/DD/YYYY" showYearDropdown="{true}" required={true}
                               onlyDate={true} disabled={readOnly} minDate={DateHelper.getCurrentDate()}/>
                    </div>
                    <Field name="managedObject.startTime" component={TimeField} required={true} step={15}
                           time={managedObject.startTime || defaultStartTime} disabled={readOnly}/>
                </div>

                <div class="item__box">
                    <div class="text">
                        <p>Ends:</p>
                    </div>
                    <div class="input input__date">
                        <Field name="managedObject.endDate" class="date" placeholder="Select date"
                               component={DateField} dateFormat="MM/DD/YYYY" showYearDropdown="{true}" required={true}
                               onlyDate={true} validate={maxOrEqualsThanDate("managedObject.startDate")} disabled={readOnly} initiallyChoosenDate={managedObject.startDate} minDate={DateHelper.getCurrentDate()}/>
                    </div>
                    <Field name="managedObject.endTime" component={TimeField} required={true} step={15}
                           validate={maxOrEqualsThanTime("managedObject.startTime", false)} disabled={readOnly}
                           time={managedObject.endTime || defaultEndTime}/>
                </div>

                <div class="posting__help">
                    <p class="mandatory">Fields marked* are mandatory</p>
                </div>
            </div>
            <div class="footer__btn-wrapper">
                {navigation}
            </div>
        </form>)
};

PostingSimplePage = reduxForm({
    form: 'addposting',
    destroyOnUnmount: false,
    forceUnregisterOnUnmount: true
})(PostingSimplePage);


const selector = formValueSelector('addposting');

PostingSimplePage = connect(state => {
    //const certificateDetails = selector(state, 'cprDetails');
    const managedObject = selector(state, 'managedObject');

    return {
        //certificateDetails: certificateDetails,
        managedObject: managedObject
    }
})(PostingSimplePage);

export default PostingSimplePage