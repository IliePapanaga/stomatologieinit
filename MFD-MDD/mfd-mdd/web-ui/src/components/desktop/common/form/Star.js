import React, {Component} from 'react';

export default class Star extends Component {

    static idCounter = 1;

    id = undefined;

    initID() {
        this.id = `checkbox${++Star.idCounter}`;
    }

    onClick() {
        const {form, dispatch} = this.props.meta;
        const {viewIndex, readOnly} = this.props;

        if(readOnly){
            return false;
        }

        let newValue = !this.props.input.value;
        this.props.input.onChange(newValue);
        if (newValue) {

            for(let i=0;i<viewIndex;i++){
                dispatch({
                    type: "@@redux-form/CHANGE",
                    meta: {
                        form: form,
                        field: this.props.input.name.replace(viewIndex,i),
                        touch: false,
                        persistentSubmitErrors: false,
                        valid: false
                    },
                    payload: newValue
                });
            }

        }else{
            for(let i=viewIndex;i<5;i++){
                dispatch({
                    type: "@@redux-form/CHANGE",
                    meta: {
                        form: form,
                        field: this.props.input.name.replace(viewIndex,i),
                        touch: false,
                        persistentSubmitErrors: false,
                        valid: false
                    },
                    payload: newValue
                });
            }
        }

    }

    render() {
        const {input} = this.props;

        if (!this.id) {
            this.initID();
        }
        return (

            <div onClick={this.onClick.bind(this)}>
                <svg class={input.value ? "star__active" : "star__default"} for={this.id}>
                    <use xlinkHref="#star"></use>
                </svg>
            </div>
        );
    }
}