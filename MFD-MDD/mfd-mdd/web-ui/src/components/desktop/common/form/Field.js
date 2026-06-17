import React from 'react';

import {Field as ReduxField} from 'redux-form';
import {baseValidation} from '../../../../utils/Validators';
import {date} from '../../../../utils/Normalizers';

export default class Field extends ReduxField {

    // constructor(props, context) {
    //     let modifiedProps = Object.assign({}, props);
    //     if (modifiedProps.dateFormat) {
    //         if (!modifiedProps.normalize) {
    //             modifiedProps.normalize = date.bind(context, modifiedProps.dateFormat, modifiedProps.placeholder, modifiedProps.onlyDate);
    //         }
    //     }
    //     super(modifiedProps, context)
    // }

    getValidation(props, scope) {
        if (props.validate) {
            return function (value, managedObject, form) {
                let result = baseValidation.call(scope, ...arguments) || (value && props.validate.call(scope, ...arguments));

                return result;
            }
        } else if (props.mask) {
            return function (value, managedObject, form) {
                let len = 0;
                for (var i = 0; i < props.mask.length; i++) {
                    var char = props.mask[i];
                    if (char === '9' || char === 'a' || char === '*') {
                        len++;
                    }
                }
                let result = baseValidation.call(scope, ...arguments) || (value && (value.length !== len || value.indexOf(props.maskChar) >= 0) ? "Field's format is wrong" : undefined);

                return result;
            }
        }

        return function (value, managedObject, form) {
            return baseValidation.call(scope, ...arguments);
        }
    }

    initNormalize(props) {
        var _this2 = this;
        if (props.dateFormat) {
            if (!props.normalize) {
                props.normalize = date.bind(_this2, props.dateFormat, props.placeholder, props.onlyDate);
            }
        }
    }

    componentDidMount() {
        var _this2 = this;
        const {required, minLength, maxLength, validate, ...other} = _this2.props;
        // _this2.props=Object.assign({},_this2.props);
        // this.initNormalize(_this2.props);
        this.context._reduxForm.register(this.name, 'Field', function () {
            return _this2.getValidation(_this2.props, _this2);
        }, function () {
            return _this2.props.warn;
        });
    }


    render() {
        var _this2 = this;
        let props=Object.assign({},_this2.props);
        _this2.initNormalize(props);
        return (<ReduxField {...props}/>);
    }
}