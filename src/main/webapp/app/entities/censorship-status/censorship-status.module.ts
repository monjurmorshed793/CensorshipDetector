import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { CensorshipDetectorSharedModule } from 'app/shared';
import {
    CensorshipStatusComponent,
    CensorshipStatusDetailComponent,
    CensorshipStatusUpdateComponent,
    CensorshipStatusDeletePopupComponent,
    CensorshipStatusDeleteDialogComponent,
    censorshipStatusRoute,
    censorshipStatusPopupRoute
} from './';
import { CensorshipDetectorIspModule } from 'app/entities/isp/isp.module';

const ENTITY_STATES = [...censorshipStatusRoute, ...censorshipStatusPopupRoute];

@NgModule({
    imports: [CensorshipDetectorSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        CensorshipStatusComponent,
        CensorshipStatusDetailComponent,
        CensorshipStatusUpdateComponent,
        CensorshipStatusDeleteDialogComponent,
        CensorshipStatusDeletePopupComponent
    ],
    entryComponents: [
        CensorshipStatusComponent,
        CensorshipStatusUpdateComponent,
        CensorshipStatusDeleteDialogComponent,
        CensorshipStatusDeletePopupComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class CensorshipDetectorCensorshipStatusModule {}
