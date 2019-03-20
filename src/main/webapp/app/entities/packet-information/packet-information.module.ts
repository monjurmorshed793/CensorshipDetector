import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { CensorshipDetectorSharedModule } from 'app/shared';
import {
    PacketInformationComponent,
    PacketInformationDetailComponent,
    PacketInformationUpdateComponent,
    PacketInformationDeletePopupComponent,
    PacketInformationDeleteDialogComponent,
    packetInformationRoute,
    packetInformationPopupRoute
} from './';

const ENTITY_STATES = [...packetInformationRoute, ...packetInformationPopupRoute];

@NgModule({
    imports: [CensorshipDetectorSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        PacketInformationComponent,
        PacketInformationDetailComponent,
        PacketInformationUpdateComponent,
        PacketInformationDeleteDialogComponent,
        PacketInformationDeletePopupComponent
    ],
    entryComponents: [
        PacketInformationComponent,
        PacketInformationUpdateComponent,
        PacketInformationDeleteDialogComponent,
        PacketInformationDeletePopupComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class CensorshipDetectorPacketInformationModule {}
