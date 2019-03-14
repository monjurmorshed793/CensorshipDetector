import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IWebAddress } from 'app/shared/model/web-address.model';

@Component({
    selector: 'jhi-web-address-detail',
    templateUrl: './web-address-detail.component.html'
})
export class WebAddressDetailComponent implements OnInit {
    webAddress: IWebAddress;

    constructor(protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ webAddress }) => {
            this.webAddress = webAddress;
        });
    }

    previousState() {
        window.history.back();
    }
}
