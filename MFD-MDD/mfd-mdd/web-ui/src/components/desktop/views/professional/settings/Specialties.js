import React from 'react';
import {connect, Provider} from 'react-redux';
import UiView from '../../../../../utils/UiView';
import Remote from '../../../../../utils/Remote';
import Error from '../../../../../utils/Error';
import {FrontOfficeQuestionnaireModelInput} from "../../../../../models/questionnaire/FrontOfficeQuestionnaireModelInput";
import {AssistantQuestionnaireModelInput} from "../../../../../models/questionnaire/AssistantQuestionnaireModelInput";
import {DentistQuestionnaireModelInput} from "../../../../../models/questionnaire/DentistQuestionnaireModelInput";
import loadData from "../../../../../actions/common/loadData";

import BaseView, {addObject, baseViewDispatcherMap, baseViewStateMap, saveObject} from '../../../common/BaseView';
import {
    BUTTON_TYPE_ADD,
    BUTTON_TYPE_DELETE,
    BUTTON_TYPE_EDIT,
    DEFAULT_CELL_STYLE, REST_API_PREFIX,
    SELECTION_MODE_SINGLE, STATUS_BOOKED, STATUS_INVITED, STATUS_SCHEDULED, STATUS_ACCEPTED
} from '../../../../../utils/Constants';

import AddEditSpecialty, {dialogInfo} from '../../../dialogs/specialty/AddEditSpecialty';
import Questionnaire, {dialogInfo as dialogInfoQuestionnaire} from '../../../dialogs/specialty/Questionnaire';
import Renderer from '../../../../../utils/Renderer';
import {toastr} from "react-redux-toastr";
import {Field as QueryField, Pagination, Query, QueryResult} from "../../../../../models/core/QueryInfo";

class Specialties extends BaseView {

    generateUpdateQueryName(managedObject) {
        if (managedObject.questionnaire instanceof FrontOfficeQuestionnaireModelInput) {
            return "editFrontOfficeQuestionnaire";
        } else if (managedObject.questionnaire instanceof AssistantQuestionnaireModelInput) {
            return "editAssistantQuestionnaire";
        } else if (managedObject.questionnaire instanceof DentistQuestionnaireModelInput) {
            return "editDentistQuestionnaire";
        }
        return "editHygienistQuestionnaire";
    }

    initView(props) {
        let me = this;
        let configuration = {
            additionalFields: [],
            requestInfo: {
                fetchQueryName: 'professionalSubcategories',
                getQueryName: "getQuestionnaire",
                addQueryName: "addProfessionalSubcategories",
                updateQueryName: me.generateUpdateQueryName,
                deleteQueryName: "deleteProfessionalSubcategory",
                getResponseModel: "Questionnaire",
                //getResponseModelMode: 'input',
                //addResponseModel: "PracticeLocationModel",
                updateResponseModel: undefined
            },

            columns: [
                {
                    dataIndex: 'id',
                    name: 'id',
                    title: 'Id',
                    hidden: true,
                    cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'categoryName',
                    name: 'categoryName',
                    title: 'Category',
                    orderName: 'CATEGORY_NAME',
                    cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'subCategoryName',
                    name: 'subCategoryName',
                    title: 'Sub-category',
                    orderName: 'SUBCATEGORY_NAME',
                    cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'status',
                    name: 'status',
                    title: 'Status',
                    cellClass: DEFAULT_CELL_STYLE,
                    orderName: 'STATUS',
                    renderer: Renderer.getStatusRenderer()
                }
            ],
            actions: [
                {
                    label: "Add Specialty",
                    type: BUTTON_TYPE_ADD,
                    dialog: AddEditSpecialty,
                    dialogInfo: dialogInfo
                },
                /*{
                    label: "Edit Specialty",
                    type: BUTTON_TYPE_EDIT,
                    dialog: AddEditSpecialty,
                    dialogInfo: dialogInfo
                },*/
                {label: "Delete Specialty", type: BUTTON_TYPE_DELETE, dialogInfo: dialogInfo},

                {
                    label: "Edit Questionnaire",
                    type: BUTTON_TYPE_EDIT,
                    dialog: Questionnaire,
                    dialogInfo: dialogInfoQuestionnaire
                }
            ],
            selectionMode: SELECTION_MODE_SINGLE
        }
        return configuration;
    }

    onAddObject(Dialog, dialogInfo, button) {
        let me = this;

        me.props.onLoadRefs(dialogInfo.references || [], me.props.references, me.props.metaInfo, function () {
            let actions = {
                save: addObject(me.requestInfo).bind(me)
            }
            let gridData = me.baseGrid.getData();
            let addedSubCategories = [];

            gridData.forEach(element => {
                addedSubCategories.push(element.id);
            });

            let managedObject = {
                category: me.props.references.categories[0].id,
                addedSubCategories: addedSubCategories
            }

            UiView.showDialog(<Provider store={UiView.createDialogStore()}><Dialog managedObject={managedObject}
                                                                                   actions={actions}
                                                                                   references={me.props.references}
                                                                                   metaInfo={me.props.metaInfo}/></Provider>);
        });


    }

    onEditObject(Dialog, dialogInfo, button) {
        let me = this;
        let selection = me.state.selection;
        if (selection.length > 0) {
            try {
                let requestFields = dialogInfo.fields || Remote.getFieldsByModel(me.props.metaInfo, me.requestInfo.getResponseModel, [], me.requestInfo.getResponseModelMode === 'input');

                let refs = dialogInfo.references || [];

                if (refs.includes('categories')) {
                    refs.push('categories');
                }

                me.props.onLoadRefs(refs, me.props.references, me.props.metaInfo, function () {
                    me.props.onLoadRecord({
                        queryName: me.requestInfo.getQueryName,
                        requestFields: requestFields
                    }, {id: {category: me.props.references.categories.find(c => c.name === selection[0].categoryName).id}}, function (object) {

                        let actions = {
                            save: saveObject(me.requestInfo).bind(me)
                        }
                        let managedObject = me.prepareManagedObject(object) || {};
                        managedObject.subCategoryName = selection[0].subCategoryName;
                        managedObject.categoryName = selection[0].categoryName;

                        UiView.showDialog(<Provider
                            store={UiView.createDialogStore()}><Dialog {...me.dialogProps(Dialog)}
                                                                       managedObject={managedObject}
                                                                       actions={actions}
                                                                       references={me.props.references}
                                                                       metaInfo={me.props.metaInfo}/></Provider>);
                    });
                });
            } catch (ex) {
                Error.showErrors(ex)
            }
        }
    }

    onDeleteObject(dialogInfo, e) {
        let me = this;
        let selection = me.state.selection;

        if (selection.length > 0) {
            const name=selection[0].id;

            let has = false;


            Promise.all([
                this.createcheckPostingRequest("professionalPermanentJobPostings", STATUS_INVITED),
                this.createcheckPostingRequest("professionalPermanentJobPostings", STATUS_SCHEDULED),

            ]).then((res: QueryResult[]) => {

                res.forEach((q: QueryResult) => {
                    const nodes = q.nodes;
                    if (nodes.nodes && nodes.nodes.length > 0) {
                        has = nodes.nodes.find(n=>n.name.indexOf(`${name}\\`)>=0);
                        if(has){
                            return false;
                        }
                    }
                });
                if (!has) {
                    Promise.all([
                        this.createcheckPostingRequest("professionalTemporaryJobPostings", STATUS_BOOKED),
                        this.createcheckPostingRequest("professionalTemporaryJobPostings", STATUS_ACCEPTED),
                        this.createcheckPostingRequest("professionalPermanentJobPostings", STATUS_BOOKED),
                        this.createcheckPostingRequest("professionalPermanentJobPostings", STATUS_ACCEPTED)
                    ]).then((res: QueryResult[]) => {
                        res.forEach((q: QueryResult) => {
                            const nodes = q.nodes;
                            if (nodes.nodes && nodes.nodes.length > 0) {
                                has = nodes.nodes.find(n=>n.name.indexOf(`${name}\\`)>=0);
                                if(has){
                                    return false;
                                }
                            }
                        });
                        if (has) {
                            toastr.warning('DELETE', 'You need to decline your existing job offers first.');
                        } else {
                            super.onDeleteObject(dialogInfo, e);
                        }
                    });
                } else {
                    toastr.warning('DELETE', 'You need to decline your existing invitations first.');
                }

            });

        }

        return false;

    }

    createcheckPostingRequest(queryName: string, status: string): Promise {
        const query = new Query({
            endpoint: REST_API_PREFIX,
            name: queryName,
            select: [new QueryField("nodes.name")],
            parameters: {status: status},
            pagination: new Pagination({
                allowed: false,
                page: 0
            })
        });
        return loadData(query);
    }

    prepareManagedObject(loadedObject) {
        let convertToArraysFn = function (parent, objectName) {
            if (parent[objectName]) {
                let arr = [];
                for (var key in parent[objectName]) {
                    if (parent[objectName][key])
                        arr.push(key);
                }
                parent[objectName] = arr;
            }
        }

        convertToArraysFn(loadedObject, 'specialtiesFamiliarity');
        convertToArraysFn(loadedObject, 'duties');


        return loadedObject;
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