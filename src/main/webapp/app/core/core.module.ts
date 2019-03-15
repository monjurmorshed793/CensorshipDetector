import { NgModule, LOCALE_ID } from '@angular/core';
import { DatePipe, registerLocaleData } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Title } from '@angular/platform-browser';
import locale from '@angular/common/locales/en';
import { FileUploadModule } from 'ng2-file-upload';
import { FileUploaderComponent } from 'app/core/file-uploader/file-uploader.component';
import { RouterModule } from '@angular/router';
import { coreState } from 'app/core/core.route';
import { CensorshipDetectorHomeModule } from 'app/home';
import { NotifierModule } from 'angular-notifier';

@NgModule({
    imports: [HttpClientModule, FileUploadModule, NotifierModule, CensorshipDetectorHomeModule, RouterModule.forChild(coreState)],
    exports: [],
    declarations: [FileUploaderComponent],
    providers: [
        Title,
        {
            provide: LOCALE_ID,
            useValue: 'en'
        },
        DatePipe
    ]
})
export class CensorshipDetectorCoreModule {
    constructor() {
        registerLocaleData(locale);
    }
}
