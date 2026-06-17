import {loadReferencesPromise} from '../actions/common/loadReferences';
import {EVENT_UPDATED_REFERENCES} from './Constants';
import Remote from './Remote';

const refMetaInfo = [
    {queryName: "educations", requestModel: "EducationModel", ignoreFields: []},
    {queryName: "languages", requestModel: "LanguageModel", ignoreFields: []},
    {queryName: "academicDegrees", requestModel: "AcademicDegreeModel", ignoreFields: []},
    {
        queryName: "categories",
        requestModel: "CategoryModel",
        ignoreFields: [{parent: 'subCategories', children: ['category']}]
    },
    {queryName: "notificationTypes", requestModel: "NotificationTypeDescriptorModelConnection", ignoreFields: []}
];

export default class References {
    static updateRefs(refs, loadedRefs, dispatch, metaInfo, scope, callback) {
        let loadingRefs = [];
        refs.forEach(function (ref) {
            if (!loadedRefs[ref]) {
                let refArray = refMetaInfo.filter((meta) => meta.queryName === ref);
                if (refArray.length > 0) {
                    let refInfo = refArray[0];
                    loadingRefs.push({
                        queryName: refInfo.queryName,
                        queryFilter: refInfo.queryFilter,
                        fields: Remote.getFieldsByModel(metaInfo, refInfo.requestModel, refInfo.ignoreFields)
                    });
                }

            }
        }, scope);
        if (loadingRefs.length > 0) {
            loadReferencesPromise(loadingRefs).then(function (result) {
                let res = result ? result : {};
                dispatch({type: EVENT_UPDATED_REFERENCES, references: res});
                if (callback) {
                    callback.call(scope, res);
                }
            });
        } else {
            if (callback) {
                callback.call(scope);
            }
        }
    }
}