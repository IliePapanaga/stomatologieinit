import React, {Component} from 'react';
import Checkbox from './Checkbox';

export default class YesNoAnswer extends Component {
    static idCounter = 1;
    id = undefined;

    componentDidMount() {
        this.id = `checkbox${++Checkbox.idCounter}`;
    }

    handleChange(e, id) {
        let value = this.props.input.value || false;

        value = !value;

        this.props.input.onChange(value);
        this.setState({ value: value });
    }

    getError(meta) {
        return meta.active && meta.touched && meta.error && <span data-style="">{meta.error}</span>
    }
    render() {
        const { input, name, names, classWrapper = "item__radio-wrapper", itemClassWrapper = "item__radio-content" } = this.props;
        const value = input.value || false;
        if (!this.id) {
            this.componentDidMount();
        }
        let id = this.id;
        return (
            <div class={classWrapper}>
                <div class={itemClassWrapper}>
                    <input type="checkbox" class={input.class} id={`true_${id}`} value={true} checked={value} onChange={(e) => this.handleChange(e, (names && names.length > 0 ? names[0] : name))} />
                    <label for={`true_${id}`}>{(names && names.length > 0 ? names[0] : '')}</label>
                </div>
         {/*     <div class={itemClassWrapper}>
                    <input type="radio" class={input.class} id={`false_${id}`} value={false} checked={!value} onChange={(e) => this.handleChange(e, (names && names.length > 1 ? names[1] : name))} />
                    <label for={`false_${id}`}>{(names && names.length > 1 ? names[1] : '')}</label>
                </div>*/}

            </div>);

    }
}