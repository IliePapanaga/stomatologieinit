import React, {Component} from 'react';
import ObjectHelper from '../../../../../utils/Object';
import {oneShouldBeChecked} from '../../../../../utils/Validators';
import TimeField from "../../../common/form/TimeField";
import Field from "../../../common/form/Field";
import {serverShortDateFormat} from "../../../../../utils/DateHelper";
import moment from "moment/moment";

export default class JobDay extends Component {

    addValues(values) {
        const {form, dispatch} = this.props.meta;
        this.setState({'value': values});
        this.props.input.onChange(values);
        if (this.props.required) {
            let obj = {};
            ObjectHelper.setValue(obj, this.props.input.name, oneShouldBeChecked(values), true);
            dispatch({
                type: "@@redux-form/UPDATE_SYNC_ERRORS",
                meta: {form: form},
                payload: {
                    syncErrors: obj
                }
            });
        }
    }

    handleChange(e, item) {
        let values = this.props.input.value || [];

        item.excluded = !item.excluded;

        this.addValues(values);
    }

    handleChangeStartTime(item, timeString) {
        let values = this.props.input.value || [];
        item['startTime'] = timeString;
        this.addValues(values);
    }

    handleChangeEndTime(item, timeString) {
        let values = this.props.input.value || [];
        item['endTime'] = timeString;
        this.addValues(values);
    }


    getError(meta) {
        return meta.active && meta.touched && meta.error && <span data-style="">{meta.error}</span>
    }


    render() {
        const {input, name, disabled} = this.props;
        const value = input.value || [];
        let itemRenderer = this.props.itemRenderer;
        itemRenderer = function (item, value, name, scope) {
            let cls = "input input__time";
            let currentMoment = moment(item.date, serverShortDateFormat);
            let id = currentMoment.format("MMDD");
            let label = currentMoment.format("MM/DD dddd");
            if (item.excluded) {
                cls += " input__time-disabled";
            }
            return (
                <div class="item__box">
                    <div class="text">
                        <div class="row__check">
                            <input type="checkbox" checked={!item.excluded} id={`${id}`} value={name}
                                   onChange={(e) => scope.handleChange(e, item)} disabled={disabled}/>
                            <label for={`${id}`}></label>
                        </div>
                        <p>{label}</p>
                    </div>
                    <div class={cls}>
                        <Field disabled={item.excluded || disabled} name={`${id}-startTime`}
                               onChange={(e, timeString) => scope.handleChangeStartTime(item, timeString)}
                               component={TimeField}
                               required={!item.excluded} time={item.startTime} step={15}/>
                    </div>

                    <div class={cls}>
                        <Field disabled={item.excluded || disabled} name={`${id}-endTime`}
                               onChange={(e, timeString) => scope.handleChangeEndTime(item, timeString)}
                               component={TimeField}
                               required={!item.excluded} time={item.endTime} step={15}/>
                    </div>
                </div>)
        }

        return (value.map((item, viewIndex) => (
            itemRenderer(item, value, name, this)
        )));


    }
}