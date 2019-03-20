/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { CensorshipDetectorTestModule } from '../../../test.module';
import { PacketInformationDeleteDialogComponent } from 'app/entities/packet-information/packet-information-delete-dialog.component';
import { PacketInformationService } from 'app/entities/packet-information/packet-information.service';

describe('Component Tests', () => {
    describe('PacketInformation Management Delete Component', () => {
        let comp: PacketInformationDeleteDialogComponent;
        let fixture: ComponentFixture<PacketInformationDeleteDialogComponent>;
        let service: PacketInformationService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [CensorshipDetectorTestModule],
                declarations: [PacketInformationDeleteDialogComponent]
            })
                .overrideTemplate(PacketInformationDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(PacketInformationDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(PacketInformationService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('confirmDelete', () => {
            it('Should call delete service on confirmDelete', inject(
                [],
                fakeAsync(() => {
                    // GIVEN
                    spyOn(service, 'delete').and.returnValue(of({}));

                    // WHEN
                    comp.confirmDelete(123);
                    tick();

                    // THEN
                    expect(service.delete).toHaveBeenCalledWith(123);
                    expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                    expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
                })
            ));
        });
    });
});
