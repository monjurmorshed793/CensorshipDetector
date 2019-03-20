import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IPacketInformation } from 'app/shared/model/packet-information.model';
import { PacketInformationService } from './packet-information.service';

@Component({
    selector: 'jhi-packet-information-delete-dialog',
    templateUrl: './packet-information-delete-dialog.component.html'
})
export class PacketInformationDeleteDialogComponent {
    packetInformation: IPacketInformation;

    constructor(
        protected packetInformationService: PacketInformationService,
        public activeModal: NgbActiveModal,
        protected eventManager: JhiEventManager
    ) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.packetInformationService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'packetInformationListModification',
                content: 'Deleted an packetInformation'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-packet-information-delete-popup',
    template: ''
})
export class PacketInformationDeletePopupComponent implements OnInit, OnDestroy {
    protected ngbModalRef: NgbModalRef;

    constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ packetInformation }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(PacketInformationDeleteDialogComponent as Component, {
                    size: 'lg',
                    backdrop: 'static'
                });
                this.ngbModalRef.componentInstance.packetInformation = packetInformation;
                this.ngbModalRef.result.then(
                    result => {
                        this.router.navigate(['/packet-information', { outlets: { popup: null } }]);
                        this.ngbModalRef = null;
                    },
                    reason => {
                        this.router.navigate(['/packet-information', { outlets: { popup: null } }]);
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
