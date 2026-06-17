import React from 'react';
import {connect} from 'react-redux';
import {reduxForm} from 'redux-form';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import Field from '../../common/form/Field';

import {
    DEFAULT_CELL_STYLE
} from '../../../../utils/Constants';
import {PAYMENTS} from '../../../../data/Statuses';
import Renderer from '../../../../utils/Renderer';
import GridField from '../../common/form/GridField';

export const dialogInfo = {}

let Form = props => {
    const {handleSubmit, handleCancel} = props;

    return <form onSubmit={handleSubmit}>
        <div class="modal__gray-main modal58">
            <div class="modal__table">
                <Field
                    name="managedObject.paymentDetails.steps"
                    columns={[
                        {
                            dataIndex: 'date',
                            name: 'date',
                            title: 'Date',
                            cellClass: DEFAULT_CELL_STYLE,
                            sortable: true,
                            renderer: Renderer.getDateRenderer
                        },
                        {
                            dataIndex: 'status',
                            name: 'status',
                            title: 'Status',
                            cellClass: DEFAULT_CELL_STYLE,
                            sortable: true,
                            renderer: Renderer.getStatusRenderer(PAYMENTS)
                        },
                        {
                            dataIndex: 'method',
                            name: 'method',
                            title: 'Payment Method',
                            cellClass: DEFAULT_CELL_STYLE,
                            sortable: true
                        },
                        {
                            dataIndex: 'amount',
                            name: 'amount',
                            title: 'Amount',
                            cellClass: DEFAULT_CELL_STYLE,
                            sortable: true,
                            renderer: Renderer.getCoastRenderer
                        },
                        {
                            dataIndex: 'gatewayMessage',
                            name: 'gatewayMessage',
                            title: 'Message',
                            cellClass: DEFAULT_CELL_STYLE,
                            sortable: true
                        },
                        {
                            dataIndex: 'gatewayId',
                            name: 'gatewayId',
                            title: 'Gateway ID',
                            cellClass: DEFAULT_CELL_STYLE,
                            sortable: true
                        },
                        {
                            dataIndex: 'log',
                            name: 'log',
                            title: 'Comment',
                            cellClass: DEFAULT_CELL_STYLE,
                            sortable: true
                        }
                    ]}
                    component={GridField}/>
            </div>
        </div>

        <div class="footer__btn-wrapper">
            <div class="footer__btn">
                <button class="blue white" type="button" onClick={handleCancel}>Cancel</button>
            </div>
        </div>

    </form>
}

Form = reduxForm({
    form: 'viewpayment'
})(Form);


class ViewPayment extends BaseDialog {


    dialogProps() {
        return {
            width: 960,
            height: 700,
            className: "modal__gray",
            title: "View Payments Details"
        }
    }

    /**
     * TODO change component for skiping this logic
     */
    convertData(managedObject) {
        return managedObject;
    }

    /**
     * TODO change component for skiping this logic
     */

    renderDialogContent() {
        return (<Form onSubmit={this.onSave} handleCancel={this.close} dialog={this}
                      initialValues={{managedObject: this.props.managedObject}} references={this.props.references}/>);
    }
}

const
    ViewPaymentDialogConnector = connect(
        function (state, ownProps) {
            return Object.assign(baseStateMap(state, ownProps), {});
        },
        function (dispatch) {
            return Object.assign(baseDispatcherMap(dispatch), {})
        }
    )(ViewPayment);

export default ViewPaymentDialogConnector;
