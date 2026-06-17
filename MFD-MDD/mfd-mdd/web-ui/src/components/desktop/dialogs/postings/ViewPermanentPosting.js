import React from 'react';
import {connect} from 'react-redux';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import {formValueSelector, reduxForm} from 'redux-form';
import Field from '../../common/form/Field';
import TextArea from '../../common/form/TextArea';
import ExSelectField from '../../common/form/ExSelectField';
import CheckboxGroup from '../../common/form/CheckboxGroup';
import SelectField from '../../common/form/SelectField';
import DateHelper from '../../../../utils/DateHelper';
import {Logger} from 'react-logger-lib';
import ObjectUtils from "../../../../utils/Object";
import DateField from "../../common/form/DateField";
import WorkSchedule from "../schedules/WorkSchedule";
import BrowseDialogButton from "../../common/BrowseDialogButton";
import {SUBCATEGORY_ORDERS} from "../../../../utils/Constants";
import ObjectHelper from "../../../../utils/Object";

export const dialogInfo = {
    references: ['categories', 'languages']
}


let Form = props => {
    const {handleSubmit, change, handleCancel, references, initialValues, readOnly} = props;
    let managedObject = props.managedObject || initialValues.managedObject;
    let category = managedObject.category;
    let selectedCategories = references.categories.filter((item) => item.id === category);
    let subCategories = undefined;
    if (selectedCategories.length) {
        subCategories = selectedCategories[0].subCategories;

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

        let startToMoveIndex = Math.round(subCategories.length / 2);

        for (let i = 0; i + startToMoveIndex < subCategories.length; i++) {
            let from = i + startToMoveIndex;
            let to = -1 * (startToMoveIndex - 1 - i);
            console.log(from, to);
            ObjectHelper.move(subCategories, subCategories[from], to);
        }
    }
    let changedCategory = function (e, newValue, oldValue) {
        if (newValue !== oldValue) {
            Logger.of('App.AddPermanentPosting').info('Category:', newValue);
            change(`managedObject.requiredSubcategories`, []);
        }
    }

    let generateName = function (e, newValue, oldValue) {
        let requiredSubcategories = ObjectUtils.isArray(newValue) ? newValue : managedObject.requiredSubcategories;
        let practiceLocationId = ObjectUtils.isArray(newValue) ? managedObject.practiceLocationId : newValue;
        if (requiredSubcategories && requiredSubcategories.length > 0 && practiceLocationId) {
            let subCats = requiredSubcategories.join("\\");
            let location = managedObject.practice.locations.find(function (l) {
                return l.id === practiceLocationId;
            });
            change(`managedObject.name`, `${subCats}\\${location.name}`);
        }
        return false;
    }
    return (
        <form onSubmit={handleSubmit}>

            <div class="modal__gray-main modal6">
                <div class="modal__gray-main-item">
                    <div class="item__box">
                        <div class="title">
                            <p>Posting Name:</p>
                        </div>
                        <div class="data">
                            <p>{managedObject.name}</p>
                        </div>
                    </div>
                </div>

                <div class="modal__gray-main-item">
                    <h3>Position</h3>
                    <div class="item__box">
                        <div class="text">
                            <p>Category:</p>
                        </div>
                        <div class="input board__input-wrapper select__input">
                            <Field name="managedObject.category" component={SelectField}
                                   menuItems={references.categories} required={true} onChange={changedCategory}
                                   hideEmpty={true} disabled={readOnly}/>
                        </div>
                    </div>

                    {selectedCategories.length > 0 &&
                    <div class="item__box check">
                        <Field itemRenderer={function (item, value, name, itemClassWrapper, scope) {
                            return <div class={itemClassWrapper} onChange={(e) => scope.handleChange(e, item.id)}>
                                <input type="checkbox" checked={value.indexOf(item.id) > -1}
                                       id={`${item.id}`}
                                       value={name} disabled={readOnly}
                                />
                                <label for={`${item.id}`}>{item.name}</label>
                            </div>
                        }} name="managedObject.requiredSubcategories" items={subCategories} required={true}
                               component={CheckboxGroup} itemClassWrapper="check-item" classWrapper={"check"}
                               columns={1} onChange={generateName}/></div>}


                </div>

                <div class="modal__gray-main-item">
                    <div class="item__box">
                        <div class="text">
                            <p>Location:</p>
                        </div>
                        <div class="input board__input-wrapper">
                            <Field onChange={generateName} name="managedObject.practiceLocationId"
                                   component={SelectField}
                                   menuItems={managedObject.practice.locations}
                                   required={true} disabled={readOnly}/>
                        </div>
                    </div>

                    <div class="item__box">
                        <div class="text">
                            <p>Start Date:</p>
                        </div>
                        <div class="input input__date">
                            <Field name="managedObject.startDate" class="date" placeholder="Select date"
                                   component={DateField} dateFormat="MM/DD/YYYY" showYearDropdown="{true}"
                                   required={true} onlyDate={true} minDate={DateHelper.getCurrentDate()}
                                   disabled={readOnly}/>
                        </div>
                    </div>

                    <div class="item__box">
                        <div class="text">
                            <p>Work Schedule:</p>
                        </div>
                        <div class="input">
                            <BrowseDialogButton title="Select" dialog={WorkSchedule} actions={{
                                save: function (editor, updatedManagedObject, successfulCallBack) {
                                    change(`managedObject.workSchedules`, updatedManagedObject.workSchedules);
                                    editor.close();
                                }
                            }} managedObject={managedObject} browseProps={{readOnly: {readOnly}}}/>
                        </div>
                    </div>

                </div>
                <div class="modal__gray-main-item">
                    <div class="item__box">
                        <div class="text">
                            <p>Language:</p>
                        </div>
                        <div class="input board__input-wrapper select__input">
                            <Field name="managedObject.requiredLanguages" component={ExSelectField}
                                   menuItems={references.languages}
                                   required={true} multiple={true} readOnly={readOnly}/>
                        </div>
                    </div>

                    <div class="item__box">
                        <div class="text">
                            <p>Comments:</p>
                        </div>
                        <Field name="managedObject.comment" component={TextArea} placeholder="Your text..."
                               required={false} readOnly={readOnly}/>
                    </div>

                </div>

            </div>
            <div class="footer__btn-wrapper">
                <div class="footer__btn">
                    <button class="blue white" type="button" onClick={handleCancel}>Close</button>
                </div>
            </div>
        </form>
    )
}

Form = reduxForm({
    form: 'addpermanentposting'
})(Form);

const selector = formValueSelector('addpermanentposting');

Form = connect(state => {
    const managedObject = selector(state, 'managedObject')
    return {
        managedObject: managedObject
    }
})(Form)


class ViewPermanentPosting extends BaseDialog {

    dialogProps() {
        return {
            width: 560,
            height: 780,
            className: "modal__gray",
            title: "View Permanent Posting"
        }
    }

    beforeSave(dialog, managedData) {
        let jobPosting = managedData.managedObject;

        delete jobPosting['category'];
        delete jobPosting['practice'];
        delete jobPosting['_type_'];

        if (jobPosting.workSchedules) {

            for (var i = jobPosting.workSchedules.length - 1; i >= 0; i--) {
                if (!jobPosting.workSchedules[i]._enabled) {
                    delete jobPosting.workSchedules[i];
                } else {
                    delete jobPosting.workSchedules[i]['_enabled'];
                }
            }
        }


        managedData.managedObject = {jobPosting: jobPosting};

        Logger.of('App.AddEditLocation.beforeSave').info('ManagedObject data:', managedData);
        return true;
    }

    renderDialogContent() {
        return (<Form onSubmit={this.onSave} handleCancel={this.close} dialog={this}
                      initialValues={{managedObject: this.props.managedObject}} references={this.props.references}
                      readOnly={true}/>);
    }
}

const ViewPermanentPostingDialogConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {});
    },
    function (dispatch) {
        return Object.assign(baseDispatcherMap(dispatch), {});
    })(ViewPermanentPosting);

export default ViewPermanentPostingDialogConnector;