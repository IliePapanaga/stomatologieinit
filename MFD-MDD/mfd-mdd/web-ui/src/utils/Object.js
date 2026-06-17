export default class ObjectHelper {
    static copyObject(origin) {
        return JSON.parse(JSON.stringify(origin));
    }

    static getValue(owner, fieldName, autogenerate) {
        var me = owner;

        if (!me) return null;

        var result;
        if (fieldName.indexOf(".") > -1) {
            if (autogenerate) {
                owner[fieldName.split(".")[0]] = owner[fieldName.split(".")[0]] || {};
            }
            result = ObjectHelper.getValue(owner[fieldName.split(".")[0]], fieldName.substring(fieldName.indexOf(".") + 1), autogenerate);
        } else {
            result = owner[fieldName];
        }

        return result;
    }

    static setValue(owner, fieldName, value, autogenerate) {
        var me = owner;

        if (!me) return null;

        var result;
        if (fieldName.indexOf(".") > -1) {
            if (autogenerate) {
                owner[fieldName.split(".")[0]] = owner[fieldName.split(".")[0]] || {};
            }
            ObjectHelper.setValue(owner[fieldName.split(".")[0]], fieldName.substring(fieldName.indexOf(".") + 1), value, autogenerate);
        } else {
            owner[fieldName] = value;
        }

        return result;
    }

    static isString(myVar) {
        if (myVar === null) {
            return false;
        }
        return (typeof myVar === 'string' || myVar instanceof String);
    }

    static isObject(myVar) {
        if (myVar === null) {
            return false;
        }
        return ((typeof myVar === 'function') || (typeof myVar === 'object'));
    }

    static isArray(myVar) {
        return Array.isArray(myVar);
    }

    static isFunction(input) {
        return input instanceof Function || Object.prototype.toString.call(input) === '[object Function]';
    }

    static move(arrayEl, element, offset) {
        let index = arrayEl.indexOf(element),
            newIndex = index + offset;
        if (newIndex > -1 && newIndex < arrayEl.length) {
            let removedElement = arrayEl.splice(index, 1)[0];
            arrayEl.splice(newIndex, 0, removedElement)
        }
    }
}