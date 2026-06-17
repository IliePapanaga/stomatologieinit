import React from 'react';
import {connect} from 'react-redux';
//import Dialog from 'material-ui/Dialog';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import {formValueSelector, reduxForm} from 'redux-form';
import Field from '../../common/form/Field';
import CheckboxGroup from '../../common/form/CheckboxGroup';
import Checkbox from '../../common/form/Checkbox';
import SelectField from '../../common/form/SelectField';
import ExSelectField from '../../common/form/ExSelectField';
import {Logger} from 'react-logger-lib';
import {CATEGORY_ID_FRONT_OFFICE_PERSONNEL, SUBCATEGORY_ORDERS} from "../../../../utils/Constants";
import {STATES} from '../../../../data/States';

export const dialogInfo = {
    references: ['categories']
}

let SpecialtyForm = props => {
    const {handleSubmit, change, handleCancel, invalid, references, initialValues} = props;
    let managedObject = props.managedObject || initialValues.managedObject;
    let selectedCategories = references.categories.filter((item) => item.id === managedObject.category);
    let subCategories = undefined;
    if (selectedCategories.length) {
        subCategories = selectedCategories[0].subCategories.filter(function (subCategory) {
            return !managedObject.addedSubCategories || !managedObject.addedSubCategories.includes(subCategory.id);
        });

        subCategories = subCategories.sort(function (a, b) {
            let value1 = SUBCATEGORY_ORDERS[selectedCategories[0].id].indexOf(a.id);
            let value2 = SUBCATEGORY_ORDERS[selectedCategories[0].id].indexOf(b.id);

            if (value2 > value1) {
                return -1;
            }
            if (value1 > value2) {
                return 1;
            }
            return 0;
        });
    }

    let changedCategory = function (e, newValue, oldValue) {
        if (newValue !== oldValue) {
            Logger.of('App.AddEditSpecialtyPage1').info('Category:', newValue);
            change(`subcategories`, []);
        }
    }
    return (
        <form onSubmit={handleSubmit} class="modal__form">

            <div class="specialties__first-main">

                <div class="item__box item__box-header">
                    <div class="text">
                        <p>Category<span class="star">*</span>:</p>
                    </div>
                    <div class="input board__input-wrapper">
                        <Field name="managedObject.category" component={SelectField} menuItems={references.categories}
                               required={true} onChange={changedCategory} hideEmpty={true}/>
                    </div>
                </div>

                {selectedCategories.length > 0 &&
                [<div class="item__box-check-header">
                    <div class="title"><p>Sub-Categories:</p></div>
                    <div class="row__check">
                        <p>YES</p>
                    </div>
                </div>,
                    <div>
                        {managedObject.category !== CATEGORY_ID_FRONT_OFFICE_PERSONNEL && [<div
                            className="category__state">
                            <div className="state__attention">
                                <div className="item__box">
                                    <div className="text">
                                        <p>Please click on the subcategories that you feel comfortable working within.</p>
                                        <p>Ex: RDAEF clicks on RDA and DA instead of just RDAEF</p>
                                    </div>
                                </div>
                            </div>
                            <div
                                className="state__check">
                                <div className="item__box">
                                    <Field name="verified" component={Checkbox} required={true}/>
                                    <div className="text">
                                        <p>I verify I am fully certified to practice in the following states and have no
                                            violations or restrictions against my license</p>
                                    </div>
                                </div>
                            </div>
                            <div className="state__select">
                                <div className="item__box">
                                    <div className="title">
                                        <p>State<span className="star">*</span>:</p>
                                    </div>
                                    <Field name="states" component={ExSelectField} multiple={true}
                                           required={true} menuItems={STATES}/>
                                </div>
                            </div>
                            <div className="state__attention">
                                <div className="item__box">
                                    <div className="text">
                                        <p>Please visit Add/Edit Certificates section to upload licenses/certificates.
                                            Your status will be pending review by Mayday Dental</p>
                                    </div>
                                </div>
                            </div>
                        </div>]}

                        <div class="category__wrapper">
                            <Field itemRenderer={function (item, value, name, itemClassWrapper, scope) {
                                return <div class={itemClassWrapper}>
                                    <div class="title"><p>{item.name}</p></div>
                                    <div class="row__check">
                                        <input type="checkbox" checked={value.indexOf(item.id) > -1} id={`${item.id}`}
                                               value={name}
                                               onChange={(e) => scope.handleChange(e, item.id)}/>
                                        <label for={`${item.id}`}></label>
                                    </div>
                                </div>
                            }} name="managedObject.subcategories" items={subCategories} required={true}
                                   component={CheckboxGroup} itemClassWrapper="item__box-check"/>
                        </div>
                    </div>

                ]}
            </div>

            <div class="footer__btn-wrapper">
                <div class="specialties__help">
                    <p class="mandatory">Fields marked* are mandatory</p>
                </div>
                <div class="footer__btn">
                    <button class="blue white" type="button" onClick={handleCancel}>Cancel</button>
                    <button class="blue" type="button" onClick={handleSubmit} disabled={invalid}>Ok</button>
                </div>
            </div>
        </form>)
}

SpecialtyForm = reduxForm({
    form: 'addeditspecialty',
    destroyOnUnmount: false,
    forceUnregisterOnUnmount: true
})(SpecialtyForm);

const selector = formValueSelector('addeditspecialty');

SpecialtyForm = connect(state => {
    const managedObject = selector(state, 'managedObject')
    return {
        managedObject: managedObject
    }
})(SpecialtyForm)

class AddEditSpecialty extends BaseDialog {

    dialogProps() {
        return {
            width: 640,
            height: 630,
            className: "modal__gray",
            title: "Add/Edit Specialty"
        }
    }

    beforeSave(dialog, managedData) {
        delete managedData.managedObject['category'];
        delete managedData.managedObject['addedSubCategories'];

        Logger.of('App.AddEditSpecialty.beforeSave').info('ManagedObject data:', managedData);
        return true;
    }

    renderDialogContent() {
        return (<SpecialtyForm onSubmit={this.onSave} handleCancel={this.close} dialog={this}
                               initialValues={{managedObject: this.props.managedObject}}
                               references={this.props.references}
                               anchor={this.props.anchor}/>);
    }
}

const AddEditSpecialtyDialogConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {});
    },
    function (dispatch) {
        return Object.assign(baseDispatcherMap(dispatch), {});
    })(AddEditSpecialty);

export default AddEditSpecialtyDialogConnector;
