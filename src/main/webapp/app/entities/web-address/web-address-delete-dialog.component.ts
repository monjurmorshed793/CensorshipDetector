import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IWebAddress } from 'app/shared/model/web-address.model';
import { WebAddressService } from './web-address.service';

@Component({
    selector: 'jhi-web-address-delete-dialog',
    templateUrl: './web-address-delete-dialog.component.html'
})
export class WebAddressDeleteDialogComponent {
    webAddress: IWebAddress;

    constructor(
        protected webAddressService: WebAddressService,
        public activeModal: NgbActiveModal,
        protected eventManager: JhiEventManager
    ) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.webAddressService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'webAddressListModification',
                content: 'Deleted an webAddress'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-web-address-delete-popup',
    template: ''
})
export class WebAddressDeletePopupComponent implements OnInit, OnDestroy {
    protected ngbModalRef: NgbModalRef;

    constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ webAddress }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(WebAddressDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
                this.ngbModalRef.componentInstance.webAddress = webAddress;
                this.ngbModalRef.result.then(
                    result => {
                        this.router.navigate(['/web-address', { outlets: { popup: null } }]);
                        this.ngbModalRef = null;
                    },
                    reason => {
                        this.router.navigate(['/web-address', { outlets: { popup: null } }]);
                        this.ngbModalRef = null;
                    }
                );
            }, 0);
        });
    }

    ngOnDestroy() {
        this.ngbModalRef = null;
    }
}
