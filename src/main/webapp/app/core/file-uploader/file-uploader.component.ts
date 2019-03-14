import { Component, OnInit } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';
import { FileUploaderService } from 'app/core';

@Component({
    selector: 'jhi-file-uploader',
    templateUrl: './file-uploader.component.html',
    styles: []
})
export class FileUploaderComponent implements OnInit {
    file: any = {};
    url: string = '';

    constructor(private fileUploadService: FileUploaderService) {}

    upload() {
        console.log('Update params');
        console.log(this.fileUploadService.url);
    }

    ngOnInit() {}

    previousState() {
        window.history.back();
    }
}
