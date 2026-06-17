import React from 'react';
import BaseView from '../../common/BaseView';
import UiView from '../../../../utils/UiView';
import FindLocation from '../../dialogs/filters/FindLocation';
import {Provider} from 'react-redux';
import {FILTER_TYPE_LOCATION} from '../../../../utils/Constants';

export default class FindByLocation extends BaseView {
    locationInfo = {
        distance: undefined,
        lng: undefined,
        lat: undefined
    }
    selectLocation() {
        let me = this,
            locationData = me.locationInfo;

        let actions = {
            save: function (editor, updatedManagedObject, successfulCallBack) {
                let data=Object.assign(me.baseFilters.filters||{}, updatedManagedObject);
                me.baseFilters.onChange(data, undefined, me.baseFilters);
                me.locationInfo = updatedManagedObject;
                successfulCallBack.call(editor, editor);
                me.filterLocationDiv.classList.add('filter__location-selected');
            }
        }
        UiView.showDialog(<Provider store={UiView.createDialogStore()}><FindLocation actions={actions} managedObject={locationData} /></Provider>);

    }

    generateFilter(element, values) {
        let me = this;
        let filter = super.generateFilter(element, values);
        if (!filter) {
            switch (element.type) {
                case FILTER_TYPE_LOCATION:
                    filter = <div class="filter__location" ref={(div) => {
                        me.filterLocationDiv = div
                    }}>
                        <button class="yellow" type="button" onClick={this.selectLocation.bind(this)}></button>
                    </div>;
                    break;
                default:
                    break;

            }
        }
        return filter;
    }

    // onChangeFilters(data, reducer, form) {
    //     let me = this;
    //     Object.assign(data, me.locationInfo);
    //     super.onChangeFilters(data, reducer, form);
    // }

    resetFilters(resetFn) {
        let me = this;

        me.locationInfo = {
            distance: undefined,
            lng: undefined,
            lat: undefined
        };
        if(me.filterLocationDiv){
            me.filterLocationDiv.classList.remove('filter__location-selected');
        }
        resetFn();
        me.baseFilters.onChange(me.locationInfo, undefined, me.baseFilters);
    }

    getFiltersReduxFormInitialValues() {

        return {
            distance: undefined,
            lng: undefined,
            lat: undefined
        }
    }
}