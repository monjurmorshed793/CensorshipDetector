import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { IWebAddress } from 'app/shared/model/web-address.model';
import { WebAddressService } from './web-address.service';

@Component({
    selector: 'jhi-web-address-update',
    templateUrl: './web-address-update.component.html'
})
export class WebAddressUpdateComponent implements OnInit {
    webAddress: IWebAddress;
    isSaving: boolean;

    constructor(protected webAddressService: WebAddressService, protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ webAddress }) => {
            this.webAddress = webAddress;
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.webAddress.id !== undefined) {
            this.subscribeToSaveResponse(this.webAddressService.update(this.webAddress));
        } else {
            this.subscribeToSaveResponse(this.webAddressService.create(this.webAddress));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<IWebAddress>>) {
        result.subscribe((res: HttpResponse<IWebAddress>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    protected onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError() {
        this.isSaving = false;
    }
}
