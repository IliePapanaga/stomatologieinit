import {Enum} from 'enumify';

export class Query {
    endpoint: string;
    name: string;
    pagination: Pagination;
    parameters: Object;
    select: Array<Field> = [];


    constructor(fields?: {
        endpoint?: string,
        name?: string,
        pagination?: Pagination,
        parameters?: Object,
        select?: Array<Field>
    }) {

        if (fields) {
            Object.assign(this, fields);
        }
    }
}

export class QueryResult<T> {
    pagination: Pagination;
    result: Array<T>;

    constructor(fields?: {
        nodes?: Array<T>,
        count: number
    }) {

        if (fields) {
            Object.assign(this, fields);
        }
    }
}

export class Pagination {
    allowed:boolean;
    total: number;
    page: number;
    perPage: number;
    orders: Array<Order> = [];
    totalApproximate: boolean;
    lastId: string; // any

    constructor(fields?: {
        total?: number,
        page?: number,
        perPage?: number,
        orders?: Array<Order>,
        totalApproximate?: boolean,
        lastId?: string,
        allowed?: boolean
    }) {

        if (fields) {
            Object.assign(this, fields);
        }
    }
}

export class Order {
    field: string;
    direction: Direction;
    nullsFirst: boolean;
    //sortOrder: any;

    constructor(fields?: {
        field?: string,
        direction?: Direction,
        nullsFirst?: boolean,
        //sortOrder?: any
    }) {

        if (fields) {
            Object.assign(this, fields);
        }
    }
}

export class Field {
    name: string;

    constructor(name?: string) {
        if (name) {
            this.name = name;
        }
    }
}

/** Enums */

export class Direction extends Enum { }
Direction.initEnum(['ASC', 'DESC']);