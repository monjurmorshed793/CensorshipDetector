import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { FileUploader } from 'ng2-file-upload';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class FileUploaderService {
    public url: string = '';

    constructor(private http: HttpClient) {}

    upload(file: any, url: string): Observable<HttpResponse<any>> {
        return this.http.post<any>('/api/' + url, file, { observe: 'response' });
    }
}
