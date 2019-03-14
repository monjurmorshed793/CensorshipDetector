import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiAlertService } from 'ng-jhipster';
import { ICensorshipStatus } from 'app/shared/model/censorship-status.model';
import { CensorshipStatusService } from './censorship-status.service';
import { IWebAddress } from 'app/shared/model/web-address.model';
import { WebAddressService } from 'app/entities/web-address';
import { IIsp } from 'app/shared/model/isp.model';
import { IspService } from 'app/entities/isp';

@Component({
    selector: 'jhi-censorship-status-update',
    templateUrl: './censorship-status-update.component.html'
})
export class CensorshipStatusUpdateComponent implements OnInit {
    censorshipStatus: ICensorshipStatus;
    isSaving: boolean;

    webaddresses: IWebAddress[];

    isps: IIsp[];

    constructor(
        protected jhiAlertService: JhiAlertService,
        protected censorshipStatusService: CensorshipStatusService,
        protected webAddressService: WebAddressService,
        protected ispService: IspService,
        protected activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ censorshipStatus }) => {
            this.censorshipStatus = censorshipStatus;
        });
        this.webAddressService
            .query({ filter: 'censorshipstatus-is-null' })
            .pipe(
                filter((mayBeOk: HttpResponse<IWebAddress[]>) => mayBeOk.ok),
                map((response: HttpResponse<IWebAddress[]>) => response.body)
            )
            .subscribe(
                (res: IWebAddress[]) => {
                    if (!this.censorshipStatus.webAddress || !this.censorshipStatus.webAddress.id) {
                        this.webaddresses = res;
                    } else {
                        this.webAddressService
                            .find(this.censorshipStatus.webAddress.id)
                            .pipe(
                                filter((subResMayBeOk: HttpResponse<IWebAddress>) => subResMayBeOk.ok),
                                map((subResponse: HttpResponse<IWebAddress>) => subResponse.body)
                            )
                            .subscribe(
                                (subRes: IWebAddress) => (this.webaddresses = [subRes].concat(res)),
                                (subRes: HttpErrorResponse) => this.onError(subRes.message)
                            );
                    }
                },
                (res: HttpErrorResponse) => this.onError(res.message)
            );
        this.ispService
            .query({ filter: 'censorshipstatus-is-null' })
            .pipe(
                filter((mayBeOk: HttpResponse<IIsp[]>) => mayBeOk.ok),
                map((response: HttpResponse<IIsp[]>) => response.body)
            )
            .subscribe(
                (res: IIsp[]) => {
                    if (!this.censorshipStatus.isp || !this.censorshipStatus.isp.id) {
                        this.isps = res;
                    } else {
                        this.ispService
                            .find(this.censorshipStatus.isp.id)
                            .pipe(
                                filter((subResMayBeOk: HttpResponse<IIsp>) => subResMayBeOk.ok),
                                map((subResponse: HttpResponse<IIsp>) => subResponse.body)
                            )
                            .subscribe(
                                (subRes: IIsp) => (this.isps = [subRes].concat(res)),
                                (subRes: HttpErrorResponse) => this.onError(subRes.message)
                            );
                    }
                },
                (res: HttpErrorResponse) => this.onError(res.message)
            );
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.censorshipStatus.id !== undefined) {
            this.subscribeToSaveResponse(this.censorshipStatusService.update(this.censorshipStatus));
        } else {
            this.subscribeToSaveResponse(this.censorshipStatusService.create(this.censorshipStatus));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<ICensorshipStatus>>) {
        result.subscribe((res: HttpResponse<ICensorshipStatus>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    protected onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError() {
        this.isSaving = false;
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    trackWebAddressById(index: number, item: IWebAddress) {
        return item.id;
    }

    trackIspById(index: number, item: IIsp) {
        return item.id;
    }
}
