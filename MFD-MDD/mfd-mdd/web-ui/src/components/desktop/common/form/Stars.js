import React, {Component} from 'react';
import Star from "./Star";
import Field from "./Field";

export default class Stars extends Component {

    onClick(){
        // let values = this.props.input.value || [];
        //
        // values = ObjectHelper.copyObject(values);
        //
        // if (e.target.checked) {
        //     values.push(id);
        // } else {
        //     values.splice(values.indexOf(id), 1);
        // }
        //
        // this.addValues(values);
        alert(1);
    }
    render() {
        const {fields, readOnly} = this.props;

        let itemRenderer = function (field, value, viewIndex, scope) {
            return (<Field name={`${field}._enabled`} component={Star} readOnly={readOnly} viewIndex={viewIndex}/>)
        }


        return (fields.map((field, viewIndex) => (
            itemRenderer(field, fields.get(viewIndex), viewIndex, this)
        )));
    }
}