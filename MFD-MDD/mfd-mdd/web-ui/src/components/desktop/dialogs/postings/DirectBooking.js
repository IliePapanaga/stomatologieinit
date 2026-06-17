import React from 'react';
import {connect, Provider} from 'react-redux';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import BaseGrid from '../../common/BaseGrid';
import {reduxForm} from 'redux-form';
import {
    DEFAULT_CELL_STYLE,
    NUMBER_CELL_STYLE,
    REST_API_PREFIX,
    REST_API_PREFIX_GET_USER_PHOTO,
    SELECTION_MODE_NONE
} from '../../../../utils/Constants';
import Renderer from "../../../../utils/Renderer";
import {FieldsInfoByModel} from "../../../../models/core/FieldsInfoByModel";
import UiView from "../../../../utils/UiView";
import Remote from "../../../../utils/Remote";
import {getMultyDataPromise} from "../../../../actions/common/getData";
import Error from "../../../../utils/Error";
import ViewProfessional from '../professionals/ViewProfessional';

export const dialogDirectBooking = {
    references: []
};

let Form = props => {
    const {handleSubmit, handleCancel, dialog, onView, initialValues:{managedObject}, selection} = props;
    const columns = [
        {
            dataIndex: 'id',
            name: 'id',
            title: 'Professional Id',
            hidden: true,
            cellClass: DEFAULT_CELL_STYLE
        },

        {
            dataIndex: 'lastName',
            name: 'lastName',
            title: 'Professional Last Name',
            orderName: 'LAST_NAME',
            cellClass: DEFAULT_CELL_STYLE,
            renderer: function (value, cellClass, row, columnIndex, rowIndex) {
                let src = REST_API_PREFIX_GET_USER_PHOTO.replace('{0}', row.id);
                return (<div>
                    <img src={src} onClick={onView.bind(dialog, row)} alt=""/><p onClick={onView.bind(dialog, row)}>{value}</p>
                </div>);
            }
        },
        {
            dataIndex: 'firstName',
            name: 'firstName',
            title: 'Professional First Name',
            orderName: 'FIRST_NAME',
            cellClass: DEFAULT_CELL_STYLE
        },
        {
            dataIndex: 'ratePerHour',
            name: 'ratePerHour',
            title: 'Hourly Rate ($)',
            orderName: 'RPH',
            cellClass: NUMBER_CELL_STYLE
        },
        {
            dataIndex: 'totalRating',
            name: 'totalRating',
            title: 'Rating',
            renderer: Renderer.getRatingRenderer
        }];
    return (
        <form onSubmit={handleSubmit}>

            {/*added class "hide to "modal__date" or "modal__calendar" for table without date or calendar*/}
            <div class="modal__gray-main modal15 modal55">

                <div class="modal__calendar hide">
                    <div>
                        <svg class="pag__icon">
                            <use xlinkHref="#left-arrow"></use>
                        </svg>
                    </div>
                    <div class="start">Dec / 4 / 2017</div>
                    <div class="end">Dec / 10 / 2018</div>
                    <div>
                        <svg class="pag__icon">
                            <use xlinkHref="#right-arrow-end"></use>
                        </svg>
                    </div>
                </div>
                <div class="modal__table">
                    <BaseGrid
                        columns={columns}
                        selectionMode={SELECTION_MODE_NONE}
                        url={REST_API_PREFIX} queryName="directBookingCandidates"
                        additionalFields={[]}
                        params={{
                            requiredSubcategories: managedObject.requiredSubcategories||['NAN'],
                            practiceLocationId: managedObject.practiceLocationId
                        }}
                        onRef={ref => (dialog.applicantsGrid = ref)}
                        hasPagination={true}
                        localOrdering={false}
                        key="base_view_base_grid"/>
                </div>
            </div>
            <div class="footer__btn-wrapper">
                <div class="footer__btn">
                    <button class="blue white" type="button" onClick={handleCancel}>Close</button>
                    <button class="blue" type="button" disabled={selection.length <= 0} onClick={handleSubmit}>Ok</button>
                </div>
            </div>

        </form>)
};

Form = reduxForm({
    form: 'directbooking'
})(Form);


export class DirectBooking extends BaseDialog {

    dialogProps() {
        return {
            width: 1180,
            height: 760,
            className: "modal__gray",
            title: "Direct Booking Candidate"
        }
    }

    renderDialogContent() {
        let selection = this.applicantsGrid ? this.applicantsGrid.state.selection : [];
        return (<Form onSubmit={this.onSave} handleCancel={this.close} dialog={this}
                      initialValues={{managedObject: this.props.managedObject}} references={this.props.references}
                      onCancel={this.onCancel} selection={selection} onView={this.onView} metaInfo={this.props.metaInfo}/>);
    }

    onView(applicant, event) {
        let me = this;
        try {
            let requestFields = [];
            ["ProfessionalModel", "ProfessionalSubcategoryModelConnection", "RequiredCertificateConnection", "Questionnaire"].forEach(getResponseModel => {
                let tmpRequestFields = undefined;
                if (getResponseModel instanceof FieldsInfoByModel) {
                    tmpRequestFields = Remote.getFieldsByModel(me.props.metaInfo, getResponseModel.model, getResponseModel.ignoreFields, getResponseModel.modelMode);
                } else {
                    tmpRequestFields = Remote.getFieldsByModel(me.props.metaInfo, getResponseModel, [], false);
                }
                requestFields.push(tmpRequestFields);
            });

            getMultyDataPromise(
                ["professional", "professionalSubcategoriesByProfessionalId", "professionalRequiredCertificatesByProfessionalId ","getQuestionnaire"],
                [{id: applicant.id}, {professionalId: applicant.id}, {professionalId: applicant.id}, {category:me.props.managedObject.category, professional: applicant.id}],
                requestFields).then(
                function (loadedObject) {

                    let managedObject = loadedObject.professional;
                    managedObject.profile = managedObject.profile || {workReferences: [], workExperiences: []};
                    managedObject.professionalSubcategories = loadedObject.professionalSubcategoriesByProfessionalId.nodes;
                    managedObject.certificates = loadedObject.professionalRequiredCertificatesByProfessionalId.nodes;
                    managedObject.getQuestionnaire = loadedObject.getQuestionnaire||{};

                    UiView.showDialog(<Provider store={UiView.createDialogStore()}><ViewProfessional
                    managedObject={managedObject}
                    references={me.props.references}
                    metaInfo={me.props.metaInfo}/></Provider>);
                });
        } catch (ex) {
            Error.showErrors(ex)
        }
    }
}

const DirectBookingConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {});
    },
    function (dispatch) {
        return Object.assign(baseDispatcherMap(dispatch), {});
    })(DirectBooking);

export default DirectBookingConnector;