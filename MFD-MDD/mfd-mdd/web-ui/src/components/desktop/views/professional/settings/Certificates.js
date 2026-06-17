import React from 'react';
import {connect, Provider} from 'react-redux';
import UiView from '../../../../../utils/UiView';
import Remote from '../../../../../utils/Remote';
import Error from '../../../../../utils/Error';
import BaseView, {baseViewDispatcherMap, baseViewStateMap} from '../../../common/BaseView';
import {
    BUTTON_TYPE_EDIT,
    DEFAULT_CELL_STYLE,
    SELECTION_MODE_SINGLE,
    STATUS_PENDING
} from '../../../../../utils/Constants';

import UploadCertificate, {dialogInfo as dialogInfoUploadCertificate} from '../../../dialogs/specialty/UploadCertificate';
import Renderer from '../../../../../utils/Renderer';

class Specialties extends BaseView {
    initView(props) {
        let configuration = {
            additionalFields: [],
            requestInfo: {
                fetchQueryName: 'professionalRequiredCertificates',
                getQueryName: "certificateDetails",
                addQueryName: "addProfessionalSubcategories",
                updateQueryName: "updateProfessionalSubcategories",
                deleteQueryName: "deleteProfessionalSubcategory",
                getResponseModel: "CertificateDetailsModel",
                //getResponseModelMode: 'input',
                //addResponseModel: "PracticeLocationModel",
                updateResponseModel: undefined
            },

            columns: [
                {
                    dataIndex: 'certificateId',
                    name: 'certificateId',
                    title: 'Id',
                    hidden: true,
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.clearUndersores()

                },
                {
                    dataIndex: 'type',
                    name: 'type',
                    title: 'Type',
                    cellClass: DEFAULT_CELL_STYLE,
                    orderName: 'TYPE',
                    renderer: Renderer.getCertificateTypeTitle()
                },
                {
                    dataIndex: 'status', name: 'status', title: 'Status', cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getStatusRenderer()
                },
                {
                    dataIndex: 'optional', name: 'optional', title: 'Required', cellClass: DEFAULT_CELL_STYLE,
                    orderName: 'OPTIONAL',
                    renderer: Renderer.getRequiredRenderer(true)
                }
            ],
            actions: [
                {
                    label: "Upload/Edit certificate",
                    type: BUTTON_TYPE_EDIT,
                    dialog: UploadCertificate,
                    dialogInfo: dialogInfoUploadCertificate
                },
                {
                    label: "View certificate",
                    type: BUTTON_TYPE_EDIT,
                    dialog: UploadCertificate,
                    dialogInfo: dialogInfoUploadCertificate,
                    name: 'viewCertificate'
                }
            ],
            selectionMode: SELECTION_MODE_SINGLE
        }
        return configuration;
    }

    prepareUpdatedObjectBeforeDisplay(oldObject, forUpdateObject, newObject) {
        let data = undefined;

        return data;
    }

    onEditObject(Dialog, dialogInfo, e) {
        let me = this;
        const target = e.target.parentElement;
        let selection = me.state.selection;
        if (selection.length > 0) {
            try {

                let showDialog = function (selection, certificateDetails) {
                    let managedObject = {certificateDetails: certificateDetails, certificate: selection[0]};
                    let actions = {
                        save: function (editor, updatedManagedObject, successfulCallBack) {
                            let originManagedObject = me.state.selection;
                            let updatedObject = {};
                            Object.assign(updatedObject, originManagedObject[0]);
                            Object.assign(updatedObject, updatedManagedObject.certificateDetails);
                            updatedObject.certificateId = updatedObject.id;
                            me.baseGrid.updateRow(originManagedObject[0], updatedObject);
                            editor.close();
                        }
                    }

                    UiView.showDialog(<Provider
                        store={UiView.createDialogStore()}><Dialog {...me.dialogProps(Dialog, target)}
                                                                   managedObject={managedObject}
                                                                   references={me.props.references}
                                                                   metaInfo={me.props.metaInfo}
                                                                   actions={actions}/></Provider>);
                }
                if (selection[0].certificateId) {
                    let requestFields = dialogInfo.fields || Remote.getFieldsByModel(me.props.metaInfo, me.requestInfo.getResponseModel, [], me.requestInfo.getResponseModelMode === 'input');
                    me.props.onLoadRecord({
                        queryName: me.requestInfo.getQueryName,
                        requestFields: requestFields
                    }, {id: selection[0].certificateId}, function (certificateDetails) {
                        me.props.onLoadRefs(dialogInfo.references || [], me.props.references, me.props.metaInfo, function () {
                            showDialog(selection, certificateDetails);
                        });
                    });
                } else {
                    showDialog(selection, {});
                }
            } catch (ex) {
                Error.showErrors(ex)
            }
        }
    }

    dialogProps(dlg, button) {
        switch (button.getAttribute('action-name')) {
            case 'viewCertificate':
                return {readOnly: true};
            default:
                return null;
        }
    }

    isEnabledAction(element, selection) {
        let result = super.isEnabledAction(element, selection);

        if (result) {
            switch (element.type) {
                case BUTTON_TYPE_EDIT:
                    return selection.length > 0 && (element.name !== "viewCertificate" || selection[0].status !== STATUS_PENDING);
                default:
                    break;
            }
        }
        return result;
    }
}

const SpecialtiesConnector = connect(
    function (state, ownProps) {
        let userId = state.context.currentUser ? state.context.currentUser.id : undefined;
        return Object.assign(baseViewStateMap(state, ownProps), {
            userId: userId,
            metaInfo: state.context.metaInfo
        });
    },
    baseViewDispatcherMap)(Specialties);

export default SpecialtiesConnector;