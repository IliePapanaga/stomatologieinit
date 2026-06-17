import React from 'react';
import {connect} from 'react-redux';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import {
    CATEGORY_ID_ASSISTANTS,
    CATEGORY_ID_DENTISTS,
    CATEGORY_ID_FRONT_OFFICE_PERSONNEL,
    CATEGORY_ID_HYGIENISTS
} from '../../../../utils/Constants';
import {Logger} from 'react-logger-lib';
import FrontOfficePersonnelForm from "./questionnaire/FrontOfficePersonnelQuestionnaire";
import AssistantQuestionnaireForm from "./questionnaire/AssistantQuestionnaire";
import DentistQuestionnaireForm from "./questionnaire/DentistQuestionnaire";
import HygienistQuestionnaireForm from "./questionnaire/HygienistQuestionnaire";
import {FrontOfficeQuestionnaireModelInput} from "../../../../models/questionnaire/FrontOfficeQuestionnaireModelInput";
import {AssistantQuestionnaireModelInput} from "../../../../models/questionnaire/AssistantQuestionnaireModelInput";
import {DentistQuestionnaireModelInput} from "../../../../models/questionnaire/DentistQuestionnaireModelInput";
import {HygienistQuestionnaireModelInput} from "../../../../models/questionnaire/HygienistQuestionnaireModelInput";


export const dialogInfo = {
    references: ['categories']
}

class Questionnaire extends BaseDialog {
    componentDidMount() {
        try {
            let extendedJs = require('../../../../resources/js/main');
            extendedJs.modalInitialization();
        } catch (ex) {
        }

    }

    dialogProps() {
        return {
            width: 940,
            height: 740,
            className: "modal__gray",
            title: "Edit Questionnaire"
        }
    }

    beforeSave(dialog, managedData) {

        let convertArraysFn = function (parent, arrayName) {
            if (parent[arrayName]) {
                let obj = {};
                parent[arrayName].forEach(function(value){
                    obj[value] = true;
                })
                parent[arrayName] = obj;
            }
        }

        convertArraysFn(managedData.managedObject,'specialtiesFamiliarity');
        convertArraysFn(managedData.managedObject,'duties');

        managedData.managedObject = {questionnaire: new this.model(managedData.managedObject)};


        delete managedData.managedObject.questionnaire['_type_'];
        delete managedData.managedObject.questionnaire['subCategoryName'];
        delete managedData.managedObject.questionnaire['categoryName'];

        // switch (categoryId) {
        //     case CATEGORY_ID_FRONT_OFFICE_PERSONNEL:
        //         break;
        //     case CATEGORY_ID_DENTISTS:
        //         delete managedData.managedObject.questionnaire['specialtiesFamiliarity'];
        //         delete managedData.managedObject.questionnaire['duties'];
        //         break;
        //     case CATEGORY_ID_ASSISTANTS:
        //         break;
        //     case CATEGORY_ID_HYGIENISTS:
        //         break;
        //     default:
        //         delete managedData.managedObject.questionnaire['specialtiesComfort'];
        //         break;
        // }

        Logger.of('App.AddEditLocation.beforeSave').info('ManagedObject data:', managedData);
        return true;
    }

    questionnaireForm = undefined;
    model = undefined;

    initForm() {
        if (!this.questionnaireForm) {

            let categoryId = this.props.references.categories.find(c => c.name === this.props.managedObject.categoryName).id;

            switch (categoryId) {
                case CATEGORY_ID_FRONT_OFFICE_PERSONNEL:
                    this.questionnaireForm = FrontOfficePersonnelForm;
                    this.model = FrontOfficeQuestionnaireModelInput;
                    break;
                case CATEGORY_ID_DENTISTS:
                    this.questionnaireForm = DentistQuestionnaireForm;
                    this.model = DentistQuestionnaireModelInput;
                    break;
                case CATEGORY_ID_ASSISTANTS:
                    this.questionnaireForm = AssistantQuestionnaireForm;
                    this.model = AssistantQuestionnaireModelInput;
                    break;
                case CATEGORY_ID_HYGIENISTS:
                    this.questionnaireForm = HygienistQuestionnaireForm;
                    this.model = HygienistQuestionnaireModelInput;
                    break;
                default:
                    break;
            }
        }
        return this.questionnaireForm;
    }

    renderDialogContent() {

        let Form = this.initForm();
        return (<Form onSubmit={this.onSave} handleCancel={this.close} dialog={this}
                      initialValues={{managedObject: this.props.managedObject}} references={this.props.references} readOnly={this.props.readOnly}/>);
    }
}

const QuestionnaireDialogConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {});
    },
    function (dispatch) {
        return Object.assign(baseDispatcherMap(dispatch), {});
    })(Questionnaire);

export default QuestionnaireDialogConnector;