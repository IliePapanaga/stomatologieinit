import React from 'react';
import {connect} from 'react-redux';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import {reduxForm} from 'redux-form';
import {Logger} from 'react-logger-lib';

export const dialogInfo = {}

let Form = props => {
    const {handleSubmit, handleCancel, invalid, body} = props;
    return (
        <form onSubmit={handleSubmit}>
            {body}
            <div class="footer__btn-wrapper">
                <div class="footer__btn">
                    <button class="blue white" type="button" onClick={handleCancel}>Cancel</button>
                    <button class="blue" type="button" onClick={handleSubmit} disabled={invalid}>Ok</button>
                </div>
            </div>
        </form>)
}

Form = reduxForm({
    form: 'operation'
})(Form);

class Execute extends BaseDialog {
    dialogProps() {
        const {width, height, title} = this.props;
        return {
            width: width,
            height: height,
            className: "modal__gray",
            title: title
        }
    }

    beforeSave(dialog, managedData) {

        Logger.of('App.Operation.Execute.beforeSave').info('ManagedObject data:', managedData);
        return true;
    }

    renderDialogContent() {
        return (<Form onSubmit={this.onSave} handleCancel={this.close} dialog={this}
                      initialValues={{managedObject: this.props.managedObject}} body={this.props.body}/>);
    }
}

const ExecuteDialogConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {});
    },
    function (dispatch) {
        return Object.assign(baseDispatcherMap(dispatch), {});
    })(Execute);

export default ExecuteDialogConnector;