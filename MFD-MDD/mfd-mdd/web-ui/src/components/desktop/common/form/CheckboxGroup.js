import React, {Component} from 'react';
import ObjectHelper from '../../../../utils/Object';

export default class CheckboxGroup extends Component {
    static idCounter = 1;

    addValues(values) {
        this.setState({value: values});

        //if (this.props.required) {
        // let obj = {};
        // ObjectHelper.setValue(obj, this.props.input.name, oneShouldBeChecked(values), true);
        // dispatch({
        //     type: "@@redux-form/CHANGE",
        //     meta: {form: form, field: this.props.input.name, touch: false, persistentSubmitErrors: false, valid: false},
        //     payload: values
        // });
        // //}
        this.props.input.onChange(values);
    }

    handleChange(e, id) {
        let values = this.props.input.value || [];

        values = ObjectHelper.copyObject(values);

        if (e.target.checked) {
            values.push(id);
        } else {
            values.splice(values.indexOf(id), 1);
        }

        this.addValues(values);
    }

    getError(meta) {
        return meta.active && meta.touched && meta.error && <span data-style="">{meta.error}</span>
    }


    render() {
        const {input, meta, items, name, classWrapper, itemClassWrapper, required, columns = 1} = this.props;
        const value = input.value || [];
        let itemRenderer = this.props.itemRenderer;
        let className = undefined;
        let error = this.getError(meta);
        if (error || required) {
            className = meta.error ? `input__false` : `input__true`;
        }
        itemRenderer = itemRenderer || function (item, value, name, itemClassWrapper, scope) {
            const key = `group${++CheckboxGroup.idCounter}`;
            return <div class={itemClassWrapper} onChange={(e) => scope.handleChange(e, item.code)}>
                <input type="checkbox" checked={value.indexOf(item.code) > -1} id={`${key}_${item.code}`} value={name}  disabled={scope.props.disabled}/>
                <label for={`${key}_${item.code}`}>{item.name}</label>
            </div>
        }
        if (classWrapper) {
            let cols = [];
            const maxColumnDeep = Math.round(items.length / columns);
            let columnIndex = 0;
            cols[columnIndex] = [];
            items.forEach(function (item) {
                if (cols[columnIndex].length >= maxColumnDeep) {
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