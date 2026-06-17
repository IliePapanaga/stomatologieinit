import React from 'react';
import {connect} from 'react-redux';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import BaseLocalGrid from '../../common/BaseLocalGrid';
import {Logger} from 'react-logger-lib';
import {DEFAULT_CELL_STYLE, SELECTION_MODE_NONE} from '../../../../utils/Constants';
import Renderer from '../../../../utils/Renderer';


export const dialogInfo = {
    //fields: []
}

class ViewLocations extends BaseDialog {
    dialogProps() {
        return {
            width: 1200,
            height: 820,
            className: "modal__gray sys-offices-locations",
            title: "Locations"
        }
    }

    renderDialogContent() {
        Logger.of('App.ViewLocations.renderDialogContent').info('ManagedObject:', this.props.managedObject);
        const columns = [
            {
                dataIndex: 'id', name: 'id', title: 'Id', hidden: true, cellClass: DEFAULT_CELL_STYLE
            },
            {
                dataIndex: 'name', name: 'name', title: 'Practice Name', sortable: true, cellClass: DEFAULT_CELL_STYLE
            },
            {
                dataIndex: 'contact.name.last', name: 'lastName', title: 'Last Name', sortable: true, cellClass: DEFAULT_CELL_STYLE
            },
            {
                dataIndex: 'contact.name.first', name: 'firstName', title: 'First Name', sortable: true, cellClass: DEFAULT_CELL_STYLE
            },
            {
                dataIndex: 'contact.address.street', name: 'street', title: 'Address', cellClass: DEFAULT_CELL_STYLE,
                renderer(street, cellClass, row, columnIndex, rowIndex) {
                    let value = `${street} ${row.contact.address.city} ${row.contact.address.state}  ${row.contact.address.zipCode}`;
                    return <div class={cellClass}>{value}</div>
                }
            },
            {
                dataIndex: 'contact.email', name: 'email', title: 'Notification Email', sortable: true, cellClass: DEFAULT_CELL_STYLE
            },
            {
                dataIndex: 'contact.phone', name: 'phone', title: 'Office Phone', sortable: true, cellClass: DEFAULT_CELL_STYLE,
                renderer: Renderer.getPhoneRenderer
            }
        ];
        return ([
            <div class="modal__neighbor modal35"></div>,
            <BaseLocalGrid key="base_local_grid" columns={columns} selectionMode={SELECTION_MODE_NONE} nodes={this.props.managedObject.locations} onRef={ref => (this.baseGrid = ref)}  />,
        <div class="footer__btn">
            <button class="blue white" type="button" onClick={this.close}>Cancel</button>
        </div>]);
    }
}

const ViewLocationsDialogConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {
        });
    },
    function (dispatch) {
        return Object.assign(baseDispatcherMap(dispatch), {
            onSend: (sendData, d, form) => { }
        });
    })(ViewLocations);

export default ViewLocationsDialogConnector;
