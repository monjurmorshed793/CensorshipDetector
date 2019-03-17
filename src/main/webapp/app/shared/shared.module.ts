import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { NgbDateAdapter } from '@ng-bootstrap/ng-bootstrap';

import { NgbDateMomentAdapter } from './util/datepicker-adapter';
import {
    CensorshipDetectorSharedLibsModule,
    CensorshipDetectorSharedCommonModule,
    JhiLoginModalComponent,
    HasAnyAuthorityDirective
} from './';
import { NgSelect2Module } from 'ng-select2';

@NgModule({
    imports: [CensorshipDetectorSharedLibsModule, CensorshipDetectorSharedCommonModule, NgSelect2Module],
    declarations: [JhiLoginModalComponent, HasAnyAuthorityDirective],
    providers: [{ provide: NgbDateAdapter, useClass: NgbDateMomentAdapter }],
    entryComponents: [JhiLoginModalComponent],
    exports: [CensorshipDetectorSharedCommonModule, JhiLoginModalComponent, HasAnyAuthorityDirective],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class CensorshipDetectorSharedModule {
    static forRoot() {
        return {
            ngModule: CensorshipDetectorSharedModule
        };
    }
}
