import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { IPacketInformation } from 'app/shared/model/packet-information.model';
import { PacketInformationService } from './packet-information.service';

@Component({
    selector: 'jhi-packet-information-update',
    templateUrl: './packet-information-update.component.html'
})
export class PacketInformationUpdateComponent implements OnInit {
    packetInformation: IPacketInformation;
    isSaving: boolean;
    lastModified: string;

    constructor(protected packetInformationService: PacketInformationService, protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ packetInformation }) => {
            this.packetInformation = packetInformation;
            this.lastModified =
                this.packetInformation.lastModified != null ? this.packetInformation.lastModified.format(DATE_TIME_FORMAT) : null;
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        this.packetInformation.lastModified = this.lastModified != null ? moment(this.lastModified, DATE_TIME_FORMAT) : null;
        if (this.packetInformation.id !== undefined) {
            this.subscribeToSaveResponse(this.packetInformationService.update(this.packetInformation));
        } else {
            this.subscribeToSaveResponse(this.packetInformationService.create(this.packetInformation));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<IPacketInformation>>) {
        result.subscribe((res: HttpResponse<IPacketInformation>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    protected onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError() {
        this.isSaving = false;
    }
}
