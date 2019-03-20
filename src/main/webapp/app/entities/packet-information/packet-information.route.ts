import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { PacketInformation } from 'app/shared/model/packet-information.model';
import { PacketInformationService } from './packet-information.service';
import { PacketInformationComponent } from './packet-information.component';
import { PacketInformationDetailComponent } from './packet-information-detail.component';
import { PacketInformationUpdateComponent } from './packet-information-update.component';
import { PacketInformationDeletePopupComponent } from './packet-information-delete-dialog.component';
import { IPacketInformation } from 'app/shared/model/packet-information.model';

@Injectable({ providedIn: 'root' })
export class PacketInformationResolve implements Resolve<IPacketInformation> {
    constructor(private service: PacketInformationService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IPacketInformation> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<PacketInformation>) => response.ok),
                map((packetInformation: HttpResponse<PacketInformation>) => packetInformation.body)
            );
        }
        return of(new PacketInformation());
    }
}

export const packetInformationRoute: Routes = [
    {
        path: '',
        component: PacketInformationComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'PacketInformations'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/view',
        component: PacketInformationDetailComponent,
        resolve: {
            packetInformation: PacketInformationResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'PacketInformations'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'new',
        component: PacketInformationUpdateComponent,
        resolve: {
            packetInformation: PacketInformationResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'PacketInformations'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/edit',
        component: PacketInformationUpdateComponent,
        resolve: {
            packetInformation: PacketInformationResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'PacketInformations'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const packetInformationPopupRoute: Routes = [
    {
        path: ':id/delete',
        component: PacketInformationDeletePopupComponent,
        resolve: {
            packetInformation: PacketInformationResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'PacketInformations'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
