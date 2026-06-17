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

import {TRANSPORT_TYPES} from '../../../../../data/TransportType'

import AddEditTemplate, {dialogInfo} from '../../../dialogs/templates/AddEditTemplate';
import {findStatus} from "../../../../../data/Statuses";

class Templates extends BaseView {

    initView(props) {
        let me = this,
            configuration = {
                requestInfo: {
                    fetchQueryName: 'notificationTemplates',
                    getQueryName: "notificationTemplate",
                    addQueryName: "addNotificationTemplate",
                    updateQueryName: "updateNotificationTemplate",
                    deleteQueryName: "deleteNotificationTemplate",
                    getResponseModel: "NotificationTemplateModel",
                    addResponseModel: "NotificationTemplateModel",
                    updateResponseModel: "NotificationTemplateModel"
                },

                additionalFields: [],
                columns: [
                    {
                        dataIndex: 'id', name: 'id', title: 'Id', hidden: true, cellClass: DEFAULT_CELL_STYLE
                    },
                    {
                        dataIndex: 'type',
                        name: 'type',
                        title: 'Notification Type',
                        orderName: 'NAME',
                        cellClass: DEFAULT_CELL_STYLE,
                        renderer: function (value, cellClass, row, columnIndex, rowIndex) {
                            let type = findStatus(value, me.notificationTypes);
                            return <div class={cellClass}>{type ? type.name : 'N/A'}</div>;
                        }
                    },
                    {
                        dataIndex: 'description',
                        name: 'description',
                        title: 'Description of Notification Type',
                        orderName: 'DESCRIPTION',
                        cellClass: DEFAULT_CELL_STYLE
                    },
                    {
                        dataIndex: 'transport',
                        name: 'transport',
                        title: 'Transport Type',
                        orderName: 'TRANSPORT',
                        cellClass: DEFAULT_CELL_STYLE,
                        renderer: function (value, cellClass, row, columnIndex, rowIndex) {
                            let status = findStatus(value, TRANSPORT_TYPES);
                            return <div><span class={cellClass}>{status ? status.name : 'N/A'}</span></div>;
                        }
                    },
                ],
                actions: [
                    {label: "Add", type: BUTTON_TYPE_ADD, dialog: AddEditTemplate, dialogInfo: dialogInfo},
                    {label: "Edit", type: BUTTON_TYPE_EDIT, dialog: AddEditTemplate, dialogInfo: dialogInfo},
                    {label: "Delete", type: BUTTON_TYPE_DELETE},
                ],
                /*  filters: [
                  ],*/
                selectionMode: SELECTION_MODE_SINGLE
            };
        me.notificationTypes = me.notificationTypes || [];
        return configuration;
    }


    onLoad() {
        let me = this;
        me.props.onLoadRefs(['notificationTypes'], me.props.references, me.props.metaInfo, function (result) {
            try {
                if (me.props.references.notificationTypes && me.props.references.notificationTypes.nodes) {
                    me.props.references.notificationTypes.nodes.forEach(function (n) {
                        me.notificationTypes.push({code: n.type, name: n.name});
                    })
                }
                me.initBaseFilters();
                me.setInitialized(true);
            } catch (ex) {}
        });
    }

    prepareManagedObject(loadedObject, target) {
        return loadedObject;
    }
}

const TemplatesConnector = connect(
    baseViewStateMap,
    baseViewDispatcherMap)(Templates);

export default TemplatesConnector;