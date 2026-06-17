import React, {Component} from 'react';
import UiView from '../../../utils/UiView';
import {Provider} from 'react-redux';


export default class BrowseDialogButton extends Component {

    // constructor(props) {
    //     super(props);
    //     const { input, meta, columns, actions = [], required } = this.props;
    //     const nodes = meta.initial || [];

    //     this.state = {
    //         columns: columns,
    //         selection: [],
    //         selectionMode: SELECTION_MODE_SINGLE,
    //         nodes: nodes,
    //         orders: this.configureOrders(columns),
    //         orderInfo: undefined
    //     }
    // }

    render() {
        const { btnClass = "blue zip__btn", title, dialog, managedObject, actions, browseProps } = this.props;
        return (<button class={btnClass} type="button" onClick={this.onBrowse.bind(this, dialog, managedObject, actions, browseProps)}>{title}</button>);
    }

    onBrowse(Dialog, managedObject, actions, browseProps, eOpts) {
        try {
            UiView.showDialog(<Provider store={UiView.createDialogStore()}><Dialog managedObject={managedObject} actions={actions} {...browseProps} /></Provider>);
        } catch (ex) {
            Error.showErrors(ex)
        }

    }

    saveObject(requestInfo) {
        return function (editor, updatedManagedObject, successfulCallBack) {
            try {
                // me.props.onSaveRecord(requestInfo, updatedManagedObject, function (updatedObject) {
                //     let originManagedObject = me.state.selection;
                //     updatedObject = me.prepareUpdatedObjectBeforeDisplay(originManagedObject[0], updatedManagedObject, updatedObject);
                //     me.baseGrid.updateRow(originManagedObject[0], updatedObject);
                //     successfulCallBack.call(editor, editor);
                // }, me.props.metaInfo, true);
            } catch (ex) {
                Error.showErrors(ex)
            }
        };
    }
}