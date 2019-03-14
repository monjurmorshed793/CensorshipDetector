import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IWebAddress } from 'app/shared/model/web-address.model';

type EntityResponseType = HttpResponse<IWebAddress>;
type EntityArrayResponseType = HttpResponse<IWebAddress[]>;

@Injectable({ providedIn: 'root' })
export class WebAddressService {
    public resourceUrl = SERVER_API_URL + 'api/web-addresses';
    public resourceSearchUrl = SERVER_API_URL + 'api/_search/web-addresses';

    constructor(protected http: HttpClient) {}

    create(webAddress: IWebAddress): Observable<EntityResponseType> {
        return this.http.post<IWebAddress>(this.resourceUrl, webAddress, { observe: 'response' });
    }

    update(webAddress: IWebAddress): Observable<EntityResponseType> {
        return this.http.put<IWebAddress>(this.resourceUrl, webAddress, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<IWebAddress>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IWebAddress[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IWebAddress[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
    }
}
