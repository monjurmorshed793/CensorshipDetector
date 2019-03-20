import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IPacketInformation } from 'app/shared/model/packet-information.model';

type EntityResponseType = HttpResponse<IPacketInformation>;
type EntityArrayResponseType = HttpResponse<IPacketInformation[]>;

@Injectable({ providedIn: 'root' })
export class PacketInformationService {
    public resourceUrl = SERVER_API_URL + 'api/packet-informations';
    public resourceSearchUrl = SERVER_API_URL + 'api/_search/packet-informations';

    constructor(protected http: HttpClient) {}

    create(packetInformation: IPacketInformation): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(packetInformation);
        return this.http
            .post<IPacketInformation>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    update(packetInformation: IPacketInformation): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(packetInformation);
        return this.http
            .put<IPacketInformation>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http
            .get<IPacketInformation>(`${this.resourceUrl}/${id}`, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<IPacketInformation[]>(this.resourceUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<IPacketInformation[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    protected convertDateFromClient(packetInformation: IPacketInformation): IPacketInformation {
        const copy: IPacketInformation = Object.assign({}, packetInformation, {
            lastModified:
                packetInformation.lastModified != null && packetInformation.lastModified.isValid()
                    ? packetInformation.lastModified.toJSON()
                    : null
        });
        return copy;
    }

    protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
        if (res.body) {
            res.body.lastModified = res.body.lastModified != null ? moment(res.body.lastModified) : null;
        }
        return res;
    }

    protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
        if (res.body) {
            res.body.forEach((packetInformation: IPacketInformation) => {
                packetInformation.lastModified = packetInformation.lastModified != null ? moment(packetInformation.lastModified) : null;
            });
        }
        return res;
    }
}
