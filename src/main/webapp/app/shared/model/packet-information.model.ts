import { Moment } from 'moment';

export interface IPacketInformation {
    id?: number;
    sourceAddress?: string;
    destinationAddress?: string;
    window?: number;
    identificationNumber?: number;
    sequenceNumber?: number;
    sourcePort?: number;
    destinationPort?: number;
    acknowledgeNumber?: number;
    ttl?: number;
    syn?: string;
    fin?: string;
    ack?: string;
    lastModified?: Moment;
    protocol?: string;
}

export class PacketInformation implements IPacketInformation {
    constructor(
        public id?: number,
        public sourceAddress?: string,
        public destinationAddress?: string,
        public window?: number,
        public identificationNumber?: number,
        public sequenceNumber?: number,
        public sourcePort?: number,
        public destinationPort?: number,
        public acknowledgeNumber?: number,
        public ttl?: number,
        public syn?: string,
        public fin?: string,
        public ack?: string,
        public lastModified?: Moment,
        public protocol?: string
    ) {}
}
