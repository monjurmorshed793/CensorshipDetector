import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { CensorshipDetectorSharedModule } from 'app/shared';
import {
    IspComponent,
    IspDetailComponent,
    IspUpdateComponent,
    IspDeletePopupComponent,
    IspDeleteDialogComponent,
    ispRoute,
    ispPopupRoute
} from './';

const ENTITY_STATES = [...ispRoute, ...ispPopupRoute];

@NgModule({
    imports: [CensorshipDetectorSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [IspComponent, IspDetailComponent, IspUpdateComponent, IspDeleteDialogComponent, IspDeletePopupComponent],
    entryComponents: [IspComponent, IspUpdateComponent, IspDeleteDialogComponent, IspDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class CensorshipDetectorIspModule {}
