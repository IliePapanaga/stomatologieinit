import {Field} from '../models/core/QueryInfo';
import Cookies from 'universal-cookie';

import {Logger} from 'react-logger-lib';
import axios from 'axios';
import {REST_API_PREFIX, REST_API_PREFIX_SIMPLE} from './Constants';
import ObjectHelper from './Object';
import Error from './Error';
import {FieldsInfo} from "../models/core/FieldsInfo";

const cookies = new Cookies();

export default class Remote {
    static executeQuery(query: any, success, error, simpleQuery = false, uri = REST_API_PREFIX, recaptchaHash = undefined) {
        let isComposite = false,
            collectedQuery = undefined,
            isString = ObjectHelper.isString(query);

        if (isString) {
            collectedQuery = query;
        } else {
            collectedQuery = query.join('},{');
            isComposite = true;
        }
        let data = {};
        data["query"] = simpleQuery ? collectedQuery : `{${collectedQuery}}`
        let optionHeaders = {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': cookies.get(' XSRF-TOKEN'),
            'X-Requested-With': 'XMLHttpRequest'
        }
        if (recaptchaHash) {
            optionHeaders['g-recaptcha-response'] = recaptchaHash;
        }
        let options = {
            url: uri,
            method: 'post',
            headers: optionHeaders,
            data: data
        };
        return axios(options).then(function (response) {
            let result = response.data.data;
            if (!Error.hasGraphQlErrors(response.data)) {
                if (!isComposite) {
                    for (var key in response.data.data) {
                        result = response.data.data[key];

                    }
                }
                return success.call(this, response.status, result);
            } else {
                return error.call(this, response.data);
            }
        }).catch(error);
    }

    static executeMultiQuery(query: any, success, error, simpleQuery = false, uri = REST_API_PREFIX, recaptchaHash = undefined) {
        let isComposite = false,
            collectedQuery = undefined,
            isString = ObjectHelper.isString(query);

        if (isString) {
            collectedQuery = query;
        } else {
            collectedQuery = query.join(',');
            isComposite = true;
        }
        let data = {};
        data["query"] = simpleQuery ? collectedQuery : `{${collectedQuery}}`;

        let optionHeaders = {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': cookies.get(' XSRF-TOKEN'),
            'X-Requested-With': 'XMLHttpRequest'
        }
        if (recaptchaHash) {
            optionHeaders['g-recaptcha-response'] = recaptchaHash;
        }

        let options = {
            url: uri,
            method: 'post',
            headers: optionHeaders,
            data: data
        };
        return axios(options).then(function (response) {
            let result = response.data.data;
            if (!Error.hasGraphQlErrors(response.data)) {
                if (!isComposite) {
                    for (var key in response.data.data) {
                        result = response.data.data[key];

                    }
                }
                return success.call(this, response.status, result);
            } else {
                return error.call(this, response.data);
            }
        }).catch(error);
    }

    static executeMutation(data: any, success, error, uri = REST_API_PREFIX, recaptchaHash = undefined) {
        let isComposite = false,
            collectedQuery = undefined,
            isString = ObjectHelper.isString(data);

        if (isString) {
            collectedQuery = data;
        } else {
            collectedQuery = data.join('},{');
            isComposite = true;
        }
        data = {query: `mutation{${collectedQuery}}`};

        let optionHeaders = {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': cookies.get(' XSRF-TOKEN'),
            'X-Requested-With': 'XMLHttpRequest'
        }

        if (recaptchaHash) {
            optionHeaders['g-recaptcha-response'] = recaptchaHash;
        }

        let options = {
            url: uri,
            method: 'post',
            headers: optionHeaders,
            data: data
        };
        return axios(options).then(function (response) {
            let result = response.data.data;
            if (!Error.hasGraphQlErrors(response.data)) {
                if (!isComposite) {
                    for (var key in response.data.data) {
                        result = response.data.data[key];

                    }
                }
                return success.call(this, response.status, result);
            } else {
                return error.call(this, response.data);
            }
        }).catch(error);
    }

    static collectResult(fieldsInfo: Array<FieldsInfo>, head = undefined) {
        let createResultChain = function (chainHead, entities) {
                let result = chainHead ? `${chainHead}{` : '';

                for (var key in entities) {
                    if (ObjectHelper.isArray(entities[key])) {
                        var upperChain = createResultChain(key, entities[key]);
                        result += ` ${upperChain}`
                    } else {
                        result += ` ${entities[key]}`
                    }
                }

                result = chainHead ? `${result}}` : result;

                Logger.of('App.Remote.createResultLeaf').info('ChainHead:', chainHead, 'Conversion:', result);

                return result;
            },
            getConversion = function (select: Array<Field>, conversionHhead) {
                let entities = [];
                select.forEach(function (element) {
                    let parts = element.name.split('.');
                    let root = entities;
                    parts.forEach(function (part, index, partArr) {
                        if (partArr.length - 1 === index) {
                            root.push(part);
                        } else {
                            if (root.indexOf(part) >= 0) {
                                delete root[root.indexOf(part)];
                            }
                            root[part] = root[part] || [];
                            root = root[part];
                        }

                    }, this);

                }, this);

                Logger.of('App.Remote.collectResult').info('entities:', entities);

                let conversion = createResultChain(conversionHhead, entities);

                Logger.of('App.Remote.collectResult').info('Select:', select, 'Conversion:', conversion);

                return conversion;
            };
        if (fieldsInfo.length === 1 && !fieldsInfo[0].subClass) {
            return getConversion(fieldsInfo[0].select, head);
        } else {
            let result = '';
            fieldsInfo.forEach(function (fieldsInfoItem) {
                result += getConversion(fieldsInfoItem.select, ` ...on ${fieldsInfoItem.model}`);
            });
            return result;
        }
    }

    static getFieldsByModel(metaInfo, model, ignoreFields = [], input = false): Array<FieldsInfo> {
        let fields: Array<FieldsInfo> = [],
            addSubFields = function (fields, parentName, parentModel, ignoreFields, input = true) {
                let baseTypes = metaInfo.types.filter((type) => type.name === parentModel);
                let fieldsProperty = !input ? baseTypes[0].fields : baseTypes[0].inputFields;
                let ignoreArray = [];
                if (ignoreFields) {
                    ignoreArray = ignoreFields.filter((item) => item.parent === parentName);
                    if (ignoreArray && ignoreArray.length > 0) {
                        ignoreArray = ignoreArray[0].children;
                    }
                }
                fieldsProperty.forEach(function (field) {
                    if (ignoreArray && ignoreArray.includes(field.name)) {
                        return false;
                    }
                    let parentConcat = `${parentName}.${field.name}`;
                    if (field.type.kind === (input ? 'INPUT_OBJECT' : 'OBJECT')) {
                        addSubFields(fields, parentConcat, field.type.name, ignoreFields, input);
                    } else if (field.type.kind === 'LIST') {
                        if (field.type.ofType.kind !== 'SCALAR') {
                            addObject(metaInfo, fields, field.type.ofType.name, parentConcat, ignoreFields, input);
                        } else {
                            fields.push(new Field(parentConcat));
                        }

                    } else {
                        fields.push(new Field(parentConcat));
                    }
                }, this);

            },
            addObject = function (metaInfo, fields, model, parent, ignoreFields, input = true) {
                Logger.of('App.Remote.getFieldsByModel').info('MetaInfo:', metaInfo, 'Model:', model);
                let baseTypes = metaInfo.types.filter((type) => type.name === model);
                if (baseTypes.length > 0) {
                    let fieldsProperty = !input ? baseTypes[0].fields : baseTypes[0].inputFields;
                    let ignoreArray = [];
                    if (ignoreFields) {
                        ignoreArray = ignoreFields.filter((item) => item.parent === parent);
                        if (ignoreArray && ignoreArray.length > 0) {
                            ignoreArray = ignoreArray[0].children;
                        }
                    }
                    fieldsProperty.forEach(function (field) {
                        if (ignoreArray && ignoreArray.includes(field.name)) {
                            return false;
                        }
                        let fieldName = `${field.name}`;
                        if (parent) {
                            fieldName = `${parent}.${fieldName}`;
                        }
                        if (field.type.kind === (input ? 'INPUT_OBJECT' : 'OBJECT')) {
                            addSubFields(fields, fieldName, field.type.name, ignoreFields, input);
                        } else if (field.type.kind === 'LIST') {
                            if (field.type.ofType.kind !== 'SCALAR') {
                                addObject(metaInfo, fields, field.type.ofType.name, fieldName, ignoreFields, input);
                            } else {
                                fields.push(new Field(fieldName));
                            }

                        } else {
                            fields.push(new Field(fieldName));
                        }
                    }, this);
                }
            };
        let baseType = metaInfo.types.find((type) => type.name === model);
        if (baseType && baseType.possibleTypes && baseType.possibleTypes.length > 0) {
            baseType.possibleTypes.forEach(function (possibleType, index) {
                let fieldsInfo = new FieldsInfo({model: possibleType.name, select: [], subClass: true});
                addObject(metaInfo, fieldsInfo.select, possibleType.name, null, ignoreFields, input);
                fields.push(fieldsInfo);
            });
        } else {
            let fieldsInfo = new FieldsInfo({model: model, select: []});
            addObject(metaInfo, fieldsInfo.select, model, null, ignoreFields, input);
            fields.push(fieldsInfo);
        }
        return fields;
    }

    static executeSimpleQuery(queryName, params, success, error, recaptchaHash) {

        let optionHeaders = {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': cookies.get(' XSRF-TOKEN'),
            'X-Requested-With': 'XMLHttpRequest'
        }
        if (recaptchaHash) {
            optionHeaders['g-recaptcha-response'] = recaptchaHash;
        }
        let options = {
            url: `${REST_API_PREFIX_SIMPLE}/${queryName}`,
            method: 'post',
            headers: optionHeaders,
            params: params,
            data: params
        };
        return axios(options).then(function (response) {
            return success.call(this, response.status, response.data);

        }).catch(error);
    }


}