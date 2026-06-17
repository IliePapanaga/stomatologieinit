import React from 'react';
import {connect, Provider} from 'react-redux';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import BaseGrid from '../../common/BaseGrid';
import {reduxForm} from 'redux-form';
import DateHelper, {serverShortDateFormat, uiDateFormatMonthDayYear} from '../../../../utils/DateHelper';
import {
    DEFAULT_CELL_STYLE,
    EVENT_VIEW_SAVE_DATA,
    NUMBER_CELL_STYLE,
    REST_API_PREFIX,
    REST_API_PREFIX_GET_USER_PHOTO,
    SELECTION_MODE_NONE,
    STATUS_BOOKED,
    STATUS_NEW
} from '../../../../utils/Constants';
import UiView from '../../../../utils/UiView';
import {getMultyDataPromise} from "../../../../actions/common/getData";
import Remote from "../../../../utils/Remote";
import Error from "../../../../utils/Error";
import {toastr} from "react-redux-toastr";
import saveData from "../../../../actions/common/saveData";
import {FieldsInfoByModel} from "../../../../models/core/FieldsInfoByModel";
import ViewProfessional from '../professionals/ViewProfessional';
import Renderer from "../../../../utils/Renderer";

export const dialogViewApplicants = {
    references: ['categories', 'languages', 'educations', 'academicDegrees']
}

let Form = props => {
    const {handleSubmit, handleCancel, onBook, onCancel, onView, dialog, readOnly} = props;
    const managedObject = props.initialValues.managedObject;
    const postingSpecialties = managedObject.name ? managedObject.name.toLowerCase().split('\\') : null;
    if(postingSpecialties){
        postingSpecialties.forEach(s=>postingSpecialties.push(s.replace('_',' ')))
    }
    const columns = [
        {
            dataIndex: 'professionalId',
            name: 'professionalId',
            title: 'ProfessionalId',
            hidden: true,
            cellClass: DEFAULT_CELL_STYLE
        },

        {
            dataIndex: 'lastName',
            name: 'lastName',
            title: 'Last Name',
            orderName: 'LAST_NAME',
            cellClass: DEFAULT_CELL_STYLE,
            renderer: function (value, cellClass, row, columnIndex, rowIndex) {
                let src = REST_API_PREFIX_GET_USER_PHOTO.replace('{0}', row.professionalId);
                return (<div class="link__text">
                    <img src={src} onClick={onView.bind(dialog, row)} alt=""/><p onClick={onView.bind(dialog, row)}>{value}</p>
                </div>);
            }
        },
        {
            dataIndex: 'firstName',
            name: 'firstName',
            title: 'First Name',
            orderName: 'FIRST_NAME',
            cellClass: DEFAULT_CELL_STYLE
        },
        {
            dataIndex: 'specialty',
            name: 'specialty',
            title: 'Specialty',
            cellClass: DEFAULT_CELL_STYLE,
            renderer: function (value, cellClass, row, columnIndex, rowIndex) {
                const applicantSpecialties=value.split(',');
                const commonSpecialties = applicantSpecialties.filter(function(value) {
                    return value && postingSpecialties ? postingSpecialties.indexOf(value.toLowerCase()) > -1 : false;
                });
                return <div class={cellClass}>{commonSpecialties.join(', ')}</div>;
            }
        },
        {
            dataIndex: 'rph',
            name: 'rph',
            title: 'Rate($)',
            orderName: 'RPH',
            cellClass: NUMBER_CELL_STYLE
        },
        {
            dataIndex: 'rating',
            name: 'rating',
            title: 'Rating',
            cellClass: DEFAULT_CELL_STYLE,
            renderer: Renderer.getRatingRenderer
        }];
//"DD ddd"

    let jobDays = DateHelper.getJobDays(managedObject.startDate, managedObject.endDate, serverShortDateFormat);

    jobDays.forEach(function (jobDay) {
        columns.push({
            dataIndex: 'id',
            name: 'id',
            title: DateHelper.convertServerDateStringToString(jobDay.date, uiDateFormatMonthDayYear, true),
            cellClass: DEFAULT_CELL_STYLE,
            renderer: function (value, cellClass, row, columnIndex, rowIndex) {
                //let dateClass = "dates__empty";
                let dateClass = "dates__disabled";

                if (managedObject.zonedJobDays) {
                    if (managedObject.zonedJobDays.find(zonedJobDay => zonedJobDay.date === jobDay.date && !zonedJobDay.excluded)) {
                        dateClass = "dates__required";
                    }
                }

                if (row.workingDays) {
                    if (row.workingDays.find(workingDay => workingDay.date === jobDay.date && !workingDay.excluded)) {
                        dateClass = "dates__applied";
                    }
                }
                // "dates__disabled"

                return (
                    <div>
                        <div class={dateClass}></div>
                    </div>)
            }
        });
    });

    if (!readOnly) {
        columns.push({
            dataIndex: 'id',
            name: 'id',
            title: 'Booking',
            cellClass: DEFAULT_CELL_STYLE,
            renderer: function (value, cellClass, row, columnIndex, rowIndex) {
                return (
                    <div>
                        {row.status === STATUS_NEW &&
                        <button class="blue" type="button" disabled={row.status !== STATUS_NEW}
                                onClick={onBook.bind(dialog, row)}>Book
                        </button>}
                        {row.status === STATUS_BOOKED && <p class="row__status-booked modal__table-status">Invitation sent</p>}
                        <button class="white blue" type="button"
                                onClick={onCancel.bind(dialog, row)}>Cancel
                        </button>
                    </div>)
            }
        });
    }
    return (
        <form onSubmit={handleSubmit}>

            {/*added class "hide to "modal__date" or "modal__calendar" for table without date or calendar*/}
            <div class="modal__gray-main modal15">

                <div class="modal__date">
                    <div class="modal__date-item">
                        <div class="item__box">
                            <div class="title">
                                <p>Starts:</p>
                            </div>
                            <div class="data">
                                <p>{DateHelper.convertServerDateStringToString(managedObject.startDate, "MMM Do YYYY")}</p>
                            </div>
                        </div>
                    </div>
                    <div class="modal__date-item">
                        <div class="item__box">
                            <div class="title">
                                <p>Ends by:</p>
                            </div>
                            <div class="data">
                                <p>{DateHelper.convertServerDateStringToString(managedObject.endDate, "MMM Do YYYY")}</p>
                            </div>
                        </div>
                    </div>
                </div>

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
                        url={REST_API_PREFIX} queryName="temporaryPostingApplicants"
                        additionalFields={['status', 'workingDays{date excluded}']}
                        params={{postingId: managedObject.id}}
                        onRef={ref => (dialog.applicantsGrid = ref)}
                        hasPagination={true}
                        localOrdering={false}
                        key="base_view_base_grid"/>
                </div>

                <div class="modal__legend">
                    <div class="modal__legend-item">
                        <div class="dates__applied"></div>
                        <p class="text"> — Days Applied for</p>
                    </div>

                    <div class="modal__legend-item">
                        <div class="dates__required"></div>
                        <p class="text"> — Days not Available</p>
                    </div>

                    <div class="modal__legend-item">
                        <div class="dates__disabled"></div>
                        <p class="text"> — Dates not required for the job</p>
                    </div>
                </div>
            </div>


            <div class="footer__btn-wrapper">
                <div class="footer__btn">
                    <button class="blue white" type="button" onClick={handleCancel}>Close</button>
                </div>
            </div>

        </form>)
}

Form = reduxForm({
    form: 'viewapplicants'
})(Form);


export class ViewApplicantsDialog extends BaseDialog {

    dialogProps() {
        return {
            width: 1180,
            height: 760,
            className: "modal__gray",
            title: "Applicants For Specialty/Location"
        }
    }

    renderDialogContent() {
        return (<Form onSubmit={this.onSave} handleCancel={this.onClose} dialog={this}
                      initialValues={{managedObject: this.props.managedObject}} references={this.props.references}
                      onBook={this.onBook} onCancel={this.onCancel} onView={this.onView}
                      readOnly={this.props.readOnly}/>);
    }

    onExecuteOperation(button, managedObject, confirmationMessage, queryName, callBack) {
        let me = this;
        const toastrConfirmOptions = {
            title: 'ewewe',
            onOk: function () {
                try {
                    me.props.onExecuteOperation(queryName, managedObject, callBack);
                } catch (ex) {
                    Error.showErrors(ex)
                }

            },
            onCancel: () => console.log('CANCEL: clicked')
        };
        toastr.confirm(confirmationMessage, toastrConfirmOptions);
    }

    onBook(row, ev) {
        let me = this;
        let selection = [row];
        if (selection.length > 0) {
            me.onExecuteOperation(ev,
                {applicationId: selection[0].id},
                <div class="modal57">
                    <div class="header important"><h2>Hire this candidate</h2></div>
                    <div class="body">
                        Please confirm you are inviting {selection[0].firstName} {selection[0].lastName} for "{me.getPostingSpeciality()}" job</div></div>,
                'bookApplication',
                function (returnObject) {
                    console.log(returnObject);
                    let updatedObject = Object.assign({}, selection[0]);
                    updatedObject.status = STATUS_BOOKED;
                    me.applicantsGrid.updateRow(selection[0], updatedObject);
                    me.applicantsGrid.onRefresh();
                });
        }
    }

    onCancel(row, ev) {
        let me = this;
        let selection = [row];
        if (selection.length > 0) {
            me.onExecuteOperation(ev,
                {applicationId: selection[0].id},
                <div class="modal57">
                    <div class="header deactivate"><h2>Cancel</h2></div>
                    <div class="body">
                        Confirm cancellation of  {selection[0].firstName}, {selection[0].lastName} for (list all dates). Please note that you will be invoiced with a fee for each day cancelled, according to Recruiter Fee Agreement.</div></div>,
                'cancelApplication',
                function (returnObject) {
                    console.log(returnObject);
                    let updatedObject = Object.assign({}, selection[0]);
                    updatedObject.status = STATUS_NEW;
                    me.applicantsGrid.updateRow(selection[0], updatedObject);
                    me.applicantsGrid.onRefresh();
                });
        }
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
                [{id: applicant.professionalId}, {professionalId: applicant.professionalId}, {professionalId: applicant.professionalId}, {category:me.props.managedObject.category, professional: applicant.professionalId}],
                requestFields).then(
                function (loadedObject) {

                    let managedObject = loadedObject.professional;
                    managedObject.category = me.props.managedObject.category;
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

    getPostingSpeciality(){
        let me=this;
        const categories=me.props.references.categories||[];
        const postingSpecialityKeys=me.props.managedObject.requiredSubcategories||[];
        return categories.find(c=>c.id===me.props.managedObject.category).subCategories.filter(sc=>postingSpecialityKeys.includes(sc.id)).map(r=>r.name).join(', ');
    }
}

const ViewApplicants = ViewApplicantsDialog;

const ViewApplicantsDialogConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {});
    },
    function (dispatch) {
        return Object.assign(baseDispatcherMap(dispatch), {
            onExecuteOperation: (queryName, managedObject, callBackFn) => {
                let me = this;
                dispatch(saveData({queryName: queryName}, managedObject)).then(
                    function (result) {
                        dispatch({type: EVENT_VIEW_SAVE_DATA, result});
                        callBackFn.call(me, result);
                    });
            }
        });
    })(ViewApplicants);

export default ViewApplicantsDialogConnector;