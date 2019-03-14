import { ActivatedRouteSnapshot, Resolve, Route, RouterStateSnapshot } from '@angular/router';
import { FileUploaderComponent, FileUploaderService } from 'app/core';
import { IWebAddress } from 'app/shared/model/web-address.model';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class FileUploaderResolve implements Resolve<any> {
    constructor(private fileUploaderService: FileUploaderService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<any> | Promise<any> | any {
        this.fileUploaderService.url = route.params['url'] ? route.params['url'] : null;
        console.log('******Url*****');
        console.log(this.fileUploaderService.url);
    }
}

export const fileUploaderRoute: Route = {
    path: 'file-upload/:url',
    component: FileUploaderComponent,
    resolve: {
        webAddress: FileUploaderResolve
    },
    data: {
        authorities: [],
        pateTitle: 'File Upload'
    }
};
