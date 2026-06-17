import React, {Component} from 'react';
import TimeField from "../../../common/form/TimeField";
import Field from "../../../common/form/Field";
import Checkbox from "../../../common/form/Checkbox";
import {DAYS_OF_WEEK} from '../../../../../data/DaysOfWeek';
import {defaultStartTime, defaultEndTime} from "../../../../../utils/DateHelper";

export default class WorkScheduleGroup extends Component {
    getError(meta) {
        return meta.active && meta.touched && meta.error && <span data-style="">{meta.error}</span>
    }

    render() {
        const {fields, disabled} = this.props;


        let itemRenderer = function (field, value, scope) {
            let cls = "input input__time";
            let name = DAYS_OF_WEEK.find(day => day.code === value.weekDay).name;

            if (!value._enabled) {
                cls += " input__time-disabled";
            }

            return (<div class="item__box">
                <Field name={`${field}._enabled`} component={Checkbox} title={name} disabled={disabled}/>

                <div class={cls}>
                    <Field readOnly={!value._enabled} name={`${field}.startTime`}
                           component={TimeField}
                           required={value._enabled} step={15} time={value.startTime || defaultStartTime} disabled={disabled}/>
                </div>

                <div class={cls}>
                    <Field readOnly={!value._enabled} name={`${field}.endTime`}
                           component={TimeField}
                           required={value._enabled} step={15} time={value.endTime || defaultEndTime} disabled={disabled}/>
                </div>
            </div>)
        };

        return (fields.map((field, viewIndex) => (
            itemRenderer(field, fields.get(viewIndex), this)
        )));

    }
}