import React from 'react';
import {TIME_AM, TIME_PM} from '../../../../utils/Constants';
import onClickOutside from 'react-onclickoutside';


const EMPTY = '-';

class TimeField extends React.Component {
    static idCounter = 1;
    id = undefined;

    state = {
        hours: EMPTY,
        minutes: EMPTY,
        am_pm: EMPTY
    };

    componentDidMount() {
        this.id = `time${++TimeField.idCounter}`;

        const {input, time} = this.props;

        let value = input.value || time;

        this.initState(value);
    }

    showMenu(e) {
        if (!this.props.disabled && !this.props.readOnly) {
            if (e.target.offsetWidth - e.nativeEvent.offsetX <= 30) {
                this.baseMenu.classList.toggle("hidden");
            }
        }
    }

    closeMenu(e) {
        this.baseMenu.classList.toggle("hidden");
    }

    handleClickOutside = () => {
        this.baseMenu.classList.add("hidden");
    };

    initState(value) {
        let newState = {
            hours: EMPTY,
            minutes: EMPTY,
            am_pm: EMPTY
        };

        if (value) {
            let timeParts = value.split(':');
            let hours = Number.parseInt(timeParts[0], 10);
            let minutes = Number.parseInt(timeParts[1], 10);

            newState.am_pm = TIME_AM;

            if (hours > 12) {
                newState.am_pm = TIME_PM;
                hours -= 12;
            }

            newState.hours = (hours < 10 ? `0${hours}` : hours);
            newState.minutes = (minutes < 10 ? `0${minutes}` : minutes);
            if (!this.props.input.value) {
                this.props.input.onChange(value);
            }
        }

        this.setState(newState);
    }

    handleChange(e) {
        let me = this;
        const {step = 1} = this.props;
        let value = e.target.value;

        let timeParts = value.split(':');

        let minutes = Number.parseInt(timeParts[1], 10);

        let arr = [0, 1, 3, 4, 15, 30, 45];

        //console.log(me.props.input.value,value);

        if (!arr.includes(minutes)/* || minutes % step != 0*/) {

            e.stopPropagation();

            e.nativeEvent.stopImmediatePropagation();

            return false;

        } else {
            if (arr.includes(minutes)) {
                switch (minutes) {
                    case 0:
                        value = `${timeParts[0]}:00`;
                        break;
                    case 1:
                    case 15:
                        value = `${timeParts[0]}:15`;
                        break;
                    case 3:
                    case 30:
                        value = `${timeParts[0]}:30`;
                        break;
                    case 4:
                    case 45:
                        value = `${timeParts[0]}:45`;
                        break;
                }
            }
            me.initState(value);

            me.props.input.onChange(value);

        }
    }

    handleChangeRegister(up, h, m, a, e) {
        const {step = 1} = this.props;

        let changeHours = function (hours, up, a) {
            let tmpHours = undefined;
            if (hours === '-') {
                tmpHours = up ? 1 : 12;
            } else {
                if (up) {
                    tmpHours = hours < 12 ? hours + 1 : 1;
                } else {
                    tmpHours = hours > 1 ? hours - 1 : 12;
                }
            }
            return tmpHours;
        };

        let {hours, minutes, am_pm} = this.state;

        if (hours !== '-') {
            hours = Number.parseInt(hours, 10);
        }

        if (minutes !== '-') {
            minutes = Number.parseInt(minutes, 10);
        }

        if (h) {
            hours = changeHours(hours, up, a);
        }

        if (up) {
            if (m) {
                if (minutes === '-') {
                    minutes = 0;
                } else {
                    if (minutes < 60 - step) {
                        minutes += step;
                    } else {
                        minutes = 0;
                    }
                }
            }
            if (a) {
                if (am_pm === '-') {
                    am_pm = TIME_AM;
                } else {
                    am_pm = am_pm === TIME_AM ? TIME_PM : TIME_AM;
                }
            }
        } else {
            if (m) {
                if (minutes === '-') {
                    minutes = (60 - step);
                } else {
                    minutes = Number.parseInt(minutes, 10);
                    if (minutes - step >= 0) {
                        minutes -= step;
                    } else {
                        minutes = (60 - step);
                    }
                }
            }

            if (a) {
                if (am_pm === '-') {
                    am_pm = TIME_PM;
                } else {
                    am_pm = am_pm === TIME_AM ? TIME_PM : TIME_AM;
                }
            }
        }

        let newState = {
            hours: (hours < 10 ? `0${hours}` : hours),
            minutes: (minutes < 10 ? `0${minutes}` : minutes),
            am_pm: am_pm
        };

        this.setState(newState);

        if (hours !== '-' && minutes !== '-' && am_pm !== '-') {
            if (am_pm === TIME_PM) {
                if (hours < 12) {
                    hours += 12;
                }
            }
            if (am_pm === TIME_AM) {
                if (hours === 12) {
                    hours = 0;
                }
            }
            hours = (hours < 10 ? `0${hours}` : hours);
            minutes = (minutes < 10 ? `0${minutes}` : minutes);
            this.timeField.value = `${hours}:${minutes}`;
            this.props.input.onChange(this.timeField.value);
        }

    }

    getError(meta) {
        return meta.active && meta.touched && meta.error && <span data-style="">{meta.error}</span>
    }

    render() {
        const {input, meta, required, time} = this.props;
        const {hours, minutes, am_pm} = this.state;
    /*    const formattedTime = () => {
            if (time === undefined) return input.value;
            const [hours, minutes] = time.split(":");
            const timeObj = new Date();
            timeObj.setHours(hours);
            timeObj.setMinutes(minutes);
            const ampm = timeObj.getHours() >= 12 ? "PM" : "AM";
            let hours12 = timeObj.getHours() % 12;
            hours12 = hours12 ? hours12 : 12; // convert 0 to 12
            return `${hours12}:${minutes} ${ampm}`;
        };
*/

        let className = "board__input board__input-time";
        if (((input.value || time) && required) || meta.error) {
            className += meta.error ? ` input__false` : ` input__true`;
        }
        return (
            <div class="input__time">
                <input disabled={this.props.disabled || this.props.readOnly} id={this.id} type="text"
                       value={input.value || time}
                       datetime-local class={className} step={this.props.step ? this.props.step * 60 : 1}
                       onClick={this.showMenu.bind(this)} onChange={this.handleChange.bind(this)} ref={(input) => {
                    this.timeField = input
                }}/>
         {       <div class="time__edit hidden" ref={(div) => {
                    this.baseMenu = div
                }}>
                    <div class="time__edit-content">
                        <div class="time__edit-item">
                            <button class="time__top" type="button"
                                    onClick={this.handleChangeRegister.bind(this, true, true, false, false)}>
                                <p>&#9650;</p></button>
                            <div>{hours}</div>
                            <button class="time__bottom" type="button"
                                    onClick={this.handleChangeRegister.bind(this, false, true, false, false)}>
                                <p>&#9660;</p></button>
                        </div>
                        <div class="time__edit-item-dots">:</div>
                        <div class="time__edit-item">
                            <button class="time__top" type="button"
                                    onClick={this.handleChangeRegister.bind(this, true, false, true, false)}>
                                <p>&#9650;</p></button>
                            <div>{minutes}</div>
                            <button class="time__bottom" type="button"
                                    onClick={this.handleChangeRegister.bind(this, false, false, true, false)}>
                                <p>&#9660;</p></button>
                        </div>
                        <div class="time__edit-item time__edit-item-meridiem">
                            <button class="time__top" type="button"
                                    onClick={this.handleChangeRegister.bind(this, true, false, false, true)}>
                                <p>&#9650;</p></button>
                            <div>{am_pm}</div>
                            <button class="time__bottom" type="button"
                                    onClick={this.handleChangeRegister.bind(this, false, false, false, true)}>
                                <p>&#9660;</p></button>
                        </div>
                    </div>
                    <div class="time__edit-btn">
                        <button type="button" onClick={this.closeMenu.bind(this)}>Ok</button>
                    </div>
                </div>}
            </div>
        )
    }
}

export default onClickOutside(TimeField)