import React from 'react';
import InputMask from 'react-input-mask';

export default class TextField extends InputMask {

    getError(meta) {
        return meta.active && meta.touched && meta.error && <span data-style="">{meta.error}</span>
    }
    render() {
        const { input, meta, type, mask, maskChar, readonly, placeholder } = this.props;
        let error = this.getError(meta);
        let className = undefined;
        if ((input.value /*&& required*/) || meta.error) {
            className = meta.error ? `input__false` : `input__true`;
        }


        return (
            <div class="input">
                {mask && <InputMask {...input} mask={mask} maskChar={maskChar} class={className} autocomplete="off" type={type} />}
                {!mask && <input readonly={readonly} {...input} type={type} autocomplete="off" class={className} placeholder={placeholder}/>}{error}
            </div>
        );
    }
}