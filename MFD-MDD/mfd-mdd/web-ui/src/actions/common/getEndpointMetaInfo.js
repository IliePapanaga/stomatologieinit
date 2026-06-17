import Remote from '../../utils/Remote';
import Error from '../../utils/Error';
import {RESPONSE_OK_STATUS} from '../../utils/Constants';
import ObjectHelper from '../../utils/Object';
import {Logger} from 'react-logger-lib';

const getEndpointMetaInfo = () => {
  Logger.of('App.getEndpointMetaInfo').info('getting...');
  return new Promise((resolve) => {
    let query = `query
  IntrospectionQuery {
    __schema {
      types {
        ...FullType
      }
    }
  }
  fragment
  FullType
  on
  __Type {
    name
    fields(includeDeprecated: true) {
      name
      args {
        ...InputValue
      }
      type {
        ...TypeRef
      }
      isDeprecated
      deprecationReason
    }
    inputFields {
      ...InputValue
    }
    interfaces {
      ...TypeRef
    }
    enumValues(includeDeprecated: true) {
      name
      description
      isDeprecated
      deprecationReason
    }
    possibleTypes {
      ...TypeRef
    }
  }
  fragment
  InputValue
  on
  __InputValue {
    name
    description
    type {
      ...TypeRef
    }
    defaultValue
  }
  fragment
  TypeRef
  on
  __Type {
    kind
    name
    ofType {
      kind
      name
      ofType {
        kind
        name
        ofType {
          kind
          name
          ofType {
            kind
            name
            ofType {
              kind
              name
              ofType {
                kind
                name
                ofType {
                  kind
                  name
                }
              }
            }
          }
        }
      }
    }
  }`;

    return Remote.executeQuery(query, function (status, metaInfo) {
      switch (status) {
        case RESPONSE_OK_STATUS:
          if (ObjectHelper.isObject(metaInfo)) {
            resolve(metaInfo);
          } break;

        default:

      }
      resolve(undefined);
    }, function (response) {
      Error.showRemoteErrors(response);
      resolve(undefined);
    }, true);
  });
}

export default getEndpointMetaInfo;