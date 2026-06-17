import React, {Component} from 'react';
import ObjectHelper from '../../../../utils/Object';
import {oneShouldBeChecked} from '../../../../utils/Validators';

export default class RadioGroup extends Component {

    addValues(value) {
        const {form, dispatch} = this.props.meta;
        this.setState({value: value});
        this.props.input.onChange(value);
        if (this.props.required) {
            let obj = {};
            ObjectHelper.setValue(obj, this.props.input.name, oneShouldBeChecked(value), true);
            dispatch({
                type: "@@redux-form/UPDATE_SYNC_ERRORS",
                meta: {form: form},
                payload: {
                    syncErrors: obj
                }
            });
        }
    }

    handleChange(e, id) {
        this.addValues(id);
    }

    getError(meta) {
        return meta.active && meta.touched && meta.error && <span data-style="">{meta.error}</span>
    }


    render() {
        const {input, meta, items, name, classWrapper, itemClassWrapper, required, disabled, columns = 1} = this.props;
        const value = input.value;
        let itemRenderer = this.props.itemRenderer;
        let className = undefined;
        let error = this.getError(meta);
        if (error || required) {
            className = meta.error ? `input__false` : `input__true`;
        }
        itemRenderer = itemRenderer || function (item, value, name, itemClassWrapper, scope) {
            return <div class={itemClassWrapper}>
                <input disabled={disabled} type="radio" checked={value===item.code} id={`${item.code}`} value={name}
                       onChange={(e) => scope.handleChange(e, item.code)}/>
                <label for={`${item.code}`}>{item.name}</label>
            </div>
        }
        if (classWrapper) {
            let cols = [];
            const maxColumnDeep = Math.round(items.length / columns);
            let columnIndex = 0;
            cols[columnIndex] = [];
            items.forEach(function (item) {
                if (cols[columnIndex].length  >= maxColumnDeep) {
                    columnIndex++;
                    cols[columnIndex] = [];
                }
                cols[columnIndex].push(item);
            });
            return (
                cols.map((col, viewIndex) => (
                    <div class={`${classWrapper} ${className}`}>
                        {col.map((item, viewIndex) => (
                            itemRenderer(item, value, name, itemClassWrapper, this)
                        ))}
                    </div>
                )));
        } else {
            return (items.map((item, viewIndex) => (
                itemRenderer(item, value, name, itemClassWrapper, this)
            )));
        }

    }
}