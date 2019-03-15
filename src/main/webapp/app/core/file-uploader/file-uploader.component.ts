import { Component, OnInit } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';
import { FileUploaderService } from 'app/core';
import { Observable } from 'rxjs';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { IWebAddress } from 'app/shared/model/web-address.model';

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
        let formData: FormData = new FormData();
        formData.append('file', this.file[0], 'filename.xls');
        this.subscribeToSaveResponse(this.fileUploadService.upload(formData, this.fileUploadService.url));
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<IWebAddress>>) {
        result.subscribe((res: HttpResponse<IWebAddress>) => this.onSaveSuccess(), (res: HttpErrorResponse) => console.error(res));
    }
    protected onSaveSuccess() {
        this.previousState();
    }
    ngOnInit() {}

    previousState() {
        window.history.back();
    }
}
