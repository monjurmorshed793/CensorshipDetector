import { IWebAddress } from 'app/shared/model/web-address.model';
import { IIsp } from 'app/shared/model/isp.model';

export const enum Status {
    CENSORED = 'CENSORED',
    NOT_CENSORED = 'NOT_CENSORED'
}

export interface ICensorshipStatus {
    id?: number;
    status?: Status;
    description?: string;
    ooniStatus?: string;
    webAddress?: IWebAddress;
    isp?: IIsp;
}

export class CensorshipStatus implements ICensorshipStatus {
    constructor(
        public id?: number,
        public status?: Status,
        public description?: string,
        public ooniStatus?: string,
        public webAddress?: IWebAddress,
        public isp?: IIsp
    ) {}
}
