import React, {Component} from 'react';
import {toastr} from 'react-redux-toastr';
import BaseGrid from './BaseGrid';
import BaseActions from './BaseActions';
import UiView from '../../../utils/UiView';
import References from '../../../utils/References';
import Error from '../../../utils/Error';
import Remote from '../../../utils/Remote';
import ObjectHelper from '../../../utils/Object';
import DateHelper from '../../../utils/DateHelper';
import DateField from './form/DateField';
import Field from './form/Field';
import SelectField from './form/SelectField';
import TextField from './form/TextField';
import getData from '../../../actions/common/getData';
import saveData from '../../../actions/common/saveData';
import {connect, Provider} from 'react-redux';
import {formValueSelector, reduxForm} from 'redux-form';
import deleteData from '../../../actions/common/deleteData';
import {
    BUTTON_TYPE_DELETE,
    BUTTON_TYPE_EDIT,
    BUTTON_TYPE_EDIT_ON_SELECTION,
    EVENT_VIEW_DELETE_DATA,
    EVENT_VIEW_GET_DATA,
    EVENT_VIEW_SAVE_DATA,
    FILTER_TYPE_CHECK,
    FILTER_TYPE_DATE,
    FILTER_TYPE_DATE_RANGE,
    FILTER_TYPE_DROPDOWN,
    FILTER_TYPE_SEARCH,
    FILTER_TYPE_SELECT,
    FILTER_TYPE_TOGGLE,
    REST_API_PREFIX
} from '../../../utils/Constants';
import {FieldsInfoByModel} from '../../../models/core/FieldsInfoByModel';
import {maxOrEqualsThanDate} from "../../../utils/Validators";

let FiltersFormBody = props => {
    const {handleSubmit, handleReset, invalid, renderViewHeader, filters} = props;
    let header = renderViewHeader(handleSubmit, handleReset, invalid, filters, props);
    return (header)
}

export default class BaseView extends Component {

    actions = undefined;
    filters = undefined;
    baseGrid = undefined;
    requestInfo = undefined;
    baseFilters = undefined;
    baseActions = undefined;
    filtersFormComponent = undefined;
    recordLabel = "object";


    constructor(props) {
        super(props);

        let initializationParams = this.initView(props);

        let identificationColumnList = initializationParams.columns.filter((column) => column.name === 'id');

        initializationParams.requestInfo = initializationParams.requestInfo || {};

        initializationParams.requestInfo.idParameterName = 'id';

        if (identificationColumnList.length > 0) {
            initializationParams.requestInfo.idParameterName = identificationColumnList[0].dataIndex;
        }

        this.requestInfo = initializationParams.requestInfo;


        this.state = {
            columns: initializationParams.columns,
            queryName: this.requestInfo ? this.requestInfo.fetchQueryName || this.requestInfo.queryName : undefined,
            additionalFields: initializationParams.additionalFields,
            url: initializationParams.url || REST_API_PREFIX,
            params: initializationParams.params,
            selectionMode: initializationParams.selectionMode,
            hasPagination: initializationParams.hasPagination === false ? initializationParams.hasPagination : true,
            localOrdering: initializationParams.localOrdering === true ? initializationParams.localOrdering : false,
            initialized: false,
            selection: []
        }
        this.actions = initializationParams.actions;
        this.filters = initializationParams.filters;
        this.onLoad();
    }

    initView(props) {
    }

    onLoad() {
        let me = this;
        me.initBaseFilters();
        me.setInitialized(true);
    }

    initBaseFilters() {
        const me = this;
        const id = Date.now();
        const formName=`filters-${id}`;
        let FiltersForm = reduxForm({
            form: formName,
        })(FiltersFormBody);

        const fromSelector = formValueSelector(formName);

        FiltersForm = connect(state => {
            const result = {};
            if(me.filters){
                me.filters.forEach(f => {
                    switch (f.type) {
                        case FILTER_TYPE_DATE_RANGE:
                            result[f.fromName] = fromSelector(state, f.fromName);
                            result[f.toName] = fromSelector(state, f.toName);
                            break;
                        default:
                            result[f.name] = fromSelector(state, f.name);

                    }
                });
            }
            return {filters: result};
        })(FiltersForm);


        me.filtersFormComponent = <FiltersForm
            renderViewHeader={me.renderViewHeader.bind(me)}
            onSubmit={me.applyFilters.bind(me)}
            initialValues={me.getFiltersReduxFormInitialValues()}
            handleReset={me.resetFilters.bind(me)}
            onChange={me.onChangeFilters.bind(me)}
            key="base_filters"/>;
        return me;
    }

    setInitialized(initialized) {
        let me = this;
        me.state.initialized = initialized;
        me.setState({initialized: me.state.initialized});
    }

    generateFilter(element, values) {
        let me = this;
        let filter = undefined;
        switch (element.type) {
            case FILTER_TYPE_DATE_RANGE:
                element.allowedHistory = element.allowedHistory === undefined ? true : element.allowedHistory;
                filter = <div class="board__header-date">
                    <div class="date__select">
                        <Field name={element.fromName} class="date" onlyDate={element.onlyDate || false}
                               placeholder={element.fromPlaceholder || "From"} component={DateField}
                               dateFormat="MM/DD/YYYY"
                               minDate={element.allowedHistory ? null : DateHelper.getCurrentDate()}
                               maxDate={element.allowedFuture ? null : DateHelper.getCurrentDate()}/>
                    </div>
                    <div class="date__select">
                        <Field name={element.toName} class="date" onlyDate={element.onlyDate || false}
                               placeholder={element.toPlaceholder || "To"} component={DateField} dateFormat="MM/DD/YYYY"
                               minDate={element.allowedHistory ? null : DateHelper.getCurrentDate()}
                               maxDate={element.allowedFuture ? null : DateHelper.getCurrentDate()} validate={maxOrEqualsThanDate(element.fromName, true)} initiallyChoosenDate={values[element.fromName]}/>
                    </div>
                </div>;
                break;
            case FILTER_TYPE_DATE:
                filter = <div class="board__header-date">
                    <div class="date__select">
                        <Field name={element.name} class="date" onlyDate={element.onlyDate || false}
                               placeholder={element.placeholder || "From"} component={DateField} dateFormat="MM/DD/YYYY"
                               minDate={element.minDate} maxDate={element.maxDate}/>
                    </div>
                </div>;
                break;
            case FILTER_TYPE_SELECT:
                filter = <div class="filter__select">
                    <Field name={element.name} component={SelectField} placeholder={element.title}
                           menuItems={element.menuItems} grouped={element.grouped} multiple={element.multiple}
                           required={element.required}/>
                </div>;
                break;


            case FILTER_TYPE_DROPDOWN:
                filter = <div class="filter__dropdown" onClick={element.onClick}>
                    <div class="dropdown__text">{element.title}</div>
                    <div class="dropdown__list-hide hide"></div>
                    <div class="dropdown__list hide">
                        <div class="dropdown__list-wrapper">
                            {element.children.map((filter, index) => (
                                me.generateFilter(filter, values)
                            ))}
                            {false && <div class="btn">
                                <button class="yellow">Ok</button>
                            </div>}
                            <div class="btn hide__btn">
                            </div>
                        </div>
                    </div>
                </div>;
                break;

            case FILTER_TYPE_TOGGLE:
                filter = <div class="filter__toggle" onClick={element.onClick}>
                    <div class="toggle__text">{element.title}</div>
                </div>;
                break;


            case FILTER_TYPE_SEARCH:
                filter = <div class="filter__search">
                    <div>{element.title}</div>
                    <Field name={element.name} component={TextField} placeholder="Name Search"
                           required={element.required}/>
                </div>;
                break;


            case FILTER_TYPE_CHECK:
                filter = <div class="filter__check-wrapper">
                    <div class="filter__check">
                        <div class="row__check">
                            <input type="checkbox" name="check" id="check"/>
                            <label for="check"></label>
                        </div>
                        <div class="text">
                            {/*<Field name={element.name} component={CheckboxGroup} placeholder={element.title} />*/}
                            <p>Filter</p>
                        </div>
                    </div>
                    <div class="filter__check">
                        <div class="row__check">
                            <input type="checkbox" name="check" id="check"/>
                            <label for="check"></label>
                        </div>
                        <div class="text">
                            {/*<Field name={element.name} component={CheckboxGroup} placeholder={element.title} />*/}
                            <p>Filter</p>
                        </div>
                    </div>
                </div>;
                break;
            default:
                break;

        }
        return filter;
    }

    renderViewHeader(handleSubmit, handleReset, invalid, filterValues,props) {
        let me = this;
        me.baseFilters = props;

        let clNameTrue = "board__header fullHeader";

        return (<div class="board__header" className={me.filters && clNameTrue}>
            {me.filters && <div class="board__header-left">
                <div class="board__header-filters">
                    {me.filters.map((filter, viewIndex) => (
                        me.generateFilter(filter, filterValues)
                    ))}
                </div>
            </div>}


            {me.filters && <div class="board__header-right">
                <div class="board__header-reset">
                    <div class="reset" onClick={handleReset.bind(me, props.reset)}>Reset Filters</div>
                </div>
            </div>}
        </div>);
    }

    onAddObject(Dialog, dialogInfo, e) {
        let me = this;
        const target = e.target.parentElement;
        me.props.onLoadRefs(dialogInfo.references || [], me.props.references, me.props.metaInfo, function () {

            let actions = {
                save: addObject(me.requestInfo).bind(me),
                close: function () {
                    const dialog = this;
                    if (me.requestInfo && me.requestInfo.reloadAfterClose) {
                        me.baseGrid.onRefresh();
                    }
                    dialog.close();

                }
            }
            UiView.showDialog(<Provider store={UiView.createDialogStore()}><Dialog {...me.dialogProps(Dialog, target)}
                                                                                   managedObject={me.prepareManagedObject({}, target)}
                                                                                   actions={actions}
                                                                                   references={me.props.references}
                                                                                   metaInfo={me.props.metaInfo}/></Provider>);
        });


    }

    onEditObject(Dialog, dialogInfo, e) {
        let me = this;

        let editAction = me.baseActions.props.actions.find(a => a.type === BUTTON_TYPE_EDIT);

        const target = e.target ? e.target.parentElement : {
            getAttribute: function (name) {
                return editAction ? editAction.name : null;
            }
        };
        let selection = me.state.selection;
        if (selection.length > 0) {
            try {

                let requestFields = undefined;

                if (ObjectHelper.isArray(me.requestInfo.getQueryName)) {

                    requestFields = [];
                    me.requestInfo.getResponseModel.forEach(getResponseModel => {
                        let tmpRequestFields = undefined;
                        if (getResponseModel instanceof FieldsInfoByModel) {
                            tmpRequestFields = Remote.getFieldsByModel(me.props.metaInfo, getResponseModel.model, getResponseModel.ignoreFields, getResponseModel.modelMode);
                        } else {
                            tmpRequestFields = Remote.getFieldsByModel(me.props.metaInfo, getResponseModel, [], me.requestInfo.getResponseModelMode === 'input');
                        }
                        requestFields.push(tmpRequestFields);
                    });

                } else {
                    requestFields = dialogInfo.fields || Remote.getFieldsByModel(me.props.metaInfo, me.requestInfo.getResponseModel, [], me.requestInfo.getResponseModelMode === 'input');
                }

                me.props.onLoadRecord({
                    queryName: ObjectHelper.copyObject(me.requestInfo.getQueryName),
                    requestFields: requestFields,
                    getQueryParameterName: me.requestInfo.getQueryParameterName,
                    idParameterName: me.requestInfo.idParameterName
                }, selection[0], function (object) {
                    if (ObjectHelper.isObject(object)) {
                        me.props.onLoadRefs(dialogInfo.references || [], me.props.references, me.props.metaInfo, function () {
                            let actions = {
                                save: saveObject(me.requestInfo).bind(me),
                                close: function () {
                                    const dialog = this;
                                    if (me.requestInfo && me.requestInfo.reloadAfterClose) {
                                        me.baseGrid.onRefresh();
                                    }
                                    dialog.close();

                                }
                            };
                            me.showDialog(me, Dialog, Provider, target, object, actions);
                        });

                    }
                });
            } catch (ex) {
                Error.showErrors(ex)
            }
        }
    }

    onEditOnSelectionObject(Dialog, dialogInfo, e) {
        let me = this;
        const target = e.target.parentElement;
        let selection = me.state.selection;
        if (selection.length > 0) {
            try {
                me.props.onLoadRefs(dialogInfo.references || [], me.props.references, me.props.metaInfo, function () {
                    let actions = {
                        save: saveObject(me.requestInfo).bind(me),
                        close: function () {
                            const dialog = this;
                            if (me.requestInfo && me.requestInfo.reloadAfterClose) {
                                me.baseGrid.onRefresh();
                            }
                            dialog.close();

                        }
                    }
                    me.showDialog(me, Dialog, Provider, target, selection[0], actions);
                });
            } catch (ex) {
                Error.showErrors(ex)
            }
        }
    }

    showDialog(me, Dialog, Provider, target, object, actions) {
        UiView.showDialog(<Provider
            store={UiView.createDialogStore()}><Dialog {...me.dialogProps(Dialog, target)}
                                                       managedObject={me.prepareManagedObject(object, target)}
                                                       actions={actions}
                                                       references={me.props.references}
                                                       metaInfo={me.props.metaInfo}/></Provider>);
    }

    /**
     * Prepare got object before put it as managed object for dialogs
     */
    prepareManagedObject(loadedObject) {
        return loadedObject;
    }

    dialogProps(dlg, e) {
        return null;
    }

    onDeleteObject(dialogInfo, e) {
        let me = this;
        let selection = me.state.selection;
        if (selection.length > 0) {
            const toastrConfirmOptions = {
                onOk: function () {
                    try {
                        me.props.onDeleteRecord(me.requestInfo, selection[0], function (success) {
                            me.baseGrid.deleteRows(selection);
                        });
                    } catch (ex) {
                        Error.showErrors(ex)
                    }

                },
                onCancel: () => console.log('CANCEL: clicked')
            };
            toastr.confirm(
                <div class="modal57">
                    <div class="header deactivate"><h2>delete</h2></div>
                    <div class="body">Are you sure you want to delete the selection?</div></div>,
                toastrConfirmOptions);
        }

    }

    onExecuteOperation(button, managedObject, confirmationMessage, queryName, responseModel, callBack) {
        let me = this;
        let selection = me.state.selection;
        if (selection.length > 0) {
            let selectedObject = selection[0];
            const toastrConfirmOptions = {
                onOk: function () {
                    try {
                        if (managedObject) {
                            for (var prop in managedObject) {
                                if (!managedObject[prop] && selectedObject.hasOwnProperty(prop) && selectedObject[prop]) {
                                    managedObject[prop] = selectedObject[prop];
                                }
                            }
                        } else {
                            managedObject = selectedObject;
                        }

                        me.props.onExecuteOperation(queryName, responseModel, managedObject, callBack, me.props.metaInfo, selectedObject);
                    } catch (ex) {
                        Error.showErrors(ex)
                    }

                },
                onCancel: () => console.log('CANCEL: clicked')
            };
            toastr.confirm(confirmationMessage, toastrConfirmOptions);
        }

    }

    onChangeFilters(data, reducer, form) {
        let me = this;
        setTimeout(function () {
            if (!form.invalid) {
                me.applyFilters(data);
            }
        }, 500)


    }

    /**
     * Filters initial values
     */
    getFiltersReduxFormInitialValues() {
        return undefined;
    }

    applyFilters(data) {
        let me = this,
            filters = ObjectHelper.copyObject(data);
        me.baseGrid.changeFilters(this.convertFilterDataBeforeFiltering(filters));
    }

    /**
     * Convert filters' data before filtering
     */
    convertFilterDataBeforeFiltering(data) {
        return data;
    }

    /**
     * Prepare updated object for display in grid
     */
    prepareUpdatedObjectBeforeDisplay(oldObject, forUpdateObject, newObject) {
        return newObject;
    }

    resetFilters(resetFn) {
        resetFn();
    }

    isEnabledAction(element, selection) {
        let result = true;
        switch (element.type) {
            case BUTTON_TYPE_EDIT:
            case BUTTON_TYPE_EDIT_ON_SELECTION:
            case BUTTON_TYPE_DELETE:
                result = selection && selection.length === 1;
                break;
            default:
                break;
        }

        if (result) {
            if (element.allowedValues && element.allowedByFieldName) {
                result = selection && selection.length === 1 && element.allowedValues.includes(selection[0][element.allowedByFieldName])
            }
        }
        if (result) {
            if (element.disallowedValues && element.disallowedByFieldName) {
                result = selection && selection.length === 1 && !element.disallowedValues.includes(selection[0][element.disallowedByFieldName])
            }
        }
        if (result && element.hasSelectedRows) {
            result = element.hasSelectedRows === selection.length;
        }
        return result;
    }

    showRow(record, index, e) {
        if (this.actions) {
            let elementArr = this.actions.filter((action) => action.type === BUTTON_TYPE_EDIT);
            if (elementArr.length) {
                let element = elementArr[0];
                if (this.isEnabledAction(element, [record])) {
                    this.onEditObject(element.dialog, element.dialogInfo, e);
                }
            }
        }
    }

    changeSelection(selection) {
        this.setState({selection: selection})
    }

    render() {
        let {columns, selectionMode, url, queryName, additionalFields, hasPagination, localOrdering, params, initialized} = this.state;

        if (!initialized) {
            return false;
        }
        return (
            [
                this.filtersFormComponent,
                <BaseActions
                    actions={this.actions}
                    baseView={this}
                    key="base_actions"
                    ref={(el) => {
                        this.baseActions = el
                    }}
                    selection={this.state.selection}
                />,
                <BaseGrid
                    columns={columns}
                    selectionMode={selectionMode}
                    url={url} queryName={queryName}
                    additionalFields={additionalFields}
                    params={params}
                    showRow={this.showRow.bind(this)}
                    onRef={ref => (this.baseGrid = ref)}
                    hasPagination={hasPagination}
                    localOrdering={localOrdering}
                    changeSelection={this.changeSelection.bind(this)}
                    key="base_view_base_grid"/>
            ]

        );

    }
}

export const baseViewStateMap = (state, ownProps) => ({
    view: state.view,
    metaInfo: state.context.metaInfo,
    references: state.references
});

export let baseViewDispatcherMap = dispatch => ({
    onDestroy(view) {
        dispatch({type: "@@redux-form/DESTROY", meta: {form: ["filters"]}});
    },
    onLoadRefs: (neededReferences, references, metaInfo, callback) => {
        let me = this;
        References.updateRefs(neededReferences, references, dispatch, metaInfo, me, callback);
    },
    onLoadRecord: (requestInfo, selectedRecord, callBackFn) => {
        let me = this;
        let objectId = undefined;
        if (ObjectHelper.isArray(requestInfo.getQueryParameterName)) {
            objectId = [];
            let removeIndexes = [];

            requestInfo.getQueryParameterName.forEach(function (parameterName, index) {
                let obj = {};
                if (ObjectHelper.isObject(parameterName)) {
                    obj[parameterName.parameterName] = ObjectHelper.getValue(selectedRecord, (parameterName.idParameterName ? parameterName.idParameterName : requestInfo.idParameterName || 'id'));
                    if (!obj[parameterName.parameterName]) {
                        removeIndexes.push(index);
                    }
                } else {
                    obj[parameterName] = ObjectHelper.getValue(selectedRecord, requestInfo.idParameterName || 'id');
                    if (!obj[parameterName]) {
                        removeIndexes.push(index);
                    }
                }

                objectId.push(obj);
            });

            if (removeIndexes.length > 0) {
                for (var i = removeIndexes.length - 1; i >= 0; i--) {
                    requestInfo.queryName.splice(removeIndexes[i], 1);
                    objectId.splice(removeIndexes[i], 1);
                    requestInfo.requestFields.splice(removeIndexes[i], 1);
                }
            }

        } else {
            objectId = ObjectHelper.getValue(selectedRecord, requestInfo.idParameterName || 'id');
        }

        dispatch(getData(requestInfo.queryName, objectId, requestInfo.requestFields)).then(
            function (result) {
                dispatch({type: EVENT_VIEW_GET_DATA, result});
                callBackFn.call(me, result);
            });
    },
    onSaveRecord: (requestInfo, managedObject, callBackFn, metaInfo, update) => {
        let me = this,
            data = {},
            queryName = update ?
                (ObjectHelper.isFunction(requestInfo.updateQueryName) ? requestInfo.updateQueryName(managedObject) : requestInfo.updateQueryName) :
                (ObjectHelper.isFunction(requestInfo.addQueryName) ? requestInfo.addQueryName(managedObject) : requestInfo.addQueryName),
            wrapperName = update ? requestInfo.updateQueryWrapperName : requestInfo.addQueryWrapperName,
            responseModel = update ? requestInfo.updateResponseModel : requestInfo.addResponseModel;

        if (wrapperName) {
            data[wrapperName] = managedObject;
        } else {
            data = managedObject;
        }


        let fields = responseModel ? Remote.getFieldsByModel(metaInfo, responseModel) : undefined;

        dispatch(saveData({queryName: queryName, fields: fields, showLoader: true}, data)).then(
            function (result) {
                dispatch({type: EVENT_VIEW_SAVE_DATA, result});
                callBackFn.call(me, result);
            });
    },
    onDeleteRecord: (requestInfo, selectedRecord, callBackFn) => {
        let me = this;
        dispatch(deleteData({queryName: requestInfo.deleteQueryName}, ObjectHelper.getValue(selectedRecord, requestInfo.idParameterName))).then(
            function (result) {
                if (result) {
                    dispatch({type: EVENT_VIEW_DELETE_DATA, result});
                    callBackFn.call(me, result);
                }
            });
    },
    onExecuteOperation: (queryName, responseModel, managedObject, callBackFn, metaInfo, selectedObject) => {
        let me = this;

        let fields = responseModel ? Remote.getFieldsByModel(metaInfo, responseModel) : undefined;

        dispatch(saveData({queryName: queryName, fields: fields}, managedObject)).then(
            function (result) {
                dispatch({type: EVENT_VIEW_SAVE_DATA, result});
                callBackFn.call(me, result, selectedObject);
            });
    }
});

export function saveObject(requestInfo) {
    return function (editor, updatedManagedObject, successfulCallBack) {
        let me = this;

        try {
            me.props.onSaveRecord(requestInfo, updatedManagedObject, function (updatedObject) {
                if(requestInfo&&requestInfo.reloadAfterEdit){
                    me.baseGrid.onRefresh();
                }else{
                    let originManagedObject = me.state.selection;
                    updatedObject = me.prepareUpdatedObjectBeforeDisplay(originManagedObject[0], updatedManagedObject, updatedObject);
                    me.baseGrid.updateRow(originManagedObject[0], updatedObject);
                }
                successfulCallBack.call(editor, editor);
            }, me.props.metaInfo, true);
        } catch (ex) {
            Error.showErrors(ex)
        }
    };
}

export function addObject(requestInfo) {
    return function (editor, newManagedObject, successfulCallBack) {
        let me = this;
        try {
            me.props.onSaveRecord(requestInfo, newManagedObject, function (savedObject) {

                if(requestInfo&&requestInfo.reloadAfterAdd){
                    me.baseGrid.onRefresh();
                }else{
                    savedObject = me.prepareUpdatedObjectBeforeDisplay(null, newManagedObject, savedObject);
                    me.baseGrid.addRow(savedObject);
                }
                successfulCallBack.call(editor, editor);
            }, me.props.metaInfo, false);
        } catch (ex) {
            Error.showErrors(ex)
        }
    };

}