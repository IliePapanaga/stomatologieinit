import React from 'react';
import {LocalGrid} from '../BaseLocalGrid';
import {Actions} from '../BaseActions';
import {BUTTON_TYPE_DELETE, BUTTON_TYPE_EDIT, SELECTION_MODE_SINGLE} from '../../../../utils/Constants';
import ObjectHelper from '../../../../utils/Object';
import UiView from '../../../../utils/UiView';
import {Provider} from 'react-redux';
import {toastr} from 'react-redux-toastr';


export default class GridField extends LocalGrid {

    constructor(props) {
        super(props);
        const {meta, columns} = this.props;
        const nodes = meta.initial || [];

        this.state = {
            columns: columns,
            selection: [],
            selectionMode: SELECTION_MODE_SINGLE,
            nodes: nodes,
            orders: this.configureOrders(columns),
            orderInfo: undefined
        }
    }

    render() {
        let grid = super.render();
        const {actions = []} = this.props;
        const {selection} = this.state;
        return (
            [<div class="data">
                <Actions
                    selection={selection}
                    actions={actions}
                    baseView={this}
                    key="grid_actions"
                    ref={(el) => {
                        this.baseActions = el
                    }}/>
            </div>,
                <div class="modal__table">
                    {grid}
                </div>]
        );
    }

    isEnabledAction(element, selection) {
        let result = true;
        switch (element.type) {
            case BUTTON_TYPE_EDIT:
            case BUTTON_TYPE_DELETE:
                result = selection && selection.length === 1;
                break;
            default:
                break;
        }

        if (result) {
            if (element.allowedValues && element.allowedByFieldName) {
                result = selection && selection.length === 1 && element.allowedValues.includes(selection[0][element.allowedByFieldName])
            }
        }
        if (result) {
            if (element.disallowedValues && element.disallowedByFieldName) {
                result = selection && selection.length === 1 && !element.disallowedValues.includes(selection[0][element.disallowedByFieldName])
            }
        }

        if (result && element.hasSelectedRows) {
            result = element.hasSelectedRows === selection.length;
        }
        if (result && element.isEnabled) {
            result = element.isEnabled(selection);
        }
        return result;
    }


    onAddObject(Dialog, dialogInfo, button) {
        let me = this;

        let actions = {
            save: me.addObject(me.requestInfo).bind(me)
        }
        UiView.showDialog(<Provider store={UiView.createDialogStore()}><Dialog managedObject={{}}
                                                                               actions={actions}/></Provider>);
    }

    addObject(requestInfo) {
        return function (editor, newManagedObject, successfulCallBack) {}
    }

    onEditObject(Dialog, dialogInfo, button) {
        let me = this;
        const {selection} = me.state;
        if (selection.length > 0) {
            try {
                if (ObjectHelper.isObject(selection[0])) {

                    let actions = {
                        save: me.saveObject(me.requestInfo).bind(me)
                    }
                    UiView.showDialog(<Provider store={UiView.createDialogStore()}><Dialog managedObject={selection[0]}
                                                                                           parent={me}
                                                                                           actions={actions}/></Provider>);


                }
            } catch (ex) {
                Error.showErrors(ex)
            }
        }
    }

    saveObject(requestInfo) {
        return function (editor, updatedManagedObject, successfulCallBack) {};
    }

    onDeleteObject(dialogInfo, button) {
        let me = this;
        const {selection} = me.state;
        if (selection.length > 0) {
            const toastrConfirmOptions = {
                onOk: function () {
                    try {
                        // me.props.onDeleteRecord(me.requestInfo, selection[0], function (success) {
                        //     me.baseGrid.deleteRows(selection);
                        // });
                    } catch (ex) {
                        Error.showErrors(ex)
                    }

                },
                onCancel: () => console.log('CANCEL: clicked')
            };
            toastr.confirm(<div className="modal57">
                <div className="header activate"><h2>Delete</h2></div>
                <div className="body">Are you sure you want to delete the selection?</div></div>, toastrConfirmOptions);
        }
    }
}