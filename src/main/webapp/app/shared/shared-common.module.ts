import { NgModule } from '@angular/core';

import { CensorshipDetectorSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent } from './';

@NgModule({
    imports: [CensorshipDetectorSharedLibsModule],
    declarations: [JhiAlertComponent, JhiAlertErrorComponent],
    exports: [CensorshipDetectorSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent]
})
export class CensorshipDetectorSharedCommonModule {}
