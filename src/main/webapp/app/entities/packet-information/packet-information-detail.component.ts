import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPacketInformation } from 'app/shared/model/packet-information.model';

@Component({
    selector: 'jhi-packet-information-detail',
    templateUrl: './packet-information-detail.component.html'
})
export class PacketInformationDetailComponent implements OnInit {
    packetInformation: IPacketInformation;

    constructor(protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ packetInformation }) => {
            this.packetInformation = packetInformation;
        });
    }

    previousState() {
        window.history.back();
    }
}
