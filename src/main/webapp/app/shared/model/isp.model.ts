export const enum IspType {
    BROADBAND = 'BROADBAND',
    MOBILE_NETWORK = 'MOBILE_NETWORK'
}

export interface IIsp {
    id?: number;
    name?: string;
    type?: IspType;
}

export class Isp implements IIsp {
    constructor(public id?: number, public name?: string, public type?: IspType) {}
}
