import React, {Component} from 'react';

export default class PasswordField extends Component {

    state = {type: 'password'}

    getButtons(type) {
        return (
            [
                <span class={type === 'text' ? `pass__hide` : `pass__hide hide`}
                      onClick={this.hideHandler.bind(this)}></span>,
                <span class={type === 'password' ? `pass__show` : `pass__show hide`}
                      onClick={this.showHandler.bind(this)}></span>
            ]);
    }

    getInput(input, other, meta, hasValidation){
        return (<input {...input} {...other}
                       class={hasValidation ? (meta.error ? `input__false` : `input__true`) : ``}/>);
    }

    /*{...input}*/
    render() {
        const {input, classField = 'createPass__main-input', fieldWrapperClass, buttonsWrapperClass, hasInputIcon = true, hasValidation = true, meta, ...other} = this.props;
        const {type} = this.state;
        other.type = type;

        return (
            <div class={classField}>
                {fieldWrapperClass && <div class={fieldWrapperClass}>
                    {this.getInput(input, other, meta, hasValidation)}
                </div>}

                {!fieldWrapperClass && this.getInput(input, other, meta, hasValidation)}
                {hasInputIcon && <div>
                    <svg class="input__icon">
                        <use xlinkHref="#Password"></use>
                    </svg>
                </div>}

                {buttonsWrapperClass && <div class={buttonsWrapperClass}>
                    {this.getButtons(type)}
                </div>}

                {!buttonsWrapperClass && this.getButtons(type)}

            </div>
        );
    }

    showHandler(ev) {
        this.setState({type: 'text'});
    }

    hideHandler(ev) {
        this.setState({type: 'password'});
    }
}