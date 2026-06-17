import React, {Component} from 'react';

export default class Checkbox extends Component {

    static idCounter = 1;

    id = undefined;

    initID() {
        this.id = `checkbox${++Checkbox.idCounter}`;
    }

    getError(meta) {
        return meta.active && meta.touched && meta.error && <span data-style="">{meta.error}</span>
    }
    /*{...input}*/
    render() {
        const {input, classWrapper = 'input', title, disabled} = this.props;

        if (!this.id) {
            this.initID();
        }
        return (<div class={classWrapper}>
                <input type="checkbox" {...input} id={this.id} checked={input.value} disabled={disabled} onChange={this.changeValue.bind(this)}/>
                <label for={this.id}>{title}</label>
            </div>
        );
    }

    changeValue(ev){
        this.props.input.onChange(ev);
        this.props.input.onBlur();
    }
}