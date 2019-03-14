export interface IWebAddress {
    id?: number;
    name?: string;
}

export class WebAddress implements IWebAddress {
    constructor(public id?: number, public name?: string) {}
}
