import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { FileUploadModule } from 'ng2-file-upload';
import { CensorshipDetectorSharedModule } from 'app/shared';
import {
    WebAddressComponent,
    WebAddressDetailComponent,
    WebAddressUpdateComponent,
    WebAddressDeletePopupComponent,
    WebAddressDeleteDialogComponent,
    webAddressRoute,
    webAddressPopupRoute
} from './';

const ENTITY_STATES = [...webAddressRoute, ...webAddressPopupRoute];

@NgModule({
    imports: [CensorshipDetectorSharedModule, FileUploadModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        WebAddressComponent,
        WebAddressDetailComponent,
        WebAddressUpdateComponent,
        WebAddressDeleteDialogComponent,
        WebAddressDeletePopupComponent
    ],
    entryComponents: [WebAddressComponent, WebAddressUpdateComponent, WebAddressDeleteDialogComponent, WebAddressDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class CensorshipDetectorWebAddressModule {}
