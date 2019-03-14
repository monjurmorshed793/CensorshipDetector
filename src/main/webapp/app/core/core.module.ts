import { NgModule, LOCALE_ID } from '@angular/core';
import { DatePipe, registerLocaleData } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Title } from '@angular/platform-browser';
import locale from '@angular/common/locales/en';
import { FileUploaderComponent } from './file-uploader/file-uploader/file-uploader.component';

@NgModule({
    imports: [HttpClientModule],
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
