import React, {Component} from 'react';

export default class TextArea extends Component {
    getError(meta) {
        return meta.active && meta.touched && meta.error && <span data-style="">{meta.error}</span>
    }
    render() {
        const { input, meta, required, readOnly=false} = this.props;
        let className = undefined;
        if ((input.value && required) || meta.error) {
            className = meta.error ? `input__false` : `input__true`;
        }
        return (<div class="input">
            <textarea {...input } id={input.name} class={className} readOnly={readOnly}></textarea>
        </div>);
    }
}