/*eslint-disable no-unused-vars*/
import React from 'react';
/*eslint-enable no-unused-vars*/
import {connect} from 'react-redux';

import BaseView, {baseViewDispatcherMap, baseViewStateMap} from '../../../common/BaseView';
import {
    BUTTON_TYPE_ADD,
    BUTTON_TYPE_DELETE,
    BUTTON_TYPE_EDIT,
    DEFAULT_CELL_STYLE,
    SELECTION_MODE_SINGLE
} from '../../../../../utils/Constants';
import PaymentMethod, {dialogInfo} from '../../../dialogs/payments/PaymentMethods';
import Renderer from '../../../../../utils/Renderer';
import {toastr} from "react-redux-toastr";

class PaymentMethods extends BaseView {
    recordLabel = "method";

    generateSaveQueryName(managedObject) {
        if (managedObject.ach) {
            return "saveBankAccount";
        } else if (managedObject.card) {
            return "saveCreditCard";
        }
    }

    initView(props) {
        let me = this;

        let configuration = {
            requestInfo: {
                fetchQueryName: 'paymentMethods',
                getQueryName: "paymentMethod",
                addQueryName: me.generateSaveQueryName,
                updateQueryName: me.generateSaveQueryName,
                deleteQueryName: "deletePaymentMethod",
                getResponseModel: "PaymentInstrument",
                addResponseModel: "PaymentInstrument",
                updateResponseModel: "PaymentInstrument",
                reloadAfterAdd:true,
                reloadAfterEdit:true
            },
            columns: [
                {
                    dataIndex: 'id', name: 'id', title: 'Id', hidden: true, cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: '_type_',
                    name: 'type',
                    title: 'Type', /*orderName: 'TYPE',*/
                    cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'label', name: 'label', title: 'Name', orderName: 'LABEL', cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'preferred',
                    name: 'preferred',
                    title: 'Preferred',
                    orderName: 'PREFERRED',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getRequiredRenderer()
                }

            ],
            actions: [
                {label: "Add", type: BUTTON_TYPE_ADD, dialog: PaymentMethod, dialogInfo: dialogInfo},
                {label: "Edit", type: BUTTON_TYPE_EDIT, dialog: PaymentMethod, dialogInfo: dialogInfo},
                {label: "Delete", type: BUTTON_TYPE_DELETE, dialogInfo: dialogInfo},
            ],
            selectionMode: SELECTION_MODE_SINGLE
        }
        return configuration;
    }

    prepareManagedObject(loadedObject) {
        return loadedObject;
    }

    onDeleteObject(dialogInfo, e) {
        let me = this;
        let selection = me.state.selection;

        if (selection.length > 0 && selection[0].preferred) {
            toastr.warning('DELETE', 'Please be informed that you cannot delete the preferred payment method. Please assign other payment method as preferred. After that you can remove it.');
            return false;
        }

        if (me.baseGrid.state.queryResult.count === 1) {
            toastr.warning('DELETE', 'At least one payment method should exist.');
            return false;
        }

        super.onDeleteObject(dialogInfo, e);

    }
}


const PaymentMethodsConnector = connect(
    baseViewStateMap,
    baseViewDispatcherMap)(PaymentMethods);

export default PaymentMethodsConnector;


