import React, {Component} from 'react';
import {connect} from 'react-redux';
import {
    DEFAULT_CELL_STYLE,
    DEFAULT_PER_PAGE,
    EVENT_FINISH_LOADING_DATA_INTO_GRID,
    EVENT_START_LOADING_DATA_INTO_GRID,
    SELECTION_MODE_MULTY,
    SELECTION_MODE_NONE,
    SELECTION_MODE_SINGLE,
    STATUS_ACTIVE
} from '../../../utils/Constants';
import {Direction, Field as QueryField, Order, Pagination, Query, QueryResult} from '../../../models/core/QueryInfo';
import loadData from '../../../actions/common/loadData';
import ObjectHelper from '../../../utils/Object';
import SelectField from './form/SelectField';
import Field from './form/Field';
import {reduxForm} from 'redux-form';
import Renderer from '../../../utils/Renderer';

const SELECT_ALL = 'all';

let PaginationForm = props => {
    const {
        onGoToPage, onChangePerPage, onPrev, onNext, onFirst, onLast, onRefresh, onStartsWith, initialValues: {
            page, pages, firstItemOnPage, lastItemOnPage, total, activateStartsWith, startsWith
        }
    } = props;

    const startLetters = ["a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"];
    return (
        <form class="board__navigation">
            <div class="board__pagination" key="base_grid_board__pagination">
                <div class="board__pagination-page">
                    <span>Records per Page&nbsp;</span>
                    {/* <div class="input board__input-wrapper">*/}
                    <Field hideEmpty={true} name="perPage" component={SelectField} menuItems={
                        [
                            {value: 25, name: 25},
                            {value: 50, name: 50},
                            {value: 100, name: 100}
                        ]} className="board__pagination-page-active" onChange={onChangePerPage}/>
                    {/*  </div>*/}
                </div>
                &nbsp;
                <div class="board__pagination-page">
                    <span>Page</span>
                    <Field hideEmpty={true} name="page" component="input" className="board__pagination-page-active"
                           onChange={onGoToPage}/>
                    <span>&nbsp; of {pages}</span>
                </div>
                <div class="board__pagination-arrows">

                    <div disabled={page <= 1} onClick={page > 1 ? onFirst : undefined}>
                        <div>
                            <svg class="pag__icon">
                                <use xlinkHref="#left"></use>
                            </svg>
                        </div>
                    </div>

                    <div disabled={page <= 1} onClick={page > 1 ? onPrev : undefined}>
                        <div>
                            <svg class="pag__icon">
                                <use xlinkHref="#left-arrow"></use>
                            </svg>
                        </div>
                    </div>

                    <div disabled={page >= pages} onClick={page < pages ? onNext : undefined}>
                        <div>
                            <svg class="pag__icon">
                                <use xlinkHref="#right-arrow-end"></use>
                            </svg>
                        </div>
                    </div>

                    <div disabled={page >= pages} onClick={page < pages ? onLast : undefined}>
                        <div>
                            <svg class="pag__icon">
                                <use xlinkHref="#right"></use>
                            </svg>
                        </div>
                    </div>

                    <div onClick={onRefresh}>
                        <div>
                            <svg class="pag__icon">
                                <use xlinkHref="#refresh"></use>
                            </svg>
                        </div>
                    </div>

                    {firstItemOnPage > 0 &&
                    <span>{`Displaying ${firstItemOnPage} - ${lastItemOnPage} of ${total}`}</span>}
                </div>
            </div>


            {activateStartsWith && <div class="board__key-nav">
                <div class={!startsWith ? 'board__key-nav-item board__key-nav-item-active' : 'board__key-nav-item'}
                     onClick={onStartsWith}>All
                </div>
                <div class="board__key-nav-wrapper">

                    {startLetters.map((letter) => (
                        <div
                            class={startsWith === letter ? 'board__key-nav-item board__key-nav-item-active' : 'board__key-nav-item'}
                            onClick={onStartsWith}>{letter}</div>
                    ))}
                </div>
            </div>}
        </form>
    )
}

class BaseGrid extends Component {

    constructor(props) {
        super(props);

        let {onRef, columns, url, queryName, selectionMode, additionalFields, hasPagination = true, localOrdering = false} = props;

        onRef(this);

        let initialPagination = this.configurePagination(columns, hasPagination);

        let select: Array<QueryField> = [];

        if (additionalFields) {
            additionalFields.forEach(function (additionalField) {
                select.push(new QueryField(additionalField));
            }, this);
        }

        columns.forEach(function (column) {
            select.push(new QueryField(column.dataIndex));
        }, this);

        let query = queryName ? new Query({
            endpoint: url,
            name: queryName,
            select: select,
            pagination: initialPagination
        }) : undefined;

        let queryResult = new QueryResult();

        this.state = {
            columns: props.columns,
            query: query,
            queryResult: queryResult,
            selection: [],
            params: props.params,
            filters: [],
            selectionMode: selectionMode || SELECTION_MODE_MULTY
        }

        if (localOrdering) {
            this.state['orderInfo'] = undefined;
            this.state['orders'] = this.configureOrders(columns);

        }

    }

    componentDidMount() {
        this.onLoadData();
    }

    configureOrders(columns) {
        let orders: Array<Order> = [];
        //let sortOrder = 0;

        columns.forEach(function (element, index) {
            if (element.sortable) {
                orders.push(new Order({
                    field: element.dataIndex,
                    direction: Direction.ASC,
                    nullsFirst: true/*, sortOrder: sortOrder++ */
                }));
                element.orderName = element.dataIndex;
            }
        }, this);

        return orders;
    }

    configurePagination(columns, allowed) {
        let orders: Array<Order> = [];
        //let sortOrder = 0;

        columns.forEach(function (element, index) {
            if (element.orderName) {
                orders.push(new Order({
                    field: element.orderName,
                    direction: Direction.ASC,
                    nullsFirst: true/*, sortOrder: sortOrder++ */
                }));
            }
        }, this);

        return new Pagination({
            allowed: allowed,
            page: 0,
            perPage: DEFAULT_PER_PAGE,
            orders: orders
        });
    }


    onLoadData(filters = null) {
        let me = this;
        const {query, params} = me.state;
        if (!query) {
            return false;
        }
        query.parameters = {};
        if (params) {
            query.parameters = Object.assign(query.parameters, params);
        }

        if (filters) {
            query.parameters = Object.assign(query.parameters, filters);
            me.setState(...this.state, {filters: filters});
        } else if (me.state.filters) {
            query.parameters = Object.assign(query.parameters, me.state.filters);
        }

        if (query) {
            me.props.startLoading(me.state);
            loadData(query).then(function (queryResult) {
                if (queryResult) {
                    me.setState(...me.state, {queryResult: queryResult});
                    if(me.props.changeSelection){
                        me.props.changeSelection([]);
                    }
                }
                me.props.finishLoading(me.state);

            });
        }
    }

    getOrder(pagination, column) {
        if (this.props.localOrdering) {
            return this.state.orders.filter((order) => order.field === column.dataIndex)[0];
        } else {
            return pagination.orders.filter((order) => order.field === column.orderName)[0];
        }
    }

    onSort(column, columnIndex) {
        let me = this;
        let state = me.state;

        if (me.props.localOrdering) {
            let orders = state.orders;
            orders.forEach(function (order) {
                order.nullsFirst = true;
            }, this);

            let order = me.getOrder(undefined, column);
            order.direction = order.direction === Direction.ASC ? Direction.DESC : Direction.ASC;
            order.nullsFirst = false;
            me.setState(...this.state, {
                orders: orders,
                orderInfo: {dataIndex: column.dataIndex, direction: order.direction}
            });
        } else {
            let query = state.query;
            let pagination = query.pagination;

            pagination.orders.forEach(function (order) {
                order.nullsFirst = true;
            }, this);

            let order = me.getOrder(pagination, column);
            order.direction = order.direction === Direction.ASC ? Direction.DESC : Direction.ASC;
            order.nullsFirst = false;
            me.setState(...this.state, {query: query});
            me.onLoadData();
        }
    }

    onNext() {
        let me = this;
        let state = me.state;
        let query = state.query;
        let pagination = query.pagination;
        pagination.page++;
        me.setState(...this.state, {query: query});
        me.onLoadData();
    }

    onRefresh() {
        let me = this;
        let state = me.state;
        let query = state.query;
        me.setState(...this.state, {query: query});
        me.onLoadData();
    }

    onStartsWith(e) {
        let me = this;
        let value = e.target.innerHTML;
        let filters = me.state.filters || {};
        value = value === 'All' ? undefined : value;
        filters['nameStartsWith'] = value;
        me.onLoadData(filters);
    }

    onPrev() {
        let me = this;
        let state = me.state;
        let query = state.query;
        let pagination = query.pagination;
        pagination.page--;
        me.setState(...this.state, {query: query});
        me.onLoadData();
    }

    onFirst() {
        let me = this;
        let state = me.state;
        let query = state.query;
        let pagination = query.pagination;
        pagination.page = 0;
        me.setState(...this.state, {query: query});
        me.onLoadData();
    }

    onLast(pages) {
        let me = this;
        let state = me.state;
        let query = state.query;
        let pagination = query.pagination;
        pagination.page = pages-1;
        me.setState(...this.state, {query: query});
        me.onLoadData();
    }

    onGoToPage(pages, event) {
        let me = this;
        let page = Number.parseInt(event.target.value, 10);
        if (page >= 1 && page <= pages) {
            let state = me.state;
            let query = state.query;
            let pagination = query.pagination;
            pagination.page = page - 1;
            me.setState(...this.state, {query: query});
            me.onLoadData();
        }
    }

    onChangePerPage(event) {
        let me = this;
        let perPage = Number.parseInt(event.target.value, 10);
        let state = me.state;
        let query = state.query;
        let pagination = query.pagination;
        pagination.perPage = perPage;
        me.setState(...this.state, {query: query});
        me.onLoadData();

    }

    onChangeSelection(event) {
        let me = this;
        let checked = event.target.checked;
        let index = event.target.id.substr(event.target.id.indexOf('-') + 1);
        let rows = index === SELECT_ALL ? me.state.queryResult.nodes : [me.state.queryResult.nodes[index]];
        let oldSelection = me.state.selectionMode === SELECTION_MODE_MULTY ? me.state.selection : [];
        let newSelection = undefined;

        if (checked) {
            newSelection = oldSelection.concat(rows);
        } else {
            newSelection = oldSelection.filter(function (row) {
                return !rows.includes(row);
            });
        }
        me.setState({selection: newSelection});
        if(me.props.changeSelection){
            me.props.changeSelection(newSelection);
        }
    }

    onSelectRow(index, event) {
        let me = this;
        let rows = undefined;
        let checked = false;
        let oldSelection = me.state.selection || [];
        if (index === SELECT_ALL) {
            checked = oldSelection.length !== me.state.queryResult.nodes.length;
            rows = me.state.queryResult.nodes;
        } else {
            checked = !oldSelection.includes(me.state.queryResult.nodes[index]);
            rows = [me.state.queryResult.nodes[index]];
        }

        let newSelection = undefined;

        if (checked) {
            if (me.state.selectionMode === SELECTION_MODE_SINGLE || me.state.selectionMode === SELECTION_MODE_NONE) {
                newSelection = rows;
            } else if (me.state.selectionMode === SELECTION_MODE_MULTY) {
                newSelection = oldSelection.concat(rows);
            }
        } else {
            newSelection = oldSelection.filter(function (row) {
                return !rows.includes(row);
            });
        }

        me.setState({selection: newSelection});
        if(me.props.changeSelection) {
            me.props.changeSelection(newSelection);
        }

    }

    onDoubleClickOnRow(index, showRow, event) {
        event.stopPropagation();
        let me = this;

        if (!showRow) {
            return false;
        }


        let target = document.getElementById(`check-${index}`);
        let targetChecked = target.checked;
        let rows = index === SELECT_ALL ? me.state.queryResult.nodes : [me.state.queryResult.nodes[index]];
        let oldSelection = me.state.selectionMode === SELECTION_MODE_MULTY ? me.state.selection : [];
        let newSelection = undefined;

        if (!targetChecked) {
            newSelection = oldSelection.concat(rows);
            me.setState({selection: newSelection});
            if(me.props.changeSelection) {
                me.props.changeSelection(newSelection);
            }
        }
        setTimeout(function () {
            showRow(me.state.queryResult.nodes[index], index, event);
        }, 500);

    }

    updateRow(originRow, updatedRow) {
        let me = this;
        if (!updatedRow) {
            me.onLoadData();
            return false;
        }
        Object.assign(originRow, updatedRow);
        me.setState(...this.state, {queryResult: me.state.queryResult});
        if(me.props.changeSelection) {
            me.props.changeSelection([originRow]);
        }

    }

    addRow(newRow) {
        let me = this;
        if (!newRow) {
            me.onLoadData();
            return false;
        }
        if (ObjectHelper.isArray(newRow)) {

            newRow.forEach(element => {
                me.state.queryResult.nodes.unshift(element);
            });

            me.state.queryResult.count += newRow.length;

        } else {
            me.state.queryResult.nodes.unshift(newRow);
            me.state.queryResult.count++;
        }


        me.setState(...this.state, {queryResult: me.state.queryResult});
    }

    deleteRows(deletingRows) {
        let me = this;

        me.state.queryResult.nodes = me.state.queryResult.nodes.filter(function (row) {
            return !deletingRows.includes(row);
        });
        me.state.queryResult.count--;
        me.setState(...this.state, {queryResult: me.state.queryResult});
        if(me.props.changeSelection){
            me.props.changeSelection([]);
        }
    }

    changeFilters(filters) {
        this.onLoadData(filters);
    }

    getData() {
        let me = this;
        return me.state.queryResult.nodes
    }

    defaultCellRenderer(value, cellClass, row, columnIndex, rowIndex) {
        return <div class={cellClass}>{value}</div>;
    }

    renderCell(value, row, column, columnIndex, rowIndex, dataIndex) {
        if (column.renderer) {
            return column.renderer(value, column.cellClass || DEFAULT_CELL_STYLE, row, columnIndex, rowIndex, dataIndex);
        } else {
            return this.defaultCellRenderer(value, column.cellClass || DEFAULT_CELL_STYLE, row, columnIndex, rowIndex, dataIndex);
        }
    }

    //onChange={this.onChangeSelection.bind(this)}

    renderRow(columns, row, rowIndex, selection, showRow) {
        return [<tr onDoubleClick={this.onDoubleClickOnRow.bind(this, rowIndex, showRow)}
                    class={"row__btn row__btn-" + Renderer.getRowStatusClassPrefix(row.status || STATUS_ACTIVE)+(selection.includes(row)?' pressed':'')}
                    key={`row-${rowIndex}`} onClick={this.onSelectRow.bind(this, rowIndex)}>
            {this.state.selectionMode !== SELECTION_MODE_NONE && <td class="row">
                <div class="row__check">
                    <input type="checkbox" checked={selection.includes(row)} name="check" id={`check-${rowIndex}`}/>
                    <label htmlFor={`check-${rowIndex}`}></label>
                </div>
            </td>}
            {columns.map((column, columnIndex) => (
                (!column.hidden) &&
                <td key={`td-${columnIndex}`}>{this.renderCell(ObjectHelper.getValue(row, column.dataIndex), row, column, columnIndex, rowIndex, column.dataIndex)}</td>
            ))}
        </tr>,
        ];
    }

    render() {
        let me = this;
        const {showRow, params} = this.props;
        const {
            columns, query, queryResult, selection, selectionMode, orderInfo
        } = me.state;
        if (!query) {
            return (<div></div>);
        }
        let items = queryResult.nodes || [];

        if (orderInfo) {
            items = items.sort(function (a, b) {
                let value1 = ObjectHelper.getValue(a, orderInfo.dataIndex, true),
                    value2 = ObjectHelper.getValue(b, orderInfo.dataIndex, true);
                if (orderInfo.direction === Direction.DESC ? value1 > value2 : value2 > value1) {
                    return -1;
                }
                if (orderInfo.direction === Direction.DESC ? value2 > value1 : value1 > value2) {
                    return 1;
                }
                return 0;
            });
        }

        const total = queryResult.count;

        const pagination = query.pagination;
        const perPage = pagination.perPage;
        const parameters = query.parameters;
        const activateStartsWith = params && params.hasOwnProperty('nameStartsWith');


        let page = pagination.page + 1;
        let pages = Math.floor(total / perPage);
        let firstItemOnPage = (page - 1) * perPage + 1;
        if (items.length === 0) {
            pages = 1;
            firstItemOnPage = 0;
        } else {
            if (total % perPage > 0) {
                pages++;
            }
        }

        let lastItemOnPage = firstItemOnPage + perPage - 1;
        if (lastItemOnPage > total) {
            lastItemOnPage = total;
        }


        const getColumnOrderStyle = function (pagination, column) {
            let direction = me.getOrder(pagination, column).direction;
            let prefix = direction === Direction.DESC ? ' row__select-arrow-active' : '';
            return `row__select-arrow${prefix}`;
        }

        let id = Date.now();

        const Form = reduxForm({
            form: `pagination-${id}`
        })(PaginationForm);

        return (
            [<div class="board__table" key="base_grid_board__table">

                <table>
                    <thead>
                    <tr>
                        {selectionMode !== SELECTION_MODE_NONE &&
                        <th data-resizable-column-id="id" class="row" key="th-sel">
                            {selectionMode === SELECTION_MODE_MULTY &&
                            <div class="row__check" key="row__check-sel">
                                <input type="checkbox" checked={selection.length >= (total < perPage ? total : perPage)}
                                       onChange={this.onChangeSelection.bind(this)} name="check"
                                       id={`check-${SELECT_ALL}`}/>
                                <label for={`check-${SELECT_ALL}`}></label>
                            </div>
                            }
                        </th>}


                        {columns.map((column, columnIndex) => (
                            (!column.hidden) &&
                            <th data-resizable-column-id={`id${columnIndex}`} class="row" width={column.width}
                                key={`th-${columnIndex}`}>
                                <div class="row__select"
                                     onClick={column.orderName ? this.onSort.bind(this, column, columnIndex) : undefined}
                                     key={`row__select-${columnIndex}`}>
                                    <div>{column.title}</div>
                                    {column.orderName && <div class={getColumnOrderStyle(pagination, column)}></div>}
                                </div>
                            </th>
                        ))}

                    </tr>
                    </thead>
                    <tbody>
                    {items.map((row, rowIndex) => (
                        this.renderRow(columns, row, rowIndex, selection, showRow)
                    ))}
                    </tbody>
                </table>
            </div>,
                pagination.allowed && <Form initialValues={
                    {
                        perPage: perPage,
                        page: page,
                        pages: pages,
                        firstItemOnPage: firstItemOnPage,
                        lastItemOnPage: lastItemOnPage,
                        total: total,
                        activateStartsWith: activateStartsWith,
                        startsWith: parameters ? parameters.nameStartsWith : undefined
                    }}
                                            onGoToPage={this.onGoToPage.bind(this, pages)}
                                            onChangePerPage={this.onChangePerPage.bind(this)}
                                            onPrev={this.onPrev.bind(this)}
                                            onNext={this.onNext.bind(this)}
                                            onFirst={this.onFirst.bind(this)}
                                            onLast={this.onLast.bind(this, pages)}
                                            onRefresh={this.onRefresh.bind(this)}
                                            onStartsWith={this.onStartsWith.bind(this)}/>

            ]
        );
    }
}

const GridConnector = connect(
    (state, ownProps) => ({}),
    dispatch => ({
        startLoading: (state) => {
            dispatch({type: EVENT_START_LOADING_DATA_INTO_GRID, queryInfo: state.queryInfo});
        },
        finishLoading: (state) => {
            dispatch({
                type: EVENT_FINISH_LOADING_DATA_INTO_GRID,
                queryInfo: state.queryInfo,
                queryResult: state.queryResult
            });
        }/*,
        changeSelection(selection) {
            dispatch({type: EVENT_CHANGE_SELECTION_IN_GRID, selection});
        }*/
    }))(BaseGrid);

export default GridConnector;