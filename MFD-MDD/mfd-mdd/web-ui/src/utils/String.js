export default class String {
    static capitalizeFirst(str) {
        if (!str) {
            return str;
        } else if (str.length === 1) {
            return str.toUpperCase();
        }

        return `${str.charAt(0).toUpperCase()}${str.substring(1, str.length)}`;
    }

    static clearUndersores(str){
        if (!str || str.indexOf('_') < 0) {
            return str;
        }
        return str.replace(/_/g, ' ');
    }

    
}