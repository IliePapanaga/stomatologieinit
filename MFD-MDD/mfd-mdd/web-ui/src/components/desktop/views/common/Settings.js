/*eslint-disable no-unused-vars*/
import React, {Component} from 'react';
/*eslint-enable no-unused-vars*/
import {getDataPromise} from '../../../../actions/common/getData';
import saveData from '../../../../actions/common/saveData';
import Remote from '../../../../utils/Remote';
import ObjectHelper from "../../../../utils/Object";
import {Logger} from "react-logger-lib";
import {Enum} from "enumify";

class Settings extends Component {

    state = {settings: undefined}


    async componentDidMount() {
        let me = this,
            fields = Remote.getFieldsByModel(me.props.metaInfo, "SystemSettingModelConnection", []);
        let systemSettings = await getDataPromise('systemSettings', undefined, fields);

        let settings = {};

        systemSettings.nodes.forEach(function (node) {
            settings[node.key.replace(/\./gi, '__')] = node.value;
        });
        me.setState({settings: settings, origin: systemSettings});

    }

    submit(data) {
        let me = this,
            resultData = ObjectHelper.copyObject(data),
            settings = [];

        for (var prop in resultData) {
            let key = prop.replace(/__/gi, '.')
            settings.push({
                value: resultData[prop],
                key: key,
                type: new Enum({name: me.state.origin.nodes.find(node => node.key === key).type})
            });
        }

        Logger.of('App.General.submit').info('General data:', saveData);

        me.props.onSave({settings: settings});
    }
}

export default Settings;