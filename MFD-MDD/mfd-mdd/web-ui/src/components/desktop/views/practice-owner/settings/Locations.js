import React from 'react';
import {connect} from 'react-redux';

import BaseView, {baseViewDispatcherMap, baseViewStateMap} from '../../../common/BaseView';
import {
    BUTTON_TYPE_ADD,
    BUTTON_TYPE_DELETE,
    BUTTON_TYPE_EDIT,
    DEFAULT_CELL_STYLE,
    SELECTION_MODE_SINGLE
} from '../../../../../utils/Constants';
import Renderer from '../../../../../utils/Renderer';
import AddEditLocation, {dialogInfo} from '../../../dialogs/location/AddEditLocation';

class Locations extends BaseView {
    initView(props) {
        let configuration = {
            requestInfo: {
                fetchQueryName: 'practiceLocations',
                getQueryName: "practiceLocation",
                addQueryName: "addPracticeLocation",
                addQueryWrapperName: "addPracticeLocation",
                updateQueryName: "updatePracticeLocation",
                updateQueryWrapperName: "updatePracticeLocation",
                deleteQueryName: "deletePracticeLocation",
                getResponseModel: "PracticeLocationModel",
                addResponseModel: "PracticeLocationModel",
                updateResponseModel: "PracticeLocationModel"
            },

            additionalFields: ['contact.name.last', 'contact.address.city', 'contact.address.state', 'contact.address.zipCode'],
            columns: [
                {
                    dataIndex: 'id', name: 'id', title: 'Id', hidden: true, cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'name', name: 'name', title: 'Practice Name', orderName: 'NAME', cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'contact.address.street', name: 'address', title: 'Address', cellClass: DEFAULT_CELL_STYLE,
                    renderer(street, cellClass, row, columnIndex, rowIndex) {
                        let value = `${street} ${row.contact.address.city} ${row.contact.address.state}  ${row.contact.address.zipCode}`;
                        return <div class={cellClass}>{value}</div>
                    }
                },
                {
                    dataIndex: 'contact.name.first', name: 'name', title: 'Contact Person Name', cellClass: DEFAULT_CELL_STYLE,
                    renderer(first, cellClass, row, columnIndex, rowIndex) {
                        let value = `${first}, ${row.contact.name.last}`;
                        return <div class={cellClass}>{value}</div>
                    }
                },
                {
                    dataIndex: 'contact.email', name: 'email', title: 'Email', orderName: 'EMAIL', cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'contact.phone', name: 'phone', title: 'Phone', orderName: 'PHONE', cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getPhoneRenderer
                }
            ],
            actions: [
                { label: "Add", type: BUTTON_TYPE_ADD, dialog: AddEditLocation, dialogInfo: dialogInfo },
                { label: "Edit", type: BUTTON_TYPE_EDIT, dialog: AddEditLocation, dialogInfo: dialogInfo },
                { label: "Delete", type: BUTTON_TYPE_DELETE, dialogInfo: dialogInfo }
            ],
            selectionMode: SELECTION_MODE_SINGLE
        }
        return configuration;
    }
}

const LocationsConnector = connect(
    baseViewStateMap,
    baseViewDispatcherMap)(Locations);

export default LocationsConnector;