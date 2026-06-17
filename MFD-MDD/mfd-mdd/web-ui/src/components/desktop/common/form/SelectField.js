import React, {Component} from 'react';

export default class SelectField extends Component {
    getError(meta) {
        return meta.active && meta.touched && meta.error && <span data-style="">{meta.error}</span>
    }

    render() {
        const {input, meta, menuItems, placeholder = 'Select', hideEmpty = false, disabled = false, grouped = false} = this.props;
        let className = this.props.className;

        if ((input.value /*&& required*/) || meta.error) {
            className = meta.error ? `input__false` : `input__true`;
        }
        if (grouped) {
            return ([<select {...input} class={className} disabled={disabled}>
                    {!hideEmpty && <option value="">{placeholder}</option>}
                    {menuItems.map((menuItem, index) => (
                        <optgroup label={menuItem.group}>
                            {menuItem.options.map((option, optionIndex) => (
                                <option value={option.code || option.id}>{option.name || menuItem.label}</option>
                            ))}
                        </optgroup>
                    ))}
                </select>]
            );
        } else {
            return ([<select {...input} class={className} disabled={disabled}>
                    {!hideEmpty && <option value="">{placeholder}</option>}
                    {menuItems.map((menuItem, index) => (
                        <option value={menuItem.code || menuItem.id}>{menuItem.name || menuItem.label}</option>
                    ))}
                </select>/*, {error}*/]
            );
        }

    }
}//