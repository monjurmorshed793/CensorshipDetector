import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { WebAddress } from 'app/shared/model/web-address.model';
import { WebAddressService } from './web-address.service';
import { WebAddressComponent } from './web-address.component';
import { WebAddressDetailComponent } from './web-address-detail.component';
import { WebAddressUpdateComponent } from './web-address-update.component';
import { WebAddressDeletePopupComponent } from './web-address-delete-dialog.component';
import { IWebAddress } from 'app/shared/model/web-address.model';

@Injectable({ providedIn: 'root' })
export class WebAddressResolve implements Resolve<IWebAddress> {
    constructor(private service: WebAddressService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IWebAddress> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<WebAddress>) => response.ok),
                map((webAddress: HttpResponse<WebAddress>) => webAddress.body)
            );
        }
        return of(new WebAddress());
    }
}

export const webAddressRoute: Routes = [
    {
        path: '',
        component: WebAddressComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'WebAddresses'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/view',
        component: WebAddressDetailComponent,
        resolve: {
            webAddress: WebAddressResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'WebAddresses'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'new',
        component: WebAddressUpdateComponent,
        resolve: {
            webAddress: WebAddressResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'WebAddresses'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/edit',
        component: WebAddressUpdateComponent,
        resolve: {
            webAddress: WebAddressResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'WebAddresses'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const webAddressPopupRoute: Routes = [
    {
        path: ':id/delete',
        component: WebAddressDeletePopupComponent,
        resolve: {
            webAddress: WebAddressResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'WebAddresses'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
