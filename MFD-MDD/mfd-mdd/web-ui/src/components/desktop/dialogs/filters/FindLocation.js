/*global google*/
import React from 'react';
import {connect} from 'react-redux';
import Dialog from 'react-dialog';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import {GOOGLE_API_KEY} from '../../../../utils/Constants';

import {reduxForm} from 'redux-form';
import Field from '../../common/form/Field';
import SelectField from '../../common/form/SelectField';
import {COMMUTING_RADIUS_FOR_SEARCH} from '../../../../data/CommutingRadius';
import {Logger} from 'react-logger-lib';

var VisibilitySensor = require('react-visibility-sensor');
export const dialogInfo = {}


const _ = require("lodash");
const {compose, withProps, lifecycle} = require("recompose");
const {
    withScriptjs,
    withGoogleMap,
    GoogleMap,
    Marker,
} = require("react-google-maps");
const {SearchBox} = require("react-google-maps/lib/components/places/SearchBox");

const LocationMapComponent = compose(
    withProps({
        googleMapURL: `https://maps.googleapis.com/maps/api/js?key=${GOOGLE_API_KEY}&v=3.exp&libraries=geometry,drawing,places`,
        loadingElement: <div style={{height: `100%`}}/>,
        containerElement: <div style={{height: `${650}px`}}/>,
        /*  containerElement: <div style={{ height: `${window.innerHeight - 240}px` }} />,*/
        mapElement: <div style={{height: `100%`}}/>,
    }),
    lifecycle({
        componentWillReceiveProps(props) {
            this.setState({
                selection: props.selection
            });
        },
        componentWillMount() {
            const refs = {}

            this.setState({
                bounds: null,
                center: {
                    lat: 37.25022, lng: -119.75126
                },
                markers: [],
                onMapMounted: ref => {
                    refs.map = ref;
                },
                onBoundsChanged: () => {
                    this.setState({
                        bounds: refs.map.getBounds()/*,
                        center: refs.map.getCenter(),*/
                    })
                },
                onSearchBoxMounted: ref => {
                    refs.searchBox = ref;
                },
                onPlacesChanged: () => {
                    const places = refs.searchBox.getPlaces();
                    const bounds = new google.maps.LatLngBounds();

                    places.forEach(place => {
                        if (place.geometry.viewport) {
                            bounds.union(place.geometry.viewport)
                        } else {
                            bounds.extend(place.geometry.location)
                        }
                    });
                    const nextMarkers = places.map(place => ({
                        position: place.geometry.location,
                    }));
                    const nextCenter = _.get(nextMarkers, '0.position', this.state.center);


                    let selection = undefined;
                    if (nextMarkers && nextMarkers.length) {
                        selection = nextMarkers[0].position;
                        nextMarkers.shift();
                    }

                    this.setState({
                        center: nextCenter,
                        markers: nextMarkers,
                        selection: selection
                    });
                    this.props.selectPoint({latLng: selection});
                    refs.map.fitBounds(bounds);
                },
            })
        },
    }),
    withScriptjs,
    withGoogleMap
)(props =>
    <GoogleMap
        ref={props.onMapMounted}
        defaultZoom={10}
        center={props.center}
        onBoundsChanged={props.onBoundsChanged} onClick={props.selectPoint}
    >
        <SearchBox
            ref={props.onSearchBoxMounted}
            bounds={props.bounds}
            controlPosition={google.maps.ControlPosition.TOP_LEFT}
            onPlacesChanged={props.onPlacesChanged}
        >
            <input
                type="text" onKeyPress={e => {
                if (e.key === 'Enter') e.preventDefault();
            }}
                placeholder="Search Address..."
                style={{
                    boxSizing: `border-box`,
                    border: `1px solid transparent`,
                    width: `240px`,
                    height: `32px`,
                    marginTop: `27px`,
                    padding: `0 12px`,
                    borderRadius: `3px`,
                    boxShadow: `0 2px 6px rgba(0, 0, 0, 0.3)`,
                    fontSize: `14px`,
                    outline: `none`,
                    textOverflow: `ellipses`,
                }}
            />
        </SearchBox>


        {props.selection && props.selection.lat && props.selection.lng &&
        <Marker key="foundLocation" position={props.selection}/>}
        {props.markers.map((marker, index) =>
            <Marker key={index} position={marker.position}/>
        )}


    </GoogleMap>
);

let Form = props => {
    const {handleSubmit, change, handleCancel, invalid, dialog, lat, lng, showDistance} = props;
    let selectPoint = function (ev) {
        if (ev.latLng) {
            change('managedObject.lat', ev.latLng.lat());
            change('managedObject.lng', ev.latLng.lng());
            dialog.setState({
                lat: ev.latLng.lat(),
                lng: ev.latLng.lng()
            });
        }
    }
    let selection = {lat: lat, lng: lng};
    return (
        <form onSubmit={handleSubmit} class="location__form">


            {showDistance && <div class="info location__info">
                <div class="text">
                    <p>Radius:</p>
                </div>
                <div class="input board__input-wrapper">
                    <Field name="managedObject.distance" component={SelectField} menuItems={COMMUTING_RADIUS_FOR_SEARCH}
                           required={true}/>
                </div>

            </div>}


            <LocationMapComponent selectPoint={selectPoint} selection={selection}/>
            <div class="footer__btn">
                <button class="blue white" type="button" onClick={handleCancel}>Cancel</button>
                <button class="blue" type="button" onClick={handleSubmit} disabled={invalid || !(lat && lng)}>Ok
                </button>
            </div>
        </form>)
}

Form = reduxForm({
    form: 'findlocation'
})(Form);

class FindLocation extends BaseDialog {
    state = {
        lat: undefined,
        lng: undefined
    }

    componentDidMount() {
        let me = this;

        if (me.props.managedObject) {

            Logger.of('App.FindLocation.componentDidMount').info(`Coordinates: (${me.props.managedObject.lat},${me.props.managedObject.lng})`);

            me.setState({
                lat: me.props.managedObject.lat,
                lng: me.props.managedObject.lng
            })
        }

    }

    dialogProps() {
        return {
            width: window.innerWidth,
            height: window.innerHeight,
            className: "modal__gray",
            title: "Location"
        }
    }

    beforeSave(dialog, managedData) {
        managedData.managedObject.distance = Number.parseFloat(managedData.managedObject.distance);
        return true;
    }

    render() {
        const {height, className, title} = this.dialogProps();
        const {lat, lng} = this.state;
        let coordinates = undefined;
        if (lat && lng) {
            coordinates = `(${lat}; ${lng})`;
        }

        return (<Dialog position={this.getPosition(800, height)} className={className} width={"800px"} height={height}
                        title={<div class="modal__header handle">
                            <div class="modal__header-icon">
                                <div>
                                    <svg class="close__icon" onClick={this.close.bind(this)}>
                                        <use xlinkHref="#Close"></use>
                                    </svg>
                                </div>
                            </div>
                            <h2> {title} {coordinates}</h2>
                        </div>} modal={true} isDraggable={true} isResizable={false} ref={this.props.uid}>

                {this.renderDialogContent()}
                <VisibilitySensor onChange={this.onVisible.bind(this)}/>
            </Dialog>
        );
    }

    renderDialogContent() {
        const {showDistance = true} = this.props;
        let managedObject = Object.assign({}, this.props.managedObject);
        return (<Form onSubmit={this.onSave} handleCancel={this.close} dialog={this}
                      initialValues={{managedObject: managedObject}} lat={this.state.lat} lng={this.state.lng}
                      showDistance={showDistance}/>);
    }

}

const FindLocationDialogConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {});
    },
    function (dispatch) {
        return Object.assign(baseDispatcherMap(dispatch), {
            onSend: (sendData, d, form) => {
            }
        });
    })(FindLocation);

export default FindLocationDialogConnector;
